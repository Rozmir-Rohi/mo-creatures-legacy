package drzhark.mocreatures.block;

import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class MultiItemBlock extends ItemBlock {

    public MultiItemBlock(Block block) 
    {
        super(block);
        setHasSubtypes(true);
        //setItemName("multiBlock"); //TODO
        setUnlocalizedName("multiBlock");
    }

    @Override
    public int getMetadata (int damageValue) {
        return damageValue;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return getUnlocalizedName() + "." + MoCreatures.multiBlockNames.get(itemStack.getItemDamage());
    }
}