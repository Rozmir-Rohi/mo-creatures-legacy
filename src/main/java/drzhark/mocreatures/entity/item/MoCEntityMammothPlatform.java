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

public class MoCEntityMammothPlatform extends Entity {
    private int mastersID = 0;
    private double yOffset;
    private double zOffset;
    private int mountCount;

    public MoCEntityMammothPlatform(World world, int id, double yOffset, double zOffset)
    {
        this(world);
        isImmuneToFire = true;
        mastersID = id;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        setSize(0.1F, 0.1F);
    }

    public MoCEntityMammothPlatform(World world)
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

    public void setYOffset(double yOffset)
    {
        this.yOffset = yOffset;
    }

    public void setZOffset(double zOffset)
    {
        this.zOffset = zOffset;
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
            riddenByEntity.setPosition(posX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ);
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