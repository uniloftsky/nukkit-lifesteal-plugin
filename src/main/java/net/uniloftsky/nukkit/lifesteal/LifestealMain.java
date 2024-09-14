package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.plugin.PluginBase;
import net.uniloftsky.nukkit.lifesteal.listener.EventListener;

/**
 * Main plugin class
 */
public class LifestealMain extends PluginBase {

    private static LifestealMain INSTANCE;

    public static LifestealMain getInstance() {
        return INSTANCE;
    }

    /**
     * Invoked on plugin enabling, when server starts
     */
    @Override
    public void onEnable() {
        INSTANCE = this;

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
