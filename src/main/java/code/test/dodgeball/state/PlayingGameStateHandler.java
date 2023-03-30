package code.test.dodgeball.state;

import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.state.game.GameState;
import code.test.dodgeball.api.team.TeamDescription;
import code.test.dodgeball.api.team.TeamState;
import code.test.dodgeball.manager.GameManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static code.test.dodgeball.api.util.StringUtil.color;
import static code.test.dodgeball.api.util.StringUtil.formatAndColor;

public class PlayingGameStateHandler extends CommonGameStateHandler {

    public PlayingGameStateHandler(JavaPlugin plugin, GameManager manager) {
        super(plugin, manager, GameState.GAME_PLAYING);
    }

    @Override
    public void init() {
        super.init();

        listen(EntityDamageByEntityEvent.class, this::onPlayerHitBySnowball);
        listen(ProjectileHitEvent.class, this::onSnowballHitBlock);
        listen(PlayerMoveEvent.class, this::onPlayerMove);
        listen(PlayerJoinEvent.class, this::onPlayerJoin);
        listen(PlayerQuitEvent.class, this::onPlayerQuit);
        listen(EntityPickupItemEvent.class, this::onItemPickup);
        listen(EntitySpawnEvent.class, this::onEntitySpawn);
    }

    private void processWin(TeamState winner) {
        manager.broadcast(
                formatAndColor("{0} &7won the game!", winner.getDescription().display())
        );

        manager.setPreviouslyWinningTeam(winner);

        manager.setState(GameState.GAME_ENDING);
    }

    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (manager.getParticipants().size() >= GameManager.MAX_PLAYERS) {
            player.kick(color("&cThe game has reached max capacity."));
            return;
        }

        PlayerState state = new PlayerState(player);

        manager.getParticipants().put(player.getUniqueId(), state);

        state.setAlive(false);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
           player.setGameMode(GameMode.SPECTATOR);
           player.teleport(manager.getLobbySpawn());
           player.sendMessage(color("&7A Dodgeball game is currently in progress."));
        });
    }

    private void onPlayerQuit(PlayerQuitEvent event) {
        PlayerState player = manager.getParticipant(event.getPlayer().getUniqueId());

        manager.getParticipants().remove(player.getUuid());

        if (player.isAlive()) {
            player.getTeam().remove(player);
            if (player.getTeam().getPlayersAlive().size() == 0) {
                TeamState oppositeTeam = manager.getOppositeTeam(player.getTeam());

                processWin(oppositeTeam);
            }
        }
    }

    private void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().equals(event.getTo())) return;

        PlayerState player = manager.getParticipant(event.getPlayer().getUniqueId());

        if (!player.isAlive()) return;
        if (!player.getTeam().getArena().isInside(event.getTo())) {
            event.setCancelled(true);
        }
    }

    private void onPlayerHitBySnowball(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player attackerPlayer)) return;
        if (!(event.getEntity() instanceof Player defenderPlayer)) return;

        PlayerState attacker = manager.getParticipant(attackerPlayer.getUniqueId());
        PlayerState defender = manager.getParticipant(defenderPlayer.getUniqueId());

        if (attacker.getTeam().equals(defender.getTeam())) {
            manager.getEntityManager().dropItem(defenderPlayer.getLocation(), Material.SNOWBALL);
            return;
        }

        defender.setAlive(false);
        defender.getStats().setEliminatedByPlayerName(attackerPlayer.getName());

        attacker.getStats().addElimination();

        defenderPlayer.setGameMode(GameMode.SPECTATOR);
        defenderPlayer.getInventory().all(Material.SNOWBALL).forEach((index, item) -> {
            manager.getEntityManager().dropItem(defenderPlayer.getLocation(), item);
        });
        defenderPlayer.getInventory().clear();

        manager.broadcast(
                formatAndColor("{0} &7eliminated {1}",
                        TeamDescription.display(attacker),
                        TeamDescription.display(defender)
                )
        );

        if (defender.getTeam().getPlayersAlive().size() == 0) {
            processWin(attacker.getTeam());
        }
    }

    private void onItemPickup(EntityPickupItemEvent event) {
        Item item = event.getItem();
        PlayerState player = manager.getParticipant(event.getEntity().getUniqueId());
        if (player == null) {
            if (manager.getEntityManager().isTrackedItem(item)) {
                event.setCancelled(true);
            }
            return;
        }

        manager.getEntityManager().remove(item);
    }

    private void onEntitySpawn(EntitySpawnEvent event) {
        // This first if statement is a dirty hack to avoid a class cast exception because Bukkit.
        if (!(event instanceof ProjectileLaunchEvent)) return;
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player player)) return;

        PlayerState state = manager.getParticipant(player.getUniqueId());

        state.getStats().addSnowballThrow();
    }

    private void onSnowballHitBlock(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!(snowball.getShooter() instanceof Player)) return;
        if (event.getHitEntity() != null) return;

        manager.getEntityManager().dropItem(snowball.getLocation(), Material.SNOWBALL);
    }
}
