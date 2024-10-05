package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.item.Item;

import java.util.Optional;

public enum WeaponType {

    WOODEN_SWORD(Item.WOODEN_SWORD, 5),
    WOODEN_AXE(Item.WOODEN_AXE, 5),

    STONE_SWORD(Item.STONE_SWORD, 8),
    STONE_AXE(Item.STONE_AXE, 8),

    IRON_SWORD(Item.IRON_SWORD, 10),
    IRON_AXE(Item.IRON_AXE, 10),

    GOLD_SWORD(Item.GOLD_SWORD, 9),
    GOLD_AXE(Item.GOLD_AXE, 9),

    DIAMOND_SWORD(Item.DIAMOND_SWORD, 15),
    DIAMOND_AXE(Item.DIAMOND_AXE, 15);

    /**
     * The ID of Minecraft item
     */
    private final int itemId;

    /**
     * Percentage of the lifesteal depending on the weapon material type.
     * Holds the value in percents (%)
     */
    private final int lifesteal;


    WeaponType(int itemId, int lifesteal) {
        this.itemId = itemId;
        this.lifesteal = lifesteal;
    }

    public int getItemId() {
        return itemId;
    }

    public int getLifesteal() {
        return lifesteal;
    }

    public static Optional<WeaponType> findWeaponById(int weaponId) {
        for (WeaponType weapon : WeaponType.values()) {
            if (weapon.getItemId() == weaponId) {
                return Optional.of(weapon);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
