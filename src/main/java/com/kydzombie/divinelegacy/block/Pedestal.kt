package com.kydzombie.divinelegacy.block

import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World
import net.modificationstation.stationapi.api.template.block.TemplateBlockWithEntity
import net.modificationstation.stationapi.api.util.Identifier

class Pedestal(identifier: Identifier, material: Material) : TemplateBlockWithEntity(identifier, material) {
    init {
        setTranslationKey(identifier)
        setHardness(4f)
    }

    override fun createBlockEntity(): BlockEntity = PedestalEntity()

    override fun onUse(world: World?, x: Int, y: Int, z: Int, player: PlayerEntity?): Boolean {
        if (world == null || player == null) return false
        @Suppress("SENSELESS_COMPARISON")
        assert(world != null && player != null) // So Kotlin stops complaining about var changing

        //            getBlockEntity()
        return (world.method_1777(x, y, z) as PedestalEntity).swapItem(player)
    }
}

class PedestalEntity : BlockEntity() {
    var stack: ItemStack? = null
        private set

    fun swapItem(player: PlayerEntity): Boolean {
        if (player.hand == null && stack != null) {
            player.inventory.setStack(player.inventory.selectedSlot, stack)
            stack = null
        } else if (player.hand != null && stack == null) {
            stack = player.hand
            player.inventory.setStack(player.inventory.selectedSlot, null)
        } else {
            return false
        }
        markDirty()
        return true
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains("Item")) {
            stack = ItemStack(nbt.getCompound("Item"))
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        if (stack != null) {
            val stackTag = NbtCompound()
            stack!!.writeNbt(stackTag)
            nbt.put("Item", stackTag)
        }
    }
}