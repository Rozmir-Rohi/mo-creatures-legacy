package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.entity.MoCEntityAnimal;
import drzhark.mocreatures.entity.MoCEntityInsect;
import drzhark.mocreatures.entity.ambient.MoCEntityMaggot;
import drzhark.mocreatures.entity.ambient.MoCEntitySnail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class MoCEntityDuck extends MoCEntityAnimal//EntityChicken
{
	
    public MoCEntityDuck(World world)
    {
        super(world);
        texture = "duck.png";
        setSize(0.3F, 0.4F);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    }
    
    public boolean isSwimmerEntity()
    {
        return true;
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:duckhurt";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:duckhurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:duck";
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (isOnAir() && motionY < 0.0D) //slows falling 
        {
            motionY *= 0.6D;
        }
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    protected Item getDropItem()
    {
        return Items.feather;
    }
}