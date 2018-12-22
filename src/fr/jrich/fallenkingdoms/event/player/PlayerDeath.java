package fr.jrich.fallenkingdoms.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Score;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.event.FKListener;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class PlayerDeath extends FKListener {
    public PlayerDeath(FKPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Team playerTeam = Team.getPlayerTeam(player);
        if (!Step.isStep(Step.IN_GAME) || playerTeam == Team.SPEC) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            event.setDroppedExp(0);
            return;
        }
        event.setDeathMessage(FKPlugin.prefix + event.getDeathMessage().replace(playerTeam.getColor() + player.getName(), playerTeam.getColor() + player.getName() + ChatColor.WHITE));
        if (player.getKiller() != null) {
            Score score = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("kills").getScore(player.getKiller());
            score.setScore(score.getScore() + 1);
        }
    }
}
