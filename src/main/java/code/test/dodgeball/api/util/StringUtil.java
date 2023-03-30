package code.test.dodgeball.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.text.DecimalFormat;
import java.util.Objects;

public class StringUtil {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.#");

    public static Component color(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    public static String format(String text, Object... values) {
        for (int i = 0; i < values.length; i++) {
            text = text.replace("{" + i + "}", Objects.toString(values[i]));
        }
        return text;
    }

    public static Component formatAndColor(String text, Object... values) {
        return color(format(text, values));
    }

    public static String normalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static String calculateAndFormatPercentage(double of, double x) {
        if (x == 0) return "100";
        return FORMAT.format(100 * (of / x));
    }
}
