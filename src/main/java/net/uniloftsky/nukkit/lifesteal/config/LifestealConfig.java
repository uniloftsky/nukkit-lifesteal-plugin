package net.uniloftsky.nukkit.lifesteal.config;

import cn.nukkit.item.Item;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.uniloftsky.nukkit.lifesteal.LifestealPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to hold configurable data. It holds information about lifesteal chance, registered weapons and its lifesteal potential
 */
public class LifestealConfig {

    /**
     * Name of main config
     */
    private static final String MAIN_CONFIG = "config.json";

    /**
     * Default name of the unknown and non-existing item
     */
    private static final String UNKNOWN_ITEM = "unknown";

    /**
     * Plugin instance
     */
    private final LifestealPlugin plugin;

    /**
     * Gson instance to parse JSON configs
     */
    private final Gson gson;

    /**
     * Plugin data folder
     */
    private final File pluginDataFolder;

    /**
     * Storage with registered weapons
     */
    private final Set<LifestealWeapon> weapons = new HashSet<>();

    /**
     * Chance of lifesteal. Read from config
     */
    private int lifestealChance;

    public LifestealConfig(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.pluginDataFolder = plugin.getDataFolder();

        init();
    }

    private void init() {
        plugin.getLogger().info("Loading configuration...");
        processMainConfig();
    }

    private void processMainConfig() {
        plugin.getLogger().info("Loading " + MAIN_CONFIG);
        plugin.saveResource(MAIN_CONFIG);

        String mainConfigContents = ""; // will be always present
        try {
            mainConfigContents = getConfigContents(MAIN_CONFIG);
        } catch (IOException ex) {
            plugin.getLogger().error("Cannot get main " + MAIN_CONFIG + " file");
        }

        JsonObject configObject = JsonParser.parseString(mainConfigContents).getAsJsonObject();
        JsonElement lifestealChanceElement = configObject.get(MainConfigFields.LIFESTEAL_CHANCE_FIELD); // retrieve chance of lifesteal from config
        if (lifestealChanceElement != null) {
            this.lifestealChance = lifestealChanceElement.getAsInt();
        }

        List<JsonElement> jsonWeapons = configObject.getAsJsonArray(MainConfigFields.WEAPONS_LIST_FIELD).asList(); // retrieve weapons and register them
        for (JsonElement jsonWeapon : jsonWeapons) {
            LifestealWeapon weapon = gson.fromJson(jsonWeapon, LifestealWeapon.class);
            registerWeapon(weapon);
        }
    }

    public int getLifestealChance() {
        return lifestealChance;
    }

    public Set<LifestealWeapon> getWeapons() {
        return new HashSet<>(weapons);
    }

    private String getConfigContents(String configName) throws IOException {
        Path configPath = pluginDataFolder.toPath().resolve(configName);
        return new String(Files.readAllBytes(configPath));
    }

    private void registerWeapon(LifestealWeapon weapon) {
        if (weapon.getId() > 0) {
            Item minecraftItem = Item.get(weapon.getId());
            if (!isWeaponValid(minecraftItem)) /* if weapon is not valid */ {
                plugin.getLogger().warning("Cannot register a weapon with ID " + weapon.getId() + ". It either doesn't exist or is not a weapon");
            } else /* if weapon is valid, proceed to retrieve its name and register it */ {
                weapon.setName(minecraftItem.getName());
                this.weapons.add(weapon);
                plugin.getLogger().info("Registered weapon: " + weapon);
            }

        }
    }

    private boolean isWeaponValid(Item item) {
        return !item.isNull() && !item.getName().equalsIgnoreCase(UNKNOWN_ITEM) && (item.isAxe() || item.isSword());
    }

    private static class MainConfigFields {
        static final String LIFESTEAL_CHANCE_FIELD = "chance";
        static final String WEAPONS_LIST_FIELD = "weapons";
    }
}
