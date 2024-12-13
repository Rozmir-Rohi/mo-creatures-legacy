package drzhark.mocreatures.entity;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageHealth;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;


public abstract class MoCEntityAmbient extends EntityAnimal  implements IMoCEntity
{
    protected float moveSpeed;
    protected boolean riderIsDisconnecting;
    protected String texture;

    public MoCEntityAmbient(World world)
    {
        super(world);
        setTamed(false);
        setAdult(true);
        riderIsDisconnecting = false;
        texture = "blank.png";
    }

    @Override
	public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture(texture);
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(getMaxHealth());
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
    public EntityAgeable createChild(EntityAgeable var1)
    {
        return null;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(15, Byte.valueOf((byte) 0)); // isAdult - 0 false 1 true
        dataWatcher.addObject(16, Byte.valueOf((byte) 0)); // isTamed - 0 false 1 true
        dataWatcher.addObject(17, String.valueOf("")); // displayName empty string by default
        dataWatcher.addObject(18, Integer.valueOf(0)); // int ageTicks
        dataWatcher.addObject(19, Integer.valueOf(0)); // int type
        dataWatcher.addObject(20, String.valueOf("")); //owners name
        
    }

    @Override
	public void setType(int i)
    {
        dataWatcher.updateObject(19, Integer.valueOf(i));
    }

    @Override
    public int getType()
    {
        return dataWatcher.getWatchableObjectInt(19);
    }

    public void setDisplayName(boolean flag)
    {

    }

