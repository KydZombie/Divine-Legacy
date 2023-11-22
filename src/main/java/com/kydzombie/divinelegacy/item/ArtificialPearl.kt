package com.kydzombie.divinelegacy.item

import net.minecraft.client.resource.language.I18n
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider
import net.modificationstation.stationapi.api.template.item.TemplateItem
import net.modificationstation.stationapi.api.util.Identifier

class ArtificialPearl(identifier: Identifier) : TemplateItem(identifier), CustomTooltipProvider {
    private val descriptionTranslationKey: String

    init {
        setTranslationKey(identifier)
        maxCount = 4
        descriptionTranslationKey = "item.${identifier}.description"
    }

    override fun getTooltip(stack: ItemStack, originalTooltip: String): Array<String> {
        return arrayOf(
            originalTooltip,
            *I18n.getTranslation(descriptionTranslationKey).split("\n").toTypedArray()
        )
    }
}