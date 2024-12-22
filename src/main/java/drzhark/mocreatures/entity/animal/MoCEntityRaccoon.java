package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityRaccoon extends MoCEntityTameableAnimal{

    public MoCEntityRaccoon(World world)
    {
        super(world);
        setSize(0.8F, 0.8F);
        texture = "raccoon.png";
        setMoCAge(70 + rand.nextInt(30));
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }
    
    @Override
    public boolean doesForageForFood()
    {
    	return true;
    }
    
    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && ticksExisted > 2400;
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.0D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            
            if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName()))))
            { 
            	return false; 
            }
            
            if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        ItemStack itemStack = entityPlayer.getHeldItem();
        if ((itemStack != null) && isItemEdible(itemStack.getItem()))
        {
            if (--itemStack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            if (MoCreatures.isServer() && !getIsTamed())
            {
                MoCTools.tameWithName(entityPlayer, this);
            }
            
            heal(5);

            if (MoCreatures.isServer() && !getIsAdult() && (getMoCAge() < 100))
            {
                setMoCAge(getMoCAge() + 1);
            }

            return true;
        }
        return false;
    }
    
    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return 
        		(
        			super.shouldEntityBeIgnored(entity) //including the mobs specified in parent file
                    || entity instanceof MoCEntityRaccoon
                    || entity instanceof MoCEntityFox
                    || !getIsAdult() 
                    || ((entity.width > 0.5D) || (entity.height > 0.5D))
        		);
    }
    

    @Override
    protected Entity findPlayerToAttack()
    {
        if ((rand.nextInt(80) == 0) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            EntityLivingBase entityLiving = MoCTools.getClosestEntityLivingThatCanBeTargetted(this, 8D);
            return entityLiving;
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public boolean isMyHealFood(ItemStack itemStack)
    {
    	return itemStack != null && isItemEdible(itemStack.getItem());
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:raccoondying";
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.fur;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:raccoonhurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:raccoongrunt";
    }

    @Override
    public int nameYOffset()
    {
        return -30;
    }

    @Override
    public double roperYOffset()
    {
        return 1.15D;
    }

    @Override
    public float getSizeFactor() 
    {   
        return 0.8F * getMoCAge() * 0.01F;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (!getIsAdult() && rand.nextInt(300) == 0)
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                }
            }
        }
    }
    
    @Override
    public int getTalkInterval()
    {
        return 400;
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 2;
    }
}