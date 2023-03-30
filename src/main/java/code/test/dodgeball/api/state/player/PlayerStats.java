package code.test.dodgeball.api.state.player;

import java.util.Optional;

public class PlayerStats {

    private int eliminations = 0;
    private String eliminatedByPlayerName;
    private int snowballsThrown = 0;

    public void reset() {
        this.eliminations = 0;
        this.eliminatedByPlayerName = null;
        this.snowballsThrown = 0;
    }

    public int getEliminations() {
        return eliminations;
    }

    public void addElimination() {
        this.eliminations += 1;
    }

    public Optional<String> getEliminatedByPlayerName() {
        return Optional.ofNullable(eliminatedByPlayerName);
    }

    public void setEliminatedByPlayerName(String eliminatedByPlayerName) {
        this.eliminatedByPlayerName = eliminatedByPlayerName;
    }

    public int getSnowballsThrown() {
        return snowballsThrown;
    }

    public void addSnowballThrow() {
        this.snowballsThrown += 1;
    }
}
