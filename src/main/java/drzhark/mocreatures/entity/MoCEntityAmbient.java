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

    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture(texture);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(getMaxHealth());
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
        if (isSwimming() && swimmerEntity())
        {
            floating();
        }

        moveSpeed = getMoveSpeed();
        super.onLivingUpdate();
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

        int distance_to_surface_of_water = (int) MoCTools.distanceToSurface(this);
        
        if (distance_to_surface_of_water > 1)
        {
            motionY += (distance_to_surface_of_water * 0.07);
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

    public EntityItem getClosestItem(Entity entity, double distance, ItemStack item, ItemStack item1)
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
	            
	            if ((entityitem_nearby.getEntityItem() != item) && (entityitem_nearby.getEntityItem() != item1))
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
            
            int interation_length = entities_nearby_list.size();
            
            if (interation_length > 0)
            {
	            if (entities_nearby_list != null)
	            {
	                for (int index = 0; index < interation_length; index++)
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
	                    
	                    if ((distance < 2.0F) && (rand.nextInt(10) == 0))
	                    {
	                        //TODO 4FIX
	                        //attackEntityFrom(DamageSource.causeMobDamage((EntityLiving) entity),((EntityMob) entity).attackStrength);
	                    }
	                }
	            }
            }
            if (entityplayer.isSneaking())
            {
                if (!worldObj.isRemote)
                {
                    entityplayer.mountEntity(null);
                }
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

    public boolean getCanSpawnHereAnimal()
    {
        int x_coordinate = MathHelper.floor_double(posX);
        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        int z_coordinate = MathHelper.floor_double(posZ);
        
        return worldObj.getBlock(x_coordinate, y_coordinate - 1, z_coordinate) == Blocks.grass && worldObj.getFullBlockLightValue(x_coordinate, y_coordinate, z_coordinate) > 8;
    }

    public boolean getCanSpawnHereCreature()
    {
        int x_coordinate = MathHelper.floor_double(posX);
        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        int z_coordinate = MathHelper.floor_double(posZ);
        
        return getBlockPathWeight(x_coordinate, y_coordinate, z_coordinate) >= 0.0F;
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
        if (MoCreatures.entityMap.get(this.getClass()).getFrequency() <= 0) {return false;}
        
        int x_coordinate = MathHelper.floor_double(posX);
        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        int z_coordinate = MathHelper.floor_double(posZ);

        String biome_name = MoCTools.BiomeName(worldObj, x_coordinate, y_coordinate, z_coordinate);

        if (biome_name.equals("Jungle") || biome_name.equals("JungleHills")) { return getCanSpawnHereJungle(); }

        return super.getCanSpawnHere();
    }

    public boolean getCanSpawnHereJungle()
    {
        if (this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(this.boundingBox))
        {
            int x_coordinate = MathHelper.floor_double(this.posX);
            int y_coordinate = MathHelper.floor_double(this.boundingBox.minY);
            int z_coordinate = MathHelper.floor_double(this.posZ);

            if (y_coordinate < 63) { return false; }

            Block block = this.worldObj.getBlock(x_coordinate, y_coordinate - 1, z_coordinate);

            if (
            		block == Blocks.grass
            		|| block == Blocks.leaves
            		|| (block != null && block.isLeaves(worldObj, x_coordinate, y_coordinate - 1, z_coordinate))
            	) 
            {
            	return true;
            }
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
        if (!isFlyer() && (!rideableEntity() || this.riddenByEntity == null))
        {
            super.moveEntityWithHeading(strafe_movement, forward_movement);
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
                    worldObj.playSoundAtEntity(this, "mocreatures:" + getMadSound(), 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                    riddenByEntity.motionY += 0.3D;
                    riddenByEntity.motionZ -= 0.3D;
                    riddenByEntity.mountEntity(null);
                    this.riddenByEntity = null;
                }
            }
            double y_coordinate = posY;
            if (!worldObj.isRemote)
            {
                moveFlying(strafe_movement, forward_movement, 0.02F);
                moveEntity(motionX, motionY, motionZ);
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
                
                moveEntity(motionX, motionY, motionZ);

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
                    double x_velocity = 0.05F * Math.cos((MoCTools.realAngle(this.rotationYaw - 90F)) / 57.29578F);
                    double z_velocity = 0.05F * Math.sin((MoCTools.realAngle(this.rotationYaw - 90F)) / 57.29578F);
                    this.motionX -= x_velocity;
                    this.motionZ -= z_velocity;
                }
            }

            if (isFlyer() && riddenByEntity == null && entityToAttack != null && entityToAttack.posY < this.posY && rand.nextInt(30) == 0)
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
            
            if (this.riddenByEntity != null && isOnAir())
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
    public boolean renderName()
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
        return this.dataWatcher.getWatchableObjectString(20);
    }

    @Override
    public void setOwner(String par1Str)
    {
        this.dataWatcher.updateObject(20, par1Str);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        Entity entity = damagesource.getEntity();
        //this avoids damage done by Players to a tamed creature that is not theirs
        if (MoCreatures.proxy.enableStrictOwnership && getOwnerName() != null && !getOwnerName().equals("") && entity != null && entity instanceof EntityPlayer && !((EntityPlayer) entity).getCommandSenderName().equals(getOwnerName()) && !MoCTools.isThisPlayerAnOP(((EntityPlayer) entity))) { return false; }

        if (MoCreatures.isServer() && getIsTamed())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHealth(this.getEntityId(),  this.getHealth()), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
        }
        return super.attackEntityFrom(damagesource, i);
    }

    public boolean getIsRideable() 
    {    
        return false;
    }

    public void setRideable(boolean b) {}

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
	
	            if (entitiesToIgnore(entity_nearby))
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

    public boolean entitiesToIgnore(Entity entity)
    {
        return (
	        		!(entity instanceof EntityLiving)
	                || (entity instanceof EntityMob) 
	                || (entity instanceof EntityPlayer && this.getIsTamed()) 
	                || (entity instanceof MoCEntityKittyBed) 
	                || (entity instanceof MoCEntityLitterBox) 
	                || (this.getIsTamed() && (entity instanceof MoCEntityAnimal && ((MoCEntityAnimal) entity).getIsTamed())) 
	                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)) 
	                || ((entity instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
	                || (entity.width > this.width && entity.height > this.height)
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
        double current_minimum_distance = -1D;
        EntityLivingBase entityliving = null;
        
        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, 4D, d));
        
        int iteration_length = entities_nearby_list.size();
        
        for (int index = 0; index < iteration_length; index++)
        {
            Entity entity_nearby = (Entity) entities_nearby_list.get(index);
            
            if (entitiesThatAreScary(entity_nearby))
            {
                entityliving = (EntityLivingBase) entity_nearby;
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
        return (
        			(entity instanceof EntityLivingBase)
        			&& ((entity.width >= 0.5D) || (entity.height >= 0.5D))
        		);
    }

    public void runLikeHell(Entity entity)
    {
        double x_distance = posX - entity.posX;
        double z_distance = posZ - entity.posZ;
        
        double angle_in_radians_to_new_location = Math.atan2(x_distance, z_distance);
        angle_in_radians_to_new_location += (rand.nextFloat() - rand.nextFloat()) * 0.75D;
        
        double temp_new_posX = posX + (Math.sin(angle_in_radians_to_new_location) * 8D);
        double temp_new_posZ = posZ + (Math.cos(angle_in_radians_to_new_location) * 8D);
        
        int temp1_new_posX = MathHelper.floor_double(temp_new_posX);
        int temp1_new_posY = MathHelper.floor_double(boundingBox.minY);
        int temp1_new_posZ = MathHelper.floor_double(temp_new_posZ);
        
        int index = 0;
        
        do
        {
            if (index >= 16)
            {
                break;
            }
            
            int new_posX = (temp1_new_posX + rand.nextInt(4)) - rand.nextInt(4);
            int new_posY = (temp1_new_posY + rand.nextInt(3)) - rand.nextInt(3);
            int new_posZ = (temp1_new_posZ + rand.nextInt(4)) - rand.nextInt(4);
            
            if (
            		(new_posY > 4) 
            		&& ((worldObj.isAirBlock(new_posX, new_posY, new_posZ)) || (worldObj.getBlock(new_posX, new_posY, new_posZ) == Blocks.snow))
            		&& (!worldObj.isAirBlock(new_posX, new_posY - 1, new_posZ))
            	)
            {
                PathEntity pathentity = worldObj.getEntityPathToXYZ(this, new_posX, new_posY, new_posZ, 16F, true, false, false, true);
                setPathToEntity(pathentity);
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
        this.riderIsDisconnecting = true;
    }
}