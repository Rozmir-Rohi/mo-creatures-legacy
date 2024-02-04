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
    private boolean has_killed_prey = false;


    public MoCEntityAnimal(World world)
    {
        super(world);
        setTamed(false);
        setAdult(true);
        riderIsDisconnecting = false;
        texture = "blank.jpg";
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture(texture);
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData par1EntityLivingData)
    {
        selectType();
        return super.onSpawnWithEgg(par1EntityLivingData);
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

    public boolean getDisplayName()
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
        return this.dataWatcher.getWatchableObjectString(17);
    }

    /**
     * @return networked Entity "Age" in integer value, typical values are
     *         0-100.
     */
    public int getMoCAge()
    {
        return dataWatcher.getWatchableObjectInt(18);
    }

    public boolean getIsJumping()
    {
        return isEntityJumping;

    }

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
        double current_minimum_distance = -1D;

        EntityLivingBase entityliving = null;

        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));

        int iteration_length = entities_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);

	            if (entitiesToIgnoreWhenHunting(entity_nearby))
	            {
	                continue;
	            }

	            double overall_distance_squared = entity_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);

	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)) && ((EntityLivingBase) entity_nearby).canEntityBeSeen(entity))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityliving = (EntityLivingBase) entity_nearby;
	            }
	        }
        }

        return entityliving;
    }

    public EntityLivingBase getClosestTarget(Entity entity, double distance)
    {
        double current_minimum_distance = -1D;

        EntityLivingBase entityliving = null;

        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));

        int iteration_length = entities_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);

	            if (!(entity_nearby instanceof EntityLivingBase) || (entity_nearby == entity) || (entity_nearby == entity.riddenByEntity) || (entity_nearby == entity.ridingEntity) || (entity_nearby instanceof EntityPlayer) || (entity_nearby instanceof EntityMob) || (height <= entity_nearby.height) || (width <= entity_nearby.width))
	            {
	                continue;
	            }

	            double overall_distance_squared = entity_nearby.getDistanceSq(entity.posY, entity.posZ, entity.motionX);

	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)) && ((EntityLivingBase) entity_nearby).canEntityBeSeen(entity))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityliving = (EntityLivingBase) entity_nearby;
	            }
	        }
        }

        return entityliving;
    }


    protected EntityLivingBase getClosestSpecificEntity(Entity entity, Class myClass, double distance)
    {
        double current_minimum_distance = -1D;

        EntityLivingBase entityliving = null;

        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(distance, distance, distance));

        int iteration_length = entities_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);

	            if (!myClass.isAssignableFrom(entity_nearby.getClass()))
	            {
	                continue;
	            }

	            double overall_distance_squared = entity_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);

	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)))// && ((EntityLiving) entity1).canEntityBeSeen(entity))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityliving = (EntityLivingBase) entity_nearby;
	            }
	        }
        }

        return entityliving;
    }

    /**
     * Tells the creature not to hunt any of the entities that are returned with this function.
     * This is used within the findPlayerToAttack function
     *
     * @param entity
     * @return
     */
    public boolean entitiesToIgnoreWhenHunting(Entity entity)
    {
        return
        	(
        		!(entity instanceof EntityLiving)
                || (entity instanceof IMob || entity instanceof MoCEntityMob) //don't hunt the creature if it is a mob
                || (entity instanceof EntityPlayer)
                || (entity instanceof MoCEntityKittyBed || entity instanceof MoCEntityLitterBox)
                || (this.getIsTamed() && (entity instanceof IMoCEntity && ((IMoCEntity) entity).getIsTamed()))
                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves))
                || (entity instanceof MoCEntityHorse && !(MoCreatures.proxy.attackHorses))
                || (entity.width > this.width || entity.height > this.height)
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
        double current_minimum_distance = -1D;

        EntityLivingBase entityliving = null;

        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, 4D, distance));

        int iteration_length = entities_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);

	            if (entitiesThatAreScary(entity_nearby))
	            {
	                entityliving = (EntityLivingBase) entity_nearby;
	            }
	        }
        }

        return entityliving;
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
        		entity.getClass() != this.getClass()
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

    public void onKillEntity(EntityLivingBase entityliving_that_has_been_killed)
    {
    	if (isPredator() && MoCreatures.proxy.destroyDrops)
    	{
    		if (!(entityliving_that_has_been_killed instanceof EntityPlayer) && !(entityliving_that_has_been_killed instanceof EntityMob))
    		{
	    		has_killed_prey = true;
    		}
    	}
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer())
        {
            if (rideableEntity() && this.riddenByEntity != null)
            {
                Riding();
            }

            if (forceUpdates() && rand.nextInt(500) == 0)
            {
                MoCTools.forceDataSync(this);
            }

            if (isPredator() && has_killed_prey)
            {
            	if (MoCreatures.proxy.destroyDrops) //destroy the drops of the prey
            	{
	            	List entities_nearby_list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(3, 3, 3));

	            	int iteration_length = entities_nearby_list.size();

	            	if (iteration_length > 0)
	            	{
		                for (int index = 0; index < iteration_length; index++)
		                {
		                    Entity entity_in_list = (Entity) entities_nearby_list.get(index);
		                    if (!(entity_in_list instanceof EntityItem))
		                    {
		                        continue;
		                    }

		                    EntityItem entityitem = (EntityItem) entity_in_list;

		                    if ((entityitem != null) && (entityitem.age < 5)) //targeting entityitem with age below 5 makes sure that the predator only eats the items that are dropped from the prey
		                    {
		                        entityitem.setDead();
		                    }
		                }
	            	}
            	}

            	heal(5);
	    		MoCTools.playCustomSound(this, "eating", worldObj);

                has_killed_prey = false;

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
            	EntityItem closest_entityitem = getClosestEntityItem(this, 12D);

                if (closest_entityitem != null)
                {
                	ItemStack itemstack = closest_entityitem.getEntityItem();

                	if (isMyHealFood(itemstack))
                	{

                		float distance_to_entity_item = closest_entityitem.getDistanceToEntity(this);

                		if (this instanceof MoCEntityBird)
                		{
                			MoCEntityBird bird = (MoCEntityBird) this;

                			if (bird.fleeing) {return;}

                			bird.FlyToNextEntity(closest_entityitem);

                			if ((distance_to_entity_item < 2.0F) && (closest_entityitem != null))
	                		{
	                			if (rand.nextInt(50) == 0) //birds take some time to eat the item
	                			{
	                				closest_entityitem.setDead();
		                			heal(5);

		                			if (!getIsTamed() && rand.nextInt(3) == 0) {bird.setPreTamed(true);}
	                			}
	                		}
                		}


                		else
                		{
	                		if (distance_to_entity_item > 2.0F)
	                		{
	                			getMyOwnPath(closest_entityitem, distance_to_entity_item);
	                		}

	                		if ((distance_to_entity_item < 2.0F) && (closest_entityitem != null))
	                		{
	                			closest_entityitem.setDead();

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

        if (isSwimming() && swimmerEntity())
        {
            floating();
        }

        if (!isMovementCeased() && entityToAttack == null)
        {
            followPlayer();
        }
        this.resetInLove();
        super.onLivingUpdate();
    }

    public boolean isNotScared()
    {
        return false;
    }

    public boolean swimmerEntity()
    {
        return false;
    }

    public boolean isSwimming()
    {
        return ((isInsideOfMaterial(Material.water)));
    }

    public void floating()
    {
        if (motionY < 0)
        {
            motionY = 0;
        }
        motionY += 0.001D;// 0.001

        int distY = (int) MoCTools.distanceToSurface(this);
        if (distY > 1)
        {
            motionY += (distY * 0.07);
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
     * @param itemstack
     * @return
     */
    protected boolean isMyHealFood(ItemStack itemstack)
    {
        return false;
    }

    @Override
    public boolean isInWater()
    {
        if (swimmerEntity()) { return false; }
        return super.isInWater();
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return swimmerEntity();
    }

    public EntityItem getClosestItem(Entity entity, double distance, Item item, Item item1)
    {
        double current_minimum_distance = -1D;
        EntityItem entityitem = null;

        List entitites_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));

        int iteration_length = entitites_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entitites_nearby_list.get(index);
	            if (!(entity_nearby instanceof EntityItem))
	            {
	                continue;
	            }
	            EntityItem entityitem_nearby = (EntityItem) entity_nearby;
	            if ((entityitem_nearby.getEntityItem().getItem() != item) && (entityitem_nearby.getEntityItem().getItem() != item1))
	            {
	                continue;
	            }
	            double overall_distance_squared = entityitem_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityitem = entityitem_nearby;
	            }
	        }
        }

        return entityitem;
    }

    public EntityItem getClosestEntityItem(Entity entity, double distance)
    {
        double current_minimum_distance = -1D;
        EntityItem entityitem = null;

        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));

        int iteration_length = entities_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);

	            if (!(entity_nearby instanceof EntityItem))
	            {
	                continue;
	            }

	            EntityItem entityitem_nearby = (EntityItem) entity_nearby;

	            double overall_distance_squared = entityitem_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);

	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityitem = entityitem_nearby;
	            }
	        }
        }

        return entityitem;
    }

    public EntityItem getClosestFood(Entity entity, double distance)
    {
        double current_minimum_distance = -1D;
        EntityItem entityitem = null;

        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));

        int iteration_length = entities_nearby_list.size();

        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);
	            if (!(entity_nearby instanceof EntityItem))
	            {
	                continue;
	            }

	            EntityItem entityitem_nearby = (EntityItem) entity_nearby;

	            if (!isItemEdible(entityitem_nearby.getEntityItem().getItem()))
	            {
	                continue;
	            }

	            double overall_distance_squared = entityitem_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);

	            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)))
	            {
	                current_minimum_distance = overall_distance_squared;
	                entityitem = entityitem_nearby;
	            }
	        }
        }

        return entityitem;
    }

    public void faceLocation(int x, int y, int z, float f)
    {
        double x_distance_to_new_facing_location = x + 0.5D - posX;
        double y_distance_to_new_facing_location = y + 0.5D - posY;
        double z_distance_to_new_facing_location = z + 0.5D - posZ;

        double overall_distance_to_new_facing_location_squared = (double) MathHelper.sqrt_double(x_distance_to_new_facing_location * x_distance_to_new_facing_location + z_distance_to_new_facing_location * z_distance_to_new_facing_location);
        
        float xz_angle_in_degrees_to_new_facing_location = (float) (Math.atan2(z_distance_to_new_facing_location, x_distance_to_new_facing_location) * 180.0D / Math.PI) - 90.0F;
        float y_angle_in_degrees_to_new_facing_location = (float) (-(Math.atan2(y_distance_to_new_facing_location, overall_distance_to_new_facing_location_squared) * 180.0D / Math.PI));
        
        this.rotationPitch = -this.updateRotation(rotationPitch, y_angle_in_degrees_to_new_facing_location, f);
        this.rotationYaw = this.updateRotation(rotationYaw, xz_angle_in_degrees_to_new_facing_location, f);
    }

    /**
     *
     * @param current_rotation
     * @param intended_rotation
     * @param max_increment
     * @return
     */
    private float updateRotation(float current_rotation, float intended_rotation, float max_increment)
    {
        float amount_to_change_rotation_by;

        for (amount_to_change_rotation_by = intended_rotation - current_rotation; amount_to_change_rotation_by < -180.0F; amount_to_change_rotation_by += 360.0F)
        {
            ;
        }

        while (amount_to_change_rotation_by >= 180.0F)
        {
            amount_to_change_rotation_by -= 360.0F;
        }

        if (amount_to_change_rotation_by > max_increment)
        {
            amount_to_change_rotation_by = max_increment;
        }

        if (amount_to_change_rotation_by < -max_increment)
        {
            amount_to_change_rotation_by = -max_increment;
        }

        return current_rotation + amount_to_change_rotation_by;
    }

    public void getMyOwnPath(Entity entity, float f)
    {
        PathEntity pathentity = worldObj.getPathEntityToEntity(this, entity, 16F, true, false, false, true);
        if (pathentity != null)
        {
            setPathToEntity(pathentity);
        }
    }

    /**
     * Called to make ridden entities pass on collision to rider
     */
    public void Riding()
    {
        if ((riddenByEntity != null) && (riddenByEntity instanceof EntityPlayer))
        {
            EntityPlayer entityplayer = (EntityPlayer) riddenByEntity;

            List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1.0D, 0.0D, 1.0D));

            int iteration_length = entities_nearby_list.size();

            if (iteration_length > 0)
            {
                for (int index = 0; index < iteration_length; index++)
                {
                    Entity entity_nearby = (Entity) entities_nearby_list.get(index);

                    if (entity_nearby.isDead)
                    {
                        continue;
                    }

                    entity_nearby.onCollideWithPlayer(entityplayer);

                    if (!(entity_nearby instanceof EntityMob))
                    {
                        continue;
                    }

                    float distance = getDistanceToEntity(entity_nearby);

                    if ((distance < 2.0F) && entity_nearby instanceof EntityMob && (rand.nextInt(10) == 0))
                    {
                        attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity_nearby), (float)((EntityMob)entity_nearby).getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
                    }
                }
            }

            if (entityplayer.isSneaking())
            {
                this.makeEntityDive();
            }
        }
    }

    protected void getPathOrWalkableBlock(Entity entity, float f)
    {
        PathEntity pathentity = worldObj.getPathEntityToEntity(this, entity, 16F, true, false, false, true);

        if ((pathentity == null) && (f > 8F))
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
            setPathToEntity(pathentity);
        }
    }

    public MoCEntityAnimal spawnBabyAnimal(EntityAgeable par1EntityAgeable)
    {
        return null;
    }

    public boolean getCanSpawnHereCreature()
    {
        int x_coordinate = MathHelper.floor_double(this.posX);
        int y_coordinate = MathHelper.floor_double(this.boundingBox.minY);
        int z_coordinate = MathHelper.floor_double(this.posZ);
        return this.getBlockPathWeight(x_coordinate, y_coordinate, z_coordinate) >= 0.0F;
    }

    public boolean getCanSpawnHereLiving()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox);
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
        if (MoCreatures.entityMap.get(this.getClass()).getFrequency() <= 0) {return false;}

        if (worldObj.provider.dimensionId != 0)
        {
            return getCanSpawnHereCreature() && getCanSpawnHereLiving();
        }

        int x_coordinate = MathHelper.floor_double(posX);
        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        int z_coordinate = MathHelper.floor_double(posZ);

        String s = MoCTools.BiomeName(worldObj, x_coordinate, y_coordinate, z_coordinate);

        if (s.toLowerCase().contains("jungle"))
        {
            return getCanSpawnHereJungle();
        }

        if (s.equals("WyvernBiome"))
        {
            return getCanSpawnHereMoCBiome();
        }

        return super.getCanSpawnHere();
    }

    private boolean getCanSpawnHereMoCBiome()
    {
        if (this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox))
        {
            int x_coordinate = MathHelper.floor_double(this.posX);
            int y_coordinate = MathHelper.floor_double(this.boundingBox.minY);
            int z_coordinate = MathHelper.floor_double(this.posZ);

            if (y_coordinate < 50) { return false; }

            Block block = this.worldObj.getBlock(x_coordinate, y_coordinate - 1, z_coordinate);

            if (
            		block == MoCreatures.mocDirt
            		|| block == MoCreatures.mocGrass
            		|| (block != null && block.isLeaves(worldObj, x_coordinate, y_coordinate - 1, z_coordinate))
            	)
            {
            	return true;
            }
        }
        return false;
    }

    public boolean getCanSpawnHereJungle()
    {
        if (this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty())
        {
            return true;
        }

        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Tamed", getIsTamed());
        nbttagcompound.setBoolean("Adult", getIsAdult());
        nbttagcompound.setInteger("Age", getMoCAge());
        nbttagcompound.setString("Name", getName());
        nbttagcompound.setInteger("TypeInt", getType());
        nbttagcompound.setString("Owner", getOwnerName());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setTamed(nbttagcompound.getBoolean("Tamed"));
        setAdult(nbttagcompound.getBoolean("Adult"));
        setMoCAge(nbttagcompound.getInteger("Age"));
        setName(nbttagcompound.getString("Name"));
        setType(nbttagcompound.getInteger("TypeInt"));
        setOwner(nbttagcompound.getString("Owner"));
    }

    @Override
    public void moveEntityWithHeading(float strafe_movement, float forward_movement)
    {
        //If the entity is not ridden by entityplayer, then execute the normal Entityliving code
        if (!isFlyer() && (!rideableEntity() || this.riddenByEntity == null))// || (this.ridingEntity != null && !(this.ridingEntity instanceof EntityPlayer)))
        {
            super.moveEntityWithHeading(strafe_movement, forward_movement);
            return;
        }

        float movement_sideways = strafe_movement;
        float movement_forward = forward_movement;

        if (handleWaterMovement())
        {
            if (riddenByEntity != null)
            {
                motionX += riddenByEntity.motionX * (getCustomSpeed() / 2.0D);
                motionZ += riddenByEntity.motionZ * (getCustomSpeed() / 2.0D);
                movement_sideways = (float) (((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F * (getCustomSpeed() / 2.0D));;
                movement_forward = (float) (((EntityLivingBase)this.riddenByEntity).moveForward * (getCustomSpeed() / 2.0D));

                if (jumpPending && !getIsJumping())
                {
                    motionY = getCustomJump()*2;
                    setIsJumping(true);
                    jumpPending = false;
                }

                if (!worldObj.isRemote)
                {
                    super.moveEntityWithHeading(movement_sideways, movement_forward);
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
                    worldObj.playSoundAtEntity(this, "mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.3D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    this.riddenByEntity = null;
                }

                if (rand.nextInt(25) == 0)
                {
                    setIsJumping(false);
                }
            }

            double y_coordinate = posY;

            if (!worldObj.isRemote)
            {
                moveFlying(strafe_movement, forward_movement, 0.02F);
                //moveEntity(motionX, motionY, motionZ);
                super.moveEntityWithHeading(movement_sideways, movement_forward);
            }
            motionX *= 0.800000011920929D;
            motionY *= 0.800000011920929D;
            motionZ *= 0.800000011920929D;
            motionY -= 0.02D;
            if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, ((motionY + 0.60000002384185791D) - posY) + y_coordinate, motionZ))
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
                movement_sideways = (float) (((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F * (getCustomSpeed() / 2.0D));;
                movement_forward = (float) (((EntityLivingBase)this.riddenByEntity).moveForward * (getCustomSpeed() / 2.0D));

                if (jumpPending && !getIsJumping())
                {
                    motionY = getCustomJump();
                    jumpPending = false;
                }

                //moveEntity(motionX, motionY, motionZ);
                super.moveEntityWithHeading(movement_sideways, movement_forward);

                rotationPitch = riddenByEntity.rotationPitch * 0.5F;
                if (rand.nextInt(20) == 0)
                {
                    rotationYaw = riddenByEntity.rotationYaw;
                }
                setRotation(rotationYaw, rotationPitch);
                if (MoCreatures.isServer() && !getIsTamed())
                {
                    worldObj.playSoundAtEntity(this, "mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.3D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    this.riddenByEntity = null;
                }
            }
            double y_coordinate1 = posY;

            moveFlying(strafe_movement, forward_movement, 0.02F);
            moveEntity(motionX, motionY, motionZ);

            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
            motionY -= 0.02D;
            if (isCollidedHorizontally && isOffsetPositionInLiquid(motionX, ((motionY + 0.60000002384185791D) - posY) + y_coordinate1, motionZ))
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
            moveFlying(strafe_movement, forward_movement, onGround ? 0.1F * f3 : 0.02F);

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
                    motionY = this.getCustomJump();
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
                    worldObj.playSoundAtEntity(this, "mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.9D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    this.riddenByEntity = null;
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
                    movement_sideways = (float) (((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F * getCustomSpeed());
                    movement_forward = (float) (((EntityLivingBase)this.riddenByEntity).moveForward * getCustomSpeed());
                }

                if (jumpPending && (isFlyer()))
                {
                    motionY += flyerThrust();//0.3D;
                    jumpPending = false;

                    if (selfPropelledFlyer() && isOnAir() && MoCreatures.isServer())
                    {
                        float velX = (float) (0.5F * Math.cos((MoCTools.realAngle(this.rotationYaw - 90F)) / 57.29578F));
                        float velZ = (float) (0.5F * Math.sin((MoCTools.realAngle(this.rotationYaw - 90F)) / 57.29578F));
                        this.motionX -= velX;
                        this.motionZ -= velZ;
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
                    super.moveEntityWithHeading(movement_sideways, movement_forward);//, motionZ);
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
                //moveEntity(motionX, motionY, motionZ);
                super.moveEntityWithHeading(movement_sideways, movement_forward);//, motionZ);
            }
            if (isFlyingAlone())
            {
                int distance_from_ground = MoCTools.distanceToFloor(this);
                if (distance_from_ground <= flyingHeight())
                {
                    motionY *= 0.3 + (movement);
                }
                if (distance_from_ground <= flyingHeight() && (isCollidedHorizontally || rand.nextInt(100) == 0))
                {
                    motionY += 0.1D;
                }
                if (distance_from_ground > flyingHeight() || rand.nextInt(150) == 0)
                {
                    motionY -= 0.10D;
                }

                if (isOnAir())
                {
                    double x_velocity = 0.1F * Math.cos((MoCTools.realAngle(this.rotationYaw - 90F)) / 57.29578F);
                    double z_velocity = 0.1F * Math.sin((MoCTools.realAngle(this.rotationYaw - 90F)) / 57.29578F);
                    this.motionX -= x_velocity;
                    this.motionZ -= z_velocity;
                }

                if (motionY < 0)
                {
                    motionY *= 0.5D;
                }
                /*if (MoCreatures.isServer())
                {
                    moveEntity(motionX, motionY, motionZ);
                    //super.moveEntityWithHeading(par1, par2);//, motionZ);
                }*/
            }

            if (isFlyer() && riddenByEntity == null && entityToAttack != null && entityToAttack.posY < this.posY && rand.nextInt(30) == 0)
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

            if ((isFlyer()) && (riddenByEntity != null) && getIsTamed()
            		&& (this instanceof MoCEntityHorse || this instanceof MoCEntityWyvern))
            {
            	if (isFlyer())  //slow fall for flying horses and wyverns
            	{
            		motionY += (0.21D - (myFallSpeed()/10D));//0.15D;
                    motionY *= myFallSpeed();//0.6D
            	}


                if (isOnAir() || !(this instanceof MoCEntityWyvern))  //controls speed for flying mounts when they are flying. Also makes every flyer except wyverns be as fast on the ground as they are in the air
                {
                	movement = ((float) getCustomSpeed())*0.77F;
                }

                //motionX += riddenByEntity.motionX * getCustomSpeed();
               // motionZ += riddenByEntity.motionZ * getCustomSpeed();
            }
            else if (!isFlyingAlone())
            {
                motionY -= 0.08D;
                motionY *= 0.98000001907348633D;
            }

            if (this.riddenByEntity != null && isOnAir() & !isFlyer())
            {
            	movement = flyerFriction();

            }

            motionX *= movement;
            motionZ *= movement;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;

        double x_distance_travelled = posX - prevPosX;
        double z_distance_travelled = posZ - prevPosZ;

        float overall_horizontal_distance_travelled_squared = MathHelper.sqrt_double((x_distance_travelled * x_distance_travelled) + (z_distance_travelled * z_distance_travelled)) * 4.0F;

        if (overall_horizontal_distance_travelled_squared > 1.0F)
        {
            overall_horizontal_distance_travelled_squared = 1.0F;
        }

        this.limbSwingAmount += (overall_horizontal_distance_travelled_squared - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
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
        this.jumpPending = true;
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
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null);
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
        float hunting_range = 16F;

        if (entityToAttack == null)
        {
            entityToAttack = findPlayerToAttack();
            if (entityToAttack != null)
            {
                entitypath = worldObj.getPathEntityToEntity(this, entityToAttack, hunting_range, true, false, false, true);
            }
        }

        // prevent tamed animals attacking other tamed animals
        else if (!entityToAttack.isEntityAlive() || MoCTools.isTamed(entityToAttack)) //((entityToAttack instanceof IMoCTameable) && ((IMoCTameable)entityToAttack).getIsTamed() && this.getIsTamed()))
        {
            entityToAttack = null;
        }

        else
        {
            float distance_to_prey = entityToAttack.getDistanceToEntity(this);
            if (canEntityBeSeen(entityToAttack))
            {
                attackEntity(entityToAttack, distance_to_prey);
            }
        }

        if (!hasAttacked && (entityToAttack != null) && ((entitypath == null) || (rand.nextInt(10) == 0)))
        {
            // entitypath = worldObj.getPathToEntity(this, entityToAttack, f);
            entitypath = worldObj.getPathEntityToEntity(this, entityToAttack, hunting_range, true, false, false, true);
        }

        else if (((entitypath == null) && (rand.nextInt(80) == 0)) || (rand.nextInt(80) == 0))
        {
            boolean flag = false;
            
            int new_posX = -1;
            int new_PosY = -1;
            int new_PosZ = -1;
            
            float minimum_probability = -99999F;
            
            for (int index = 0; index < 10; index++)
            {
                int temp_x = MathHelper.floor_double((posX + rand.nextInt(13)) - 6D);
                int temp_y = MathHelper.floor_double((posY + rand.nextInt(7)) - 3D);
                int temp_z = MathHelper.floor_double((posZ + rand.nextInt(13)) - 6D);
                
                float liklihood_of_entity_walking_to_this_block = getBlockPathWeight(temp_x, temp_y, temp_z);
                
                if (liklihood_of_entity_walking_to_this_block > minimum_probability)
                {
                    minimum_probability = liklihood_of_entity_walking_to_this_block;
                    new_posX = temp_x;
                    new_PosY = temp_y;
                    new_PosZ = temp_z;
                    flag = true;
                }
            }

            if (flag)
            {
                entitypath = worldObj.getEntityPathToXYZ(this, new_posX, new_PosY, new_PosZ, 10F, true, false, false, true);
            }
        }

        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        boolean is_water_movement = handleWaterMovement();
        boolean is_lava_movement = handleLavaMovement();

        rotationPitch = 0.0F;
        if ((entitypath == null) || (rand.nextInt(100) == 0))
        {
            super.updateEntityActionState();
            entitypath = null;
            return;
        }

        Vec3 vector_3D = entitypath.getPosition(this); //client

        //vector_3D vector_3D = entitypath.getPosition(this); //server

        for (double d = width * 2.0F; (vector_3D != null) && (vector_3D.squareDistanceTo(posX, vector_3D.yCoord, posZ) < (d * d));)
        {
            entitypath.incrementPathIndex();

            if (entitypath.isFinished())
            {
                vector_3D = null;
                entitypath = null;
            }
            else
            {
                //vector_3D = entitypath.getPosition(this); //server
                //TODO 4FIX test!
                vector_3D = entitypath.getPosition(this);
            }
        }

        isJumping = false;
        if (vector_3D != null)
        {
            double vector_x_distance = vector_3D.xCoord - posX;
            double vector_y_distance = vector_3D.yCoord - y_coordinate;
            double vector_z_distance = vector_3D.zCoord - posZ;

            float angle_in_degrees_to_new_location = (float) ((Math.atan2(vector_z_distance, vector_x_distance) * 180D) / Math.PI) - 90F;
            float amount_of_degrees_to_change_rotationYaw_by = angle_in_degrees_to_new_location - rotationYaw;

            moveForward = (float) this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();

            for (; amount_of_degrees_to_change_rotationYaw_by < -180F; amount_of_degrees_to_change_rotationYaw_by += 360F)
            {
            }
            for (; amount_of_degrees_to_change_rotationYaw_by >= 180F; amount_of_degrees_to_change_rotationYaw_by -= 360F)
            {
            }
            if (amount_of_degrees_to_change_rotationYaw_by > 30F)
            {
                amount_of_degrees_to_change_rotationYaw_by = 30F;
            }
            if (amount_of_degrees_to_change_rotationYaw_by < -30F)
            {
                amount_of_degrees_to_change_rotationYaw_by = -30F;
            }
            rotationYaw += amount_of_degrees_to_change_rotationYaw_by;
            if (hasAttacked && (entityToAttack != null))
            {
                double x_distance = entityToAttack.posX - posX;
                double z_distance = entityToAttack.posZ - posZ;
                float previous_rotationYaw = rotationYaw;
                rotationYaw = (float) ((Math.atan2(z_distance, x_distance) * 180D) / Math.PI) - 90F;
                float angle_in_degrees_between_previous_and_new_rotationYaw = (((previous_rotationYaw - rotationYaw) + 90F) * (float) Math.PI) / 180F;
                moveStrafing = -MathHelper.sin(angle_in_degrees_between_previous_and_new_rotationYaw) * moveForward * 1.0F;
                moveForward = MathHelper.cos(angle_in_degrees_between_previous_and_new_rotationYaw) * moveForward * 1.0F;
            }
            if (vector_y_distance > 0.0D)
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
        if ((rand.nextFloat() < 0.8F) && (is_water_movement || is_lava_movement))
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

    public void faceItem(int x_coordinate, int y_coordinate, int z_coordinate, float f)
    {
        double x_distance = x_coordinate - posX;
        double y_distance = y_coordinate - posY;
        double z_distance = z_coordinate - posZ;

        double overall_distance_squared = MathHelper.sqrt_double((x_distance * x_distance) + (z_distance * z_distance));

        float xz_angle_in_degrees_to_new_location = (float) ((Math.atan2(z_distance, x_distance) * 180D) / Math.PI) - 90F;
        float y_angle_in_degrees_to_new_location = (float) ((Math.atan2(y_distance, overall_distance_squared) * 180D) / Math.PI);

        rotationPitch = -adjustRotation(rotationPitch, y_angle_in_degrees_to_new_location, f);
        rotationYaw = adjustRotation(rotationYaw, xz_angle_in_degrees_to_new_location, f);
    }

    /**
     * 
     * @param current_rotation
     * @param rotation_adjustment
     * @param rotation_limit
     * @return
     */
    public float adjustRotation(float current_rotation, float rotation_adjustment, float rotation_limit)
    {
        float amount_to_change_rotation_by = rotation_adjustment;
        for (amount_to_change_rotation_by = rotation_adjustment - current_rotation; amount_to_change_rotation_by < -180F; amount_to_change_rotation_by += 360F)
        {
        }
        for (; amount_to_change_rotation_by >= 180F; amount_to_change_rotation_by -= 360F)
        {
        }
        if (amount_to_change_rotation_by > rotation_limit)
        {
            amount_to_change_rotation_by = rotation_limit;
        }
        if (amount_to_change_rotation_by < -rotation_limit)
        {
            amount_to_change_rotation_by = -rotation_limit;
        }
        return current_rotation + amount_to_change_rotation_by;
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
        double x_velocity = this.rand.nextGaussian() * 0.02D;
        double y_velocity = this.rand.nextGaussian() * 0.02D;
        double z_velocity = this.rand.nextGaussian() * 0.02D;

        this.worldObj.spawnParticle("heart", this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, x_velocity, y_velocity, z_velocity);

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
     * @param par1ItemStack
     * @return
     */
    public boolean isMyFollowFood(ItemStack par1ItemStack)
    {
        return false;
    }

    private void followPlayer()
    {
        EntityPlayer closest_entityplayer = worldObj.getClosestPlayerToEntity(this, 24D);

        if (closest_entityplayer == null) { return; }

        ItemStack itemstack_that_player_is_holding = closest_entityplayer.inventory.getCurrentItem();

        if (itemstack_that_player_is_holding != null && isMyFollowFood(itemstack_that_player_is_holding))
        {
            PathEntity pathentity = worldObj.getPathEntityToEntity(this, closest_entityplayer, 16F, true, false, false, true);
            setPathToEntity(pathentity);
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
                if (this.ridingEntity != null)
                {
                    this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double) this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                    this.ridingEntity.riddenByEntity = null;
                }
                this.ridingEntity = null;
            }
            else
            {
                this.ridingEntity = entity;
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
        this.divePending = true;
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
        return this.dataWatcher.getWatchableObjectString(20);
    }

    @Override
    public void setOwner(String par1Str)
    {
        this.dataWatcher.updateObject(20, par1Str);
    }

    @Override
    public void onDeath(DamageSource damagesource)
    {
        if (MoCreatures.isServer())
        {
            dropMyStuff();
        }

        super.onDeath(damagesource);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        Entity entity = damagesource.getEntity();
        //this avoids damage done by Players to a tamed creature that is not theirs
        if (MoCreatures.proxy.enableStrictOwnership && getOwnerName() != null && !getOwnerName().equals("") && entity != null && entity instanceof EntityPlayer && !((EntityPlayer) entity).getCommandSenderName().equals(getOwnerName()) && !MoCTools.isThisPlayerAnOP((EntityPlayer) entity)) { return false; }

        if (isNotScared())
        {
            Entity tempEntity = entityToAttack;

            boolean flag = super.attackEntityFrom(damagesource, i);

            fleeingTick = 0;

            entityToAttack = tempEntity;
            return flag;
        }
        else
        {
            return super.attackEntityFrom(damagesource, i);
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
        if (MoCreatures.isServer() && this.riddenByEntity != null)
        {
            this.riddenByEntity.mountEntity(null);
            this.riddenByEntity = null;
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
        this.riderIsDisconnecting = true;
    }
}