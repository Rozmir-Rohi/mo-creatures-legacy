package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityFox extends MoCEntityTameableAnimal {
    protected double attackRange;
    protected int attackDamage;

    public MoCEntityFox(World world)
    {
        super(world);
        setSize(0.9F, 1.3F);
        attackDamage = 2;
        attackRange = 4D;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
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
        return !getIsTamed() && this.ticksExisted > 2400;
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.0D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        }
    }

    @Override
    public void selectType()
    {
        checkSpawningBiome();
        
        if (getType() == 0)
        {
            setType(1);
        }
    }   

    @Override
    public ResourceLocation getTexture()
    {

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("fox.png");
        case 2:
            return MoCreatures.proxy.getTexture("foxsnow.png");

        default:
            return MoCreatures.proxy.getTexture("fox.png");
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
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        
        if ((itemstack != null) && 
        		(//taming items
        			itemstack.getItem() == MoCreatures.rawTurkey
        			|| MoCreatures.isGregTech6Loaded &&
        				(
        					OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodScrapmeat"
        				)
        		)
        	)
        {
            if (--itemstack.stackSize == 0)
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
    public boolean isNotScared()
    {
        return true;
    }
    
    @Override
    public boolean entitiesToIgnoreWhenLookingForAnEntityToAttack(Entity entity)
    {
        return (super.entitiesToIgnoreWhenLookingForAnEntityToAttack(entity) //including the mobs specified in parent file
            	|| (entity instanceof MoCEntityFox)
            	|| ((entity.width > 0.5D) && (entity.height > 0.5D)) //don't try to hunt creatures larger than it
            	|| (entity instanceof MoCEntityKomodo)
            	|| (entity instanceof MoCEntityJellyFish || entity instanceof MoCEntityRay || entity instanceof EntitySquid)
                || (getIsTamed() && (entity instanceof IMoCEntity) && ((IMoCEntity)entity).getIsTamed() ) 
        		);
    }


    @Override
    protected Entity findPlayerToAttack()
    {
        if ((rand.nextInt(80) == 0) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            EntityLivingBase entityLiving = getClosestEntityLiving(this, 8D);
            
            if (entityLiving instanceof MoCEntityAquatic || entityLiving instanceof MoCEntityTameableAquatic) // don't go and hunt fish if they are in the ocean
            {
            	int x = MathHelper.floor_double(entityLiving.posX);
                int y = MathHelper.floor_double(entityLiving.posY);
                int z = MathHelper.floor_double(entityLiving.posZ);

                BiomeGenBase biomeThatPreyIsIn = MoCTools.Biomekind(worldObj, x, y, z);

                if (BiomeDictionary.isBiomeOfType(biomeThatPreyIsIn, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biomeThatPreyIsIn, Type.BEACH))
                {
                	return null;
                }
            }
            
            else {return entityLiving;}
        }
        return null;
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        
        BiomeGenBase currentBiome = MoCTools.Biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SNOWY))
        {
            setType(2);
        }
        return true;
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:foxdying";
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.fur;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:foxhurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:foxcall";
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.3F;
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
    	return itemstack != null && isItemEdible(itemstack.getItem());
    }

    @Override
    public int nameYOffset()
    {
        return -50;
    }

    @Override
    public double roperYOffset()
    {
        return 0.8D;
    }
    
    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }
}