package com.kydzombie.divinelegacy.entity

import net.minecraft.entity.Item
import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemInstance
import net.minecraft.level.Level

class CleansableItemEntity(level: Level, x: Double, y: Double, z: Double, item: ItemInstance, val player: PlayerBase?) :
    Item(level, x, y, z, item)
