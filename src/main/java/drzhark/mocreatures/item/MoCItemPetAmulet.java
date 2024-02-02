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
        this.amuletType = type;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer entityplayer)
    {	
    	
    	int amulet_durability = itemstack.getItemDamage();
       
        if (amulet_durability == 0) //empty fishnet
        {
            return itemstack;
        }

        if (amulet_durability != 0)
        {

            double dist = 1D;
            double newPosY = entityplayer.posY;
            double newPosX = entityplayer.posX - (dist * Math.cos((MoCTools.realAngle(entityplayer.rotationYaw - 90F)) / 57.29578F));
            double newPosZ = entityplayer.posZ - (dist * Math.sin((MoCTools.realAngle(entityplayer.rotationYaw - 90F)) / 57.29578F));

            ItemStack emptyAmulet = new ItemStack(MoCreatures.fishnet, 1, 0);
            if (this.amuletType == 1)
            {
                emptyAmulet = new ItemStack(MoCreatures.petamulet, 1, 0);
            }

            //entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(MoCreatures.fishnet, 1, 0));
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
            	        if (ownerName.length() > 0 && !(ownerName.equals(entityplayer.getCommandSenderName())) && MoCreatures.instance.mapData != null)
            	        {
            	        	return itemstack;
            	        }
                        
                        
                        ((EntityLiving)storedCreature).setPosition(newPosX, newPosY, newPosZ);
                        storedCreature.setType(creatureType);
                        storedCreature.setTamed(true);
                        storedCreature.setName(name);
                        storedCreature.setOwnerPetId(PetId);
                        storedCreature.setOwner(entityplayer.getCommandSenderName());
                        
                        ((EntityLiving)storedCreature).setHealth(health);
                        storedCreature.setMoCAge(age);
                        storedCreature.setAdult(adult);
                        // special case for kitty
                        if (spawnClass.equalsIgnoreCase("Kitty"))
                        {
                            ((MoCEntityKitty)storedCreature).setKittyState(2); // allows name to render
                        }

                        if (entityplayer.worldObj.spawnEntityInWorld((EntityLiving)storedCreature))
                        { 	
                            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAppear(((EntityLiving)storedCreature).getEntityId()), new TargetPoint(entityplayer.worldObj.provider.dimensionId, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 64));
                            if ((MoCreatures.proxy.enableStrictOwnership && ownerName.isEmpty()) || name.isEmpty()) 
                            {
                                 MoCTools.tameWithName(entityplayer, storedCreature);
                            }

                            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, emptyAmulet);
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
        this.PetId = nbt.getInteger("PetId");
        this.creatureType = nbt.getInteger("CreatureType");
        this.health = nbt.getFloat("Health");
        this.age = nbt.getInteger("Age");
        this.name = nbt.getString("Name");
        this.spawnClass = nbt.getString("SpawnClass");
        this.adult = nbt.getBoolean("Adult");
        this.ownerName = nbt.getString("OwnerName");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("PetID", this.PetId);
        nbt.setInteger("CreatureType", this.creatureType);
        nbt.setFloat("Health", this.health);
        nbt.setInteger("Age", this.age);
        nbt.setString("Name", this.name);
        nbt.setString("SpawnClass", this.spawnClass);
        nbt.setBoolean("Adult", this.adult);
        nbt.setString("OwnerName", this.ownerName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        icons = new IIcon[4];
        icons[0] = par1IconRegister.registerIcon("mocreatures"+ this.getUnlocalizedName().replaceFirst("item.", ":")); //empty fishnet
        icons[1] = par1IconRegister.registerIcon("mocreatures"+ this.getUnlocalizedName().replaceFirst("item.", ":") + "full"); //fishnet with generic fish
        icons[2] = par1IconRegister.registerIcon("mocreatures"+ this.getUnlocalizedName().replaceFirst("item.", ":")); //empty superamulet
        icons[3] = par1IconRegister.registerIcon("mocreatures"+ this.getUnlocalizedName().replaceFirst("item.", ":") + "full"); //full superamulet
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int par1)
    {
        if (this.amuletType == 1)
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
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        initAndReadNBT(par1ItemStack);
        if (spawnClass != "") par3List.add(EnumChatFormatting.AQUA + StatCollector.translateToLocal("entity.MoCreatures." + this.spawnClass + ".name"));  //Writes the name of the entity type to item desc
        if (name != "")    par3List.add(EnumChatFormatting.BLUE + this.name); //writes the pet name to item desc
        if (ownerName != "") par3List.add(EnumChatFormatting.DARK_BLUE + ((new ChatComponentTranslation("amulet_and_fishnet_desc.MoCreatures.ownedBy", new Object[] {this.ownerName})).getUnformattedTextForChat())); //writes "owned by OWNER" (dependent on lang files)in item desc
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