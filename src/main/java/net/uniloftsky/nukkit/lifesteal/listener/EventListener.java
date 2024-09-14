package net.uniloftsky.nukkit.lifesteal.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.plugin.PluginLogger;
import net.uniloftsky.nukkit.lifesteal.LifestealMain;

/**
 * Listener for in-game events
 */
public class EventListener implements Listener {

    /**
     * Percentage of the lifesteal depending on the weapon material type.
     * Holds the value in percents (%)
     */
    private static final double WOODEN_WEAPON_LIFESTEAL = 5;
    private static final double STONE_WEAPON_LIFESTEAL = 8;
    private static final double IRON_WEAPON_LIFESTEAL = 10;
    private static final double GOLD_WEAPON_LIFESTEAL = 12;
    private static final double DIAMOND_WEAPON_LIFESTEAL = 15;


    private final PluginLogger logger;

    public EventListener() {
        this.logger = LifestealMain.getInstance().getLogger();
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager().isPlayer) {
            Player player = (Player) event.getDamager();
            logger.info("Attacker: " + player.getName());
            logger.info("Target: " + event.getEntity().getName());
            Item itemInHand = player.getInventory().getItemInHand();
            switch (itemInHand.getId()) {
                case Item.WOODEN_AXE:
                case Item.WOODEN_SWORD: {
                    healPlayer(player, itemInHand.getAttackDamage(), WOODEN_WEAPON_LIFESTEAL);
                    createHealingParticle(player);
                    break;
                }
                case Item.STONE_AXE:
                case Item.STONE_SWORD: {
                    healPlayer(player, itemInHand.getAttackDamage(), STONE_WEAPON_LIFESTEAL);
                    createHealingParticle(player);
                    break;
                }
                case Item.IRON_AXE:
                case Item.IRON_SWORD: {
                    healPlayer(player, itemInHand.getAttackDamage(), IRON_WEAPON_LIFESTEAL);
                    createHealingParticle(player);
                    break;
                }
                case Item.GOLD_AXE:
                case Item.GOLD_SWORD: {
                    healPlayer(player, itemInHand.getAttackDamage(), GOLD_WEAPON_LIFESTEAL);
                    createHealingParticle(player);
                    break;
                }
                case Item.DIAMOND_AXE:
                case Item.DIAMOND_SWORD: {
                    healPlayer(player, itemInHand.getAttackDamage(), DIAMOND_WEAPON_LIFESTEAL);
                    createHealingParticle(player);
                    break;
                }
            }
            logger.info(itemInHand.toString());
        }
    }

    /**
     * Heal the player on their attack
     *
     * @param attacker    player to heal
     * @param dealtDamage damage that was dealt from player to another entity
     * @param percentage  percentage of healing from damage
     */
    private void healPlayer(Player attacker, int dealtDamage, double percentage) {
        if (attacker.isOnline() && attacker.isAlive() && dealtDamage > 0) {
            double amountOfHeal = (((double) dealtDamage) / 100 * percentage) * 2;
            attacker.heal((float) amountOfHeal);
            logger.info("Player was healed for: " + amountOfHeal + " amount of heal");
        }
    }

    /**
     * Create healing particle on Players attack
     *
     * @param attacker Player which attacks
     */
    private void createHealingParticle(Player attacker) {
        BlockFace direction = attacker.getDirection();
        logger.info(direction.getAxis().getName());
        logger.info(direction.getAxisDirection().name());

        double x = 0;
        double z = 0;
        if (direction.getAxis() == BlockFace.Axis.X) {
            x = direction.getAxisDirection() == BlockFace.AxisDirection.NEGATIVE ? -1.5 : 1.5;
        } else if (direction.getAxis() == BlockFace.Axis.Z) {
            z = direction.getAxisDirection() == BlockFace.AxisDirection.NEGATIVE ? -1.5 : 1.5;
        }

        for (int i = 0; i < 5; i++) {
            attacker.getLevel().addParticle(new HappyVillagerParticle(attacker.add(x, 1.5, z)));
        }
    }

}
