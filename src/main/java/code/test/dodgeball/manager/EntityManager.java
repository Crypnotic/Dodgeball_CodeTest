package code.test.dodgeball.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class EntityManager {

    private final Set<Entity> entities;

    public EntityManager() {
        this.entities = new HashSet<>();
    }

    public boolean isTrackedItem(Entity entity) {
        return entities.contains(entity);
    }

    public void remove(Entity entity) {
        entity.remove();
        entities.remove(entity);
    }

    public void dropItem(Location location, Material material) {
        dropItem(location, new ItemStack(material));
    }

    public void dropItem(Location location, ItemStack item) {
        Item entity = location.getWorld().dropItem(location, item);

        entity.setVelocity(new Vector(0, 0, 0));

        entities.add(entity);
    }

    public void clear() {
        entities.forEach(Entity::remove);
        entities.clear();
    }
}
