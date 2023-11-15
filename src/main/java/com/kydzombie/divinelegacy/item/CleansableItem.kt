package com.kydzombie.divinelegacy.item

import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemInstance
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.item.TemplateItemBase

abstract class CleansableItem(identifier: Identifier): TemplateItemBase(identifier) {
    abstract fun getCleansingOutput(itemInstance: ItemInstance, player: PlayerBase): ItemInstance?
    open fun getCleansingLevelRequirement(player: PlayerBase): Int = 0
    open fun getCleansingLevelCost(player: PlayerBase): Int = 0
}