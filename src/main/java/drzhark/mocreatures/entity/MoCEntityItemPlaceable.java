package drzhark.mocreatures.entity;

import drzhark.mocreatures.MoCreatures;
import net.minecraft.entity.EntityLiving;
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
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken) {
        if (MoCreatures.isServer()) {
           if (DamageSource.outOfWorld.equals(damageSource))
           {
              setDead();
              return false;
           }
           
           else if (isEntityInvulnerable()) {return false;}
           
           else if (damageSource.isExplosion())
           {
              dropItemEntity();
              setDead();
              return false;
           }
           else if (DamageSource.inFire.equals(damageSource))
           {
        	   
              if (!isBurning()) {setFire(5);} 
              else {damageItemEntity(0.15F);}

              return false;
           }
           else if (DamageSource.onFire.equals(damageSource) && getHealth() > 0.5F)
           {
              damageItemEntity(4.0F);
              return false;
           }
           else
           {
              boolean sourceOfDamageIsArrow = "arrow".equals(damageSource.getDamageType());
              boolean sourceOfDamageIsFromPlayer = "player".equals(damageSource.getDamageType());
              if (!sourceOfDamageIsFromPlayer && !sourceOfDamageIsArrow) {return false;}
              else
              {
            	  
                 if (damageSource.getSourceOfDamage() instanceof EntityArrow)
                 {
                    damageSource.getSourceOfDamage().setDead();
                 }

                 if (damageSource.getEntity() instanceof EntityPlayer
                		 && !((EntityPlayer)damageSource.getEntity()).capabilities.allowEdit)
                 {
                    return false;
                 }
                 else if (damageSource.getEntity() instanceof EntityPlayer
                		 && ((EntityPlayer)damageSource.getEntity()).capabilities.isCreativeMode)
                 {
                    setDead();
                    return false;
                 }
                 else
                 {
                    long i = worldObj.getTotalWorldTime();
                    if (i - punchCooldown > 5L && !sourceOfDamageIsArrow)
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
	
	 
	 private void damageItemEntity(float decreaseHealthAmount)
	 {
	        float itemEntityHealth = getHealth();
	        itemEntityHealth -= decreaseHealthAmount;
	        if (itemEntityHealth <= 0.5F) {
	           setDead();
	        } else {
	           setHealth(itemEntityHealth);
	        }

	     }
	     
	 
	public void dropItemEntity()
	{
		if (MoCreatures.isServer())
	    {
			entityDropItem(new ItemStack(Items.stick), 0F);   // <--- Default drop as placeholder, this is ment to be overridden by the child classes
	    }
	}
}
