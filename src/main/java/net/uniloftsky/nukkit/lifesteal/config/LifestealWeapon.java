package net.uniloftsky.nukkit.lifesteal.config;

import java.util.Objects;

/**
 * Registered weapon that supports lifesteal feature
 */
public class LifestealWeapon {

    /**
     * ID of weapon (Minecraft item ID)
     */
    private final int id;

    /**
     * Percentage of lifesteal
     */
    private final int lifesteal;

    /**
     * Weapon name
     */
    private String name;

    public LifestealWeapon(int id, int lifesteal) {
        this.id = id;
        this.lifesteal = lifesteal;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLifesteal() {
        return lifesteal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LifestealWeapon that = (LifestealWeapon) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lifesteal=" + lifesteal +
                '}';
    }
}
