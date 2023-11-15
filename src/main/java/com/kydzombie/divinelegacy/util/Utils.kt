package com.kydzombie.divinelegacy.util

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.BlockBase
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerBase
import net.minecraft.item.ItemBase
import net.minecraft.item.ItemInstance
import net.minecraft.packet.play.ChatMessage0x3Packet
import net.modificationstation.stationapi.api.packet.PacketHelper

fun ItemBase.asItemInstance(count: Int = 1, damage: Int = 0): ItemInstance = ItemInstance(this, count, damage)

fun BlockBase.asItemInstance(count: Int = 1, damage: Int = 0): ItemInstance = ItemInstance(this, count, damage)

fun sendChatMessage(receiver: PlayerBase, message: String) {
    if (!receiver.level.isServerSide) { // Client on server
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            (FabricLoader.getInstance().gameInstance as Minecraft).overlay.addChatMessage(message)
        } else {
            PacketHelper.sendTo(receiver, ChatMessage0x3Packet(message))
        }
    }
}