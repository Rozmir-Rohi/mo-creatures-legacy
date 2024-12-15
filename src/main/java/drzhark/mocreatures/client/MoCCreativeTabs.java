package drzhark.mocreatures.client;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MoCCreativeTabs extends CreativeTabs {

    public MoCCreativeTabs(int par1, String par2Str)
    {
        super(par1, par2Str);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem()
    {
        return MoCreatures.amuletFairyFull;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllReleventItems(List createTabList)
    {
        Iterator iterator = Item.itemRegistry.iterator();

        while (iterator.hasNext())
        {
            Item item = (Item)iterator.next();

            if (item == null)
            {
                continue;
            }
            
            else if (item == MoCreatures.kittybed)
            {
            	addKittyBedsToList(createTabList);
            }
            
            else
            {
	            for (CreativeTabs tab : item.getCreativeTabs())
	            {
	                if (tab == this)
	                {
	                    item.getSubItems(item, this, createTabList);
	                }
	            }
            }
        }

        if (func_111225_m() != null)
        {
            addEnchantmentBooksToList(createTabList, func_111225_m());
        }
    }
    @SideOnly(Side.CLIENT)
    public void addKittyBedsToList(List list)
    {
    	for (int index = 0; index < 16; index++)
        {
            ItemStack kittyBedItemStack = new ItemStack(MoCreatures.kittybed, 1, index);
            list.add(kittyBedItemStack);
        }
    }
}
