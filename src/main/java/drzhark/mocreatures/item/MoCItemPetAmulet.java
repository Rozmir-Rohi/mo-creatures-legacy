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
    private int ageCounter;
    private String name;
    private float maxHealth;
    private float health;
    private int age;
    private int creatureType;
    private String spawnClass;
    private String ownerName;
    private int amuletType;
    private boolean adult;
    private int PetId;

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
    public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer entityPlayer)
    {	
    	
    	int amuletDurability = itemstack.getItemDamage();
       
        if (amuletDurability == 0) //empty fishnet
        {
            return itemstack;
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
                initAndReadNBT(itemstack);
                if (spawnClass.isEmpty())// || creatureType == 0)
                {
                    return itemstack;
                }
                try
                {
                    if (spawnClass.equalsIgnoreCase("MoCHorse"))
                    {
                        spawnClass = "WildHorse";
                    }
                    

                    EntityLiving tempLiving = MoCTools.spawnListByNameClass(spawnClass, worldObj);
                    
                    if (tempLiving != null && tempLiving instanceof IMoCEntity)
                    {
                        IMoCTameable storedCreature = (IMoCTameable) tempLiving;
                        
                        //if the player using the amulet is not the owner
            	        if (ownerName.length() > 0 && !(ownerName.equals(entityPlayer.getCommandSenderName())) && MoCreatures.instance.mapData != null)
            	        {
            	        	return itemstack;
            	        }
                        
                        
                        ((EntityLiving) storedCreature).setPosition(newPosX, newPosY, newPosZ);
                        storedCreature.setType(creatureType);
                        storedCreature.setTamed(true);
                        storedCreature.setName(name);
                        storedCreature.setOwnerPetId(PetId);
                        storedCreature.setOwner(entityPlayer.getCommandSenderName());
                        
                        
                        ((EntityLiving) storedCreature).getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth);
                        ((EntityLiving) storedCreature).setHealth(health);
                        storedCreature.setMoCAge(age);
                        storedCreature.setAdult(adult);
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
        return itemstack;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        PetId = nbt.getInteger("PetId");
        creatureType = nbt.getInteger("CreatureType");
        maxHealth = nbt.getFloat("MaxHealth");
        health = nbt.getFloat("Health");
        age = nbt.getInteger("Age");
        name = nbt.getString("Name");
        spawnClass = nbt.getString("SpawnClass");
        adult = nbt.getBoolean("Adult");
        ownerName = nbt.getString("OwnerName");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("PetID", PetId);
        nbt.setInteger("CreatureType", creatureType);
        nbt.setFloat("MaxHealth", maxHealth);
        nbt.setFloat("Health", health);
        nbt.setInteger("Age", age);
        nbt.setString("Name", name);
        nbt.setString("SpawnClass", spawnClass);
        nbt.setBoolean("Adult", adult);
        nbt.setString("OwnerName", ownerName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        icons = new IIcon[4];
        icons[0] = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":")); //empty fishnet
        icons[1] = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":") + "full"); //fishnet with generic fish
        icons[2] = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":")); //empty superamulet
        icons[3] = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":") + "full"); //full superamulet
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int par1)
    {
        if (amuletType == 1)
        {
            if (par1 < 1)
            {
                return icons[2];
            }
            return icons[3];
        }
        
        if (par1 < 1)
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
    public void addInformation(ItemStack itemstack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        initAndReadNBT(itemstack);
        if (spawnClass.length() > 0) par3List.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("entity.MoCreatures." + spawnClass + ".name"));  //Writes the name of the entity type to item desc
        if (name.length() > 0)    par3List.add(EnumChatFormatting.BLUE + name); //writes the pet name to item desc
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