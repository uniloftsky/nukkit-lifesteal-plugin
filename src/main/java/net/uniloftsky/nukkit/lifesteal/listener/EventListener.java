package net.uniloftsky.nukkit.lifesteal.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginLogger;
import net.uniloftsky.nukkit.lifesteal.LifestealCore;

/**
 * Listener for in-game events
 */
public class EventListener implements Listener {

    private PluginLogger logger;
    private LifestealCore lifeSteal;

    public EventListener(PluginLogger logger, LifestealCore lifeSteal) {
        this.logger = logger;
        this.lifeSteal = lifeSteal;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager().isPlayer) {
            Player player = (Player) event.getDamager();
            Item itemInHand = player.getInventory().getItemInHand();
            try {
                lifeSteal.healPlayer(player, itemInHand);
            } catch (IllegalArgumentException ex) {
                logger.error("Some exception occurred when tried to heal the player", ex);
            }
        }
    }

}
