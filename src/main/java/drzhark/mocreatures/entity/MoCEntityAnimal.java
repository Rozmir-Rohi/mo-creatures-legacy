package drzhark.mocreatures.entity;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityBird;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.animal.MoCEntityWyvern;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
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
import net.minecraft.util.Vec3;
import net.minecraft.world.World;


public abstract class MoCEntityAnimal extends EntityAnimal implements IMoCEntity
{
    protected boolean divePending;
    protected boolean jumpPending;
    protected int temper;
    protected boolean isEntityJumping;
    public EntityLivingBase roper;
    private PathEntity entitypath;
    // used by MoCPlayerTracker to prevent dupes when a player disconnects on animal from server
    protected boolean riderIsDisconnecting;
    private int petDataId = -1;
    public float moveSpeed;
    protected String texture;
    private boolean hasKilledPrey = false;


    public MoCEntityAnimal(World world)
    {
        super(world);
        setTamed(false);
        setAdult(true);
        riderIsDisconnecting = false;
        texture = "blank.jpg";
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }

    @Override
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

    public boolean getShouldDisplayName()
    {
        return (getName() != null && !getName().equals(""));
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
    public boolean canBeCollidedWith()
    {
        return riddenByEntity == null;
    }
    
    @Override
    public boolean canBePushed()
    {
        return canBeCollidedWith();
    }

    public boolean getIsJumping()
    {
        return isEntityJumping;

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

    public void setIsJumping(boolean flag)
    {
        isEntityJumping = flag;
    }

    @Override
    protected boolean canDespawn()
    {
    	return false;
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

    public EntityLivingBase getClosestTarget(Entity entity, double distance)
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

	            if (!(entityNearby instanceof EntityLivingBase) || (entityNearby == entity) || (entityNearby == entity.riddenByEntity) || (entityNearby == entity.ridingEntity) || (entityNearby instanceof EntityPlayer) || (entityNearby instanceof EntityMob) || (height <= entityNearby.height) || (width <= entityNearby.width))
	            {
	                continue;
	            }

	            double overallDistanceSquared = entityNearby.getDistanceSq(entity.posY, entity.posZ, entity.motionX);

	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)) && ((EntityLivingBase) entityNearby).canEntityBeSeen(entity))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityLiving = (EntityLivingBase) entityNearby;
	            }
	        }
        }

        return entityLiving;
    }


    protected EntityLivingBase getClosestSpecificEntity(Entity entity, Class myClass, double distance)
    {
        double currentMinimumDistance = -1D;

        EntityLivingBase entityLiving = null;

        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(distance, distance, distance));

        int iterationLength = entitiesNearbyList.size();

        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);

	            if (!myClass.isAssignableFrom(entityNearby.getClass()))
	            {
	                continue;
	            }

	            double overallDistanceSquared = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);

	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)))// && ((EntityLiving) entity1).canEntityBeSeen(entity))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityLiving = (EntityLivingBase) entityNearby;
	            }
	        }
        }

        return entityLiving;
    }

    /**
     * Tells the creature not to hunt any of the entities that are returned with this function.
     * This is used within the findPlayerToAttack function
     *
     * @param entity
     * @return
     */
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return
        	(
        		!(entity instanceof EntityLiving)
                || (entity instanceof IMob || entity instanceof MoCEntityMob) //don't hunt the creature if it is a mob
                || (entity instanceof EntityPlayer)
                || (entity instanceof MoCEntityKittyBed || entity instanceof MoCEntityLitterBox)
                || (getIsTamed() && (entity instanceof IMoCEntity && ((IMoCEntity) entity).getIsTamed()))
                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves))
                || (entity instanceof MoCEntityHorse && !(MoCreatures.proxy.attackHorses))
                || (entity.width > width || entity.height > height)
                || (entity instanceof MoCEntityEgg)
        	);
    }

    /**
     * Finds an entity described in entitiesThatAreScary within the given distance
     *
     * @param distance
     * @return
     */
    protected EntityLivingBase getScaryEntity(double distance)
    {
        double currentMinimumDistance = -1D;

        EntityLivingBase entityLiving = null;

        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, 4D, distance));

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
        return
        	(
        		entity.getClass() != getClass()
        		&& entity instanceof EntityLivingBase
        		&& ((entity.width >= 0.5D) || (entity.height >= 0.5D))
        	);
    }


    public boolean isPredator()
    {
    	return false;
    }

    public boolean doesForageForFood()
    {
    	return false;
    }

    @Override
	public void onKillEntity(EntityLivingBase entityLivingThatHasBeenKilled)
    {
    	if (isPredator() && MoCreatures.proxy.destroyDrops)
    	{
    		if (!(entityLivingThatHasBeenKilled instanceof EntityPlayer) && !(entityLivingThatHasBeenKilled instanceof EntityMob))
    		{
	    		hasKilledPrey = true;
    		}
    	}
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer())
        {
            if (forceUpdates() && rand.nextInt(500) == 0)
            {
                MoCTools.forceDataSync(this);
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

                if (this instanceof MoCEntityBigCat)
                {
                	((MoCEntityBigCat) this).setHungry(false);
                }
            }

            if (
            		doesForageForFood()
            		&& !isMovementCeased()
            		&& (getHealth() < getMaxHealth() || !isPredator() || !getIsAdult())
            		&& riddenByEntity == null
            		&& ridingEntity == null
            	)
            {
            	EntityItem closestEntityItem = getClosestEntityItem(this, 12D);

                if (closestEntityItem != null)
                {
                	ItemStack itemStack = closestEntityItem.getEntityItem();

                	if (isMyHealFood(itemStack))
                	{

                		float distanceToEntityItem = closestEntityItem.getDistanceToEntity(this);

                		if (this instanceof MoCEntityBird)
                		{
                			MoCEntityBird bird = (MoCEntityBird) this;

                			if (bird.isFleeing) {return;}

                			bird.flyToNextEntity(closestEntityItem);

                			if ((distanceToEntityItem < 2.0F) && (closestEntityItem != null))
	                		{
	                			if (rand.nextInt(50) == 0) //birds take some time to eat the item
	                			{
	                				closestEntityItem.setDead();
		                			heal(5);

		                			if (!getIsTamed() && rand.nextInt(3) == 0) {bird.setPreTamed(true);}
	                			}
	                		}
                		}


                		else
                		{
	                		if (distanceToEntityItem > 2.0F)
	                		{
	                			getMyOwnPath(closestEntityItem, distanceToEntityItem);
	                		}

	                		if ((distanceToEntityItem < 2.0F) && (closestEntityItem != null))
	                		{
	                			closestEntityItem.setDead();

	                			heal(5);

	                			MoCTools.playCustomSound(this, "eating", worldObj);
	                		}
	                		if ((this instanceof MoCEntityBigCat) && !getIsAdult() && !getIsTamed() && (getMoCAge() < 80))
	            			{
	                			if (rand.nextInt(10) == 0) //1 out of 10 chance
	                			{
	                				((MoCEntityBigCat) this).setPreTamed(true); //sets the baby big cat as ready for taming with a medallion
	                			}
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

        if (isSwimming() && isSwimmerEntity())
        {
            floatOnWater();
        }

        if (!isMovementCeased() && entityToAttack == null)
        {
            followPlayer();
        }
        resetInLove();
        super.onLivingUpdate();
    }

    public boolean isNotScared()
    {
        return false;
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

        int yDistanceToSurfaceOfWater = (int) MoCTools.distanceToWaterSurface(this);
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
     * All foods in minecraft
     *
     * @param item
     * @return
     */
    public boolean isItemEdible(Item item)
    {
        return
        	(
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

    public EntityItem getClosestItem(Entity entity, double distance, Item item, Item item1)
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
	            if ((entityItemNearby.getEntityItem().getItem() != item) && (entityItemNearby.getEntityItem().getItem() != item1))
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

    public MoCEntityAnimal spawnBabyAnimal(EntityAgeable entityAgeable)
    {
        return null;
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
        return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox);
    }

    public boolean getCanSpawnHereAquatic()
    {
        return worldObj.checkNoEntityCollision(boundingBox);
    }

    public boolean getCanSpawnHere2()
    {
        return getCanSpawnHereCreature() && getCanSpawnHereLiving();
    }

    @Override
    public boolean getCanSpawnHere()
    {
        if (MoCreatures.entityMap.get(getClass()).getFrequency() <= 0) {return false;}

        if (worldObj.provider.dimensionId != 0)
        {
            return getCanSpawnHereCreature() && getCanSpawnHereLiving();
        }

        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        String biomeName = MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (biomeName.toLowerCase().contains("jungle"))
        {
            return getCanSpawnHereJungle();
        }

        if (biomeName.equals("WyvernBiome"))
        {
            return getCanSpawnHereMoCBiome();
        }

        return super.getCanSpawnHere();
    }

    private boolean getCanSpawnHereMoCBiome()
    {
        if (worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(boundingBox))
        {
            int xCoordinate = MathHelper.floor_double(posX);
            int yCoordinate = MathHelper.floor_double(boundingBox.minY);
            int zCoordinate = MathHelper.floor_double(posZ);

            if (yCoordinate < 50) { return false; }

            Block block = worldObj.getBlock(xCoordinate, yCoordinate - 1, zCoordinate);

            if (
            		block == MoCreatures.mocDirt
            		|| block == MoCreatures.mocGrass
            		|| (block != null && block.isLeaves(worldObj, xCoordinate, yCoordinate - 1, zCoordinate))
            	)
            {
            	return true;
            }
        }
        return false;
    }

    public boolean getCanSpawnHereJungle()
    {
        if (worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty())
        {
            return true;
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
        if (!isFlyer() && (!rideableEntity() || riddenByEntity == null))// || (ridingEntity != null && !(ridingEntity instanceof EntityPlayer)))
        {
            super.moveEntityWithHeading(strafeMovement, forwardMovement);
            return;
        }

        float movementSideways = strafeMovement;
        float movementForward = forwardMovement;

        if (handleWaterMovement())
        {
            if (riddenByEntity != null)
            {
                motionX += riddenByEntity.motionX * (getCustomSpeed() / 2.0D);
                motionZ += riddenByEntity.motionZ * (getCustomSpeed() / 2.0D);
                movementSideways = (float) (((EntityLivingBase)riddenByEntity).moveStrafing * 0.5F * (getCustomSpeed() / 2.0D));;
                movementForward = (float) (((EntityLivingBase)riddenByEntity).moveForward * (getCustomSpeed() / 2.0D));

                if (jumpPending && !getIsJumping())
                {
                    motionY = getCustomJump()*2;
                    setIsJumping(true);
                    jumpPending = false;
                }

                if (!worldObj.isRemote)
                {
                    super.moveEntityWithHeading(movementSideways, movementForward);
                    //moveEntity(motionX, motionY, motionZ);
                }

                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                if (rand.nextInt(20) == 0)
                {
                    rotationYaw = riddenByEntity.rotationYaw;
                }
                setRotation(rotationYaw, rotationPitch);

                if (MoCreatures.isServer() && !getIsTamed())
                {
                    playSound(getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.3D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    riddenByEntity = null;
                }

                if (rand.nextInt(25) == 0)
                {
                    setIsJumping(false);
                }
            }

            double yCoordinate = posY;

            if (!worldObj.isRemote)
            {
                moveFlying(strafeMovement, forwardMovement, 0.02F);
                //moveEntity(motionX, motionY, motionZ);
                super.moveEntityWithHeading(movementSideways, movementForward);
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
                movementSideways = (float) (((EntityLivingBase)riddenByEntity).moveStrafing * 0.5F * (getCustomSpeed() / 2.0D));;
                movementForward = (float) (((EntityLivingBase)riddenByEntity).moveForward * (getCustomSpeed() / 2.0D));

                if (jumpPending && !getIsJumping())
                {
                    motionY = getCustomJump();
                    jumpPending = false;
                }

                //moveEntity(motionX, motionY, motionZ);
                super.moveEntityWithHeading(movementSideways, movementForward);

                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                if (rand.nextInt(20) == 0)
                {
                    rotationYaw = riddenByEntity.rotationYaw;
                }
                setRotation(rotationYaw, rotationPitch);
                if (MoCreatures.isServer() && !getIsTamed())
                {
                    playSound(getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
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
            float acceleration = 0.91F;
            if (onGround)
            {
                acceleration = 0.5460001F;
                Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
                if (block != Blocks.air)
                {
                    acceleration = block.slipperiness * 0.91F;
                }
            }

            float f3 = 0.162771F / (acceleration * acceleration * acceleration);
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
                if ((rand.nextInt(5) == 0) && !getIsJumping() && motionY < 0 && motionY > -0.2D)
                {
                    motionY = getCustomJump();
                    setIsJumping(true);
                }
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
                    playSound(getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.9D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    riddenByEntity = null;
                }

                if (onGround)
                {
                    setIsJumping(false);
                }
                if (MoCreatures.isServer() && this instanceof MoCEntityTameableAnimal)
                {
                    int chance = (getMaxTemper() - getTemper());
                    if (chance <= 0)
                    {
                        chance = 5;
                    }
                    if (rand.nextInt(chance * 8) == 0)
                    {
                        MoCTools.tameWithName((EntityPlayer) riddenByEntity, (MoCEntityTameableAnimal)this);
                    }
                }
            }
            if ((riddenByEntity != null) && getIsTamed())
            {
                boundingBox.maxY = riddenByEntity.boundingBox.maxY;
                if (!selfPropelledFlyer() || (selfPropelledFlyer() && !isOnAir()))
                {
                    movementSideways = (float) (((EntityLivingBase)riddenByEntity).moveStrafing * 0.5F * getCustomSpeed());
                    movementForward = (float) (((EntityLivingBase)riddenByEntity).moveForward * getCustomSpeed());
                }

                if (jumpPending && (isFlyer()))
                {
                    motionY += flyerThrust();//0.3D;
                    jumpPending = false;

                    if (selfPropelledFlyer() && isOnAir() && MoCreatures.isServer())
                    {
                        float velX = (float) (0.5F * Math.cos((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F));
                        float velZ = (float) (0.5F * Math.sin((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F));
                        motionX -= velX;
                        motionZ -= velZ;
                        if (MoCreatures.isServer())
                        {
                            moveEntity(motionX, 0D, motionZ);
                        }
                    }
                }
                else if (jumpPending && !getIsJumping())
                {
                    motionY = getCustomJump()*2;
                    setIsJumping(true);
                    jumpPending = false;
                }

                if (divePending)
                {
                    divePending = false;
                    motionY -= 0.3D;
                }

                // blood - This must be run on server side only since it causes glitch/twitch if run on both sides.
                if (MoCreatures.isServer())
                {
                    //moveEntity(motionX, motionY, motionZ);
                    super.moveEntityWithHeading(movementSideways, movementForward);//, motionZ);
                }
                if (onGround)
                {
                    // blood - fixes jump bug
                    jumpPending = false;
                    setIsJumping(false);
                    divePending = false;
                }
                prevRotationYaw = rotationYaw = riddenByEntity.rotationYaw;
                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                setRotation(rotationYaw, rotationPitch);
            }
            // blood - This must be run on server side only since it causes glitch/twitch if run on both sides.
            if (!worldObj.isRemote)
            {
                //needs to be left in so flying mounts can be controlled
                super.moveEntityWithHeading(movementSideways, movementForward);
            }
            if (isFlyingAlone())
            {
                int yDistanceFromGround = MoCTools.distanceToWaterFloor(this);
                if (yDistanceFromGround <= flyingHeight())
                {
                    motionY *= 0.3 + (acceleration);
                }
                if (yDistanceFromGround <= flyingHeight() && (isCollidedHorizontally || rand.nextInt(100) == 0))
                {
                    motionY += 0.1D;
                }
                if (yDistanceFromGround > flyingHeight() || rand.nextInt(150) == 0)
                {
                    motionY -= 0.10D;
                }

                if (isOnAir())
                {
                    double xVelocity = 0.1F * Math.cos((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F);
                    double zVelocity = 0.1F * Math.sin((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F);
                    motionX -= xVelocity;
                    motionZ -= zVelocity;
                }

                if (motionY < 0)
                {
                    motionY *= 0.5D;
                }
            }

            if (isFlyer() && riddenByEntity == null && entityToAttack != null && entityToAttack.posY < posY && rand.nextInt(30) == 0)
            {
                motionY = -0.25D;
            }

            if (!isFlyer() && (this instanceof MoCEntityHorse)) //slow fall for floating horses (unicorns, and ghost horses)
        	{
        		if (((MoCEntityHorse) this).isFloater())
        		{
        			motionY += (0.21D - (myFallSpeed()/10D));//0.15D;
                    motionY *= myFallSpeed();//0.6D
        		}
        	}

            if (
            		riddenByEntity != null //isFlyer()
            		&& getIsTamed()
            		&& (
            				this instanceof MoCEntityHorse || this instanceof MoCEntityWyvern
            			)
            	)
            {
            	if (isFlyer())  //slow fall for flying horses and wyverns
            	{
            		motionY += (0.21D - (myFallSpeed()/10D));//0.15D;
                    motionY *= myFallSpeed();//0.6D
            	}


                if (isOnAir() || !(this instanceof MoCEntityWyvern))  //controls the land and flying speed for horses, and fly speeds for Wyverns. Also makes every flyer except wyverns be as fast on the ground as they are in the air
                {
                	acceleration = ((float) getCustomSpeed())*0.77F;
                }
            }
            else if (!isFlyingAlone())
            {
                motionY -= 0.08D;
                motionY *= 0.98000001907348633D;
            }

            if (riddenByEntity != null && isOnAir() & !isFlyer())
            {
            	acceleration = flyerFriction();

            }

            motionX *= acceleration;
            motionZ *= acceleration;
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
        jumpPending = true;
    }

    /**
     * Boolean used for flying mounts
     */
    public boolean isFlyer()
    {
        return false;
    }

    public int getTemper()
    {
        return temper;
    }

    public void setTemper(int i)
    {
        temper = i;
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
        return getShouldDisplayName() && (riddenByEntity == null);
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
     * adds a following behavior to animals with a rope
     */
    @Override
    protected void updateEntityActionState()
    {
        if (getIsTamed() && (riddenByEntity == null) && (roper != null))
        {
            float distance = roper.getDistanceToEntity(this);
            if (distance > 3F)
            {
                getPathOrWalkableBlock(roper, distance);
            }
        }

        if (!isFlyingAlone())
        {
            super.updateEntityActionState();
            return;
        }

        hasAttacked = false;
        float huntingRange = 16F;

        if (entityToAttack == null)
        {
            entityToAttack = findPlayerToAttack();
            if (entityToAttack != null)
            {
                entitypath = worldObj.getPathEntityToEntity(this, entityToAttack, huntingRange, true, false, false, true);
            }
        }

        // prevent tamed animals attacking other tamed animals
        else if (!entityToAttack.isEntityAlive() || MoCTools.isTamed(entityToAttack)) //((entityToAttack instanceof IMoCTameable) && ((IMoCTameable)entityToAttack).getIsTamed() && getIsTamed()))
        {
            entityToAttack = null;
        }

        else
        {
            float distanceToPrey = entityToAttack.getDistanceToEntity(this);
            if (canEntityBeSeen(entityToAttack))
            {
                attackEntity(entityToAttack, distanceToPrey);
            }
        }

        if (!hasAttacked && (entityToAttack != null) && ((entitypath == null) || (rand.nextInt(10) == 0)))
        {
            // entitypath = worldObj.getPathToEntity(this, entityToAttack, f);
            entitypath = worldObj.getPathEntityToEntity(this, entityToAttack, huntingRange, true, false, false, true);
        }

        else if (((entitypath == null) && (rand.nextInt(80) == 0)) || (rand.nextInt(80) == 0))
        {
            boolean flag = false;
            
            int newPosX = -1;
            int newPosY = -1;
            int newPosZ = -1;
            
            float minimumProbability = -99999F;
            
            for (int index = 0; index < 10; index++)
            {
                int tempX = MathHelper.floor_double((posX + rand.nextInt(13)) - 6D);
                int tempY = MathHelper.floor_double((posY + rand.nextInt(7)) - 3D);
                int tempZ = MathHelper.floor_double((posZ + rand.nextInt(13)) - 6D);
                
                float liklihoodOfEntityWalkingToThisBlock = getBlockPathWeight(tempX, tempY, tempZ);
                
                if (liklihoodOfEntityWalkingToThisBlock > minimumProbability)
                {
                    minimumProbability = liklihoodOfEntityWalkingToThisBlock;
                    newPosX = tempX;
                    newPosY = tempY;
                    newPosZ = tempZ;
                    flag = true;
                }
            }

            if (flag)
            {
                entitypath = worldObj.getEntityPathToXYZ(this, newPosX, newPosY, newPosZ, 10F, true, false, false, true);
            }
        }

        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        boolean isWaterMovement = handleWaterMovement();
        boolean isLavaMovement = handleLavaMovement();

        rotationPitch = 0.0F;
        if ((entitypath == null) || (rand.nextInt(100) == 0))
        {
            super.updateEntityActionState();
            entitypath = null;
            return;
        }

        Vec3 vectorThreeDimensional = entitypath.getPosition(this); //client

        //vectorThreeDimensional vectorThreeDimensional = entitypath.getPosition(this); //server

        for (double d = width * 2.0F; (vectorThreeDimensional != null) && (vectorThreeDimensional.squareDistanceTo(posX, vectorThreeDimensional.yCoord, posZ) < (d * d));)
        {
            entitypath.incrementPathIndex();

            if (entitypath.isFinished())
            {
                vectorThreeDimensional = null;
                entitypath = null;
            }
            else
            {
                //vectorThreeDimensional = entitypath.getPosition(this); //server
                //TODO 4FIX test!
                vectorThreeDimensional = entitypath.getPosition(this);
            }
        }

        isJumping = false;
        if (vectorThreeDimensional != null)
        {
            double vectorDistanceX = vectorThreeDimensional.xCoord - posX;
            double vectorDistanceY = vectorThreeDimensional.yCoord - yCoordinate;
            double vectorDistanceZ = vectorThreeDimensional.zCoord - posZ;

            float angleInDegreesToNewLocation = (float) ((Math.atan2(vectorDistanceZ, vectorDistanceX) * 180D) / Math.PI) - 90F;
            float amountOfDegreesToChangeRotationYawBy = angleInDegreesToNewLocation - rotationYaw;

            moveForward = (float) getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();

            for (; amountOfDegreesToChangeRotationYawBy < -180F; amountOfDegreesToChangeRotationYawBy += 360F)
            {
            }
            for (; amountOfDegreesToChangeRotationYawBy >= 180F; amountOfDegreesToChangeRotationYawBy -= 360F)
            {
            }
            if (amountOfDegreesToChangeRotationYawBy > 30F)
            {
                amountOfDegreesToChangeRotationYawBy = 30F;
            }
            if (amountOfDegreesToChangeRotationYawBy < -30F)
            {
                amountOfDegreesToChangeRotationYawBy = -30F;
            }
            rotationYaw += amountOfDegreesToChangeRotationYawBy;
            if (hasAttacked && (entityToAttack != null))
            {
                double xDistance = entityToAttack.posX - posX;
                double zDistance = entityToAttack.posZ - posZ;
                float previousRotationYaw = rotationYaw;
                rotationYaw = (float) ((Math.atan2(zDistance, xDistance) * 180D) / Math.PI) - 90F;
                float angleInDegreesBetweenPreviousAndNewRotationYaw = (((previousRotationYaw - rotationYaw) + 90F) * (float) Math.PI) / 180F;
                moveStrafing = -MathHelper.sin(angleInDegreesBetweenPreviousAndNewRotationYaw) * moveForward * 1.0F;
                moveForward = MathHelper.cos(angleInDegreesBetweenPreviousAndNewRotationYaw) * moveForward * 1.0F;
            }
            if (vectorDistanceY > 0.0D)
            {
                isJumping = true;
            }
        }
        if (entityToAttack != null)
        {
            faceEntity(entityToAttack, 30F, 30F);
        }
        if (isCollidedHorizontally)
        {
            isJumping = true;
        }
        if ((rand.nextFloat() < 0.8F) && (isWaterMovement || isLavaMovement))
        {
            isJumping = true;
        }
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
     * Used to spawn hearts at this location
     */
    public void SpawnHeart()
    {
        double xVelocity = rand.nextGaussian() * 0.02D;
        double yVelocity = rand.nextGaussian() * 0.02D;
        double zVelocity = rand.nextGaussian() * 0.02D;

        worldObj.spawnParticle("heart", posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, xVelocity, yVelocity, zVelocity);

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

    /**
     * This method must be overrided to work in conjunction with our
     * onLivingUpdate update packets. It is currently used to fix mount bug when
     * players reconnect.
     */
    @Override
    public void mountEntity(Entity entity)
    {
        if (updateMount())
        {
            if (entity == null)
            {
                if (ridingEntity != null)
                {
                    setLocationAndAngles(ridingEntity.posX, ridingEntity.boundingBox.minY + ridingEntity.height, ridingEntity.posZ, rotationYaw, rotationPitch);
                    ridingEntity.riddenByEntity = null;
                }
                ridingEntity = null;
            }
            else
            {
                ridingEntity = entity;
                entity.riddenByEntity = this;
            }
        }
        else
        {
            super.mountEntity(entity);
        }
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

    public boolean isOnAir()
    {
        return (
        			worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.2D), MathHelper.floor_double(posZ))
        			&& worldObj.isAirBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 1.2D), MathHelper.floor_double(posZ))
                );
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
    public void onDeath(DamageSource damageSource)
    {
        if (MoCreatures.isServer())
        {
            dropMyStuff();
        }

        super.onDeath(damageSource);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        Entity entityThatAttackedThisCreature = damageSource.getEntity();
        
        if (	//this avoids damage done by Players to a tamed creature that is not theirs
        		MoCreatures.proxy.enableStrictOwnership
        		&& getOwnerName() != null
        		&& !getOwnerName().equals("")
        		&& entityThatAttackedThisCreature != null
        		&& entityThatAttackedThisCreature instanceof EntityPlayer
        		&& !((EntityPlayer) entityThatAttackedThisCreature).getCommandSenderName().equals(getOwnerName())
        		&& !MoCTools.isThisPlayerAnOP((EntityPlayer) entityThatAttackedThisCreature)
        	)
        {
        	return false;
        }

        if (isNotScared())
        {
            Entity tempEntity = entityToAttack;

            boolean flag = super.attackEntityFrom(damageSource, damageTaken);

            fleeingTick = 0;

            entityToAttack = tempEntity;
            return flag;
        }
        else
        {
            return super.attackEntityFrom(damageSource, damageTaken);
        }
    }

    public boolean getIsRideable()
    {
        return false;
    }

    public void setRideable(boolean b) {}

    @Override
    public void setArmorType(byte i) {}

    public byte getArmorType()
    {
        return 0;
    }

    @Override
    public void dismountEntity()
    {
        if (MoCreatures.isServer() && riddenByEntity != null)
        {
            riddenByEntity.mountEntity(null);
            riddenByEntity = null;
        }
    }

    /**
     * Drops armor if the animal has one
     */
    public void dropArmor() {}

    @Override
    public int pitchRotationOffset()
    {
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

    protected boolean canBeTrappedInAmulet()
    {
        return (this instanceof IMoCTameable) && getIsTamed();
    }

    @Override
    public void riderIsDisconnecting(boolean flag)
    {
        riderIsDisconnecting = true;
    }
}