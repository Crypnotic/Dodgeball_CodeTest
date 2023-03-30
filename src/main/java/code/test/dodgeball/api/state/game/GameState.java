package code.test.dodgeball.api.state.game;

import code.test.dodgeball.manager.GameManager;
import code.test.dodgeball.state.EndingGameStateHandler;
import code.test.dodgeball.state.LobbyGameStateHandler;
import code.test.dodgeball.state.PlayingGameStateHandler;
import code.test.dodgeball.state.StartingGameStateHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiFunction;

public enum GameState {
    LOBBY(LobbyGameStateHandler::new),
    GAME_STARTING(StartingGameStateHandler::new),
    GAME_PLAYING(PlayingGameStateHandler::new),
    GAME_ENDING(EndingGameStateHandler::new);

    private final BiFunction<JavaPlugin, GameManager, GameStateHandler> supplier;

    GameState(BiFunction<JavaPlugin, GameManager, GameStateHandler> supplier) {
        this.supplier = supplier;
    }

    public GameStateHandler create(JavaPlugin plugin, GameManager manager) {
        return supplier.apply(plugin, manager);
    }
}
