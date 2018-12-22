package fr.jrich.fallenkingdoms.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import fr.jrich.fallenkingdoms.FKPlugin;
import fr.jrich.fallenkingdoms.util.Cuboid;
import fr.jrich.fallenkingdoms.util.ItemBuilder;

public enum Team {
    BLUE("blue", "Bleue", Material.INK_SACK, DyeColor.BLUE.getDyeData(), ChatColor.BLUE),
    RED("red", "Rouge", Material.INK_SACK, DyeColor.RED.getDyeData(), ChatColor.RED),
    GREEN("green", "Verte", Material.INK_SACK, DyeColor.LIME.getDyeData(), ChatColor.GREEN),
    YELLOW("yellow", "Jaune", Material.INK_SACK, DyeColor.YELLOW.getDyeData(), ChatColor.YELLOW),
    SPEC("spec", "Spec", null, (short) 0, ChatColor.GRAY);

    public static Team getPlayerTeam(Player player) {
        if (player == null) {
            return SPEC;
        } else if (!player.hasMetadata("team")) {
            for (Team team : Team.values()) {
                if (team.craftTeam.getPlayers().contains(player)) { return team; }
            }
        } else {
            String teamName = player.getMetadata("team").get(0).asString();
            return Team.getTeam(teamName);
        }
        return SPEC;
    }

    public static Team getRandomTeam() {
        Team lastTeam = Team.BLUE;
        for (Team team : Team.values()) {
            if (team != SPEC && lastTeam != team && team.craftTeam.getSize() < lastTeam.craftTeam.getSize()) {
                lastTeam = team;
            }
        }
        return lastTeam;
    }

    public static Team getTeam(String name) {
        for (Team team : Team.values()) {
            if (team.craftTeam != null && team.craftTeam.getName().equalsIgnoreCase(name)) { return team; }
        }
        return SPEC;
    }

    public static Team getTeam(ChatColor color) {
        for (Team team : Team.values()) {
            if (team.color == color) { return team; }
        }
        return null;
    }

    public static List<Team> getAliveTeams() {
        List<Team> aliveTeams = new ArrayList<>();
        for (Team team : Team.values()) {
            if (team != Team.SPEC && team.getOnlinePlayers().size() > 0) {
                aliveTeams.add(team);
            }
        }
        return aliveTeams;
    }

    private String name;
    private String displayName;
    private ItemStack icon;
    private ChatColor color;
    private org.bukkit.scoreboard.Team craftTeam;
    private Location spawnLocation;
    private Location witherLocation;
    private Cuboid cuboid;

    private Team(String name, String displayName, Material material, short durability, ChatColor color) {
        this.name = name;
        this.displayName = displayName;
        if (material != null) {
            icon = new ItemBuilder(material, 1, durability).setTitle(color + "Rejoindre l'équipe " + displayName).build();
        }
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public org.bukkit.scoreboard.Team getCraftTeam() {
        return craftTeam;
    }

    public void setCraftTeam(org.bukkit.scoreboard.Team craftTeam) {
        this.craftTeam = craftTeam;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getWitherLocation() {
        return witherLocation;
    }

    public void setWitherLocation(Location witherLocation) {
        this.witherLocation = witherLocation;
    }

    public boolean isWither(Wither wither) {
        if (this == Team.SPEC || cuboid == null) { return false; }
        return wither.getCustomName().equals(color + "Equipe " + displayName);
    }

    public void loose(Wither wither) {
        for (final Player player : this.getOnlinePlayers()) {
            player.damage(Double.MAX_VALUE);
            FKPlugin.i.setSpectator(player, true);
        }
        cuboid = null;
        wither.getWorld().strikeLightning(witherLocation);
        wither.remove();
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public void addPlayer(Player player) {
        player.setMetadata("team", new FixedMetadataValue(FKPlugin.i, name));
        player.setPlayerListName(color + (player.getName().length() > 14 ? player.getName().substring(0, 14) : player.getName()));
        craftTeam.addPlayer(player);
        if (this != Team.SPEC) {
            Score score = this.getScore();
            this.setScore(score.getScore() + 1);
        }
    }

    public void removePlayer(Player player) {
        player.removeMetadata("team", FKPlugin.i);
        craftTeam.removePlayer(player);
        if (this != Team.SPEC) {
            Score score = this.getScore();
            this.setScore(score.getScore() - 1);
        }
    }

    public Score getScore() {
        Score objScore = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams").getScore(color + "Equipe " + displayName);
        return objScore;
    }

    public void setScore(int score) {
        if (Step.isStep(Step.LOBBY)) {
            Score objScore = this.getScore();
            if (score == 0) {
                objScore.setScore(1);
            }
            objScore.setScore(score);
        }
    }

    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();
        for (OfflinePlayer offline : craftTeam.getPlayers()) {
            if (offline instanceof Player && offline.isOnline()) {
                players.add((Player) offline);
            }
        }
        return players;
    }

    public void broadcastMessage(String msg) {
        for (Player player : this.getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }

    public void createTeam(Scoreboard scoreboard) {
        craftTeam = scoreboard.getTeam(name);
        if (craftTeam == null) {
            craftTeam = scoreboard.registerNewTeam(name);
        }
        craftTeam.setPrefix(color.toString());
        craftTeam.setDisplayName(name);
        craftTeam.setAllowFriendlyFire(false);
    }
}
