package com.kydzombie.divinelegacy.player

import net.minecraft.entity.player.PlayerBase
import net.minecraft.util.io.CompoundTag
import net.modificationstation.stationapi.api.entity.player.PlayerHandler
import net.modificationstation.stationapi.impl.entity.player.PlayerAPI

class DivinePlayerHandler(private val player: PlayerBase): PlayerHandler {
    var divineLevel = 0

    override fun readEntityBaseFromNBT(tag: CompoundTag): Boolean {
        divineLevel = tag.getInt("divine-legacy:divineLevel")
        return false
    }

    override fun writeEntityBaseToNBT(tag: CompoundTag): Boolean {
        tag.put("divine-legacy:divineLevel", divineLevel)
        return false
    }

    companion object {
        fun PlayerBase.getDivineStats(): DivinePlayerHandler {
            return PlayerAPI.getPlayerHandler(this, DivinePlayerHandler::class.java) as DivinePlayerHandler
        }
    }
}
