package com.kydzombie.divinelegacy

import com.kydzombie.divinelegacy.block.CleansingWaterFlowing
import com.kydzombie.divinelegacy.block.CleansingWaterStill
import com.kydzombie.divinelegacy.item.ArtificialPearl
import com.kydzombie.divinelegacy.item.CleansingDust
import com.kydzombie.divinelegacy.item.Vial
import com.kydzombie.divinelegacy.item.Vial.Companion.Contents
import com.kydzombie.divinelegacy.item.Vial.Companion.getVialContents
import com.kydzombie.divinelegacy.player.DivinePlayerHandler
import com.kydzombie.divinelegacy.registry.CleansingRecipe
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistryEvent
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.item.ItemBase
import net.minecraft.item.ItemInstance
import net.modificationstation.stationapi.api.StationAPI
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.event.entity.player.PlayerEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.registry.ModID

@Suppress("MemberVisibilityCanBePrivate")
object DivineLegacy {
    @Entrypoint.ModID
    lateinit var MOD_ID: ModID

    lateinit var cleansingDust: CleansingDust
    lateinit var vial: Vial
    lateinit var artificialPearl: ArtificialPearl

    @EventListener
    private fun registerItems(event: ItemRegistryEvent) {
        cleansingDust = CleansingDust(MOD_ID.id("cleansing_dust"))
        vial = Vial(MOD_ID.id("vial"))
        artificialPearl = ArtificialPearl(MOD_ID.id("artificial_pearl"))
        StationAPI.EVENT_BUS.post(CleansingRecipeRegistryEvent())
    }

    lateinit var cleansingWaterFlowing: CleansingWaterFlowing
    lateinit var cleansingWaterStill: CleansingWaterStill

    @EventListener
    private fun registerBlocks(event: BlockRegistryEvent) {
        cleansingWaterFlowing = CleansingWaterFlowing(MOD_ID.id("cleansing_fluid_flowing"))
        cleansingWaterStill = CleansingWaterStill(MOD_ID.id("cleansing_fluid_still"))
    }

    @EventListener
    private fun registerPlayerHandlers(event: PlayerEvent.HandlerRegister) {
        event.playerHandlers.add(DivinePlayerHandler(event.player))
    }

    @EventListener
    private fun registerCleansingRecipes(event: CleansingRecipeRegistryEvent) {
        CleansingRecipe(
            input = arrayOf(Vial.createVial(Contents.WATER)),
            output = arrayOf(Vial.createVial(Contents.CLEANSING_WATER)),
            levelRequirement = 2,
            levelCost = 1
        )
        CleansingRecipe(
            input = arrayOf(ItemInstance(ItemBase.slimeball), ItemInstance(ItemBase.diamond)),
            output = arrayOf(ItemInstance(artificialPearl)),
            levelRequirement = 10,
            levelCost = 5
        )
    }
}

object DivineLegacyClient {
    @EventListener
    private fun registerPredicates(event: ItemModelPredicateProviderRegistryEvent) {
        event.registry.register(DivineLegacy.vial, DivineLegacy.MOD_ID.id("contents")) { item: ItemInstance, _, _, _ ->
            item.getVialContents().ordinal / (Contents.entries.size - 1f)
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