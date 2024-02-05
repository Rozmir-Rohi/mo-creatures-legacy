package drzhark.mocreatures.entity.monster;

import drzhark.mocreatures.MoCreatures;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityHellRat extends MoCEntityRat {

    private int textureCounter;

    public MoCEntityHellRat(World world)
    {
        super(world);
        setSize(0.7F, 0.7F);
        isImmuneToFire = true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }

    @Override
    public void selectType()
    {
        setType(4);
    }

    @Override
    public ResourceLocation getTexture()
    {
        if (rand.nextInt(2) == 0)
        {
            textureCounter++;
        }
        if (textureCounter < 10)
        {
            textureCounter = 10;
        }
        if (textureCounter > 29)
        {
            textureCounter = 10;
        }
        String textureNumber = "" + textureCounter;
        textureNumber = textureNumber.substring(0, 1);
        return MoCreatures.proxy.getTexture("hellrat" + textureNumber + ".png");
    }

    @Override
    protected Item getDropItem()
    {
        boolean flag = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);
        if (flag) { return Item.getItemFromBlock(Blocks.fire); }
        return Items.redstone;
    }
}