package drzhark.mocreatures.entity.witchery_integration;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityMob;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityWerewolfPlayerDummyWitchery extends MoCEntityMob {
    private boolean isTransforming = false;
    private boolean isHunched;
    private int textureCounter;
    private EntityPlayer playerToMirror;
    public boolean noClip = true;
    private boolean isMountedOnPlayer = false;

    public MoCEntityWerewolfPlayerDummyWitchery(World world)
    {
        super(world);
        setSize(0.9F, 1.6F);
    }
    
    public MoCEntityWerewolfPlayerDummyWitchery(World world, EntityPlayer player, boolean shouldMountPlayer)
    {
        super(world);
        setSize(0.9F, 1.6F);
        this.playerToMirror = player;
        if (shouldMountPlayer)
        {
        	this.isMountedOnPlayer = true;
        	setInvisible(true);
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); //hunched
    }
    
    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            setType(1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    { 
       return MoCreatures.proxy.getTexture("wolftimber.png");
    }

    public boolean getIsHumanForm()
    {
        return false;
    }

    public boolean isMountedOnPlayer()
    {
        return (isMountedOnPlayer);
    }

    public void setHumanForm(boolean flag)
    {
    }

    public boolean getIsHunched()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setHunched(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageDealtToWerewolf)
    {
        return false;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        return null;
    }
    
    public EntityLivingBase getClosestTarget(Entity entity, double distance)
    {
        return null;
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
    }
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        if (!worldObj.isRemote)
        {
        	if (playerToMirror == null)
        	{
        		setDead();
        	}
        	
        	else
        	{
        		
        		if (
        				playerToMirror.isDead
        				|| (
        						!(40 <= playerToMirror.getMaxHealth() && playerToMirror.getMaxHealth() <= 60)
        						&& !playerToMirror.isInvisible()
        					)
        			)
            	{
            		setDead();
            	}
        		
        		changePlayersPerspectiveToThirdPerson();
        		
        		if (!(playerToMirror.onGround))
        		{
        			setInvisible(!isMountedOnPlayer); //turn mounted werewolf dummy on for better tracking in air and turn off the other dummy
        		}
        		
        		if ((playerToMirror.onGround))
        		{
        			setInvisible(isMountedOnPlayer);  //turn non-mounted werewolf dummy on for better tracking on ground and turn off the other dummy
        		}
        		
        		
        		
        		if(getMaxHealth() != playerToMirror.getMaxHealth())
        		{
        			getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(playerToMirror.getMaxHealth());
        		}
        		
        		if (getHealth() != playerToMirror.getHealth())
        		{
        			setHealth(playerToMirror.getHealth());
        		}
        		
	        	if (rotationYawHead != playerToMirror.rotationYawHead)
	        	{
	        		rotationYawHead = playerToMirror.rotationYawHead;
	        	}
	        	
	        	if (rotationYaw != playerToMirror.rotationYaw)
	        	{
	        		rotationYaw = playerToMirror.rotationYaw;
	        	}
	        	
	        	
	        	
	        	
	        	if (!isMountedOnPlayer && posX != playerToMirror.posX)
	        	{
	        		posX = playerToMirror.posX;
	        	}
	        	
	        	if (!isMountedOnPlayer && posY != playerToMirror.posY)
	        	{
	        		posY = playerToMirror.posY;
	        	}
	        	
	        	if (!isMountedOnPlayer && posZ != playerToMirror.posZ)
	        	{
	        		posZ = playerToMirror.posZ;
	        	}
	        	
	        	
	        	
	        	
	        	if (playerToMirror.isSprinting() && !getIsHunched())
	        	{
	        		setHunched(true);
	        	}
	        	
	        	if (!playerToMirror.isSprinting() && getIsHunched())
	        	{
	        		setHunched(false);
	        	}
        	}
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void changePlayersPerspectiveToThirdPerson() 
    {
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
		{
			Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
		}
		
	}

	@Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setHumanForm(nbtTagCompound.getBoolean("HumanForm"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("HumanForm", getIsHumanForm());
    }
}
