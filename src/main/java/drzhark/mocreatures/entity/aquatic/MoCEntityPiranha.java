package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAmbient;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityPiranha extends MoCEntitySmallFish{

    public static final String fishNames[] = { "Piranha"};

    public MoCEntityPiranha(World world)
    {
        super(world);
        setSize(0.3F, 0.3F);
        setMoCAge(30 + rand.nextInt(70));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }

    @Override
    public void selectType()
    {
        setType(1);
    }

    public ResourceLocation getTexture()
    {
       return MoCreatures.proxy.getTexture("smallfish_piranha.png");
    }
    
    protected EntityLivingBase getClosestEntityLiving(Entity entity, double d)
    {
        double d1 = -1D;
        EntityLivingBase entityliving = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);

            if ( (!(entity1.isInWater()) // do not hunt if entity is not in water
            		|| !(entity1 instanceof EntityLivingBase) // do not hunt the following mobs below
                    || (entity1 == entity) 
                    || (entity1 == entity.riddenByEntity) 
                    || (entity1 == entity.ridingEntity) 
                    || (entity1 instanceof MoCEntityAmbient)
                    || (entity1 instanceof MoCEntityKittyBed || entity1 instanceof MoCEntityLitterBox)
                    || (entity1 instanceof IMob || entity1 instanceof MoCEntityMob) 
                    || (entity1 instanceof EntityPlayer)
                    || (getIsTamed() && (entity1 instanceof IMoCEntity) && ((IMoCEntity)entity1).getIsTamed() ) // do not hunt if this is tamed and the target is a tamed MOC mob
                    || ((entity1 instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
                    || ((entity1 instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves))
            		)
            	)
            {
                continue;
            }
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
    protected Entity findPlayerToAttack()
    {
        if ((worldObj.difficultySetting.getDifficultyId() > 0))
        {
            EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 12D);
            if ((entityplayer != null) && entityplayer.isInWater() && !getIsTamed()) { return entityplayer; }
            
            if (rand.nextInt(80) == 0)
            {
            	EntityLivingBase entityliving = getClosestEntityLiving(this, 4D);
                return entityliving;
            }
        }
        return null;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        if (super.attackEntityFrom(damagesource, i) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            Entity entity = damagesource.getEntity();
            if ((riddenByEntity == entity) || (ridingEntity == entity)) { return true; }
            if (entity != this)
            {
                entityToAttack = entity;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isNotScared()
    {
        return true;
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
        if (entity.isInWater() && (f < 0.8D))
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
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int i = rand.nextInt(100);
        if (i < 70)
        {
            entityDropItem(new ItemStack(Items.fish, 1, 0), 0.0F);
        }
        else
        {
            int j = rand.nextInt(2);
            for (int k = 0; k < j; k++)
            {
                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, 90), 0.0F); 
            }
        }
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