package drzhark.mocreatures.entity.animal;

import cpw.mods.fml.common.registry.GameRegistry;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class MoCEntityDuck extends MoCEntityAnimal//EntityChicken
{
	
    public MoCEntityDuck(World world)
    {
        super(world);
        texture = "duck.png";
        setSize(0.3F, 0.4F);
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    }
    
    @Override
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
    protected void dropFewItems(boolean hasEntityBeenHitByPlayer, int levelOfLootingEnchantmentUsedToKillThisEntity)
    {   
        int randomAmount = rand.nextInt(3);

        dropItem(Items.feather, randomAmount);
        
    	if (MoCreatures.isExoticBirdsLoaded)
    	{
    		if (isBurning())
    		{
    			dropItem(GameRegistry.findItem("exoticbirds", "cooked_birdmeat_small"), 1);
    		}
    		else 
    		{
    			dropItem(GameRegistry.findItem("exoticbirds", "birdmeat_small"), 1);
    		}
    	}
    }
}