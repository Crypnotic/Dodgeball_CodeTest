package code.test.dodgeball.manager;

import code.test.dodgeball.DodgeballPlugin;
import code.test.dodgeball.api.command.CommandPackExecutor;
import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.region.SpawnRegion;
import code.test.dodgeball.api.team.TeamDescription;
import code.test.dodgeball.api.state.game.GameState;
import code.test.dodgeball.api.io.Configuration;
import code.test.dodgeball.api.state.game.GameStateHandler;
import code.test.dodgeball.api.team.TeamState;
import code.test.dodgeball.api.util.LocationUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    public static final int MAX_PLAYERS = 20;
    public static final int COUNTDOWN_TIME = 20;
    public static final double GAME_START_RATIO = 0.1;

    private final DodgeballPlugin plugin;
    private final Map<UUID, PlayerState> participants;
    private final EntityManager entityManager;

    private Location lobbySpawn;
    private SpawnRegion snowballSpawns;

    private GameState currentState;
    private GameStateHandler currentHandler;
    private Map<GameState, GameStateHandler> handlers;

    private CommandPackExecutor victoryCommands;

    private TeamState redTeam;
    private TeamState blueTeam;

    private TeamState previouslyWinningTeam;

    public GameManager(DodgeballPlugin plugin) {
        this.plugin = plugin;
        this.participants = new HashMap<>();
        this.entityManager = new EntityManager();
    }

    public void init() {
        Configuration configuration = Configuration.builder()
                .folder(plugin.getDataFolder())
                .name("config.yml")
                .build();

        this.redTeam = TeamState.create(configuration, TeamDescription.RED);
        this.blueTeam = TeamState.create(configuration, TeamDescription.BLUE);

        this.handlers = new HashMap<>();

        for (GameState gameState : GameState.values()) {
            GameStateHandler handler = gameState.create(plugin, this);

            handler.init();

            handlers.put(gameState, handler);
        }

        this.lobbySpawn = LocationUtil.parseLocation(configuration.node("locations", "lobby")).orElseThrow();
        this.snowballSpawns = LocationUtil.parseSpawnRegion(configuration.node("locations", "snowballs")).orElseThrow();

        try {
            this.victoryCommands = new CommandPackExecutor(plugin, configuration.node("commands", "victory").getList(String.class));
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }

        setState(GameState.LOBBY);
    }

    public void broadcast(Component message) {
        participants.values().forEach(player -> player.message(message));
    }

    public boolean canStart() {
        return getParticipants().size() >= GameManager.MAX_PLAYERS * GameManager.GAME_START_RATIO;
    }

    public PlayerState getParticipant(UUID uuid) {
        return participants.get(uuid);
    }

    public Map<UUID, PlayerState> getParticipants() {
        return participants;
    }

    public GameState getState() {
        return currentState;
    }

    public void setState(GameState state) {
        if (currentHandler != null) {
            currentHandler.stop();
        }

        this.currentState = state;
        this.currentHandler = handlers.get(state);

        currentHandler.start();
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public TeamState getRedTeam() {
        return redTeam;
    }

    public TeamState getBlueTeam() {
        return blueTeam;
    }

    public TeamState getOppositeTeam(TeamState team) {
        return team.equals(blueTeam) ? redTeam : blueTeam;
    }

    public SpawnRegion getSnowballSpawns() {
        return snowballSpawns;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public CommandPackExecutor getVictoryCommands() {
        return victoryCommands;
    }

    public TeamState getPreviouslyWinningTeam() {
        return previouslyWinningTeam;
    }

    public void setPreviouslyWinningTeam(TeamState previouslyWinningTeam) {
        this.previouslyWinningTeam = previouslyWinningTeam;
    }
}
