package com.kydzombie.divinelegacy.registry

import com.kydzombie.divinelegacy.entity.CleansableItemEntity
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import net.minecraft.block.Block
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import kotlin.math.min

const val CLEAR_WATER_OFFSET = 1024

// TODO: Cleansing timer
class CleansingRecipe(
    val input: Array<ItemStack>,
    val output: Array<ItemStack>,
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
                    existingItem.isItemEqual(inputItem)
                }
            }
        )
        CleansingRecipeRegistry.recipes = CleansingRecipeRegistry.recipes.plus(this)
    }

    fun meetsRequirements(player: PlayerEntity?): Boolean {
        if (player == null) return requiresPlayer
        val handler = player.getDivineStats()
        return handler.divineLevel >= levelRequirement && handler.divineLevel >= levelCost && handler.energy >= energyCost
    }

    fun takeCosts(
        world: World,
        x: Int,
        y: Int,
        z: Int,
        player: PlayerEntity?,
        items: List<CleansableItemEntity>
    ): Boolean {
        input.forEach { recipeItem ->
            val matchingItems = items.filter { recipeItem.isItemEqual(it.stack) }
            var remainingToCollect = recipeItem.count
            matchingItems.forEach itemMatch@{ itemEntity ->
                if (remainingToCollect <= 0) return@itemMatch
                val toTake = min(remainingToCollect, itemEntity.stack.count)
                remainingToCollect -= toTake
                itemEntity.stack.count -= toTake
                if (itemEntity.stack.count <= 0) {
                    world.method_231(itemEntity)
                }
            }
        }
        output.forEach { recipeItem ->
            val spawnedItem =
                ItemEntity(world, x.toDouble() + 0.5, y.toDouble() + 0.5, z.toDouble() + 0.5, recipeItem.copy())
            spawnedItem.velocityY = 0.2
            world.method_210(spawnedItem)
        }

        if (player != null) {
            val handler = player.getDivineStats()
            handler.divineLevel -= levelCost
            handler.energy -= energyCost
        }

        return if (clearWater) {
            world.setBlock(x, y, z, Block.WATER.id)
            true
        } else false
    }
}