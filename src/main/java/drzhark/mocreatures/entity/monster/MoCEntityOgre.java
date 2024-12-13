package drzhark.mocreatures.entity.monster;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import drzhark.mocreatures.network.message.MoCMessageExplode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityOgre extends MoCEntityMob{

    public int attackFrequency;
    public int attackCounterLeftArm;
    public int attackCounterRightArm;
    private int movingHead;
    public boolean isPendingSmashAttack;

    public MoCEntityOgre(World world)
    {
        super(world);
        setSize(1.9F, 3F);
        isImmuneToFire = false;
        attackFrequency = 30;
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(getType() > 4 ? 50.0D : 35.0D);
    }

    @Override
    protected double getAttackStrength() 
    {
        return 3D;
    }

    @Override
    public void selectType()
    {
        if (worldObj.provider.isHellWorld)
        {
            setType(rand.nextInt(2)+3);
            setHealth(getMaxHealth());
            isImmuneToFire = true;

        }else
        {
            if (getType() == 0)
            {
                int fireOgreChance = MoCreatures.proxy.fireOgreChance;
                int caveOgreChance = MoCreatures.proxy.caveOgreChance;
                
                int typeChance = rand.nextInt(100);
                
                if (canCaveOgreSpawn() && (typeChance >= (100 - caveOgreChance)))
                {    //System.out.println("can spawn cave o");
                    setType(rand.nextInt(2)+5);
                }
                else if (typeChance >= (100 - fireOgreChance))
                {
                    setType(rand.nextInt(2)+3);
                    isImmuneToFire = true;
                }
                else
                {
                    setType(rand.nextInt(2)+1);
                }

                setHealth(getMaxHealth());
            }
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1: 
        case 2:
            return MoCreatures.proxy.getTexture("ogregreen.png");
        case 3: 
        case 4:
            return MoCreatures.proxy.getTexture("ogrered.png");
        case 5: 
        case 6: 
            return MoCreatures.proxy.getTexture("ogreblue.png");
        default:
            return MoCreatures.proxy.getTexture("ogregreen.png");
        }
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.5F) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            attackTime = 20;
            attackEntityAsMob(entity);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    public void DestroyingOgre()
    {
        if (deathTime > 0) { return; }
        MoCTools.destroyBlast(this, posX, posY + 1.0D, posZ, getDestroyForce(), getOgreFire());
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        float brightness = getBrightness(1.0F);
        if (brightness < 0.5F)
        {
            EntityPlayer closestEntityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, getAttackRange());
            if ((closestEntityPlayer != null) && (worldObj.difficultySetting.getDifficultyId() > 0)) { return closestEntityPlayer; }
        }
        return null;
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:ogredying";
    }

    @Override
    protected Item getDropItem()
    {
        if (getType() < 3)
        {
        return Item.getItemFromBlock(Blocks.obsidian);
        }
        else if (getType() < 5)
        {
            boolean flag = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);
             if (!flag) 
             {
                    return Item.getItemFromBlock(Blocks.fire);
             }
             return MoCreatures.heartFire;
        }
        return Items.diamond;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:ogrehurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:ogre";
    }

    public boolean getOgreFire()
    {
        if(getType() == 3 || getType() == 4)
        {
            isImmuneToFire = true;
            return true;
        }
        return false;
    }

    public float getDestroyForce()
    {
        int t = getType();
        if (t < 3) //green
        {
            return MoCreatures.proxy.ogreStrength;
        }else if (t < 5) //red
        {
            return MoCreatures.proxy.fireOgreStrength;
        }
        return MoCreatures.proxy.caveOgreStrength;
    }

    public int getAttackRange()
    {
        return MoCreatures.proxy.ogreAttackRange;
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer())
        {
            
            if ((entityToAttack != null) && (rand.nextInt(attackFrequency) == 0) && attackTime == 0 && attackCounterLeftArm == 0 && attackCounterRightArm == 0)
            {
                startOgreAttack();
            }
            
            if ((attackTime <= 0) && isPendingSmashAttack)
            {
                isPendingSmashAttack = false;
                DestroyingOgre();
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageExplode(getEntityId()), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
            }

            if (getType() > 2)
            {
                
            
                if (worldObj.isDaytime())
                {
                    float brightness = getBrightness(1.0F);
                    if ((brightness > 0.5F) && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && ((rand.nextFloat() * 30F) < ((brightness - 0.4F) * 2.0F)))
                    {
                        setHealth(getHealth() - 5);
                    }
                }
            }
        }

        if (attackCounterLeftArm > 0 && ++attackCounterLeftArm > 30)
        {
            attackCounterLeftArm = 0;
        }
        
        if (attackCounterRightArm > 0 && ++attackCounterRightArm > 30)
        {
            attackCounterRightArm = 0;
        }
        super.onLivingUpdate();
    }

    /**
     * Starts attack counters and synchronizes animations with clients
     */
    private void startOgreAttack() 
    {
        if (MoCreatures.isServer())
        {
            attackTime = 15;
            isPendingSmashAttack = true;
            boolean leftArmW = (getType() == 2 || getType() == 4 || getType() == 6) && rand.nextInt(2) == 0;

            if (leftArmW)
            {
                attackCounterLeftArm = 1;
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 1), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
            }
            else
            {
                attackCounterRightArm = 1;
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 2), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
            }
        }
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
    
    @Override
	public void onDeath(DamageSource damageSource) {
        if (damageSource.getEntity() != null && damageSource.getEntity() instanceof EntityPlayer)
        {
          EntityPlayer player = (EntityPlayer)damageSource.getEntity();
          if (player != null)
            player.addStat(MoCAchievements.kill_ogre, 1); 
        } 
        super.onDeath(damageSource);
      }

    public int getMovingHead()
    {
        if (getType() == 1 || getType() == 3 || getType() == 5) //single headed ogre
        {
            return 1;
        }

        if (rand.nextInt(100) == 0)
        {
            movingHead = rand.nextInt(2)+2;  //randomly changes the focus head, returns 2 or 3
        }
        return movingHead;
    }

    private boolean canCaveOgreSpawn()
    {
        return (!worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))) && (posY < 50D);
    }
}