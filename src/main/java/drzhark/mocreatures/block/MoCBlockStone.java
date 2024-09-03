package drzhark.mocreatures.block;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class MoCBlockStone extends MoCBlock
{
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
    public MoCBlockStone(String name)
    {
        super(name, Material.rock);
        setTickRandomly(true);
    }

    @Override
    public int damageDropped(int i)
    {
        return i;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        icons = new IIcon[MoCreatures.multiBlockNames.size()];

        for (int x = 0; x < MoCreatures.multiBlockNames.size(); x++)
        {
            icons[x] = par1IconRegister.registerIcon("mocreatures:" + "rock_" + MoCreatures.multiBlockNames.get(x));
        }
    }

    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if (par2 < 0 || par2 >= MoCreatures.multiBlockNames.size()) par2 = 0;
        return icons[par2];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) 
    {
        for (int index = 0; index < MoCreatures.multiBlockNames.size(); index++) 
        {
        	ItemStack itemstack = new ItemStack(item, 1, index);
            subItems.add(itemstack);
            
            OreDictionary.registerOre("stone", itemstack);
        }
    }
}