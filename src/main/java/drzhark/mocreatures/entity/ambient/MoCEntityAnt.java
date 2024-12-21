package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MoCEntityAnt extends MoCEntityInsect{

    public MoCEntityAnt(World world)
    {
        super(world);
        texture = "ant.png";
    }

     @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // foundFood 0 = false, 1 = true
    }

    public boolean getHasFood()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setHasFood(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (!getHasFood())
            {
                EntityItem entityItem = getClosestFood(this, 8D);
                if (entityItem != null && entityItem.ridingEntity == null)
                {
                    float distanceToEntityItem = entityItem.getDistanceToEntity(this);
                    
                    if (distanceToEntityItem > 1.0F)
                    {
                        int entityItemPosX = MathHelper.floor_double(entityItem.posX);
                        int entityItemPosY = MathHelper.floor_double(entityItem.posY);
                        int entityItemPosZ = MathHelper.floor_double(entityItem.posZ);
                        MoCTools.faceLocation(this, entityItemPosX, entityItemPosY, entityItemPosZ, 30F);

                        getMyOwnPath(entityItem, distanceToEntityItem);
                        return;
                    }
                    if ((distanceToEntityItem < 1.0F) && (entityItem != null))
                    {
                        exchangeItem(entityItem);
                        setHasFood(true);
                        return;
                    }
                }
            }

            

        }
        
        if (getHasFood())
        {
            if (riddenByEntity == null)
            {
                EntityItem entityItem = getClosestFood(this, 2D);
                if (entityItem != null && entityItem.ridingEntity == null)
                {
                    entityItem.mountEntity(this);
                    return;
                    
                }
                
                if (riddenByEntity == null)
                {
                    setHasFood(false);
                }
            }
        }
    }

    private void exchangeItem(EntityItem entityItem)
    {
        EntityItem cargo = new EntityItem(worldObj, posX, posY+0.2D, posZ, entityItem.getEntityItem());
        entityItem.setDead();
        if (MoCreatures.isServer()) worldObj.spawnEntityInWorld(cargo);
    }
    @Override
    public boolean getIsFlying()
    {
        return false;
    }


    @Override
    protected float getWalkingSpeed()
    {
        return 0.8F;
    }

    @Override
    public boolean isMyFollowFood(ItemStack itemStack)
    {
        return itemStack != null && isItemEdible(itemStack.getItem());
    }

    
    @Override
	protected int getFlyingFreq()
    {
        return 5000;
    }
}
