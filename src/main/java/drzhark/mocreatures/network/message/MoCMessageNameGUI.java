package drzhark.mocreatures.network.message;

import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.client.gui.helpers.MoCGUIEntityNamer;
import drzhark.mocreatures.entity.IMoCEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

public class MoCMessageNameGUI implements IMessage, IMessageHandler<MoCMessageNameGUI, IMessage> {

    int entityId;

    public MoCMessageNameGUI() {}

    public MoCMessageNameGUI(int entityId)
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
    public IMessage onMessage(MoCMessageNameGUI message, MessageContext context)
    {
        if (context.side == Side.CLIENT)
        {
            handleClientMessage(message, context);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void handleClientMessage(MoCMessageNameGUI message, MessageContext context)
    {
        List<Entity> entityList = MoCClientProxy.mc.thePlayer.worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof IMoCEntity)
            {
                MoCClientProxy.mc.displayGuiScreen(new MoCGUIEntityNamer(((IMoCEntity) entity), ((IMoCEntity) entity).getName()));
                break;
            }
        }
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageNameGUI - entityId:%s", entityId);
    }
}