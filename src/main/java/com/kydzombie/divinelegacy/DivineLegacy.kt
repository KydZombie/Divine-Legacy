package com.kydzombie.divinelegacy

import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.item.ItemBase
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.registry.ModID

object DivineLegacy {
    @Entrypoint.ModID
    lateinit var MOD_ID: ModID
    @EventListener
    private fun registerItems(event: ItemRegistryEvent) {

    }
}