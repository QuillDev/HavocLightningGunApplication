package moe.quill.lightninggun.Events;

import moe.quill.lightninggun.LightningEffect.LightningEffectManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record LightningEffectRemovalListener(
        LightningEffectManager lightningEffectManager) implements Listener {

    /**
     * Remove the entity from the lightning effect manager when they die
     *
     * @param event for detecting entity deaths
     */
    @EventHandler
    public void lightningEffectEntityDeathEvent(EntityDeathEvent event) {
        final var entity = event.getEntity();
        if (!lightningEffectManager.hasEntity(entity)) return;
        lightningEffectManager.removeEntity(entity);
    }

    /**
     * If the player effected by lightning quits, remove the effect
     *
     * @param event to process quitting etc from
     */
    @EventHandler
    public void lightningEntityQuitEvent(PlayerQuitEvent event) {
        final var entity = event.getPlayer();
        if (!lightningEffectManager.hasEntity(entity)) return;
        lightningEffectManager.removeEntity(entity);
    }

    /**
     * remove the entity from the lightning manager if they teleport
     *
     * @param event for when an enemy teleports
     */
    @EventHandler
    public void lightningEffectEntityTeleportEvent(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!lightningEffectManager.hasEntity(entity)) return;
        lightningEffectManager.removeEntity(entity);
    }
}
