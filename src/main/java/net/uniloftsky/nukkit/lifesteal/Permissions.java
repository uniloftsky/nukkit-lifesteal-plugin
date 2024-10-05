package net.uniloftsky.nukkit.lifesteal;

public enum Permissions {

    LIFESTEAL_ABILITY_PERMISSION("uniloftsky.nukkit.lifesteal");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return this.name() + ":" + this.permission;
    }
}
