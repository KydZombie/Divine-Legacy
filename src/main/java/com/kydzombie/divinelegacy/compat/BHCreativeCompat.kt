package com.kydzombie.divinelegacy.compat

import com.kydzombie.divinelegacy.DivineLegacy
import com.kydzombie.divinelegacy.item.Vial
import com.kydzombie.divinelegacy.util.asStack
import net.mine_diver.unsafeevents.listener.EventListener
import paulevs.bhcreative.api.CreativeTab
import paulevs.bhcreative.api.SimpleTab
import paulevs.bhcreative.registry.TabRegistryEvent

object BHCreativeCompat {
    private lateinit var blocks: CreativeTab
    private lateinit var items: CreativeTab

    @EventListener
    fun onTabInit(event: TabRegistryEvent) {
        blocks = SimpleTab(DivineLegacy.NAMESPACE.id("blocks"), DivineLegacy.cleansingDust)
        event.register(blocks)
        blocks.addItem(DivineLegacy.pedestal.asStack())
        items = SimpleTab(DivineLegacy.NAMESPACE.id("items"), DivineLegacy.cleansingDust)
        event.register(items)
        items.addItem(DivineLegacy.cleansingDust.asStack())
        items.addItem(DivineLegacy.artificialPearl.asStack())
        items.addItem(Vial.createVial(Vial.Companion.Contents.EMPTY))
        items.addItem(Vial.createVial(Vial.Companion.Contents.WATER))
    }
}