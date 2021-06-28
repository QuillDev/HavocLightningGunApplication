package moe.quill.lightninggun;

import moe.quill.lightninggun.Commands.LightningGunCommand;
import moe.quill.lightninggun.Events.LightningEffectRemovalListener;
import moe.quill.lightninggun.LightningEffect.LightningEffectManager;
import moe.quill.lightninggun.Events.LightningGunShootListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class LightningGun extends JavaPlugin {

    // Plugin startup logic
    final LightningEffectManager lightningEffectManager = new LightningEffectManager(this);

    @Override
    public void onEnable() {


        final var lightningGunCommand = getCommand("lightninggun");
        if (lightningGunCommand == null) {
            System.out.println("The lightning gun command was not registered so an executor cannot be set!");
        } else {
            lightningGunCommand.setExecutor(new LightningGunCommand(lightningEffectManager));
        }


        final var pluginManger = getServer().getPluginManager();
        pluginManger.registerEvents(new LightningGunShootListener(lightningEffectManager), this);
        pluginManger.registerEvents(new LightningEffectRemovalListener(lightningEffectManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        lightningEffectManager.disable();
    }
}
