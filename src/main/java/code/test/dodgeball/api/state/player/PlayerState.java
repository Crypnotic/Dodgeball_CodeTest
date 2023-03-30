package code.test.dodgeball.api.state.player;

import code.test.dodgeball.api.team.TeamState;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerState {

    private final Player player;
    private final PlayerStats stats;

    public boolean alive;
    private TeamState team;

    public PlayerState(Player player) {
        this.player = player;
        this.stats = new PlayerStats();
        this.alive = true;
    }

    public void reset() {
        this.team = null;
        this.stats.reset();
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerStats getStats() {
        return stats;
    }

    public TeamState getTeam() {
        return team;
    }

    public UUID getUuid() {
        return player.getUniqueId();
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void message(ComponentLike message) {
        player.sendMessage(message);
    }

    public void setTeam(TeamState team) {
        this.team = team;
    }
}
