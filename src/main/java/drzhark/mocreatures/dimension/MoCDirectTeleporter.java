package drzhark.mocreatures.dimension;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class MoCDirectTeleporter extends Teleporter 
{
    private boolean portalDone;
    
    public MoCDirectTeleporter(WorldServer worldServer) 
    {
        super(worldServer);
    }

    @Override
    public void placeInPortal(Entity entity, double par2X, double par4Y, double par6Z, float par8)
    {
        int var9 = MathHelper.floor_double(entity.posX);
        int var10 = MathHelper.floor_double(entity.posY) - 1;
        int var11 = MathHelper.floor_double(entity.posZ);
        entity.setLocationAndAngles(var9, var10, var11, entity.rotationYaw, 0.0F);
        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
    }
    
    public void createPortal(World world, Random par2Random)
    {
        MoCWorldGenPortal myPortal = new MoCWorldGenPortal(Blocks.quartz_block, 2, Blocks.quartz_stairs, 0, Blocks.quartz_block, 1, Blocks.quartz_block, 0);
        for (int i = 0; i< 14; i++)
        {
            if (!portalDone)
            {
                int randPosY = 58 + i;//par2Random.nextInt(8);
                portalDone = myPortal.generate(world, par2Random, 0, randPosY, 0);
            }
        }
    }
}