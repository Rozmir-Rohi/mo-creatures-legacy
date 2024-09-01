package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
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
        			itemstack.getItem() == MoCreatures.turkeyRaw
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
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return
        		(
        			super.shouldEntityBeIgnored(entity) //including the mobs specified in parent file
        			|| (entity instanceof MoCEntityFox)
        			|| ((entity.width > 0.5D) && (entity.height > 0.5D)) //don't try to hunt creatures larger than a chicken
        			|| (entity instanceof MoCEntityKomodoDragon)
        			|| (entity instanceof MoCEntityJellyFish || entity instanceof MoCEntityRay || entity instanceof EntitySquid)
        			|| (getIsTamed() && (entity instanceof IMoCEntity) && ((IMoCEntity)entity).getIsTamed() ) 
        		);
    }


    @Override
    protected Entity findPlayerToAttack()
    {
        if ((rand.nextInt(80) == 0) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            EntityLivingBase closestEntityLiving = getClosestEntityLiving(this, 8D);
            
            if (shouldEntityBeIgnored(closestEntityLiving))
            {
            	return null;
            }
            
            if (closestEntityLiving != null && !MoCTools.isEntityAFishThatIsInTheOcean(closestEntityLiving))
            {
            	return closestEntityLiving;
            }
        }
        return null;
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        
        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);

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
    	if (itemstack != null)
    	{
    		Item item = itemstack.getItem();
    		
    		List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
    	
	    	return 
	    			(
	    					isItemEdible(item)
			        		|| item == Items.porkchop
			    			|| item == Items.beef 
			    			|| item == Items.chicken
			    			|| (item == Items.fish && itemstack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
			    			|| item == MoCreatures.ratRaw
			        		|| item == MoCreatures.turkeyRaw
			            	|| item == MoCreatures.ostrichRaw
			        		|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:rabbit_raw")
			    			|| oreDictionaryNameArray.contains("listAllmeatraw")
			    			|| oreDictionaryNameArray.contains("listAllfishraw")
			    			|| MoCreatures.isGregTech6Loaded &&
			    			(
			    					oreDictionaryNameArray.contains("foodScrapmeat")
							)
						);
    	}
    	
    	return false;
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