package drzhark.mocreatures.entity;

import java.util.List;

import drzhark.mocreatures.MoCreatures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityItemPlaceable extends EntityLiving {
	
	public long punchCooldown;
	
	public MoCEntityItemPlaceable(World world) 
	{
		super(world);
	}
	
	 @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (MoCreatures.isServer()) {
           if (DamageSource.outOfWorld.equals(source))
           {
              setDead();
              return false;
           }
           
           else if (isEntityInvulnerable()) {return false;}
           
           else if (source.isExplosion())
           {
              dropItemEntity();
              setDead();
              return false;
           }
           else if (DamageSource.inFire.equals(source))
           {
        	   
              if (!isBurning()) {setFire(5);} 
              else {damageItemEntity(0.15F);}

              return false;
           }
           else if (DamageSource.onFire.equals(source) && getHealth() > 0.5F)
           {
              damageItemEntity(4.0F);
              return false;
           }
           else
           {
              boolean flag = "arrow".equals(source.getDamageType());
              boolean flag1 = "player".equals(source.getDamageType());
              if (!flag1 && !flag) {return false;}
              else
              {
            	  
                 if (source.getSourceOfDamage() instanceof EntityArrow)
                 {
                    source.getSourceOfDamage().setDead();
                 }

                 if (source.getEntity() instanceof EntityPlayer
                		 && !((EntityPlayer)source.getEntity()).capabilities.allowEdit)
                 {
                    return false;
                 }
                 else if (source.getEntity() instanceof EntityPlayer
                		 && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode)
                 {
                    setDead();
                    return false;
                 }
                 else
                 {
                    long i = worldObj.getTotalWorldTime();
                    if (i - punchCooldown > 5L && !flag)
                    {
                       punchCooldown = i;
                    }
                    else
                    {
                       dropItemEntity();
                       setDead();
                    }
                    return false;
                 }
              }
           }
        }
        else {return false;}
     }
	
	 
	 private void damageItemEntity(float decreasHealthAmount) {
	        float itemEntityHealth = getHealth();
	        itemEntityHealth -= decreasHealthAmount;
	        if (itemEntityHealth <= 0.5F) {
	           setDead();
	        } else {
	           setHealth(itemEntityHealth);
	        }

	     }
	     
	 
	public void dropItemEntity() {
		if (MoCreatures.isServer())
	    {
			entityDropItem(new ItemStack(Items.stick), 0F);   // <--- Default drop as placeholder, this is ment to be overridden by the child classes
	    }
	}

	public EntityItem getClosestEntityItem(Entity entity, double d)
    {
        double d1 = -1D;
        EntityItem entityItem = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
        for (int k = 0; k < list.size(); k++)
        {
            Entity entity1 = (Entity) list.get(k);
            if (!(entity1 instanceof EntityItem))
            {
                continue;
            }
            EntityItem entityItem1 = (EntityItem) entity1;
            double d2 = entityItem1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)))
            {
                d1 = d2;
                entityItem = entityItem1;
            }
        }

        return entityItem;
    }
}
