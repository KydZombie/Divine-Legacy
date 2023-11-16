package com.kydzombie.divinelegacy.item

import net.minecraft.client.resource.language.I18n
import net.minecraft.item.ItemInstance
import net.modificationstation.stationapi.api.client.gui.CustomTooltipProvider
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.item.TemplateItemBase

class ArtificialPearl(identifier: Identifier) : TemplateItemBase(identifier), CustomTooltipProvider {
    private val descriptionTranslationKey: String
    init {
        setTranslationKey(identifier)
        maxStackSize = 4
        descriptionTranslationKey = "item.${identifier}.description"
    }

    override fun getTooltip(itemInstance: ItemInstance, originalTooltip: String): Array<String> {
        return arrayOf(
            originalTooltip,
            *I18n.translate(descriptionTranslationKey).split("\n").toTypedArray()
        )
    }
}