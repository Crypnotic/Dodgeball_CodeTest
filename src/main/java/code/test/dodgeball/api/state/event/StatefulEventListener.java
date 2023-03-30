package code.test.dodgeball.api.state.event;

import code.test.dodgeball.api.state.game.GameStateHandler;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

public class StatefulEventListener<T extends Event> implements Listener {

    private final GameStateHandler stateHandler;
    private final Consumer<T> handler;

    public StatefulEventListener(GameStateHandler stateHandler, Consumer<T> handler) {
        this.stateHandler = stateHandler;
        this.handler = handler;
    }

    @EventHandler
    public void onEventCall(T event) {
        if (stateHandler.isActive()) {
            handler.accept(event);
        }
    }
}
