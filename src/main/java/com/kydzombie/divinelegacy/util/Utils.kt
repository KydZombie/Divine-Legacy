package com.kydzombie.divinelegacy.util

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.render.Tessellator
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.play.ChatMessagePacket
import net.minecraft.world.World
import net.modificationstation.stationapi.api.client.StationRenderAPI
import net.modificationstation.stationapi.api.client.render.RendererAccess
import net.modificationstation.stationapi.api.client.render.model.BakedModel
import net.modificationstation.stationapi.api.client.render.model.VanillaBakedModel
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import net.modificationstation.stationapi.api.network.packet.PacketHelper
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

fun Item.asStack(count: Int = 1, damage: Int = 0): ItemStack = ItemStack(this, count, damage)

fun Block.asStack(count: Int = 1, damage: Int = 0): ItemStack = ItemStack(this, count, damage)

@Suppress("DEPRECATION")
fun sendChatMessage(receiver: PlayerEntity, message: String) {
    if (!receiver.world.isRemote) { // Client on server
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            (FabricLoader.getInstance().gameInstance as Minecraft).inGameHud.addChatMessage(message)
        } else {
            PacketHelper.sendTo(receiver, ChatMessagePacket(message))
        }
    }
}

fun ItemStack.render(
    world: World,
    x: Double, y: Double, z: Double,
    scale: Float = 0.5f,
    rotation: Float = 0f
) {
    fun renderVanilla() {

    }

    fun renderBakedModel(model: BakedModel) {
        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glTranslated(x, y, z)
        GL11.glRotatef(rotation, 0f, 1f, 0f)
        GL11.glScalef(scale, scale, scale)

        StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE).bindTexture()

        val tessellator = Tessellator.INSTANCE
        tessellator.startQuads()
        RendererAccess.INSTANCE.renderer.bakedModelRenderer().renderItem(
            this,
            ModelTransformation.Mode.FIXED,
            1f,
            model
        )
        tessellator.draw()

        GL11.glScalef(1 / scale, 1 / scale, 1 / scale)
        GL11.glRotatef(-rotation, 0f, 0f, 0f)
        GL11.glTranslated(-x, -y, -z)
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)
        GL11.glPopMatrix()
    }

    val model = RendererAccess.INSTANCE.renderer.bakedModelRenderer().getModel(
        this,
        world,
        null,
        0
    )

    if (model is VanillaBakedModel) {
        renderVanilla()
    } else if (!model.isBuiltin) {
        renderBakedModel(model)
    }
}