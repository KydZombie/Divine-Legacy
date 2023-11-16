package com.kydzombie.divinelegacy.block

import com.kydzombie.divinelegacy.entity.CleansableItemEntity
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistry
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistry.CleansingRecipe
import com.kydzombie.divinelegacy.util.sendChatMessage
import net.minecraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityBase
import net.minecraft.entity.player.PlayerBase
import net.minecraft.level.Level
import net.minecraft.util.maths.Box
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.block.TemplateFlowingFluid
import net.modificationstation.stationapi.api.template.block.TemplateStillFluid
import java.util.*

const val USABLE_UNTIL_LEVEL = 10

class CleansingWaterFlowing(identifier: Identifier): TemplateFlowingFluid(identifier, Material.WATER)

class CleansingWaterStill(identifier: Identifier): TemplateStillFluid(identifier, Material.WATER) {
    override fun onEntityCollision(level: Level, x: Int, y: Int, z: Int, entity: EntityBase) {
        if (level.getTileMeta(x, y, z) != 0) {
            super.onEntityCollision(level, x, y, z, entity)
            return
        }
        when (entity) {
            is PlayerBase -> {
                val handler = entity.getDivineStats()
                if (handler.divineLevel >= USABLE_UNTIL_LEVEL) return
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
                fun getNearbyItems(): HashMap<PlayerBase?, Vector<CleansableItemEntity>> {
                    val uncheckedItems = (level.getEntities(
                        CleansableItemEntity::class.java,
                        Box.create(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 1.0, z + 1.0)
                    ) as List<CleansableItemEntity>).toMutableList()

                    return HashMap<PlayerBase?, Vector<CleansableItemEntity>>().apply {
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

                fun HashMap<PlayerBase?, Vector<CleansableItemEntity>>.getRecipes(): HashMap<PlayerBase?, List<CleansingRecipe>> {
                    return HashMap<PlayerBase?, List<CleansingRecipe>>().apply {
                        this@getRecipes.forEach { (player, items) ->
                            set(player, CleansingRecipeRegistry.getValidRecipes(items.map { it.item }, player))
                        }
                    }
                }

                fun HashMap<PlayerBase?, List<CleansingRecipe>>.sortByImportance(): List<Pair<PlayerBase?, CleansingRecipe>> {
                    if (this.isEmpty()) return emptyList()
                    val recipes = mutableListOf<Pair<PlayerBase?, CleansingRecipe>>()
                    this.forEach { (player, playerRecipes) ->
                        recipes.addAll(playerRecipes.map { Pair(player, it) })
                    }
                    return recipes.sortedWith(compareBy { (_, recipe) -> recipe.priority })
                }

                val playerItems = getNearbyItems()

                val recipes = playerItems.getRecipes().sortByImportance()

                recipes.forEach {(player, recipe) ->
                    if (recipe.takeCosts(level, x, y, z, player, playerItems[player]!!)) return@onEntityCollision
                }
            }
        }
        super.onEntityCollision(level, x, y, z, entity)
    }
}