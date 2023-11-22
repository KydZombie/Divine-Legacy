package com.kydzombie.divinelegacy.registry

import net.mine_diver.unsafeevents.Event
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object CleansingRecipeRegistry {
    internal var recipes: Array<CleansingRecipe> = emptyArray()
    internal var cleansableItems: Array<ItemStack> = emptyArray()

    fun getValidRecipes(items: List<ItemStack>, player: PlayerEntity? = null): List<CleansingRecipe> {
        // TODO: Respect multiple smaller stacks
        return recipes.filter { recipe -> // Check each recipe
            return@filter if (recipe.meetsRequirements(player)) {
                recipe.input.all { requiredItem -> // If all item requirements are satisfied
                    items.any { item ->
                        item.isItemEqual(requiredItem)
                    }
                }
            } else false
        }
    }

    fun ItemStack?.isCleansable(): Boolean = if (this == null) false else
        cleansableItems.any { isItemEqual(it) }
}

class CleansingRecipeRegistryEvent : Event()
