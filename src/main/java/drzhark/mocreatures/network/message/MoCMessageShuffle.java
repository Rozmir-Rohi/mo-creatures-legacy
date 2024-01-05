package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageShuffle implements IMessage, IMessageHandler<MoCMessageShuffle, IMessage> {

    private int entityId;
    private boolean flag;

    public MoCMessageShuffle() {}

    public MoCMessageShuffle(int entityId, boolean flag)
    {
        this.entityId = entityId;
        this.flag = flag;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.entityId);
        buffer.writeBoolean(flag);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.entityId = buffer.readInt();
        this.flag = buffer.readBoolean();
    }

    @Override
    public IMessage onMessage(MoCMessageShuffle message, MessageContext ctx)
    {
        List<Entity> entityList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof MoCEntityHorse)
            {
                if (flag)
                {
                    ((MoCEntityHorse) entity).shuffle();
                }
                else
                {
                    ((MoCEntityHorse) entity).shuffleCounter = 0;
                }
                break;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageShuffle - entityId:%s, flag:%s", this.entityId, this.flag);
    }
}