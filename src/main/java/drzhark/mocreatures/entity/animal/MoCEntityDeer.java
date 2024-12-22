package drzhark.mocreatures.entity.animal;

import cpw.mods.fml.common.registry.GameRegistry;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityDeer extends MoCEntityTameableAnimal {

    
    private boolean isRunning = false;

    public MoCEntityDeer(World world)
    {
        super(world);
        setMoCAge(75);
        setSize(0.9F, 1.3F);
        //health = 10;
        setAdult(true);
        setTamed(false);
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            int i = rand.nextInt(100);
            if (i <= 20)
            {
                setType(1);
            }
            else if (i <= 70)
            {
                setType(2);
            }
            else
            {
                setType(3);
            }
        }
    }

    @Override
    public ResourceLocation getTexture()
    {

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("deer.png");
        case 2:
            return MoCreatures.proxy.getTexture("deerf.png");
        case 3:
            setAdult(false);
            return MoCreatures.proxy.getTexture("deerb.png");

        default:
            return MoCreatures.proxy.getTexture("deer.png");
        }
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    public boolean entitiesThatAreScary(Entity entity)
    {
        return !(entity instanceof MoCEntityDeer) && (
        		entity instanceof EntityPlayer
        		|| entity instanceof MoCEntityBear
        		|| entity instanceof MoCEntityBigCat
        		|| entity instanceof MoCEntityCrocodile
        		|| entity instanceof MoCEntityKomodoDragon
        		|| entity instanceof MoCEntityWyvern);
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:deerdying";
    }
    
    @Override
    protected void dropFewItems(boolean hasEntityBeenHitByPlayer, int levelOfLootingEnchantmentUsedToKillThisEntity)
    {   
        int randomAmount = rand.nextInt(3);

        dropItem(MoCreatures.hide, randomAmount);
        
        if (!MoCreatures.isGregTech6Loaded)
        {
        	if (MoCreatures.isPalmsHarvestLoaded)
        	{
        		if (isBurning())
        		{
        			dropItem(GameRegistry.findItem("harvestcraft", "venisoncookedItem"), randomAmount);
        		}
        		else 
        		{
        			dropItem(GameRegistry.findItem("harvestcraft", "venisonrawItem"), randomAmount);
        		}
        	}
        	
        	else if (MoCreatures.isTwilightForestLoaded)
        	{
        		if (isBurning())
        		{
        			dropItem(GameRegistry.findItem("TwilightForest", "item.venisonCooked"), randomAmount);
        		}
        		else 
        		{
        			dropItem(GameRegistry.findItem("TwilightForest", "item.venisonRaw"), randomAmount);
        		}
        	}
        }
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:deerhurt";
    }

    @Override
    protected String getLivingSound()
    {
        if (!getIsAdult())
        {
            return "mocreatures:deerbgrunt";
        }
        else
        {
            return "mocreatures:deerfgrunt";
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!worldObj.isRemote)
        {
            if ((getType() == 3) && !getIsAdult() && (rand.nextInt(250) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 130)
                {
                    setAdult(true);
                    int i = rand.nextInt(1);
                    setType(i);// = i;
                }
            }
            if (rand.nextInt(5) == 0)
            {
                EntityLivingBase scaryEntityNearby = MoCTools.getScaryEntity(this, 10D);
                
                if (scaryEntityNearby instanceof EntityPlayer)
                {
                	EntityPlayer entityPlayer = (EntityPlayer) scaryEntityNearby;
                	
                	if (entityPlayer.capabilities.isCreativeMode
                			|| entityPlayer.isPotionActive(14)) //invisibility potion
                	{
                		scaryEntityNearby = null; // ignore player
                	}
                }
                
                if (scaryEntityNearby != null)
                {
                    isRunning = true;

                    
                    fleeingTick = 200; //run away
                    //MoCTools.runLikeHell(this, scaryEntityNearby);

                }
                else if ((scaryEntityNearby == null) && (isRunning || fleeingTick > 0))
                {
                    isRunning = false;
                    fleeingTick = 0;
                }
            }
            
            if ((isRunning) && !isInWater()) // increase running speed
            {
            	if (motionX != 0) {motionX = getMoveSpeed() * 0.08 * Math.signum(motionX);}
            	
            	if (motionZ != 0) {motionZ = getMoveSpeed() * 0.08 * Math.signum(motionZ);}
            	
            	
            	if (onGround && (rand.nextInt(30) == 0) &&
            			((motionX > 0.1D) || (motionZ > 0.1D) || (motionX < -0.1D) || (motionZ < -0.1D)))
            	{
            		motionY = 0.5D; //hop in between
            	}
            }
        }
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
        	isRunning = true;
        	fleeingTick = 200;
        }
        return true;
    }

    @Override
    public float getMoveSpeed()
    {
        float speed = 1.0F;
        
        if (getType() == 1)
        {
            speed = 1.7F;
        }
        else if (getType() == 2)
        {
            speed = 1.9F;
        }
        else
        {
            speed = 1.3F;
        }
        if (isRunning)
        {
            speed *= 2.0F;
        }
        return speed;
    }
}