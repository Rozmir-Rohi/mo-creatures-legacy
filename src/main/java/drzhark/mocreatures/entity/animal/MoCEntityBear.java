package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
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
        dataWatcher.addObject(23, Byte.valueOf((byte)0));
    }
    
    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && ticksExisted > 2400;
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
        
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        setHealth(getMaxHealth());
    }
    
    @Override
    public boolean isPredator()
    {
    	return (getType() != 3 && getType() != 5); //not a panda or giant panda
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
        case 5:
            return MoCreatures.proxy.getTexture("bearpanda.png");

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
        case 5:
            return 1.2F;

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
	        case 5:
	            return 20;
	
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
        case 5:
            return 3D;

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
        int worldDifficulty = (worldObj.difficultySetting.getDifficultyId());

        switch (getType())
        {
        case 1:
            return 2 * worldDifficulty;
        case 2:
            return 1 * worldDifficulty;
        case 3:
            return 1;
        case 4:
            return 3 * worldDifficulty;
        case 5:
            return 2 * worldDifficulty;

        default:
            return 2;
        }
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
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
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
    	if (super.attackEntityFrom(damageSource, damageTaken))
        {
    		if (!(getIsAdult()) && (damageSource.getEntity() != null))
        	{
    			if (getBearState() == 2) //if sitting
    			{
    				setBearState(0);
    			}
    			
    			MoCTools.runAway(this, damageSource.getEntity()); //child runs away from attacking entity
    			
    			List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(10D, 10D, 10D));
    			
    			int iterationLength = entitiesNearbyList.size();
    			
    			if (iterationLength > 0)
    			{
	    			for (int index = 0; index < iterationLength; index++)
	    	        {
	    	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
		                if (entityNearby instanceof MoCEntityBear) //set attack target of adults near by to the entity that is attacking that child
		                {
		                	MoCEntityBear bearNearby = (MoCEntityBear) entityNearby;
		                	if (bearNearby.getIsAdult());
		                	{	
		                		if (!(bearNearby.getIsTamed()) && (bearNearby.getType() == getType()))
		                		{
		                			if (bearNearby.getBearState() == 2) //if sitting
		                			{
		                				bearNearby.setBearState(0);
		                			}
		                			
		                			bearNearby.entityToAttack = damageSource.getEntity();
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
    			Entity entityThatAttackedThisCreature = damageSource.getEntity();
    			
    			if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName()))))
                { 
                	return false; 
                }
    			
    			if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
    			if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
    			{
    				entityToAttack = entityThatAttackedThisCreature;
        			
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
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (
	        		super.shouldEntityBeIgnored(entity) //including the mobs specified in parent file
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
        	EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, getAttackRange());
           	if (entityPlayer != null) { return entityPlayer; }
           
           	else if (rand.nextInt(80) == 0 && getType() != 3)
            {
                EntityLivingBase closestEntityLiving = getClosestEntityLiving(this, 10D);
                
                
                if (closestEntityLiving != null && !MoCTools.isEntityAFishThatIsInTheOcean(closestEntityLiving))
                {
                	return closestEntityLiving;
                }
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
        if (
        		MoCreatures.isServer() && getBearState() != 2 && !hasPath() && !isInWater() && entityToAttack == null
        		&& (getType() == 3 || getType() == 5 || (!getIsAdult() && getMoCAge() < 60))
        		&& (rand.nextInt(300) == 0)
        	)
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

        if ((MoCreatures.isServer()) && standingCounter == 0 && getBearState() != 2 && getIsAdult() && getType() != 3 && getType() != 5 && (rand.nextInt(500) == 0))
        {
            standingCounter = 1;
            EntityPlayer closestPlayer = worldObj.getClosestPlayerToEntity(this, 8D);
            if (closestPlayer != null)
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
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        if ((itemstack != null) && (getType() == 3 || getType() == 5) && (isItemstackPandaFoodItem(itemstack)))
        {
        	
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            if (MoCreatures.isServer() && !getIsTamed())
            {
                MoCTools.tameWithName(entityPlayer, this);
                entityPlayer.addStat(MoCAchievements.tame_panda, 1);
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
        return MoCreatures.hide;
    }
    
    @Override
    protected String getDeathSound()
    {
    	if (getType() == 3 && !(getIsAdult())) {return "mocreatures:pandacubdeath";}
    	else if ((getType() == 3 && getIsAdult()) || getType() == 5) {return "mocreatures:pandaadultdeath";}
    	else {return "mocreatures:beardying";}
    }

    @Override
    protected String getHurtSound()
    {
        openMouth();
        if (getType() == 3 && !(getIsAdult())) {return "mocreatures:pandacubhurt";}
    	else if ((getType() == 3 && getIsAdult()) || getType() == 5) {return "mocreatures:pandaadulthurt";}
    	else {return "mocreatures:bearhurt";}
    }

    @Override
    protected String getLivingSound()
    {
        openMouth();
        
        if (getType() == 3 && !(getIsAdult())) {return "mocreatures:pandacubgrunt";}
    	else if ((getType() == 3 && getIsAdult()) || getType() == 5) {return "mocreatures:pandaadultgrunt";}
    	else {return "mocreatures:beargrunt";}
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
            setType(4); //polar bear
            return true;
        }
        
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.JUNGLE) || currentBiome.biomeName.toLowerCase().contains("bamboo"))  // also adds Biome's O Plenty and Et Futurum Requiem "Bamboo Forest" compatibility
        {
            setType(3);//panda
            
            if(MoCreatures.proxy.enableRareGiantPandaVariant && rand.nextInt(100) <= 10) //10 percent chance if enabled
            {
            	setType(5);// giant panda
            	setMoCAge(100);
            }
            
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
        if (getIsAdult())
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
    	return (getType() == 3 || getType() == 5) && itemstack != null && (isItemstackPandaFoodItem(itemstack)); 
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        if (itemstack != null) 
        {
        	if (getType() == 3 || getType() == 5) //panda and giant panda
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
            	|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:bamboo")
            	|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:bamboo")
            	|| (item.itemRegistry).getNameForObject(item).equals("tropicraft:bambooChute")
            	|| (item.itemRegistry).getNameForObject(item).equals("harvestcraft:bambooshootItem")
            	|| (item.itemRegistry).getNameForObject(item).equals("Growthcraft|Bamboo:grc.bamboo")
            	
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooFargesiaRobusta")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooShortTassled")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooTimorBlack")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooGolden")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooWetForest")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooAsper")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooTropicalBlue")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooMoso")
            	|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:bambooGiantTimber")
            ) {return true;}
		
		return false;
	}

    @Override
    public int nameYOffset()
    {
        if (getIsAdult())
        {
        	if (getType() == 3) {return -55;}
        	
        	else {return -70;}
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