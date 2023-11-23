package com.kydzombie.divinelegacy.util

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.play.ChatMessagePacket
import net.modificationstation.stationapi.api.network.packet.PacketHelper

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