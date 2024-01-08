package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityShark extends MoCEntityTameableAquatic {
    public MoCEntityShark(World world)
    {
        super(world);
        texture = "shark.png";
        setSize(1.5F, 0.8F);
        setMoCAge(100 + rand.nextInt(100));
        this.tasks.addTask(4, new EntityAIAvoidEntity(this, MoCEntityDolphin.class, 8.0F, 0.6D, 0.6D)); // run away from dolphins, not sure if this is working though
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
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
                Entity playerMount = ((EntityPlayer)entity).ridingEntity;
                if (playerMount instanceof EntityBoat) 
                {
                    return;
                }
            }
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5);
            
            if (!(entity instanceof EntityPlayer))
            {
                MoCTools.destroyDrops(this, 3D);
            }
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
                    ))   
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
        int i = rand.nextInt(100);
        if (i < 90)
        {
            int j = rand.nextInt(3) + 1;
            for (int l = 0; l < j; l++)
            {
                entityDropItem(new ItemStack(MoCreatures.sharkteeth, 1, 0), 0.0F);
            }
        }
        else if ((worldObj.difficultySetting.getDifficultyId() > 0) && (getMoCAge() > 150))
        {
            int k = rand.nextInt(3);
            for (int i1 = 0; i1 < k; i1++)
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
            EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
            if ((entityplayer != null) && entityplayer.isInWater() && !getIsTamed()) { return entityplayer; }
            
            if (rand.nextInt(200) == 0)  // hunting cooldown between each prey
            {
                EntityLivingBase entityliving = getClosestEntityLiving(this, 16D);
                if ((entityliving != null) && !(entityliving instanceof EntityPlayer))
                	{ return entityliving; }
            }
        }
        return null;
    }

    public EntityLivingBase getClosestEntityLiving(Entity entity, double d)
    {
        double d1 = -1D;
        EntityLivingBase entityliving = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if (!(entity1 instanceof EntityLivingBase)
            		|| (!((entity1 instanceof MoCEntityAquatic) || (entity1 instanceof MoCEntityTameableAquatic)) && !(entity1.isInWater()) // don't attack if mob is not aquatic and not in water
                    || (entity1 instanceof MoCEntityShark) // don't attack mobs below as well
                    || (entity1 == entity.riddenByEntity) 
                    || (entity1 == entity.ridingEntity)
                    || (entity1 instanceof IMob || entity1 instanceof EntityMob || entity1 instanceof MoCEntityMob) // don't attack if creature is a mob (eg: slime)
                    || (entity1 instanceof MoCEntityDolphin || entity1 instanceof MoCEntityJellyFish)
                    || (getIsTamed() && (entity1 instanceof IMoCEntity) && ((IMoCEntity)entity1).getIsTamed() ) 
                    || ((entity1 instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
                    || ((entity1 instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)))
            	)
            	
            	{ continue;}
            
            double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            
            if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)) && ((EntityLivingBase) entity1).canEntityBeSeen(entity))
            {
                d1 = d2;
                entityliving = (EntityLivingBase) entity1;
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
    public boolean isMyHealFood(ItemStack par1ItemStack)
    {
        return par1ItemStack != null && (par1ItemStack.getItem() == Items.fish);
    }
}