package drzhark.mocreatures.entity.item;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityItemPlaceable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class MoCEntityFishBowl extends MoCEntityItemPlaceable {
    private int rotation = 0;
    private boolean isMoving = false;

    public MoCEntityFishBowl(World world)
    {
        super(world);
        setSize(1.0F, 1.0F);
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "fishbowl.png";
    }

    public MoCEntityFishBowl(World world, double d, double d1, double d2)
    {
        super(world);
        setSize(1.0F, 1.0F);
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "fishbowl.png";
    }

    public MoCEntityFishBowl(World world, int i)
    {
        this(world);
        setType(i);
    }

    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture("fishbowl.png");
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D); // setMaxHealth
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        dataWatcher.addObject(15, Byte.valueOf((byte) 0)); // pickedUp - 0 false 1 true
        dataWatcher.addObject(16, Integer.valueOf(0)); // sheetColor int
    }

    public int getType()
    {
        return dataWatcher.getWatchableObjectInt(16);
    }

    public boolean getPickedUp()
    {
        return (dataWatcher.getWatchableObjectByte(15) == 1);
    }

    public void setPickedUp(boolean flag)
    {
        if (worldObj.isRemote) { return; }
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(15, Byte.valueOf(input));
    }

    public void setType(int i)
    {
        if (worldObj.isRemote) { return; }
        dataWatcher.updateObject(16, Integer.valueOf(i));
    }

    public boolean attackEntityFrom(Entity entity, int i)
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return !isDead;
    }

    @Override
    public boolean canBePushed()
    {
        return !isDead;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }

    @Override
    public boolean canEntityBeSeen(Entity entity)
    {
        return worldObj.rayTraceBlocks(Vec3.createVectorHelper(posX, posY + getEyeHeight(), posZ), Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)) == null;
    }
    
    @Override
    public void dropItemEntity()
    {
        if (MoCreatures.isServer())
        {
        	switch (getType())
        	{
        		case 0:
        			entityDropItem(new ItemStack(MoCreatures.fishbowlEmpty, 1, 0), 0F);
        			break;
	        	case 1:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy1, 1, 1), 0F);
	        		break;
	        	case 2:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy2, 1, 2), 0F);
	        		break;
	        	case 3:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy3, 1, 3), 0F);
	        		break;
	        	case 4:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy4, 1, 4), 0F);
	        		break;
	        	case 5:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy5, 1, 5), 0F);
	        		break;
	        	case 6:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy6, 1, 6), 0F);
	        		break;
	        	case 7:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy7, 1, 7), 0F);
	        		break;
	        	case 8:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy8, 1, 8), 0F);
	        		break;
	        	case 9:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy9, 1, 9), 0F);
	        		break;
	        	case 10:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlFishy10, 1, 10), 0F);
	        		break;
	        	case 11:
	        		entityDropItem(new ItemStack(MoCreatures.fishbowlWater, 1, 11), 0F);
	        		break;
	        	default:
        			entityDropItem(new ItemStack(MoCreatures.fishbowlEmpty, 1, 0), 0F);
        	}
        }
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return null;
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.0F;
    }

    @Override
    public double getYOffset()
    {
        // If we are in SMP, do not alter offset on any client other than the player being mounted on
        if (((ridingEntity instanceof EntityPlayer) && !worldObj.isRemote) || ridingEntity == MoCreatures.proxy.getPlayer())//MoCProxy.mc().thePlayer)
        {
            setPickedUp(true);
            return (yOffset - 1.0F);
        }
        else
        {
            return yOffset;
        }
    }

    @Override
    public void handleHealthUpdate(byte byte0)
    {
    }

    public static ItemStack toItemStack(int type)
    {
        switch (type)
        {
        case 0:
            return new ItemStack(MoCreatures.fishbowlEmpty, 1, 0);
        case 1:
            return new ItemStack(MoCreatures.fishbowlFishy1, 1, 1);
        case 2:
            return new ItemStack(MoCreatures.fishbowlFishy2, 1, 2);
        case 3:
            return new ItemStack(MoCreatures.fishbowlFishy3, 1, 3);
        case 4:
            return new ItemStack(MoCreatures.fishbowlFishy4, 1, 4);
        case 5:
            return new ItemStack(MoCreatures.fishbowlFishy5, 1, 5);
        case 6:
            return new ItemStack(MoCreatures.fishbowlFishy6, 1, 6);
        case 7:
            return new ItemStack(MoCreatures.fishbowlFishy7, 1, 7);
        case 8:
            return new ItemStack(MoCreatures.fishbowlFishy8, 1, 8);
        case 9:
            return new ItemStack(MoCreatures.fishbowlFishy9, 1, 9);
        case 10:
            return new ItemStack(MoCreatures.fishbowlFishy10, 1, 10);
        case 11:
            return new ItemStack(MoCreatures.fishbowlWater, 1, 11);
        default:
            return null;
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();

        if ((itemstack != null) && (getType() > 0 && getType() < 11) && ((itemstack.getItem() == MoCreatures.fishbowlEmpty) || (itemstack.getItem() == MoCreatures.fishbowlWater)))
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            ItemStack mystack = toItemStack(getType());
            entityPlayer.inventory.addItemStackToInventory(mystack);
            setType(0);
            return true;
        }
        
        if (itemstack == null)
        {
	        if ((ridingEntity == null) && (entityPlayer.ridingEntity == null) && (MoCreatures.isServer()))
	        {
	            rotationYaw = entityPlayer.rotationYaw;
	            mountEntity(entityPlayer);
	        }
	        else
	        {
	            mountEntity(null);
	            motionX = entityPlayer.motionX * 5D;
	            motionY = (entityPlayer.motionY / 2D) + 0.2D;
	            motionZ = entityPlayer.motionZ * 5D;
	        }
        	return true;
    	}
        return false;
    }

    @Override
    public void moveEntity(double x, double y, double z)
    {
        if ((ridingEntity != null) || !onGround)
        {
            if (!worldObj.isRemote)
            {
                super.moveEntity(x, y, z);
            }
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (rand.nextInt(80) == 0)
        {
            isMoving = !isMoving;
        }
        if (isMoving)
        {
            rotation += rand.nextInt(10);
            if (rotation > 360)
            {
                rotation = 0;
            }
        }
        prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setType(nbtTagCompound.getInteger("SheetColour"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("SheetColour", getType());
    }

    public int getRotation()
    {
        return rotation;
    }
}