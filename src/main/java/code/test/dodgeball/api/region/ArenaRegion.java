package code.test.dodgeball.api.region;

import org.bukkit.Location;

public class ArenaRegion {

    private final Location min;
    private final Location max;

    private ArenaRegion(Location min, Location max) {
        this.min = min;
        this.max = max;
    }

    public boolean isInside(Location location) {
        if (min.getX() > location.getX() || max.getX() < location.getX()) {
            return false;
        }
        if (min.getY() > location.getY() || max.getY() < location.getY()) {
            return false;
        }
        if (min.getZ() > location.getZ() || max.getZ() < location.getZ()) {
            return false;
        }
        return true;
    }

    public static ArenaRegion create(Location a, Location b) {
        Location min = new Location(
                a.getWorld(),
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
        );

        Location max = new Location(
                a.getWorld(),
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        );

        return new ArenaRegion(min, max);
    }
}
