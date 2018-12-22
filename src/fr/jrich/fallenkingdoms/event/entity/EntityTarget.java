package fr.jrich.fallenkingdoms.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Team;

public class EntityTarget extends FKListener {

    public EntityTarget(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityTargetByEntity(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && Team.getPlayerTeam((Player) event.getTarget()) == Team.SPEC) {
            event.setCancelled(true);
        }
    }
}
