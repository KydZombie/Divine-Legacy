package com.kydzombie.divinelegacy.block

import com.kydzombie.divinelegacy.entity.CleansableItemEntity
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import com.kydzombie.divinelegacy.registry.CleansingRecipe
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistry
import com.kydzombie.divinelegacy.util.sendChatMessage
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.Box
import net.minecraft.world.World
import net.modificationstation.stationapi.api.template.block.TemplateFlowingLiquidBlock
import net.modificationstation.stationapi.api.template.block.TemplateStillLiquidBlock
import net.modificationstation.stationapi.api.util.Identifier
import java.util.*

const val USABLE_UNTIL_LEVEL = 10

class CleansingWaterFlowing(identifier: Identifier) : TemplateFlowingLiquidBlock(identifier, Material.WATER)

class CleansingWaterStill(identifier: Identifier) : TemplateStillLiquidBlock(identifier, Material.WATER) {
    override fun onEntityCollision(world: World, x: Int, y: Int, z: Int, entity: Entity) {
        if (world.getBlockMeta(x, y, z) != 0) {
            super.onEntityCollision(world, x, y, z, entity)
            return
        }
        when (entity) {
            is PlayerEntity -> {
                val handler = entity.getDivineStats()
                if (handler.divineLevel >= USABLE_UNTIL_LEVEL) return
                handler.divineLevel++
                if (handler.divineLevel == 1) {
                    sendChatMessage(entity, "You feel blessed by the gods.")
                } else {
                    sendChatMessage(entity, "You feel more holy. Divine level: ${handler.divineLevel}")
                }
                world.setBlock(x, y, z, Block.WATER.id)
                return
            }

            is CleansableItemEntity -> {
                fun getNearbyItems(): HashMap<PlayerEntity?, Vector<CleansableItemEntity>> {
                    @Suppress("UNCHECKED_CAST")
                    val uncheckedItems = (world.method_175(
                        CleansableItemEntity::class.java,
                        Box.create(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 1.0, z + 1.0)
                    ) as List<CleansableItemEntity>).toMutableList()

                    return HashMap<PlayerEntity?, Vector<CleansableItemEntity>>().apply {
                        while (uncheckedItems.isNotEmpty()) {
                            val items = Vector<CleansableItemEntity>()
                            val player = uncheckedItems[0].player
                            uncheckedItems.removeIf { itemEntity ->
                                if (itemEntity.player == player) {
                                    items.add(itemEntity)
                                    true
                                } else false
                            }
                            set(player, items)
                        }
                    }
                }

                fun HashMap<PlayerEntity?, Vector<CleansableItemEntity>>.getRecipes(): HashMap<PlayerEntity?, List<CleansingRecipe>> {
                    return HashMap<PlayerEntity?, List<CleansingRecipe>>().apply {
                        this@getRecipes.forEach { (player, items) ->
                            set(player, CleansingRecipeRegistry.getValidRecipes(items.map { it.stack }, player))
                        }
                    }
                }

                fun HashMap<PlayerEntity?, List<CleansingRecipe>>.sortByImportance(): List<Pair<PlayerEntity?, CleansingRecipe>> {
                    if (this.isEmpty()) return emptyList()
                    val recipes = mutableListOf<Pair<PlayerEntity?, CleansingRecipe>>()
                    this.forEach { (player, playerRecipes) ->
                        recipes.addAll(playerRecipes.map { Pair(player, it) })
                    }
                    return recipes.sortedWith(compareBy { (_, recipe) -> recipe.priority })
                }

                val playerItems = getNearbyItems()

                val recipes = playerItems.getRecipes().sortByImportance()

                recipes.forEach { (player, recipe) ->
                    if (recipe.takeCosts(world, x, y, z, player, playerItems[player]!!)) return@onEntityCollision
                }
            }
        }
        super.onEntityCollision(world, x, y, z, entity)
    }
}