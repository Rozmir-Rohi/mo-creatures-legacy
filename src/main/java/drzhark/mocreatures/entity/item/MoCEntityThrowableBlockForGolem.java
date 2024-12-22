package drzhark.mocreatures.entity.item;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.monster.MoCEntityBigGolem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class MoCEntityThrowableBlockForGolem extends Entity {

    
    public int timeOutCounterToTransformToSolidBlock;
    public int acceleration = 100;
    private double oldPosX;
    private double oldPosY;
    private double oldPosZ;
    private int attackDamage = worldObj.difficultySetting.getDifficultyId() == 3 ? 3 : 2;

    public MoCEntityThrowableBlockForGolem(World world)
    {
        super(world);
        preventEntitySpawning = true;
        setSize(1F, 1F);
        yOffset = height / 2.0F;
    }

    public MoCEntityThrowableBlockForGolem(World world, Entity entityThrower, double x, double y, double z)//, int behavior)//, int bMetadata)
    {
        this(world);
        setPosition(x, y, z);
        timeOutCounterToTransformToSolidBlock = 250;
        prevPosX = oldPosX = x;
        prevPosY = oldPosY = y;
        prevPosZ = oldPosZ = z;
        setMasterID(entityThrower.getEntityId());
    }

    public void setMetadata(int i)
    {
        dataWatcher.updateObject(20, Integer.valueOf(i));
    }

    public int getMetadata()
    {
        return dataWatcher.getWatchableObjectInt(20);
    }

    public void setMasterID(int i)
    {
        dataWatcher.updateObject(22, Integer.valueOf(i));
    }

    public int getMasterID()
    {
        return dataWatcher.getWatchableObjectInt(22);
    }

    public void setBehavior(int i)
    {
        dataWatcher.updateObject(21, Integer.valueOf(i));
    }

    public int getBehavior()
    {
        return dataWatcher.getWatchableObjectInt(21);
    }

    public int getType()
    {
        return dataWatcher.getWatchableObjectInt(19);
    }

    public void setType(int i)
    {
        dataWatcher.updateObject(19, Integer.valueOf(i));
    }

    @Override
    protected void entityInit()
    {
        dataWatcher.addObject(19, Integer.valueOf(0)); //blockID
        dataWatcher.addObject(20, Integer.valueOf(0)); //metadata
        dataWatcher.addObject(21, Integer.valueOf(0)); //behaviorType
        dataWatcher.addObject(22, Integer.valueOf(0)); //masterID
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        nbtTagCompound.setInteger("TypeInt", getType());
        nbtTagCompound.setInteger("Metadata", getMetadata());
        nbtTagCompound.setInteger("Behavior", getBehavior());
        nbtTagCompound.setInteger("MasterID", getMasterID());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        setType(nbtTagCompound.getInteger("TypeInt"));
        setMetadata(nbtTagCompound.getInteger("Metadata"));
        setBehavior(nbtTagCompound.getInteger("Behavior"));
        setMasterID(nbtTagCompound.getInteger("MasterID"));
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onEntityUpdate()
    {
        Entity master = getMaster();
        if (MoCreatures.isServer())
        {
        	if (getBehavior() != 2 && onGround) {transformToSolidBlock();} //turn to solid if not moving towards it's master and if on ground
        	
        	if (timeOutCounterToTransformToSolidBlock-- <= 0) {transformToSolidBlock();}
        }

        //held ThrowableBlocks don't need to adjust its position
        if (getBehavior() == 1)
        {
            return;
        }

        //throwable block damage code (for all throwable block behaviors)
        if (!onGround) //onground!
        {
            List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));

            int iterationLength = entitiesNearbyList.size();
            
            if (iterationLength > 0)
            {
	            for (int index = 0; index < iterationLength; index++)
	            {
	                Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	                
	                if (master != null && entityNearby.getEntityId() == master.getEntityId())
	                {
	                    continue;
	                }
	                if (entityNearby instanceof MoCEntityBigGolem)
	                {
	                    continue;
	                }
	                if (entityNearby != null && !(entityNearby instanceof EntityLivingBase))
	                {
	                    continue;
	                }
	
	                if (master != null)
	                {
	                    entityNearby.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) master), attackDamage);
	                }
	                else
	                {
	                    entityNearby.attackEntityFrom(DamageSource.generic, 4);
	                }
	            }
            }
        }

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (getBehavior() == 2)
        {
            if (master == null) { return; }

            //moves towards the master entity the bigger the number, the slower
            --acceleration;
            if (acceleration < 10)
            {
                acceleration = 10;
            }

            float xDistanceToMaster = (float) posX - (float) master.posX;
            float zDistanceToMaster = (float) posZ - (float) master.posZ;
            float xzDistanceToMaster = xDistanceToMaster * xDistanceToMaster + zDistanceToMaster * zDistanceToMaster;

            if (xzDistanceToMaster < 1.0F && master instanceof MoCEntityBigGolem)
            {
                ((MoCEntityBigGolem) master).receiveBlock(getType(), getMetadata());
                setDead();
            }

            double summonedSpeed = acceleration;//20D;
            motionX = ((master.posX - posX) / summonedSpeed);
            motionY = ((master.posY - posY) / 20D + 0.15D);
            motionZ = ((master.posZ - posZ) / summonedSpeed);
            if (MoCreatures.isServer())
            {
                moveEntity(motionX, motionY, motionZ);
            }
            return;
        }

        if (getBehavior() == 4)// imploding / exploding throwable block
        {
            if (master == null)
            {
                if (MoCreatures.isServer())
                {
                    setBehavior(5);
                }
                return;
            }

            //moves towards the master entity the bigger the number, the slower
            acceleration = 10;

            float xDistanceToMaster = (float) posX - (float) master.posX;
            float zDistanceToMaster = (float) posZ - (float) master.posZ;
            float xzDistanceToMaster = xDistanceToMaster * xDistanceToMaster + zDistanceToMaster * zDistanceToMaster;

            double summonedSpeed = acceleration;//20D;
            motionX = ((master.posX - posX) / summonedSpeed);
            motionY = ((master.posY - posY) / 20D + 0.15D);
            motionZ = ((master.posZ - posZ) / summonedSpeed);

            if (xzDistanceToMaster < 2.5F && master instanceof MoCEntityBigGolem)
            {
                motionX = 0D;
                motionY = 0D;
                motionZ = 0D;
            }

            if (MoCreatures.isServer())
            {
                moveEntity(motionX, motionY, motionZ);
            }

            return;
        }

        if (getBehavior() == 5)// exploding throwable block
        {
            acceleration = 5;
            double summonedSpeed = acceleration;//20D;
            motionX = ((oldPosX - posX) / summonedSpeed);
            motionY = ((oldPosY - posY) / 20D + 0.15D);
            motionZ = ((oldPosZ - posZ) / summonedSpeed);
            if (MoCreatures.isServer())
            {
                moveEntity(motionX, motionY, motionZ);
            }
            setBehavior(0);
            return;
        }

        motionY -= 0.04D;
        if (MoCreatures.isServer())
        {
            moveEntity(motionX, motionY, motionZ);
        }
        motionX *= 0.98D;
        motionY *= 0.98D;
        motionZ *= 0.98D;

        if (onGround)
        {
            motionX *= 0.699D;
            motionZ *= 0.699D;
            motionY *= -0.5D;
        }

    }

    private void transformToSolidBlock()
    {
        if ((MoCTools.mobGriefing(worldObj)) && (MoCreatures.proxy.golemDestroyBlocks)) // don't drop throwable blocks if mobgriefing is set to false, prevents duping
        {
            if (!(
            		getType() == 8 //flowing water
            		|| getType() == 9 //water
            		|| getType() == 10 //flowing lava
            		|| getType() == 11 //lava
            		||Block.getBlockById(getType()) instanceof IFluidBlock //do not try to transform into solid block if the block is a liquid
            	))
            {
            	worldObj.setBlock(MathHelper.floor_double(posX),MathHelper.floor_double(posY),MathHelper.floor_double(posZ), Block.getBlockById(getType()));
            }
        }
        setDead();
    }

    public Block getMyBlock()
    {
        if (getType() != 0)
        {
            return Block.getBlockById(getType());
        }
        return Blocks.stone;
    }

    private Entity getMaster()
    {
        List<Entity> entityList = worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == getMasterID()) { return entity; }
        }

        return null;
    }
}