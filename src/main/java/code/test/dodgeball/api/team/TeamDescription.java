package code.test.dodgeball.api.team;

import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.util.StringUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public enum TeamDescription {
    BLUE("&b", Color.BLUE), RED("&c", Color.RED);

    private final String textColor;
    private final Color fireworkColor;

    TeamDescription(String textColor, Color fireworkColor) {
        this.textColor = textColor;
        this.fireworkColor = fireworkColor;
    }

    public String textColor() {
        return textColor;
    }

    public Color fireworkColor() {
        return fireworkColor;
    }

    public String display() {
        return textColor + StringUtil.normalize(name());
    }

    public static String display(PlayerState player) {
        return player.getTeam().getDescription().textColor() + player.getPlayer().getName();
    }
}
