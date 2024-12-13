package drzhark.mocreatures.item;

import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class MoCItemHorseSaddle extends MoCItem {

    public MoCItemHorseSaddle(String name)
    {
        super(name);
        maxStackSize = 32;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer entityPlayer, EntityLivingBase entityLiving)
    {
        if (entityLiving instanceof MoCEntityHorse)
        {
            MoCEntityHorse entityhorse = (MoCEntityHorse) entityLiving;
            if (!entityhorse.getIsRideable() && entityhorse.getIsAdult())
            {
                entityhorse.setRideable(true);
                itemStack.stackSize--;
                return true;
            }
        }
        return false;
    }
}