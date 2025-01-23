package drzhark.mocreatures.entity.monster;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MoCEntityWraith extends MoCEntityMob
{
	
    public MoCEntityWraith(World world)
    {
        super(world);
        isCollidedVertically = false;
        texture = "wraith.png";
        setSize(1.5F, 1.5F);
        isImmuneToFire = false;
        noClip = MoCreatures.proxy.wraithsCanGoThroughWalls;
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(worldObj.difficultySetting.getDifficultyId() + 1); // setAttackStrength
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    public boolean canSpawnHere()
    {
        return super.getCanSpawnHere();
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:wraithdying";
    }

    @Override
    protected Item getDropItem()
    {
        return Items.gunpowder;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:wraithhurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:wraith";
    }

    @Override
    public void onLivingUpdate()
    {
        if (!worldObj.isRemote)
        {
            if (worldObj.isDaytime())
            {
                float brightness = getBrightness(1.0F);
                if ((brightness > 0.5F) && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && ((rand.nextFloat() * 30F) < ((brightness - 0.4F) * 2.0F)))
                {
                    //fire = 300;
                    setFire(15);
                }
            }
            
            if (getEntityToAttack() != null)  //this is a path finding helper to attack other entities when flying
            {
	        	
	        	double xDistance = getEntityToAttack().posX - posX;
	            double yDistance = getEntityToAttack().posY - posY;
	            double zDistance = getEntityToAttack().posZ - posZ;
	            double overallDistanceSquared = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;
	        	
	            double flySpeed = getMoveSpeed();
	            
	            
	            if (yDistance != 0) //fly to player's height
	        	{
	        		 motionY += (yDistance / overallDistanceSquared) * 0.3D;
	        	}
		            
	        	if (isOnAir() && overallDistanceSquared > 8) //dash ability and find player when hit from far away
	        	{
			        faceEntity(getEntityToAttack(), 10F, 10F);

            		motionX = xDistance / overallDistanceSquared * flySpeed;
            		
                    motionZ = zDistance / overallDistanceSquared * flySpeed;
	        	}
            }
        }
        super.onLivingUpdate();
    }
    
    @Override
    protected Entity findPlayerToAttack()
    {
        EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
        
        if (
	        	entityPlayer != null
	        	&& MoCTools.isEntityRidingUndeadMoCreature(this, entityPlayer)
        	)
        {
        	return null;
        }
        	
        else {return entityPlayer;}
    }
    
    public boolean isOnAir()
    {
        return worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D), MathHelper.floor_double(posZ));
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (MoCreatures.isServer())
        {
        	if (noClip && DamageSource.inWall.equals(damageSource))
            {
         	   return false;
            }
        }
        
        return super.attackEntityFrom(damageSource, damageTaken);
    }
    
    @Override
	public void onDeath(DamageSource damageSource)
    {
        if (damageSource.getEntity() != null && damageSource.getEntity() instanceof EntityPlayer)
        {
          EntityPlayer player = (EntityPlayer)damageSource.getEntity();
          if (player != null) {player.addStat(MoCAchievements.kill_wraith, 1);} 
        } 
        super.onDeath(damageSource);
    }

    @Override
    public boolean isFlyer()
    {
        return true;
    }

    @Override
    public float getMoveSpeed()
    {
        return 1.3F;
    }
    
    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

	@Override
	public boolean entitiesThatAreScary(Entity entityNearby)
	{
		return false;
	}
}