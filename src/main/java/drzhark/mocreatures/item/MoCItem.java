package drzhark.mocreatures.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

public class MoCItem extends Item
{
    public MoCItem(String name)
    {	
    	GameRegistry.registerItem(this, name);
    	this.setUnlocalizedName(name);
    	
    	if (!(name.contains("achievement_icon_"))) //do not add the achievement icons as items in the creative tab
    	{
        	this.setCreativeTab(MoCreatures.tabMoC);	
    	}
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("mocreatures"+ this.getUnlocalizedName().replaceFirst("item.", ":"));
    }
}