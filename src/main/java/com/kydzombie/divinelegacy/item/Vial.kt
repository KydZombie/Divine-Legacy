package com.kydzombie.divinelegacy.item

import com.kydzombie.divinelegacy.DivineLegacy
import com.kydzombie.divinelegacy.player.DivinePlayerHandler.Companion.getDivineStats
import com.kydzombie.divinelegacy.util.sendChatMessage
import net.minecraft.block.BlockBase
import net.minecraft.client.resource.language.I18n
import net.minecraft.entity.animal.Cow
import net.minecraft.entity.player.PlayerBase
import net.minecraft.entity.swimming.Squid
import net.minecraft.item.ItemInstance
import net.minecraft.level.Level
import net.minecraft.util.hit.HitType
import net.minecraft.util.maths.MathHelper
import net.minecraft.util.maths.Vec3f
import net.modificationstation.stationapi.api.client.gui.CustomTooltipProvider
import net.modificationstation.stationapi.api.registry.Identifier
import net.modificationstation.stationapi.api.template.item.TemplateItemBase

class Vial(identifier: Identifier) : TemplateItemBase(identifier), CustomTooltipProvider {
    init {
        setTranslationKey(identifier)
    }

    override fun use(itemInstance: ItemInstance, level: Level, player: PlayerBase): ItemInstance {
        val contents = itemInstance.getVialContents()
        if (contents != Contents.EMPTY) {
            return itemInstance
        }

        val var4 = 1.0f
        val var5 = player.prevPitch + (player.pitch - player.prevPitch) * var4
        val var6 = player.prevYaw + (player.yaw - player.prevYaw) * var4
        val var7 = player.prevX + (player.x - player.prevX) * var4.toDouble()
        val var9 =
            player.prevY + (player.y - player.prevY) * var4.toDouble() + 1.62 - player.standingEyeHeight.toDouble()
        val var11 = player.prevZ + (player.z - player.prevZ) * var4.toDouble()
        val var13 = Vec3f.from(var7, var9, var11)
        val var14 = MathHelper.cos(-var6 * 0.017453292f - 3.1415927f)
        val var15 = MathHelper.sin(-var6 * 0.017453292f - 3.1415927f)
        val var16 = -MathHelper.cos(-var5 * 0.017453292f)
        val var17 = MathHelper.sin(-var5 * 0.017453292f)
        val var18 = var15 * var16
        val var20 = var14 * var16
        val var21 = 5.0
        val var23 = var13.method_1301(var18.toDouble() * var21, var17.toDouble() * var21, var20.toDouble() * var21)
        val hitResult = level.method_161(var13, var23, true)

        return if (hitResult == null) {
            itemInstance
        } else {
            if (hitResult.type == HitType.field_789) { // Hit a block
                val hitX = hitResult.x
                val hitY = hitResult.y
                val hitZ = hitResult.z
                if (!level.method_171(player, hitX, hitY, hitZ)) {
                    return itemInstance
                }
                when (level.getTileId(hitX, hitY, hitZ)) {
                    BlockBase.FLOWING_WATER.id -> {
                        if (level.getTileMeta(hitX, hitY, hitZ) == 0) {
                            itemInstance.setVialContents(Contents.WATER, player)
                        }
                    }

                    BlockBase.STILL_WATER.id -> {
                        itemInstance.setVialContents(Contents.WATER, player)
                    }

                    BlockBase.FLOWING_LAVA.id -> {
                        if (level.getTileMeta(hitX, hitY, hitZ) == 0) {
                            itemInstance.setVialContents(Contents.LAVA, player)
                        }
                    }

                    BlockBase.STILL_LAVA.id -> {
                        itemInstance.setVialContents(Contents.LAVA, player)
                    }

                    DivineLegacy.cleansingWaterFlowing.id -> {
                        if (level.getTileMeta(hitX, hitY, hitZ) > 0) return itemInstance
                        val handler = player.getDivineStats();
                        if (handler.divineLevel < 10) {
                            sendChatMessage(
                                player,
                                "You are not yet blessed enough to do this directly. Divine level: ${handler.divineLevel}/10"
                            )
                            return itemInstance
                        }
                        itemInstance.setVialContents(Contents.CLEANSING_WATER, player)
                    }

                    DivineLegacy.cleansingWaterStill.id -> {
                        val handler = player.getDivineStats();
                        if (handler.divineLevel < 10) {
                            sendChatMessage(
                                player,
                                "You are not yet blessed enough to do this directly. Divine level: ${handler.divineLevel}/10"
                            )
                            return itemInstance
                        }
                        itemInstance.setVialContents(Contents.CLEANSING_WATER, player)
                    }
                }
            } else {
                when (hitResult.field_1989) {
                    is Cow -> {
                        itemInstance.setVialContents(Contents.MILK, player)
                    }

                    is Squid -> {
                        itemInstance.setVialContents(Contents.INK, player)
                    }
                }
            }
            itemInstance
        }
    }

    override fun getTooltip(itemInstance: ItemInstance, originalTooltip: String): Array<String> {
        val contents = itemInstance.getVialContents()
        if (contents == Contents.EMPTY) {
            return arrayOf(I18n.translate("item.divine-legacy:vial.empty"))
        }
        return arrayOf(I18n.translate("item.divine-legacy:vial.unformatted").format(contents.getTranslation()))
    }

    companion object {
        enum class Contents {
            EMPTY, WATER, CLEANSING_WATER, LAVA, MILK, INK;

            // TODO: Make this lazy initialized
            fun getTranslation(): String = I18n.translate("fluid.divine-legacy:${this.toString().lowercase()}.name")
        }

        fun createVial(contents: Contents): ItemInstance =
            ItemInstance(DivineLegacy.vial).apply { setVialContents(contents) }

        fun ItemInstance.getVialContents(): Contents = Contents.entries[stationNBT.getByte("contents").toInt()]

        fun ItemInstance.setVialContents(contents: Contents) = stationNBT.put("contents", contents.ordinal.toByte())

        private fun ItemInstance.setVialContents(contents: Contents, player: PlayerBase) {
            if (count == 1) setVialContents(contents)
            val newVial = ItemInstance(type).apply {
                setVialContents(contents)
            }
            if (!player.inventory.addStack(newVial)) {
                player.dropItem(newVial)
            }
            count -= 1
        }
    }
}