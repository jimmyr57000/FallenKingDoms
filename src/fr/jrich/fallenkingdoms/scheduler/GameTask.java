package fr.jrich.fallenkingdoms.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.handler.State;
import fr.jrich.fallenkingdoms.handler.Step;
import fr.jrich.fallenkingdoms.handler.Team;

public class GameTask extends BukkitRunnable {
    private FKPlugin plugin;
    private Objective objective;
    public static int day = 1;
    public static int hour = 6;
    public static int minutes = 0;
    public static int nextHour;
    private static String nextState;
    private static boolean deathMatch;

    public GameTask(FKPlugin plugin) {
        this.plugin = plugin;
        objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams");
        this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        if (!Step.isStep(Step.IN_GAME)) {
            this.cancel();
            plugin.stopGame(null);
            return;
        }
        for (Team team : Team.values()) {
            if (team == Team.SPEC) {
                continue;
            }
            String name = team.getColor() + "Wither " + (team.getDisplayName().endsWith("e") && team != Team.RED && team != Team.YELLOW ? team.getDisplayName().substring(0, team.getDisplayName().length() - 1) : team.getDisplayName());
            if (team.getCuboid() == null) {
                objective.getScoreboard().resetScores(name);
            } else {
                for (Wither wither : Bukkit.getWorlds().get(0).getEntitiesByClass(Wither.class)) {
                    if (!team.isWither(wither)) {
                        continue;
                    } else if (GameTask.deathMatch) {
                        wither.setHealth(((Damageable) wither).getHealth() - 1);
                    }
                    if (wither.getLocation().distanceSquared(team.getWitherLocation()) >= 0.5) {
                        wither.teleport(team.getWitherLocation());
                    }
                    objective.getScore(name).setScore((int) (((Damageable) wither).getHealth() / 2));
                }
            }
        }
        if (GameTask.minutes == 60) {
            GameTask.hour++;
            GameTask.minutes = 0;
            GameTask.nextHour -= 1;
            if (GameTask.hour == 24) {
                GameTask.hour = 0;
                GameTask.day++;
                return;
            }
        }
        if (GameTask.hour == 6 && GameTask.minutes == 0) {
            plugin.world.setTime(23000);
            if (GameTask.day == 1) {
                Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.AQUA + "Le jour " + ChatColor.GOLD + GameTask.day + ChatColor.AQUA + " se lève...");
                if (State.isState(State.NONE)) {
                    GameTask.nextState = "PvP";
                    plugin.world.setTime(23000);
                    Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.GRAY + "Préparez-vous avant de pouvoir vous battre.");
                    State.setState(State.PREPARATION);
                    GameTask.nextHour = 6;
                }
            }
        } else if (GameTask.hour == 12 && GameTask.minutes == 0) {
            plugin.world.setTime(6000);
            if (GameTask.day == 1 && State.isState(State.PREPARATION)) {
                GameTask.nextHour = 15;
                GameTask.nextState = "A l'assaut !";
                objective.getScoreboard().resetScores("PvP");
                Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.AQUA + "Vous pouvez désormais PvP.");
                State.setState(State.PVP);
            }
        } else if (GameTask.hour == 3 && GameTask.minutes == 0) {
            if (GameTask.day == 2 && State.isState(State.PVP)) {
                GameTask.nextHour = 20;
                GameTask.nextState = "Mort subite";
                objective.getScoreboard().resetScores("A l'assaut !");
                Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.AQUA + "Les assauts sont maintenants actifs, bonne chance.");
                State.setState(State.ASSAULT);
            }
        } else if (GameTask.hour == 23 && GameTask.minutes == 0) {
            if (GameTask.day == 2 && State.isState(State.ASSAULT)) {
                objective.getScoreboard().resetScores("Mort subite");
                objective.getScore("Mort subite").setScore(-1);
                Bukkit.broadcastMessage(FKPlugin.prefix + ChatColor.RED + "Deathmatch !" + ChatColor.AQUA + " Les wither perderont 1 de vie chaque seconde !");
                State.setState(State.DEATHMATCH);
                GameTask.deathMatch = true;
                return;
            }
        }
        objective.setDisplayName(ChatColor.GOLD + "Jour " + GameTask.day + ChatColor.GREEN + " " + (GameTask.hour < 10 ? "0" : "") + GameTask.hour + "H" + (GameTask.minutes < 10 ? "0" : "") + GameTask.minutes);
        objective.getScore(GameTask.nextState).setScore(-1 * GameTask.nextHour);
        GameTask.minutes++;
    }
}
