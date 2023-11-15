package com.kydzombie.divinelegacy.entity

import com.kydzombie.divinelegacy.item.CleansableItem
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import com.kydzombie.divinelegacy.util.asItemInstance
import net.minecraft.entity.Item
import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemBase
import net.minecraft.item.ItemInstance
import net.minecraft.level.Level

class CleansableItemEntity(level: Level, x: Double, y: Double, z: Double, item: ItemInstance, private val player: PlayerBase?): Item(level, x, y, z, item) {
    private val divineHandler = player?.getDivineStats()
    fun cleanse(): Item? {
        if (player == null || divineHandler == null || item == null || item.type == null) return null
        item.type.let { itemType ->
            when (itemType) {
                is CleansableItem -> {
                    if (divineHandler.divineLevel >= itemType.getCleansingLevelRequirement(player)) {
                        itemType.getCleansingOutput(item, player)?.let { output ->
                            divineHandler.divineLevel -= itemType.getCleansingLevelCost(player)
                            return Item(level, x, y, z, output)
                        }
                    }
                }
                else -> {
                    when (itemType.id) {
                        ItemBase.book.id -> {
                            return Item(level, x, y, z, ItemBase.redstoneDust.asItemInstance())
                        }
                        else -> Unit
                    }
                }
            }
        }
        return null
    }
}