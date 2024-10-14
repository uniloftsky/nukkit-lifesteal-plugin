package net.uniloftsky.nukkit.lifesteal.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginLogger;
import net.uniloftsky.nukkit.lifesteal.LifestealCore;
import net.uniloftsky.nukkit.lifesteal.LifestealPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventListenerTest {

    @Mock
    private LifestealCore core;

    @Mock
    private static PluginLogger logger;

    @InjectMocks
    private EventListener listener = new EventListener(logger, core);

    @BeforeAll
    static void beforeAll() {
        LifestealPlugin mockedPlugin = mock(LifestealPlugin.class);
        given(mockedPlugin.getLogger()).willReturn(logger);
    }

    @Test
    public void testOnAttack() throws ReflectiveOperationException {

        // given
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);

        Player mockedPlayer = mock(Player.class);
        given(event.getDamager()).willReturn(mockedPlayer);
        setIsPlayerField(mockedPlayer); // set isPlayer to true

        PlayerInventory mockedInventory = mock(PlayerInventory.class);
        given(mockedPlayer.getInventory()).willReturn(mockedInventory);

        Item mockedItem = mock(Item.class);
        given(mockedInventory.getItemInHand()).willReturn(mockedItem);

        // when
        listener.onAttack(event);

        // then
        then(core).should().healPlayer(mockedPlayer, mockedItem);
    }

    @Test
    public void testOnAttackNotPlayer() {

        // given
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);

        Entity mockedEntity = mock(Entity.class);
        given(event.getDamager()).willReturn(mockedEntity);

        // when
        listener.onAttack(event);

        // then
        then(core).should(times(0)).healPlayer(any(Player.class), any(Item.class));
    }

    @Test
    public void testOnAttackIllegalArgument() throws ReflectiveOperationException {

        // given
        EntityDamageByEntityEvent event = mock(EntityDamageByEntityEvent.class);

        Player mockedPlayer = mock(Player.class);
        given(event.getDamager()).willReturn(mockedPlayer);
        setIsPlayerField(mockedPlayer); // set isPlayer to true

        PlayerInventory mockedInventory = mock(PlayerInventory.class);
        given(mockedPlayer.getInventory()).willReturn(mockedInventory);

        try {
            doThrow(new IllegalArgumentException()).when(core).healPlayer(mockedPlayer, null);
        } catch (Exception ignored) {
        }

        // when
        listener.onAttack(event);

        // then
        then(logger).should().error(eq("Some exception occurred when tried to heal the player"), argThat(e -> e instanceof IllegalArgumentException));
        then(core).should(times(0)).healPlayer(any(Player.class), any(Item.class));
    }

    private void setIsPlayerField(Entity player) throws ReflectiveOperationException {
        Field isPlayerField = Entity.class.getDeclaredField("isPlayer");
        isPlayerField.setAccessible(true);
        isPlayerField.set(player, true);
    }
}
