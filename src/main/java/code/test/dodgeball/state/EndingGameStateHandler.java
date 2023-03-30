package code.test.dodgeball.state;

import code.test.dodgeball.api.state.game.GameState;
import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.state.player.PlayerStats;
import code.test.dodgeball.api.team.TeamState;
import code.test.dodgeball.api.util.StringUtil;
import code.test.dodgeball.manager.GameManager;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import static code.test.dodgeball.api.util.StringUtil.*;

public class EndingGameStateHandler extends CommonGameStateHandler {

    public EndingGameStateHandler(JavaPlugin plugin, GameManager manager) {
        super(plugin, manager, GameState.GAME_ENDING);
    }

    @Override
    public void init() {
        super.init();

        listen(PlayerJoinEvent.class, this::onPlayerJoin);
        listen(PlayerQuitEvent.class, this::onPlayerQuit);
    }

    @Override
    public void start() {
        manager.getEntityManager().clear();

        manager.getParticipants().values().forEach(state -> {
            state.getPlayer().getInventory().clear();

            PlayerStats stats = state.getStats();

            state.message(color("&7&l=== &b&lPost-game Statistics &7&l==="));
            state.message(color("  &aEliminations: &6" + stats.getEliminations()));
            state.message(color("  &aSnowballs Thrown: &6" + stats.getSnowballsThrown()));
            if (stats.getSnowballsThrown() > 0) {
                state.message(
                        formatAndColor("  &aAccuracy: &6{0}%",
                                calculateAndFormatPercentage(stats.getEliminations(), stats.getSnowballsThrown())
                        )
                );
            }

            stats.getEliminatedByPlayerName().ifPresent(name ->
                    state.message(color("  &aEliminated By: &6" + name))
            );
        });

        TeamState winner = manager.getPreviouslyWinningTeam();

        AtomicInteger count = new AtomicInteger(5);
        Location[] locations = manager.getSnowballSpawns().generateEvenlySpacedLocations(5);
        Color fireworkColor = winner.getDescription().fireworkColor();
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int time = count.getAndDecrement();
            if (time == 5 || time == 1) {
                spawnFirework(locations[2], fireworkColor);
            } else if (time == 4 || time == 2) {
                spawnFirework(locations[1], fireworkColor);
                spawnFirework(locations[3], fireworkColor);
            } else if (time == 3) {
                spawnFirework(locations[0], fireworkColor);
                spawnFirework(locations[4], fireworkColor);
            } else if (time == 0) {
                manager.getVictoryCommands().execute(winner.getPlayers());

                manager.setState(GameState.LOBBY);
            }
        }, 0L, 40L);
    }

    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (manager.canStart()) {
            player.kick(color("&cThe game has reached max capacity."));
            return;
        }

        PlayerState state = new PlayerState(player);

        manager.getParticipants().put(player.getUniqueId(), state);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(manager.getLobbySpawn());
            player.sendMessage(color("&7A Dodgeball game is currently in progress."));
        });
    }

    private void onPlayerQuit(PlayerQuitEvent event) {
        PlayerState player = manager.getParticipant(event.getPlayer().getUniqueId());

        manager.getParticipants().remove(player.getUuid());
    }

    private void spawnFirework(Location location, Color color) {
        location.getWorld().spawnEntity(location, EntityType.FIREWORK, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
            Firework firework = (Firework) entity;
            FireworkMeta meta = firework.getFireworkMeta();

            meta.addEffect(FireworkEffect.builder().withColor(color).build());

            firework.setFireworkMeta(meta);
        });
    }
}
