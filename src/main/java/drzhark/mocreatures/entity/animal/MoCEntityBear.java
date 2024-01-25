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
import net.minecraft.entity.item.EntityItem;
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
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "bearbrowm.png";
        setSize(1.2F, 1.5F);
        roper = null;
        //health = 25;
        setMoCAge(55);
        //force = 5;
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

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
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

            this.setHealth(getMaxHealth());
        }
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
        int factor = (worldObj.difficultySetting.getDifficultyId());

        switch (getType())
        {
        case 1:
            return 2 * factor;
        case 2:
            return 1 * factor;
        case 3:
            return 1;
        case 4:
            return 3 * factor;

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
            if (!(entity instanceof EntityPlayer))
            {
                MoCTools.destroyDrops(this, 3D);
            }
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
    			MoCTools.runLikeHell(this, damagesource.getEntity()); //child runs away from attacking entity
    			
    			List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(10D, 10D, 10D));
    			for (int index = 0; index < entitiesNearbyList.size(); index++)
    	        {
    	            Entity entity1 = (Entity) entitiesNearbyList.get(index);
	                if (entity1 instanceof MoCEntityBear) //set attack target of adults near by to the entity that is attacking that child
	                {
	                	MoCEntityBear bearEntityNearBy = (MoCEntityBear) entity1;
	                	if (bearEntityNearBy.getIsAdult());
	                	{	
	                		if (!(bearEntityNearBy.getIsTamed()) && (bearEntityNearBy.getType() == this.getType()))
	                		{
	                			bearEntityNearBy.entityToAttack = damagesource.getEntity();
	                		}
	                	}
	                	
	                	continue; 
	                }
	                
	                else {continue;}
	                
	                
    	        }    
    			
    			
        		return false;
        	}
    		else
            {
    			Entity entity = damagesource.getEntity();
    			if ((riddenByEntity == entity) || (ridingEntity == entity)) { return true; }
    			if ((entity != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && this.getType() != 3)
    			{
    				entityToAttack = entity;
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
    public boolean entitiesToIgnore(Entity entity) //don't hunt the following mobs below
    {
        return (super.entitiesToIgnore(entity) //including the mobs specified in parent file
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
            float f = getBrightness(1.0F);
            if (f < 0.0F && this.getType() == 1 || this.getType() == 4)
            {
                EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, getAttackRange());
                if (entityplayer != null) { return entityplayer; }
            }
            if (rand.nextInt(80) == 0 && this.getType() != 3)
            {
                EntityLivingBase entityliving = getClosestEntityLiving(this, 10D);
                
                if (entityliving instanceof MoCEntityAquatic || entityliving instanceof MoCEntityTameableAquatic) // don't go and hunt fish if they are in the ocean
                {
                	int x = MathHelper.floor_double(entityliving.posX);
                    int y = MathHelper.floor_double(entityliving.posY);
                    int z = MathHelper.floor_double(entityliving.posZ);

                    BiomeGenBase biome_that_prey_is_in = MoCTools.Biomekind(worldObj, x, y, z);

                    if (BiomeDictionary.isBiomeOfType(biome_that_prey_is_in, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biome_that_prey_is_in, Type.BEACH))
                    {
                    	 return null;
                    }
                }
                
                else {return entityliving;}
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
         * panda bears and cubs will sit down every now and then
         */
        if ((MoCreatures.isServer()) && (getType() == 3 || (!getIsAdult() && getMoCAge() < 60)) && (rand.nextInt(300) == 0))
        {
            setBearState(2);
        }

        /**
         * Sitting bears will resume on fours stance every now and then
         */
        if ((MoCreatures.isServer()) && (getBearState() == 2) && (rand.nextInt(800) == 0))
        {
            setBearState(0);
        }

        /**
         * Adult non panda bears will stand on hind legs if close to player
         */

        if ((MoCreatures.isServer()) && standingCounter == 0 && getBearState() != 2 && getIsAdult() && getType() != 3 && (rand.nextInt(500) == 0))
        {
            standingCounter = 1;
            EntityPlayer entityplayer1 = worldObj.getClosestPlayerToEntity(this, 8D);
            if (entityplayer1 != null)
            {
                setBearState(1);
            }
        }

        if ((MoCreatures.isServer()) && standingCounter > 0 && ++standingCounter > 50)
        {
            standingCounter = 0;
            setBearState(0);
        }

        if (MoCreatures.isServer() && getType() == 3 && (deathTime == 0) && getBearState() != 2)
        {
            EntityItem entityitem = getClosestEntityItem(this, 8D);
            if (entityitem != null)
            {
            	ItemStack itemstack = entityitem.getEntityItem();
            	
            	if (isItemstackFoodItem(itemstack))
            	{

            		float f = entityitem.getDistanceToEntity(this);
            		if (f > 2.0F)
            		{
            			getMyOwnPath(entityitem, f);
            		}
            		
            		if ((f < 2.0F) && (entityitem != null) && (deathTime == 0))
            		{
            			entityitem.setDead();
            			worldObj.playSoundAtEntity(this, "mocreatures:eating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            			this.setHealth(getMaxHealth());
            		}
            	}

            }
        }
    }

	@Override
    public boolean interact(EntityPlayer entityplayer)
    {
        if (super.interact(entityplayer)) { return false; }
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        if ((itemstack != null) && (getType() == 3) && (isItemstackFoodItem(itemstack)))
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

            this.setHealth(getMaxHealth());
            eatingAnimal();
            if (MoCreatures.isServer() && !getIsAdult() && (getMoCAge() < 100))
            {
                setMoCAge(getMoCAge() + 1);
            }

            return true;
        }
        return false;
    }
	
	private boolean isItemstackFoodItem(ItemStack itemstack) {
    	
    	Item item = itemstack.getItem();
    	
		if (
				item == Items.reeds
				|| item == Items.sugar
            	|| (((item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:bamboo")))
            	|| (((item.itemRegistry).getNameForObject(item).equals("etfuturum:bamboo")))
            	|| (((item.itemRegistry).getNameForObject(item).equals("tropicraft:bambooChute")))
            ) {return true;}
		
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
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);

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
    public boolean isMyFavoriteFood(ItemStack itemstack)
    {
    	return this.getType() == 3 && itemstack != null && (isItemstackFoodItem(itemstack)); 
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return this.getType() == 3 && itemstack != null && (isItemstackFoodItem(itemstack)); 
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