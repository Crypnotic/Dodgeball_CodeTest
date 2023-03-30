package code.test.dodgeball.api.util;

import code.test.dodgeball.api.region.ArenaRegion;
import code.test.dodgeball.api.region.SpawnRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;
import java.util.Optional;

public class LocationUtil {

    public static Optional<Location> parseLocation(ConfigurationNode node) {
        try {
            String[] data = Objects.requireNonNull(node.getString()).split(":");

            World world = Bukkit.getServer().getWorld(data[0]);
            double x = Double.parseDouble(data[1]);
            double y = Double.parseDouble(data[2]);
            double z = Double.parseDouble(data[3]);
            float yaw = Float.parseFloat(data[4]);
            float pitch = Float.parseFloat(data[5]);

            return Optional.of(new Location(world, x, y, z, yaw, pitch));
        } catch (NullPointerException exception) {
            return Optional.empty();
        }
    }

    public static Optional<SpawnRegion> parseSpawnRegion(ConfigurationNode node) {
        try {
            String[] data = Objects.requireNonNull(node.getString()).split(":");

            World world = Bukkit.getServer().getWorld(data[0]);
            double x1 = Double.parseDouble(data[1]);
            double y1 = Double.parseDouble(data[2]);
            double z1 = Double.parseDouble(data[3]);
            double x2 = Double.parseDouble(data[4]);
            double z2 = Double.parseDouble(data[5]);
            float yaw = Float.parseFloat(data[6]);

            return Optional.of(SpawnRegion.create(
                    new Location(world, x1, y1, z1),
                    new Location(world, x2, y1, z2),
                    yaw
            ));
        } catch (NullPointerException exception) {
            return Optional.empty();
        }
    }

    public static Optional<ArenaRegion> parseGameRegion(ConfigurationNode node) {
        try {
            String[] data = Objects.requireNonNull(node.getString()).split(":");

            World world = Bukkit.getServer().getWorld(data[0]);
            double x1 = Double.parseDouble(data[1]);
            double y1 = Double.parseDouble(data[2]);
            double z1 = Double.parseDouble(data[3]);
            double x2 = Double.parseDouble(data[4]);
            double y2 = Double.parseDouble(data[5]);
            double z2 = Double.parseDouble(data[6]);

            return Optional.of(ArenaRegion.create(
                    new Location(world, x1, y1, z1),
                    new Location(world, x2, y2, z2)
            ));
        } catch (NullPointerException exception) {
            return Optional.empty();
        }
    }
}
