package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig;
import net.uniloftsky.nukkit.lifesteal.config.LifestealWeapon;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Lifesteal core holds the logic regarding the lifesteal feature (incl. healing calculations, spawning particles etc.)
 */
public final class LifestealCore {

    /**
     * ID of Happy Villager particle
     */
    private static final int PARTICLE_ID = Particle.TYPE_VILLAGER_HAPPY;

    /**
     * Amount of particles around the player
     */
    private static final double PARTICLES_AMOUNT = 20;

    /**
     * Heal multiplier. It is needed because a player has 10 hearts but 20 HP
     */
    private static final int HEAL_MULTIPLIER = 2;

    /**
     * Config instance
     */
    private LifestealConfig config;

    public LifestealCore(LifestealConfig config) {
        this.config = config;
    }

    /**
     * Heal the player depending on the dealt damage and weapon type
     *
     * @param target     player to heal
     * @param itemInHand item that was used while attacking
     * @return true if player was healed and false if not
     * @throws IllegalArgumentException if parameter target or itemInHand is null
     */
    public boolean healPlayer(Player target, Item itemInHand) {
        if (target == null) {
            throw new IllegalArgumentException("Target player cannot be null!");
        }
        if (itemInHand == null) {
            throw new IllegalArgumentException("Item in hand cannot be null!");
        }

        if (target.isOnline() && target.isAlive() && target.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.getPermission())) /* if the player still online, alive and has a permission */ {
            int randomOfLifestealChance = ThreadLocalRandom.current().nextInt(100);
            int lifestealChance = config.getLifestealChance();
            if (randomOfLifestealChance <= lifestealChance) {
                Optional<LifestealWeapon> optionalWeapon = config.getWeapon(itemInHand.getId());
                if (optionalWeapon.isPresent()) {
                    LifestealWeapon weapon = optionalWeapon.get();
                    int dealtDamage = itemInHand.getAttackDamage();
                    if (dealtDamage > 0) /* if the dealt damage above zero, so we are not going to make needles calculations */ {
                        float amountOfHeal = calculateHealAmount(dealtDamage, weapon.getLifesteal());
                        target.heal(amountOfHeal);

                        // spawn healing particles
                        spawnHealingParticles(target);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Spawn the healing particle near the attacked entity
     *
     * @param target entity that was attacked
     * @throws IllegalArgumentException if parameter target is null
     */
    void spawnHealingParticles(Player target) {
        if (target == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        for (int i = 0; i < PARTICLES_AMOUNT; i++) {
            double x = ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
            double z = ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
            double y = ThreadLocalRandom.current().nextDouble(1, 2);
            Location locationToSpawnParticles = target.add(x, y, z);
            target.getLevel().addParticle(new GenericParticle(locationToSpawnParticles, PARTICLE_ID));
        }
    }

    float calculateHealAmount(int dealtDamage, int lifesteal) {
        return (((float) dealtDamage) / 100 * lifesteal) * HEAL_MULTIPLIER;
    }

}
