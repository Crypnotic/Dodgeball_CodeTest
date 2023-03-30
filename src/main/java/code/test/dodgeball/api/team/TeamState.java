package code.test.dodgeball.api.team;

import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.io.Configuration;
import code.test.dodgeball.api.region.ArenaRegion;
import code.test.dodgeball.api.region.SpawnRegion;
import code.test.dodgeball.api.util.LocationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamState {

    private final SpawnRegion spawn;
    private final ArenaRegion arena;
    private final TeamDescription description;
    private final List<PlayerState> players;

    public TeamState(SpawnRegion spawn, ArenaRegion arena, TeamDescription description) {
        this.spawn = spawn;
        this.arena = arena;
        this.description = description;
        this.players = new ArrayList<>();
    }

    public SpawnRegion getSpawn() {
        return spawn;
    }

    public ArenaRegion getArena() {
        return arena;
    }

    public TeamDescription getDescription() {
        return description;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void add(PlayerState player) {
        this.players.add(player);
    }

    public void remove(PlayerState player) {
        this.players.remove(player);
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public List<PlayerState> getPlayersAlive() {
        return getPlayers().stream()
                .filter(PlayerState::isAlive)
                .collect(Collectors.toList());
    }

    public static TeamState create(Configuration configuration, TeamDescription teamDescription) {
        SpawnRegion spawnRegion = LocationUtil.parseSpawnRegion(
                configuration.node("locations", "spawns", teamDescription.name().toLowerCase())
        ).orElseThrow();
        ArenaRegion arenaRegion = LocationUtil.parseGameRegion(
                configuration.node("locations", "space", teamDescription.name().toLowerCase())
        ).orElseThrow();

        return new TeamState(spawnRegion, arenaRegion, teamDescription);
    }

    public void reset() {
        players.clear();
    }
}
