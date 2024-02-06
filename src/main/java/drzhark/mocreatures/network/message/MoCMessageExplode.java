package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.monster.MoCEntityOgre;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageExplode implements IMessage, IMessageHandler<MoCMessageExplode, IMessage> {

    private int entityId;

    public MoCMessageExplode() {}

    public MoCMessageExplode(int entityId)
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
    public IMessage onMessage(MoCMessageExplode message, MessageContext context)
    {
        List<Entity> entityList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof MoCEntityOgre)
            {
                ((MoCEntityOgre) entity).DestroyingOgre();
                break;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageExplode - entityId:%s", this.entityId);
    }
}