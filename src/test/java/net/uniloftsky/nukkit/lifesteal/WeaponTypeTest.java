package net.uniloftsky.nukkit.lifesteal;

import cn.nukkit.item.Item;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WeaponTypeTest {

    @Test
    public void testFindWeaponById() {

        // given
        WeaponType expectedWeapon = WeaponType.DIAMOND_AXE;
        int weaponId = Item.DIAMOND_AXE;

        // when
        Optional<WeaponType> optionalResult = WeaponType.findWeaponById(weaponId);

        // then
        assertTrue(optionalResult.isPresent());

        WeaponType result = optionalResult.get();
        assertEquals(expectedWeapon, result);
    }

    @Test
    public void testFindWeaponByIdEmpty() {

        // given
        int weaponId = Item.GRASS; // not-weapon item

        // when
        Optional<WeaponType> optionalResult = WeaponType.findWeaponById(weaponId);

        // then
        assertTrue(optionalResult.isEmpty());
    }

}
