package com.kydzombie.divinelegacy.registry

import com.kydzombie.divinelegacy.entity.CleansableItemEntity
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import net.minecraft.entity.Item
import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemInstance
import net.minecraft.level.Level
import net.modificationstation.stationapi.api.template.block.TemplateStillFluid
import kotlin.math.min

const val CLEAR_WATER_OFFSET = 1024

// TODO: Cleansing timer
class CleansingRecipe(
    val input: Array<ItemInstance>,
    val output: Array<ItemInstance>,
    private val clearWater: Boolean = false,
    private val levelCost: Int = 0,
    private val levelRequirement: Int = levelCost,
    private val energyCost: Int = 0,
    private val requiresPlayer: Boolean = levelCost == 0 && levelRequirement == 0 && energyCost == 0,
    val priority: Int = (if (clearWater) CLEAR_WATER_OFFSET else 0) + input.size
) {
    init {
        CleansingRecipeRegistry.cleansableItems = CleansingRecipeRegistry.cleansableItems.plus(
            // Check if the existing cleansable items contains the same item
            input.filter { inputItem ->
                CleansingRecipeRegistry.cleansableItems.none { existingItem ->
                    existingItem.isDamageAndIDIdentical(inputItem)
                }
            }
        )
        CleansingRecipeRegistry.recipes = CleansingRecipeRegistry.recipes.plus(this)
    }

    fun meetsRequirements(player: PlayerBase?): Boolean {
        if (player == null) return requiresPlayer
        val handler = player.getDivineStats()
        return handler.divineLevel >= levelRequirement && handler.divineLevel >= levelCost && handler.energy >= energyCost
    }

    fun takeCosts(
        level: Level,
        x: Int,
        y: Int,
        z: Int,
        player: PlayerBase?,
        items: List<CleansableItemEntity>
    ): Boolean {
        input.forEach { recipeItem ->
            val matchingItems = items.filter { recipeItem.isDamageAndIDIdentical(it.item) }
            var remainingToCollect = recipeItem.count
            matchingItems.forEach itemMatch@{ itemEntity ->
                if (remainingToCollect <= 0) return@itemMatch
                val toTake = min(remainingToCollect, itemEntity.item.count)
                remainingToCollect -= toTake
                itemEntity.item.count -= toTake
                if (itemEntity.item.count <= 0) {
                    level.removeEntity(itemEntity)
                }
            }
        }
        output.forEach { recipeItem ->
            val spawnedItem = Item(level, x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5, recipeItem.copy())
            spawnedItem.velocityY = 0.2
            level.spawnEntity(spawnedItem)
        }

        if (player != null) {
            val handler = player.getDivineStats()
            handler.divineLevel -= levelCost
            handler.energy -= energyCost
        }

        return if (clearWater) {
            level.setTile(x, y, z, TemplateStillFluid.STILL_WATER.id)
            true
        } else false
    }
}