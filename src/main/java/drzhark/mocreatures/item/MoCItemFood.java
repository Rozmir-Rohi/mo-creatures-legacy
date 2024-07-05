package drzhark.mocreatures.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemFood;

public class MoCItemFood extends ItemFood
{

    public MoCItemFood(String name, int j)
    {
        super(j, 0.6F, false);
        setCreativeTab(MoCreatures.MOC_CREATIVE_TAB);
        setUnlocalizedName(name);
        GameRegistry.registerItem(this, name);
        maxStackSize = 32;
    }

    public MoCItemFood(String name, int j, float f, boolean flag)
    {
        super(j, f, flag);
        setCreativeTab(MoCreatures.MOC_CREATIVE_TAB);
        setUnlocalizedName(name);
        GameRegistry.registerItem(this, name);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister)
    {
        itemIcon = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":"));
    }
}