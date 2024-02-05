package drzhark.mocreatures.entity.monster;

import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MoCEntityWraith extends MoCEntityMob//MoCEntityFlyerMob
{
    public MoCEntityWraith(World world)
    {
        super(world);
        isCollidedVertically = false;
        texture = "wraith.png";
        setSize(1.5F, 1.5F);
        isImmuneToFire = false;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(worldObj.difficultySetting.getDifficultyId() == 1 ? 2.0D : 3.0D); // setAttackStrength
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    public boolean d2()
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
                    this.setFire(15);
                }
            }
            
            if (this.getEntityToAttack() != null)  //this is a path finding helper to attack other entities when flying
            {
	        	
	        	double xDistance = this.getEntityToAttack().posX - this.posX;
	            double yDistance = this.getEntityToAttack().posY - this.posY;
	            double zDistance = this.getEntityToAttack().posZ - this.posZ;
	            double overallDistanceSquared = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;
	        	
	            double flySpeed = getMoveSpeed();
	            
	            
	            if (yDistance > 0) //fly up to player
	        	{
	        		 this.motionY += (yDistance / overallDistanceSquared) * 0.3D;
	        	}
		            
	        	if (this.isOnAir() && overallDistanceSquared > 8) //chase player through air
	        	{
			        this.faceEntity(this.getEntityToAttack(), 10F, 10F);

            		this.motionX = xDistance / overallDistanceSquared * flySpeed;
            		
                    this.motionZ = zDistance / overallDistanceSquared * flySpeed;
	        	}
            }
        }
        super.onLivingUpdate();
    }
    
    public boolean isOnAir()
    {
        return worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D), MathHelper.floor_double(posZ));
    }
    
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
}