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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class to hold configurable data. It holds information about lifesteal chance, registered weapons and its lifesteal potential
 */
public final class LifestealConfig {

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
    private LifestealPlugin plugin;

    /**
     * Gson instance to parse JSON configs
     */
    private final Gson gson;

    /**
     * Plugin data folder
     */
    private File pluginDataFolder;

    /**
     * Storage with registered weapons. Key - id, value - weapon
     */
    private Map<Integer, LifestealWeapon> weapons = new HashMap<>();

    /**
     * Chance of lifesteal. Read from config
     */
    private int lifestealChance;

    /**
     * Flag to define if config is initialized. After successful initialization the value changes to true
     */
    private boolean configInitialized = false;

    public LifestealConfig(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.pluginDataFolder = plugin.getDataFolder();
    }

    /**
     * Initialization method. Must be invoked after instantiation before calling any further method
     *
     * @return true if initialization was successful, false if wasn't
     */
    public boolean init() {
        plugin.getLogger().info("Loading configuration...");
        configInitialized = processMainConfig();
        return configInitialized;
    }

    boolean processMainConfig() {
        plugin.getLogger().info("Loading " + MAIN_CONFIG);
        plugin.saveResource(MAIN_CONFIG);

        String mainConfigContents;
        try {
            mainConfigContents = getConfigContents(MAIN_CONFIG);
        } catch (IOException ex) {
            plugin.getLogger().error("Cannot get " + MAIN_CONFIG + " file");
            return false;
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
        return true;
    }

    public int getLifestealChance() {
        isInitialized();
        return lifestealChance;
    }

    public Map<Integer, LifestealWeapon> getWeapons() {
        isInitialized();
        return new HashMap<>(weapons);
    }

    public Optional<LifestealWeapon> getWeapon(int id) {
        isInitialized();
        return Optional.ofNullable(weapons.get(id));
    }

    String getConfigContents(String configName) throws IOException {
        Path configPath = pluginDataFolder.toPath().resolve(configName);
        return new String(Files.readAllBytes(configPath));
    }

    void registerWeapon(LifestealWeapon weapon) {
        if (weapon.getId() > 0) {
            Item minecraftItem = Item.get(weapon.getId());
            if (!isWeaponItemValid(minecraftItem)) /* if weapon is not valid */ {
                plugin.getLogger().warning("Cannot register a weapon with ID " + weapon.getId() + ". It either doesn't exist or is not a weapon");
            } else /* if weapon is valid, proceed to retrieve its name and register it */ {
                weapon.setName(minecraftItem.getName());
                this.weapons.put(minecraftItem.getId(), weapon);
                plugin.getLogger().info("Registered weapon: " + weapon);
            }
        }
    }

    /**
     * Check if the imported from config weapon is a valid minecraft weapon item
     *
     * @param item item to check
     * @return true if valid, false if not
     */
    boolean isWeaponItemValid(Item item) {
        return !item.isNull() && !item.getName().equalsIgnoreCase(UNKNOWN_ITEM) && (item.isAxe() || item.isSword());
    }

    void isInitialized() {
        if (!configInitialized) {
            throw new RuntimeException("Config wasn't initialized properly");
        }
    }

    static class MainConfigFields {
        static final String LIFESTEAL_CHANCE_FIELD = "chance";
        static final String WEAPONS_LIST_FIELD = "weapons";
    }
}
