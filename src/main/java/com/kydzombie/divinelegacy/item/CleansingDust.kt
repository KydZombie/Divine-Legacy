package com.kydzombie.divinelegacy.item

import net.modificationstation.stationapi.api.template.item.TemplateItem
import net.modificationstation.stationapi.api.util.Identifier

class CleansingDust(identifier: Identifier) : TemplateItem(identifier) {
    init {
        translationKey = identifier.toString()
    }
}