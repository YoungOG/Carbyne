package com.medievallords.carbyne.utils.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.medievallords.carbyne.Carbyne;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by Calvin on 4/6/2017
 * for the Carbyne project.
 */
public class SignGUI {

    private static Hashtable<UUID, BlockPosition> playerBlockPositions;
    private static Hashtable<UUID, Block> playerBlocks;

    public SignGUI() {
        playerBlockPositions = new Hashtable<>();
        playerBlocks = new Hashtable<>();
        registerSignUpdateListener();
    }

    public static void openSignEditor(Player player, Block block, String[] text) {
        int x = player.getLocation().getBlockX();
        int y = 255;
        int z = player.getLocation().getBlockZ();
        BlockPosition bp = new BlockPosition(x, y, z);
        WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange();
        WrappedBlockData blockData = WrappedBlockData.createData(Material.SIGN_POST);
        blockChangePacket.setBlockData(blockData);
        blockChangePacket.setLocation(bp);
        blockChangePacket.sendPacket(player);
        WrapperPlayServerUpdateSign updateSignPacket = new WrapperPlayServerUpdateSign();
        updateSignPacket.setLocation(new BlockPosition(x, y, z));
        WrappedChatComponent[] lines = { WrappedChatComponent.fromText(text[0]), WrappedChatComponent.fromText(text[1]), WrappedChatComponent.fromText(text[2]), WrappedChatComponent.fromText(text[3]) };
        updateSignPacket.setLines(lines);
        updateSignPacket.sendPacket(player);
        WrapperPlayServerOpenSignEntity packet = new WrapperPlayServerOpenSignEntity();
        packet.setLocation(new BlockPosition(x, y, z));
        packet.sendPacket(player);
        SignGUI.playerBlockPositions.put(player.getUniqueId(), bp);
        SignGUI.playerBlocks.put(player.getUniqueId(), block);
    }

    private void registerSignUpdateListener() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        if (SignGUI.playerBlockPositions == null) {
            SignGUI.playerBlockPositions = new Hashtable<>();
        }

        if (SignGUI.playerBlocks == null) {
            SignGUI.playerBlocks = new Hashtable<>();
        }

        manager.addPacketListener(new PacketAdapter(Carbyne.getInstance(), PacketType.Play.Client.UPDATE_SIGN) {
            public void onPacketReceiving(PacketEvent event) {
                String[] text = new String[4];
                Player player = event.getPlayer();
                WrapperPlayClientUpdateSign packet = new WrapperPlayClientUpdateSign(event.getPacket());
                BlockPosition bp = packet.getLocation();
                BlockPosition playerBlockPos = SignGUI.playerBlockPositions.get(player.getUniqueId());
                Block block = SignGUI.playerBlocks.get(player.getUniqueId());

                if (playerBlockPos != null && bp.getX() == playerBlockPos.getX() && bp.getY() == playerBlockPos.getY() && bp.getZ() == playerBlockPos.getZ()) {
                    for (int i = 0; i < packet.getLines().length; ++i) {
                        WrappedChatComponent chat = packet.getLines()[i];
                        String str = StringEscapeUtils.unescapeJavaScript(chat.getJson());
                        str = str.substring(1, str.length() - 1);
                        text[i] = str;
                    }

                    WrapperPlayServerBlockChange blockChangePacket = new WrapperPlayServerBlockChange();
                    WrappedBlockData blockData = WrappedBlockData.createData(Material.AIR);
                    blockChangePacket.setBlockData(blockData);
                    blockChangePacket.setLocation(playerBlockPos);
                    blockChangePacket.sendPacket(player);
                    SignGUI.playerBlockPositions.remove(player.getUniqueId());
                    SignGUI.playerBlocks.remove(player.getUniqueId());
                    SignGUIUpdateEvent updateEvent = new SignGUIUpdateEvent(player, block, text);
                    Bukkit.getServer().getPluginManager().callEvent(updateEvent);
                }
            }

            public void onPacketSending(final PacketEvent event) {
            }
        });
    }
}
