package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenOcean;

public class MoCEntityRay extends MoCEntityTameableAquatic {

    private int poisonCounter;
    private int tailCounter;

    public MoCEntityRay(World world)
    {
        super(world);
        setSize(1.8F, 0.5F);
        setMoCAge(50 + (rand.nextInt(50)));
    }

    protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
    }

    @Override
    public void selectType()
    {
        checkSpawningBiome();

        if (getType() == 0)
        {
            int i = rand.nextInt(100);
            if (i <= 35)
            {
                setType(1);
                setMoCAge(80 + (rand.nextInt(100)));
            }
            else
            {
                setType(1);
                setMoCAge(70);
            }
            getMaxHealth();
        }
        
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(getType() == 2 ? 10.0D : 20.0D);
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("mantray.png");
        case 2:
            return MoCreatures.proxy.getTexture("stingray.png");

        default:
            return MoCreatures.proxy.getTexture("stingray.png");
        }
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.3F;
    }

    public boolean isPoisoning()
    {
        return tailCounter != 0;
    }
    
    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        
        if (riddenByEntity == null && getType() == 1)
        {
            entityPlayer.rotationYaw = rotationYaw;
            entityPlayer.rotationPitch = rotationPitch;
            entityPlayer.posY = posY;
            if (!worldObj.isRemote)
            {
                entityPlayer.mountEntity(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!worldObj.isRemote)
        {
            if (!getIsAdult() && (rand.nextInt(50) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if ((getType() == 1 && getMoCAge() >= 180) || (getType() > 1 && getMoCAge() >= 90))
                {
                    setAdult(true);
                }
            }

            if (!getIsTamed() && getType() > 1 && ++poisonCounter > 250 && (worldObj.difficultySetting.getDifficultyId() > 0) && rand.nextInt(30) == 0)
            {
                if (MoCTools.findClosestPlayerAndPoisonThem(this, true))
                {
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 1), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                    poisonCounter = 0;
                }
            }
        }
        else //client stuff
        {
            if (tailCounter > 0 && ++tailCounter > 50)
            {
                tailCounter = 0;
            }
        }
    }
    
    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
    	if (itemstack != null)
    	{
	    	Item item = itemstack.getItem();
	    	
	    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
	    	
	    	return
	    		(
        			(item == Items.fish && itemstack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
        			|| oreDictionaryNameArray.contains("listAllfishraw")
        		);
    	}
    	return false;
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType == 1) //attacking with tail
        {
            tailCounter = 1;
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            if (getType() == 1 || (worldObj.difficultySetting.getDifficultyId() == 0)) { return true; }
            Entity entityThatAttackedThisCreature = damageSource.getEntity();

            if (entityThatAttackedThisCreature != this)
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        //String biomeName = MoCTools.BiomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);
        BiomeGenBase biome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        if (!(biome instanceof BiomeGenOcean))
        {
            setType(2);
        }
        return true;
    }

    @Override
    public float getAdjustedYOffset()
    {
        if (!isSwimming())
        {
            return 0.09F;
        }
        else if (getType() == 1)
        {
            return 0.15F;
        }

        return 0.25F;
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null);
    }

    @Override
    public int nameYOffset()
    {
        return -25;
    }

    @Override
    public boolean canBeTrappedInNet()
    {
        return true;
    }

    @Override
    public double getMountedYOffset()
    {
        return (double)height * 0.15D * getSizeFactor();
    }

    @Override
    public float getSizeFactor()
    {
        float sizeFactor = (float) getMoCAge() * 0.01F;
        if (sizeFactor > 1.5F) sizeFactor = 1.5F;
        return sizeFactor;
    }
}