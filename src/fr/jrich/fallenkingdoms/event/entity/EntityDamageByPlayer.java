package fr.jrich.fallenkingdoms.event.entity;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.State;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class EntityDamageByPlayer extends FKListener {

    public EntityDamageByPlayer(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            final Player player = event.getDamager() instanceof Player ? (Player) event.getDamager() : (Player) ((Projectile) event.getDamager()).getShooter();
            if (!Step.isStep(Step.IN_GAME) || Team.getPlayerTeam(player) == Team.SPEC) {
                event.setCancelled(true);
            } else if (Step.isStep(Step.IN_GAME) && event.getEntity() instanceof Wither) {
                Team playerTeam = Team.getPlayerTeam(player);
                Wither wither = (Wither) event.getEntity();
                if (playerTeam.isWither(wither) || !State.isState(State.ASSAULT) && !State.isState(State.DEATHMATCH)) {
                    event.setCancelled(true);
                    if (!playerTeam.isWither(wither)) {
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                        player.sendMessage(FKPlugin.prefix + ChatColor.RED + "Vous devez attendre l'assaut pour attaquer les wither ennemis.");
                    }
                } else {
                    event.setCancelled(true);
                    wither.damage(event.getDamage());
                    for (Team team : Team.values()) {
                        if (team.isWither(wither)) {
                            double health = ((Damageable) wither).getHealth() / 2;
                            for (Player online : team.getOnlinePlayers()) {
                                online.playSound(online.getLocation(), Sound.WITHER_SPAWN, 1.0F, 0.5F);
                                online.sendMessage(ChatColor.RED + "!!! Le wither est attaqué !!! (" + health + "/" + 500 + ")");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
