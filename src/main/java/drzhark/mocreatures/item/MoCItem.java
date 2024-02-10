package drzhark.mocreatures.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public class MoCItem extends Item
{
    public MoCItem(String name)
    {	
    	GameRegistry.registerItem(this, name);
    	setUnlocalizedName(name);
    	
    	if (!(name.contains("achievement_icon_"))) //do not add the achievement icons as items in the creative tab
    	{
        	setCreativeTab(MoCreatures.tabMoC);	
    	}
    }
    
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) //only the unbreaking enchantment can be applied to whips
    {	
    	return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityPlayer, List loreList, boolean flag)
    {
        super.addInformation(itemstack, entityPlayer, loreList, flag);
        
        if (itemstack.getItem() == MoCreatures.scrollFreedom)
        {
        	loreList.add("Unames and untames the pet.");
        }
        
        if (itemstack.getItem() == MoCreatures.scrollOfSale)
        {
        	loreList.add("Removes ownership from the pet.");
        	loreList.add("Allows reclaiming by medallions.");
        }
        
        if (itemstack.getItem() == MoCreatures.scrollOfOwner)
        {
        	loreList.add("Removes ownership from the pet.");
        	loreList.add("Usable by Opped players only.");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        itemIcon = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":"));
    }
}