package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.IMoCEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageAnimation implements IMessage, IMessageHandler<MoCMessageAnimation, IMessage> {

    private int entityId;
    private int animationType;

    public MoCMessageAnimation() {}

    public MoCMessageAnimation(int entityId, int animationType)
    {
        this.entityId = entityId;
        this.animationType = animationType;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeInt(animationType);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        entityId = buffer.readInt();
        animationType = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageAnimation message, MessageContext context)
    {
        List<Entity> entityList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof IMoCEntity)
            {
                ((IMoCEntity) entity).performAnimation(message.animationType);
                break;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageAnimation - entityId:%s, animationType:%s", entityId, animationType);
    }
}
