package drzhark.mocreatures.item;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.dimension.MoCDirectTeleporter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class ItemStaffPortal extends MoCItem
{
    public ItemStaffPortal(String name)
    {
        super(name);
        maxStackSize = 1;
        setMaxDamage(3);
    }

    private int portalPosX;
    private int portalPosY;
    private int portalPosZ;
    private int portalDimension;

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int i, int j, int k, int l, float f1, float f2, float f3)
    {
        if(!MoCreatures.isServer())
        {
            return false;
        }
        if( itemStack.stackTagCompound == null )
        {
             itemStack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbtcompound = itemStack.stackTagCompound;

        EntityPlayerMP thePlayer = (EntityPlayerMP) entityPlayer;
        if (thePlayer.ridingEntity != null || thePlayer.riddenByEntity != null)
        {
            return false;
        }
        else
        {
            if (thePlayer.dimension != MoCreatures.wyvernLairDimensionID)
            {
                portalDimension = thePlayer.dimension;
                portalPosX = (int) thePlayer.posX;
                portalPosY = (int) thePlayer.posY;
                portalPosZ = (int) thePlayer.posZ;
                writeToNBT(nbtcompound);

                ChunkCoordinates var2 = thePlayer.mcServer.worldServerForDimension(MoCreatures.wyvernLairDimensionID).getEntrancePortalLocation();

                if (var2 != null)
                {
                    thePlayer.playerNetServerHandler.setPlayerLocation(var2.posX, var2.posY, var2.posZ, 0.0F, 0.0F);
                }
                thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, MoCreatures.wyvernLairDimensionID, new MoCDirectTeleporter(thePlayer.mcServer.worldServerForDimension(MoCreatures.wyvernLairDimensionID)));
                itemStack.damageItem(1, entityPlayer);
                entityPlayer.addStat(MoCAchievements.wyvern_portal_staff, 1);
                return true;
            }
            else
            {
                //on the WyvernLair!
                if ( (thePlayer.posX > 1.5D || thePlayer.posX < -1.5D) || (thePlayer.posZ > 2.5D || thePlayer.posZ < -2.5D))
                {
                    return false;
                }
                readFromNBT(nbtcompound);

                boolean foundSpawn = false;
                if (portalPosX == 0 && portalPosY == 0 && portalPosZ == 0) //dummy staff
                {
                    ChunkCoordinates var2 = thePlayer.mcServer.worldServerForDimension(0).getSpawnPoint();

                    if (var2 != null)
                    {
                        for (int i1 = 0; i1 < 60; i1++)
                        {
                            Block block = thePlayer.mcServer.worldServerForDimension(0).getBlock(var2.posX, var2.posY + i1, var2.posZ);
                            Block block1 = thePlayer.mcServer.worldServerForDimension(0).getBlock(var2.posX, var2.posY + i1 + 1, var2.posZ);
                            if (block == Blocks.air && block1 == Blocks.air)
                            {
                                thePlayer.playerNetServerHandler.setPlayerLocation(var2.posX, (double)var2.posY+i1+1, var2.posZ, 0.0F, 0.0F);
                                if (MoCreatures.proxy.debug) {System.out.println("MoC Staff teleporter found location at spawn");}
                                foundSpawn = true;
                                break;
                            }
                        }

                        if (!foundSpawn)
                        {
                            if (MoCreatures.proxy.debug) {System.out.println("MoC Staff teleporter couldn't find an adequate teleport location at spawn");}
                            return false;
                        }
                    }
                    else
                    {
                        if (MoCreatures.proxy.debug) System.out.println("MoC Staff teleporter couldn't find spawn point");
                        return false;
                    }
                }
                else
                {
                    thePlayer.playerNetServerHandler.setPlayerLocation(portalPosX, (portalPosY) + 1D, portalPosZ, 0.0F, 0.0F);
                }

                itemStack.damageItem(1, entityPlayer);
                thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, portalDimension, new MoCDirectTeleporter(thePlayer.mcServer.worldServerForDimension(0)));
                return true;
            }
        }
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @Override
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack itemStack)
    {
        return EnumAction.block;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        par3EntityPlayer.setItemInUse(itemStack, getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        portalPosX = nbt.getInteger("portalPosX");
        portalPosY = nbt.getInteger("portalPosY");
        portalPosZ = nbt.getInteger("portalPosZ");
        portalDimension = nbt.getInteger("portalDimension");
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("portalPosX", portalPosX);
        nbt.setInteger("portalPosY", portalPosY);
        nbt.setInteger("portalPosZ", portalPosZ);
        nbt.setInteger("portalDimension", portalDimension);
    }
}