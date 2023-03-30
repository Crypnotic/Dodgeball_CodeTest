package code.test.dodgeball.api.state.game;

import code.test.dodgeball.api.state.event.StatefulEventListener;
import code.test.dodgeball.manager.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public abstract class GameStateHandler {

    private static final Method onEventCallMethod;
    private static final EventExecutor executor;

    static {
        try {
            onEventCallMethod = StatefulEventListener.class.getDeclaredMethod("onEventCall", Event.class);
            executor = EventExecutor.create(onEventCallMethod, Event.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected final JavaPlugin plugin;
    protected final GameManager manager;
    protected final GameState state;

    protected GameStateHandler(JavaPlugin plugin, GameManager manager, GameState state) {
        this.plugin = plugin;
        this.manager = manager;
        this.state = state;
    }

    protected <T extends Event> void listen(Class<T> clazz, Consumer<T> handler) {
        StatefulEventListener<T> listener = new StatefulEventListener<>(this, handler);

        plugin.getServer().getPluginManager().registerEvent(clazz, listener, EventPriority.NORMAL, executor, plugin);
    }

    public abstract void init();

    public void start() {}
    public void stop() {}

    public boolean isActive() {
        return manager.getState().equals(state);
    }
}
