package net.uniloftsky.nukkit.plugin;

import cn.nukkit.plugin.PluginBase;

public class LifestealMain extends PluginBase {

    private static LifestealMain INSTANCE;

    public static LifestealMain getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.getLogger().info("Lifesteal plugin enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Lifesteal plugin disabled!");
    }
}
