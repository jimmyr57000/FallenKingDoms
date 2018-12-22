package fr.jrich.fallenkingdoms.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import org.bukkit.event.Listener;

import fr.jrich.fallenkingdoms.FKPlugin;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FKListener implements Listener {
    protected FKPlugin plugin;
}
