package fr.jrich.fallenkingdoms.event.entity;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;
import fr.jrich.fallenkingdoms.util.MathUtils;

public class EntityDeath extends FKListener {

    public EntityDeath(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        } else if (event.getEntity() instanceof Creeper && ((Creeper) event.getEntity()).isPowered()) {
            int amount = MathUtils.random(1, 3);
            event.getDrops().add(new ItemStack(Material.TNT, amount));
        } else if (event.getEntity() instanceof Wither) {
            Wither wither = (Wither) event.getEntity();
            for (final Team team : Team.values()) {
                if (team == Team.SPEC || team.getCuboid() == null) {
                    continue;
                } else if (team.isWither(wither)) {
                    team.loose(wither);
                    if (wither.getKiller() != null) {
                        plugin.getData(wither.getKiller()).addCoins(8, false);
                        wither.getKiller().sendMessage(ChatColor.GRAY + "Gain de FunCoins + " + ChatColor.GOLD + "8.0" + ChatColor.GRAY + " (" + ChatColor.YELLOW + "Une équipe en moins !" + ChatColor.GRAY + ")");
                    }
                    break;
                }
            }
        }
    }
}
