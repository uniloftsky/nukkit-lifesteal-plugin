package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.math.Vector3;
import net.uniloftsky.nukkit.lifesteal.config.LifestealConfig;
import net.uniloftsky.nukkit.lifesteal.config.LifestealWeapon;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LifestealCoreTest {

    private final static int LIFESTEAL_CHANCE = 25;
    private MockedStatic<ThreadLocalRandom> mockedThreadLocal;
    private ThreadLocalRandom random;

    @Mock
    private LifestealConfig config;

    @Spy
    @InjectMocks
    private LifestealCore core = new LifestealCore(config);

    @BeforeEach
    void beforeTest() {
        random = mock(ThreadLocalRandom.class);
        mockedThreadLocal = mockStatic(ThreadLocalRandom.class);
        mockedThreadLocal.when(ThreadLocalRandom::current).thenReturn(random);
    }

    @AfterEach
    void afterTest() {
        mockedThreadLocal.close();
    }

    @Test
    public void testHealPlayer() {

        // given
        Player player = mock(Player.class);
        given(player.isOnline()).willReturn(true);
        given(player.isAlive()).willReturn(true);
        given(player.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.getPermission())).willReturn(true);

        int dealtDamage = 10;
        int mockedId = 666;
        Item itemInHand = mock(Item.class);
        given(itemInHand.getId()).willReturn(mockedId);
        given(itemInHand.getAttackDamage()).willReturn(dealtDamage);

        given(config.getLifestealChance()).willReturn(LIFESTEAL_CHANCE);
        given(random.nextInt(100)).willReturn(LIFESTEAL_CHANCE);

        LifestealWeapon weapon = new LifestealWeapon(mockedId, 10);
        given(config.getWeapon(mockedId)).willReturn(Optional.of(weapon));

        doNothing().when(core).spawnHealingParticles(player);

        // when
        boolean result = core.healPlayer(player, itemInHand);

        // then
        assertTrue(result);

        float amountOfHeal = core.calculateHealAmount(dealtDamage, weapon.getLifesteal());
        then(player).should().heal(amountOfHeal);
        then(core).should().spawnHealingParticles(player);
    }

    @Test
    public void testHealPlayerOffline() {

        // given
        Player player = mock(Player.class);
        given(player.isOnline()).willReturn(false);

        // when
        boolean result = core.healPlayer(player, mock(Item.class));

        // then
        assertFalse(result);
        then(player).should(times(0)).heal(any());
    }

    @Test
    public void testHealPlayerNotAlive() {

        // given
        Player player = mock(Player.class);
        given(player.isOnline()).willReturn(true);
        given(player.isAlive()).willReturn(false);

        // when
        boolean result = core.healPlayer(player, mock(Item.class));

        // then
        assertFalse(result);
        then(player).should(times(0)).heal(any());
    }

    @Test
    public void testHealPlayerNoPermission() {

        // given
        Player player = mock(Player.class);
        given(player.isOnline()).willReturn(true);
        given(player.isAlive()).willReturn(true);
        given(player.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.getPermission())).willReturn(false);

        // when
        boolean result = core.healPlayer(player, mock(Item.class));

        // then
        assertFalse(result);
        then(player).should(times(0)).heal(any());
    }

    @Test
    public void testHealPlayerBadChance() {

        // given
        Player player = mock(Player.class);
        given(player.isOnline()).willReturn(true);
        given(player.isAlive()).willReturn(true);
        given(player.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.getPermission())).willReturn(true);

        given(config.getLifestealChance()).willReturn(LIFESTEAL_CHANCE);
        given(random.nextInt(100)).willReturn(50); // 50%, so no lifesteal

        // when
        boolean result = core.healPlayer(player, mock(Item.class));

        // then
        assertFalse(result);
        then(player).should(times(0)).heal(any());
    }

    @Test
    public void testHealPlayerInvalidParameters() {

        // given
        Item itemInHand = mock(Item.class);

        try {

            // when
            core.healPlayer(null, itemInHand);
        } catch (RuntimeException e) {

            // then
            assertInstanceOf(IllegalArgumentException.class, e);
        }

        // given
        Player player = mock(Player.class);
        try {

            // when
            core.healPlayer(player, null);
        } catch (RuntimeException e) {

            // then
            assertInstanceOf(IllegalArgumentException.class, e);
        }
    }

    @Test
    public void testSpawnHealingParticlesAxisZ() {

        // given
        Player player = mock(Player.class);
        player.setPosition(new Vector3(0, 0, 0));
        given(player.add(anyDouble(), anyDouble(), anyDouble())).willReturn(new Location());
        given(player.getLevel()).willReturn(mock(Level.class));

        // when
        core.spawnHealingParticles(player);

        // then
        then(player.getLevel()).should(times(20)).addParticle(any(GenericParticle.class)) /* 20 is amount of particles */;
    }

    @Test
    public void testSpawnHealingParticlesAxisX() {

        // given
        Player player = mock(Player.class);
        player.setPosition(new Vector3(0, 0, 0));
        given(player.add(anyDouble(), anyDouble(), anyDouble())).willReturn(new Location());
        given(player.getLevel()).willReturn(mock(Level.class));

        // when
        core.spawnHealingParticles(player);

        // then
        then(player.getLevel()).should(times(20)).addParticle(any(GenericParticle.class)) /* 20 is amount of particles */;
    }

    @Test
    public void testSpawnHealingParticlesInvalidParameter() {
        try {

            // when
            core.spawnHealingParticles(null);
        } catch (RuntimeException e) {

            // then
            assertInstanceOf(IllegalArgumentException.class, e);
        }
    }
}
