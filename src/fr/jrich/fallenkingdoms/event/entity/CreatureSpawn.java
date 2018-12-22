package fr.jrich.fallenkingdoms.event.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;
import fr.jrich.fallenkingdoms.util.MathUtils;

public class CreatureSpawn extends FKListener {

    public CreatureSpawn(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        } else {
            for (Team team : Team.values()) {
                if (team != Team.SPEC && team.getCuboid() != null && team.getCuboid().contains(event.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (event.getSpawnReason() != SpawnReason.CUSTOM) {
                for (int i = 0; i < 2; i++) {
                    World world = event.getEntity().getWorld();
                    Location loc = event.getEntity().getLocation();
                    Entity spawn = world.spawnEntity(world.getHighestBlockAt(loc.add(MathUtils.random(-32, 32), 0, MathUtils.random(-32, 32))).getLocation(), MathUtils.randomBoolean() ? event.getEntityType() : EntityType.CREEPER);
                    if (spawn instanceof Creeper) {
                        ((Creeper) spawn).setPowered(true);
                    }
                }
            }
        }
    }
}
