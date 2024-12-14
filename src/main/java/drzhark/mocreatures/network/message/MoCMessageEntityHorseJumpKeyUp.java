package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MoCMessageEntityHorseJumpKeyUp implements IMessage, IMessageHandler<MoCMessageEntityHorseJumpKeyUp, IMessage> {
 
	public MoCMessageEntityHorseJumpKeyUp()
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
    public IMessage onMessage(MoCMessageEntityHorseJumpKeyUp message, MessageContext context)
    {
    	EntityPlayerMP entityPlayerMP = context.getServerHandler().playerEntity;
    	
        if (entityPlayerMP.ridingEntity != null && entityPlayerMP.ridingEntity instanceof MoCEntityHorse)
    	{
        	((MoCEntityHorse) entityPlayerMP.ridingEntity).setJumpKeyDown(false);
    	}

        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageEntityHorseJumpKeyUp");
    }
}