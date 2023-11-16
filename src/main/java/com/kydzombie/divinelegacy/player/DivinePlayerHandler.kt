package com.kydzombie.divinelegacy.player

import net.minecraft.entity.player.PlayerBase
import net.minecraft.util.io.CompoundTag
import net.modificationstation.stationapi.api.entity.player.PlayerHandler
import net.modificationstation.stationapi.impl.entity.player.PlayerAPI
import kotlin.math.min

const val MAX_ENERGY = 100

class DivinePlayerHandler(private val player: PlayerBase) : PlayerHandler {
    var divineLevel = 0
    var energy = 0

    override fun readEntityBaseFromNBT(tag: CompoundTag): Boolean {
        divineLevel = tag.getInt("divine-legacy:divine_level")
        energy = tag.getInt("divine-legacy:energy")
        return false
    }

    override fun writeEntityBaseToNBT(tag: CompoundTag): Boolean {
        tag.put("divine-legacy:divine_level", divineLevel)
        tag.put("divine-legacy:energy", energy)
        return false
    }

    override fun onLivingUpdate(): Boolean {
        energy = min(energy + 1, MAX_ENERGY)
        return false
    }

    companion object {
        fun PlayerBase.getDivineStats(): DivinePlayerHandler {
            return PlayerAPI.getPlayerHandler(this, DivinePlayerHandler::class.java) as DivinePlayerHandler
        }
    }
}
