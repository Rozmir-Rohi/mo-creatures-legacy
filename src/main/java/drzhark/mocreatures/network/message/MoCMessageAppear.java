package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageAppear implements IMessage, IMessageHandler<MoCMessageAppear, IMessage> {

    private int entityId;

    public MoCMessageAppear() {}

    public MoCMessageAppear(int entityId)
    {
        this.entityId = entityId;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        entityId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageAppear message, MessageContext context)
    {
        List<Entity> entityList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof MoCEntityHorse)
            {
                ((MoCEntityHorse) entity).MaterializeFX();
                break;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageAppear - entityId:%s", entityId);
    }
}