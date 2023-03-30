package code.test.dodgeball.state;

import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.state.game.GameState;
import code.test.dodgeball.manager.GameManager;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

import static code.test.dodgeball.api.util.StringUtil.color;

public class LobbyGameStateHandler extends CommonGameStateHandler {

    private BukkitTask countdownTask;

    public LobbyGameStateHandler(JavaPlugin plugin, GameManager manager) {
        super(plugin, manager, GameState.LOBBY);
    }

    @Override
    public void init() {
        super.init();

        listen(PlayerJoinEvent.class, this::onPlayerJoin);
        listen(PlayerQuitEvent.class, this::onPlayerQuit);
    }

    @Override
    public void start() {
        manager.getParticipants().values().forEach(state -> {
            state.reset();

            state.getPlayer().setGameMode(GameMode.ADVENTURE);
            state.getPlayer().teleport(manager.getLobbySpawn());
        });

        manager.getBlueTeam().reset();
        manager.getRedTeam().reset();

        if (manager.canStart()) {
            startCountdown();
        }
    }

    @Override
    public void stop() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (manager.getParticipants().size() >= GameManager.MAX_PLAYERS) {
            player.kick(color("&cThe game has reached max capacity."));
            return;
        }

        PlayerState state = new PlayerState(player);

        manager.getParticipants().put(player.getUniqueId(), state);

        if (manager.canStart()) {
            startCountdown();
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(manager.getLobbySpawn());
        });
    }

    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        manager.getParticipants().remove(player.getUniqueId());

        if (countdownTask == null) return;

        if (!manager.canStart()) {
            countdownTask.cancel();
            countdownTask = null;

            manager.broadcast(color("&7Countdown cancelled due to lack of players."));
        }
    }

    private void startCountdown() {
        if (countdownTask != null) {
            return;
        }

        AtomicInteger countdown = new AtomicInteger(GameManager.COUNTDOWN_TIME);
        this.countdownTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            int currentTime = countdown.getAndDecrement();

            // TODO: Add countdown sounds
            if (currentTime == 0) {
                manager.setState(GameState.GAME_STARTING);
                return;
            }

            manager.getParticipants().values().forEach(player -> {
                player.getPlayer().sendActionBar(color("&6&lDodgeball starts in: &c&l" + currentTime));
            });
        }, 20L, 20L);
    }
}
