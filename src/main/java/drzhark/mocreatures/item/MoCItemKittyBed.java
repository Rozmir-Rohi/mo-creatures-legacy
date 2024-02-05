package drzhark.mocreatures.item;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCItemKittyBed extends MoCItem {

	int bedType;
	
    public MoCItemKittyBed(String name)
    {
        super(name);
        maxStackSize = 8;
        setHasSubtypes(true);
    }
    
    public MoCItemKittyBed(String name, int j)
    {
        this(name);
        bedType = j;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityPlayer)
    {
        if (MoCreatures.isServer())
        {
            itemstack.stackSize--;
            MoCEntityKittyBed entitykittybed = new MoCEntityKittyBed(world, itemstack.getItemDamage());
            entitykittybed.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
            world.spawnEntityInWorld(entitykittybed);
            entitykittybed.motionY += world.rand.nextFloat() * 0.05F;
            entitykittybed.motionX += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
            entitykittybed.motionZ += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
        }
        return itemstack;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
        return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemstack.getItemDamage()).toString();
    }
}