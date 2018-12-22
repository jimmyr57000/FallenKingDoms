package fr.jrich.fallenkingdoms.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;

public class PlayerAchievementAwarded extends FKListener {
    public PlayerAchievementAwarded(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerAchievementArwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
