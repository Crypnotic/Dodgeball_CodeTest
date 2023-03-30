package code.test.dodgeball.hook;

import code.test.dodgeball.api.state.player.PlayerStats;
import code.test.dodgeball.api.util.StringUtil;
import code.test.dodgeball.manager.GameManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static code.test.dodgeball.api.util.StringUtil.calculateAndFormatPercentage;

public class PlaceholderApiHook extends PlaceholderExpansion {

    private final GameManager manager;

    public PlaceholderApiHook(GameManager manager) {
        this.manager = manager;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] data = params.split("\\.");
        switch (data[0]) {
            case "count" -> {
                if (!data[1].equalsIgnoreCase("team")) return "0";
                return switch (data[2]) {
                    case "current" ->
                            String.valueOf(manager.getParticipant(player.getUniqueId()).getTeam().getPlayerCount());
                    case "blue" -> String.valueOf(manager.getBlueTeam().getPlayerCount());
                    case "red" -> String.valueOf(manager.getRedTeam().getPlayerCount());
                    default -> "0";
                };
            }

            case "stats" -> {
                PlayerStats stats = manager.getParticipant(player.getUniqueId()).getStats();
                return switch (data[1]) {
                    case "eliminations" -> String.valueOf(stats.getEliminations());
                    case "snowballsThrown" -> String.valueOf(stats.getSnowballsThrown());
                    case "eliminatedByPlayer" -> stats.getEliminatedByPlayerName().orElse("");
                    case "accuracy" -> calculateAndFormatPercentage(stats.getEliminations(), stats.getSnowballsThrown());
                    default -> "0";
                };
            }
        }


        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dodgeball";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Crypnotic";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}
