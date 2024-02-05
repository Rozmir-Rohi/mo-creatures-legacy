package drzhark.mocreatures.item;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.animal.MoCEntityBunny;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.animal.MoCEntityTurtle;
import drzhark.mocreatures.entity.aquatic.MoCEntityDolphin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCItemCreaturePedia extends MoCItem {

    public MoCItemCreaturePedia(String name)
    {
        super(name);
        maxStackSize = 1;
    }

    /**
     * Called when a player right clicks a entity with a item.
     */
    public void itemInteractionForEntity2(ItemStack itemstack, EntityLiving entityLiving)
    {
        if (entityLiving.worldObj.isRemote) { return; }

        if (entityLiving instanceof MoCEntityHorse)
        {
            MoCreatures.showCreaturePedia("/mocreatures/pedia/horse.png");
            return;
        }

        if (entityLiving instanceof MoCEntityTurtle)
        {
            MoCreatures.showCreaturePedia("/mocreatures/pedia/turtle.png");
            return;
        }

        if (entityLiving instanceof MoCEntityBunny)
        {
            MoCreatures.showCreaturePedia("/mocreatures/pedia/bunny.png");
            return;
        }

        if (entityLiving instanceof MoCEntityDolphin)
        {
            MoCreatures.showCreaturePedia("/mocreatures/pedia/dolphin.png");
            return;
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer entityPlayer)
    {
        if (!worldObj.isRemote)
        {
            double dist = 5D;
            double newPosX = entityPlayer.posX - (dist * Math.cos((MoCTools.realAngle(entityPlayer.rotationYaw - 90F)) / 57.29578F));
            double newPosZ = entityPlayer.posZ - (dist * Math.sin((MoCTools.realAngle(entityPlayer.rotationYaw - 90F)) / 57.29578F));
            double newPosY = entityPlayer.posY - 1D;

            double d1 = -1D;
            EntityLivingBase entityLiving = null;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(entityPlayer, entityPlayer.boundingBox.expand(dist, dist, dist));
            for (int i = 0; i < list.size(); i++)
            {
                Entity entity1 = (Entity) list.get(i);
                if (entity1 == null || !(entity1 instanceof EntityLivingBase))
                {
                    continue;
                }

                if (!(entityPlayer.canEntityBeSeen(entity1)))
                {
                    continue;
                }

                double d2 = entity1.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
                if (((dist < 0.0D) || (d2 < (dist * dist))) && ((d1 == -1D) || (d2 < d1)) && ((EntityLivingBase) entity1).canEntityBeSeen(entityPlayer))
                {
                    d1 = d2;
                    entityLiving = (EntityLivingBase) entity1;
                }
            }

            if (entityLiving == null) { return itemstack; }

            if (entityLiving instanceof MoCEntityHorse)
            {
                MoCreatures.showCreaturePedia(entityPlayer, "/mocreatures/pedia/horse.png");
                return itemstack;
            }

            if (entityLiving instanceof MoCEntityTurtle)
            {
                MoCreatures.showCreaturePedia(entityPlayer, "/mocreatures/pedia/turtle.png");
                return itemstack;
            }

            if (entityLiving instanceof MoCEntityBunny)
            {
                MoCreatures.showCreaturePedia(entityPlayer, "/mocreatures/pedia/bunny.png");
                return itemstack;
            }

            //TODO 4FIX             
            /*if (entityLiving instanceof MoCEntityDolphin)
            {
                //System.out.println("showing dolphin");
                MoCreatures.showCreaturePedia(entityPlayer, "/mocreatures/pedia/dolphin.png");
                return itemstack;
            }*/
        }

        return itemstack;
    }

    private int ageCounter;
}