    @Override
    public boolean getIsAdult()
    {
        return (dataWatcher.getWatchableObjectByte(15) == 1);
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

    /**
     * @return networked Entity "Age" in integer value, typical values are
     *         0-100.
     */
    @Override
	public int getMoCAge()
    {
        return dataWatcher.getWatchableObjectInt(18);
    }

    @Override
	public void setMoCAge(int i)
    {
        dataWatcher.updateObject(18, Integer.valueOf(i));
    }

    @Override
    public void setAdult(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(15, Byte.valueOf(input));
    }

    @Override
    public void setName(String name)
    {
        dataWatcher.updateObject(17, String.valueOf(name));
    }

    @Override
    public void setTamed(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(16, Byte.valueOf(input));

    }

    @Override
    protected boolean canDespawn()
    {
    	return true;
    }

    /**
     * called in getCanSpawnHere to make sure the right type of creature spawns
     * in the right biome i.e. snakes, rays, bears, BigCats and later wolves,
     * etc.
     */
    @Override
    public boolean checkSpawningBiome()
    {
        return true;
    }

    @Override
    public void onLivingUpdate()
    {
        if (isSwimming() && isSwimmerEntity())
        {
            floatOnWater();
        }

        moveSpeed = getMoveSpeed();
        super.onLivingUpdate();
    }

    public boolean isSwimmerEntity()
    {
        return false;
    }

    public boolean isSwimming()
    {
        return ((isInsideOfMaterial(Material.water)));
    }

    public void floatOnWater()
    {
        if (motionY < 0)
        {
            motionY = 0;
        }
        motionY += 0.001D;// 0.001

        int yDistanceToSurfaceOfWater = (int) MoCTools.distanceToSurface(this);
        
        if (yDistanceToSurfaceOfWater > 1)
        {
            motionY += (yDistanceToSurfaceOfWater * 0.07);
        }

        if (hasPath() && isCollidedHorizontally)
        {
            jump();
        }
    }

    /**
     * List of edible foods
     * 
     * @param item
     * @return
     */
    public boolean isItemEdible(Item item)
    {
        return (
	        		item instanceof ItemFood
	        		|| item instanceof ItemSeeds
	        		|| item == Items.wheat
	        		|| item == Items.sugar
	        		|| item == Items.cake
	        		|| item == Items.egg
        		);
    }

    /**
     * Used to breed
     * 
     * @param item1
     * @return
     */
    public boolean isMyAphrodisiac(Item item1)
    {
        return false;
    }

    //used to drop armor, inventory, saddles, etc.
    public void dropMyStuff() {}

    /**
     * Used to heal the animal
     * 
     * @param itemStack
     * @return
     */
    protected boolean isMyHealFood(ItemStack itemStack)
    {
        return false;
    }

    @Override
    public boolean isInWater()
    {
        if (isSwimmerEntity()) { return false; }
        return super.isInWater();
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return isSwimmerEntity();
    }

    public EntityItem getClosestItem(Entity entity, double distance, ItemStack item, ItemStack item1)
    {
        double currentMinimumDistance = -1D;
        EntityItem entityItem = null;
        
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
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
	            
	            if ((entityItemNearby.getEntityItem() != item) && (entityItemNearby.getEntityItem() != item1))
	            {
	                continue;
	            }
	            
	            double overallDistanceSquared = entityItemNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityItem = entityItemNearby;
	            }
	        }
        }

        return entityItem;
    }

    public EntityItem getClosestEntityItem(Entity entity, double distance)
    {
        double currentMinimumDistance = -1D;
        EntityItem entityItem = null;
        
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
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
	            
	            double overallDistanceSquared = entityItemNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityItem = entityItemNearby;
	            }
	        }
        }

        return entityItem;
    }

    public EntityItem getClosestFood(Entity entity, double distance)
    {
        double currentMinimumDistance = -1D;
        EntityItem entityItem = null;
        
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
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
	            
	            if (!isItemEdible(entityItemNearby.getEntityItem().getItem()))
	            {
	                continue;
	            }
	            
	            double overallDistanceSquared = entityItemNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityItem = entityItemNearby;
	            }
	        }
        }

        return entityItem;
    }

    public void faceLocation(int x, int y, int z, float f)
    {
        double xDistanceToNewFacingLocation = x + 0.5D - posX;
        double yDistanceToNewFacingLocation = y + 0.5D - posY;
        double zDistanceToNewFacingLocation = z + 0.5D - posZ;
        
        double overallDistanceToNewFacingLocationSquared = MathHelper.sqrt_double(xDistanceToNewFacingLocation * xDistanceToNewFacingLocation + zDistanceToNewFacingLocation * zDistanceToNewFacingLocation);
        
        float xzAngleInDegreesToNewFacingLocation = (float) (Math.atan2(zDistanceToNewFacingLocation, xDistanceToNewFacingLocation) * 180.0D / Math.PI) - 90.0F;
        float yAngleInDegreesToNewFacingLocation = (float) (-(Math.atan2(yDistanceToNewFacingLocation, overallDistanceToNewFacingLocationSquared) * 180.0D / Math.PI));
        
        rotationPitch = -updateRotation(rotationPitch, yAngleInDegreesToNewFacingLocation, f);
        rotationYaw = updateRotation(rotationYaw, xzAngleInDegreesToNewFacingLocation, f);
    }

    /**
     * 
     * @param currentRotation
     * @param intendedRotation
     * @param maxIncrement
     * @return
     */
    private float updateRotation(float currentRotation, float intendedRotation, float maxIncrement)
    {
        float amountToChangeRotationBy;

        for (amountToChangeRotationBy = intendedRotation - currentRotation; amountToChangeRotationBy < -180.0F; amountToChangeRotationBy += 360.0F)
        {
            ;
        }

        while (amountToChangeRotationBy >= 180.0F)
        {
            amountToChangeRotationBy -= 360.0F;
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

    public void getMyOwnPath(Entity entity, float f)
    {
        PathEntity pathEntity = worldObj.getPathEntityToEntity(this, entity, 16F, true, false, false, true);
        if (pathEntity != null)
        {
            setPathToEntity(pathEntity);
        }
    }

    /**
     * Called to make ridden entities pass on collision to rider
     */
    public void Riding()
    {
        if ((riddenByEntity != null) && (riddenByEntity instanceof EntityPlayer))
        {
            EntityPlayer entityPlayer = (EntityPlayer) riddenByEntity;
            List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1.0D, 0.0D, 1.0D));
            
            int interationLength = entitiesNearbyList.size();
            
            if (interationLength > 0)
            {
	            if (entitiesNearbyList != null)
	            {
	                for (int index = 0; index < interationLength; index++)
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
	                    
	                    if ((distance < 2.0F) && (rand.nextInt(10) == 0))
	                    {
	                        //TODO 4FIX
	                        //attackEntityFrom(DamageSource.causeMobDamage((EntityLiving) entity),((EntityMob) entity).attackStrength);
	                    }
	                }
	            }
            }
            if (entityPlayer.isSneaking())
            {
                if (!worldObj.isRemote)
                {
                    entityPlayer.mountEntity(null);
                }
            }
        }
    }

    protected void getPathOrWalkableBlock(Entity entity, float f)
    {
        PathEntity pathEntity = worldObj.getPathEntityToEntity(this, entity, 16F, true, false, false, true);
        
        if ((pathEntity == null) && (f > 8F))
        {
            int x = MathHelper.floor_double(entity.posX) - 2;
            int y = MathHelper.floor_double(entity.posZ) - 2;
            int z = MathHelper.floor_double(entity.boundingBox.minY);
            
            for (int index = 0; index <= 4; index++)
            {
                for (int index1 = 0; index1 <= 4; index1++)
                {
                    if (((index < 1) || (index1 < 1) || (index > 3) || (index1 > 3)) && worldObj.getBlock(x + index, z - 1, y + index1).isNormalCube() && !worldObj.getBlock(x + index, z, y + index1).isNormalCube() && !worldObj.getBlock(x + index, z + 1, y + index1).isNormalCube())
                    {
                        setLocationAndAngles((x + index) + 0.5F, z, (y + index1) + 0.5F, rotationYaw, rotationPitch);
                        return;
                    }
                }
            }
        }
        else
        {
            setPathToEntity(pathEntity);
        }
    }

    public boolean getCanSpawnHereAnimal()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        
        return worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate) == Blocks.grass && worldObj.getFullBlockLightValue(xCoordinate, yCoordinate, zCoordinate) > 8;
    }

    public boolean getCanSpawnHereCreature()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);
        
        return getBlockPathWeight(xCoordinate, yCoordinate, zCoordinate) >= 0.0F;
    }

    public boolean getCanSpawnHereLiving()
    {
        return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).size() == 0 && !worldObj.isAnyLiquid(boundingBox);
    }

    public boolean getCanSpawnHereAquatic()
    {
        return worldObj.checkNoEntityCollision(boundingBox);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        if (MoCreatures.entityMap.get(getClass()).getFrequency() <= 0) {return false;}
        
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        String biomeName = MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (biomeName.equals("Jungle") || biomeName.equals("JungleHills")) { return getCanSpawnHereJungle(); }

        return super.getCanSpawnHere();
    }

    public boolean getCanSpawnHereJungle()
    {
        if (worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox))
        {
            int xCoordinate = MathHelper.floor_double(posX);
            int yCoordinate = MathHelper.floor_double(boundingBox.minY);
            int zCoordinate = MathHelper.floor_double(posZ);

            if (yCoordinate < 63) { return false; }

            Block block = worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate);

            if (
            		block == Blocks.grass
            		|| block == Blocks.leaves
            		|| (block != null && block.isLeaves(worldObj, xCoordinate, yCoordinate - 1, zCoordinate))
            	) 
            {
            	return true;
            }
        }

        return false;
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

    @Override
    public void moveEntityWithHeading(float strafeMovement, float forwardMovement)
    {
        //If the entity is not ridden by entityPlayer, then execute the normal Entityliving code
        if (!isFlyer() && (!rideableEntity() || riddenByEntity == null))
        {
            super.moveEntityWithHeading(strafeMovement, forwardMovement);
            return;
        }

        if (handleWaterMovement())
        {
            if (riddenByEntity != null)
            {
                motionX += riddenByEntity.motionX * (getCustomSpeed() / 2.0D);
                motionZ += riddenByEntity.motionZ * (getCustomSpeed() / 2.0D);
                
                if (!worldObj.isRemote)
                {
                    moveEntity(motionX, motionY, motionZ);
                }

                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                if (rand.nextInt(20) == 0)
                {
                    rotationYaw = riddenByEntity.rotationYaw;
                }
                setRotation(rotationYaw, rotationPitch);

                if (MoCreatures.isServer() && !getIsTamed())
                {
                    playSound("mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.3D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    riddenByEntity = null;
                }
            }
            double yCoordinate = posY;
            if (!worldObj.isRemote)
            {
                moveFlying(strafeMovement, forwardMovement, 0.02F);
                moveEntity(motionX, motionY, motionZ);
            }
            motionX *= 0.800000011920929D;
            motionY *= 0.800000011920929D;
            motionZ *= 0.800000011920929D;
            motionY -= 0.02D;
            if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, ((motionY + 0.60000002384185791D) - posY) + yCoordinate, motionZ))
            {
                motionY = 0.30000001192092901D;
            }
        }
        else if (handleLavaMovement())
        {
            if (riddenByEntity != null)
            {
                motionX += riddenByEntity.motionX * (getCustomSpeed() / 2.0D);
                motionZ += riddenByEntity.motionZ * (getCustomSpeed() / 2.0D);
                
                moveEntity(motionX, motionY, motionZ);

                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                if (rand.nextInt(20) == 0)
                {
                    rotationYaw = riddenByEntity.rotationYaw;
                }
                setRotation(rotationYaw, rotationPitch);
                if (MoCreatures.isServer() && !getIsTamed())
                {
                    playSound("mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.3D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    riddenByEntity = null;
                }
            }
            double yCoordinate1 = posY;

            moveFlying(strafeMovement, forwardMovement, 0.02F);
            moveEntity(motionX, motionY, motionZ);

            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
            motionY -= 0.02D;
            if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, ((motionY + 0.60000002384185791D) - posY) + yCoordinate1, motionZ))
            {
                motionY = 0.30000001192092901D;
            }
        }
        else
        {
            float movement = 0.91F;
            if (onGround)
            {
                movement = 0.5460001F;
                Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
                if (block != Blocks.air)
                {
                    movement = block.slipperiness * 0.91F;
                }
            }

            float f3 = 0.162771F / (movement * movement * movement);
            
            moveFlying(strafeMovement, forwardMovement, onGround ? 0.1F * f3 : 0.02F);

            if (isOnLadder())
            {
                fallDistance = 0.0F;
                if (motionY < -0.15D)
                {
                    motionY = -0.15D;
                }
            }
            if ((riddenByEntity != null) && !getIsTamed())
            {
                
                if (rand.nextInt(10) == 0)
                {
                    motionX += rand.nextDouble() / 30D;
                    motionZ += rand.nextDouble() / 10D;
                }
                // blood - This must be run on server side only since it causes glitch/twitch if run on both sides.
                if (!worldObj.isRemote)
                {
                    moveEntity(motionX, motionY, motionZ);
                }
                if (MoCreatures.isServer() && rand.nextInt(50) == 0)
                {
                    playSound("mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.9D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    riddenByEntity = null;
                }
            }
            if ((riddenByEntity != null) && getIsTamed())
            {
                boundingBox.maxY = riddenByEntity.boundingBox.maxY;
                if (!selfPropelledFlyer() || (selfPropelledFlyer() && !isOnAir()))
                {
                    motionX += riddenByEntity.motionX * getCustomSpeed();
                    motionZ += riddenByEntity.motionZ * getCustomSpeed();
                }

                // blood - This must be run on server side only since it causes glitch/twitch if run on both sides.
                if (MoCreatures.isServer())
                {
                    moveEntity(motionX, motionY, motionZ);
                }

                prevRotationYaw = rotationYaw = riddenByEntity.rotationYaw;
                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                setRotation(rotationYaw, rotationPitch);
            }
            // blood - This must be run on server side only since it causes glitch/twitch if run on both sides.
            if (!worldObj.isRemote)
            {
                //needs to be left in so flying mounts can be controlled
                moveEntity(motionX, motionY, motionZ);
            }
            if (isFlyingAlone())
            {
                int distY = MoCTools.distanceToFloor(this);
                if (distY <= flyingHeight())
                {
                    motionY *= movement;
                }
                if (distY <= flyingHeight() && (isCollidedHorizontally || rand.nextInt(100) == 0))
                {
                    motionY += 0.1D;
                }
                if (distY > flyingHeight() || rand.nextInt(150) == 0)
                {
                    motionY -= 0.10D;
                }
                
                if (isOnAir())
                {
                    double xVelocity = 0.05F * Math.cos((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F);
                    double zVelocity = 0.05F * Math.sin((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F);
                    motionX -= xVelocity;
                    motionZ -= zVelocity;
                }
            }

            if (isFlyer() && riddenByEntity == null && entityToAttack != null && entityToAttack.posY < posY && rand.nextInt(30) == 0)
            {
                motionY = -0.25D;
            }

            if (isFlyer() && (riddenByEntity != null) && getIsTamed())
            {
                motionY -= 0.08D;
                motionY *= myFallSpeed();//0.6D;
            }
            else if (!isFlyingAlone())
            {
                motionY -= 0.08D;
                motionY *= 0.98000001907348633D;
            }
            
            if (riddenByEntity != null && isOnAir())
            {
                movement = flyerFriction();
                
            }
            motionX *= movement;
            motionZ *= movement;
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

    /**
     * Maximum flyer height when moving autonomously
     * @return
     */
    public int flyingHeight()
    {
        return 5;
    }

    /**
     * Used for flyer mounts, to calculate fall speed
     * @return
     */
    protected double myFallSpeed()
    {
        return 0.6D;
    }

    /**
     * flyer mounts Y thrust
     * @return
     */
    protected double flyerThrust()
    {
        return 0.3D;
    }

    /**
     * flyer deceleration on Z and X axis
     * @return
     */
    protected float flyerFriction()
    {
        return 0.91F;
    }

    /**
     * Alternative flyer mount movement, when true, the player only controls frequency of wing flaps
     * @return
     */
    protected boolean selfPropelledFlyer()
    {
        return false;
    }

    /**
     * Sets a flag that will make the Entity "jump" in the next onGround
     * moveEntity update
     */
    @Override
    public void makeEntityJump()
    {
    }

    /**
     * Boolean used for flying mounts
     */
    public boolean isFlyer()
    {
        return false;
    }

    /**
     * How difficult is the creature to be tamed? the Higher the number, the
     * more difficult
     */
    public int getMaxTemper()
    {
        return 100;
    }

    /**
     * mount speed
     */
    public double getCustomSpeed()
    {
        return 0.8D;
    }

    /**
     * mount jumping power
     */
    public double getCustomJump()
    {
        return 0.4D;
    }

    /**
     * sound played when an untamed mount buckles rider
     */
    protected String getMadSound()
    {
        return null;
    }

    @Override
    public void makeEntityDive() {}

    /**
     * Is this a rideable entity?
     */
    public boolean rideableEntity()
    {
        return false;
    }

    @Override
    public boolean shouldRenderName()
    {
        return getDisplayName() && (riddenByEntity == null);
    }

    public boolean getDisplayName()
    {
        return (getName() != null && !getName().equals(""));
    }

    @Override
    public int nameYOffset()
    {
        return -80;
    }

    @Override
    public double roperYOffset()
    {
        return 0D;
    }

    /**
     * fixes bug with entities following a player carrying wheat
     */
    @Override
    protected Entity findPlayerToAttack()
    {
        return null;
    }

    public void faceItem(int xCoordinate, int yCoordinate, int zCoordinate, float f)
    {
        double xDistance = xCoordinate - posX;
        double yDistance = yCoordinate - posY;
        double zDistance = zCoordinate - posZ;
        
        double overallDistanceSquared = MathHelper.sqrt_double((xDistance * xDistance) + (zDistance * zDistance));
        
        float xzAngleInDegreesToNewLocation = (float) ((Math.atan2(zDistance, xDistance) * 180D) / Math.PI) - 90F;
        float yAngleInDegreesToNewLocation = (float) ((Math.atan2(yDistance, overallDistanceSquared) * 180D) / Math.PI);
        
        rotationPitch = -adjustRotation(rotationPitch, yAngleInDegreesToNewLocation, f);
        rotationYaw = adjustRotation(rotationYaw, xzAngleInDegreesToNewLocation, f);
    }

    /**
     * 
     * @param currentRotation
     * @param rotationAdjustment
     * @param rotationLimit
     * @return
     */
    public float adjustRotation(float currentRotation, float rotationAdjustment, float rotationLimit)
    {
        float amountToChangeRotationBy = rotationAdjustment;
        for (amountToChangeRotationBy = rotationAdjustment - currentRotation; amountToChangeRotationBy < -180F; amountToChangeRotationBy += 360F)
        {
        }
        for (; amountToChangeRotationBy >= 180F; amountToChangeRotationBy -= 360F)
        {
        }
        if (amountToChangeRotationBy > rotationLimit)
        {
            amountToChangeRotationBy = rotationLimit;
        }
        if (amountToChangeRotationBy < -rotationLimit)
        {
            amountToChangeRotationBy = -rotationLimit;
        }
        return currentRotation + amountToChangeRotationBy;
    }

    public boolean isFlyingAlone()
    {
        return false;
    }

    public float getMoveSpeed()
    {
        return 0.7F;
    }

    /**
     * Used to synchronize animations between server and client
     * 
     * @param attackType
     */
    @Override
    public void performAnimation(int attackType)
    {
    }

    /**
     * Used to follow the player carrying the item
     * 
     * @param itemStack
     * @return
     */
    public boolean isMyFollowFood(ItemStack itemStack)
    {
        return false;
    }

    private void followPlayer()
    {
        EntityPlayer closestEntityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
        if (closestEntityPlayer == null) { return; }

        ItemStack itemstackThatPlayerIsHolding = closestEntityPlayer.getHeldItem();
        if (itemstackThatPlayerIsHolding != null && isMyFollowFood(itemstackThatPlayerIsHolding))
        {
            PathEntity pathEntity = worldObj.getPathEntityToEntity(this, closestEntityPlayer, 16F, true, false, false, true);
            setPathToEntity(pathEntity);
        }
    }

    @Override
    public Entity getRoper()
    {
        return null;
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

    public boolean isOnAir()
    {
        return (worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D), MathHelper.floor_double(posZ)) &&
                worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 1.2D), MathHelper.floor_double(posZ)));
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

    @Override
    public String getOwnerName()
    {
        return dataWatcher.getWatchableObjectString(20);
    }

    @Override
    public void setOwner(String par1Str)
    {
        dataWatcher.updateObject(20, par1Str);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        Entity entityThatAttackedThisCreature = damageSource.getEntity();
        //this avoids damage done by Players to a tamed creature that is not theirs
        if (MoCreatures.proxy.enableStrictOwnership && getOwnerName() != null && !getOwnerName().equals("") && entityThatAttackedThisCreature != null && entityThatAttackedThisCreature instanceof EntityPlayer && !((EntityPlayer) entityThatAttackedThisCreature).getCommandSenderName().equals(getOwnerName()) && !MoCTools.isThisPlayerAnOP(((EntityPlayer) entityThatAttackedThisCreature))) { return false; }

        if (MoCreatures.isServer() && getIsTamed())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHealth(getEntityId(),  getHealth()), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        return super.attackEntityFrom(damageSource, damageTaken);
    }

    public boolean getIsRideable() 
    {    
        return false;
    }

    public void setRideable(boolean b) {}

    protected EntityLivingBase getClosestEntityLiving(Entity entity, double distance)
    {
        double currentMinimumDistance = -1D;
        EntityLivingBase entityLiving = null;
        
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
        int iterationLength = entitiesNearbyList.size();
        
        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	
	            if (shouldEntityBeIgnored(entityNearby))
	            {
	                continue;
	            }
	            
	            double overallDistanceSquared = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)) && ((EntityLivingBase) entityNearby).canEntityBeSeen(entity))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityLiving = (EntityLivingBase) entityNearby;
	            }
	        }
        }
        return entityLiving;
    }

    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (
	        		!(entity instanceof EntityLiving)
	                || (entity instanceof EntityMob) 
	                || (entity instanceof EntityPlayer && getIsTamed()) 
	                || (entity instanceof MoCEntityKittyBed) 
	                || (entity instanceof MoCEntityLitterBox) 
	                || (getIsTamed() && (entity instanceof MoCEntityAnimal && ((MoCEntityAnimal) entity).getIsTamed())) 
	                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)) 
	                || ((entity instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
	                || (entity.width > width && entity.height > height)
	                || (entity instanceof MoCEntityEgg)
	            );
    }

    @Override
    public void setArmorType(byte i) {}
    
    @Override
    public void dismountEntity() {}

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
        
        for (int index = 0; index < iterationLength; index++)
        {
            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
            
            if (entitiesThatAreScary(entityNearby))
            {
                entityLiving = (EntityLivingBase) entityNearby;
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
        return (
        			(entity instanceof EntityLivingBase)
        			&& ((entity.width >= 0.5D) || (entity.height >= 0.5D))
        		);
    }

    public void runLikeHell(Entity entity)
    {
        double xDistance = posX - entity.posX;
        double zDistance = posZ - entity.posZ;
        
        double angleInRadiansToNewLocation = Math.atan2(xDistance, zDistance);
        angleInRadiansToNewLocation += (rand.nextFloat() - rand.nextFloat()) * 0.75D;
        
        double tempNewPosX = posX + (Math.sin(angleInRadiansToNewLocation) * 8D);
        double tempNewPosZ = posZ + (Math.cos(angleInRadiansToNewLocation) * 8D);
        
        int temp1NewPosX = MathHelper.floor_double(tempNewPosX);
        int temp1NewPosY = MathHelper.floor_double(boundingBox.minY);
        int temp1NewPosZ = MathHelper.floor_double(tempNewPosZ);
        
        int index = 0;
        
        do
        {
            if (index >= 16)
            {
                break;
            }
            
            int newPosX = (temp1NewPosX + rand.nextInt(4)) - rand.nextInt(4);
            int newPosY = (temp1NewPosY + rand.nextInt(3)) - rand.nextInt(3);
            int newPosZ = (temp1NewPosZ + rand.nextInt(4)) - rand.nextInt(4);
            
            if (
            		(newPosY > 4) 
            		&& ((worldObj.isAirBlock(newPosX, newPosY, newPosZ)) || (worldObj.getBlock(newPosX, newPosY, newPosZ) == Blocks.snow))
            		&& (!worldObj.isAirBlock(newPosX, newPosY - 1, newPosZ))
            	)
            {
                PathEntity pathEntity = worldObj.getEntityPathToXYZ(this, newPosX, newPosY, newPosZ, 16F, true, false, false, true);
                setPathToEntity(pathEntity);
                break;
            }
            
            index++;
        } while (true);
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

    protected boolean canBeTrappedInNet() 
    {
        return (this instanceof IMoCTameable) && getIsTamed();
    }

    /**
     * Returns true if the entity is of the @link{EnumCreatureType} provided
     * @param type The EnumCreatureType type this entity is evaluating
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount)
    {
        if (type == EnumCreatureType.ambient) {return true;}
        else {return false;}
    }

    @Override
    public void riderIsDisconnecting(boolean flag)
    {
        riderIsDisconnecting = true;
    }
}