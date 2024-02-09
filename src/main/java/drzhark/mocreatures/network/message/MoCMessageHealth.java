package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.client.MoCClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class MoCMessageHealth implements IMessage, IMessageHandler<MoCMessageHealth, IMessage> {

    private int entityId;
    private float health;

    public MoCMessageHealth() {}

    public MoCMessageHealth(int entityId, float health)
    {
        this.entityId = entityId;
        this.health = health;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeFloat(health);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        entityId = buffer.readInt();
        health = buffer.readFloat();
    }

    @Override
    public IMessage onMessage(MoCMessageHealth message, MessageContext context)
    {
        List<Entity> entityList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof EntityLiving)
            {
                ((EntityLiving) entity).setHealth(message.health);
                break;
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageHealth - entityId:%s, health:%s", entityId, health);
    }
}