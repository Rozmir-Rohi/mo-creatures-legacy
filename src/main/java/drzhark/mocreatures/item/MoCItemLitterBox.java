package drzhark.mocreatures.item;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCItemLitterBox extends MoCItem {

    public MoCItemLitterBox(String name)
    {
        super(name);
        maxStackSize = 16;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {

        if (MoCreatures.isServer())
        {

            itemStack.stackSize--;
            MoCEntityLitterBox entitylitterbox = new MoCEntityLitterBox(world);
            entitylitterbox.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
            entityPlayer.worldObj.spawnEntityInWorld(entitylitterbox);
            entitylitterbox.motionY += world.rand.nextFloat() * 0.05F;
            entitylitterbox.motionX += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
            entitylitterbox.motionZ += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
        }
        return itemStack;
    }
}