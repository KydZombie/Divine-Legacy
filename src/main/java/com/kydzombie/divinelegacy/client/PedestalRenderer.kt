package com.kydzombie.divinelegacy.client

import com.kydzombie.divinelegacy.block.PedestalEntity
import com.kydzombie.divinelegacy.util.render
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.render.block.entity.BlockEntityRenderer

object PedestalRenderer : BlockEntityRenderer() {
    private const val SPIN_SPEED_MULTIPLIER = 3 // Inversely related
    override fun render(blockEntity: BlockEntity, x: Double, y: Double, z: Double, g: Float) {
        if (blockEntity !is PedestalEntity) return
        blockEntity.stack?.render(
            blockEntity.world,
            x + 0.5,
            y + 1.5,
            z + 0.5,
            rotation = System.currentTimeMillis() % (360 * SPIN_SPEED_MULTIPLIER) / SPIN_SPEED_MULTIPLIER.toFloat()
        )
    }
}