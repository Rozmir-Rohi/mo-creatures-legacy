package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class MoCEntityFirefly extends MoCEntityInsect
{
    private int soundCount;

    public MoCEntityFirefly(World world)
    {
        super(world);
        texture = "firefly.png";
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 5D);
            if (entityPlayer != null && getIsFlying() && --soundCount == -1)
            {
                MoCTools.playCustomSound(this, "cricketfly", worldObj);
                soundCount = 20;
            }

            if (getIsFlying() && rand.nextInt(500) == 0)
            {
                setIsFlying(false);
            }
        }
    }

    @Override
    protected float getFlyingSpeed()
    {
        return 0.3F;
    }

    @Override
    protected float getWalkingSpeed()
    {
        return 0.2F;
    }
}