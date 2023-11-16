package com.kydzombie.divinelegacy.registry

import net.mine_diver.unsafeevents.Event
import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemInstance

object CleansingRecipeRegistry {
    internal var recipes: Array<CleansingRecipe> = emptyArray()
    internal var cleansableItems: Array<ItemInstance> = emptyArray()

    fun getValidRecipes(items: List<ItemInstance>, player: PlayerBase? = null): List<CleansingRecipe> {
        // TODO: Respect multiple smaller stacks
        return recipes.filter { recipe -> // Check each recipe
            return@filter if (recipe.meetsRequirements(player)) {
                recipe.input.all { requiredItem -> // If all item requirements are satisfied
                    items.any { item ->
                        item.isDamageAndIDIdentical(requiredItem)
                    }
                }
            } else false
        }
    }

    fun ItemInstance?.isCleansable(): Boolean = if (this == null) false else
        cleansableItems.any { isDamageAndIDIdentical(it) }
}

class CleansingRecipeRegistryEvent : Event()
