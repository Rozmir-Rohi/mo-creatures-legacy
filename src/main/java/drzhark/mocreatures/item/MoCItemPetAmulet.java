package drzhark.mocreatures.item;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCPetData;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.IMoCTameable;
import drzhark.mocreatures.entity.animal.MoCEntityKitty;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAppear;
import drzhark.mocreatures.utils.MoCLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class MoCItemPetAmulet extends MoCItem
{
    private IIcon[] icons;
    private String name;
    private float maxHealth;
    private float health;
    private int age;
    private int creatureType;
    private String spawnClass;
    private String ownerName;
    private int amuletType;
    private boolean isAdult;
    private int petId;

    public MoCItemPetAmulet(String name) 
    {
        super(name);
        maxStackSize = 1;
        setHasSubtypes(true);
    }

    public MoCItemPetAmulet(String name, int type) 
    {
        this(name);
        amuletType = type;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer entityPlayer)
    {	
    	
    	int amuletDurability = itemStack.getItemDamage();
       
        if (amuletDurability == 0) //empty fishnet
        {
            return itemStack;
        }

        if (amuletDurability != 0)
        {

            double dist = 1D;
            double newPosY = entityPlayer.posY;
            double newPosX = entityPlayer.posX - (dist * Math.cos((MoCTools.realAngle(entityPlayer.rotationYaw - 90F)) / 57.29578F));
            double newPosZ = entityPlayer.posZ - (dist * Math.sin((MoCTools.realAngle(entityPlayer.rotationYaw - 90F)) / 57.29578F));

            ItemStack emptyAmulet = new ItemStack(MoCreatures.fishNet, 1, 0);
            if (amuletType == 1)
            {
                emptyAmulet = new ItemStack(MoCreatures.petAmulet, 1, 0);
            }

            //entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(MoCreatures.fishnet, 1, 0));
            if (MoCreatures.isServer())
            {
                initAndReadNBT(itemStack);
                if (spawnClass.isEmpty())// || creatureType == 0)
                {
                    return itemStack;
                }
                try
                {
                    if (spawnClass.equalsIgnoreCase("MoCHorse"))
                    {
                        spawnClass = "Horse";
                    }
                    

                    EntityLiving tempLiving = MoCTools.spawnListByNameClass(spawnClass, worldObj);
                    
                    if (tempLiving != null && tempLiving instanceof IMoCEntity)
                    {
                        IMoCTameable storedCreature = (IMoCTameable) tempLiving;
                        
                        //if the player using the amulet is not the owner
            	        if (ownerName.length() > 0 && !(ownerName.equals(entityPlayer.getCommandSenderName())) && MoCreatures.instance.mapData != null)
            	        {
            	        	return itemStack;
            	        }
                        
                        
                        ((EntityLiving) storedCreature).setPosition(newPosX, newPosY, newPosZ);
                        storedCreature.setType(creatureType);
                        storedCreature.setTamed(true);
                        storedCreature.setName(name);
                        storedCreature.setOwnerPetId(petId);
                        storedCreature.setOwner(entityPlayer.getCommandSenderName());
                        
                        
                        ((EntityLiving) storedCreature).getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
                        ((EntityLiving) storedCreature).setHealth(health);
                        storedCreature.setMoCAge(age);
                        storedCreature.setAdult(isAdult);
                        // special case for kitty
                        if (spawnClass.equalsIgnoreCase("Kitty"))
                        {
                            ((MoCEntityKitty)storedCreature).setKittyState(2); // allows name to render
                        }

                        if (entityPlayer.worldObj.spawnEntityInWorld((EntityLiving)storedCreature))
                        { 	
                            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAppear(((EntityLiving)storedCreature).getEntityId()), new TargetPoint(entityPlayer.worldObj.provider.dimensionId, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, 64));
                            if ((MoCreatures.proxy.enableStrictOwnership && ownerName.isEmpty()) || name.isEmpty()) 
                            {
                                 MoCTools.tameWithName(entityPlayer, storedCreature);
                            }

                            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, emptyAmulet);
                            MoCPetData petData = MoCreatures.instance.mapData.getPetData(storedCreature.getOwnerName());
                            if (petData != null)
                            {	
                                petData.setInAmulet(storedCreature.getOwnerPetId(), false);
                            }
                        }
                    }
                }catch (Exception ex) 
                {
                    if (MoCreatures.proxy.debug) MoCLog.logger.warn("Error spawning creature from fishnet/amulet " + ex);
                }
            }
       }
        return itemStack;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        petId = nbt.getInteger("PetId");
        creatureType = nbt.getInteger("CreatureType");
        maxHealth = nbt.getFloat("MaxHealth");
        health = nbt.getFloat("Health");
        age = nbt.getInteger("Age");
        name = nbt.getString("Name");
        spawnClass = nbt.getString("SpawnClass");
        isAdult = nbt.getBoolean("Adult");
        ownerName = nbt.getString("OwnerName");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("PetID", petId);
        nbt.setInteger("CreatureType", creatureType);
        nbt.setFloat("MaxHealth", maxHealth);
        nbt.setFloat("Health", health);
        nbt.setInteger("Age", age);
        nbt.setString("Name", name);
        nbt.setString("SpawnClass", spawnClass);
        nbt.setBoolean("Adult", isAdult);
        nbt.setString("OwnerName", ownerName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[4];
        icons[0] = iconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":")); //empty fishnet
        icons[1] = iconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":") + "full"); //fishnet with generic fish
        icons[2] = iconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":")); //empty superamulet
        icons[3] = iconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":") + "full"); //full superamulet
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int itemDamage)
    {
        if (amuletType == 1)
        {
            if (itemDamage < 1)
            {
                return icons[2];
            }
            return icons[3];
        }
        
        if (itemDamage < 1)
        {
            return icons[0];
        }
        return icons[1];
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        initAndReadNBT(itemStack);
        if (spawnClass.length() > 0) par3List.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("entity.MoCreatures." + spawnClass + ".name"));  //Writes the name of the entity type to item desc
        if (name.length() > 0)    par3List.add(EnumChatFormatting.BLUE + name); //writes the pet name to item desc
        if (ownerName.length() > 0) par3List.add(EnumChatFormatting.DARK_BLUE + ((new ChatComponentTranslation("amulet_and_fishnet_desc.MoCreatures.ownedBy", new Object[] {ownerName})).getUnformattedTextForChat())); //writes "owned by OWNER" (dependent on lang files)in item desc
    }
    
    private void initAndReadNBT(ItemStack itemStack)
    {
        if( itemStack.stackTagCompound == null )
        {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbtcompound = itemStack.stackTagCompound;
        readFromNBT(nbtcompound);
    }
}