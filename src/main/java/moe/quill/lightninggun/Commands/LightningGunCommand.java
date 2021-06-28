package moe.quill.lightninggun.Commands;

import moe.quill.lightninggun.LightningEffect.LightningEffectManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class LightningGunCommand implements CommandExecutor {

    private final ItemStack lightningGun = new ItemStack(Material.PRISMARINE_BRICKS);

    public LightningGunCommand(LightningEffectManager lightningEffectManager) {
        final var lightningGunKey = lightningEffectManager.getLightningGunKey();

        //Start creating the lightning gun
        final var meta = lightningGun.getItemMeta();

        //Set the display name of the gun
        //Name component
        Component nameComponent = Component.text("Lightning Gun").color(TextColor.color(Color.AQUA.asRGB()));
        meta.displayName(nameComponent);

        //Lore Components
        final Component descriptionLoreComponent = Component.text("Shoots electric bolts which shock enemies.").color(TextColor.color(Color.WHITE.asRGB()));
        final Component rightClickComponent = Component.text("Right-Click").color(TextColor.color(Color.YELLOW.asRGB()));
        final Component usageComponent = Component.text("to fire").color(TextColor.color(Color.GRAY.asRGB()));

        //Create the lore
        final var lightningGunLore = new ArrayList<>(Arrays.asList(
                Component.empty(),
                descriptionLoreComponent,
                Component.empty(),
                rightClickComponent.append(Component.space().append(usageComponent))
        ));

        //Apply the lightning gun key
        final var dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(lightningGunKey, PersistentDataType.STRING, "");
        meta.lore(lightningGunLore);

        //set the meta of the gun
        lightningGun.setItemMeta(meta);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        player.getInventory().addItem(lightningGun);
        player.sendMessage("You received a lightning gun!");
        return true;
    }
}
