package net.uniloftsky.nukkit.lifesteal.config;

import java.util.Objects;

public class LifestealWeapon {

    private final int id;
    private final String name;
    private final int lifesteal;

    public LifestealWeapon(int id, String name, int lifesteal) {
        this.id = id;
        this.name = name;
        this.lifesteal = lifesteal;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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
        return "[" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lifesteal=" + lifesteal +
                ']';
    }
}
