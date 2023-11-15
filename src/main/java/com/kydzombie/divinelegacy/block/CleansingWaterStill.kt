package com.kydzombie.divinelegacy.block

import com.kydzombie.divinelegacy.entity.CleansableItemEntity
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import com.kydzombie.divinelegacy.util.sendChatMessage
import net.minecraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityBase
import net.minecraft.entity.player.PlayerBase
import net.minecraft.level.Level
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.block.TemplateStillFluid

class CleansingWaterStill(identifier: Identifier): TemplateStillFluid(identifier, Material.WATER) {
    override fun onEntityCollision(level: Level, x: Int, y: Int, z: Int, entity: EntityBase) {
        if (level.getTileMeta(x, y, z) != 0) {
            super.onEntityCollision(level, x, y, z, entity)
            return
        }
        when (entity) {
            is PlayerBase -> {
                val handler = entity.getDivineStats()
                handler.divineLevel++
                if (handler.divineLevel == 1) {
                    sendChatMessage(entity, "You feel blessed by the gods.")
                } else {
                    sendChatMessage(entity, "You feel more holy. Divine level: ${handler.divineLevel}")
                }
                level.setTile(x, y, z, BlockBase.STILL_WATER.id)
                return
            }
            is CleansableItemEntity -> {
                val item = entity.cleanse()
                if (item == null) {
                    super.onEntityCollision(level, x, y, z, entity)
                    return
                }
                entity.item.count--
                if (entity.item.count <= 0) {
                    level.removeEntity(entity)
                }
                level.spawnEntity(item)
                level.setTile(x, y, z, BlockBase.STILL_WATER.id)
            }
        }
        super.onEntityCollision(level, x, y, z, entity)
    }
}