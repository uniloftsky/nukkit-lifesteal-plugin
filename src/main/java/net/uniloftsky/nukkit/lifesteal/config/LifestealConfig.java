package net.uniloftsky.nukkit.lifesteal.config;

import cn.nukkit.utils.Config;
import net.uniloftsky.nukkit.lifesteal.LifestealPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to hold configurable data. It holds information about lifesteal chance, registered weapons and its lifesteal potential
 */
public class LifestealConfig {

    /**
     * Plugin instance
     */
    private final LifestealPlugin plugin;

    /**
     * Plugin config instance
     */
    private final Config config;

    /**
     * Set storage with registered weapons
     */
    private final Set<LifestealWeapon> weapons = new HashSet<>();

    /**
     * Chance of lifesteal. Read from config
     */
    private int lifestealChance;

    public LifestealConfig(LifestealPlugin plugin) {
        this.plugin = plugin;

        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        init();
    }

    private void init() {
        plugin.getLogger().info("Started: Initializing LifestealConfig");

        this.lifestealChance = config.getInt("chance");

        plugin.getLogger().info("Finished: Initializing LifestealConfig. Registered weapons: " + Collections.emptyList());
    }

    public int getLifestealChance() {
        return lifestealChance;
    }
}
