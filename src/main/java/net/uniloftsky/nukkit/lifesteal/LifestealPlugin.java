package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.plugin.PluginBase;
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig;
import net.uniloftsky.nukkit.lifesteal.listener.EventListener;

/**
 * Main plugin class
 */
public class LifestealPlugin extends PluginBase {

    private LifestealCore lifestealCore;

    private LifestealConfig config;

    /**
     * Invoked on plugin enabling, when server starts
     */
    @Override
    public void onEnable() {
        this.config = new LifestealConfig(this);
        boolean initialized = this.config.init();

        if (!initialized) {
            this.getLogger().error("Configuration cannot be initialized. Plugin will be disabled");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.lifestealCore = new LifestealCore(config);
        this.getServer().getPluginManager().registerEvents(new EventListener(this.getLogger(), lifestealCore), this);
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
