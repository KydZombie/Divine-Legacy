package com.kydzombie.divinelegacy.item

import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.item.TemplateItemBase

class ArtificialPearl(identifier: Identifier): TemplateItemBase(identifier) {
    init {
        setTranslationKey(identifier)
        maxStackSize = 4
    }
}