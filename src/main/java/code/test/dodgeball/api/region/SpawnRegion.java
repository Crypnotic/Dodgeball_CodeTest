package code.test.dodgeball.api.region;

import org.bukkit.Location;

public class SpawnRegion {

    private final Location min;
    private final float yaw;

    private final double xSize;
    private final double zSize;

    private SpawnRegion(Location min, Location max, float yaw) {
        this.min = min;
        this.yaw = yaw;

        this.xSize = Math.abs(max.getX() - min.getX());
        this.zSize = Math.abs(max.getZ() - min.getZ());
    }

    public Location[] generateEvenlySpacedLocations(int count) {
        double xDistance = xSize / (count + 1);
        double zDistance = zSize / (count + 1);

        Location[] locations = new Location[count];
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                Location location = new Location(
                        min.getWorld(),
                        min.getX() + (xDistance * (i + 1)),
                        min.getY(),
                        min.getZ() + (zDistance * (j + 1)),
                        yaw, 0
                );

                locations[i] = location;
            }
        }

        return locations;
    }

    public static SpawnRegion create(Location a, Location b, float yaw) {
        Location min = new Location(
                a.getWorld(),
                Math.min(a.getX(), b.getX()),
                a.getY(),
                Math.min(a.getZ(), b.getZ())
        );

        Location max = new Location(
                a.getWorld(),
                Math.max(a.getX(), b.getX()),
                a.getY(),
                Math.max(a.getZ(), b.getZ())
        );

        return new SpawnRegion(min, max, yaw);
    }
}
