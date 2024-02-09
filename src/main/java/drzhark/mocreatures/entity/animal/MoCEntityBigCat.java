package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityAmbient;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityBigCat extends MoCEntityTameableAnimal {
	
	public MoCEntityBigCat(World world)
    {
        super(world);
        setMoCAge(35);
        setSize(0.9F, 1.3F);
        if (rand.nextInt(4) == 0)
        {
            setAdult(false);

        }
        else
        {
            setAdult(true);
        }
        setHungry(true);
        setTamed(false);
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

    /**
     * Sets the type and texture of a BigCat if not set already.
     */
    @Override
    public void selectType()
    {
        checkSpawningBiome();  //apply big cat type based on the biome it spawns in
        
        
        if ((getType() == 0) && checkSpawningBiome())  //if big cat is still type 0 and it can spawn in the biome, make it a lion
        {
        	setType(1);
        }

        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        
        setHealth(getMaxHealth());
    }

    @Override
    public float getMoveSpeed()
    {
        switch (getType())
        {
        case 1: //female lion
            return 1.4F;
            
        case 2: //male lion
            return 1.4F;
            
        case 3: //panther
            return 1.6F;
            
        case 4: //cheetah
            return 1.9F;
            
        case 5: //normal tiger
            return 1.6F;
            
        case 6: //snow leopard
            return 1.7F;
            
        case 7: //white tiger
            return 1.7F;

        default:
            return 1.4F;
        }
    }

    public float calculateMaxHealth()
    {
        switch (getType())
        {
        case 1: //female lion
            return 25;
            
        case 2: //male lion
            return 30;
            
        case 3: //panther
            return 20;
            
        case 4: //cheetah
            return 20;
            
        case 5: //normal tiger
            return 35;
            
        case 6: //snow leopard
            return 25;
            
        case 7: //white tiger
            return 40;

        default:
            return 20;
        }

    }

    public float getWidthF()
    {
        switch (getType())
        {
        case 1:
            return 1F;
        case 2:
            return 1.1F;
        case 3:
            return 0.9F;
        case 4:
            return 0.8F;
        case 5:
            return 1.1F;
        case 6:
            return 0.8F;
        case 7:
            return 1.2F;

        default:
            return 1F;
        }
    }

    public float getHeightF()
    {
        switch (getType())
        {
        case 1:
            return 1.0F;
        case 2:
            return 1.1F;
        case 3:
            return 0.9F;
        case 4:
            return 0.8F;
        case 5:
            return 1.1F;
        case 6:
            return 0.8F;
        case 7:
            return 1.2F;

        default:
            return 1F;
        }
    }

    public float getLengthF()
    {
        switch (getType())
        {
        case 1:
            return 1.0F;
        case 2:
            return 1.0F;
        case 3:
            return 0.9F;
        case 4:
            return 1.0F;
        case 5:
            return 1.1F;
        case 6:
            return 0.9F;
        case 7:
            return 1.2F;

        default:
            return 1F;
        }
    }

    public int getAttackStrength()
    {
        switch (getType())
        {
        case 1: //female lion
            return 5;
            
        case 2: //male lion
            return 5;
            
        case 3: //panther
            return 4;
            
        case 4: //cheetah
            return 3;
            
        case 5: //normal tiger
            return 6;
            
        case 6: //snow leopard
            return 3;
            
        case 7: //white tiger
            return 8;

        default:
            return 5;
        }
    }

    public double getAttackRange()
    {
        switch (getType())
        {
        case 1:
            return 8D;
        case 2:
            return 4D;
        case 3:
            return 6D;
        case 4:
            return 6D;
        case 5:
            return 8D;
        case 6:
            return 4D;
        case 7:
            return 10D;

        default:
            return 6D;
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
            case 1:
                return MoCreatures.proxy.getTexture("lionf.png");
            case 2:
                return MoCreatures.proxy.getTexture("lionf.png");
            case 3:
                return MoCreatures.proxy.getTexture("panther.png");
            case 4:
                return MoCreatures.proxy.getTexture("cheetah.png");
            case 5:
                return MoCreatures.proxy.getTexture("tiger.png");
            case 6:
                return MoCreatures.proxy.getTexture("leopard.png");
            case 7:
                return MoCreatures.proxy.getTexture("tigerw.png");
            default:
                return MoCreatures.proxy.getTexture("lionf.png");
        }
    }

    /**
     * Initializes datawatchers for entity. Each datawatcher is used to sync
     * server data to client.
     */
    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isHungry - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // isPreTamed - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // isSitting - 0 false 1 true
    }

    public boolean getIsHungry()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getPreTamed()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public boolean getIsSitting()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public void setHungry(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setPreTamed(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public void setSitting(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }
    

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity > 2.0F) && (distanceToEntity < 6F) && (rand.nextInt(50) == 0))
        {
            if (onGround)
            {
                double xDistance = entity.posX - posX;
                double zDistance = entity.posZ - posZ;
                float overallHorizontalDistanceSquared = MathHelper.sqrt_double((xDistance * xDistance) + (zDistance * zDistance));
                motionX = ((xDistance / overallHorizontalDistanceSquared) * 0.5D * 0.8D) + (motionX * 0.2D);
                motionZ = ((zDistance / overallHorizontalDistanceSquared) * 0.5D * 0.8D) + (motionZ * 0.2D);
                motionY = 0.4D;
            }
            return;

        }

        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), getAttackStrength());
        }
    }

    // Method used for receiving damage from another source
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
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting != worldObj.difficultySetting.PEACEFUL))
            {
                entityToAttack = entityThatAttackedThisCreature;
                
                if (!(getIsAdult()) && (entityThatAttackedThisCreature != null) && !(getIsTamed()))
            	{
        			
        			List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(10D, 10D, 10D));
        			for (int index = 0; index < entitiesNearbyList.size(); index++)
        	        {
        	            Entity entity1 = (Entity) entitiesNearbyList.get(index);
    	                if (entity1 instanceof MoCEntityBigCat) //set attack target of adults near by to the entity that is attacking that child
    	                {
    	                	MoCEntityBigCat bigCatEntityNearBy = (MoCEntityBigCat) entity1;
    	                	if (bigCatEntityNearBy.getIsAdult());
    	                	{	
    	                		if (!(bigCatEntityNearBy.getIsTamed()) && (bigCatEntityNearBy.getType() == getType()))
    	                		{
    	                			bigCatEntityNearBy.entityToAttack = damageSource.getEntity();
    	                		}
    	                	}	
    	                	continue; 
    	                }
    	                else {continue;}
        	        }    
            		return false;
            	}
                
                return true;
            }
        }
        return false;
    }

    public int checkForOtherBigCatsNearbyAndTheirType(double d)
    {
        boolean flag = false;
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
        for (int index = 0; index < entitiesNearbyList.size(); index++)
        {
            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
            if ((entityNearby != this) && (entityNearby instanceof MoCEntityBigCat))
            {
                MoCEntityBigCat entitybigcatNearby = (MoCEntityBigCat) entityNearby;
                return entitybigcatNearby.getType();
            }
        }

        return 0;
    }

    @Override
    protected Entity findPlayerToAttack()
    {	
        if (roper != null && roper instanceof EntityPlayer) { return getMastersEnemy((EntityPlayer) roper, 12D); }

        if (worldObj.difficultySetting != worldObj.difficultySetting.PEACEFUL)
        {
            EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, getAttackRange());
            if (!getIsTamed() && (entityPlayer != null) && getIsAdult() && getIsHungry())
            {
                if ((getType() == 1) || (getType() == 5) || (getType() == 7))
                {
                    setHungry(false);
                    return entityPlayer;
                }
                if (rand.nextInt(30) == 0)
                {
                    setHungry(false);
                    return entityPlayer;
                }
            }
            if ((rand.nextInt(80) == 0) && getIsHungry())
            {
                EntityLivingBase entityLiving = getClosestEntityLiving(this, getAttackRange());
                setHungry(false);
                return entityLiving;
            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
    	{
	        if (getIsTamed()) //defend owner if they are attacked by an entity
	    	{
	    		EntityPlayer ownerOfEntityThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(getOwnerName());
	    		
	    		if (ownerOfEntityThatIsOnline != null)
	    		{
	    			EntityLivingBase entityThatAttackedOwner = ownerOfEntityThatIsOnline.getAITarget();
	    			
	    			if (entityThatAttackedOwner != null)
	    			{
	    				return entityThatAttackedOwner;
	    			}
	    		}
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

        BiomeGenBase currentBiome = MoCTools.Biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        String biomeName = MoCTools.BiomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        int typeChance = rand.nextInt(100);

        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SAVANNA))
        {
        	if (!(currentBiome.biomeName.toLowerCase().contains("outback")))
        	{
	            if (typeChance <= 30)
	    			{setType(4);} //cheetah
	            
	            else if (typeChance <= 40)
	    			{setType(2);} //lion type 1 - male
	            
	            else {setType(1);} //lion type 2 - female
        	}
        	else
        	{
        		return false; //don't spawn big cats in the outback biome from the Biomes O' Plenty mod. The code for this is continued in MoCEventHooks.java
        	}
            
            return true;
        }
        
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.JUNGLE))
        {
        	if (typeChance <= 10)
    			{setType(7);} //white tiger
        	
        	else if (typeChance <= 30)
        		{setType(3);} //panther
            
            else {setType(5);} //tiger
            
            return true;
        }
        
        
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SNOWY))
        {
            setType(6); //snow leopard
            return true;
        }
        
        

        int typeOfOtherBigCatNearby = 0;
        {
            typeOfOtherBigCatNearby = checkForOtherBigCatsNearbyAndTheirType(12D);

           
            if (typeOfOtherBigCatNearby == 2)
            {
                typeOfOtherBigCatNearby = 1;
            }
            else if (typeOfOtherBigCatNearby == 1 && rand.nextInt(3) == 0)
            {
                typeOfOtherBigCatNearby = 2;
            }
            else if (typeOfOtherBigCatNearby == 7)
            {
                typeOfOtherBigCatNearby = 5;
            }
        }
        
        setType(typeOfOtherBigCatNearby);
        return true;
    }

    // TODO move somewhere else
    public boolean NearSnowWithDistance(Entity entity, Double double1)
    {
        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(double1.doubleValue(), double1.doubleValue(), double1.doubleValue());
        int i = MathHelper.floor_double(axisalignedbb.minX);
        int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
        int k = MathHelper.floor_double(axisalignedbb.minY);
        int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
        int i1 = MathHelper.floor_double(axisalignedbb.minZ);
        int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
        for (int k1 = i; k1 < j; k1++)
        {
            for (int l1 = k; l1 < l; l1++)
            {
                for (int i2 = i1; i2 < j1; i2++)
                {
                    Block block = worldObj.getBlock(k1, l1, i2);
                    if ((block != Blocks.air) && (block.getMaterial() == Material.snow)) { return true; }
                }

            }

        }

        return false;
    }

    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (super.shouldEntityBeIgnored(entity) //including the mobs specified in parent file
                    || (entity instanceof MoCEntityBigCat)
                    || (getIsAdult() && (entity.width > 1.3D && entity.height > 1.3D)) // don't try to hunt creature larger than a deer when adult
                    || (!getIsAdult() && (entity.width > 0.5D && entity.height > 0.5D)) // don't try to hunt creature larger than a chicken when child
                    || (entity == entity.riddenByEntity) 
                    || (entity == entity.ridingEntity) 
                    || (entity instanceof MoCEntityAmbient)
                    || (entity instanceof MoCEntityWyvern)
                    || (entity instanceof MoCEntityAquatic || entity instanceof MoCEntityTameableAquatic || entity instanceof EntitySquid)
                    || (entity instanceof MoCEntityElephant)
                    || ((entity instanceof EntityMob) && (!getIsTamed() || !getIsAdult()))
                    );
    }

    @Override
    protected String getDeathSound()
    {
        if (getIsAdult())
        {
            return "mocreatures:liondeath";
        }
        else
        {
            return "mocreatures:cubdying";
        }
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.bigcatClaw;
    }

    @Override
    protected String getHurtSound()
    {
        if (getIsAdult())
        {
            return "mocreatures:lionhurt";
        }
        else
        {
            return "mocreatures:cubhurt";
        }
    }

    @Override
    protected String getLivingSound()
    {
        if (getIsAdult())
        {
            return "mocreatures:liongrunt";
        }
        else
        {
            return "mocreatures:cubgrunt";
        }
    }

    public EntityCreature getMastersEnemy(EntityPlayer entityPlayer, double d)
    {
        double d1 = -1D;
        EntityCreature entitycreature = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entityPlayer, boundingBox.expand(d, 4D, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity = (Entity) list.get(i);
            if (!(entity instanceof EntityCreature) || (entity == this))
            {
                continue;
            }
            EntityCreature entitycreature1 = (EntityCreature) entity;
            if ((entitycreature1 != null) && (entitycreature1.getAttackTarget() == entityPlayer)) { return entitycreature1; }
        }

        return entitycreature;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {

        if (super.interact(entityPlayer)) { return false; }
        
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        
        if ((itemstack != null) && !getIsTamed() && getPreTamed() && (itemstack.getItem() == MoCreatures.medallion))
        {
            if (MoCreatures.isServer())
            {
                MoCTools.tameWithName(entityPlayer, this);
                entityPlayer.addStat(MoCAchievements.tame_big_cat, 1);
            }
            
            if (getIsTamed() && --itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                return true;
            }

            return false;
        }
        
        if ((itemstack != null) && getIsTamed() && isMyHealFood(itemstack))
        {
            heal(5);
            worldObj.playSoundAtEntity(this, "mocreatures:eating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            setHungry(false);
        }
        return false;

    }

    /**
     * Checks if bigcat is sitting.
     */
    @Override
    protected boolean isMovementCeased()
    {
        return getIsSitting();
    }
    
    @Override
    public int nameYOffset()
    {
        if (getIsAdult() && getType() == 2) //tamed male lions need a slightly higher name y offset because of their manes
        {
            return -80;
        }
        return -60;
    }

    //drops medallion on death
    @Override
    public void onDeath(DamageSource damageSource)
    {
        if (MoCreatures.isServer())
        {
            if (getIsTamed())
            {
                MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.medallion, 1));
            }
        }
        super.onDeath(damageSource);
    }
    
    @Override
    public void onLivingUpdate()
    {

        super.onLivingUpdate();

        if ((rand.nextInt(300) == 0) && (getHealth() <= getMaxHealth()) && (deathTime == 0) && !worldObj.isRemote)
        {
            //health++;
            setHealth(getHealth() + 1);
        }
        if (!getIsAdult() && (rand.nextInt(250) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 100)
            {
                setAdult(true);
                
                if (getType() == 1) //used to make baby lions have a chance to grow into male lions
                {
                	int typeThatBabyLionGrowsUpToBe  = rand.nextInt(2)+1;
                	
                	if(typeThatBabyLionGrowsUpToBe != 1)
                	{
                		setType(typeThatBabyLionGrowsUpToBe);
                		
                		
                		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
                        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
                        
                        setHealth(getMaxHealth());
                	}
                }
            }
        }
        if (!getIsHungry() && !getIsSitting() && (rand.nextInt(200) == 0))
        {
            setHungry(true);
        }
        if ((roper != null))
        {
            float f = roper.getDistanceToEntity(this);
            if ((f > 5F) && !getIsSitting())
            {
                getPathOrWalkableBlock(roper, f);
            }
            if ((f > 18F) & getIsSitting())
            {
                roper = null;
            }
        }
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
	    			item == Items.porkchop
	    			|| item == Items.beef 
	    			|| item == Items.chicken
	    			|| (item == Items.fish && itemstack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
	    			|| item == MoCreatures.ostrichRaw
	    			|| item == MoCreatures.turkeyRaw
	    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:rabbit_raw")
	    			|| oreDictionaryNameArray.contains("listAllmeatraw")
	    			|| oreDictionaryNameArray.contains("listAllfishraw")
	    			|| MoCreatures.isGregTech6Loaded &&
	    				(
	    					OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodScrapmeat"
	    				)
	    		);
    	}
    	else {return false;}
    }

    @Override
    public boolean isNotScared()
    {
        return true;
    }

    @Override
    public boolean renderName()
    {
        return !getName().isEmpty() && getDisplayName() && MoCreatures.proxy.getDisplayPetName();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setSitting(nbtTagCompound.getBoolean("Sitting"));
        setDisplayName(nbtTagCompound.getBoolean("DisplayName"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Sitting", getIsSitting());
        nbtTagCompound.setBoolean("DisplayName", getDisplayName());
    }
    
    @Override
    public void dropMyStuff() 
    {
    }
}