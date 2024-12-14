package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MoCMessageEntityHorseJumpKeyDown implements IMessage, IMessageHandler<MoCMessageEntityHorseJumpKeyDown, IMessage> {
 
	public MoCMessageEntityHorseJumpKeyDown()
    {
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
    }

    @Override
    public IMessage onMessage(MoCMessageEntityHorseJumpKeyDown message, MessageContext context)
    {
    	EntityPlayerMP entityPlayerMP = context.getServerHandler().playerEntity;
    	
        if (entityPlayerMP.ridingEntity != null && entityPlayerMP.ridingEntity instanceof MoCEntityHorse)
    	{
        	((MoCEntityHorse) entityPlayerMP.ridingEntity).setJumpKeyDown(true);
    	}

        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageEntityHorseJumpKeyDown");
    }
}