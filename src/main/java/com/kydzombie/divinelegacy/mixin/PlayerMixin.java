package com.kydzombie.divinelegacy.mixin;

import com.kydzombie.divinelegacy.entity.CleansableItemEntity;
import com.kydzombie.divinelegacy.item.CleansableItem;
import com.kydzombie.divinelegacy.player.DivinePlayerHandler;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.level.Level;
import net.modificationstation.stationapi.impl.entity.player.PlayerAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerBase.class)
public abstract class PlayerMixin extends EntityBase {
    public PlayerMixin(Level arg) {
        super(arg);
    }

    @Redirect(
            method = "dropItem(Lnet/minecraft/item/ItemInstance;Z)V",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/level/Level;DDDLnet/minecraft/item/ItemInstance;)Lnet/minecraft/entity/Item;"
            )
    )
    private Item replaceWithCleansableItem(Level level, double x, double y, double z, ItemInstance itemInstance) {
        var divineLevel = ((DivinePlayerHandler) PlayerAPI.getPlayerHandler((PlayerBase) (Object) this, DivinePlayerHandler.class)).getDivineLevel();
        if (divineLevel > 0 && itemInstance != null && (itemInstance.getType() instanceof CleansableItem || itemInstance.itemId == ItemBase.book.id)) {
            return new CleansableItemEntity(level, x, y, z, itemInstance, (PlayerBase) (Object) this);
        } else {
            return new Item(level, x, y, z, itemInstance);
        }
    }
}
