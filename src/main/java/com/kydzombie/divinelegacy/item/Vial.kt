package com.kydzombie.divinelegacy.item

import com.kydzombie.divinelegacy.DivineLegacy
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import com.kydzombie.divinelegacy.util.sendChatMessage
import net.minecraft.block.Block
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.passive.CowEntity
import net.minecraft.entity.passive.SquidEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.hit.HitResultType
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.modificationstation.stationapi.api.client.item.CustomTooltipProvider
import net.modificationstation.stationapi.api.template.item.TemplateItem
import net.modificationstation.stationapi.api.util.Identifier

class Vial(identifier: Identifier) : TemplateItem(identifier), CustomTooltipProvider {
    init {
        setTranslationKey(identifier)
    }

    override fun use(stack: ItemStack, world: World, player: PlayerEntity): ItemStack {
        val contents = stack.getVialContents()
        if (contents != Contents.EMPTY) {
            return stack
        }

        val var4 = 1.0f
        val var5 = player.prevPitch + (player.pitch - player.prevPitch) * var4
        val var6 = player.prevYaw + (player.yaw - player.prevYaw) * var4
        val var7 = player.prevX + (player.x - player.prevX) * var4.toDouble()
        val var9 =
            player.prevY + (player.y - player.prevY) * var4.toDouble() + 1.62 - player.eyeHeight.toDouble()
        val var11 = player.prevZ + (player.z - player.prevZ) * var4.toDouble()
        val var13 = Vec3d.create(var7, var9, var11)
        val var14 = MathHelper.cos(-var6 * 0.017453292f - 3.1415927f)
        val var15 = MathHelper.sin(-var6 * 0.017453292f - 3.1415927f)
        val var16 = -MathHelper.cos(-var5 * 0.017453292f)
        val var17 = MathHelper.sin(-var5 * 0.017453292f)
        val var18 = var15 * var16
        val var20 = var14 * var16
        val var21 = 5.0
        val var23 = var13.add(var18.toDouble() * var21, var17.toDouble() * var21, var20.toDouble() * var21)
        val hitResult = world.method_161(var13, var23, true)

        return if (hitResult == null) {
            stack
        } else {
            if (hitResult.type == HitResultType.BLOCK) { // Hit a block
                val hitX = hitResult.blockX
                val hitY = hitResult.blockY
                val hitZ = hitResult.blockZ
                if (!world.method_171(player, hitX, hitY, hitZ)) {
                    return stack
                }
                when (world.getBlockId(hitX, hitY, hitZ)) {
                    Block.FLOWING_WATER.id -> {
                        if (world.getBlockMeta(hitX, hitY, hitZ) == 0) {
                            stack.setVialContents(Contents.WATER, player)
                        }
                    }

                    Block.WATER.id -> {
                        stack.setVialContents(Contents.WATER, player)
                    }

                    Block.FLOWING_LAVA.id -> {
                        if (world.getBlockMeta(hitX, hitY, hitZ) == 0) {
                            stack.setVialContents(Contents.LAVA, player)
                        }
                    }

                    Block.LAVA.id -> {
                        stack.setVialContents(Contents.LAVA, player)
                    }

                    DivineLegacy.cleansingWaterFlowing.id -> {
                        if (world.getBlockMeta(hitX, hitY, hitZ) > 0) return stack
                        val handler = player.getDivineStats()
                        if (handler.divineLevel < 10) {
                            sendChatMessage(
                                player,
                                "You are not yet blessed enough to do this directly. Divine level: ${handler.divineLevel}/10"
                            )
                            return stack
                        }
                        stack.setVialContents(Contents.CLEANSING_WATER, player)
                    }

                    DivineLegacy.cleansingWaterStill.id -> {
                        val handler = player.getDivineStats()
                        if (handler.divineLevel < 10) {
                            sendChatMessage(
                                player,
                                "You are not yet blessed enough to do this directly. Divine level: ${handler.divineLevel}/10"
                            )
                            return stack
                        }
                        stack.setVialContents(Contents.CLEANSING_WATER, player)
                    }
                }
            } else {
                when (hitResult.entity) {
                    is CowEntity -> {
                        stack.setVialContents(Contents.MILK, player)
                    }

                    is SquidEntity -> {
                        stack.setVialContents(Contents.INK, player)
                    }
                }
            }
            stack
        }
    }

    override fun getTooltip(stack: ItemStack, originalTooltip: String): Array<String> {
        val contents = stack.getVialContents()
        if (contents == Contents.EMPTY) {
            return arrayOf(I18n.getTranslation("item.divine-legacy:vial.empty"))
        }
        return arrayOf(I18n.getTranslation("item.divine-legacy:vial.unformatted", contents.getTranslation()))
    }

    companion object {
        enum class Contents {
            EMPTY, WATER, CLEANSING_WATER, LAVA, MILK, INK;

            // TODO: Make this lazy initialized
            fun getTranslation(): String =
                I18n.getTranslation("fluid.divine-legacy:${this.toString().lowercase()}.name")
        }

        fun createVial(contents: Contents): ItemStack =
            ItemStack(DivineLegacy.vial).apply { setVialContents(contents) }

        fun ItemStack.getVialContents(): Contents = Contents.entries[stationNbt.getByte("contents").toInt()]

        fun ItemStack.setVialContents(contents: Contents) = stationNbt.putByte("contents", contents.ordinal.toByte())

        private fun ItemStack.setVialContents(contents: Contents, player: PlayerEntity) {
            if (count == 1) setVialContents(contents)
            val newVial = ItemStack(DivineLegacy.vial).apply {
                setVialContents(contents)
            }
            if (!player.inventory.method_671(newVial)) { // addStack
                player.method_513(newVial) // takeItem
            }
            count -= 1
        }
    }
}