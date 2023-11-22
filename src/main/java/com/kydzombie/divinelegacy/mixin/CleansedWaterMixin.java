package com.kydzombie.divinelegacy.mixin;

import com.kydzombie.divinelegacy.DivineLegacy;
import net.minecraft.block.Block;
import net.minecraft.block.LiquidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.StillLiquidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StillLiquidBlock.class)
public abstract class CleansedWaterMixin extends LiquidBlock {

    public CleansedWaterMixin(int i, Material arg) {
        super(i, arg);
    }

    @Override
    public void onEntityCollision(World world, int x, int y, int z, Entity entity) {
        if (id == Block.WATER.id && world.getBlockId(x, y + 1, z) == 0 && entity instanceof ItemEntity itemEntity && itemEntity.stack != null && itemEntity.stack.itemId == DivineLegacy.cleansingDust.id && itemEntity.stack.count > 0) {
            itemEntity.stack.count--;
            if (itemEntity.stack.count <= 0) {
                world.method_231(itemEntity);
            }
            world.setBlock(x, y, z, DivineLegacy.cleansingWaterStill.id);
        } else {
            super.onEntityCollision(world, x, y, z, entity);
        }
    }
}
