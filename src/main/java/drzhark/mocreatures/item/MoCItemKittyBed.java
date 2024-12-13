package drzhark.mocreatures.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MoCItemKittyBed extends MoCItem {

	int bedType;
	
	private IIcon[] icons;
	
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
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (MoCreatures.isServer())
        {
            itemStack.stackSize--;
            MoCEntityKittyBed entitykittybed = new MoCEntityKittyBed(world, itemStack.getItemDamage());
            entitykittybed.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
            world.spawnEntityInWorld(entitykittybed);
            entitykittybed.motionY += world.rand.nextFloat() * 0.05F;
            entitykittybed.motionX += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
            entitykittybed.motionZ += (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F;
        }
        return itemStack;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return (new StringBuilder()).append(super.getUnlocalizedName()).append(".").append(itemStack.getItemDamage()).toString();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[16];
        
        for (int index = 0; index < 16; index++)
        {
            String kittyBedWoolColour = ItemDye.field_150921_b[index];
               
            icons[index] = iconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":")+"_"+kittyBedWoolColour);
        }
        
        
    }

    /**
     * Gets an icon index based on an item's damage value
     */
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int itemDamage)
    {
        return icons[itemDamage];
    }
}