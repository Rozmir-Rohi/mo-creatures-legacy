package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.monster.MoCEntityGolem;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageTwoBytes implements IMessage, IMessageHandler<MoCMessageTwoBytes, IMessage> {

    private int entityId;
    private byte slot;
    private byte value;

    public MoCMessageTwoBytes() {}

    public MoCMessageTwoBytes(int entityId, byte slot, byte value)
    {
        this.entityId = entityId;
        this.slot = slot;
        this.value = value;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeByte(slot);
        buffer.writeByte(value);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        entityId = buffer.readInt();
        slot = buffer.readByte();
        value = buffer.readByte();
    }

    @Override
    public IMessage onMessage(MoCMessageTwoBytes message, MessageContext context)
    {
        Entity entity = MoCClientProxy.mc.thePlayer.worldObj.getEntityByID(message.entityId);
        if (entity != null && entity instanceof MoCEntityGolem)
        {
            ((MoCEntityGolem) entity).saveGolemCube(message.slot, message.value);
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageTwoBytes - entityId:%s, slot:%s, value:%s", entityId, slot, value);
    }
}