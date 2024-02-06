package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityTurtle extends MoCEntityTameableAnimal {
    private boolean isSwinging;
    private boolean twistRight;
    private int flopCounter;

    public MoCEntityTurtle(World world)
    {
        super(world);
        setSize(0.6F, 0.4F);
        setAdult(false);
        setMoCAge(110);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isHiding - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // isUpsideDown - 0 false 1 true
    }

    /**
     * Overridden for the dynamic nightmare texture.
     */
    @Override
    public ResourceLocation getTexture()
    {
        String tempText = "turtle.png";

        if (getName().equals("Donatello") || getName().equals("donatello"))
        {
            tempText = "turtled.png";
        }

        if (getName().equals("Leonardo") || getName().equals("leonardo"))
        {
            tempText = "turtlel.png";
        }

        if (getName().equals("Raphael") || getName().equals("raphael") || getName().equals("Rafael") || getName().equals("rafael"))
        {
            tempText = "turtler.png";
        }

        if (getName().equals("Michelangelo") || getName().equals("michelangelo") || getName().equals("Michaelangelo") || getName().equals("michaelangelo"))
        {
            tempText = "turtlem.png";
        }

        return MoCreatures.proxy.getTexture(tempText);
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.3F;
    }

    public boolean getIsHiding()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getIsUpsideDown()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setIsHiding(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setIsUpsideDown(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    @Override
    public double getYOffset()
    {
        // If we are in SMP, do not alter offset on any client other than the player being mounted on
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer()) { return (yOffset - (1F + (getMoCAge() * 0.01F / 6))); }
        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer()) { return (yOffset + 0.3F); }
        return yOffset;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        
        if (getIsTamed())
        {
            ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
            if (getIsUpsideDown())
            {
                flipFlop(false);
                return true;
            }

            if (ridingEntity == null)
            {
                rotationYaw = entityPlayer.rotationYaw;
                if (MoCreatures.isServer() && (entityPlayer.ridingEntity == null))
                {
                    mountEntity(entityPlayer);
                }
            }
            else
            {
                if (MoCreatures.isServer())
                {
                    this.mountEntity(null);
                }
                motionX = entityPlayer.motionX * 5D;
                motionY = (entityPlayer.motionY / 2D) + 0.2D;
                motionZ = entityPlayer.motionZ * 5D;
            }
            return true;
        }
        flipFlop(!getIsUpsideDown());

        return true;
    }

    @Override
    protected void jump()
    {
        if (isInsideOfMaterial(Material.water))
        {
            motionY = 0.3D;
            if (isSprinting())
            {
                float f = rotationYaw * 0.01745329F;
                motionX -= MathHelper.sin(f) * 0.2F;
                motionZ += MathHelper.cos(f) * 0.2F;
            }
            isAirBorne = true;
        }
    }

    @Override
    public boolean isNotScared()
    {
        return true;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (worldObj.isRemote)
        {
            if (ridingEntity != null)
            {
                updateEntityActionState();
            }
        }

        if (!worldObj.isRemote)
        {
            if (!getIsUpsideDown() && !getIsTamed())
            {
                EntityLivingBase entityLiving = getScaryEntity(4D);
                if ((entityLiving != null) && canEntityBeSeen(entityLiving))
                {

                    if (!getIsHiding())
                    {
                        worldObj.playSoundAtEntity(this, "mocreatures:turtlehissing", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                        setIsHiding(true);
                    }

                    setPathToEntity(null);
                }
                else
                {

                    setIsHiding(false);
                    if (!hasPath() && rand.nextInt(50) == 0)
                    {
                        EntityItem entityItem = getClosestItem(this, 10D, Items.melon, Items.reeds);
                        
                        if (entityItem != null)
                        {
                            float f = entityItem.getDistanceToEntity(this);
                            if (f > 2.0F)
                            {
                                getMyOwnPath(entityItem, f);
                            }
                            if ((f < 2.0F) && (entityItem != null) && (deathTime == 0))
                            {
                                entityItem.setDead();
                                MoCTools.playCustomSound(this, "eating", worldObj);

                                EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                                if (entityPlayer != null)
                                {
                                    MoCTools.tameWithName(entityPlayer, this);
                                }
                            }
                        }
                    }
                }
            }

            if (!getIsUpsideDown() && getIsTamed() && rand.nextInt(20) == 0)
            {
                EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 12D);
                if (entityPlayer != null)
                {
                    PathEntity pathEntity = worldObj.getPathEntityToEntity(this, entityPlayer, 16F, true, false, false, true);
                    setPathToEntity(pathEntity);
                }
            }
        }
    }

    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {

        if (getIsHiding())
        {
            if (rand.nextInt(10) == 0)
            {
                flipFlop(true);
            }
            return false;
        }
        else
        {
            boolean flag = super.attackEntityFrom(damageSource, damageTaken);
            if (rand.nextInt(3) == 0)
            {
                flipFlop(true);
            }
            return flag;
        }
    }

    public void flipFlop(boolean flip)
    {
        fleeingTick = 0;
        setIsUpsideDown(flip);
        setIsHiding(false);
        setPathToEntity(null);
    }

    @Override
    protected void updateEntityActionState()
    {
        if ((ridingEntity != null) && (ridingEntity instanceof EntityPlayer))
        {
            EntityPlayer entityPlayer = (EntityPlayer) ridingEntity;
            if (entityPlayer != null)
            {
                rotationYaw = entityPlayer.rotationYaw;
            }
        }
        else
        {
            super.updateEntityActionState();
        }
    }

    @Override
    public boolean entitiesToIgnoreWhenLookingForAnEntityToAttack(Entity entity)
    {
        return (entity instanceof MoCEntityTurtle) || ((entity.height <= this.height) && (entity.width <= this.width)) || super.entitiesToIgnoreWhenLookingForAnEntityToAttack(entity);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (getIsTamed() && getMoCAge() < 300 && rand.nextInt(800) == 0)
        {
            setMoCAge(getMoCAge() + 1);
        }
        if (getIsUpsideDown() && (ridingEntity == null) && rand.nextInt(20) == 0)
        {
            setSwinging(true);
            flopCounter++;
        }

        if (getIsSwinging())
        {
            swingProgress += 0.2F;

            boolean flag = (flopCounter > (rand.nextInt(3) + 8));

            if (swingProgress > 2.0F && (!flag || rand.nextInt(20) == 0))
            {
                setSwinging(false);
                swingProgress = 0.0F;
                if (rand.nextInt(2) == 0)
                {
                    twistRight = !twistRight;
                }

            }
            else if (swingProgress > 9.0F && flag)
            {
                setSwinging(false);
                swingProgress = 0.0F;
                // TODO
                worldObj.playSoundAtEntity(this, "mob.chicken.plop", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                setIsUpsideDown(false);
                flopCounter = 0;
            }
        }
    }

    public boolean getIsSwinging()
    {
        return isSwinging;
    }

    public void setSwinging(boolean flag)
    {
        isSwinging = flag;
    }

    @Override
    protected boolean isMovementCeased()
    {
        return (getIsUpsideDown() || getIsHiding());
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (ridingEntity == null);
    }

    public int getFlipDirection()
    {
        if (twistRight) { return 1; }
        return -1;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setIsUpsideDown(nbtTagCompound.getBoolean("UpsideDown"));
        setDisplayName(nbtTagCompound.getBoolean("DisplayName"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("UpsideDown", getIsUpsideDown());
        nbtTagCompound.setBoolean("DisplayName", getDisplayName());
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:turtlehurt";
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:turtledying";
    }

    @Override
    protected Item getDropItem()
    {
        if (getName().equals("Donatello") || getName().equals("donatello"))
        { return MoCreatures.bo; }

        
        if (getName().equals("Leonardo") || getName().equals("leonardo"))
        { return MoCreatures.katana; }

        
        if (getName().equals("Rafael") || getName().equals("rafael") || getName().equals("raphael") || getName().equals("Raphael"))
        { return MoCreatures.sai; }

        
        if (getName().equals("Michelangelo") || getName().equals("michelangelo") || getName().equals("Michaelangelo") || getName().equals("michaelangelo"))
        { return MoCreatures.nunchaku; }
        
        
        return MoCreatures.turtleRaw;
    }

    /**
     * Used to avoid rendering the top shell cube
     * 
     * @return
     */
    public boolean isTMNT()
    {
        if (getName().equals("Donatello") || getName().equals("donatello") || getName().equals("Leonardo") || getName().equals("leonardo") || getName().equals("Rafael") || getName().equals("rafael") || getName().equals("raphael") || getName().equals("Raphael") || getName().equals("Michelangelo") || getName().equals("michelangelo") || getName().equals("Michaelangelo") || getName()
                .equals("michaelangelo")) { return true; }
        return false;
    }

    @Override
    public boolean updateMount()
    {
        return getIsTamed();
    }

    @Override
    public boolean forceUpdates()
    {
        return getIsTamed();
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
    	if (itemstack != null)
    	{
	    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
	    	
	        return (
	        			itemstack.getItem() == Items.reeds
	        			|| itemstack.getItem() == Items.melon
	        			|| oreDictionaryNameArray.size() > 0 && oreDictionaryNameArray.contains("listAllveggie") //BOP veg or GregTech6 veg or Palm's Harvest veg
	        			|| oreDictionaryNameArray.contains("listAllfruit") //BOP fruit or GregTech6 fruit or Palm's Harvest fruit
	        		);
    	}
    	else {return false;}
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 2;
    }
}