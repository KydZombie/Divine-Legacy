package com.kydzombie.divinelegacy.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.modificationstation.stationapi.api.entity.player.PlayerHandler
import net.modificationstation.stationapi.impl.entity.player.PlayerAPI
import kotlin.math.min

const val MAX_ENERGY = 100

class DivinePlayerHandler(private val player: PlayerEntity) : PlayerHandler {
    // TODO: Per-god divine level
    var divineLevel = 0
    var energy = 0

    override fun readEntityBaseFromNBT(nbt: NbtCompound): Boolean {
        divineLevel = nbt.getInt("divine-legacy:divine_level")
        energy = nbt.getInt("divine-legacy:energy")
        return false
    }

    override fun writeEntityBaseToNBT(nbt: NbtCompound): Boolean {
        nbt.putInt("divine-legacy:divine_level", divineLevel)
        nbt.putInt("divine-legacy:energy", energy)
        return false
    }

    override fun onLivingUpdate(): Boolean {
        energy = min(energy + 1, MAX_ENERGY)
        return false
    }

    companion object {
        fun PlayerEntity.getDivineStats(): DivinePlayerHandler {
            return PlayerAPI.getPlayerHandler(this, DivinePlayerHandler::class.java) as DivinePlayerHandler
        }
    }
}
