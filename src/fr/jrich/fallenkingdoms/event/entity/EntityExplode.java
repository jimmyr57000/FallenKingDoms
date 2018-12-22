package fr.jrich.fallenkingdoms.event.entity;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.State;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class EntityExplode extends FKListener {
    private long lastCheck = 0;

    public EntityExplode(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (Step.isStep(Step.LOBBY) || event.getEntity() instanceof WitherSkull) {
            event.setCancelled(true);
            return;
        } else if (Step.isStep(Step.IN_GAME)) {
            if (!State.isState(State.ASSAULT) && !State.isState(State.DEATHMATCH)) {
                for (Block block : new ArrayList<>(event.blockList())) {
                    for (Team team : Team.values()) {
                        if (team == Team.SPEC || team.getCuboid() == null) {
                            continue;
                        }
                        Location loc = block.getLocation();
                        if (!team.getCuboid().contains(loc)) {
                            continue;
                        }
                        event.blockList().remove(block);
                        if (System.currentTimeMillis() - lastCheck >= 60000) {
                            lastCheck = System.currentTimeMillis();
                            Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.GOLD + "Rappel : " + ChatColor.GRAY + "Les assauts ne sont autorisés qu'à partir du " + ChatColor.YELLOW + "2ème jour à 3h.");
                        }
                    }
                }
            }
        }
    }
}
