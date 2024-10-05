package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.GenericParticle;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LifestealCoreTest {

    @Spy
    private LifestealCore core;

    @Test
    public void testGetInstance() {
        LifestealCore core = LifestealCore.getInstance();
        assertNotNull(core);
    }

    @Test
    public void testHealPlayer() {

        // given
        Player player = mock(Player.class);
        given(player.isOnline()).willReturn(true);
        given(player.isAlive()).willReturn(true);
        given(player.hasPermission(Permissions.LIFESTEAL_ABILITY_PERMISSION.getPermission())).willReturn(true);

        int dealtDamage = 10;
        WeaponType weapon = WeaponType.DIAMOND_AXE; // 15% of lifesteal
        Item itemInHand = mock(Item.class);
        given(itemInHand.getId()).willReturn(weapon.getItemId());
        given(itemInHand.getAttackDamage()).willReturn(dealtDamage);

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
