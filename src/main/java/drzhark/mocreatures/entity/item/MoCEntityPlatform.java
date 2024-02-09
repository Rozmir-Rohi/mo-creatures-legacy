package drzhark.mocreatures.entity.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MoCEntityPlatform extends Entity {
    private int mastersID = 0;
    private double yOffset;
    private double zOffset;
    private int mountCount;

    public MoCEntityPlatform(World world, int ID, double yOff, double zOff)
    {
        this(world);
        isImmuneToFire = true;
        mastersID = ID;
        yOffset = yOff;
        zOffset = zOff;
        setSize(0.1F, 0.1F);
    }

    public MoCEntityPlatform(World world)
    {
        super(world);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    public boolean attackEntityFrom(Entity entity, int i)
    {
        return false;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void fall(float f)
    {
    }

    

    @Override
    public void handleHealthUpdate(byte byte0)
    {
    }

    private EntityLivingBase getMaster()
    {
        List<Entity> entityList = worldObj.loadedEntityList;
        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == mastersID && entity instanceof EntityLivingBase) { return (EntityLivingBase) entity; }
        }

        return null;
    }

    @Override
    public void onUpdate()
    {
        EntityLivingBase master = getMaster();
        if (master == null || riddenByEntity == null)
        {
            if (MoCreatures.isServer())
            {
                setDead();
            }
            return;
        }

        if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && ((EntityPlayer)riddenByEntity).isSneaking() && ++mountCount > 10)
        {
            riddenByEntity.mountEntity(null);
            riddenByEntity = null;
            setDead();
           
        }
        yOffset = master.getMountedYOffset() * 0.7D;//1.8D;
        rotationYaw = master.renderYawOffset;
        double newPosX = master.posX + (zOffset * Math.cos((MoCTools.realAngle(master.renderYawOffset - 90F)) / 57.29578F));
        double newPosZ = master.posZ + (zOffset * Math.sin((MoCTools.realAngle(master.renderYawOffset - 90F)) / 57.29578F));
        setLocationAndAngles(newPosX, master.posY + yOffset, newPosZ, master.rotationYaw, master.rotationPitch);
    }

    public void setYOffset(double yOff)
    {
        yOffset = yOff;
    }

    public void setZOffset(double zOff)
    {
        zOffset = zOff;
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void updateRiderPosition()
    {
        if (riddenByEntity != null)
        {
            double var1 = Math.cos((double) rotationYaw * Math.PI / 180.0D) * 0.4D;
            double var3 = Math.sin((double) rotationYaw * Math.PI / 180.0D) * 0.4D;
            riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ);
            riddenByEntity.rotationYaw = rotationYaw;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    }
}