package com.kydzombie.divinelegacy

import com.kydzombie.divinelegacy.block.CleansingWaterFlowing
import com.kydzombie.divinelegacy.block.CleansingWaterStill
import com.kydzombie.divinelegacy.item.CleansingDust
import com.kydzombie.divinelegacy.item.Vial
import com.kydzombie.divinelegacy.item.Vial.Companion.getVialContents
import com.kydzombie.divinelegacy.player.DivinePlayerHandler
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.BlockBase
import net.minecraft.item.ItemInstance
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.event.entity.player.PlayerEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.registry.ModID

object DivineLegacy {
    @Entrypoint.ModID
    lateinit var MOD_ID: ModID

    lateinit var cleansingDust: CleansingDust
    @EventListener
    private fun registerItems(event: ItemRegistryEvent) {
        cleansingDust = CleansingDust(MOD_ID.id("cleansing_dust"))
    }

    lateinit var cleansingWaterFlowing: BlockBase
    lateinit var cleansingWaterStill: BlockBase
    lateinit var vial: Vial

    @EventListener
    private fun registerBlocks(event: BlockRegistryEvent) {
        cleansingWaterFlowing = CleansingWaterFlowing(MOD_ID.id("cleansing_fluid_flowing"))
        cleansingWaterStill = CleansingWaterStill(MOD_ID.id("cleansing_fluid_still"))
        vial = Vial(MOD_ID.id("vial"))
    }

    @EventListener
    private fun registerPlayerHandlers(event: PlayerEvent.HandlerRegister) {
        event.playerHandlers.add(DivinePlayerHandler(event.player))
    }
}

object DivineLegacyClient {
    @EventListener
    private fun registerPredicates(event: ItemModelPredicateProviderRegistryEvent) {
        event.registry.register(DivineLegacy.vial, DivineLegacy.MOD_ID.id("contents")) { item: ItemInstance, world, entity, seed ->
            item.getVialContents().ordinal / (Vial.Companion.Contents.entries.size - 1f)
        }
    }

    @EventListener
    private fun registerTextures(event: TextureRegisterEvent) {
        Atlases.getTerrain().addTexture(DivineLegacy.MOD_ID.id("block/cleansing_water_still")).index.let {
            DivineLegacy.cleansingWaterStill.texture = it
            DivineLegacy.cleansingWaterFlowing.texture = it
        }
        Atlases.getTerrain().addTexture(DivineLegacy.MOD_ID.id("block/cleansing_water_flowing"))
    }
}