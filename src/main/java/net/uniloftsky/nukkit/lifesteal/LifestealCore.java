package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.BlockFace;

import java.util.Optional;

/**
 * Lifesteal core holds the logic regarding the lifesteal feature (incl. healing calculations, spawning particles etc.)
 */
public final class LifestealCore {

    /**
     * ID of Happy Villager particle
     */
    private static final int PARTICLE_ID = Particle.TYPE_VILLAGER_HAPPY;

    /**
     * Distance between player and spawned particle
     */
    private static final double PARTICLE_DISTANCE = 1.5;

    /**
     * Amount of particles around the player
     */
    private static final double PARTICLES_AMOUNT = 5;

    /**
     * Heal multiplier. It is needed because a player has 10 hearts but 20 HP
     */
    private static final int HEAL_MULTIPLIER = 2;

    private static LifestealCore INSTANCE;

    // Singleton static factory method
    public static LifestealCore getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LifestealCore();
        }
        return INSTANCE;
    }

    // Private constructor
    private LifestealCore() {
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

        if (target.isOnline() && target.isAlive()) /* if the player still online and alive */ {
            Optional<WeaponType> optionalWeapon = WeaponType.findWeaponById(itemInHand.getId());
            if (optionalWeapon.isPresent()) {
                WeaponType weapon = optionalWeapon.get();
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
        return false;
    }

    /**
     * Spawn the healing particle near the player
     *
     * @param target player near which particles will be spawned
     * @throws IllegalArgumentException if parameter target is null
     */
    void spawnHealingParticles(Player target) {
        if (target == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        double x = 0;
        double z = 0;
        BlockFace direction = target.getDirection();
        if (direction.getAxis() == BlockFace.Axis.X) {
            x = direction.getAxisDirection() == BlockFace.AxisDirection.NEGATIVE ? -PARTICLE_DISTANCE : PARTICLE_DISTANCE;
        } else if (direction.getAxis() == BlockFace.Axis.Z) {
            z = direction.getAxisDirection() == BlockFace.AxisDirection.NEGATIVE ? -PARTICLE_DISTANCE : PARTICLE_DISTANCE;
        }

        for (int i = 0; i < PARTICLES_AMOUNT; i++) {
            target.getLevel().addParticle(new GenericParticle(target.add(x, PARTICLE_DISTANCE, z), PARTICLE_ID));
        }
    }

    float calculateHealAmount(int dealtDamage, int lifesteal) {
        return (((float) dealtDamage) / 100 * lifesteal) * HEAL_MULTIPLIER;
    }

}
