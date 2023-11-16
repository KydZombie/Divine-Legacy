package com.kydzombie.divinelegacy.item

import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.item.TemplateItemBase

class CleansingDust(identifier: Identifier) : TemplateItemBase(identifier) {
    init {
        translationKey = identifier.toString()
    }
}