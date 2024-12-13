package drzhark.mocreatures.entity.monster;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MoCEntitySilverSkeleton extends MoCEntityMob
{
    public int attackCounterLeftArm;
    public int attackCounterRightArm;

    public MoCEntitySilverSkeleton(World world)
    {
        super(world);
        texture = "silverskeleton.png";
        setSize(0.9F, 1.4F);
    }

    @Override
	protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
      getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(worldObj.difficultySetting.getDifficultyId() * 3);
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer())
        {
            if (entityToAttack == null )
            {
                setSprinting(false);
            }
            else
            {
                setSprinting(true);
            }

            if (worldObj.isDaytime())
            {
                float brightness = getBrightness(1.0F);

                if (brightness > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F)
                {
                    setFire(8);
                }
            }
        }

        if (attackCounterLeftArm > 0 && ++attackCounterLeftArm > 10)
        {
            attackCounterLeftArm = 0;
        }

        if (attackCounterRightArm > 0 && ++attackCounterRightArm > 10)
        {
            attackCounterRightArm = 0;
        }

        super.onLivingUpdate();
    }

    @Override
    protected Item getDropItem()
    {
        if (rand.nextInt(10) == 0) //10% chance
        {
            return MoCreatures.silverSword;
        }
        return Items.bone;

    }

     @Override
        public void performAnimation(int animationType)
        {
            
            if (animationType == 1) //left arm
            {
                attackCounterLeftArm = 1;
            }
            if (animationType == 2) //right arm
            {
                attackCounterRightArm = 1;
            }
        }

        /**
         * Starts attack counters and synchronizes animations with clients
         */
        private void startAttackAnimation() 
        {
            if (MoCreatures.isServer())
            {
                boolean willAttackWithLeftArm = rand.nextInt(2) == 0;
                
                if (willAttackWithLeftArm)
                {
                    attackCounterLeftArm = 1;
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 1), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                }else
                {
                    attackCounterRightArm = 1;
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 2), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                }
            }
        }

        @Override
		protected void attackEntity(Entity entity, float distanceToEntity)
        {
            if (attackTime <= 0 && distanceToEntity < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
            {
                attackTime = 20;
                startAttackAnimation();
                attackEntityAsMob(entity);
            }
        }
    @Override
	public float getMoveSpeed()
    {
    	if (isSprinting()) {return 1.2F;}
    	else {return 0.8F;}
    }

    @Override
    protected String getDeathSound()
    {
    return "mob.skeleton.death";
    }

    @Override
    protected String getHurtSound()
    {
    return "mob.skeleton.hurt";
    }

    @Override
    protected String getLivingSound()
    {
    return "mob.skeleton.say";
    }
    
    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
	public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }
   
    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block block)
    {
        playSound("mob.skeleton.step", 0.15F, 1.0F);
    }
}