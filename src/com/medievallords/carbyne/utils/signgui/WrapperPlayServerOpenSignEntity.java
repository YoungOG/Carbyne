package com.medievallords.carbyne.utils.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerOpenSignEntity extends AbstractPacket {

    public static final PacketType TYPE;
    
    static {
        TYPE = PacketType.Play.Server.OPEN_SIGN_ENTITY;
    }
    
    public WrapperPlayServerOpenSignEntity() {
        super(new PacketContainer(WrapperPlayServerOpenSignEntity.TYPE), WrapperPlayServerOpenSignEntity.TYPE);
        this.handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerOpenSignEntity(final PacketContainer packet) {
        super(packet, WrapperPlayServerOpenSignEntity.TYPE);
    }
    
    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }
    
    public void setLocation(final BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }
}