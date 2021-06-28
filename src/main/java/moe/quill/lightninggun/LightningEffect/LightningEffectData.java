package moe.quill.lightninggun.LightningEffect;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

public class LightningEffectData {
    private final LivingEntity entity;
    private final ArmorStand effectStand;

    private int effectSchedulerId = -1;

    /**
     * Create new lightning effect data from an entity and an effect stand (the stand used to display the lightning effect)
     *
     * @param entity      the effect is placed on
     * @param effectStand stand that is portraying the effect
     */
    public LightningEffectData(LivingEntity entity, ArmorStand effectStand) {
        this.entity = entity;
        this.effectStand = effectStand;
    }

    public ArmorStand getEffectStand() {
        return effectStand;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public int getEffectSchedulerId() {
        return effectSchedulerId;
    }

    public void setEffectSchedulerId(int effectSchedulerId) {
        this.effectSchedulerId = effectSchedulerId;
    }
}
