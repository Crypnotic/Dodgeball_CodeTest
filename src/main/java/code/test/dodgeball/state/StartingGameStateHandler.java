package code.test.dodgeball.state;

import code.test.dodgeball.api.state.player.PlayerState;
import code.test.dodgeball.api.state.game.GameState;
import code.test.dodgeball.api.team.TeamDescription;
import code.test.dodgeball.api.team.TeamState;
import code.test.dodgeball.manager.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static code.test.dodgeball.api.util.StringUtil.formatAndColor;

public class StartingGameStateHandler extends CommonGameStateHandler {

    public StartingGameStateHandler(JavaPlugin plugin, GameManager manager) {
        super(plugin, manager, GameState.GAME_STARTING);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        assignTeams();

        spawnSnowballs();

        fixTeamImbalance();

        manager.getParticipants().values().forEach(player -> player.getPlayer().getInventory().clear());

        manager.setState(GameState.GAME_PLAYING);
    }

    private void assignTeams() {
        for (PlayerState player : manager.getParticipants().values()) {
            TeamState team = pickRandomBalancedTeam();

            player.setTeam(team);
            team.add(player);

            player.setAlive(true);
        }

        spawnPlayers(manager.getBlueTeam());
        spawnPlayers(manager.getRedTeam());
    }

    private TeamState pickRandomBalancedTeam() {
        int blueTeamCount = manager.getBlueTeam().getPlayerCount();
        int redTeamCount = manager.getRedTeam().getPlayerCount();

        if (blueTeamCount > redTeamCount) return manager.getRedTeam();
        if (redTeamCount > blueTeamCount) return manager.getBlueTeam();

        return ThreadLocalRandom.current().nextInt(2) == 0 ? manager.getBlueTeam() : manager.getRedTeam();
    }

    private void spawnPlayers(TeamState team) {
        List<PlayerState> players = team.getPlayers();
        Location[] spawns = team.getSpawn().generateEvenlySpacedLocations(team.getPlayerCount());
        for (int i = 0; i < players.size(); i++) {
            PlayerState player = players.get(i);

            player.getPlayer().teleport(spawns[i]);
        }
    }

    private void spawnSnowballs() {
        int snowballCount = manager.getParticipants().size() / 2;
        for (Location location : manager.getSnowballSpawns().generateEvenlySpacedLocations(snowballCount)) {
            manager.getEntityManager().dropItem(location, Material.SNOWBALL);
        }
    }

    private void fixTeamImbalance() {
        TeamState blueTeam = manager.getBlueTeam();
        TeamState redTeam = manager.getRedTeam();

        TeamState unbalancedAwardTeam = null;
        if (redTeam.getPlayerCount() > blueTeam.getPlayerCount()) {
            unbalancedAwardTeam = blueTeam;
        } else if (blueTeam.getPlayerCount() > redTeam.getPlayerCount()) {
            unbalancedAwardTeam = redTeam;
        }

        if (unbalancedAwardTeam != null) {
            List<PlayerState> players = unbalancedAwardTeam.getPlayers();

            PlayerState player = players.get(ThreadLocalRandom.current().nextInt(players.size()));

            player.getPlayer().getInventory().addItem(new ItemStack(Material.SNOWBALL));

            manager.broadcast(
                    formatAndColor("{0} &7was awarded an extra snowball due to team imbalance.", TeamDescription.display(player))
            );
        }
    }
}
