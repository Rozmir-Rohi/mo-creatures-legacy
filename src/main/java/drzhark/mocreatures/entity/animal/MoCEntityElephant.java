package drzhark.mocreatures.entity.animal;

import java.util.ArrayList;
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
    public MoCAnimalChest local_elephant_chest;
    public MoCAnimalChest local_elephant_chest2;
    public MoCAnimalChest local_elephant_chest3;
    public MoCAnimalChest local_elephant_chest4;
    public ItemStack local_stack;
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
        //health = 40;
        this.stepHeight = 1.0F;

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
            int i = rand.nextInt(100);
            if (i <= 50)
            {
                setType(1);
            }
            else
            {
                setType(2);
            }
        }
        
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        this.setHealth(getMaxHealth());
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
        if (sprintCounter > 150 && this.riddenByEntity != null)
        {
            speed *= 0.5D;
        }
        
        return speed;
    }

    @Override
    public void onLivingUpdate()
    {
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
	            if (this.getIsTamed() && this.riddenByEntity == null) //defend owner if they are attacked by an entity
	        	{
	        		EntityPlayer owner_of_entity_that_is_online = MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.getOwnerName());
	        		
	        		if (owner_of_entity_that_is_online != null)
	        		{
	        			EntityLivingBase entity_that_attacked_owner = owner_of_entity_that_is_online.getAITarget();
	        			
	        			if (entity_that_attacked_owner != null)
	        			{
	        				entityToAttack = entity_that_attacked_owner;
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
            	float ram_attack_damage = calculateAttackDamage()/2;
                MoCTools.buckleMobsNotPlayers(this, ram_attack_damage, 3D, worldObj);
            }
            
            if (getIsTamed() && (riddenByEntity == null) && getArmorType() >= 1 && rand.nextInt(20) == 0)
            {
                EntityPlayer player_nearby = worldObj.getClosestPlayerToEntity(this, 3D);
                if (player_nearby != null && (!MoCreatures.proxy.enableStrictOwnership || player_nearby.getCommandSenderName().equals(getOwnerName())) && player_nearby.isSneaking())
                {
                    sit();
                }
            }

            if (MoCreatures.proxy.elephantBulldozer && getIsTamed() && (riddenByEntity != null) && (getTusks() > 0) )
            {
                   int height = 2;
                    if (getType() == 3)
                    {
                        height = 3;
                    }
                    if (getType() == 4)
                    {
                        height = 3;
                    }
                    int dmg = MoCTools.destroyTreeBlocksInFront(this, 2D, this.getTusks(), height);
                    checkTusks(dmg);
                
            }

            if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer)
            {
                if (sitCounter != 0 && getArmorType() >= 3 && !secondRider())
                {
                    List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(2D, 2D, 2D));
                    for (int i = 0; i < list.size(); i++)
                    {
                        Entity entity1 = (Entity) list.get(i);

                        if (!(entity1 instanceof EntityPlayer) || entity1 == this.riddenByEntity)
                        {
                            continue;
                        }

                        if (((EntityPlayer) entity1).isSneaking())
                        {
                            mountSecondPlayer(entity1);
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
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(3D, 3D, 3D));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if ((entity1 instanceof MoCEntityPlatform) && (entity1.riddenByEntity != null))
            {
                return true;
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
        if ( (this.getTusks() == 1 && tuskUses > 59) || (this.getTusks() == 2 && tuskUses > 250) || (this.getTusks() == 3 && tuskUses > 1000))
        {
            MoCTools.playCustomSound(this, "turtlehurt", worldObj);
            setTusks((byte) 0);
        }
    }

    private void destroyPlatforms()
    {
        int j = 0;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(3D, 3D, 3D));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if ((entity1 instanceof MoCEntityPlatform))
            {
                entity1.setDead();
                j++;
            }
        }
    }

    private void sit()
    {
        sitCounter = 1;
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
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
    public boolean interact(EntityPlayer entityplayer)
    {
        if (super.interact(entityplayer)) { return false; }
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        
        if (itemstack != null) 
        {
        	Item item = itemstack.getItem();
        	
        	List<String> ore_dictionary_name_array = MoCTools.getOreDictionaryEntries(itemstack);
        	
        	if (//general food
        			item == MoCreatures.sugarlump
            		|| item == Items.wheat
            		|| (item.itemRegistry).getNameForObject(item).equals("tropicraft:coconutChunk")
            		|| (item.itemRegistry).getNameForObject(item).equals("tropicraft:pineappleCubes")
            		|| (item.itemRegistry).getNameForObject(item).equals("harvestcraft:coconutItem")
            		|| ore_dictionary_name_array.size() > 0 &&
            			(
            					ore_dictionary_name_array.contains("listAllfruit") //BOP fruit or GregTech6 fruit or Palm's Harvest fruit
            					|| ore_dictionary_name_array.contains("listAllwheats") //GregTech6 wheat items
            					|| ore_dictionary_name_array.contains("listAllgrain") //Palm's Harvest wheat items
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
                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                }
                
                MoCTools.playCustomSound(this, "eating", worldObj);
                heal(5);
                
                if (!getIsTamed() && !getIsAdult() && (item == MoCreatures.sugarlump)) //taming food
                {
                	++temper;
                
    	            if (MoCreatures.isServer() && !getIsAdult() && !getIsTamed() && temper >= 10)
    	            {
    	                setTamed(true);
    	                MoCTools.tameWithName((EntityPlayerMP) entityplayer, this);
    	                entityplayer.addStat(MoCAchievements.tame_elephant, 1);
    	            }
            	}
                return true;
            }
        	
        	if (getIsTamed() && getIsAdult())
        	{
        		if (getArmorType() == 0 && item == MoCreatures.elephantHarness)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setArmorType((byte) 1);
                    return true;
                }

                if (getArmorType() >= 1 && item == MoCreatures.elephantChest)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    
                    
                    if (getStorage() == 0 )
                    {
                    	setStorage((byte) 1);
                    	entityplayer.inventory.addItemStackToInventory(new ItemStack(MoCreatures.key));
                    	entityplayer.addStat(MoCAchievements.elephant_chest, 1);
                    }
                    
                    else if (getStorage() == 1) {setStorage((byte) 2);}
                    
                    return true;
                }
                // third storage unit for small mammoths
                if ((getType() == 3) && getArmorType() >= 1 && getStorage() == 2 && item == Item.getItemFromBlock(Blocks.chest))
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setStorage((byte) 3);
                    return true;
                }
                // fourth storage unit for small mammoths
                if ((getType() == 3) && getArmorType() >= 1 && getStorage() == 3 && item == Item.getItemFromBlock(Blocks.chest))
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setStorage((byte) 4);
                    return true;
                }

                //giving a garment to an indian elephant with an harness will make it pretty
                if (getArmorType() == 1 && getType() == 2 && item == MoCreatures.elephantGarment)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setArmorType((byte) 2);
                    setType(5);
                    entityplayer.addStat(MoCAchievements.elephant_garment, 1);
                    return true;
                }

                //giving a howdah to a pretty indian elephant with a garment will attach the howdah
                if (getArmorType() == 2 && getType() == 5 && item == MoCreatures.elephantHowdah)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "roping", worldObj);
                    setArmorType((byte) 3);
                    entityplayer.addStat(MoCAchievements.elephant_howdah, 1);
                    return true;
                }

                //giving a platform to a ? mammoth with harness will attach the platform
                if (getArmorType() == 1 && getType() == 4 && item == MoCreatures.mammothPlatform)
                {
                    if (--itemstack.stackSize == 0)
                    {
                        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                    }
                    MoCTools.playCustomSound(this, "armoroff", worldObj);
                    setArmorType((byte) 3);
                    entityplayer.addStat(MoCAchievements.mammoth_platform, 1);
                    return true;
                }

                if (item == MoCreatures.tusksWood
                		|| item == MoCreatures.tusksIron
                		|| item == MoCreatures.tusksDiamond)
                {
                		
                		byte tusk_type = 0;
                		
                		if (item == MoCreatures.tusksWood) {tusk_type = 1;}
                		if (item == MoCreatures.tusksIron) {tusk_type = 2;}
                		if (item == MoCreatures.tusksDiamond) {tusk_type = 3;}
                		
                		if (--itemstack.stackSize == 0)
                	    {
                			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
                	    }
                		
                		MoCTools.playCustomSound(this, "armoroff", worldObj);
                	    dropTusks();
                	    tuskUses = (byte) itemstack.getItemDamage();
                	    this.setTusks(tusk_type);
                	    entityplayer.addStat(MoCAchievements.elephant_tusks, 1);
                	    return true;
                }
        	}
            
            

            if ((item == MoCreatures.key) && getStorage() > 0)
            {
                if (local_elephant_chest == null)
                {
                    local_elephant_chest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
                }

                if (getStorage() == 1)
                {
                    // only open this chest on server side
                    if (MoCreatures.isServer())
                    {
                        entityplayer.displayGUIChest(local_elephant_chest);
                    }
                    return true;
                }

                if (getStorage() == 2)
                {
                               
                    if (local_elephant_chest2 == null)
                    {
                        local_elephant_chest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
                    }
                    // only open this chest on server side
                    InventoryLargeChest doubleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), local_elephant_chest, local_elephant_chest2);
                    if (MoCreatures.isServer())
                    {
                        entityplayer.displayGUIChest(doubleChest);
                    }
                    return true;
                }
                
                if (getStorage() == 3)
                {
                    if (local_elephant_chest2 == null)
                    {
                        local_elephant_chest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
                    }
                            
                    if (local_elephant_chest3 == null)
                    {
                        local_elephant_chest3 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
                    }
                    // only open this chest on server side
                    InventoryLargeChest doubleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), local_elephant_chest, local_elephant_chest2);
                    InventoryLargeChest tripleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), doubleChest, local_elephant_chest3);
                    
                    if (MoCreatures.isServer())
                    {
                        entityplayer.displayGUIChest(tripleChest);
                    }
                    return true;
                }
                
                if (getStorage() == 4)
                {
                    if (local_elephant_chest2 == null)
                    {
                        local_elephant_chest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
                    }
                            
                    if (local_elephant_chest3 == null)
                    {
                        local_elephant_chest3 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
                    }
                    
                    if (local_elephant_chest4 == null)
                    {
                        local_elephant_chest4 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
                    }
                    // only open this chest on server side
                    InventoryLargeChest doubleChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), local_elephant_chest, local_elephant_chest2);
                    InventoryLargeChest doubleChestb = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), local_elephant_chest3, local_elephant_chest4);
                    InventoryLargeChest fourChest = new InventoryLargeChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), doubleChest, doubleChestb);
                    
                    if (MoCreatures.isServer())
                    {
                        entityplayer.displayGUIChest(fourChest);
                    }
                    return true;
                }

            }
            if (getTusks() > 0 && (item == Items.shears)) 
            { 
                MoCTools.playCustomSound(this, "armoroff", worldObj);
                dropTusks();
                return true; 
            }
        }
        
        if (itemstack == null && getIsTamed() && getIsAdult() && getArmorType() >= 1 && sitCounter != 0)
        {
            entityplayer.rotationYaw = rotationYaw;
            entityplayer.rotationPitch = rotationPitch;
            sitCounter = 0;
            entityplayer.mountEntity(this);
            entityplayer.addStat(MoCAchievements.mount_elephant, 1);
            
            return true;
        }
        
        return false;
    }

    /**
     * Used to mount a second player on the elephant
     */
    private void mountSecondPlayer(Entity entity)
    {
        double yOff = 2.0D;
        MoCEntityPlatform platform = new MoCEntityPlatform(this.worldObj, this.getEntityId(), yOff, 1.25D);
        platform.setPosition(posX, posY + yOff, posZ);
        worldObj.spawnEntityInWorld(platform);
        //System.out.println("created platform " + platform.entityId);
        entity.mountEntity(platform);
    }

    /**
     * Drops tusks, makes sound
     */
    private void dropTusks()
    {
        if (!MoCreatures.isServer()) {return;}
        
        int tusk_index = getTusks();
        Item tusk_item_to_drop = MoCreatures.tusksWood; //default one as place holder
        
        if (tusk_index == 1) {tusk_item_to_drop = MoCreatures.tusksWood;}
        else if (tusk_index == 2) {tusk_item_to_drop = MoCreatures.tusksIron;}
        else if (tusk_index == 3) {tusk_item_to_drop = MoCreatures.tusksDiamond;}
        else {tusk_item_to_drop = null;}

        if (tusk_item_to_drop != null)
        {
	        EntityItem entityitem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, new ItemStack(tusk_item_to_drop, 1, tuskUses));
	        entityitem.delayBeforeCanPickup = 10;
	        worldObj.spawnEntityInWorld(entityitem);
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
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);

        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SNOWY))
        {
            setType(3 + rand.nextInt(2));
            return true;
        }
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SAVANNA))
        {
        	if (!(currentbiome.biomeName.toLowerCase().contains("outback")))
        	{
	            setType(1);
	            return true;
        	}
        	else
        	{
        		return false; //don't spawn elephants in the outback biome from the Biomes O' Plenty mod. The code for this is continued in MoCEventHooks.java
        	}
        }

        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.JUNGLE))
        {
            setType(2);
            return true;
        }

        return false;
    }

    @Override
    public float getSizeFactor()
    {
        float sizeF = 1.25F;

        switch (getType())
        {
        case 4:
            sizeF *= 1.2F;
            break;
            
        case 2:
        case 5:
            sizeF *= 0.80F;
            break;
        }

        if (!getIsAdult())
        {
            sizeF = sizeF * (getMoCAge() * 0.01F);
        }
        return sizeF;
    }

    

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setTusks(nbttagcompound.getByte("Tusks"));
        setArmorType(nbttagcompound.getByte("Harness"));
        setStorage(nbttagcompound.getByte("Storage"));
        tuskUses = nbttagcompound.getByte("TuskUses");
        if (getStorage() > 0)
        {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
            local_elephant_chest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < local_elephant_chest.getSizeInventory())
                {
                    local_elephant_chest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
                }
            }
        }
        if (getStorage() >= 2)
        {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Items2", 10);
            local_elephant_chest2 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 18);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < local_elephant_chest2.getSizeInventory())
                {
                    local_elephant_chest2.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
                }
            }
        }
        
        if (getStorage() >= 3)
        {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Items3", 10);
            local_elephant_chest3 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < local_elephant_chest3.getSizeInventory())
                {
                    local_elephant_chest3.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
                }
            }
        }
        
        if (getStorage() >= 4)
        {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Items4", 10);
            local_elephant_chest4 = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.ElephantChest"), 9);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < local_elephant_chest4.getSizeInventory())
                {
                    local_elephant_chest4.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound1));
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setByte("Tusks", getTusks());
        nbttagcompound.setByte("Harness", getArmorType());
        nbttagcompound.setByte("Storage", getStorage());
        nbttagcompound.setByte("TuskUses", this.tuskUses);
        
        if (getStorage() > 0 && local_elephant_chest != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < local_elephant_chest.getSizeInventory(); i++)
            {
                local_stack = local_elephant_chest.getStackInSlot(i);
                if (local_stack != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    local_stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbttagcompound.setTag("Items", nbttaglist);
        }

        if (getStorage() >= 2 && local_elephant_chest2 != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < local_elephant_chest2.getSizeInventory(); i++)
            {
                local_stack = local_elephant_chest2.getStackInSlot(i);
                if (local_stack != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    local_stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbttagcompound.setTag("Items2", nbttaglist);
        }

        if (getStorage() >= 3 && local_elephant_chest3 != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < local_elephant_chest3.getSizeInventory(); i++)
            {
                local_stack = local_elephant_chest3.getStackInSlot(i);
                if (local_stack != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    local_stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbttagcompound.setTag("Items3", nbttaglist);
        }

        if (getStorage() >= 4 && local_elephant_chest4 != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < local_elephant_chest4.getSizeInventory(); i++)
            {
                local_stack = local_elephant_chest4.getStackInSlot(i);
                if (local_stack != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    local_stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbttagcompound.setTag("Items4", nbttaglist);
        }
    }

    @Override
    public boolean isMyHealFood(ItemStack par1ItemStack)
    {
        return par1ItemStack != null &&(
                par1ItemStack.getItem() == Items.baked_potato
                || par1ItemStack.getItem() == Items.bread
                || par1ItemStack.getItem() == MoCreatures.haystack
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
            EntityPlayer entityplayer = (EntityPlayer) riddenByEntity;
            List entities_near_elephant_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1.0D, 0.0D, 1.0D));
            if (entities_near_elephant_list != null)
            {
                for (int i = 0; i < entities_near_elephant_list.size(); i++)
                {
                    Entity entity_nearby = (Entity) entities_near_elephant_list.get(i);
                    if (entity_nearby.isDead)
                    {
                        continue;
                    }
                    entity_nearby.onCollideWithPlayer(entityplayer);
                }

            }
            
            if (entityplayer.isSneaking())
            {
                if (MoCreatures.isServer())
                {
                    if (sitCounter == 0)
                    {
                        sit();
                    }
                    if (sitCounter >= 50)
                    {
                        entityplayer.mountEntity(null);
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
        
        double dist = (1.0D);
        switch (getType())
        {
        case 1:
        case 3:
            
            dist = 0.8D;
            break;
        case 2:
        case 5:
            
            dist = 0.1D;
            break;
        case 4:
            dist = 1.2D;
            
            break;
        }

        double newPosX = posX - (dist * Math.cos((MoCTools.realAngle(renderYawOffset - 90F)) / 57.29578F));
        double newPosZ = posZ - (dist * Math.sin((MoCTools.realAngle(renderYawOffset - 90F)) / 57.29578F));
        riddenByEntity.setPosition(newPosX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), newPosZ);
    }

    @Override
    public double getMountedYOffset()
    {
        double yOff = 0F;
        boolean sit = (sitCounter != 0);

        switch (getType())
        {
        case 1:
            yOff = 0.55D;
            if (sit) yOff = -0.05D;
            break;
        case 3:
            yOff = 0.55D;
            if (sit) yOff = -0.05D;
            break;
        case 2:
        case 5:
            yOff = 0.0D;
            if (sit) yOff = -0.5D;
            break;
        case 4:
            yOff = 1.2D;
            if (sit) yOff = 0.45D;
            break;
        }
        return (double) (yOff + (this.height * 0.75D));
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
      return MoCreatures.animalHide;
    }
    
    @Override
    public boolean getCanSpawnHere()
    {
        return (MoCreatures.entityMap.get(this.getClass()).getFrequency() > 0) && getCanSpawnHereCreature() && getCanSpawnHereLiving();
    }
    
    @Override
    public void dropMyStuff() 
    {
        if (MoCreatures.isServer())
        {
            dropTusks();
            destroyPlatforms();
            //dropSaddle(this, worldObj);
            if (getStorage()>0)
            {
                if (getStorage() > 0)
                {
                    MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(MoCreatures.elephantChest, 1));
                    if (local_elephant_chest != null) MoCTools.dropInventory(this, local_elephant_chest);
                    
                }
                if (getStorage() >=2)
                {
                    if (local_elephant_chest2 != null) MoCTools.dropInventory(this, local_elephant_chest2);
                    MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(MoCreatures.elephantChest, 1));
                }
                if (getStorage() >=3)
                {
                    if (local_elephant_chest3 != null) MoCTools.dropInventory(this, local_elephant_chest3);
                    MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(Blocks.chest, 1));
                }
                if (getStorage() >=4)
                {
                    if (local_elephant_chest4 != null) MoCTools.dropInventory(this, local_elephant_chest4);
                    MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(Blocks.chest, 1));
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
            MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(MoCreatures.elephantHarness, 1));
        }
        if (getType() == 5 && getArmorType() >= 2)
        {
            
            MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(MoCreatures.elephantGarment, 1));
            if (getArmorType() == 3)
            {
                MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(MoCreatures.elephantHowdah, 1));
            }
            setType(2);
        }
        if (getType() == 4 && getArmorType() == 3)
        {
            MoCTools.dropCustomItem(this, this.worldObj, new ItemStack(MoCreatures.mammothPlatform, 1));
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
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        if (super.attackEntityFrom(damagesource, i))
        {
            Entity entity = damagesource.getEntity();
            if (entity != null && getIsTamed() && (entity instanceof EntityPlayer && (entity.getCommandSenderName().equals(getOwnerName()))))
            { 
            	return false; 
            }
            else if ((riddenByEntity == entity) || (ridingEntity == entity)) { return true; }
            
            else if (!(this.getIsAdult()) && (damagesource.getEntity() != null))
        	{
    			MoCTools.runLikeHell(this, damagesource.getEntity()); //child runs away from attacking entity
    			
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
	                			elephantEntityNearBy.entityToAttack = damagesource.getEntity();
	                		}
	                	}
	                	
	                	continue; 
	                }
	                
	                else {continue;}
    	        }    
        		return false;
        	}
            else if ((entity != this) && (worldObj.difficultySetting != worldObj.difficultySetting.PEACEFUL))
            {
                entityToAttack = entity;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
        
        if (this.attackTime <= 0 && (f < 2D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
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