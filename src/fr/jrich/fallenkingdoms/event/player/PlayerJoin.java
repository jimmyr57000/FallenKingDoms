package fr.jrich.fallenkingdoms.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;
import fr.jrich.fallenkingdoms.scheduler.BeginCountdown;
import fr.jrich.fallenkingdoms.util.ItemBuilder;

public class PlayerJoin extends FKListener {

    public PlayerJoin(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();
        if (!Step.isStep(Step.LOBBY) && player.hasPermission("games.join")) {
            event.setJoinMessage(null);
            plugin.setSpectator(player, false);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            player.teleport(Team.SPEC.getSpawnLocation() == null ? plugin.lobbyLocation : Team.SPEC.getSpawnLocation());
            player.setFlying(true);
        } else if (Step.isStep(Step.LOBBY)) {
            event.setJoinMessage(FKPlugin.prefix + ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " a rejoint la partie " + ChatColor.GREEN + "(" + Bukkit.getOnlinePlayers().length + "/" + Bukkit.getMaxPlayers() + ")");
            for (Team team : Team.values()) {
                if (team != Team.SPEC && team.getSpawnLocation() != null && team.getCuboid() != null) {
                    player.getInventory().addItem(team.getIcon());
                }
            }
            plugin.loadData(player);
            player.getInventory().setItem(8, new ItemBuilder(Material.NAME_TAG).setTitle(ChatColor.GOLD + "Kits " + ChatColor.GRAY + "(Clic-droit)").build());
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(plugin.lobbyLocation);
            if (Bukkit.getOnlinePlayers().length >= 4 && !BeginCountdown.started) {
                for (Team team : Team.values()) {
                    if (team != Team.SPEC && (team.getSpawnLocation() == null || team.getCuboid() == null)) {
                        BeginCountdown.started = true;
                        return;
                    }
                }
                new BeginCountdown(plugin);
            }
        }
    }
}
