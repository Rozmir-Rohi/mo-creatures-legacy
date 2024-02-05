package drzhark.mocreatures.entity.animal;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.monster.MoCEntityScorpion;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityPetScorpion extends MoCEntityTameableAnimal {
    public static final String scorpionNames[] = { "Dirt", "Cave", "Nether", "Frost", "Undead" };
    private boolean isPoisoning;
    private int poisontimer;
    public int mouthCounter;
    public int armCounter;
    private int hideCounter;

    public MoCEntityPetScorpion(World world)
    {
        super(world);
        setSize(1.4F, 0.9F);
        poisontimer = 0;
        setAdult(false);
        setMoCAge(20);
        roper = null;
        setHasBabies(false);
        this.stepHeight = 20.0F;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
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
        boolean saddle = getIsRideable();
        switch (getType())
        {
            case 1:
                if (!saddle) { return MoCreatures.proxy.getTexture("scorpiondirt.png"); }
                return MoCreatures.proxy.getTexture("scorpiondirtsaddle.png");
            case 2:
                if (!saddle) { return MoCreatures.proxy.getTexture("scorpioncave.png"); }
                return MoCreatures.proxy.getTexture("scorpioncavesaddle.png");
            case 3:
                if (!saddle) { return MoCreatures.proxy.getTexture("scorpionnether.png"); }
                return MoCreatures.proxy.getTexture("scorpionnethersaddle.png");
            case 4:
                if (!saddle) { return MoCreatures.proxy.getTexture("scorpionfrost.png"); }
                return MoCreatures.proxy.getTexture("scorpionfrostsaddle.png");
            case 5:
                if (!saddle) { return MoCreatures.proxy.getTexture("scorpionundead.png"); }
                return MoCreatures.proxy.getTexture("scorpionundeadsaddle.png");
            default:
                return MoCreatures.proxy.getTexture("scorpiondirt.png");
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isRideable - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // has babies - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // isPicked - 0 false 1 true
    }

    public boolean getIsRideable()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getHasBabies()
    {
        return getIsAdult() && (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public boolean getIsPicked()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public boolean getIsPoisoning()
    {
        return isPoisoning;
    }

    public void setRideable(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setHasBabies(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public void setPicked(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }

    public void setPoisoning(boolean flag)
    {
        if (flag && MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
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

    @Override
    public void onLivingUpdate()
    {
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
            worldObj.playSoundAtEntity(this, "mocreatures:scorpionclaw", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
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
                worldObj.playSoundAtEntity(this, "mocreatures:scorpionsting", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
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
        
        if (this.ridingEntity instanceof EntityPlayer)
        {
        	if (((EntityPlayer) this.ridingEntity).inventory.getCurrentItem() != null)
        	{
        		this.mountEntity(null);
        		setPicked(false);
        	}
        }

        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
    	
    	if (MoCreatures.isServer())
        {
        	if (this.ridingEntity != null && 
        			(damageSource.getEntity() == this.ridingEntity || DamageSource.inWall.equals(damageSource)))
            {
         	   return false;
            }
        	
        	else if (super.attackEntityFrom(damageSource, damageTaken))
            {
                Entity entityThatAttackedThisCreature = damageSource.getEntity();
                if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName()))))
                { 
                	return false; 
                }

                
                if ((entityThatAttackedThisCreature != null) && (entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && getIsAdult())
                {
                    entityToAttack = entityThatAttackedThisCreature;
                }
                return true;
            }
        }
    	
    	return true;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if (worldObj.difficultySetting.getDifficultyId() > 0 && (!worldObj.isDaytime()) && getIsAdult())// only attacks player at night
        {
            if (!getIsTamed())
            {
                EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 12D);
                if ((entityPlayer != null) && getIsAdult()) { return entityPlayer; }
            }
            else
            {
                if ((rand.nextInt(80) == 0))
                {
                    EntityLivingBase entityLiving = getClosestEntityLiving(this, 10D);
                    return entityLiving;
                }

            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
        {
	        if (this.getIsTamed() && this.riddenByEntity == null && this.ridingEntity == null) //defend owner if they are attacked by an entity
	    	{
	    		EntityPlayer ownerOfEntityThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.getOwnerName());
	    		
	    		if (ownerOfEntityThatIsOnline != null)
	    		{
	    			EntityLivingBase entityThatAttackedOwner = ownerOfEntityThatIsOnline.getAITarget();
	    			
	    			if (entityThatAttackedOwner != null)
	    			{
	    				return entityThatAttackedOwner;
	    			}
	    		}
	    	}
        }
	    return null;
    }

    @Override
    public boolean entitiesToIgnoreWhenLookingForAnEntityToAttack(Entity entity)
    {
        return ((super.entitiesToIgnoreWhenLookingForAnEntityToAttack(entity)) || (this.getIsTamed() && entity instanceof MoCEntityScorpion && ((MoCEntityScorpion) entity).getIsTamed()));
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
            boolean isEntityToAttackInstanceOfPlayer = (entity instanceof EntityPlayer);
            if (!getIsPoisoning() && rand.nextInt(5) == 0)
            {
                setPoisoning(true);
                if (getType() <= 2)// regular scorpions
                {
                    if (isEntityToAttackInstanceOfPlayer)
                    {
                        MoCreatures.poisonPlayer((EntityPlayer) entity);
                    }
                    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 70, 0));
                }
                else if (getType() == 4)// frost scorpions
                {
                    if (isEntityToAttackInstanceOfPlayer)
                    {
                        MoCreatures.freezePlayer((EntityPlayer) entity);
                    }
                    ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 70, 0));

                }
                else if (getType() == 3)// nether scorpions
                {
                    if (isEntityToAttackInstanceOfPlayer && MoCreatures.isServer() && !worldObj.provider.isHellWorld)
                    {
                        MoCreatures.burnPlayer((EntityPlayer) entity);
                        ((EntityLivingBase) entity).setFire(15);

                    }

                }

            }
            else
            {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
                swingArm();
            }
        }
    }

    public void swingArm()
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
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

                MoCEntityScorpion entityscorpy = new MoCEntityScorpion(worldObj);
                entityscorpy.setPosition(posX, posY, posZ);
                entityscorpy.setAdult(false);
                entityscorpy.setType(getType());
                entityscorpy.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
                worldObj.spawnEntityInWorld(entityscorpy);
                worldObj.playSoundAtEntity(this, "mob.chicken.plop", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);

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
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
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
        case 5:
            return Items.rotten_flesh;

        default:
            return null;
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }

        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        if ((itemstack != null) && getIsAdult() && !getIsRideable() && (itemstack.getItem() == Items.saddle || itemstack.getItem() == MoCreatures.horsesaddle))
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            setRideable(true);
            return true;
        }

        if ((itemstack != null) && this.getIsTamed() && itemstack.getItem() == MoCreatures.essenceundead)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            else
            {
                entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
            setType(5);
            return true;
        }

        if ((itemstack != null) && this.getIsTamed() && itemstack.getItem() == MoCreatures.essencedarkness)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            else
            {
                entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
            this.setHealth(getMaxHealth());
            if (MoCreatures.isServer() && this.getIsAdult())
            {
                int eggType = getType() + 40;
                MoCEntityEgg entityEgg = new MoCEntityEgg(worldObj, eggType);
                entityEgg.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
                entityPlayer.worldObj.spawnEntityInWorld(entityEgg);
                entityEgg.motionY += worldObj.rand.nextFloat() * 0.05F;
                entityEgg.motionX += (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.3F;
                entityEgg.motionZ += (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.3F;
            }
            return true;
        }
        if (itemstack == null && this.ridingEntity == null && getMoCAge() < 60)
        {
            rotationYaw = entityPlayer.rotationYaw;
            if (MoCreatures.isServer() && (entityPlayer.ridingEntity == null))
            {
                mountEntity(entityPlayer);
                setPicked(true);
            }

            if (MoCreatures.isServer() && !getIsTamed())
            {
                MoCTools.tameWithName(entityPlayer, this);
                entityPlayer.addStat(MoCAchievements.tame_scorpion, 1);
            }
        }
        else if (itemstack == null && this.ridingEntity != null && getIsPicked())
        {
            setPicked(false);
            if (MoCreatures.isServer())
            {
                this.mountEntity(null);
            }
            motionX = entityPlayer.motionX * 5D;
            motionY = (entityPlayer.motionY / 2D) + 0.5D;
            motionZ = entityPlayer.motionZ * 5D;
        }

        if ((itemstack == null) && getIsRideable() && getIsTamed() && getIsAdult() && (riddenByEntity == null))
        {
            entityPlayer.rotationYaw = rotationYaw;
            entityPlayer.rotationPitch = rotationPitch;
            setEating(false);
            if (MoCreatures.isServer())
            {
                entityPlayer.mountEntity(this);
            }

            return true;
        }

        return false;
    }

    @Override
    public double getYOffset()
    {
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer()) { return (yOffset - 1.7F); }

        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            return (yOffset - 0.1F);
        }
        else
        {
            return yOffset;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setHasBabies(nbtTagCompound.getBoolean("Babies"));
        setRideable(nbtTagCompound.getBoolean("Saddled"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Babies", getHasBabies());
        nbtTagCompound.setBoolean("Saddled", getIsRideable());
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
    public int nameYOffset()
    {
        int yOffsetName = (int) (1 - (getMoCAge() * 0.8));
        if (yOffsetName < -70)
        {
            yOffsetName = -70;
        }

        return yOffsetName;
    }

    @Override
    public double roperYOffset()
    {
        double yOffsetRoper = (double) ((150 - getMoCAge()) * 0.012D);
        if (yOffsetRoper < 0.55D)
        {
            yOffsetRoper = 0.55D;
        }
        if (yOffsetRoper > 1.2D)
        {
            yOffsetRoper = 1.2D;
        }
        return yOffsetRoper;
    }

    @Override
    protected boolean isMyHealFood(ItemStack itemstack)
    {
        return
        		(
        			itemstack.getItem() == Items.rotten_flesh
        			|| itemstack.getItem() == MoCreatures.ratRaw
        			|| itemstack.getItem() == MoCreatures.ratCooked
        			|| MoCreatures.isGregTech6Loaded &&
            			(
            				OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodScrapmeat"
            			)
        		);
    }

    @Override
    public int getTalkInterval()
    {
        return 300;
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null) && (ridingEntity == null);
    }

    @Override
    public boolean canBeCollidedWith()
    {

        return riddenByEntity == null;
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
    }

    @Override
    protected boolean isMovementCeased()
    {
        return (riddenByEntity != null);
    }
    
    @Override
    public void dropMyStuff() 
    {
        MoCTools.dropSaddle(this, worldObj);
    }
    
    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    @Override
    public float getAdjustedYOffset()
    {
        return 0.2F;
    }
}