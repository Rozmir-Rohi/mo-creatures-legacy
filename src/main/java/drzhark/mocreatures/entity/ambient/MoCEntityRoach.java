package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCEntityRoach extends MoCEntityInsect

{
    public MoCEntityRoach(World world)
    {
        super(world);
        texture = "roach.png";
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {

            if (getIsFlying() && rand.nextInt(50) == 0)
            {
                setIsFlying(false);
            }

            if (!getIsFlying() && rand.nextInt(10) == 0)
            {
                EntityLivingBase entityLiving = MoCTools.getScaryEntity(this, 3D);
                if (entityLiving != null)
                {
                    runLikeHell(entityLiving);
                }
            }
        }
    }

    @Override
    public boolean entitiesThatAreScary(Entity entity)
    {
        return !(entity instanceof MoCEntityInsect) && super.entitiesThatAreScary(entity);
    }

    @Override
    protected float getFlyingSpeed()
    {
        return 0.2F;
    }

    @Override
    protected float getWalkingSpeed()
    {
        return 0.8F;
    }

    @Override
    public boolean isMyFollowFood(ItemStack itemStack)
    {
        return itemStack != null && itemStack.getItem() == Items.rotten_flesh;
    }
    
    @Override
    public boolean doesForageForFood()
    {
		return true;
	}

    @Override
    public boolean getIsFlying()
    {    
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    @Override
    protected int getFlyingFreq()
    {
        return 300;
    }
}