package com.kydzombie.divinelegacy

import com.kydzombie.divinelegacy.block.CleansingWaterFlowing
import com.kydzombie.divinelegacy.block.CleansingWaterStill
import com.kydzombie.divinelegacy.block.Pedestal
import com.kydzombie.divinelegacy.block.PedestalEntity
import com.kydzombie.divinelegacy.client.PedestalRenderer
import com.kydzombie.divinelegacy.item.ArtificialPearl
import com.kydzombie.divinelegacy.item.CleansingDust
import com.kydzombie.divinelegacy.item.Vial
import com.kydzombie.divinelegacy.item.Vial.Companion.Contents
import com.kydzombie.divinelegacy.item.Vial.Companion.getVialContents
import com.kydzombie.divinelegacy.player.DivinePlayerHandler
import com.kydzombie.divinelegacy.registry.CleansingRecipe
import com.kydzombie.divinelegacy.registry.CleansingRecipeRegistryEvent
import net.mine_diver.unsafeevents.listener.EventListener
import net.minecraft.block.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.modificationstation.stationapi.api.StationAPI
import net.modificationstation.stationapi.api.client.event.block.entity.BlockEntityRendererRegisterEvent
import net.modificationstation.stationapi.api.client.event.render.model.ItemModelPredicateProviderRegistryEvent
import net.modificationstation.stationapi.api.client.event.texture.TextureRegisterEvent
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.event.block.entity.BlockEntityRegisterEvent
import net.modificationstation.stationapi.api.event.entity.player.PlayerEvent
import net.modificationstation.stationapi.api.event.registry.BlockRegistryEvent
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import net.modificationstation.stationapi.api.util.Namespace

@Suppress("MemberVisibilityCanBePrivate")
object DivineLegacy {
    @Entrypoint.Namespace
    lateinit var NAMESPACE: Namespace

    lateinit var cleansingDust: CleansingDust
    lateinit var vial: Vial
    lateinit var artificialPearl: ArtificialPearl

    @EventListener
    private fun registerItems(event: ItemRegistryEvent) {
        cleansingDust = CleansingDust(NAMESPACE.id("cleansing_dust"))
        vial = Vial(NAMESPACE.id("vial"))
        artificialPearl = ArtificialPearl(NAMESPACE.id("artificial_pearl"))
        StationAPI.EVENT_BUS.post(CleansingRecipeRegistryEvent())
    }

    lateinit var cleansingWaterFlowing: CleansingWaterFlowing
    lateinit var cleansingWaterStill: CleansingWaterStill

    //    lateinit var altar: Altar
    lateinit var pedestal: Pedestal

    @EventListener
    private fun registerBlocks(event: BlockRegistryEvent) {
        cleansingWaterFlowing = CleansingWaterFlowing(NAMESPACE.id("cleansing_fluid_flowing"))
        cleansingWaterStill = CleansingWaterStill(NAMESPACE.id("cleansing_fluid_still"))
//        altar = Altar(NAMESPACE.id("altar"))
        pedestal = Pedestal(NAMESPACE.id("pedestal"), Material.STONE)
    }

    @EventListener
    private fun registerBlockEntities(event: BlockEntityRegisterEvent) {
//        event.register(AltarEntity::class.java, NAMESPACE.id("altar").toString())
        event.register(PedestalEntity::class.java, NAMESPACE.id("pedestal").toString())
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
            input = arrayOf(ItemStack(Item.SLIMEBALL), ItemStack(Item.DIAMOND)),
            output = arrayOf(ItemStack(artificialPearl)),
            levelRequirement = 10,
            levelCost = 5
        )
    }
}

object DivineLegacyClient {
    @EventListener
    private fun registerPredicates(event: ItemModelPredicateProviderRegistryEvent) {
        event.registry.register(DivineLegacy.vial, DivineLegacy.NAMESPACE.id("contents")) { stack: ItemStack, _, _, _ ->
            stack.getVialContents().ordinal / (Contents.entries.size - 1f)
        }
    }

    @EventListener
    private fun registerTextures(event: TextureRegisterEvent) {
        Atlases.getTerrain().addTexture(DivineLegacy.NAMESPACE.id("block/cleansing_water_still")).index.let {
            DivineLegacy.cleansingWaterStill.textureId = it
            DivineLegacy.cleansingWaterFlowing.textureId = it
        }
        Atlases.getTerrain().addTexture(DivineLegacy.NAMESPACE.id("block/cleansing_water_flowing"))
    }

    @EventListener
    private fun registerBlockRenderers(event: BlockEntityRendererRegisterEvent) {
//        event.renderers[AltarEntity::class.java] = AltarRenderer
        event.renderers[PedestalEntity::class.java] = PedestalRenderer
    }
}