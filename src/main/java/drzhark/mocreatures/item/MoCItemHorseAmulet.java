package drzhark.mocreatures.item;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCPetData;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAppear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class MoCItemHorseAmulet extends MoCItem {

    private int ageCounter;
    private String name;
    private float health;
    private int age;
    private int creatureType;
    private int spawnClass;
    private boolean rideable;
    private byte armor;
    private boolean adult;
    private String ownerName;
    private int PetId;
    
    public MoCItemHorseAmulet(String name)
    {
        super(name);
        maxStackSize = 1;
        setHasSubtypes(true);
        ageCounter = 0;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer entityPlayer)
    {
    	//if the player using the amulet is not the owner
        if (ownerName.length() > 0 && !(ownerName.equals(entityPlayer.getCommandSenderName())) && MoCreatures.instance.mapData != null)
        {
        	return itemstack;
        }
    	
        if (++ageCounter < 2) { return itemstack; }

        int amulet_durability = itemstack.getItemDamage();

        if (MoCreatures.isServer())
        {
            initAndReadNBT(itemstack);
        }
        
        if (spawnClass == 21 || spawnClass == 0) // horses or old amulets
        {
            //dirty fix for old amulets
            spawnClass = 22;
            if (spawnClass == 0 || creatureType == 0)
            {
                creatureType = amulet_durability;
                spawnClass = 22;
                age = 100;
                health = 20;
                armor = 0;
                name = "";
                ownerName = "";
                rideable = false;
                adult = true;
            }
        }

        if (amulet_durability != 0)
        {

            double dist = 3D;
            double newPosY = entityPlayer.posY;
            double newPosX = entityPlayer.posX - (dist * Math.cos((MoCTools.realAngle(entityPlayer.rotationYaw - 90F)) / 57.29578F));
            double newPosZ = entityPlayer.posZ - (dist * Math.sin((MoCTools.realAngle(entityPlayer.rotationYaw - 90F)) / 57.29578F));

            if (MoCreatures.isServer())
            {
                try
                {
                    MoCEntityTameableAnimal storedCreature = new MoCEntityHorse(worldObj); 
                    storedCreature.setPosition(newPosX, newPosY, newPosZ);
                    storedCreature.setType(creatureType);
                    storedCreature.setTamed(true);
                    storedCreature.setRideable(rideable);
                    storedCreature.setMoCAge(age);
                    storedCreature.setName(name);
                    storedCreature.setArmorType(armor);
                    storedCreature.setHealth(health);
                    storedCreature.setAdult(adult);
                    storedCreature.setOwnerPetId(PetId);
                    storedCreature.setOwner(entityPlayer.getCommandSenderName());

                    if (entityPlayer.worldObj.spawnEntityInWorld(storedCreature))
                    {
                        MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAppear(storedCreature.getEntityId()), new TargetPoint(entityPlayer.worldObj.provider.dimensionId, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, 64));
                        MoCTools.playCustomSound(storedCreature, "appearmagic", worldObj);
                        //gives an empty amulet
                        if (creatureType == 26 || creatureType == 27 || creatureType == 28)
                        {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(MoCreatures.amuletBone, 1, 0));
                        }
                        else if (creatureType == 21 || creatureType == 22)
                        {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(MoCreatures.amuletGhost, 1, 0));
                        }
                        else if ((creatureType > 47 && creatureType < 60))
                        {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(MoCreatures.amuletFairy, 1, 0));
                        }
                        else if (creatureType == 39 || creatureType == 40)
                        {
                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(MoCreatures.amuletPegasus, 1, 0));
                        }
                        MoCPetData petData = MoCreatures.instance.mapData.getPetData(storedCreature.getOwnerName());
                        if (petData != null)
                        {
                            petData.setInAmulet(storedCreature.getOwnerPetId(), false);
                        }
                    }
                }catch (Exception ex) 
                {
                    System.out.println("Error spawning creature from amulet " + ex);
                }
            }
            ageCounter = 0;
       }

        return itemstack;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        PetId = nbt.getInteger("PetId");
        creatureType = nbt.getInteger("CreatureType");
        health = nbt.getFloat("Health");
        age = nbt.getInteger("Age");
        name = nbt.getString("Name");
        spawnClass = nbt.getInteger("SpawnClass");
        rideable = nbt.getBoolean("Rideable");
        armor = nbt.getByte("Armor");
        adult = nbt.getBoolean("Adult");
        ownerName = nbt.getString("OwnerName");
    }
    
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("PetID", PetId);
        nbt.setInteger("CreatureType", creatureType);
        nbt.setFloat("Health", health);
        nbt.setInteger("Age", age);
        nbt.setString("Name", name);
        nbt.setInteger("SpawnClass", spawnClass);
        nbt.setBoolean("Rideable", rideable);
        nbt.setByte("Armor", armor);
        nbt.setBoolean("Adult", adult);
        nbt.setString("OwnerName", ownerName);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        initAndReadNBT(itemstack);
        if (name.length() > 0) par3List.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("entity.MoCreatures.WildHorse.name")); //Writes the name of the entity type to item desc
        if (name.length() > 0) par3List.add(EnumChatFormatting.BLUE + name); //writes the pet name to item desc
        if (ownerName.length() > 0) par3List.add(EnumChatFormatting.DARK_BLUE + ((new ChatComponentTranslation("amulet_and_fishnet_desc.MoCreatures.ownedBy", new Object[] {ownerName})).getUnformattedTextForChat())); //writes "owned by OWNER" (dependent on lang files)in item desc
    }
    
    private void initAndReadNBT(ItemStack itemstack)
    {
        if( itemstack.stackTagCompound == null )
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbtcompound = itemstack.stackTagCompound;
        readFromNBT(nbtcompound);
    }
}