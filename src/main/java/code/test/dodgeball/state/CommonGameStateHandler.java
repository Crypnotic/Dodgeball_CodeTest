package code.test.dodgeball.state;

import code.test.dodgeball.api.state.game.GameState;
import code.test.dodgeball.api.state.game.GameStateHandler;
import code.test.dodgeball.manager.GameManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonGameStateHandler extends GameStateHandler {

    public CommonGameStateHandler(JavaPlugin plugin, GameManager manager, GameState state) {
        super(plugin, manager, state);
    }

    @Override
    public void init() {
        listen(PlayerJoinEvent.class, this::onPlayerJoin);
        listen(PlayerQuitEvent.class, event -> event.quitMessage(null));
        listen(EntityDeathEvent.class, this::onEntityDeath);
        listen(EntityDamageByEntityEvent.class, this::onEntityDamage);
        listen(BlockBreakEvent.class, this::onBlockBreak);
        listen(BlockPlaceEvent.class, this::onBlockPlace);
        listen(FoodLevelChangeEvent.class, this::onFoodLevelChange);
    }

    private void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        event.getPlayer().getInventory().clear();
    }

    private void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (manager.getEntityManager().isTrackedItem(entity) || entity instanceof Player) {
            event.setCancelled(true);
        }
    }

    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        event.setCancelled(true);
    }

    private void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    private void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }
}
