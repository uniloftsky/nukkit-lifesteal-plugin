package net.uniloftsky.nukkit.lifesteal.config;

import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginLogger;
import net.uniloftsky.nukkit.lifesteal.LifestealPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LifestealConfigTest {

    private static final String VALID_WEAPON_NAME = "Wooden Sword";
    private static final String INVALID_WEAPON_NAME = "unknown";

    private static final String MAIN_CONFIG = "config.json";

    private static final String MOCKED_JSON = "{\"chance\":25,\"weapons\":[{\"id\":268,\"lifesteal\":10}]}";

    /* These values should be defined respectively to MOCKED_JSON */
    private static final int LIFESTEAL_CHANCE = 25;
    private static final int WEAPON_ID = 268;
    private static final int LIFESTEAL_POTENTIAL = 10;

    @Mock
    private LifestealPlugin plugin;

    @Mock
    private File pluginDataFolder;

    @Mock
    private Map<Integer, LifestealWeapon> weapons;

    @Mock
    private PluginLogger logger;

    @Spy
    @InjectMocks
    private LifestealConfig config;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInit() throws IOException {

        // given
        mockLogger();

        doNothing().when(config).registerWeapon(any(LifestealWeapon.class));
        doReturn(MOCKED_JSON).when(config).getConfigContents(MAIN_CONFIG);

        // when
        boolean result = config.init();

        // then
        then(logger).should(times(2)).info(anyString());
        then(plugin).should().saveResource(MAIN_CONFIG);
        assertTrue(result);
    }

    @Test
    public void testInitCannotGetConfig() throws IOException {

        // given
        mockLogger();

        doThrow(new IOException()).when(config).getConfigContents(MAIN_CONFIG);

        // when
        boolean result = config.init();

        // then
        then(logger).should(times(2)).info(anyString());
        then(logger).should().error(anyString());
        then(plugin).should().saveResource(MAIN_CONFIG);
        assertFalse(result);
    }

    @Test
    public void testGetConfigContents() throws IOException {
        Path mockedPath = mock(Path.class);
        given(pluginDataFolder.toPath()).willReturn(mockedPath);
        given(mockedPath.resolve(MAIN_CONFIG)).willReturn(mockedPath);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readAllBytes(mockedPath)).thenReturn(MOCKED_JSON.getBytes()); // mocked configuration JSON file

            // when
            String result = config.getConfigContents(MAIN_CONFIG);

            assertEquals(MOCKED_JSON, result);
        }
    }

    @Test
    public void testRegisterWeapon() {

        // given
        mockLogger();

        final String itemName = VALID_WEAPON_NAME;
        LifestealWeapon weapon = new LifestealWeapon(WEAPON_ID, LIFESTEAL_POTENTIAL);

        try (MockedStatic<Item> mockedStaticItem = mockStatic(Item.class)) {
            Item mockedItem = mock(Item.class);
            given(mockedItem.getName()).willReturn(itemName);
            given(mockedItem.getId()).willReturn(WEAPON_ID);
            mockedStaticItem.when(() -> Item.get(weapon.getId())).thenReturn(mockedItem);

            doReturn(true).when(config).isWeaponItemValid(mockedItem);

            // when
            config.registerWeapon(weapon);
        }

        // then
        then(weapons).should().put(WEAPON_ID, weapon);
        then(logger).should().info(anyString());

        assertEquals(itemName, weapon.getName());
    }

    @Test
    public void testRegisterInvalidWeapon() {

        // given
        mockLogger();

        LifestealWeapon weapon = new LifestealWeapon(WEAPON_ID, LIFESTEAL_POTENTIAL);
        try (MockedStatic<Item> mockedStaticItem = mockStatic(Item.class)) {
            Item mockedItem = mock(Item.class);
            mockedStaticItem.when(() -> Item.get(weapon.getId())).thenReturn(mockedItem);
            doReturn(false).when(config).isWeaponItemValid(mockedItem);

            // when
            config.registerWeapon(weapon);
        }

        // then
        then(weapons).should(times(0)).put(WEAPON_ID, weapon);
        then(logger).should().warning(anyString());
    }

    @Test
    public void testGetLifestealChance() {

        // given
        doNothing().when(config).isInitialized();

        // when
        int result = config.getLifestealChance();

        // then
        assertEquals(0, result);
    }

    @Test
    public void testGetWeapons() {

        // given
        doNothing().when(config).isInitialized();

        // when
        Map<Integer, LifestealWeapon> result = config.getWeapons();

        // then
        assertNotNull(result);
    }

    @Test
    public void testGetWeapon() {

        // given
        doNothing().when(config).isInitialized();

        LifestealWeapon weapon = new LifestealWeapon(WEAPON_ID, LIFESTEAL_POTENTIAL);
        given(weapons.get(WEAPON_ID)).willReturn(weapon);

        // when
        Optional<LifestealWeapon> result = config.getWeapon(WEAPON_ID);

        // then
        assertTrue(result.isPresent());

        LifestealWeapon fromResult = result.get();
        assertEquals(WEAPON_ID, fromResult.getId());
        assertEquals(LIFESTEAL_POTENTIAL, fromResult.getLifesteal());
    }

    @Test
    public void testGetWeaponNotPresent() {

        // given
        doNothing().when(config).isInitialized();

        given(weapons.get(WEAPON_ID)).willReturn(null);

        // when
        Optional<LifestealWeapon> result = config.getWeapon(WEAPON_ID);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    public void testIsWeaponItemValid() {

        // given
        Item item = mock(Item.class);
        given(item.isNull()).willReturn(false);
        given(item.getName()).willReturn(VALID_WEAPON_NAME);
        given(item.isSword()).willReturn(true);

        // when
        boolean result = config.isWeaponItemValid(item);

        // then
        assertTrue(result);
    }

    @Test
    public void testIsWeaponItemValidIsNull() {

        // given
        Item item = mock(Item.class);
        given(item.isNull()).willReturn(true);

        // when
        boolean result = config.isWeaponItemValid(item);

        // then
        assertFalse(result);
    }

    @Test
    public void testIsWeaponItemValidIsUnknown() {

        // given
        Item item = mock(Item.class);
        given(item.isNull()).willReturn(false);
        given(item.getName()).willReturn(INVALID_WEAPON_NAME);

        // when
        boolean result = config.isWeaponItemValid(item);

        // then
        assertFalse(result);
    }

    @Test
    public void testIsWeaponItemValidIsNotAxeOrSword() {

        // given
        Item item = mock(Item.class);
        given(item.isNull()).willReturn(false);
        given(item.getName()).willReturn(VALID_WEAPON_NAME);
        given(item.isSword()).willReturn(false);

        // when
        boolean result = config.isWeaponItemValid(item);

        // then
        assertFalse(result);
    }

    @Test
    public void testIsInitialized() {

        // when
        try {
            config.isInitialized();
        } catch (RuntimeException ignored) {
        }

    }

    // Invoke if logger should be mocked
    private void mockLogger() {
        given(plugin.getLogger()).willReturn(logger);
    }
}
