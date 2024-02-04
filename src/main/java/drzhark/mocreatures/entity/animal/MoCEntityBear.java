package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import drzhark.mocreatures.entity.aquatic.MoCEntityShark;
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

public class MoCEntityBear extends MoCEntityTameableAnimal {

    public int mouthCounter;
    private int attackCounter;
    private int standingCounter;

    public MoCEntityBear(World world)
    {
        super(world);
        setSize(1.2F, 1.5F);
        roper = null;
        setMoCAge(55);
        //attackRange = 16D;
        if (rand.nextInt(4) == 0)
        {
            setAdult(false);

        }
        else
        {
            setAdult(true);
        }
    }
    
    @Override
    public boolean doesForageForFood()
    {
    	return true; //all bear types will eat items from the ground if the item is their food item
    }

    /**
     * Initializes datawatchers for entity. Each datawatcher is used to sync
     * server data to client.
     */
    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(23, Byte.valueOf((byte)0));
    }
    
    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && this.ticksExisted > 2400;
    }

    /**
     * 0 - bear is on fours 1 - standing 2 - sitting
     * 
     * @return
     */
    public int getBearState()
    {
        return dataWatcher.getWatchableObjectByte(23);
    }

    public void setBearState(int i)
    {
        dataWatcher.updateObject(23, Byte.valueOf((byte)i));
    }

    @Override
    public void selectType()
    {
        checkSpawningBiome(); //apply type from the biome it spawns in
        
        if (getType() == 0) // if type is still 0, make it a brown bear or black bear 
        {

            int i = rand.nextInt(100);
            if (i <= 40)
            {
                setType(1);
            }
            else
            {
                setType(2);
            }
        }
        
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        this.setHealth(getMaxHealth());
    }
    
    @Override
    public boolean isPredator()
    {
    	return this.getType() != 3; //not a panda
    }

    @Override
    public ResourceLocation getTexture()
    {

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("bearbrowm.png");
        case 2:
            return MoCreatures.proxy.getTexture("bearblack.png");
        case 3:
            return MoCreatures.proxy.getTexture("bearpanda.png");
        case 4:
            return MoCreatures.proxy.getTexture("bearpolar.png");

        default:
            return MoCreatures.proxy.getTexture("bearbrowm.png");
        }
    }

    /**
     * Returns the factor size for the bear, polars are bigger and pandas
     * smaller
     * 
     * @return
     */
    public float getBearSize()
    {
        switch (getType())
        {
        case 1:
            return 1.2F;
        case 2:
            return 0.9F;
        case 3:
            return 0.8F;
        case 4:
            return 1.4F;

        default:
            return 1.0F;
        }
    }

    public float calculateMaxHealth()
    {
        switch (getType())
        {
	        case 1:
	            return 20;
	        case 2:
	            return 15;
	        case 3:
	            return 15;
	        case 4:
	            return 25;
	
	        default:
	            return 20;
        }
    }

    /**
     * Returns the distance at which the bear attacks prey
     * 
     * @return
     */
    public double getAttackRange()
    {
        int factor = 1;
        if (worldObj.difficultySetting.getDifficultyId() > 1)
        {
            factor = 2;
        }

        switch (getType())
        {
        case 1:
            return 6D * factor;
        case 2:
            return 6D * factor;
        case 3:
            return 1D;
        case 4:
            return 8D * factor;

        default:
            return 8D;
        }
    }

    /**
     * The damage the bear does
     * 
     * @return
     */
    public int getAttackStrength()
    {
        int world_difficulty = (worldObj.difficultySetting.getDifficultyId());

        switch (getType())
        {
        case 1:
            return 2 * world_difficulty;
        case 2:
            return 1 * world_difficulty;
        case 3:
            return 1;
        case 4:
            return 3 * world_difficulty;

        default:
            return 2;
        }
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
        if (attackTime <= 0 && (f < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            startAttack();
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength());
        }
    }

    /**
     * Checks if bear is sitting.
     */
    @Override
    protected boolean isMovementCeased()
    {
        return getBearState() == 2;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
    	if (super.attackEntityFrom(damagesource, i))
        {
    		if (!(this.getIsAdult()) && (damagesource.getEntity() != null))
        	{
    			if (getBearState() == 2) //if sitting
    			{
    				setBearState(0);
    			}
    			
    			MoCTools.runLikeHell(this, damagesource.getEntity()); //child runs away from attacking entity
    			
    			List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(10D, 10D, 10D));
    			
    			int iteration_length = entities_nearby_list.size();
    			
    			if (iteration_length > 0)
    			{
	    			for (int index = 0; index < iteration_length; index++)
	    	        {
	    	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);
		                if (entity_nearby instanceof MoCEntityBear) //set attack target of adults near by to the entity that is attacking that child
		                {
		                	MoCEntityBear bear_nearby = (MoCEntityBear) entity_nearby;
		                	if (bear_nearby.getIsAdult());
		                	{	
		                		if (!(bear_nearby.getIsTamed()) && (bear_nearby.getType() == this.getType()))
		                		{
		                			if (bear_nearby.getBearState() == 2) //if sitting
		                			{
		                				bear_nearby.setBearState(0);
		                			}
		                			
		                			bear_nearby.entityToAttack = damagesource.getEntity();
		                		}
		                	}
		                	
		                	continue; 
		                }
		                
		                else {continue;}
		                
		                
	    	        }
    			}
    			
    			
        		return false;
        	}
    		else
            {
    			Entity entity_that_attacked_this_creature = damagesource.getEntity();
    			
    			if (entity_that_attacked_this_creature != null && getIsTamed() && (entity_that_attacked_this_creature instanceof EntityPlayer && (entity_that_attacked_this_creature.getCommandSenderName().equals(getOwnerName()))))
                { 
                	return false; 
                }
    			
    			if ((riddenByEntity == entity_that_attacked_this_creature) || (ridingEntity == entity_that_attacked_this_creature)) { return true; }
    			if ((entity_that_attacked_this_creature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
    			{
    				entityToAttack = entity_that_attacked_this_creature;
        			
        			if (getBearState() == 2) //if sitting
        			{
        				setBearState(0);
        			}
    			}
    			return true;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean isNotScared()
    {
        return getType() != 3;
    }
    
    @Override
    public boolean entitiesToIgnoreWhenHunting(Entity entity)
    {
        return (
	        		super.entitiesToIgnoreWhenHunting(entity) //including the mobs specified in parent file
	            	|| (entity instanceof MoCEntityBear) 
	            	|| (getIsAdult() && (entity.width > 1.3D && entity.height > 1.3D)) // don't try to hunt creature larger than a deer when adult
	                || (!getIsAdult() && (entity.width > 0.5D && entity.height > 0.5D)) // don't try to hunt creature larger than a chicken when child
	            	|| (entity instanceof MoCEntityBigCat)
	            	|| (entity instanceof MoCEntityShark)
	            	|| (entity instanceof MoCEntityJellyFish || entity instanceof MoCEntityRay || entity instanceof EntitySquid)
            	);
    }
    
    

    @Override
    protected Entity findPlayerToAttack()
    {
        if (worldObj.difficultySetting.getDifficultyId() > 0)
        {
            float brightness = getBrightness(1.0F);
            if (brightness < 0.0F && this.getType() == 1 || this.getType() == 4)
            {
                EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, getAttackRange());
                if (entityplayer != null) { return entityplayer; }
            }
            if (rand.nextInt(80) == 0 && this.getType() != 3)
            {
                EntityLivingBase closest_entityliving = getClosestEntityLiving(this, 10D);
                
                if (closest_entityliving instanceof MoCEntityAquatic || closest_entityliving instanceof MoCEntityTameableAquatic) // don't go and hunt fish if they are in the ocean
                {
                	int x = MathHelper.floor_double(closest_entityliving.posX);
                    int y = MathHelper.floor_double(closest_entityliving.posY);
                    int z = MathHelper.floor_double(closest_entityliving.posZ);

                    BiomeGenBase biome_that_prey_is_in = MoCTools.Biomekind(worldObj, x, y, z);

                    if (BiomeDictionary.isBiomeOfType(biome_that_prey_is_in, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biome_that_prey_is_in, Type.BEACH))
                    {
                    	 return null;
                    }
                }
                
                else {return closest_entityliving;}
            }
        }
            return null;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (mouthCounter > 0 && ++mouthCounter > 30)
        {
            mouthCounter = 0;
        }

        if (attackCounter > 0 && ++attackCounter > 100)
        {
            attackCounter = 0;
        }

        if ((MoCreatures.isServer()) && !getIsAdult() && (rand.nextInt(250) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 100)
            {
                setAdult(true);
            }
        }
        /**
         * panda bears and cubs will sit down every now and then if they are not walking somewhere, not in water and if there isn't an attack target
         */
        if (MoCreatures.isServer() && getBearState() != 2 && !hasPath() && !isInWater() && entityToAttack == null && (getType() == 3 || (!getIsAdult() && getMoCAge() < 60)) && (rand.nextInt(300) == 0))
        {
            setBearState(2);
        }

        /**
         * Sitting bears will resume on fours stance if they get in water or every now and then on land
         */
        if ((MoCreatures.isServer()) && (getBearState() == 2) && ((rand.nextInt(800) == 0) || isInWater()))
        {
            setBearState(0);
        }

        /**
         * Adult non panda bears will stand on hind legs if close to player
         */

        if ((MoCreatures.isServer()) && standingCounter == 0 && getBearState() != 2 && getIsAdult() && getType() != 3 && (rand.nextInt(500) == 0))
        {
            standingCounter = 1;
            EntityPlayer closest_player = worldObj.getClosestPlayerToEntity(this, 8D);
            if (closest_player != null)
            {
                setBearState(1);
            }
        }

        if ((MoCreatures.isServer()) && standingCounter > 0 && ++standingCounter > 50)
        {
            standingCounter = 0;
            setBearState(0);
        }
    }

	@Override
    public boolean interact(EntityPlayer entityplayer)
    {
        if (super.interact(entityplayer)) { return false; }
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        if ((itemstack != null) && (getType() == 3) && (isItemstackPandaFoodItem(itemstack)))
        {
        	
            if (--itemstack.stackSize == 0)
            {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
            }

            if (MoCreatures.isServer() && !getIsTamed())
            {
                MoCTools.tameWithName(entityplayer, this);
                entityplayer.addStat(MoCAchievements.tame_panda, 1);
            }

            heal(5);
            eatingAnimal();
            if (MoCreatures.isServer() && !getIsAdult() && (getMoCAge() < 100))
            {
                setMoCAge(getMoCAge() + 1);
            }

            return true;
        }
        return false;
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.animalHide;
    }
    
    @Override
    protected String getDeathSound()
    {
    	if (getType() == 3 && !(this.getIsAdult())) {return "mocreatures:pandacubdeath";}
    	else if (getType() == 3 && (this.getIsAdult())) {return "mocreatures:pandaadultdeath";}
    	else {return "mocreatures:beardying";}
    }

    @Override
    protected String getHurtSound()
    {
        openMouth();
        if (getType() == 3 && !(this.getIsAdult())) {return "mocreatures:pandacubhurt";}
    	else if (getType() == 3 && (this.getIsAdult())) {return "mocreatures:pandaadulthurt";}
    	else {return "mocreatures:bearhurt";}
    }

    @Override
    protected String getLivingSound()
    {
        openMouth();
        
        if (getType() == 3 && !(this.getIsAdult())) {return "mocreatures:pandacubgrunt";}
    	else if (getType() == 3 && (this.getIsAdult())) {return "mocreatures:pandaadultgrunt";}
    	else {return "mocreatures:beargrunt";}
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int x_coordinate = MathHelper.floor_double(posX);
        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        int z_coordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, x_coordinate, y_coordinate, z_coordinate);

        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SNOWY))
        {
            setType(4); //polar bear
            return true;
        }
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.JUNGLE) || currentbiome.biomeName.toLowerCase().contains("bamboo"))  // also adds Biome's O Plenty and Et Futurum Requiem "Bamboo Forest" compatibility
        {
            setType(3);//panda
            return true;
        }		
        
        return true;
    }

    private void openMouth()
    {
        mouthCounter = 1;
    }

    public float getAttackSwing()
    {
        //TODO FIX!
        return 0;
        //if (attackCounter == 0) return 0;
        //return ( (float)(attackCounter/10F) - 10F)/3F;
    }

    private void startAttack()
    {

        if (attackCounter == 0 && getBearState() == 1)
        {
            attackCounter = 1;
        }
    }

    private void eatingAnimal()
    {
        openMouth();
        MoCTools.playCustomSound(this, "eating", worldObj);
    }

    @Override
    public double roperYOffset()
    {
        if (this.getIsAdult())
        {
            return 0D;
        }
        else
        {
            return (double) ((130 - getMoCAge()) * 0.01D);
        }

    }

    @Override
    public boolean isMyFollowFood(ItemStack itemstack)
    {
    	return this.getType() == 3 && itemstack != null && (isItemstackPandaFoodItem(itemstack)); 
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        if (itemstack != null) 
        {
        	if (this.getType() == 3) //panda
        	{
        		return (isItemstackPandaFoodItem(itemstack));
        	}
        	else
        	{
        		return isItemEdible(itemstack.getItem());
        	}
        }
        else {return false;}
    }
    
    
    private boolean isItemstackPandaFoodItem(ItemStack itemstack) {
    	
    	Item item = itemstack.getItem();
    	
		if (
				item == Items.reeds
				|| item == Items.sugar
            	|| (((item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:bamboo")))
            	|| (((item.itemRegistry).getNameForObject(item).equals("etfuturum:bamboo")))
            	|| (((item.itemRegistry).getNameForObject(item).equals("tropicraft:bambooChute")))
            	|| (((item.itemRegistry).getNameForObject(item).equals("harvestcraft:bambooshootItem")))
            ) {return true;}
		
		return false;
	}

    @Override
    public int nameYOffset()
    {
        if (getIsAdult())
        {
            return (-55);
        }
        return (int) ((100/getMoCAge()) * (-40));

    }
    
    @Override
    public double getCustomSpeed()
    {
        if (getBearState() == 2) 
        {
            return 0D;
        }
        return super.getCustomSpeed();
    }
}