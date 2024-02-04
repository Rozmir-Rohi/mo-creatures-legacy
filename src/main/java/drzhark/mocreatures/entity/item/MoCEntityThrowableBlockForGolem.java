package drzhark.mocreatures.entity.item;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.monster.MoCEntityGolem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

public class MoCEntityThrowableBlockForGolem extends Entity {

    /** How long the fuse is */
    public int fuse;
    private int masterID;
    public int acceleration = 100;
    private int blockMetadata;
    private double oPosX;
    private double oPosY;
    private double oPosZ;

    public MoCEntityThrowableBlockForGolem(World par1World)
    {
        super(par1World);
        this.preventEntitySpawning = true;
        this.setSize(1F, 1F);
        this.yOffset = this.height / 2.0F;
    }

    public MoCEntityThrowableBlockForGolem(World par1World, Entity entitythrower, double par2, double par4, double par6)//, int behavior)//, int bMetadata)
    {
        this(par1World);
        this.setPosition(par2, par4, par6);
        this.fuse = 250;
        this.prevPosX = oPosX = par2;
        this.prevPosY = oPosY = par4;
        this.prevPosZ = oPosZ = par6;
        this.setMasterID(entitythrower.getEntityId());
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
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setInteger("TypeInt", getType());
        nbttagcompound.setInteger("Metadata", getMetadata());
        nbttagcompound.setInteger("Behavior", getBehavior());
        nbttagcompound.setInteger("MasterID", getMasterID());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        setType(nbttagcompound.getInteger("TypeInt"));
        setMetadata(nbttagcompound.getInteger("Metadata"));
        setBehavior(nbttagcompound.getInteger("Behavior"));
        setMasterID(nbttagcompound.getInteger("MasterID"));
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
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
        	if (this.getBehavior() != 2 && this.onGround) {transformToSolidBlock();} //turn to solid if not moving towards it's master and if on ground
        	
        	if (this.fuse-- <= 0) {transformToSolidBlock();}
        }

        //held ThrowableBlocks don't need to adjust its position
        if (getBehavior() == 1)
        {
            return;
        }

        //throwable block damage code (for all throwable block behaviors)
        if (!this.onGround) //onground!
        {
            List entities_nearby_list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

            int iteration_length = entities_nearby_list.size();
            
            if (iteration_length > 0)
            {
	            for (int index = 0; index < iteration_length; index++)
	            {
	                Entity entity_nearby = (Entity) entities_nearby_list.get(index);
	                
	                if (master != null && entity_nearby.getEntityId() == master.getEntityId())
	                {
	                    continue;
	                }
	                if (entity_nearby instanceof MoCEntityGolem)
	                {
	                    continue;
	                }
	                if (entity_nearby != null && !(entity_nearby instanceof EntityLivingBase))
	                {
	                    continue;
	                }
	
	                if (master != null)
	                {
	                    entity_nearby.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) master), 4);
	                }
	                else
	                {
	                    entity_nearby.attackEntityFrom(DamageSource.generic, 4);
	                }
	            }
            }
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (getBehavior() == 2)
        {
            if (master == null) { return; }

            //moves towards the master entity the bigger the number, the slower
            --acceleration;
            if (acceleration < 10)
            {
                acceleration = 10;
            }

            float tX = (float) this.posX - (float) master.posX;
            float tZ = (float) this.posZ - (float) master.posZ;
            float distXZToMaster = tX * tX + tZ * tZ;

            if (distXZToMaster < 1.0F && master instanceof MoCEntityGolem)
            {
                ((MoCEntityGolem) master).receiveBlock(this.getType(), this.getMetadata());
                this.setDead();
            }

            double summonedSpeed = (double) acceleration;//20D;
            motionX = ((master.posX - this.posX) / summonedSpeed);
            motionY = ((master.posY - this.posY) / 20D + 0.15D);
            motionZ = ((master.posZ - this.posZ) / summonedSpeed);
            if (MoCreatures.isServer())
            {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
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

            float tX = (float) this.posX - (float) master.posX;
            float tZ = (float) this.posZ - (float) master.posZ;
            float distXZToMaster = tX * tX + tZ * tZ;

            double summonedSpeed = (double) acceleration;//20D;
            motionX = ((master.posX - this.posX) / summonedSpeed);
            motionY = ((master.posY - this.posY) / 20D + 0.15D);
            motionZ = ((master.posZ - this.posZ) / summonedSpeed);

            if (distXZToMaster < 2.5F && master instanceof MoCEntityGolem)
            {
                motionX = 0D;
                motionY = 0D;
                motionZ = 0D;
            }

            if (MoCreatures.isServer())
            {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }

            return;
        }

        if (getBehavior() == 5)// exploding throwable block
        {
            acceleration = 5;
            double summonedSpeed = (double) acceleration;//20D;
            motionX = ((oPosX - this.posX) / summonedSpeed);
            motionY = ((oPosY - this.posY) / 20D + 0.15D);
            motionZ = ((oPosZ - this.posZ) / summonedSpeed);
            if (MoCreatures.isServer())
            {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }
            setBehavior(0);
            return;
        }

        this.motionY -= 0.04D;
        if (MoCreatures.isServer())
        {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
        }
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;

        if (this.onGround)
        {
            this.motionX *= 0.699D;
            this.motionZ *= 0.699D;
            this.motionY *= -0.5D;
        }

    }

    private void transformToSolidBlock()
    {
        if ((MoCTools.mobGriefing(this.worldObj)) && (MoCreatures.proxy.golemDestroyBlocks)) // don't drop throwable blocks if mobgriefing is set to false, prevents duping
        {
            if (!(
            		getType() == 8 //flowing water
            		|| getType() == 9 //water
            		|| getType() == 10 //flowing lava
            		|| getType() == 11 //lava
            		||Block.getBlockById(getType()) instanceof IFluidBlock //do not try to transform into solid block if the block is a liquid
            	))
            {
            	worldObj.setBlock((int) posX,(int) posY,(int) posZ, Block.getBlockById(getType()));
            }
        }
        this.setDead();
    }

    public Block getMyBlock()
    {
        if (this.getType() != 0)
        {
            return Block.getBlockById(this.getType());
        }
        return Blocks.stone;
    }

    private Entity getMaster()
    {
        List<Entity> entityList = worldObj.loadedEntityList;
        for (Entity ent : entityList)
        {
            if (ent.getEntityId() == getMasterID()) { return ent; }
        }

        return null;
    }
}