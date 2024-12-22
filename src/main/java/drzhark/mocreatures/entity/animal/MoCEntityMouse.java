package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityMouse extends MoCEntityAnimal
{

    public MoCEntityMouse(World world)
    {
        super(world);
        setSize(0.3F, 0.3F);
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    }

    @Override
	public void selectType()
    {
        checkSpawningBiome();
        
        if (getType() == 0)
        {
            setType(rand.nextInt(3)+1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
            case 1:
                return MoCreatures.proxy.getTexture("miceg.png");
            case 2:
                return MoCreatures.proxy.getTexture("miceb.png");
            case 3:
                return MoCreatures.proxy.getTexture("micew.png");
            
            default:
                return MoCreatures.proxy.getTexture("miceg.png");
        }
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);

        MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SNOWY))
        {
            setType(3); //white mice!
        }
        return true;
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.8F;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // byte IsPicked, 0 = false 1 = true
    }

    public boolean getIsPicked()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setPicked(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public boolean climbing()
    {
        return !onGround && isOnLadder();
    }

    @Override
	public boolean entitiesThatAreScary(Entity entity)
    {
        return !(entity instanceof MoCEntityMouse)
        && super.entitiesThatAreScary(entity);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        return ( 
	                (MoCreatures.entityMap.get(getClass()).getFrequency() > 0) &&
	                worldObj.checkNoEntityCollision(boundingBox) 
	                && (worldObj.getCollidingBoundingBoxes(this, boundingBox).size() == 0) 
	                && !worldObj.isAnyLiquid(boundingBox) 
	                && ((worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate) == Blocks.cobblestone) 
	                || (worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate) == Blocks.planks) 
	                || (worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate) == Blocks.dirt) 
	                || (worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate) == Blocks.stone) 
	                || (worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate) == Blocks.grass))
                );
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:micedying";
    }

    @Override
    protected Item getDropItem()
    {
        return Items.wheat_seeds;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:micehurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:micegrunt";
    }

    @Override
    public double getYOffset()
    {
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer())
        {
            return (yOffset - 1.7F);
        }
            
        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            return (yOffset - 0.1F);
        }
        else 
            return yOffset;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {   
        ItemStack itemStack = entityPlayer.getHeldItem();
        
        if (	
        		(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemStack == null)
	    		|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
	    	)
        {
        	rotationYaw = entityPlayer.rotationYaw;
        
	        if (ridingEntity == null)
	        {
	            if (
	            		MoCreatures.isServer()
	            		&& !(entityPlayer.isSneaking()) && (entityPlayer.ridingEntity == null)
	                )
	            {
	            	mountEntity(entityPlayer);
	            	setPicked(true);
	            }
	        }
	        else
	        {
	            setPicked(false);
	            if (MoCreatures.isServer())
	            {
	            	mountEntity(null);
	            }
	            fallDistance = -3; //prevents fall damage when dropped
	            return false;
	        }
	        motionX = entityPlayer.motionX * 5D;
	        motionY = (entityPlayer.motionY / 2D) + 0.5D;
	        motionZ = entityPlayer.motionZ * 5D;
	
	        return true;
        }
        return false;
    }

    @Override
    public boolean isOnLadder()
    {
        return isCollidedHorizontally;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(!worldObj.isRemote)
        {
            if(rand.nextInt(15) == 0)
            {
                EntityLivingBase entityLiving = MoCTools.getScaryEntity(this, 6D);
                if(entityLiving != null)
                {
                    MoCTools.runAway(this, entityLiving);

                }
            }
            if(!onGround && (ridingEntity != null))
            {
                rotationYaw = ridingEntity.rotationYaw;
            }
            if (ridingEntity instanceof EntityPlayer)
            {
            	if (MoCreatures.proxy.emptyHandMountAndPickUpOnly && ((EntityPlayer) ridingEntity).getHeldItem() != null)
            	{
            		mountEntity(null);
            		fallDistance = -3; //prevents fall damage when dropped
            		setPicked(false);
            	}
            }
        }
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (MoCreatures.isServer())
        {
        	if (ridingEntity != null && 
        			(damageSource.getEntity() == ridingEntity || DamageSource.inWall.equals(damageSource)))
            {
         	   return false;
            }
        }
        
        return super.attackEntityFrom(damageSource, damageTaken);
    }

    public boolean upsideDown()
    {
        return getIsPicked();
    }

    @Override
    public boolean updateMount() 
    {
        return true;
    }

    @Override
    public boolean forceUpdates() 
    {
        return true;
    }

    
    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }
}