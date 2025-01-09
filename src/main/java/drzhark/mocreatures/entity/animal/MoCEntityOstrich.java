package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.inventory.MoCAnimalChest;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityOstrich extends MoCEntityTameableAnimal {

    private int eggCounter;
    private int hidingCounter;
    public int mouthCounter;
    public int wingCounter;
    public int sprintCounter;
    public int jumpCounter;
    public int transformCounter;
    public int transformType;
    public boolean canLayEggs;

    public MoCAnimalChest localChest;
    public ItemStack localItemstack;

    public MoCEntityOstrich(World world)
    {
        super(world);
        setSize(1.0F, 1.6F);
        setMoCAge(35);
        roper = null;
        eggCounter = rand.nextInt(1000) + 1000;
        stepHeight = 1.0F;
        canLayEggs = false;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isRideable - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // eggWatch - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // hiding - 0 false 1 true
        dataWatcher.addObject(25, Byte.valueOf((byte) 0)); // helmet - 0 none
        dataWatcher.addObject(26, Byte.valueOf((byte) 0)); // flagcolor - 0 white
        dataWatcher.addObject(27, Byte.valueOf((byte) 0)); // bagged - 0 false 1 true
    }
    
    @Override
    public void selectType()
    {
        if (checkSpawningBiome() && getType() == 0)
        {
            /**
             * 1 = chick 2 = female 3 = male 4 = albino male 5 = demon ostrich
             */
            int typeChance = rand.nextInt(100);
            if (typeChance <= (20))
            {
                setType(1);
            }
            else if (typeChance <= (65))
            {
                setType(2);
            }
            else if (typeChance <= (95))
            {
                setType(3);
            }
            else
            {
                setType(4);
            }
        }
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        setHealth(getMaxHealth());
    }
    
    
    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        rand.nextInt(100);

        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SAVANNA))
        {
        	if ((currentBiome.biomeName.toLowerCase().contains("outback")))
        	{
        		return false; //don't spawn Ostriches in the outback biome from the Biomes O' Plenty mod. The code for this is continued in MoCEventHooks.java
        	}
        }
        
        return true;
    }

    @Override
	public boolean getIsRideable()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    @Override
	public void setRideable(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public boolean getEggWatching()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setEggWatching(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public boolean getHiding()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public void setHiding(boolean flag)
    {
        if (worldObj.isRemote) { return; }
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }

    public byte getHelmet()
    {
        return (dataWatcher.getWatchableObjectByte(25));
    }

    public void setHelmet(byte b)
    {
        dataWatcher.updateObject(25, Byte.valueOf(b));
    }
    
    public byte getFlagColor()
    {
        return (dataWatcher.getWatchableObjectByte(26));
    }

    public void setFlagColor(byte b)
    {
        dataWatcher.updateObject(26, Byte.valueOf(b));
    }

    public boolean getIsChested()
    {
        return (dataWatcher.getWatchableObjectByte(27) == 1);
    }

    public void setIsChested(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(27, Byte.valueOf(input));
    }

    @Override
    protected boolean isMovementCeased()
    {
        return (getHiding() || riddenByEntity != null);
    }

    @Override
    public boolean isNotScared()
    {
        return (getType() == 2 && entityToAttack != null) || (getType() > 2);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        //damage reduction
        if (getIsTamed() && getHelmet() != 0)
        {
            int damageReduction = getDamageReductionFromHelmetWornByOstrich();
            damageTaken -= damageReduction;
            if (damageTaken <= 0) damageTaken = 1;
        }

        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();

            if ( ((riddenByEntity != null) && (entityThatAttackedThisCreature == riddenByEntity)) ) { return false; }
            
            if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName()))))
            { 
            	return false; 
            }

            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && getType() > 2)
            {
                entityToAttack = entityThatAttackedThisCreature;
                flapWings();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

	private int getDamageReductionFromHelmetWornByOstrich() 
	{
		switch (getHelmet())
		{
		    case 5: // hide helmet
		    case 6: // fur helmet
		    case 1: // leather helmet
		        return 1; 
		        
		    case 7: //croc helmet
		    case 2: // iron helmet
		        return 2;
		        
		    case 3: //gold helmet
		        return 3;
		        
		    case 4: //diamond helmet
		    case 9: //dirt scorpion helmet
		    case 10: //frost scorpion helmet
		    case 11: //cave scorpion helmet
		    case 12: // nether scorpion helmet
		        return 4;
		    default:
		    	return 0;
		}
	}

    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
        dropMyStuff();
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && distanceToEntity < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
        {
            attackTime = 20;
            openMouth();
            flapWings();
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
        }
    }

    public float calculateMaxHealth()
    {
        switch (getType())
        {
	        case 1:
	            return 10;
	        case 2:
	            return 15;
	        case 3:
	            return 20;
	        case 4:
	            return 20;
	        case 5:
	            return 20;
	
	        default:
	            return 20;
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
       if (transformCounter != 0 && transformType > 4)
        {
            String newTexture = "ostricha.png";
            if (transformType == 5)
            {
                newTexture = "ostriche.png";
            }
            if (transformType == 6)
            {
                newTexture = "ostrichf.png";
            }
            if (transformType == 7)
            {
                newTexture = "ostrichg.png";
            }
            if (transformType == 8)
            {
                newTexture = "ostrichh.png";
            }
            
            if ((transformCounter % 5) == 0) { return MoCreatures.proxy.getTexture(newTexture); }
            if (transformCounter > 50 && (transformCounter % 3) == 0) { return MoCreatures.proxy.getTexture(newTexture); }

            if (transformCounter > 75 && (transformCounter % 4) == 0) { return MoCreatures.proxy.getTexture(newTexture); }
        }

        switch (getType())
        {
	        case 1:
	            return MoCreatures.proxy.getTexture("ostrichc.png"); //chick
	        case 2:
	            return MoCreatures.proxy.getTexture("ostrichb.png"); //female
	        case 3:
	            return MoCreatures.proxy.getTexture("ostricha.png"); //male
	        case 4:
	            return MoCreatures.proxy.getTexture("ostrichd.png"); //albino
	        case 5:
	            return MoCreatures.proxy.getTexture("ostriche.png"); //nether
	        case 6:
	            return MoCreatures.proxy.getTexture("ostrichf.png"); //black wyvern
	        case 7:
	            return MoCreatures.proxy.getTexture("ostrichg.png"); //undead
	        case 8:
	            return MoCreatures.proxy.getTexture("ostrichh.png"); //unicorned
	        default:
	            return MoCreatures.proxy.getTexture("ostricha.png");
        }
    }

    @Override
    public double getCustomSpeed()
    {
        double ostrichSpeed = 0.8D;
        
        switch (getType())
        {
	        case 1: //baby ostrich
	        	ostrichSpeed = 0.9;
	        	break;
	        	
	        case 2: //female ostrich
	        	ostrichSpeed = 0.8D;
	        	break;
	        	
	        case 3: //male ostrich
	        	ostrichSpeed = 0.9D;
	        	break;
	        	
	        case 4: //albino ostrich
	        	ostrichSpeed = 1D;
	        	break;
	        	
	        case 5: //nether ostrich
	        	ostrichSpeed = 1.1D;
	            isImmuneToFire = true;
	            break;
	            
	        case 6: //wyvern ostrich
	        	ostrichSpeed = 1.12D;
	            break;
	            
	        case 7: //zombie ostrich
	        	ostrichSpeed = 1.2D;
	            isImmuneToFire = true;
	            break;
	            
	        case 8: //unihorn ostrich
	        	ostrichSpeed = 1.25D;
	            isImmuneToFire = true;
	            break;
	            
	        default:
	            return 0.8;
        }
        
        
        if (sprintCounter > 0 && sprintCounter < 200)
        {
            ostrichSpeed *= 1.2D;
        }
        if (
        		sprintCounter > 200 
        		&& getType() == 8 //unicorn
        	)
        {
            ostrichSpeed *= 0.7D;
        }
        
        return ostrichSpeed;
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
    }
    
    @Override
    public boolean isSelfPropelledFlyer()
    {
    	return getType() == 6; //wyvern ostrich
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        
        if (getType() == 5 && !isImmuneToFire)
        {
        	isImmuneToFire = true; //sets fire immunity true for nether ostriches if it becomes false, which does sometimes happen with world reloads.
        }

        if (getHiding())
        {
            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
        }

        if (mouthCounter > 0 && ++mouthCounter > 20)
        {
            mouthCounter = 0;
        }

        if (wingCounter > 0 && ++wingCounter > 80)
        {
            wingCounter = 0;
        }
        
        if (jumpCounter > 0 && ++jumpCounter > 8)
        {
            jumpCounter = 0;
        }

        if (sprintCounter > 0 && ++sprintCounter > 300)
        {
            sprintCounter = 0;
        }
        
        if (transformCounter > 0)
        {
            if (transformCounter == 40)
            {
                MoCTools.playCustomSound(this, "transform", worldObj);
            }

            if (++transformCounter > 100)
            {
                transformCounter = 0;
                if (transformType != 0)
                {
                    dropArmor();
                    setType(transformType);
                }
            }
        }
    }

    public void transform(int typeToTransformInto)
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), typeToTransformInto), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        transformType = typeToTransformInto;
        if (riddenByEntity == null && transformType != 0)
        {
            dropArmor();
            transformCounter = 1;
        }
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType >= 5 && animationType < 9) //transform 5 - 8
        {
            transformType = animationType;
            transformCounter = 1;
        }
        
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (
        		getType() == 8 //unihorn ostrich
    			&& (getHealth() < getMaxHealth())
    			&& rand.nextInt(100) == 0
    		)
    	{
    		heal(1);
    	}
        
        if (getIsTamed() && MoCreatures.isServer() && (rand.nextInt(300) == 0) && (getHealth() <= getMaxHealth()) && (deathTime == 0))
        {
            setHealth(getHealth() + 1);
        }

        if (MoCreatures.isServer())
        {
            //unihorn ostrich ramming!
            if (getType() == 8 && (sprintCounter > 0 && sprintCounter < 150) && (riddenByEntity != null))
            {
                MoCTools.buckleMobs(this, 2, 2D, worldObj);
            }
            //shy ostriches will run and hide
            if (!isNotScared() && fleeingTick > 0 && fleeingTick < 2)
            {
                fleeingTick = 0;
                setHiding(true);
                setPathToEntity(null);
            }

            if (getHiding())
            {
                //wild shy ostriches will hide their heads only for a short term
                //tamed ostriches will keep their heads hidden until the whip is used again
                if (++hidingCounter > 500 && !getIsTamed())
                {
                    setHiding(false);
                    hidingCounter = 0;
                }

            }

            //to add collision detection
            if (getType() == 1 && (rand.nextInt(200) == 0))
            {
                //when is chick and becomes adult, change over to different type
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                    setType(0);
                    selectType();
                }
            }

            //egg laying
            if (canLayEggs && (getType() == 2) && !getEggWatching() && --eggCounter <= 0 && rand.nextInt(5) == 0)// &&
            {
                EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 12D);
                if (entityPlayer1 != null)
                {
                    double distP = MoCTools.getSqDistanceTo(entityPlayer1, posX, posY, posZ);
                    if (distP < 10D)
                    {
                        int OstrichEggType = 30;
                        MoCEntityOstrich maleOstrich = getClosestMaleOstrich(this, 8D);
                        if (maleOstrich != null && rand.nextInt(100) < MoCreatures.proxy.ostrichEggDropChance)
                        {
                            MoCEntityEgg entityegg = new MoCEntityEgg(worldObj, OstrichEggType);
                            entityegg.setPosition(posX, posY, posZ);
                            worldObj.spawnEntityInWorld(entityegg);
    
                            if (!getIsTamed())
                            {
                                setEggWatching(true);
                                if (maleOstrich != null)
                                {
                                    maleOstrich.setEggWatching(true);
                                }
                                openMouth();
                            }
        
                            playSound("mob.chicken.plop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
                            eggCounter = rand.nextInt(2000) + 2000;
                            canLayEggs = false;
                        }
                    }
                }
            }

            //egg protection
            if (getEggWatching())
            {
                //look for and protect eggs and move close
                MoCEntityEgg myEgg = (MoCEntityEgg) MoCTools.getScaryEntity(this, 8D);
                if ((myEgg != null) && (MoCTools.getSqDistanceTo(myEgg, posX, posY, posZ) > 4D))
                {
                    PathEntity pathEntity = worldObj.getPathEntityToEntity(this, myEgg, 16F, true, false, false, true);
                    setPathToEntity(pathEntity);
                }
                if (myEgg == null) //didn't find egg
                {
                    setEggWatching(false);

                    EntityPlayer eggStealer = worldObj.getClosestPlayerToEntity(this, 10D);
                    if (eggStealer != null)
                    {
                        if (!getIsTamed() && worldObj.difficultySetting != EnumDifficulty.PEACEFUL)
                        {
                            entityToAttack = eggStealer;
                            flapWings();
                        }
                    }
                }
            }
        }
    }

    protected MoCEntityOstrich getClosestMaleOstrich(Entity entity, double d)
    {
        double d1 = -1D;
        MoCEntityOstrich entityLiving = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if (!(entity1 instanceof MoCEntityOstrich) || ((entity1 instanceof MoCEntityOstrich) && ((MoCEntityOstrich) entity1).getType() < 3))
            {
                continue;
            }

            double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)))
            {
                d1 = d2;
                entityLiving = (MoCEntityOstrich) entity1;
            }
        }

        return entityLiving;
    }

    @Override
    public boolean entitiesThatAreScary(Entity entity)
    {
        return ((entity instanceof MoCEntityEgg) && (((MoCEntityEgg) entity).eggType == 30)

        );
    }
    
    @Override
    public boolean isMyHealFood(ItemStack itemStack) //healing foods
    {
    	if (itemStack != null)
    	{
    		Item item = itemStack.getItem();
    		
    		List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemStack);
    		
    		return (
        				item instanceof ItemSeeds
        				|| (Item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
        				|| (Item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
        				|| oreDictionaryNameArray.size() > 0 &&
        					(
        						oreDictionaryNameArray.contains("listAllseed") //BOP seeds or Palm's Harvest Seeds
        						|| oreDictionaryNameArray.contains("foodRaisins") //GregTech6 seeds/raisins or Palm's Harvest raisins
        					)
    				);
    	}
    	
    	return false;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        
        ItemStack itemStack = entityPlayer.getHeldItem();
        
        EntityPlayer owner = worldObj.getPlayerEntityByName(getOwnerName());
        
        if (itemStack != null)
        {
        	Item item = itemStack.getItem();
        	
        	if (getIsTamed() && getType() > 1)
        	{
		        if (!getIsRideable() && (item == MoCreatures.craftedSaddle || item == Items.saddle))
		        {
		            if (--itemStack.stackSize == 0)
		            {
		                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		            }
		            setRideable(true);
		            return true;
		        }
		
		        if (interactIfItemstackisEssenceOfDarkness(entityPlayer, itemStack, owner, item)) {return true;}
		
		        if (interactIfItemstackisEssenceOfUndead(entityPlayer, itemStack, owner, item)) {return true;}
		
		        if (interactIfItemstackisEssenceOfLight(entityPlayer, itemStack, owner, item)) {return true;}
		
		        if (interactIfItemstackisEssenceOfFire(entityPlayer, itemStack, owner, item)) {return true;}
		        
		        if (getIsChested() && item == Item.getItemFromBlock(Blocks.wool))
		        {
		            int colorInt = (itemStack.getItemDamage());
		            if (colorInt == 0) colorInt = 16;
		            if (--itemStack.stackSize == 0)
		            {
		                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		            }
		            dropFlag();
		            setFlagColor((byte)colorInt);
		            entityPlayer.addStat(MoCAchievements.ostrich_flag, 1);
		            return true;
		        }
		        
		        if (!getIsChested() && item == Item.getItemFromBlock(Blocks.chest))
		        {
		             if (--itemStack.stackSize == 0)
		            {
		                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		            }
		
		            setIsChested(true);
		            entityPlayer.addStat(MoCAchievements.ostrich_chest, 1);
		            return true;
		        }
		
		        if (interactIfPlayerIsHoldingWearableHelmet(entityPlayer, item)) {return true;};
        	}
        	else if (getType() == 2 && item == Items.melon_seeds) //breeding item
	        {
	            if (--itemStack.stackSize == 0)
	            {
	                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
	            }
	
	            openMouth();
	            MoCTools.playCustomSound(this, "eating", worldObj);
	            canLayEggs = true;
	            return true;
	        }
        }
        
        if (	//try to mount player on ostrich - THIS MUST TO BE AT THE VERY LAST OF THE INTERACT FUNCTION so that any interactable items are used first before the player mounts the ostrich
        		(
        			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemStack == null)
    				|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
        		)
        	)
        {
	        	if (entityPlayer.isSneaking() && getIsChested())
		        {
		            // if first time opening horse chest, we must initialize it
		            if (localChest == null)
		            {
		                localChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.OstrichChest"), 9);
		            }
		            // only open this chest on server side
		            if (MoCreatures.isServer())
		            {
		                entityPlayer.displayGUIChest(localChest);
		            }
		            return true;
		        }
        	
	        	else if
	        		(
	        			!(entityPlayer.isSneaking())
	        			&& getIsRideable()
	        			&& getIsAdult()
	        			&& (riddenByEntity == null)
	        		)
	        	{
		            entityPlayer.rotationYaw = rotationYaw;
		            entityPlayer.rotationPitch = rotationPitch;
		            setHiding(false);
		            
		            if (!worldObj.isRemote && (riddenByEntity == null || riddenByEntity == entityPlayer))
		            {
		                entityPlayer.mountEntity(this);
		            }
		            return true;
	        	}
        }
        return false;
    }

	private boolean interactIfItemstackisEssenceOfDarkness(EntityPlayer entityPlayer, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		if (item == MoCreatures.essenceDarkness)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }
		    
		    if (getType() == 6) //wyvern ostrich
		    {
		        setHealth(getMaxHealth());
		    }
		    else
		    {
		        transform(6);
		        if (owner != null) {owner.addStat(MoCAchievements.wyvern_ostrich, 1);};
		    }
		    MoCTools.playCustomSound(this, "drinking", worldObj);
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackisEssenceOfUndead(EntityPlayer entityPlayer, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		if (item == MoCreatures.essenceUndead)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }
		    
		    if (getType() == 7) //undead ostrich
		    {
		        setHealth(getMaxHealth());
		    }
		    else
		    {
		        transform(7);
		        if (owner != null) {owner.addStat(MoCAchievements.undead_ostrich, 1);};
		    }
		    
		    MoCTools.playCustomSound(this, "drinking", worldObj);
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackisEssenceOfLight(EntityPlayer entityPlayer, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		if (item == MoCreatures.essenceLight)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }
		    
		    if (getType() == 8) //unihorn ostrich
		    {
		        setHealth(getMaxHealth());
		    }
		    else
		    {
		        transform(8);
		        if (owner != null) {owner.addStat(MoCAchievements.unihorn_ostrich, 1);};
		    }
		    
		    MoCTools.playCustomSound(this, "drinking", worldObj);
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackisEssenceOfFire(EntityPlayer entityPlayer, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		if (item == MoCreatures.essenceFire)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }
		    
		    if (getType() == 5) //nether ostrich
		    {
		        setHealth(getMaxHealth());
		    }
		    else
		    {
		        transform(5);
		        if (owner != null) {owner.addStat(MoCAchievements.nether_ostrich, 1);};
		    }
		    
		    MoCTools.playCustomSound(this, "drinking", worldObj);
		    return true;
		}
		return false;
	}

	private boolean interactIfPlayerIsHoldingWearableHelmet(EntityPlayer entityPlayer, Item item)
	{
		if (item instanceof ItemArmor)
		{
		    byte helmetType = 0;
		    if (item == Items.leather_helmet)
		    {
		        helmetType = 1;
		    }
		    else if (item == Items.iron_helmet)
		    {
		        helmetType = 2;
		    }
		    else if (item == Items.golden_helmet)
		    {
		        helmetType = 3;
		    }
		    else if (item == Items.diamond_helmet)
		    {
		        helmetType = 4;
		    }
		    else if (item == MoCreatures.helmetHide)
		    {
		        helmetType = 5;
		    }
		    else if (item == MoCreatures.helmetFur)
		    {
		        helmetType = 6;
		    }
		    else if (item == MoCreatures.helmetReptile)
		    {
		        helmetType = 7;
		    }
		    else if (item == MoCreatures.scorpHelmetDirt)
		    {
		        helmetType = 9;
		    }
		    else if (item == MoCreatures.scorpHelmetFrost)
		    {
		        helmetType = 10;
		    }
		    else if (item == MoCreatures.scorpHelmetCave)
		    {
		        helmetType = 11;
		    }
		    else if (item == MoCreatures.scorpHelmetNether)
		    {
		        helmetType = 12;
		    }

		    if (helmetType != 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		        dropArmor();
		        MoCTools.playCustomSound(this, "armoroff", worldObj);
		        setHelmet(helmetType);
		        entityPlayer.addStat(MoCAchievements.ostrich_helmet, 1);
		        return true;
		    }
		   
		}
		return false;
	}

    /**
     * Drops a block of the color of the flag if carrying one
     */
    private void dropFlag() 
    {
        if (MoCreatures.isServer() && getFlagColor() != 0)
        {
            int color = getFlagColor();
            if (color == 16) color = 0;
            EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Blocks.wool, 1, color));
            entityItem.delayBeforeCanPickup = 10;
            worldObj.spawnEntityInWorld(entityItem);
            setFlagColor((byte)0);
        }
    }

    private void openMouth()
    {
        mouthCounter = 1;
    }

    private void flapWings()
    {
        wingCounter = 1;
    }

    @Override
    protected String getHurtSound()
    {
        openMouth();
        return "mocreatures:ostrichhurt";
    }

    @Override
    protected String getLivingSound()
    {
        openMouth();
        if (getType() == 1) { return "mocreatures:ostrichchick"; }

        return "mocreatures:ostrichgrunt";
    }

    @Override
    protected String getDeathSound()
    {
        openMouth();
        return "mocreatures:ostrichdying";
    }

    @Override
    protected Item getDropItem()
    {
        boolean flag = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);
        if (flag && (getType() == 8)) // unicorn
        { return MoCreatures.unicornHorn; }
        if (getType() == 5 && flag) 
        { return MoCreatures.heartFire; }
        if (getType() == 6 && flag) // wyvern ostrich
        { return MoCreatures.heartDarkness; }
        if (getType() == 7 )
        {
            if (flag) { return MoCreatures.heartundead; }
            return Items.rotten_flesh;
        }
         return MoCreatures.ostrichRaw;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setRideable(nbtTagCompound.getBoolean("Saddle"));
        setEggWatching(nbtTagCompound.getBoolean("EggWatch"));
        setHiding(nbtTagCompound.getBoolean("Hiding"));
        setHelmet(nbtTagCompound.getByte("Helmet"));
        setFlagColor(nbtTagCompound.getByte("FlagColor"));
        setIsChested(nbtTagCompound.getBoolean("Bagged"));
        if (getIsChested())
        {
            NBTTagList nbtTagList = nbtTagCompound.getTagList("Items", 10);
            localChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.OstrichChest"), 18);
            for (int i = 0; i < nbtTagList.tagCount(); i++)
            {
                NBTTagCompound nbtTagCompound1 = nbtTagList.getCompoundTagAt(i);
                int j = nbtTagCompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < localChest.getSizeInventory())
                {
                    localChest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound1));
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Saddle", getIsRideable());
        nbtTagCompound.setBoolean("EggWatch", getEggWatching());
        nbtTagCompound.setBoolean("Hiding", getHiding());
        nbtTagCompound.setByte("Helmet", getHelmet());
        nbtTagCompound.setByte("FlagColor", getFlagColor());
        nbtTagCompound.setBoolean("Bagged", getIsChested());
        
        if (getIsChested() && localChest != null)
        {
            NBTTagList nbtTagList = new NBTTagList();
            for (int i = 0; i < localChest.getSizeInventory(); i++)
            {
                localItemstack = localChest.getStackInSlot(i);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) i);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbtTagList.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items", nbtTagList);
        }
    }
   
    @Override
    public boolean getCanSpawnHere()
    {
        if (	
        		!MoCreatures.isBiomesOPlentyLoaded
        		|| (MoCreatures.isBiomesOPlentyLoaded && checkSpawningBiome())
        		&& (getCanSpawnHereCreature() && getCanSpawnHereLiving())
        	)
        {
        	return super.getCanSpawnHere();  //don't let BigCats spawn in biomes that they are not supposed to spawn in
        }
       
        else {return false;}
    }
    

    @Override
    public int nameYOffset()
    {
        if (getType() > 1)
        {
            return -105;
        }
        else
        {
            return  (-5 - getMoCAge());
        }
    }

    @Override
    public double roperYOffset()
    {
        if (getType() > 1)
        {
            return 0D;
        }
        else
        {
            return (120 - getMoCAge()) * 0.01D;
        }
    }

    @Override
    public boolean updateMount()
    {
        return getIsTamed();
    }

    @Override
    public boolean forceUpdates()
    {
        return getIsTamed();
    }

    @Override
    public void dropMyStuff() 
    {
        if (MoCreatures.isServer())
        {
            dropArmor();
            MoCTools.dropSaddle(this, worldObj);
            
            if (getIsChested())
            {
               MoCTools.dropInventory(this, localChest);
               MoCTools.dropCustomItem(this, worldObj, new ItemStack(Blocks.chest, 1));
               setIsChested(false);
            }
        }
        
    }

    /**
     * Drops the helmet
     */
    @Override
    public void dropArmor() 
    {
        if (MoCreatures.isServer())
        {
            EntityItem entityItem = null;// = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Blocks.wool, 1, color));
            
            switch (getHelmet())
            {
            case 0:
            case 8:
                return;
                //break;
            case 1:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.leather_helmet, 1));
                break;
            case 2:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.iron_helmet, 1));
                break;
            case 3:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.golden_helmet, 1));
                break;
            case 4:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.diamond_helmet, 1));
                break;
            case 5:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.helmetHide, 1));
                break;
            case 6:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.helmetFur, 1));
                break;
            case 7:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.helmetReptile, 1));
                break;
            case 9:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetDirt, 1));
                break;
            case 10:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetFrost, 1));
                break;
            case 11:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetCave, 1));
                break;
            case 12:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetNether, 1));
                break;
            }

            if (entityItem != null)
            {
                entityItem.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(entityItem);
            }
            setHelmet((byte)0);
        }
    }

    @Override
    public boolean isFlyer()
    {
        return (getType() == 5 || getType() == 6);
    }

    @Override
    protected void fall(float f)
    {
        if (isFlyer()) { return; }
    }

    @Override
    protected double myFallSpeed()
    {
    	if (getType() == 6) //wyvern ostrich
    	{
    		return 0.90;
    	}
    	
        return 0.99D;
    }

    @Override
    protected double flyerThrust()
    {
        return 0.6D;
    }

    @Override
    protected float flyerFriction()
    {
        return 0.96F;
    }

    @Override
    public void makeEntityJump()
    {
        if (jumpCounter > 5)
        {
            //return;
            jumpCounter = 1;
        }
        if (jumpCounter == 0)
        {
        	if (isFlyer())
            {
        		MoCTools.playCustomSound(this, "wingflap", worldObj);
            }
            jumpPending = true;
            jumpCounter = 1;
        }
        
    }
    
    public boolean isUndead()
    {
        return getType() == 7;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        if (getType() == 7) 
        {
            return EnumCreatureAttribute.UNDEAD;
        }
        return super.getCreatureAttribute();
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }
}