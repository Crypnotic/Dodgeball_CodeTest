package code.test.dodgeball;

import code.test.dodgeball.hook.PlaceholderApiHook;
import code.test.dodgeball.manager.GameManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DodgeballPlugin extends JavaPlugin {

    private GameManager gameManager;

    @Override
    public void onLoad() {
        this.gameManager = new GameManager(this);
    }

    @Override
    public void onEnable() {
        gameManager.init();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderApiHook(gameManager).register();
        }
    }
}
