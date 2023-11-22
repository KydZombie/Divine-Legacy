package com.kydzombie.divinelegacy.entity

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class CleansableItemEntity(world: World, x: Double, y: Double, z: Double, stack: ItemStack, val player: PlayerEntity?) :
    ItemEntity(world, x, y, z, stack)
