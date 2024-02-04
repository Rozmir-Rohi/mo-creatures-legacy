package drzhark.mocreatures.entity;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.aquatic.MoCEntityMediumFish;
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
    public boolean caught_on_hook;
    protected boolean riderIsDisconnecting;
    protected float moveSpeed;
    protected String texture;
    private boolean has_killed_prey = false;

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
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getMoveSpeed());
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }

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
        return this.dataWatcher.getWatchableObjectString(17);
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

    public float updateRotation(float current_rotation, float intended_rotation, float max_increment)
    {
        float amount_to_change_rotation_by = intended_rotation;
        for (amount_to_change_rotation_by = intended_rotation - current_rotation; amount_to_change_rotation_by < -180F; amount_to_change_rotation_by += 360F)
        {
        }
        for (; amount_to_change_rotation_by >= 180F; amount_to_change_rotation_by -= 360F)
        {
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

    public void faceItem(int x_coordinate, int y_coordinate, int z_coordinate, float f)
    {
        double x_distance = x_coordinate - posX;
        double y_distance = y_coordinate - posY;
        double z_distance = z_coordinate - posZ;
        
        double d3 = MathHelper.sqrt_double((x_distance * x_distance) + (z_distance * z_distance));
        
        float xz_angle_in_degrees_to_new_location = (float) ((Math.atan2(z_distance, x_distance) * 180D) / Math.PI) - 90F;
        float y_angle_in_degrees_to_new_location = (float) ((Math.atan2(y_distance, d3) * 180D) / Math.PI);
        
        rotationPitch = -updateRotation(rotationPitch, y_angle_in_degrees_to_new_location, f);
        rotationYaw = updateRotation(rotationYaw, xz_angle_in_degrees_to_new_location, f);
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
    
    public void onKillEntity(EntityLivingBase entityliving)
    {
    	if (isPredator() && MoCreatures.proxy.destroyDrops)
    	{
    		if (!(entityliving instanceof EntityPlayer) && !(entityliving instanceof EntityMob))
    		{
	    		has_killed_prey = true;
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
        this.jumpPending = true;
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
        float movement_sideways = strafe;
        float movement_forward = forward;

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
                this.ridingEntity = null;
            }
            if (onGround)
            {
                setIsJumping(false);
            }
            if (MoCreatures.isServer() && this instanceof IMoCTameable)
            {
                int chance = (getMaxTemper() - getTemper());
                if (chance <= 0)
                {
                    chance = 1;
                }
                if (rand.nextInt(chance * 8) == 0)
                {
                    MoCTools.tameWithName((EntityPlayer) riddenByEntity, (IMoCTameable) this);
                }

            }
        }
        else if ((riddenByEntity != null) && getIsTamed())// && isSwimming())
        {
            motionX += riddenByEntity.motionX * (getCustomSpeed() / 5.0D);
            motionZ += riddenByEntity.motionZ * (getCustomSpeed() / 5.0D);
            movement_sideways = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
            movement_forward = ((EntityLivingBase)this.riddenByEntity).moveForward;

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
                //moveEntity(motionX, motionY, motionZ);
                super.moveEntityWithHeading(movement_sideways, movement_forward);
            }
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

    protected boolean MoveToNextEntity(Entity entity)
    {
        if (entity != null)
        {
            int entity_posX = MathHelper.floor_double(entity.posX);
            int entity_posY = MathHelper.floor_double(entity.posY);
            int entity_posZ = MathHelper.floor_double(entity.posZ);
            faceItem(entity_posX, entity_posY, entity_posZ, 30F);
            if (posX < entity_posX)
            {
                double distance = entity.posX - posX;
                if (distance > 0.5D)
                {
                    motionX += 0.050000000000000003D;
                }
            }
            else
            {
                double current_minimum_distance = posX - entity.posX;
                if (current_minimum_distance > 0.5D)
                {
                    motionX -= 0.050000000000000003D;
                }
            }
            if (posZ < entity_posZ)
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

    public void floating()
    {
        float distanceY = MoCTools.distanceToSurface(this);

        if (riddenByEntity != null)
        {
            EntityPlayer player_that_is_riding_this_creature = (EntityPlayer) riddenByEntity;
            if (player_that_is_riding_this_creature.isAirBorne) // TODO TEST
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

        if (distanceY < 1 || isDiving())
        {
            if (motionY < -0.05)
            {
                motionY = -0.05;
            }
        }
        else
        {
            if (motionY < 0)
            {
                motionY = 0;
            }
            motionY += 0.001D;// 0.001

            if (distanceY > 1)
            {
                motionY += (distanceY * 0.02);
                if (motionY > 0.2D)
                {
                    motionY = 0.2D;
                }
            }
        }
    }

    // used to pick up objects while riding an entity
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
            if (this.riddenByEntity != null)
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
            }

            if (forceUpdates() && rand.nextInt(500) == 0)
            {
                MoCTools.forceDataSync(this);
            }
            
            if (isFisheable() && !caught_on_hook && rand.nextInt(30) == 0) //makes a fish look for a fishing hook to willingly bite
            {
                lookForHookToGetCaughtOn();
            }
            
            if (caught_on_hook && hook_that_this_fish_is_hooked_to != null)
            {
            	boolean hook_nearby = true;
            	
            	float distance_to_hook1 = hook_that_this_fish_is_hooked_to.getDistanceToEntity(this);
                
            	if (distance_to_hook1 > 2) //tests if the fish has been reeled in by the player
                {
            		hook_nearby = false;
                }
                
                if (!(hook_nearby) && player_that_hooked_this_fish != null && hook_that_this_fish_is_hooked_to != null)
                {
                	if (player_that_hooked_this_fish.inventory.getCurrentItem() != null)  //must check if itemstack isn't null before getItem() else game will crash
                	{
	                	if (player_that_hooked_this_fish.inventory.getCurrentItem().getItem() == Items.fishing_rod)
	                	{
		                	
		                	ItemStack itemstack;
		                	
		                	if (this instanceof MoCEntityMediumFish)
		                	{
		                		MoCEntityMediumFish medium_fish = (MoCEntityMediumFish) this;
		                		
		                		if (medium_fish.getType() == 4) //red salmon
		                		{
		                			itemstack = new ItemStack(Items.fish, 1, 1);
		                		}
		                		else
		                    	{
		                    		itemstack = new ItemStack(Items.fish, 1, 0);
		                    	}
		                	}
		                	else
		                	{
		                		itemstack = new ItemStack(Items.fish, 1, 0);
		                	}
		                	
		                	
		                	
		                	EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, itemstack);
		                    double x_distance = player_that_hooked_this_fish.posX - hook_that_this_fish_is_hooked_to.posX;
		                    double y_distance = player_that_hooked_this_fish.posY - hook_that_this_fish_is_hooked_to.posY;
		                    double z_distance = player_that_hooked_this_fish.posZ - hook_that_this_fish_is_hooked_to.posZ;
		                    
		                    this.setDead();
		                    
		                    double overall_distance_squared = (double) MathHelper.sqrt_double(x_distance * x_distance + y_distance * y_distance + z_distance * z_distance);
		                    double d9 = 0.1D;
		                    
		                    entityitem.motionX = x_distance * d9;
		                    entityitem.motionY = y_distance * d9 + (double )MathHelper.sqrt_double(overall_distance_squared) * 0.08D;
		                    entityitem.motionZ = z_distance * d9;
		                    
		                    worldObj.spawnEntityInWorld(entityitem);
		                    
		                    player_that_hooked_this_fish.worldObj.spawnEntityInWorld(new EntityXPOrb(player_that_hooked_this_fish.worldObj, player_that_hooked_this_fish.posX, player_that_hooked_this_fish.posY + 0.5D, player_that_hooked_this_fish.posZ + 0.5D, rand.nextInt(6) + 1));
		                	
	                	}
                	}
                	
                	else
                	{
                		caught_on_hook = false;
                	}
                }
            }
            

            if (caught_on_hook && rand.nextInt(200) == 0) // unhooks the fish from the fishing hook if the fish is hooked for too long
            {
                caught_on_hook = false;
                
                List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(2, 2, 2));
                
                int iteration_length = entities_nearby_list.size();
                
                if (iteration_length > 0)
                {
                	for (int index = 0; index < iteration_length; index++)
                    {
                        Entity entity_nearby = (Entity) entities_nearby_list.get(index);
            
                        if (entity_nearby instanceof EntityFishHook)
                        {
                            if (((EntityFishHook) entity_nearby).field_146043_c == this)
                            {
                                ((EntityFishHook) entity_nearby).field_146043_c = null;
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
            floating();
            outOfWater = 0;
            this.setAir(300);
        }
        else
        {
            if (this.riddenByEntity != null)
            {
                if (this.riddenByEntity.isSneaking())
                {
                    this.riddenByEntity.mountEntity(null);
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
        EntityPlayer distance_to_player = this.worldObj.getClosestPlayerToEntity(this, -1.0D);
        if (distance_to_player != null)
        {
            double x_distance = distance_to_player.posX - this.posX;
            double y_distance = distance_to_player.posY - this.posY;
            double z_distance = distance_to_player.posZ - this.posZ;
            
            double overall_distance_squared = x_distance * x_distance + y_distance * y_distance + z_distance * z_distance;

            if (this.canDespawn() && overall_distance_squared > 16384.0D)
            {
                this.setDead();
            }
            //changed from 600
            if (entityAge > 1800 && rand.nextInt(800) == 0 && overall_distance_squared > 1024.0D && canDespawn())
            {
                this.setDead();
            }
            else if (overall_distance_squared < 1024.0D)
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
        this.divePending = true;
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
        return MoCreatures.entityMap.get(this.getClass()).getFrequency() > 0 && this.worldObj.checkNoEntityCollision(this.boundingBox);
    }

    @Override
    public String getOwnerName()
    {
        return this.dataWatcher.getWatchableObjectString(21);
    }

    @Override
    public void setOwner(String par1Str)
    {
        this.dataWatcher.updateObject(21, par1Str);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        Entity entity = damagesource.getEntity();
        //this avoids damage done by Players to a tamed creature that is not theirs
        if (MoCreatures.proxy.enableStrictOwnership && getOwnerName() != null && !getOwnerName().equals("") && entity != null && entity instanceof EntityPlayer && !((EntityPlayer) entity).getCommandSenderName().equals(getOwnerName()) && !MoCTools.isThisPlayerAnOP(((EntityPlayer) entity))) { return false; }

        if (isFisheable()) //tests if the fish has been force hooked by a player throwing a fishing hook at them
        {
	        if (entity != null)
	        {
	        	if (entity instanceof EntityPlayer)
	        	{
	        		if (((EntityPlayer) entity).inventory.getCurrentItem() != null) //must check if itemstack isn't null before getItem() else game will crash
	        		{
		        		if (((EntityPlayer) entity).inventory.getCurrentItem().getItem() == Items.fishing_rod)
		        		{
		        			lookForHookToGetCaughtOn(); //tests if there is a fishing hook nearby, if so sets the fish as caught on a hook
		        		}
	        		}
	        	}
	        }
    	}
        
        //to prevent tamed aquatics from getting block damage
        if (getIsTamed() && damagesource.getDamageType().equalsIgnoreCase("inWall"))
        {
            return false;
        }
        /*if (MoCreatures.isServer() && getIsTamed())
        {
            //MoCServerPacketHandler.sendHealth(this.getEntityId(), this.worldObj.provider.dimensionId, this.getHealth());
        }*/

        return super.attackEntityFrom(damagesource, i);

    }



    protected boolean canBeTrappedInNet() 
    {
        return (this instanceof IMoCTameable) && getIsTamed();
    }

    protected void dropMyStuff() {
        // TODO Auto-generated method stub
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
        if (MoCreatures.isServer() && this.riddenByEntity != null)
        {
            this.riddenByEntity.mountEntity(null);
            this.riddenByEntity = null;
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
    
    EntityPlayer player_that_hooked_this_fish;
    
    EntityFishHook hook_that_this_fish_is_hooked_to;
    
    /**
     * The act of getting Hooked into a fish Hook.
     */
    private void lookForHookToGetCaughtOn()
    {
        EntityPlayer closest_entityplayer = worldObj.getClosestPlayerToEntity(this, 18D);
        
        if (closest_entityplayer != null)
        {
            EntityFishHook fishHook = closest_entityplayer.fishEntity;
            
            if (fishHook != null)
            {
            	if (fishHook.field_146043_c == null) //hooked by fish willingly biting the fish hook
            	{
	                float distance_to_hook = fishHook.getDistanceToEntity(this);
	                
	                if (distance_to_hook > 1)
	                {
	                    MoCTools.getPathToEntity(this, fishHook, distance_to_hook);
	                }
	                else
	                {
	                    fishHook.field_146043_c = this;
	                    caught_on_hook = true;
	                    
	                    if (outOfWater == 0) //if in water
	                    {
	                    	fishHook.motionY -= 0.20000000298023224D;
		                    playSound("random.splash", 0.25F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
	                    }
	                    
	                    player_that_hooked_this_fish = closest_entityplayer;
	                    
	                    hook_that_this_fish_is_hooked_to = fishHook;
	                    
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
        double current_minimum_distance = -1D;
        
        EntityLivingBase entityliving = null;
        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, 4D, d));
        
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
        return ( (entity.getClass() != this.getClass()) && (entity instanceof EntityLivingBase) && ((entity.width >= 0.5D) || (entity.height >= 0.5D)));
    }

    public boolean isNotScared()
    {
        return false;
    }

    @Override
    public void riderIsDisconnecting(boolean flag)
    {
        this.riderIsDisconnecting = true;
    }
}