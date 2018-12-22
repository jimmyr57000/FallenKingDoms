package fr.jrich.fallenkingdoms.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class PlayerDamage extends FKListener {
    public PlayerDamage(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!Step.isStep(Step.IN_GAME) || Team.getPlayerTeam((Player) event.getEntity()) == Team.SPEC) {
                event.setCancelled(true);
            }
        }
    }
}
