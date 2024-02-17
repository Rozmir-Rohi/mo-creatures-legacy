package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.item.MoCEntityPlatform;
import drzhark.mocreatures.inventory.MoCAnimalChest;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityElephant extends MoCEntityTameableAnimal {

    public int sprintCounter;
    public int sitCounter;
    public MoCAnimalChest localElephantChest;
    public MoCAnimalChest localElephantChest2;
    public MoCAnimalChest localElephantChest3;
    public MoCAnimalChest localElephantChest4;
    public ItemStack localItemstack;
    boolean hasPlatform;
    public int tailCounter;
    private byte tuskUses;
    private byte temper;

    public MoCEntityElephant(World world)
    {
        super(world);
        setAdult(true);
        setTamed(false);
        setMoCAge(50);
        setSize(1.1F, 3F);
        stepHeight = 1.0F;

        if (MoCreatures.isServer())
        {
            if (rand.nextInt(4) == 0)
            {
                setAdult(false);
            }
            else
            {
                setAdult(true);
            }
        }
    }

    @Override
    public void selectType()
    {
        checkSpawningBiome(); //apply type from the biome it spawns in
        
        if ((getType() == 0) && checkSpawningBiome()) // if the type is still 0 and elephant can still spawn in the biome, make it an African or Asian elephant
        {
            int typeChance = rand.nextInt(100);
            if (typeChance <= 50)
            {
                setType(1);
            }
            else
            {
                setType(2);
            }
        }
        
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        setHealth(getMaxHealth());
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // harness: 0 nothing, 1 harness, 2 cabin
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // tusks: 0 nothing, 1 wood, 2 iron, 3 diamond
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // storage: 0 nothing, 1 chest, 2 chests....

    }

    public byte getTusks()
    {
        return (dataWatcher.getWatchableObjectByte(23));
    }

    public void setTusks(byte b)
    {
        dataWatcher.updateObject(23, Byte.valueOf(b));
    }

    public byte getArmorType()
    {
        return (dataWatcher.getWatchableObjectByte(22));
    }

    @Override
    public void setArmorType(byte b)
    {
        dataWatcher.updateObject(22, Byte.valueOf(b));
    }

    public byte getStorage()
    {
        return (dataWatcher.getWatchableObjectByte(24));
    }

    public void setStorage(byte b)
    {
        dataWatcher.updateObject(24, Byte.valueOf(b));
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
	        case 1:
	            return MoCreatures.proxy.getTexture("elephantafrican.png");
	        case 2:
	            return MoCreatures.proxy.getTexture("elephantindian.png");
	        case 3:
	            return MoCreatures.proxy.getTexture("mammoth.png");
	        case 4:
	            return MoCreatures.proxy.getTexture("mammothsonghua.png");
	        case 5:
	            return MoCreatures.proxy.getTexture("elephantindianpretty.png");
	        default:
	            return MoCreatures.proxy.getTexture("elephantafrican.png");
        }
    }

    public float calculateMaxHealth()
    {
        switch (getType())
        {
	        case 1:
	            return 40;
	        case 2:
	            return 30;
	        case 3:
	            return 50;
	        case 4:
	            return 60;
	        case 5:
	            return 40;
	
	        default:
	            return 30;
        }
    }

    @Override
    public double getCustomSpeed()
    {
        if (sitCounter != 0) { return 0D; }

        double speed = 0.5D;
        
        switch (getType())
        {
	        case 1:
	        	speed = 0.6D;
	        	break;
	        case 2:
	        	speed = 0.7D;
	        	break;
	        case 3:
	        	speed = 0.5D;
	        	break;
	        case 4:
	        	speed = 0.5D;
	        	break;
	        case 5:
	        	speed = 0.7D;
	        	break;
	        default:
	        	speed = 0.5D;
	        	break;
        }

        if (sprintCounter > 0 && sprintCounter < 150)
        {
            speed *= 1.5D;
        }
        if (sprintCounter > 150 && riddenByEntity != null)
        {
            speed *= 0.5D;
        }
        
        return speed;
    }

    @Override
    public void onLivingUpdate()
    {
    	if (entityToAttack != null && entityToAttack == riddenByEntity)
    	{
    		if (!(riddenByEntity instanceof EntityPlayer && riddenByEntity.getCommandSenderName().equals(getOwnerName()))) //if not the owner of this entity
    		{
    			riddenByEntity.mountEntity(null); //forcefully make the entity that is riding this entity dismount
    		}
    	}
    	
        if (tailCounter > 0 && ++tailCounter > 8)
        {
            tailCounter = 0;
        }

        if (rand.nextInt(200) == 0)
        {
            tailCounter = 1;
        }
        
        super.onLivingUpdate();
        
        if (sprintCounter > 0)
        {
            ++sprintCounter;

            if (sprintCounter > 300)
            {
                sprintCounter = 0;
            }
        }
        
        
        if (MoCreatures.isServer())
        {
            if (!getIsAdult() && (rand.nextInt(1000) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                }
            }
            
            if (MoCreatures.proxy.specialPetsDefendOwner)
            {
	            if (getIsTamed() && riddenByEntity == null) //defend owner if they are attacked by an entity
	        	{
	        		EntityPlayer ownerOfEntityThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(getOwnerName());
	        		
	        		if (ownerOfEntityThatIsOnline != null)
	        		{
	        			EntityLivingBase entityThatAttackedOwner = ownerOfEntityThatIsOnline.getAITarget();
	        			
	        			if (entityThatAttackedOwner != null)
	        			{
	        				entityToAttack = entityThatAttackedOwner;
	        			}
	        		}
	        	}
            }
            
            if (entityToAttack != null && getIsAdult() && (riddenByEntity == null) && (sprintCounter == 0)) //make elephant sprint to attack entity when not ridden
            {
            	sprintCounter = 1;
            }
            
            if ((sprintCounter > 0 && sprintCounter < 150) && (riddenByEntity != null))
            {
            	float ramAttackDamage = calculateAttackDamage()/2;
                MoCTools.buckleMobsNotPlayers(this, ramAttackDamage, 3D, worldObj);
            }
            
            if (getIsTamed() && (riddenByEntity == null) && getArmorType() >= 1 && rand.nextInt(20) == 0)
            {
                EntityPlayer playerNearby = worldObj.getClosestPlayerToEntity(this, 3D);
                if (playerNearby != null && (!MoCreatures.proxy.enableStrictOwnership || playerNearby.getCommandSenderName().equals(getOwnerName())) && playerNearby.isSneaking())
                {
                    sit();
                }
            }

            if (MoCreatures.proxy.elephantBulldozer && getIsTamed() && (riddenByEntity != null) && (getTusks() > 0) && sprintCounter > 0)
            {
                   int heightWithinToDestroyBlocks = 2;
                    if (getType() == 3)
                    {
                        heightWithinToDestroyBlocks = 3;
                    }
                    if (getType() == 4)
                    {
                        heightWithinToDestroyBlocks = 3;
                    }
                    int dmg = MoCTools.destroyBlocksInFront(this, 2D, getTusks(), heightWithinToDestroyBlocks);
                    checkTusks(dmg);
                
            }

            if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer)
            {
                if (sitCounter != 0 && getArmorType() >= 3 && !secondRider())
                {
                    List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(2D, 2D, 2D));
                    
                    int iterationLength = entitiesNearbyList.size();
                    
                    if (iterationLength > 0)
                    {
	                    for (int index = 0; index < iterationLength; index++)
	                    {
	                        Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	
	                        if (!(entityNearby instanceof EntityPlayer) || entityNearby == riddenByEntity)
	                        {
	                            continue;
	                        }
	
	                        if (((EntityPlayer) entityNearby).isSneaking())
	                        {
	                            mountSecondPlayer(entityNearby);
	                        }
	
	                    }
                    }
                }
                

            }

            if (riddenByEntity == null && rand.nextInt(100) == 0)
            {
                destroyPlatforms();
            }

        }

        if (sitCounter != 0)
        {
            if (++sitCounter > 100)
            {
                sitCounter = 0;
            }
        }
    }

    private boolean secondRider() 
    {
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(3D, 3D, 3D));
        
        int iterationLength = entitiesNearbyList.size();
        
        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	            if ((entityNearby instanceof MoCEntityPlatform) && (entityNearby.riddenByEntity != null))
	            {
	                return true;
	            }
	        }
        }
        return false;
    }

    /**
     * Checks if the tusks sets need to break or not
     * (wood = 59, stone = 131, iron = 250, diamond = 1561, gold = 32)
     * @param dmg
     */
    private void checkTusks(int dmg) 
    {
        tuskUses += (byte) dmg;
        if (
        		(getTusks() == 1 && tuskUses > 59) //wooden tusks
        		|| (getTusks() == 2 && tuskUses > 250) //iron tusks
        		|| (getTusks() == 3 && tuskUses > 1000) //diamond tusks
        	)
        {
            MoCTools.playCustomSound(this, "turtlehurt", worldObj);
            setTusks((byte) 0);
        }
    }

    private void destroyPlatforms()
    {
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(3D, 3D, 3D));
        
        int iterationLength = entitiesNearbyList.size();
        
        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	            if ((entityNearby instanceof MoCEntityPlatform))
	            {
	                entityNearby.setDead();
	            }
	        }
        }
    }

    private void sit()
    {
        sitCounter = 1;
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        setPathToEntity(null);
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType == 0) //sitting animation
        {
            sitCounter = 1;
            setPathToEntity(null);
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        
        if (itemstack != null) 
        {
        	Item item = itemstack.getItem();
        	
        	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
        	
        	if (interactIfItemIsFoodItem(entityPlayer, itemstack, item, oreDictionaryNameArray)) {return true;};
        	
        	if (getIsTamed() && getIsAdult())
        	{
        		if (getArmorType() == 0 && item == MoCreatures.elephantHarness)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setArmorType((byte) 1);
                    return true;
                }

                if (
                		getArmorType() >= 1 &&
                		(
                			(item == MoCreatures.elephantChest && getStorage() != 2))
                			|| (item == Item.getItemFromBlock(Blocks.chest) && isMammoth() && getStorage() != 4)
                		)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    }
                    
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    
                    if (item == MoCreatures.elephantChest)
                    {
	                    if (getStorage() == 0 )
	                    {
	                    	setStorage((byte) 1);
	                    	entityPlayer.inventory.addItemStackToInventory(new ItemStack(MoCreatures.key));
	                    	entityPlayer.addStat(MoCAchievements.elephant_chest, 1);
	                    	return true;
	                    }
	                    
	                    if (getStorage() == 1) 
	                    {
	                    	setStorage((byte) 2);
	                    	return true;
	                    }
                    }
                    
                    if (item == Item.getItemFromBlock(Blocks.chest)) //if isMammoth() is already checked in the first If statement
                    {
    	                //third storage unit for mammoths
    	                if (getStorage() == 2)
    	                {
    	                	setStorage((byte) 3);
    	                	return true;
    	                }
    	                
    	                //fourth storage unit for mammoths
    	                if (getStorage() == 3)
    	                {
    	                	setStorage((byte) 4);
    	                	return true;
    	                }
                    }
                    
                    return true;
                }

                //giving a garment to an indian elephant with an harness will make it pretty
                if (getArmorType() == 1 && getType() == 2 && item == MoCreatures.elephantGarment)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setArmorType((byte) 2);
                    setType(5);
                    entityPlayer.addStat(MoCAchievements.elephant_garment, 1);
                    return true;
                }

                //giving a howdah to a pretty indian elephant with a garment will attach the howdah
                if (getArmorType() == 2 && getType() == 5 && item == MoCreatures.elephantHowdah)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setArmorType((byte) 3);
                    entityPlayer.addStat(MoCAchievements.elephant_howdah, 1);
                    return true;
                }

                //giving a platform to mammoths with harness will attach the platform
                if (getArmorType() == 1 && isMammoth() && item == MoCreatures.mammothPlatform)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "armoroff", worldObj);
                    setArmorType((byte) 3);
                    entityPlayer.addStat(MoCAchievements.mammoth_platform, 1);
                    return true;
                }

                if (
                		item == MoCreatures.tusksWood
                		|| item == MoCreatures.tusksIron
                		|| item == MoCreatures.tusksDiamond
                	)
                {
                		
                		byte tuskType = 0;
                		
                		if (item == MoCreatures.tusksWood) {tuskType = 1;}
                		if (item == MoCreatures.tusksIron) {tuskType = 2;}
                		if (item == MoCreatures.tusksDiamond) {tuskType = 3;}
                		
                		if (--itemstack.stackSize == 0)
                	    {
                			entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                	    }
                		
                		MoCTools.playCustomSound(this, "armoroff", worldObj);
                	    dropTusks();
                	    tuskUses = 0;
                	    setTusks(tuskType);
                	    entityPlayer.addStat(MoCAchievements.elephant_tusks, 1);
                	    return true;
                }
        	}
            
            

            if (item == MoCreatures.key && getStorage() > 0)
            {
                if (tryToOpenElephantChest(entityPlayer)) {return true;};

            }
            if (getTusks() > 0 && (item == Items.shears)) 
            { 
                MoCTools.playCustomSound(this, "armoroff", worldObj);
                dropTusks();
                return true; 
            }
        }
        
        if (    //try to mount player on elephant - THIS MUST TO BE AT THE VERY LAST OF THE INTERACT FUNCTION so that any interactable items are used first before the player mounts the elephant
        		(
        			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
        			|| !(MoCreatures.proxy.emptyHandMountAndPickUpOnly)
        		)
        		&& getIsTamed() && getIsAdult() && getArmorType() >= 1 && sitCounter != 0
        	)
        {
            entityPlayer.rotationYaw = rotationYaw;
            entityPlayer.rotationPitch = rotationPitch;
            sitCounter = 0;
            entityPlayer.mountEntity(this);
            entityPlayer.addStat(MoCAchievements.mount_elephant, 1);
            
            return true;
        }
        
        return false;
    }

	private boolean tryToOpenElephantChest(EntityPlayer entityPlayer)
	{
		if (localElephantChest == null)
		{
		    localElephantChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
		}

		if (getStorage() == 1)
		{
		    // only open this chest on server side
		    if (MoCreatures.isServer())
		    {
		        entityPlayer.displayGUIChest(localElephantChest);
		    }
		    return true;
		}

		if (getStorage() == 2)
		{
		               
		    if (localElephantChest2 == null)
		    {
		        localElephantChest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
		    }
		    // only open this chest on server side
		    InventoryLargeChest doubleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), localElephantChest, localElephantChest2);
		    if (MoCreatures.isServer())
		    {
		        entityPlayer.displayGUIChest(doubleChest);
		    }
		    return true;
		}
		
		if (getStorage() == 3)
		{
		    if (localElephantChest2 == null)
		    {
		        localElephantChest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
		    }
		            
		    if (localElephantChest3 == null)
		    {
		        localElephantChest3 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
		    }
		    // only open this chest on server side
		    InventoryLargeChest doubleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), localElephantChest, localElephantChest2);
		    InventoryLargeChest tripleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), doubleChest, localElephantChest3);
		    
		    if (MoCreatures.isServer())
		    {
		        entityPlayer.displayGUIChest(tripleChest);
		    }
		    return true;
		}
		
		if (getStorage() == 4)
		{
		    if (localElephantChest2 == null)
		    {
		        localElephantChest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
		    }
		            
		    if (localElephantChest3 == null)
		    {
		        localElephantChest3 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
		    }
		    
		    if (localElephantChest4 == null)
		    {
		        localElephantChest4 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
		    }
		    // only open this chest on server side
		    InventoryLargeChest doubleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), localElephantChest, localElephantChest2);
		    InventoryLargeChest doubleChestb = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), localElephantChest3, localElephantChest4);
		    InventoryLargeChest fourChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), doubleChest, doubleChestb);
		    
		    if (MoCreatures.isServer())
		    {
		        entityPlayer.displayGUIChest(fourChest);
		    }
		    return true;
		}
		
		return false;
	}

	private boolean interactIfItemIsFoodItem(EntityPlayer entityPlayer, ItemStack itemstack, Item item, List<String> oreDictionaryNameArray)
	{
		if (	//general food
				item == MoCreatures.sugarLump
				|| item == Items.wheat
				|| (item.itemRegistry).getNameForObject(item).equals("tropicraft:coconutChunk")
				|| (item.itemRegistry).getNameForObject(item).equals("tropicraft:pineappleCubes")
				|| (item.itemRegistry).getNameForObject(item).equals("harvestcraft:coconutItem")
				|| oreDictionaryNameArray.size() > 0 &&
					(
							oreDictionaryNameArray.contains("listAllfruit") //BOP fruit or GregTech6 fruit or Palm's Harvest fruit
							|| oreDictionaryNameArray.contains("listAllwheats") //GregTech6 wheat items
							|| oreDictionaryNameArray.contains("listAllgrain") //Palm's Harvest wheat items
					)
				|| MoCreatures.isGregTech6Loaded &&
					(
						OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "itemGrass"
						|| OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "itemGrassDry"
						|| OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "cropGrain"
					)
			)
		{
		    
			if (--itemstack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		    }
		    
		    MoCTools.playCustomSound(this, "eating", worldObj);
		    heal(5);
		    
		    if (!getIsTamed() && !getIsAdult() && (item == MoCreatures.sugarLump)) //taming food
		    {
		    	++temper;
		    
		        if (MoCreatures.isServer() && !getIsAdult() && !getIsTamed() && temper >= 10)
		        {
		            setTamed(true);
		            MoCTools.tameWithName((EntityPlayerMP) entityPlayer, this);
		            entityPlayer.addStat(MoCAchievements.tame_elephant, 1);
		        }
			}
		    return true;
		}
		return false;
	}

    /**
     * Used to mount a second player on the elephant
     */
    private void mountSecondPlayer(Entity entity)
    {
        double yOffset = 2.0D;
        MoCEntityPlatform platform = new MoCEntityPlatform(worldObj, getEntityId(), yOffset, 1.25D);
        platform.setPosition(posX, posY + yOffset, posZ);
        worldObj.spawnEntityInWorld(platform);
        entity.mountEntity(platform);
    }

    /**
     * Drops tusks, makes sound
     */
    private void dropTusks()
    {
        if (!MoCreatures.isServer()) {return;}
        
        int tuskIndex = getTusks();
        Item tuskItemToDrop = MoCreatures.tusksWood; //default one as place holder
        
        if (tuskIndex == 1) {tuskItemToDrop = MoCreatures.tusksWood;}
        else if (tuskIndex == 2) {tuskItemToDrop = MoCreatures.tusksIron;}
        else if (tuskIndex == 3) {tuskItemToDrop = MoCreatures.tusksDiamond;}
        else {tuskItemToDrop = null;}

        if (tuskItemToDrop != null)
        {
	        EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(tuskItemToDrop, 1, tuskUses));
	        entityItem.delayBeforeCanPickup = 10;
	        worldObj.spawnEntityInWorld(entityItem);
        }
        
        setTusks((byte) 0);
        tuskUses = 0;
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
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
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentBiome = MoCTools.Biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SNOWY))
        {
            setType(3 + rand.nextInt(2));
            return true;
        }
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SAVANNA))
        {
        	if (!(currentBiome.biomeName.toLowerCase().contains("outback")))
        	{
	            setType(1);
	            return true;
        	}
        	else
        	{
        		return false; //don't spawn elephants in the outback biome from the Biomes O' Plenty mod. The code for this is continued in MoCEventHooks.java
        	}
        }

        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.JUNGLE))
        {
            setType(2);
            return true;
        }

        return true; //return true anyway to allow the elephant to spawn in the biome that isn't blacklisted
    }

    @Override
    public float getSizeFactor()
    {
        float size = 1.25F;

        switch (getType())
        {
	        case 4:
	            size *= 1.2F;
	            break;
	            
	        case 2:
	        case 5:
	            size *= 0.80F;
	            break;
        }

        if (!getIsAdult())
        {
            size *= (getMoCAge() * 0.01F);
        }
        
        return size;
    }

    

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        
        setTusks(nbtTagCompound.getByte("Tusks"));
        setArmorType(nbtTagCompound.getByte("Harness"));
        setStorage(nbtTagCompound.getByte("Storage"));
        tuskUses = nbtTagCompound.getByte("TuskUses");
        
        if (getStorage() > 0)
        {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("Items", 10);
            localElephantChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbtTagCompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbtTagCompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < localElephantChest.getSizeInventory())
                {
                    localElephantChest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound1));
                }
            }
        }
        if (getStorage() >= 2)
        {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("Items2", 10);
            localElephantChest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbtTagCompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbtTagCompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < localElephantChest2.getSizeInventory())
                {
                    localElephantChest2.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound1));
                }
            }
        }
        
        if (getStorage() >= 3)
        {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("Items3", 10);
            localElephantChest3 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbtTagCompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbtTagCompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < localElephantChest3.getSizeInventory())
                {
                    localElephantChest3.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound1));
                }
            }
        }
        
        if (getStorage() >= 4)
        {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("Items4", 10);
            localElephantChest4 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbtTagCompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbtTagCompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < localElephantChest4.getSizeInventory())
                {
                    localElephantChest4.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound1));
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        
        nbtTagCompound.setByte("Tusks", getTusks());
        nbtTagCompound.setByte("Harness", getArmorType());
        nbtTagCompound.setByte("Storage", getStorage());
        nbtTagCompound.setByte("TuskUses", tuskUses);
        
        if (getStorage() > 0 && localElephantChest != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < localElephantChest.getSizeInventory(); i++)
            {
                localItemstack = localElephantChest.getStackInSlot(i);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) i);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbttaglist.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items", nbttaglist);
        }

        if (getStorage() >= 2 && localElephantChest2 != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < localElephantChest2.getSizeInventory(); i++)
            {
                localItemstack = localElephantChest2.getStackInSlot(i);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) i);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbttaglist.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items2", nbttaglist);
        }

        if (getStorage() >= 3 && localElephantChest3 != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < localElephantChest3.getSizeInventory(); i++)
            {
                localItemstack = localElephantChest3.getStackInSlot(i);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) i);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbttaglist.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items3", nbttaglist);
        }

        if (getStorage() >= 4 && localElephantChest4 != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < localElephantChest4.getSizeInventory(); i++)
            {
                localItemstack = localElephantChest4.getStackInSlot(i);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) i);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbttaglist.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items4", nbttaglist);
        }
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return itemstack != null &&
        		(
        			itemstack.getItem() == Items.baked_potato
        			|| itemstack.getItem() == Items.bread
        			|| itemstack.getItem() == MoCreatures.haystack
                );
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null) && (ridingEntity == null);
    }

    @Override
    protected boolean isMovementCeased()
    {
        return (riddenByEntity != null) || sitCounter != 0;
    }

    @Override
    public void setType(int i)
    {
        dataWatcher.updateObject(19, Integer.valueOf(i));
    }

    @Override
    public void Riding()
    {
        if ((riddenByEntity != null) && (riddenByEntity instanceof EntityPlayer))
        {
            EntityPlayer entityPlayer = (EntityPlayer) riddenByEntity;
            List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1.0D, 0.0D, 1.0D));
            if (entitiesNearbyList != null)
            {
                for (int index = 0; index < entitiesNearbyList.size(); index++)
                {
                    Entity entityNearby = (Entity) entitiesNearbyList.get(index);
                    if (entityNearby.isDead)
                    {
                        continue;
                    }
                    entityNearby.onCollideWithPlayer(entityPlayer);
                }

            }
            
            if (entityPlayer.isSneaking())
            {
                if (MoCreatures.isServer())
                {
                    if (sitCounter == 0)
                    {
                        sit();
                    }
                    if (sitCounter >= 50)
                    {
                        entityPlayer.mountEntity(null);
                    }

                }
            }
        }
    }

    @Override
    public boolean canBePushed()
    {
        return riddenByEntity == null;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return riddenByEntity == null;
    }

    @Override
    public void updateRiderPosition()
    {   
        double horizontalOffset = (1.0D);
        
        switch (getType())
        {
	        case 1:
	        case 3:
	            
	            horizontalOffset = 0.8D;
	            break;
	        case 2:
	        case 5:
	            
	            horizontalOffset = 0.1D;
	            break;
	        case 4:
	            horizontalOffset = 1.2D;
	            
	            break;
        }

        double newPosX = posX - (horizontalOffset * Math.cos((MoCTools.realAngle(renderYawOffset - 90F)) / 57.29578F));
        double newPosZ = posZ - (horizontalOffset * Math.sin((MoCTools.realAngle(renderYawOffset - 90F)) / 57.29578F));
        riddenByEntity.setPosition(newPosX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), newPosZ);
    }

    @Override
    public double getMountedYOffset()
    {
        double yOffset = 0F;
        boolean isSitting = (sitCounter != 0);

        switch (getType())
        {
	        case 1:
	            yOffset = 0.55D;
	            if (isSitting) yOffset = -0.05D;
	            break;
	        case 3:
	            yOffset = 0.55D;
	            if (isSitting) yOffset = -0.05D;
	            break;
	        case 2:
	        case 5:
	            yOffset = 0.0D;
	            if (isSitting) yOffset = -0.5D;
	            break;
	        case 4:
	            yOffset = 1.2D;
	            if (isSitting) yOffset = 0.45D;
	            break;
        }
        return (double) (yOffset + (height * 0.75D));
    }

    
    
    /**
     * Had to set to false to avoid damage due to the collision boxes
     */
    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return false;
    }
    
    @Override
    public int getTalkInterval()
    {
        return 300;
    }
    
    
    @Override
    protected String getDeathSound()
    {
        return "mocreatures:elephantdying";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:elephanthurt";
    }

    @Override
    protected String getLivingSound()
    {
        if (!getIsAdult() && getMoCAge() < 80)
        {
            return "mocreatures:elephantcalf";
        }
        return "mocreatures:elephantgrunt";
    }
    
    @Override
    protected Item getDropItem()
    {
      return MoCreatures.hide;
    }
    
    @Override
    public boolean getCanSpawnHere()
    {
        return (
        			MoCreatures.entityMap.get(getClass()).getFrequency() > 0
        			&& getCanSpawnHereCreature()
        			&& getCanSpawnHereLiving()
        		);
    }
    
    @Override
    public void dropMyStuff() 
    {
        if (MoCreatures.isServer())
        {
            dropTusks();
            destroyPlatforms();
            
            if (getStorage()>0)
            {
                if (getStorage() > 0)
                {
                    MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.elephantChest, 1));
                    if (localElephantChest != null) MoCTools.dropInventory(this, localElephantChest);
                    
                }
                if (getStorage() >=2)
                {
                    if (localElephantChest2 != null) MoCTools.dropInventory(this, localElephantChest2);
                    MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.elephantChest, 1));
                }
                if (getStorage() >=3)
                {
                    if (localElephantChest3 != null) MoCTools.dropInventory(this, localElephantChest3);
                    MoCTools.dropCustomItem(this, worldObj, new ItemStack(Blocks.chest, 1));
                }
                if (getStorage() >=4)
                {
                    if (localElephantChest4 != null) MoCTools.dropInventory(this, localElephantChest4);
                    MoCTools.dropCustomItem(this, worldObj, new ItemStack(Blocks.chest, 1));
                }
                setStorage((byte) 0);
            }
            dropArmor();
        }
    }
    
    @Override
    public void dropArmor()
    {
        if (!MoCreatures.isServer()) {return;}
        
        if (getArmorType() >= 1)
        {
            MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.elephantHarness, 1));
        }
        
        if (getType() == 5 && getArmorType() >= 2)
        {
            
            MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.elephantGarment, 1));
            if (getArmorType() == 3)
            {
                MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.elephantHowdah, 1));
            }
            setType(2);
        }
        
        if (getType() == 4 && getArmorType() == 3)
        {
            MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.mammothPlatform, 1));
        }
        
        setArmorType((byte) 0);
        
    }

    @Override
    public int nameYOffset()
    {
        if (getIsAdult())
        {
            return (int) (getSizeFactor() * -110);
        }
        return (int) ((100/getMoCAge()) * (getSizeFactor() * -110));
    }

    @Override
    public double roperYOffset()
    {
        if (getIsAdult())
        {
            return getSizeFactor() * -0.5D;
        }
        return (double) ((100/getMoCAge()) * (getSizeFactor() * -0.5D));
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
            else if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
            
            else if (!(getIsAdult()) && (damageSource.getEntity() != null))
        	{
    			MoCTools.runAway(this, damageSource.getEntity()); //child runs away from attacking entity
    			
    			List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(10D, 10D, 10D));
    			for (int index = 0; index < entitiesNearbyList.size(); index++)
    	        {
    	            Entity entity1 = (Entity) entitiesNearbyList.get(index);
	                if (entity1 instanceof MoCEntityElephant) //set attack target of adults near by to the entity that is attacking that child
	                {
	                	MoCEntityElephant elephantEntityNearBy = (MoCEntityElephant) entity1;
	                	if (elephantEntityNearBy.getIsAdult());
	                	{	
	                		if (!(elephantEntityNearBy.getIsTamed()))
	                		{
	                			elephantEntityNearBy.entityToAttack = damageSource.getEntity();
	                		}
	                	}
	                	
	                	continue; 
	                }
	                
	                else {continue;}
    	        }    
        		return false;
        	}
            else if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting != worldObj.difficultySetting.PEACEFUL))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        
        if (attackTime <= 0 && (distanceToEntity < 2D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), calculateAttackDamage());
            
        }
    }
    
    private float calculateAttackDamage()
    {
    	if (getIsAdult() && !(getIsTamed())) {return 8;}
    	else if (getIsAdult() && getIsTamed()) {return (6 + getTusks());}
    	else {return 4;} //child attack damage
    }
    
    private boolean isMammoth()
    {
    	return (
    				getType() == 3
    				|| getType() == 4
    			);
    }

    @Override
    protected void fall(float f)
    {
        int i = (int) Math.ceil(f - 3F);
        if ((i > 0))
        {
            i /= 3;
            if (i > 0)
            {
                attackEntityFrom(DamageSource.fall, i);
            }
            if ((riddenByEntity != null) && (i > 0))
            {
                riddenByEntity.attackEntityFrom(DamageSource.fall, i);
            }

            Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.20000000298023221D - prevRotationPitch), MathHelper.floor_double(posZ));
            if (!block.isAir(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.20000000298023221D - prevRotationPitch), MathHelper.floor_double(posZ)))
            {
                Block.SoundType stepsound = block.stepSound;
                worldObj.playSoundAtEntity(this, stepsound.getStepResourcePath(), stepsound.getVolume() * 0.5F, stepsound.getPitch() * 0.75F);
            }
        }
    }
}