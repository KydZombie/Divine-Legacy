package com.kydzombie.divinelegacy.util

import net.minecraft.client.render.Tessellator
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.modificationstation.stationapi.api.client.StationRenderAPI
import net.modificationstation.stationapi.api.client.render.RendererAccess
import net.modificationstation.stationapi.api.client.render.model.BakedModel
import net.modificationstation.stationapi.api.client.render.model.VanillaBakedModel
import net.modificationstation.stationapi.api.client.render.model.json.ModelTransformation
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12

fun ItemStack.render(
    world: World,
    x: Double, y: Double, z: Double,
    scale: Float = 0.5f,
    rotation: Float = 0f
) {
    val tessellator = Tessellator.INSTANCE
    val atlas = StationRenderAPI.getBakedModelManager().getAtlas(Atlases.GAME_ATLAS_TEXTURE)
    fun renderVanilla() {
        // TODO: Render differently for blocks
        val depth = (1 / 16.0) / 2.0 // Renders outwards in both directions, so we want half of 1/16th
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, z)
        GL11.glRotatef(rotation, 0f, 1f, 0f)
        GL11.glTranslated(-.25, -.25, 0.0)
        GL11.glScalef(scale, scale, scale)

        GL11.glColor4f(1f, 1f, 1f, 1f)
        val textureId = method_725()
        atlas.bindTexture()
        val texture = atlas.getSprite(item.atlas.getTexture(textureId).id)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)

        // Front
        tessellator.startQuads()
        tessellator.normal(0f, 0f, 1f)
        tessellator.vertex(0.0, 0.0, +depth, texture.maxU.toDouble(), texture.maxV.toDouble())
        tessellator.vertex(1.0, 0.0, +depth, texture.minU.toDouble(), texture.maxV.toDouble())
        tessellator.vertex(1.0, 1.0, +depth, texture.minU.toDouble(), texture.minV.toDouble())
        tessellator.vertex(0.0, 1.0, +depth, texture.maxU.toDouble(), texture.minV.toDouble())
        tessellator.draw()

        // Back
        tessellator.startQuads()
        tessellator.normal(0f, 0f, -1f)
        tessellator.vertex(0.0, 1.0, -depth, texture.maxU.toDouble(), texture.minV.toDouble())
        tessellator.vertex(1.0, 1.0, -depth, texture.minU.toDouble(), texture.minV.toDouble())
        tessellator.vertex(1.0, 0.0, -depth, texture.minU.toDouble(), texture.maxV.toDouble())
        tessellator.vertex(0.0, 0.0, -depth, texture.maxU.toDouble(), texture.maxV.toDouble())
        tessellator.draw()

        val width = texture.contents.width
        val height = texture.contents.width
        val xDiff = texture.minU - texture.maxU
        val yDiff = texture.minV - texture.maxV
        val xSub = 0.5f * (texture.maxU - texture.minU) / width
        val ySub = 0.5f * (texture.maxV - texture.minV) / height

        // Right

        tessellator.startQuads()
        tessellator.normal(-1f, 0f, 0f)
        for (k in 0..<width) {
            val pos = k / width.toDouble()
            val iconPos = texture.maxU + xDiff * pos - xSub
            tessellator.vertex(pos, 0.0, -depth, iconPos, texture.maxV.toDouble())
            tessellator.vertex(pos, 0.0, +depth, iconPos, texture.maxV.toDouble())
            tessellator.vertex(pos, 1.0, +depth, iconPos, texture.minV.toDouble())
            tessellator.vertex(pos, 1.0, -depth, iconPos, texture.minV.toDouble())
        }
        tessellator.draw()

        // Left
        tessellator.startQuads()
        tessellator.normal(1f, 0f, 0f)
        val inverseWidth = 1f / width
        for (k in 0..<width) {
            val pos = k / width.toDouble()
            val iconPos = texture.maxU + xDiff * pos - xSub
            val posEnd = pos + inverseWidth
            tessellator.vertex(posEnd, 1.0, -depth, iconPos, texture.minV.toDouble())
            tessellator.vertex(posEnd, 1.0, +depth, iconPos, texture.minV.toDouble())
            tessellator.vertex(posEnd, 0.0, +depth, iconPos, texture.maxV.toDouble())
            tessellator.vertex(posEnd, 0.0, -depth, iconPos, texture.maxV.toDouble())
        }
        tessellator.draw()

        tessellator.startQuads()
        tessellator.normal(0f, 1f, 0f)
        val inverseHeight = 1f / height
        for (k in 0..<height) {
            val pos = k / height.toDouble()
            val iconPos = texture.maxV + yDiff * pos - ySub
            val posEnd = pos + inverseHeight
            tessellator.vertex(0.0, posEnd, +depth, texture.maxU.toDouble(), iconPos)
            tessellator.vertex(1.0, posEnd, +depth, texture.minU.toDouble(), iconPos)
            tessellator.vertex(1.0, posEnd, -depth, texture.minU.toDouble(), iconPos)
            tessellator.vertex(0.0, posEnd, -depth, texture.maxU.toDouble(), iconPos)
        }
        tessellator.draw()

        // Bottom
        tessellator.startQuads()
        tessellator.normal(0f, -1f, 0f)
        for (k in 0..<height) {
            val pos = k / height.toDouble()
            val iconPos = texture.maxV + yDiff * pos - ySub
            tessellator.vertex(1.0, pos, +depth, texture.minU.toDouble(), iconPos)
            tessellator.vertex(0.0, pos, +depth, texture.maxU.toDouble(), iconPos)
            tessellator.vertex(0.0, pos, -depth, texture.maxU.toDouble(), iconPos)
            tessellator.vertex(1.0, pos, -depth, texture.minU.toDouble(), iconPos)
        }
        tessellator.draw()

        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL12.GL_RESCALE_NORMAL)

        GL11.glPopMatrix()
    }

    fun renderBakedModel(model: BakedModel) {
        GL11.glPushMatrix()
        GL11.glEnable(GL12.GL_RESCALE_NORMAL)
        GL11.glTranslated(x, y, z)
        GL11.glRotatef(rotation, 0f, 1f, 0f)
        GL11.glScalef(scale, scale, scale)

        atlas.bindTexture()

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