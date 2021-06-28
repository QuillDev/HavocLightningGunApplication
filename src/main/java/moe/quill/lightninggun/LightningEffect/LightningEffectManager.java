package moe.quill.lightninggun.LightningEffect;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class LightningEffectManager {

    private final HashMap<LivingEntity, LightningEffectData> effectedDataMap = new HashMap<>();

    private final Plugin plugin;
    private final NamespacedKey lightningGunKey;
    private final NamespacedKey standKey;

    /**
     * THe manager for managing the state of all players with lightning effects on them
     *
     * @param plugin to use for event scheduling etc
     */
    public LightningEffectManager(Plugin plugin) {
        this.plugin = plugin;

        //Keys to use for differentiating these items from their vanilla counter parts
        this.lightningGunKey = new NamespacedKey(plugin, "lightninggun");
        this.standKey = new NamespacedKey(plugin, "standKey");
    }

    /**
     * Update the entities lightning effect state by either refreshing it or adding them to the manager
     *
     * @param data lightning effect data for the given user
     */
    public void updateEntity(LightningEffectData data) {

        //Check whether we already have that data, if we don't then add it to the effect map
        if (!effectedDataMap.containsKey(data.getEntity())) {
            effectedDataMap.put(data.getEntity(), data);
        }

        //Get teh current scheduled id for that data
        final var effectSchedulerId = data.getEffectSchedulerId();

        //If an event is already scheduled, go ahead and cancel it
        if (effectSchedulerId != -1) {
            Bukkit.getScheduler().cancelTask(effectSchedulerId);
        }

        //Refresh the event
        final var newEffectId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            removeEntity(data.getEntity());
            System.out.println("RAN");
        }, 100L);

        data.setEffectSchedulerId(newEffectId);
    }

    /**
     * Get the effect data from the data map for the given entity
     *
     * @param entity to get data for
     * @return the entity data, or null if there is no data present
     */
    public LightningEffectData getEffectData(LivingEntity entity) {
        return effectedDataMap.getOrDefault(entity, null);
    }

    /**
     * Check whether the entity is present in the effected data map
     *
     * @param entity to check the presence of
     * @return whether the entity is in the map or not
     */
    public boolean hasEntity(LivingEntity entity) {
        return effectedDataMap.containsKey(entity);
    }

    /**
     * Remove an entity from the lightning effect manager
     *
     * @param entity to remove from the effect manager
     */
    public void removeEntity(LivingEntity entity) {
        if (!effectedDataMap.containsKey(entity)) return;
        final var entityData = effectedDataMap.get(entity);
        final var stand = entityData.getEffectStand();
        entity.removePassenger(stand);
        entityData.getEffectStand().remove();
        effectedDataMap.remove(entity);

    }

    /**
     * On disable remove all armor stands and clear the data map
     */
    public void disable() {
        for (final var key : effectedDataMap.keySet()) {
            removeEntity(key);
        }
    }

    /**
     * Get the key for checking whether an item is a lightning gun
     *
     * @return the lightning gun key
     */
    public NamespacedKey getLightningGunKey() {
        return lightningGunKey;
    }

    /**
     * Get the key for checking whether an item is an effect stand or not
     *
     * @return the stand key
     */
    public NamespacedKey getStandKey() {
        return standKey;
    }
}
