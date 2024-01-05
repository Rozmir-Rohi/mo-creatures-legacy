package drzhark.mocreatures.entity.item;

import java.util.List;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityItemPlaceable;
import drzhark.mocreatures.entity.monster.MoCEntityOgre;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityLitterBox extends MoCEntityItemPlaceable {
    public int littertime;

    public MoCEntityLitterBox(World world)
    {
        super(world);
        setSize(1.0F, 0.3F);
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "litterbox.png";
    }

    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture("litterbox.png");
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5D); // setMaxHealth
    }
    
    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(15, Byte.valueOf((byte) 0)); // usedLitter - 0 false 1 true
        dataWatcher.addObject(16, Byte.valueOf((byte) 0)); // pickedUp - 0 false 1 true
    }

    public boolean getPickedUp()
    {
        return (dataWatcher.getWatchableObjectByte(15) == 1);
    }

    public boolean getUsedLitter()
    {
        return (dataWatcher.getWatchableObjectByte(16) == 1);
    }

    public void setPickedUp(boolean flag)
    {
        if (worldObj.isRemote) { return; }
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(15, Byte.valueOf(input));
    }

    public void setUsedLitter(boolean flag)
    {
        if (worldObj.isRemote) { return; }
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(16, Byte.valueOf(input));
    }
     
    @Override
    public void dropItemEntity()
    {
    	if (MoCreatures.isServer() && !getUsedLitter())
    		{
    			this.entityDropItem(new ItemStack(MoCreatures.litterbox), 0F);
    		}
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
            return (yOffset - 1.15F);
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

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();

        if ((itemstack != null) && MoCreatures.isServer() && getUsedLitter() && (itemstack.getItem() == Item.getItemFromBlock(Blocks.sand)))
        {
            if (--itemstack.stackSize == 0)
            {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
            }
            setUsedLitter(false);
            littertime = 0;
            entityplayer.addStat(MoCAchievements.kitty_litter, 1);
            return true;
        }
        else if ((itemstack == null))
        {
            rotationYaw = entityplayer.rotationYaw;
            if ((itemstack == null) && (this.ridingEntity == null) && (entityplayer.ridingEntity == null))
            {
                if (MoCreatures.isServer())
                {
                    mountEntity(entityplayer);
                }
            }
            else
            {
                if (MoCreatures.isServer())
                {
                    this.mountEntity(null);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void moveEntity(double d, double d1, double d2)
    {
        if ((ridingEntity != null) || !onGround || !MoCreatures.proxy.staticLitter)
        {
            if (!worldObj.isRemote)
            {
                super.moveEntity(d, d1, d2);
            }
        }
    }
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        if (MoCreatures.isServer() && getUsedLitter())
        {
            EntityItem entityitem = getClosestEntityItem(this, 1D);
            
            if (entityitem != null)	
            {
            
            	Block block_item = Block.getBlockFromItem(entityitem.getEntityItem().getItem());
            
            	if (block_item == Blocks.sand)
            	{
            		entityitem.setDead();
            		setUsedLitter(false);
            	}
            }
        }
    }
    

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (onGround)
        {
            setPickedUp(false);
        }
        if (getUsedLitter() && MoCreatures.isServer())
        {
            littertime++;
            worldObj.spawnParticle("smoke", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(12D, 4D, 12D));
            for (int i = 0; i < list.size(); i++)
            {
                Entity entity = (Entity) list.get(i);
                if (!(entity instanceof EntityMob))
                {
                    continue;
                }
                EntityMob entitymob = (EntityMob) entity;
                entitymob.setAttackTarget(this);
                if (entitymob instanceof EntityCreeper)
                {
                    ((EntityCreeper) entitymob).setCreeperState(-1);
                }
                if (entitymob instanceof MoCEntityOgre)
                {
                    ((MoCEntityOgre) entitymob).pendingSmashAttack = false;
                }
            }

        }
        if (littertime > 5000 && MoCreatures.isServer())
        {
            setUsedLitter(false);
            littertime = 0;
        }
    }

    @Override
    protected void updateEntityActionState()
    {
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setBoolean("UsedLitter", getUsedLitter());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        setUsedLitter(nbttagcompound.getBoolean("UsedLitter"));
    }
}