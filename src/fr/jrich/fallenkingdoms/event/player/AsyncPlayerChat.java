package fr.jrich.fallenkingdoms.event.player;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class AsyncPlayerChat extends FKListener {
    public AsyncPlayerChat(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Team playerTeam = Team.getPlayerTeam(player);
        event.setFormat(playerTeam.getColor() + player.getName() + ChatColor.WHITE + ": " + event.getMessage());
        if (Step.isStep(Step.IN_GAME)) {
            if (playerTeam == Team.SPEC || !event.getMessage().startsWith("@")) {
                if (playerTeam != Team.SPEC) {
                    event.setFormat("[" + playerTeam.getColor() + StringUtils.capitalize(playerTeam.getDisplayName()) + ChatColor.WHITE + "] " + ChatColor.RESET + event.getFormat());
                }
                for (Player online : Bukkit.getOnlinePlayers()) {
                    Team team = Team.getPlayerTeam(online);
                    if (team != null && team != playerTeam) {
                        event.getRecipients().remove(online);
                    }
                }
            } else {
                event.setFormat("(Tous) " + ChatColor.RESET + event.getFormat().replaceFirst("@", ""));
            }
        }
    }
}
