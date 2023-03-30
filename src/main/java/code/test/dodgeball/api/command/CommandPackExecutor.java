package code.test.dodgeball.api.command;

import code.test.dodgeball.DodgeballPlugin;
import code.test.dodgeball.api.state.player.PlayerState;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandPackExecutor {

    private final DodgeballPlugin plugin;
    private final List<String> commands;

    public CommandPackExecutor(DodgeballPlugin plugin, List<String> commands) {
        this.plugin = plugin;
        this.commands = commands;
    }

    public void execute(List<PlayerState> players) {
        players.forEach(player -> {
            commands.forEach(command -> {
                String substitutedCommand = command
                        .replace("{player}", player.getPlayer().getName());

                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), substitutedCommand);
            });
        });
    }
}
