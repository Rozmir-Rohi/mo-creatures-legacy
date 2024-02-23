package drzhark.mocreatures.entity;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class MoCEntityAquatic extends EntityWaterMob implements IMoCEntity//, IEntityAdditionalSpawnData
{
    protected boolean divePending;
    protected boolean jumpPending;
    protected boolean isEntityJumping;
    private PathEntity pathEntity;
    private int outOfWater;
    private int maxHealth;
    private boolean diving;
    private int divingCount;
    private int mountCount;
    public EntityLiving roper;
    public boolean isCaughtOnHook;
    protected boolean riderIsDisconnecting;
    protected float moveSpeed;
    protected String texture;
    private boolean hasKilledPrey = false;

    public MoCEntityAquatic(World world)
    {
        super(world);
        outOfWater = 0;
        setTamed(false);
        setTemper(50);
        riderIsDisconnecting = false;
        texture = "blank.jpg";
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }

    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture(texture);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData entityLivingData)
    {
        selectType();
        return super.onSpawnWithEgg(entityLivingData);
    }

    /**
     * Put your code to choose a texture / the mob type in here. Will be called
     * by default MocEntity constructors.
     */
    @Override
    public void selectType()
    {
        setType(1);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(15, Integer.valueOf(50)); // int temper
        dataWatcher.addObject(16, Byte.valueOf((byte) 0)); // byte IsTamed, 0 = false 1 = true
        dataWatcher.addObject(17, String.valueOf("")); // Name empty string by default
        dataWatcher.addObject(18, Byte.valueOf((byte) 0)); // byte IsAdult, 0 = false 1 = true
        dataWatcher.addObject(19, Integer.valueOf(0)); // int ageTicks
        dataWatcher.addObject(20, Integer.valueOf(0)); // integer type - will be automatically checked and networked in onUpdate-EntityLiving
        dataWatcher.addObject(21, String.valueOf("")); //owners name
    }

    public int getTemper()
    {
        return dataWatcher.getWatchableObjectInt(15);
    }

    @Override
    public boolean getIsTamed()
    {
        return (dataWatcher.getWatchableObjectByte(16) == 1);
    }

    @Override
    public String getName()
    {
        return dataWatcher.getWatchableObjectString(17);
    }

    @Override
    public boolean getIsAdult()
    {
        return (dataWatcher.getWatchableObjectByte(18) == 1);
    }

    /**
     * @return networked Entity "Age" in integer value, typical values are
     *         0-100.
     */
    public int getMoCAge()
    {
        return dataWatcher.getWatchableObjectInt(19);
    }

    @Override
    public int getType()
    {
        return dataWatcher.getWatchableObjectInt(20);
    }

    public void setTemper(int i)
    {
        dataWatcher.updateObject(15, Integer.valueOf(i));
    }

    @Override
    public void setTamed(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(16, Byte.valueOf(input));
    }

    @Override
    public void setName(String name)
    {
        dataWatcher.updateObject(17, String.valueOf(name));
    }

    @Override
    public void setAdult(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(18, Byte.valueOf(input));
    }

    public void setMoCAge(int i)
    {
        dataWatcher.updateObject(19, Integer.valueOf(i));
    }

    public void setType(int i)
    {
        dataWatcher.updateObject(20, Integer.valueOf(i));
    }

    /**
     * How difficult is the creature to be tamed? the Higher the number, the
     * more difficult
     */
    public int getMaxTemper()
    {
        return 100;
    }

    public float updateRotation(float currentRotation, float intendedRotation, float maxIncrement)
    {
        float amountToChangeRotationBy = intendedRotation;
        for (amountToChangeRotationBy = intendedRotation - currentRotation; amountToChangeRotationBy < -180F; amountToChangeRotationBy += 360F)
        {
        }
        for (; amountToChangeRotationBy >= 180F; amountToChangeRotationBy -= 360F)
        {
        }
        if (amountToChangeRotationBy > maxIncrement)
        {
            amountToChangeRotationBy = maxIncrement;
        }
        if (amountToChangeRotationBy < -maxIncrement)
        {
            amountToChangeRotationBy = -maxIncrement;
        }
        return currentRotation + amountToChangeRotationBy;
    }

    public void faceItem(int xCoordinate, int yCoordinate, int zCoordinate, float f)
    {
        double xDistance = xCoordinate - posX;
        double yDistance = yCoordinate - posY;
        double zDistance = zCoordinate - posZ;
        
        double overallDistanceSquared = MathHelper.sqrt_double((xDistance * xDistance) + (zDistance * zDistance));
        
        float xzAngleInDegreesToNewLocation = (float) ((Math.atan2(zDistance, xDistance) * 180D) / Math.PI) - 90F;
        float yAngleInDegreesToNewLocation = (float) ((Math.atan2(yDistance, overallDistanceSquared) * 180D) / Math.PI);
        
        rotationPitch = -updateRotation(rotationPitch, yAngleInDegreesToNewLocation, f);
        rotationYaw = updateRotation(rotationYaw, xzAngleInDegreesToNewLocation, f);
    }

    @Override
    protected boolean canDespawn()
    {
    	return !getIsTamed();
    }

    @Override
    public boolean checkSpawningBiome()
    {
        return true;
    }

    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block par4)
    {
        // TODO make the sounds
    }

    @Override
    protected void fall(float f)
    {
    }
    
    public boolean isPredator()
    {
    	return false;
    }
    
    public void onKillEntity(EntityLivingBase entityLiving)
    {
    	if (isPredator() && MoCreatures.proxy.destroyDrops)
    	{
    		if (!(entityLiving instanceof EntityPlayer) && !(entityLiving instanceof EntityMob))
    		{
	    		hasKilledPrey = true;
    		}
    	}
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return null;
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    public boolean gettingOutOfWater()
    {
        int x = (int) posX;
        int y = (int) posY;
        int z = (int) posZ;
        
        return worldObj.isAirBlock(x, y + 1, z);
    }

    protected String getUpsetSound()
    {
        return null;
    }

    /**
     * mount jumping power
     */
    public double getCustomJump()
    {
        return 0.4D;
    }

    public void setIsJumping(boolean flag)
    {
        isEntityJumping = flag;
    }

    public boolean getIsJumping()
    {
        return isEntityJumping;
    }

    public boolean getDisplayName()
    {
        return (getName() != null && !getName().equals(""));
    }

    /**
     * Sets a flag that will make the Entity "jump" in the next onGround
     * moveEntity update
     */
    @Override
    public void makeEntityJump()
    {
        jumpPending = true;
    }

    @Override
    public boolean handleWaterMovement()
    {
        return worldObj.handleMaterialAcceleration(boundingBox, Material.water, this);
    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (riddenByEntity == null)
        {
            super.moveEntityWithHeading(strafe, forward);
        }
        float movementSideways = strafe;
        float movementForward = forward;

        if ((riddenByEntity != null) && !getIsTamed() && !isSwimming())
        {
            riddenByEntity.mountEntity(null);
            return;
        }

        if ((riddenByEntity != null) && !getIsTamed())
        {
            if ((rand.nextInt(5) == 0) && !getIsJumping() && jumpPending)
            {
                motionY += getCustomJump();
                setIsJumping(true);
                jumpPending = false;
            }
            if (rand.nextInt(10) == 0)
            {
                motionX += rand.nextDouble() / 30D;
                motionZ += rand.nextDouble() / 10D;
            }
            if (MoCreatures.isServer())
            {
                moveEntity(motionX, motionY, motionZ);
            }
            if (MoCreatures.isServer() && rand.nextInt(50) == 0)
            {
                if (getUpsetSound() != null){
                    worldObj.playSoundAtEntity(this, getUpsetSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                }
                riddenByEntity.motionY += 0.9D;
                riddenByEntity.motionZ -= 0.3D;
                riddenByEntity.mountEntity(null);
                ridingEntity = null;
            }
            if (onGround)
            {
                setIsJumping(false);
            }
            if (MoCreatures.isServer() && this instanceof IMoCTameable)
            {
                int tameChance = (getMaxTemper() - getTemper());
                if (tameChance <= 0)
                {
                    tameChance = 1;
                }
                if (rand.nextInt(tameChance * 8) == 0)
                {
                    MoCTools.tameWithName((EntityPlayer) riddenByEntity, (IMoCTameable) this);
                }

            }
        }
        else if ((riddenByEntity != null) && getIsTamed())
        {
            motionX += riddenByEntity.motionX * (getCustomSpeed() / 5.0D);
            motionZ += riddenByEntity.motionZ * (getCustomSpeed() / 5.0D);
            movementSideways = ((EntityLivingBase)riddenByEntity).moveStrafing * 0.5F;
            movementForward = ((EntityLivingBase)riddenByEntity).moveForward;

            if (jumpPending)
            {
                motionY += getCustomJump();
                jumpPending = false;
            }

            if (divePending)
            {
                divePending = false;
                motionY -= 0.3D;
            }

            if (motionY > 0.01D && !isSwimming())
            {
                motionY = -0.01D;
            }
            rotationPitch = riddenByEntity.rotationPitch * 0.5F;
            prevRotationYaw = rotationYaw = riddenByEntity.rotationYaw;
            setRotation(rotationYaw, rotationPitch);

            if (MoCreatures.isServer())
            {
                super.moveEntityWithHeading(movementSideways, movementForward);
            }
        }

        prevLimbSwingAmount = limbSwingAmount;
        double xDistanceTravelled = posX - prevPosX;
        double zDistanceTravelled = posZ - prevPosZ;
        float overallHorizontalDistanceTravelledSquared = MathHelper.sqrt_double((xDistanceTravelled * xDistanceTravelled) + (zDistanceTravelled * zDistanceTravelled)) * 4.0F;
        if (overallHorizontalDistanceTravelledSquared > 1.0F)
        {
            overallHorizontalDistanceTravelledSquared = 1.0F;
        }

        limbSwingAmount += (overallHorizontalDistanceTravelledSquared - limbSwingAmount) * 0.4F;
        limbSwing += limbSwingAmount;
    }

    protected boolean MoveToNextEntity(Entity entity)
    {
        if (entity != null)
        {
            int entityPosX = MathHelper.floor_double(entity.posX);
            int entityPosY = MathHelper.floor_double(entity.posY);
            int entityPosZ = MathHelper.floor_double(entity.posZ);
            faceItem(entityPosX, entityPosY, entityPosZ, 30F);
            if (posX < entityPosX)
            {
                double distance = entity.posX - posX;
                if (distance > 0.5D)
                {
                    motionX += 0.050000000000000003D;
                }
            }
            else
            {
                double currentMinimumDistance = posX - entity.posX;
                if (currentMinimumDistance > 0.5D)
                {
                    motionX -= 0.050000000000000003D;
                }
            }
            if (posZ < entityPosZ)
            {
                double distance2 = entity.posZ - posZ;
                if (distance2 > 0.5D)
                {
                    motionZ += 0.050000000000000003D;
                }
            }
            else
            {
                double distance3 = posZ - entity.posZ;
                if (distance3 > 0.5D)
                {
                    motionZ -= 0.050000000000000003D;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Speed used to move the mob around
     * 
     * @return
     */
    public double getCustomSpeed()
    {
        return 1.5D;
    }

    @Override
    public boolean isInWater()
    {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    public boolean isDiving()
    {
        return diving;
    }

    @Override
    protected void jump()
    {

    }

    public void moveVerticallyInWater()
    {
        float yDistanceToSurfaceOfWater = MoCTools.distanceToSurface(this);

        if (riddenByEntity != null)
        {
            EntityPlayer playerThatIsRidingThisCreature = (EntityPlayer) riddenByEntity;
            if (playerThatIsRidingThisCreature.isAirBorne) // TODO TEST
            {
                motionY += 0.09D;
            }
            else if (divePending)
            {
            	motionY = -0.008D;
            }
            else
            {
                motionY = 0.0D;
            }
            return;
        }

        if ((entityToAttack != null && ((entityToAttack.posY < (posY - 0.5D)) && getDistanceToEntity(entityToAttack) < 10F)))
        {
            if (motionY < -0.1)
            {
                motionY = -0.1;
            }
            return;
        }

        if (yDistanceToSurfaceOfWater < 1 || isDiving())
        {
            if (motionY < -0.05)
            {
                motionY = -0.05;
            }
        }
        else
        {
        	motionY += 0.001D;
        	
            if (motionY < 0)
            {
                motionY = 0;
            }
        }
        
        if (yDistanceToSurfaceOfWater > 20 && rand.nextInt(50) == 0) //move up in water
        {
	    	while (motionY < 0.2)
	    	{
	    		motionY += 0.05; //TODO: make fish moving up smoother -> use moveWithHeading?
	    	}
        }
    }

    // used to pick up objects while riding an entity
    public void Riding()
    {
        if ((riddenByEntity != null) && (riddenByEntity instanceof EntityPlayer))
        {
            EntityPlayer entityPlayer = (EntityPlayer) riddenByEntity;
            List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1.0D, 0.0D, 1.0D));
            
            int iterationLength = entitiesNearbyList.size();
            
            if (iterationLength > 0)
            {
                for (int index = 0; index < iterationLength; index++)
                {
                    Entity entityNearby = (Entity) entitiesNearbyList.get(index);
                    if (entityNearby.isDead)
                    {
                        continue;
                    }
                    
                    entityNearby.onCollideWithPlayer(entityPlayer);
                    
                    if (!(entityNearby instanceof EntityMob))
                    {
                        continue;
                    }
                    
                    float distance = getDistanceToEntity(entityNearby);
                    
                    if ((distance < 2.0F) && entityNearby instanceof EntityMob && (rand.nextInt(10) == 0))
                    {
                        attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entityNearby), (float)((EntityMob)entityNearby).getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                    }
                }
            }
        }
    }

    @Override
    protected boolean isMovementCeased()
    {
        return ((!isSwimming() && riddenByEntity == null) || riddenByEntity != null);
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer())
        {
            if (riddenByEntity != null)
            {
                Riding();
                mountCount = 1;
            }

            if (mountCount > 0)
            {
                if (++mountCount > 50)
                {
                    mountCount = 0;
                }
            }
            
            if (isPredator() && hasKilledPrey) 
            {
            	if (MoCreatures.proxy.destroyDrops) //destroy the drops of the prey
            	{
	            	List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(3, 3, 3));
	
	            	int iterationLength = entitiesNearbyList.size();
	            	
	            	if (iterationLength > 0)
	            	{
		                for (int index = 0; index < iterationLength; index++)
		                {
		                    Entity entityNearby = (Entity) entitiesNearbyList.get(index);
		                    if (!(entityNearby instanceof EntityItem))
		                    {
		                        continue;
		                    }
		                    
		                    EntityItem entityItemNearby = (EntityItem) entityNearby;
		                    
		                    if ((entityItemNearby != null) && (entityItemNearby.age < 5)) //targeting entityItem with age below 5 makes sure that the predator only eats the items that are dropped from the prey
		                    {
		                        entityItemNearby.setDead();
		                    }
		                }
	            	}
            	}
                
            	heal(5);
	    		MoCTools.playCustomSound(this, "eating", worldObj);
	    		
                hasKilledPrey = false;
            }

            if (forceUpdates() && rand.nextInt(500) == 0)
            {
                MoCTools.forceDataSync(this);
            }
            
            if (isFisheable() && !isCaughtOnHook && rand.nextInt(30) == 0) //makes a fish look for a fishing hook to willingly bite
            {
                lookForHookToGetCaughtOn();
            }
            
            if (isCaughtOnHook && hookThatThisFishIsHookedTo != null)
            {
            	boolean isHookNearby = true;
            	
            	float distanceToHook1 = hookThatThisFishIsHookedTo.getDistanceToEntity(this);
                
            	if (distanceToHook1 > 2) //tests if the fish has been reeled in by the player
                {
            		isHookNearby = false;
                }
                
                if (!(isHookNearby) && playerThatHookedThisFish != null && hookThatThisFishIsHookedTo != null)
                {
                	if (playerThatHookedThisFish.inventory.getCurrentItem() != null)  //must check if itemstack isn't null before getItem() else game will crash
                	{
	                	if (playerThatHookedThisFish.inventory.getCurrentItem().getItem() == Items.fishing_rod)
	                	{
		                	
		                	ItemStack itemstackToBeFished = new ItemStack(Items.fish, 1, 0);
		                	
		                	
		                	EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, itemstackToBeFished);
		                    double xDistance = playerThatHookedThisFish.posX - hookThatThisFishIsHookedTo.posX;
		                    double yDistance = playerThatHookedThisFish.posY - hookThatThisFishIsHookedTo.posY;
		                    double zDistance = playerThatHookedThisFish.posZ - hookThatThisFishIsHookedTo.posZ;
		                    
		                    setDead();
		                    
		                    double overallDistanceSquared = (double) MathHelper.sqrt_double(xDistance * xDistance + yDistance * yDistance + zDistance * zDistance);
		                    double distanceOffset = 0.1D;
		                    
		                    entityItem.motionX = xDistance * distanceOffset;
		                    entityItem.motionY = yDistance * distanceOffset + (double )MathHelper.sqrt_double(overallDistanceSquared) * 0.08D;
		                    entityItem.motionZ = zDistance * distanceOffset;
		                    
		                    worldObj.spawnEntityInWorld(entityItem);
		                    
		                    playerThatHookedThisFish.worldObj.spawnEntityInWorld(new EntityXPOrb(playerThatHookedThisFish.worldObj, playerThatHookedThisFish.posX, playerThatHookedThisFish.posY + 0.5D, playerThatHookedThisFish.posZ + 0.5D, rand.nextInt(6) + 1));
		                	
	                	}
                	}
                	
                	else
                	{
                		isCaughtOnHook = false;
                	}
                }
            }
            

            if (isCaughtOnHook && rand.nextInt(200) == 0) // unhooks the fish from the fishing hook if the fish is hooked for too long
            {
                isCaughtOnHook = false;
                
                List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(2, 2, 2));
                
                int iterationLength = entitiesNearbyList.size();
                
                if (iterationLength > 0)
                {
                	for (int index = 0; index < iterationLength; index++)
                    {
                        Entity entityNearby = (Entity) entitiesNearbyList.get(index);
            
                        if (entityNearby instanceof EntityFishHook)
                        {
                            if (((EntityFishHook) entityNearby).field_146043_c == this)
                            {
                                ((EntityFishHook) entityNearby).field_146043_c = null;
                            }
                        }
                    }
                }
            }
        }

        if (isNotScared() && fleeingTick > 0)
        {
            fleeingTick = 0;
        }

        moveSpeed = getMoveSpeed();

        if (isSwimming())
        {
            moveVerticallyInWater();
            outOfWater = 0;
            setAir(300);
        }
        else
        {
            if (riddenByEntity != null)
            {
                if (riddenByEntity.isSneaking())
                {
                    riddenByEntity.mountEntity(null);
                }
            }

            outOfWater++;
            if (outOfWater > 10)
            {
                setPathToEntity(null);
            }
            if (outOfWater > 100 && (outOfWater % 20) == 0)
            {
                motionY += 0.3D;
                motionX = (float) (Math.random() * 0.2D - 0.1D);
                motionZ = (float) (Math.random() * 0.2D - 0.1D);
                attackEntityFrom(DamageSource.drown, 1);
            }
        }

        if (!hasPath() && riddenByEntity == null && !isMovementCeased() && entityToAttack == null)
        {
            updateWanderPath();
        }

        if (!diving)
        {
            if (riddenByEntity == null && entityToAttack == null && hasPath() && rand.nextInt(500) == 0)
            {
                diving = true;
            }
        }
        else
        {
            divingCount++;
            if (divingCount > 100 || riddenByEntity != null)
            {
                diving = false;
                divingCount = 0;
            }
        }
        super.onLivingUpdate();
    }

    public boolean isSwimming()
    {
        return ((isInsideOfMaterial(Material.water)));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Tamed", getIsTamed());
        nbtTagCompound.setBoolean("Adult", getIsAdult());
        nbtTagCompound.setInteger("Age", getMoCAge());
        nbtTagCompound.setString("Name", getName());
        nbtTagCompound.setInteger("TypeInt", getType());
        nbtTagCompound.setString("Owner", getOwnerName());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setTamed(nbtTagCompound.getBoolean("Tamed"));
        setAdult(nbtTagCompound.getBoolean("Adult"));
        setMoCAge(nbtTagCompound.getInteger("Age"));
        setName(nbtTagCompound.getString("Name"));
        setType(nbtTagCompound.getInteger("TypeInt"));
        setOwner(nbtTagCompound.getString("Owner"));
    }

    /**
     * fixes dewspawning tamed creatures
     */
    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        if (getIsTamed()) { return false; }

        return super.isEntityInsideOpaqueBlock();
    }

    public void setDisplayName(boolean flag)
    {
    }

    public void setTypeInt(int i)
    {
        setType(i);
        selectType();
    }

    /**
     * Used to synchronize the attack animation between server and client
     * 
     * @param attackType
     */
    @Override
    public void performAnimation(int attackType)
    {
    }

    /**
     * Makes the entity despawn if requirements are reached changed to the
     * entities now last longer
     */
    @Override
    protected void despawnEntity()
    {
        EntityPlayer distanceToPlayer = worldObj.getClosestPlayerToEntity(this, -1.0D);
        if (distanceToPlayer != null)
        {
            double xDistance = distanceToPlayer.posX - posX;
            double yDistance = distanceToPlayer.posY - posY;
            double zDistance = distanceToPlayer.posZ - posZ;
            
            double overallDistanceSquared = xDistance * xDistance + yDistance * yDistance + zDistance * zDistance;

            if (canDespawn() && overallDistanceSquared > 16384.0D)
            {
                setDead();
            }
            //changed from 600
            if (entityAge > 1800 && rand.nextInt(800) == 0 && overallDistanceSquared > 1024.0D && canDespawn())
            {
                setDead();
            }
            else if (overallDistanceSquared < 1024.0D)
            {
                entityAge = 0;
            }
        }
    }

    public float getMoveSpeed()
    {
        return 0.7F;
    }

    @Override
    public int nameYOffset()
    {
        return 0;
    }

    @Override
    public double roperYOffset()
    {
        return 0;
    }

    @Override
    public boolean renderName()
    {
        return false;
    }

    @Override
    public Entity getRoper()
    {
        return roper;
    }

    @Override
    public boolean updateMount()
    {
        return false;
    }

    @Override
    public boolean forceUpdates()
    {
        return false;
    }

    @Override
    public void makeEntityDive()
    {
        divePending = true;
    }

    @Override
    public float getSizeFactor()
    {
        return 1.0F;
    }

    @Override
    public float getAdjustedYOffset()
    {
        return 0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    @Override
    public boolean getCanSpawnHere()
    {
        return MoCreatures.entityMap.get(getClass()).getFrequency() > 0 && worldObj.checkNoEntityCollision(boundingBox);
    }

    @Override
    public String getOwnerName()
    {
        return dataWatcher.getWatchableObjectString(21);
    }

    @Override
    public void setOwner(String par1Str)
    {
        dataWatcher.updateObject(21, par1Str);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        Entity entityThatAttackedThisCreature = damageSource.getEntity();
        //this avoids damage done by Players to a tamed creature that is not theirs
        if (MoCreatures.proxy.enableStrictOwnership && getOwnerName() != null && !getOwnerName().equals("") && entityThatAttackedThisCreature != null && entityThatAttackedThisCreature instanceof EntityPlayer && !((EntityPlayer) entityThatAttackedThisCreature).getCommandSenderName().equals(getOwnerName()) && !MoCTools.isThisPlayerAnOP(((EntityPlayer) entityThatAttackedThisCreature))) { return false; }

        if (isFisheable()) //tests if the fish has been force hooked by a player throwing a fishing hook at them
        {
	        if (entityThatAttackedThisCreature != null)
	        {
	        	if (entityThatAttackedThisCreature instanceof EntityPlayer)
	        	{
	        		if (((EntityPlayer) entityThatAttackedThisCreature).inventory.getCurrentItem() != null) //must check if itemstack isn't null before getItem() else game will crash
	        		{
		        		if (((EntityPlayer) entityThatAttackedThisCreature).inventory.getCurrentItem().getItem() == Items.fishing_rod)
		        		{
		        			lookForHookToGetCaughtOn(); //tests if there is a fishing hook nearby, if so sets the fish as caught on a hook
		        		}
	        		}
	        	}
	        }
    	}
        
        //to prevent tamed aquatics from getting block damage
        if (getIsTamed() && damageSource.getDamageType().equalsIgnoreCase("inWall"))
        {
            return false;
        }
        /*if (MoCreatures.isServer() && getIsTamed())
        {
            //MoCServerPacketHandler.sendHealth(getEntityId(), worldObj.provider.dimensionId, getHealth());
        }*/

        return super.attackEntityFrom(damageSource, damageTaken);

    }



    protected boolean canBeTrappedInNet() 
    {
        return (this instanceof IMoCTameable) && getIsTamed();
    }

    protected void dropMyStuff()
    {

    }

    /**
     * Used to heal the animal
     * 
     * @param itemstack
     * @return
     */
    protected boolean isMyHealFood(ItemStack itemstack)
    {
        return false;
    }

    @Override
    public void setArmorType(byte i) {}

    @Override
    public void dismountEntity() 
    {
        if (MoCreatures.isServer() && riddenByEntity != null)
        {
            riddenByEntity.mountEntity(null);
            riddenByEntity = null;
        }
    }

    @Override
    public int pitchRotationOffset() {
        return 0;
    }
    
    @Override
    public int rollRotationOffset() 
    {
        return 0;
    }
    
    EntityPlayer playerThatHookedThisFish;
    
    EntityFishHook hookThatThisFishIsHookedTo;
    
    /**
     * The act of getting Hooked into a fish Hook.
     */
    private void lookForHookToGetCaughtOn()
    {
        EntityPlayer closestEntityPlayer = worldObj.getClosestPlayerToEntity(this, 18D);
        
        if (closestEntityPlayer != null)
        {
            EntityFishHook fishHook = closestEntityPlayer.fishEntity;
            
            if (fishHook != null)
            {
            	if (fishHook.field_146043_c == null) //hooked by fish willingly biting the fish hook
            	{
	                float distanceToHook = fishHook.getDistanceToEntity(this);
	                
	                if (distanceToHook > 1)
	                {
	                    MoCTools.getPathToEntity(this, fishHook, distanceToHook);
	                }
	                else
	                {
	                    fishHook.field_146043_c = this;
	                    isCaughtOnHook = true;
	                    
	                    if (outOfWater == 0) //if in water
	                    {
	                    	fishHook.motionY -= 0.20000000298023224D;
		                    playSound("random.splash", 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
	                    }
	                    
	                    playerThatHookedThisFish = closestEntityPlayer;
	                    
	                    hookThatThisFishIsHookedTo = fishHook;
	                    
	                }
            	}
            }
        }
    }

    /**
     * Is this aquatic entity prone to be fished with a fish Hook?
     * @return
     */
    protected boolean isFisheable()
    {
        return false;
    }

    @Override
    public int yawRotationOffset()
    {
        return 0;
    }

    @Override
    public float getAdjustedZOffset()
    {
        return 0F;
    }

    @Override
    public float getAdjustedXOffset()
    {
        return 0F;
    }

    /**
     * Finds and entity described in entitiesThatAreScary at d distance
     * 
     * @param d
     * @return
     */
    protected EntityLivingBase getScaryEntity(double d)
    {
        double currentMinimumDistance = -1D;
        
        EntityLivingBase entityLiving = null;
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, 4D, d));
        
        int iterationLength = entitiesNearbyList.size();
        
        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	            
	            if (entitiesThatAreScary(entityNearby))
	            {
	                entityLiving = (EntityLivingBase) entityNearby;
	            }
	        }
        }
        return entityLiving;
    }

    /**
     * Used in getScaryEntity to specify what kind of entity to look for
     * 
     * @param entity
     * @return
     */
    public boolean entitiesThatAreScary(Entity entity)
    {
        return ( (entity.getClass() != getClass()) && (entity instanceof EntityLivingBase) && ((entity.width >= 0.5D) || (entity.height >= 0.5D)));
    }

    public boolean isNotScared()
    {
        return false;
    }

    @Override
    public void riderIsDisconnecting(boolean flag)
    {
        riderIsDisconnecting = true;
    }
}