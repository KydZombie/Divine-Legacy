package com.kydzombie.divinelegacy.mixin;

import com.kydzombie.divinelegacy.entity.CleansableItemEntity;
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin {

    @Redirect(
            method = "method_509(Lnet/minecraft/item/ItemStack;Z)V",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"
            )
    )
    private ItemEntity replaceWithCleansableItem(World world, double x, double y, double z, ItemStack stack) {
        if (CleansingRecipeRegistry.INSTANCE.isCleansable(stack)) {
            return new CleansableItemEntity(world, x, y, z, stack, (PlayerEntity) (Object) this);
        } else {
            return new ItemEntity(world, x, y, z, stack);
        }
    }
}
