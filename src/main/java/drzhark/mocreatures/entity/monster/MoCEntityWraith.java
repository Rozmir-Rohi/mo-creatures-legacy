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
        //health = 10;
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
                float f = getBrightness(1.0F);
                if ((f > 0.5F) && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && ((rand.nextFloat() * 30F) < ((f - 0.4F) * 2.0F)))
                {
                    //fire = 300;
                    this.setFire(15);
                }
            }
            
            if (this.getEntityToAttack() != null)  //this is a path finding helper to attack other entities when flying
            {
	        	
	        	double x_distance = this.getEntityToAttack().posX - this.posX;
	            double y_distance = this.getEntityToAttack().posY - this.posY;
	            double z_distance = this.getEntityToAttack().posZ - this.posZ;
	            double overall_distance_sq = x_distance * x_distance + y_distance * y_distance + z_distance * z_distance;
	        	
	            double fly_speed = getMoveSpeed();
	            
	            
	            if (y_distance > 0) //fly up to player
	        	{
	        		 this.motionY += (y_distance / overall_distance_sq) * 0.3D;
	        	}
		            
	        	if (this.isOnAir() && overall_distance_sq > 8) //chase player through air
	        	{
			        this.faceEntity(this.getEntityToAttack(), 10F, 10F);

            		this.motionX = x_distance / overall_distance_sq * fly_speed;
            		
                    this.motionZ = z_distance / overall_distance_sq * fly_speed;
	        	}
            }
        }
        super.onLivingUpdate();
    }
    
    public boolean isOnAir()
    {
        return worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D), MathHelper.floor_double(posZ));
    }
    
    public void onDeath(DamageSource source_of_damage)
    {
        if (source_of_damage.getEntity() != null && source_of_damage.getEntity() instanceof EntityPlayer)
        {
          EntityPlayer player = (EntityPlayer)source_of_damage.getEntity();
          if (player != null) {player.addStat(MoCAchievements.kill_wraith, 1);} 
        } 
        super.onDeath(source_of_damage);
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