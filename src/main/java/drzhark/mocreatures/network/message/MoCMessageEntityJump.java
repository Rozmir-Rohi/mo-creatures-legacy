package drzhark.mocreatures.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.entity.IMoCEntity;
import io.netty.buffer.ByteBuf;

public class MoCMessageEntityJump implements IMessage, IMessageHandler<MoCMessageEntityJump, IMessage> {

    public MoCMessageEntityJump() {}

    @Override
    public void toBytes(ByteBuf buffer)
    {
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
    }

    @Override
    public IMessage onMessage(MoCMessageEntityJump message, MessageContext context)
    {
        if (context.getServerHandler().playerEntity.ridingEntity != null && context.getServerHandler().playerEntity.ridingEntity instanceof IMoCEntity)
        {
            ((IMoCEntity) context.getServerHandler().playerEntity.ridingEntity).makeEntityJump();
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageEntityJump");
    }
}