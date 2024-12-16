package drzhark.mocreatures.entity.monster;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.animal.MoCEntityPetScorpion;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityScorpion extends MoCEntityMob {
    private boolean isPoisoning;
    private int poisontimer;
    public int mouthCounter;
    public int armCounter;
    private int hideCounter;
    private int attackDamage = worldObj.difficultySetting.getDifficultyId();

    public MoCEntityScorpion(World world)
    {
        super(world);
        setSize(1.4F, 0.9F);
        poisontimer = 0;
        setAdult(true);
        setMoCAge(20);

        if (MoCreatures.isServer())
        {
            if (rand.nextInt(4) == 0)
            {
                setHasBabies(true);
            }
            else
            {
                setHasBabies(false);
            }
        }
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
    }

    @Override
    public void selectType()
    {
        checkSpawningBiome();

        if (checkSpawningBiome() //even if checkSpawningBiome didn't apply the type, it still checks if it can spawn in the biome
        		&& getType() == 0)
        {
            setType(1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("scorpiondirt.png");
        case 2:
            return MoCreatures.proxy.getTexture("scorpioncave.png");
        case 3:
            return MoCreatures.proxy.getTexture("scorpionnether.png");
        case 4:
            return MoCreatures.proxy.getTexture("scorpionfrost.png");
        default:
            return MoCreatures.proxy.getTexture("scorpiondirt.png");
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isPicked - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // has babies - 0 false 1 true
    }

    public boolean getHasBabies()
    {
        return getIsAdult() && (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public boolean getIsPicked()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getIsPoisoning()
    {
        return isPoisoning;
    }

    public void setHasBabies(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public void setPicked(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setPoisoning(boolean flag)
    {
        if (flag && MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        isPoisoning = flag;
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType == 0) //tail animation
        {
            setPoisoning(true);
        }
        else if (animationType == 1) //arm swinging
        {
            armCounter = 1;
            //swingArm();
        }
        else if (animationType == 3) //movement of mouth
        {
            mouthCounter = 1;
        }
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.8F;
    }

    @Override
    public boolean isOnLadder()
    {
        return isCollidedHorizontally;
    }

    public boolean climbing()
    {
        return !onGround && isOnLadder();
    }

    /**
     * finds shelter from sunlight
     */
    protected void findSunLightShelter()
    {
        Vec3 vectorThreeDimensionalToPossibleShelter = findPossibleShelter();
        if (vectorThreeDimensionalToPossibleShelter == null)
        {
            hideCounter++;
            if (hideCounter > 200)
            {
                hideCounter = 0;
            }
            updateWanderPath();
            return;// false;
        }
        else
        {
            getNavigator().tryMoveToXYZ(vectorThreeDimensionalToPossibleShelter.xCoord, vectorThreeDimensionalToPossibleShelter.yCoord, vectorThreeDimensionalToPossibleShelter.zCoord, getMoveSpeed() / 2F);
        }
    }

    /**
     * Does it want to hide?
     * 
     * @return
     */
    private boolean wantsToHide()
    {
        return (worldObj.isDaytime());
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer() && wantsToHide())
        {
            findSunLightShelter();
        }

        if (!onGround && (ridingEntity != null))
        {
            rotationYaw = ridingEntity.rotationYaw;
        }
        if (getIsAdult() && fleeingTick > 0)
        {
            fleeingTick = 0;
        }

        if (mouthCounter != 0 && mouthCounter++ > 50)
        {
            mouthCounter = 0;
        }

        if (MoCreatures.isServer() && (armCounter == 10 || armCounter == 40))
        {
            playSound("mocreatures:scorpionclaw", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
        }

        if (armCounter != 0 && armCounter++ > 24)
        {
            armCounter = 0;
        }

        if (getIsPoisoning())
        {
            poisontimer++;
            if (poisontimer == 1)
            {
                playSound("mocreatures:scorpionsting", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }
            if (poisontimer > 50)
            {
                poisontimer = 0;
                setPoisoning(false);
            }
        }

        if (MoCreatures.isServer() && !getIsAdult() && (rand.nextInt(200) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 120)
            {
                setAdult(true);
            }
        }

        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();

            if ((entityThatAttackedThisCreature != null) && (entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && getIsAdult())
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

    @Override
    protected Entity findPlayerToAttack()
    {
        if (worldObj.difficultySetting.getDifficultyId() > 0 && (!worldObj.isDaytime()) && getIsAdult())// only attacks player at night
        {
            EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 12D);
            if ((entityPlayer != null)) { return entityPlayer; }
            {
                if ((rand.nextInt(80) == 0))
                {
                    EntityLivingBase entityLiving = getClosestEntityLiving(this, 10D);
                if (entityLiving != null && !(entityLiving instanceof EntityPlayer) && canEntityBeSeen(entityLiving)) // blood - add LoS requirement
                    return entityLiving;
                }
            }
        }
        return null;
    }

    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return 
        		(
        			(super.shouldEntityBeIgnored(entity))
        			|| (getIsTamed() && entity instanceof MoCEntityScorpion && ((MoCEntityScorpion) entity).getIsTamed())
        		);
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if ((distanceToEntity > 2.0F) && (distanceToEntity < 6F) && (rand.nextInt(50) == 0))
        {
            if (onGround)
            {
                double xDistance = entity.posX - posX;
                double zDistance = entity.posZ - posZ;
                float overallHorizontalDistanceSquared = MathHelper.sqrt_double((xDistance * xDistance) + (zDistance * zDistance));
                motionX = ((xDistance / overallHorizontalDistanceSquared) * 0.5D * 0.8D) + (motionX * 0.2D);
                motionZ = ((zDistance / overallHorizontalDistanceSquared) * 0.5D * 0.8D) + (motionZ * 0.2D);
                motionY = 0.4D;
            }
        }
        else if (attackTime <= 0 && (distanceToEntity < 3.0D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            if (!getIsPoisoning() && rand.nextInt(5) == 0 && entity instanceof EntityLivingBase)
            {
                stingAndApplyEffectOnEntity(entity);
            }
            else
            {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
                swingArm();
            }
        }
    }

	private void stingAndApplyEffectOnEntity(Entity entity)
	{
		int potionTime = 70;
		
		setPoisoning(true);
		if (getType() <= 1)// regular scorpions
		{
		    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, potionTime, 0));
		}
		else if (getType() == 2) //cave scorpions
		{
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, potionTime, 0));
		}
		else if (getType() == 4)// frost scorpions
		{
		    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, potionTime, 0));

		}
		else if (getType() == 3)// fire scorpions
		{
		    if ((entity instanceof EntityPlayer) && MoCreatures.isServer() && !worldObj.provider.isHellWorld)
		    {
		        ((EntityLivingBase) entity).setFire(15);
		    }
		}
	}

    public void swingArm()
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 1), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        
        if (getType() == 3 && !isImmuneToFire)
    	{
			isImmuneToFire = true;  //sets fire immunity true for fire scorpions if it becomes false, which does sometimes happen with world reloads.
		}
    }

    public boolean swingingTail()
    {
        return getIsPoisoning() && poisontimer < 15;
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);

        if (MoCreatures.isServer() && getIsAdult() && getHasBabies())
        {
            int k = rand.nextInt(5);
            for (int i = 0; i < k; i++)
            {
                MoCEntityPetScorpion entityBabyScorpion = new MoCEntityPetScorpion(worldObj);
                entityBabyScorpion.setPosition(posX, posY, posZ);
                entityBabyScorpion.setAdult(false);
                entityBabyScorpion.setType(getType());
                worldObj.spawnEntityInWorld(entityBabyScorpion);

            }
        }
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:scorpiondying";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:scorpionhurt";
    }

    @Override
    protected String getLivingSound()
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 3), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        return "mocreatures:scorpiongrunt";
    }

    @Override
    protected Item getDropItem()
    {
        if (!getIsAdult()) { return null; }

        boolean flag = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);

        switch (getType())
        {
        case 1:
            if (flag) { return MoCreatures.scorpStingDirt; }
            return MoCreatures.chitin;
        case 2:
            if (flag) { return MoCreatures.scorpStingCave; }
            return MoCreatures.chitinCave;
        case 3:
            if (flag) { return MoCreatures.scorpStingNether; }
            return MoCreatures.chitinNether;
        case 4:
            if (flag) { return MoCreatures.scorpStingFrost; }
            return MoCreatures.chitinFrost;
        default:
            return null;
        }
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        if (!flag) return;
        Item item = getDropItem();

        if (item != null)
        {
            if (rand.nextInt(3) == 0)
            {
                dropItem(item, 1);
            }
        }

    }

    @Override
    public boolean getCanSpawnHere()
    {
        return
        	(
        			isValidLightLevel()
        			&& MoCreatures.entityMap.get(getClass()).getFrequency() > 0
        			&& checkSpawningBiome() //don't let Scorpions spawn in biomes that they are not supposed to spawn in (mainly beaches)
        			&& getCanSpawnHereLiving()
        			&& getCanSpawnHereCreature()
        	);
    }

    @Override
    public boolean checkSpawningBiome()
    {
    	if (getType() == 0)
    	{
	        if (worldObj.provider.isHellWorld)
	        {
	            setType(3);
	            isImmuneToFire = true;
	            return true;
	        }
	
	        int xCoordinate = MathHelper.floor_double(posX);
	        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
	        int zCoordinate = MathHelper.floor_double(posZ);
	
	        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
	
	        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SNOWY))
	        {
	            setType(4);
	        }
	        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.BEACH)) //do not spawn in beaches, the code for this is continued in MoCEventHooks.java
	        {
	        	return false;
	        }
	        else if (!worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && (posY < 50D))
	        {
	            setType(2);
	            return true;
	        }
    	}

        return true;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setHasBabies(nbtTagCompound.getBoolean("Babies"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Babies", getHasBabies());
    }

    @Override
    public boolean isAIEnabled()
    {
        return wantsToHide() && (entityToAttack == null) && (hideCounter < 50);
    }

    @Override
    public int getTalkInterval()
    {
        return 300;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
	public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    @Override
    public float getAdjustedYOffset()
    {
        return 30F;
    }
}