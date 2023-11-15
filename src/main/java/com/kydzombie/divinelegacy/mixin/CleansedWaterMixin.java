package com.kydzombie.divinelegacy.mixin;

import com.kydzombie.divinelegacy.DivineLegacy;
import net.minecraft.block.BlockBase;
import net.minecraft.block.Fluid;
import net.minecraft.block.StillFluid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityBase;
import net.minecraft.entity.Item;
import net.minecraft.level.Level;
import net.minecraft.util.maths.Vec3f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StillFluid.class)
public abstract class CleansedWaterMixin extends Fluid {

    public CleansedWaterMixin(int i, Material arg) {
        super(i, arg);
    }

    @Override
    public void onCollideWithEntity(Level level, int x, int y, int z, EntityBase entity, Vec3f arg3) {
        if (id == BlockBase.STILL_WATER.id && entity instanceof Item itemEntity && itemEntity.item != null && itemEntity.item.itemId == DivineLegacy.cleansingDust.id) {
            itemEntity.item.count--;
            if (itemEntity.item.count <= 0) {
                level.removeEntity(itemEntity);
            }
            level.setTile(x, y, z, DivineLegacy.cleansingWaterStill.id);
        } else {
            super.onCollideWithEntity(level, x, y, z, entity, arg3);
        }
    }
}
