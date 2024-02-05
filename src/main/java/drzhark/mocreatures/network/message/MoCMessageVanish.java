package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageVanish implements IMessage, IMessageHandler<MoCMessageVanish, IMessage> {

    private int entityId;

    public MoCMessageVanish() {}

    public MoCMessageVanish(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.entityId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageVanish message, MessageContext ctx)
    {
        List<Entity> entList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof MoCEntityHorse)
            {
                ((MoCEntityHorse) entity).setVanishC((byte) 1);
                break;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageVanish - entityId:%s", this.entityId);
    }
}