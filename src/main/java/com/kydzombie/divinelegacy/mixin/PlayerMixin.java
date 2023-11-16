package com.kydzombie.divinelegacy.mixin;

import com.kydzombie.divinelegacy.entity.CleansableItemEntity;
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistry;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerBase.class)
public abstract class PlayerMixin {

    @Redirect(
            method = "dropItem(Lnet/minecraft/item/ItemInstance;Z)V",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/level/Level;DDDLnet/minecraft/item/ItemInstance;)Lnet/minecraft/entity/Item;"
            )
    )
    private Item replaceWithCleansableItem(Level level, double x, double y, double z, ItemInstance itemInstance) {
        if (CleansingRecipeRegistry.INSTANCE.isCleansable(itemInstance)) {
            return new CleansableItemEntity(level, x, y, z, itemInstance, (PlayerBase) (Object) this);
        } else {
            return new Item(level, x, y, z, itemInstance);
        }
    }
}
