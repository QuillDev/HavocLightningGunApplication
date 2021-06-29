package moe.quill.lightninggun.Events;

import moe.quill.lightninggun.LightningEffect.LightningEffectData;
import moe.quill.lightninggun.LightningEffect.LightningEffectManager;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;


public class LightningGunShootListener implements Listener {

    private static final int range = 15;

    private final LightningEffectManager lightningEffectManager;
    private final NamespacedKey lightningGunKey;
    private final NamespacedKey standKey;

    public LightningGunShootListener(LightningEffectManager lightningEffectManager) {
        this.lightningEffectManager = lightningEffectManager;
        this.standKey = lightningEffectManager.getStandKey();
        this.lightningGunKey = lightningEffectManager.getLightningGunKey();
    }

    /**
     * Handle interactions where the player clicks the air
     *
     * @param event player interact event
     */
    @EventHandler
    public void playerUseLightningGun(PlayerInteractEvent event) {
        final var action = event.getAction();
        //Only allow right click actions as per the item description
        if (!(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))) return;
        processUseEvent(event.getPlayer(), event);
    }

    /**
     * Handle interactions where the player clicks another entity
     *
     * @param event player interact event
     */
    @EventHandler
    public void playerUseLightningGun(PlayerInteractEntityEvent event) {
        processUseEvent(event.getPlayer(), event);
    }

    public void processUseEvent(Player player, Cancellable event) {
        final var heldItem = player.getInventory().getItemInMainHand();

        //If it isn't bricks just exit out
        if (!heldItem.getType().equals(Material.PRISMARINE_BRICKS)) return;

        //Get the data on the item
        final var data = heldItem.getItemMeta().getPersistentDataContainer();
        if (!data.has(lightningGunKey, PersistentDataType.STRING))
            return; //if it doesn't have the lightning key, exit out

        final var targetBlock = player.getTargetBlock(range);
        if (targetBlock == null) return;
        final var targetLocation = targetBlock.getLocation();

        useLightningGun(player, targetLocation, 1);
        event.setCancelled(true);
    }

    /**
     * Use the lightning gun given the player and an end location
     *
     * @param shooter that is shooting the gun
     * @param end     location to stop the beam at
     * @param delta   amount of space in between each particle/collision check
     */
    public void useLightningGun(LivingEntity shooter, Location end, int delta) {

        //Get the player location + world
        final var start = shooter.getEyeLocation();
        final var world = start.getWorld();

        /*Distance between the two particles*/
        double distance = start.distance(end);

        /* The points as vectors */
        final var positionVec = start.toVector();
        final var endVec = end.toVector();

        /* Subtract gives you a vector between the points, we multiply by the space*/
        final var vector = endVec.clone().subtract(positionVec).normalize().multiply(delta);

        /*The distance covered*/
        double covered = 0;

        /* We run this code while we haven't covered the distance, we increase the point by the space every time*/
        for (; covered < distance; positionVec.add(vector)) {
            /*Spawn the particle at the point*/
            drawLightningGunParticle(positionVec, world);
            processLightningGunCollision(positionVec, world, shooter);
            /* We add the space covered */
            covered += delta;
        }
    }

    /**
     * Process possible collisions with the lightning gun at the current location
     *
     * @param position to check collisions at
     * @param world    to check collisions in
     * @param shooter  who shot the gun so we can ignore collisions with them
     */
    public void processLightningGunCollision(Vector position, World world, LivingEntity shooter) {
        final var location = new Location(world, position.getX(), position.getY(), position.getZ());
        final var targets = location.getNearbyLivingEntities(1); //Get enemies that collide at this location

        //Iterate through all targets in the list
        for (final var target : targets) {
            if (target.equals(shooter)) continue; //you can't shoot yourself.. lol
            if (target.getPersistentDataContainer().has(standKey, PersistentDataType.STRING))
                continue; //Make it so stands can't stack lightning effects

            //Deal the damage to the entity FROM the player
            target.damage(4, shooter);
            if (target.isDead()) continue; //If the impact killed the target, don't spawn a stand

            //Check if the user is already effected by this
            LightningEffectData lightningEffectData;
            if (!lightningEffectManager.hasEntity(target)) {
                final var effectStand = (ArmorStand) world.spawnEntity(target.getLocation(), EntityType.ARMOR_STAND);
                effectStand.setInvulnerable(true);
                effectStand.setInvisible(true);
                effectStand.setPersistent(false);
                effectStand.getPersistentDataContainer().set(standKey, PersistentDataType.STRING, "");
                effectStand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.SOUL_SAND));
                target.addPassenger(effectStand); //Make the effect stand ride the target
                lightningEffectData = new LightningEffectData(target, effectStand);
            } else {
                lightningEffectData = lightningEffectManager.getEffectData(target);

                //If for some reason the data comes back null, return out
                if (lightningEffectData == null) {
                    return;
                }
            }

            lightningEffectManager.updateEntity(lightningEffectData);
        }
    }

    /**
     * Draw a particle for the lightning gun
     *
     * @param position to spawn the particle at
     * @param world    to spawn the particle in
     */
    public void drawLightningGunParticle(Vector position, World world) {
        world.spawnParticle(
                Particle.REDSTONE,
                position.getX(),
                position.getY(),
                position.getZ(),
                1,
                new Particle.DustOptions(Color.AQUA, 1)
        );
    }
}
