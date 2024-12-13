package drzhark.mocreatures.item;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemStaffTeleport extends MoCItem
{
    public ItemStaffTeleport(String name)
    {
        super(name);
        maxStackSize = 1;
        setMaxDamage(128);
    }

    private int portalPosX;
    private int portalPosY;
    private int portalPosZ;
    private int portalDimension;

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
    public EnumAction getItemUseAction(ItemStack itemstack)
    {
        return EnumAction.block;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World par2World, EntityPlayer entityPlayer)
    {
        if (entityPlayer.ridingEntity != null || entityPlayer.riddenByEntity != null)
        {
            return itemstack;
        }

        double coordY = entityPlayer.posY + entityPlayer.getEyeHeight();
        double coordZ = entityPlayer.posZ;
        double coordX = entityPlayer.posX;
        for (int x = 4; x < 128; x++)
        {
            double newPosY = coordY - Math.cos( (entityPlayer.rotationPitch- 90F) / 57.29578F) * x;
            double newPosX = coordX + Math.cos((MoCTools.realAngle(entityPlayer.rotationYaw- 90F) / 57.29578F)) * (Math.sin( (entityPlayer.rotationPitch- 90F) / 57.29578F) * x );
            double newPosZ = coordZ + Math.sin((MoCTools.realAngle(entityPlayer.rotationYaw- 90F) / 57.29578F)) * (Math.sin( (entityPlayer.rotationPitch- 90F) / 57.29578F) * x );
            Block block = entityPlayer.worldObj.getBlock( MathHelper.floor_double(newPosX),  MathHelper.floor_double(newPosY),  MathHelper.floor_double(newPosZ)); 
            if (block != Blocks.air)
            {
                newPosY = coordY - Math.cos( (entityPlayer.rotationPitch- 90F) / 57.29578F) * (x-1);
                newPosX = coordX + Math.cos((MoCTools.realAngle(entityPlayer.rotationYaw- 90F) / 57.29578F)) * (Math.sin( (entityPlayer.rotationPitch- 90F) / 57.29578F) * (x-1) );
                newPosZ = coordZ + Math.sin((MoCTools.realAngle(entityPlayer.rotationYaw- 90F) / 57.29578F)) * (Math.sin( (entityPlayer.rotationPitch- 90F) / 57.29578F) * (x-1) );

                if (MoCreatures.isServer())
                {
                    EntityPlayerMP thePlayer = (EntityPlayerMP) entityPlayer;
                    thePlayer.playerNetServerHandler.setPlayerLocation(newPosX, newPosY, newPosZ, entityPlayer.rotationYaw, entityPlayer.rotationPitch);
                    MoCTools.playCustomSound(entityPlayer, "appearmagic", entityPlayer.worldObj);
                }
                MoCreatures.proxy.teleportFX(entityPlayer);
                entityPlayer.setItemInUse(itemstack, 200);
                itemstack.damageItem(1, entityPlayer);

                return itemstack;
            }
        }

        entityPlayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
        return itemstack;
    }
}