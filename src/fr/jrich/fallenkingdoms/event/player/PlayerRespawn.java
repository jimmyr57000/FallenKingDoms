package fr.jrich.fallenkingdoms.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class PlayerRespawn extends FKListener {
    public PlayerRespawn(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
        if (!Step.isStep(Step.LOBBY)) {
            if (playerTeam == Team.SPEC) {
                event.setRespawnLocation(playerTeam.getSpawnLocation() == null ? plugin.lobbyLocation : playerTeam.getSpawnLocation());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.setFlying(true);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                    }
                }.runTaskLater(plugin, 1);
            } else {
                event.setRespawnLocation(playerTeam.getSpawnLocation());
            }
        } else {
            event.setRespawnLocation(plugin.lobbyLocation);
        }
    }
}
