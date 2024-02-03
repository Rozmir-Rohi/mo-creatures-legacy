package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityShark extends MoCEntityTameableAquatic {
	
    public MoCEntityShark(World world)
    {
        super(world);
        texture = "shark.png";
        setSize(1.5F, 0.8F);
        setMoCAge(100 + rand.nextInt(100));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
    }
    
    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && this.ticksExisted > 2400;
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
        if ((f < 3.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY) && (getMoCAge() >= 100))
        {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).ridingEntity != null)
            {
                Entity entity_that_player_is_riding = ((EntityPlayer)entity).ridingEntity;
                if (entity_that_player_is_riding instanceof EntityBoat) 
                {
                    return;
                }
            }
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        if (super.attackEntityFrom(damagesource, i) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            Entity entity = damagesource.getEntity();
            if ((riddenByEntity == entity) || (ridingEntity == entity)) { return true; }
            
            if (entity != this
            	&& !( //don't attack back if the attacking mob is one of the following mobs below
            			entity instanceof EntityMob   //this also stops sharks from fighting Guardians from the Village Names mod
                    )
            	)   
            {
                entityToAttack = entity;
                return true;
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int drop_chance = rand.nextInt(100);
        if (drop_chance < 90)
        {
            int amount_of_teeth_to_drop = rand.nextInt(3) + 1;
            
            for (int index = 0; index < amount_of_teeth_to_drop; index++)
            {
                entityDropItem(new ItemStack(MoCreatures.sharkteeth, 1, 0), 0.0F);
            }
        }
        else if ((worldObj.difficultySetting.getDifficultyId() > 0) && (getMoCAge() > 150) && drop_chance < 40)
        {
            int amount_of_eggs_to_drop = rand.nextInt(3);
            for (int index1 = 0; index1 < amount_of_eggs_to_drop; index1++)
            {
                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, 11), 0.0F);
            }
        }
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if ((worldObj.difficultySetting.getDifficultyId() > 0) && (getMoCAge() >= 100))
        {
            EntityPlayer closest_entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
            if ((closest_entityplayer != null) && closest_entityplayer.isInWater() && !getIsTamed()) { return closest_entityplayer; }
            
            if (rand.nextInt(200) == 0)  // hunting cooldown between each prey
            {
                EntityLivingBase entityliving = getClosestEntityLivingThatCanBeHunted(this, 16D);
                if ((entityliving != null) && !(entityliving instanceof EntityPlayer)) { return entityliving; }
            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
        {
	        if (this.getIsTamed()) //defend owner if they are attacked by an entity
	    	{
	    		EntityPlayer owner_of_entity_that_is_online = MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.getOwnerName());
	    		
	    		if (owner_of_entity_that_is_online != null)
	    		{
	    			EntityLivingBase entity_that_attacked_owner = owner_of_entity_that_is_online.getAITarget();
	    			
	    			if (entity_that_attacked_owner != null)
	    			{
	    				return entity_that_attacked_owner;
	    			}
	    		}
	    	}
        }
        return null;
    }

    public EntityLivingBase getClosestEntityLivingThatCanBeHunted(Entity entity, double distance)
    {
        double current_minimum_distance = -1D;
        EntityLivingBase entityliving = null;
        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
        int iteration_length = entities_nearby_list.size();
        
        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);
	            
	            if (!(entity_nearby instanceof EntityLivingBase)
	            		|| (!((entity_nearby instanceof MoCEntityAquatic) || (entity_nearby instanceof MoCEntityTameableAquatic)) && !(entity_nearby.isInWater()) // don't attack if mob is not aquatic and not in water
	                    || (entity_nearby instanceof MoCEntityShark) // don't attack mobs below as well
	                    || (entity_nearby == entity.riddenByEntity) 
	                    || (entity_nearby == entity.ridingEntity)
	                    || (entity_nearby instanceof IMob || entity_nearby instanceof EntityMob || entity_nearby instanceof MoCEntityMob) // don't attack if creature is a mob (eg: slime)
	                    || (entity_nearby instanceof MoCEntityDolphin || entity_nearby instanceof MoCEntityJellyFish)
	                    || (getIsTamed() && (entity_nearby instanceof IMoCEntity) && ((IMoCEntity)entity_nearby).getIsTamed() ) 
	                    || ((entity_nearby instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
	                    || ((entity_nearby instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)))
	            	)
	            	
	            	{ continue;}
	            
	            double overall_distance_squared = entity_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)) && ((EntityLivingBase) entity_nearby).canEntityBeSeen(entity))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityliving = (EntityLivingBase) entity_nearby;
	            }
	        }
        }
        
        return entityliving;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!worldObj.isRemote)
        {
            if (!getIsAdult() && (rand.nextInt(50) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 200)
                {
                    setAdult(true);
                }
            }
        }
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName();
    }

    @Override
    public void setDead()
    {
        if (!worldObj.isRemote && getIsTamed() && (getHealth() > 0))
        {
            return;
        }
        else
        {
            super.setDead();
            return;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return itemstack != null && 
        		(
        			itemstack.getItem() == Items.fish
        		);
    }
}