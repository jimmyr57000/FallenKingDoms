package fr.jrich.fallenkingdoms.event.server;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.State;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.scheduler.BeginCountdown;
import fr.jrich.fallenkingdoms.scheduler.GameTask;

public class ServerListPing extends FKListener {
    public ServerListPing(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            if (BeginCountdown.started) {
                int remainingMins = BeginCountdown.timeUntilStart / 60 % 60;
                int remainingSecs = BeginCountdown.timeUntilStart % 60;
                event.setMotd(ChatColor.GREEN + "Début : " + (remainingMins > 0 ? remainingMins + "mn" : remainingSecs + "s"));
            } else {
                event.setMotd(Step.getMOTD());
            }
        } else if (Step.isStep(Step.IN_GAME)) {
            event.setMotd(ChatColor.YELLOW + "J" + GameTask.day + ">" + State.getState().getName());
        } else {
            event.setMotd(Step.getMOTD());
        }
    }
}
