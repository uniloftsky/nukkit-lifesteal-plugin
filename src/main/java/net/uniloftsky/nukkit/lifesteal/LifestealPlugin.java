package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.plugin.PluginBase;
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig;
import net.uniloftsky.nukkit.lifesteal.listener.EventListener;

/**
 * Main plugin class
 */
public class LifestealPlugin extends PluginBase {

    private static LifestealPlugin INSTANCE;

    public static LifestealPlugin getInstance() {
        return INSTANCE;
    }

    private LifestealConfig config;

    /**
     * Invoked on plugin enabling, when server starts
     */
    @Override
    public void onEnable() {
        INSTANCE = this;

        this.config = new LifestealConfig(this);
        this.getLogger().info("Lifesteal chance: " + config.getLifestealChance());

        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getLogger().info("Lifesteal plugin enabled!");
    }

    /**
     * Invoked on plugin disabling, when server stops
     */
    @Override
    public void onDisable() {
        this.getLogger().info("Lifesteal plugin disabled!");
    }
}
