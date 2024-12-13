package drzhark.mocreatures.item;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCItemEgg extends MoCItem {

    int eggType;

    public MoCItemEgg(String name)
    {
        super(name);
        maxStackSize = 16;
        setHasSubtypes(true);
    }

    public MoCItemEgg(String name, int j)
    {
        this(name);
        eggType = j;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        itemStack.stackSize--;
        if (MoCreatures.isServer())
        {
            int i = itemStack.getItemDamage();
            if (i == 30)
            {
                i = 31; //for ostrich eggs. placed eggs become stolen eggs.
            }
            MoCEntityEgg entityegg = new MoCEntityEgg(world, i);
            entityegg.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
            entityPlayer.worldObj.spawnEntityInWorld(entityegg);
            entityegg.motionY += world.rand.nextFloat() * 0.05F;
            entityegg.motionX += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
            entityegg.motionZ += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
        }
        return itemStack;
    }

   @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemStack.getItemDamage()).toString();
    }
}