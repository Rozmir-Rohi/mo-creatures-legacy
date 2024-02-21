package drzhark.mocreatures.entity.animal;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;


public class MoCEntitySnake extends MoCEntityTameableAnimal {

    private float tongueState;
    private float mouthState;
    private boolean isBiting;
    private int rattleState;
    private boolean isPissed;
    private int hissCounter;

    private int moveInt;
    private boolean isNearPlayer;
    public float bodyswing;

    public static final String snakeNames[] = { "Dark", "Spotted", "Orange", "Green", "Coral", "Cobra", "Rattle", "Python" };

    public MoCEntitySnake(World world)
    {
        super(world);
        setSize(1.4F, 0.5F);
        bodyswing = 2F;
        moveInt = rand.nextInt(10);
        setMoCAge(50 + rand.nextInt(50));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }
    
    @Override
    public boolean doesForageForFood()
    {
    	return true;
    }

    @Override
    public void selectType()
    {
        // snake types:
        // 1 small blackish/dark snake (passive)
        // 2 dark green /brown snake (passive)
        // 3 bright orangy snake aggressive venomous
        // 4 bright green snake aggressive venomous
        // 5 coral (aggressive - venomous) 
        // 6 cobra (aggressive - venomous - spitting)
        // 7 rattlesnake (aggressive only if player gets too close - venomous)
        // 8 python (aggressive - non venomous)
        // 9 sea snake (aggressive - venomous)
    	
    	
    	checkSpawningBiome(); //apply type based on biome it spawns in
    	
        if (getType() == 0)
        {
            setType(rand.nextInt(8)+1); //if snake is still type 0, make it a random type
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
            case 1:
                return MoCreatures.proxy.getTexture("snake1.png");
            case 2:
                return MoCreatures.proxy.getTexture("snake2.png");
            case 3:
                return MoCreatures.proxy.getTexture("snake3.png");
            case 4:
                return MoCreatures.proxy.getTexture("snake4.png");
            case 5:
                return MoCreatures.proxy.getTexture("snake5.png");
            case 6:
                return MoCreatures.proxy.getTexture("snake6.png");
            case 7:
                return MoCreatures.proxy.getTexture("snake7.png");
            case 8:
                return MoCreatures.proxy.getTexture("snake8.png");
            default:
                return MoCreatures.proxy.getTexture("snake1.png");
        }
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.6F;
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    public boolean isOnLadder()
    {
        return isCollidedHorizontally;
    }

    @Override
    // snakes can't jump
    protected void jump()
    {
    }

    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && ticksExisted > 2400;
    }

    public boolean pickedUp()
    {
        return (ridingEntity != null);
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {

        if (entityPlayer.riddenByEntity != null && entityPlayer.riddenByEntity instanceof MoCEntityMouse)
        {
        	entityPlayer.riddenByEntity.setDead();
        	
        	MoCTools.playCustomSound(this, "eating", worldObj);
        	
        	heal(5);
        	
        	entityPlayer.addStat(MoCAchievements.feed_snake_with_live_mouse, 1);
        	
        	return false;
        	
        }
        if (super.interact(entityPlayer)) { return false; }
        if (!getIsTamed()) { return false; }

        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        
        if (itemstack == null)
        {
	        rotationYaw = entityPlayer.rotationYaw;
	        if (ridingEntity == null)
	        {
	            if (
	            		MoCreatures.isServer()
	            		&&
	            		(
	                		(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
	                		|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
	                	)
	                	&& !(entityPlayer.isSneaking()) && (entityPlayer.ridingEntity == null)
	                )
	            {
	                mountEntity(entityPlayer);
	            }
	        }
	        else
	        {
	            if (MoCreatures.isServer())
	            {
	                mountEntity(null);
	            }
	        }
	        motionX = entityPlayer.motionX * 5D;
	        motionY = (entityPlayer.motionY / 2D) + 0.5D;
	        motionZ = entityPlayer.motionZ * 5D;
	        return true;
        }
        return false;
    }

    @Override
    public boolean isNotScared()
    {
        // TODO depending on size!
        if ((getType() > 2 && getMoCAge() > 50) || getType()== 7) { return true; }
        else {return false;}
    }

    /**
     * returns true when is climbing up
     * 
     * @return
     */
    public boolean isClimbing()
    {
        return isOnLadder() && motionY > 0.01F;
    }

    public boolean isResting()
    {
        return (!getNearPlayer() && onGround && (motionX < 0.01D && motionX > -0.01D) && (motionZ < 0.01D && motionZ > -0.01D));
    }

    public boolean getNearPlayer()
    {
        return isNearPlayer;
    }

    public int getMoveInt()
    {
        return moveInt;
    }

    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    public void setNearPlayer(boolean flag)
    {
        isNearPlayer = flag;
        if (flag == false) {rattleState = 0;}
    }

    /*@Override
    public double getYOffset()
    {
        // If we are in SMP, do not alter offset on any client other than the player being mounted on
        if (((ridingEntity instanceof EntityPlayer) && !worldObj.isRemote) || ridingEntity == MoCreatures.proxy.getPlayer())//MoCProxy.mc().thePlayer)
        {
            return(yOffset - 1.5F);
        }
        else
        {
            return yOffset;
        }
    }*/

    @Override
    public double getYOffset()
    {
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer()) { return (yOffset - 1.5F); }

        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            return (yOffset + 0.1F);
        }
        else
        {
            return yOffset;
        }
    }

    public float getSizeF()
    {
        float factor = 1.0F;

        if (getType() == 1 || getType() == 2)// small shy snakes
        {
            factor = 0.8F;
        }
        else if (getType() == 5)// coral
        {
            factor = 0.6F;
        }
        if (getType() == 6)// cobra 1.1
        {
            factor = 1.1F;
        }
        if (getType() == 7)// rattlesnake
        {
            factor = 0.9F;
        }
        if (getType() == 8)// python
        {
            factor = 1.5F;
        }

        return getMoCAge() * 0.01F * factor;// */
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (getMoCAge() < 100 && rand.nextInt(500) == 0)
        {
            setMoCAge(getMoCAge() + 1);
        }

        if (pickedUp())
        {
            moveInt = 0;
        }

        if (isResting())
        {

            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;

        }

        if (!onGround && (ridingEntity != null))
        {
            rotationYaw = ridingEntity.rotationYaw;// -90F;
        }

        if (getTongueState() != 0.0F)
        {
            setTongueState(getTongueState() + 0.2F);
            if (getTongueState() > 8.0F)
            {
                setTongueState(0.0F);
            }
        }
        
        

        if (worldObj.difficultySetting.getDifficultyId() > 0 && getNearPlayer() && !getIsTamed() && isNotScared())
        {

            hissCounter++;

            // TODO synchronize and get sound
            // hiss
            if (hissCounter % 25 == 0)
            {
                setMouthState(0.3F);
                worldObj.playSoundAtEntity(this, "mocreatures:snakeupset", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }
            if (hissCounter % 35 == 0)
            {
                setMouthState(0.0F);
            }

            if (hissCounter > 100 && rand.nextInt(50) == 0)
            {
                // then randomly get pissed
                setPissed(true);
                hissCounter = 0;
            }
        }
        
        if (hissCounter > 500)
        {
            hissCounter = 0;
        }

        if (getMouthState() != 0.0F && hissCounter == 0) //biting
        {
            setMouthState(getMouthState() + 0.1F);
            if (getMouthState() > 0.5F)
            {
                setMouthState(0.0F);
            }
        }
        
        if ((getType() == 7) && getRattleState() != 0 && !getIsTamed()) //rattling
        {
        	rattleState += 1;
        	
        	setPissed(true);
                
            if (rattleState % 20 == 0 )
            {
            	worldObj.playSoundAtEntity(this, "mocreatures:snakerattle", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }
            if (getRattleState() > 40)
            {
            	setRattleState(0);
            }
        }
    }

    /**
     * from 0.0 to 4.0F 0.0 = inside mouth 2.0 = completely stuck out 3.0 =
     * returning 4.0 = in.
     * 
     * @return
     */
    public float getTongueState()
    {
        return tongueState;
    }

    public void setTongueState(float toungeStateToSet)
    {
        tongueState = toungeStateToSet;
    }

    public float getMouthState()
    {
        return mouthState;
    }

    public void setMouthState(float mouthStateToSet)
    {
        mouthState = mouthStateToSet;
    }

    public float getRattleState()
    {
        return rattleState;
    }

    public void setRattleState(int rattleStateToSet)
    {
        rattleState = rattleStateToSet;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        /**
         * stick tongue
         */
        if (rand.nextInt(50) == 0 && getTongueState() == 0.0F)
        {
            setTongueState(0.1F);
        }

        /**
         * Open mouth
         */
        if (rand.nextInt(100) == 0 && getMouthState() == 0.0F)
        {
            setMouthState(0.1F);
        }

        if (getType() == 7)
        {
            int chance = 0;
            if (getNearPlayer())
            {
                chance = 30;
            }
            else
            {
                chance = 100;
            }

            if (rand.nextInt(chance) == 0)
            {
                setRattleState(1);
            }
        }
        /**
         * change in movement pattern
         */
        if (!isResting() && !pickedUp() && rand.nextInt(50) == 0)
        {
            moveInt = rand.nextInt(10);
        }

        /**
         * Biting animation
         */
        if (isBiting())
        {
            bodyswing -= 0.5F;
            setMouthState(0.3F);

            if (bodyswing < 0F)
            {
                worldObj.playSoundAtEntity(this, "mocreatures:snakesnap", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                bodyswing = 2.5F;
                setMouthState(0.0F);
                setBiting(false);
            }
        }

        /**
         * this stops chasing the target randomly
         */
        if (entityToAttack != null && rand.nextInt(100) == 0)
        {
            entityToAttack = null;
        }

        /**
         * Follow player that is carrying a mice
         * 
         */
        EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 12D);
        if (entityPlayer1 != null)
        {
            double playerDistanceToSnake = MoCTools.getSqDistanceTo(entityPlayer1, posX, posY, posZ);
            
            
            if (isNotScared())
            {
            	if (getType() == 7 && playerDistanceToSnake <= 8D)  //distance for rattle snake to only rattle as a warning to player
            	{
            		setNearPlayer(true);
            		faceEntity(entityPlayer1, 8F, 8F); //stare at player
            	}
            	else if (getType() != 7 && playerDistanceToSnake <= 5D) {setNearPlayer(true);}
                else {setNearPlayer(false);}

                if (entityPlayer1.riddenByEntity != null && (entityPlayer1.riddenByEntity instanceof MoCEntityMouse || entityPlayer1.riddenByEntity instanceof MoCEntityBird))
                {
                    PathEntity pathEntity = worldObj.getPathEntityToEntity(this, entityPlayer1, 16F, true, false, false, true);
                    setPathToEntity(pathEntity);
                    setPissed(false);
                    hissCounter = 0;
                }
            }
            else
            {
                setNearPlayer(false);
                if (playerDistanceToSnake < 2D && !getIsTamed() && getType() != 7)
                {
                    fleeingTick = 40;
                }

            }

        }
        else
        {
            setNearPlayer(false);
        }
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {

        if ((getType() < 3 || getIsTamed()) && entity instanceof EntityPlayer)
        {
            entityToAttack = null;
            return;
        }

        // attack only after hissing/rattling!
        if (!isPissed() && !getIsTamed()) { return; }

        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            setBiting(true);
            attackTime = 20;

            // venom!
            if (rand.nextInt(2) == 0 && entity instanceof EntityPlayer && getType() > 2 && getType() < 8)
            {
                ((EntityPlayer) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 120, 0));
            }

            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    public void performAnimation(int i)
    {
        setBiting(true);
    }

    public boolean isBiting()
    {
        return isBiting;
    }

    public void setBiting(boolean flag)
    {
        if (flag && MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        isBiting = flag;
    }

    public boolean isPissed()
    {
        return isPissed;
    }

    public void setPissed(boolean isPissed)
    {
        this.isPissed = isPissed;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
    	if (MoCreatures.isServer())
        {
        	if (ridingEntity != null && 
        			(damageSource.getEntity() == ridingEntity || DamageSource.inWall.equals(damageSource)))
            {
         	   return false;
            }
        	
        	else if (getType() < 3) { return super.attackEntityFrom(damageSource, damageTaken); }

        	else if (super.attackEntityFrom(damageSource, damageTaken))
            {
                Entity entityThatAttackedThisCreature = damageSource.getEntity();
                
                if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName()))))
                { 
                	return false; 
                }

                if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
                if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
                {
                    setPissed(true);
                    entityToAttack = entityThatAttackedThisCreature;
                }
                return true;
            }
        	
            else
            {
                return false;
            }
        }
    	return true;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if (worldObj.difficultySetting.getDifficultyId() > 0)
        {
            EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 4D);
            if (!getIsTamed() && (entityPlayer != null)) // && getIsAdult() )
            {
                if (isNotScared() && isPissed()) { return entityPlayer; }
            }
            if ((rand.nextInt(100) == 0))
            {
                EntityLivingBase entityLiving = getClosestEntityLiving(this, 8D);
                return entityLiving;
            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
        {
	        if (getIsTamed() && ridingEntity == null) //defend owner if they are attacked by an entity
	    	{
	    		EntityPlayer ownerOfEntityThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(getOwnerName());
	    		
	    		if (ownerOfEntityThatIsOnline != null)
	    		{
	    			double distanceToOwner = MoCTools.getSqDistanceTo(this, ownerOfEntityThatIsOnline.posX, ownerOfEntityThatIsOnline.posY, ownerOfEntityThatIsOnline.posZ);
	    			
	    			if (distanceToOwner < 20.0D)
	    			{
		    			EntityLivingBase entityThatAttackedOwner = ownerOfEntityThatIsOnline.getAITarget();
		    			
		    			if (entityThatAttackedOwner != null)
		    			{
		    				return entityThatAttackedOwner;
		    			}
	    			}
	    		}
	    	}
        }    
        return null;
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        if (getMoCAge() > 60)
        {
            int j = rand.nextInt(3);
            for (int l = 0; l < j; l++)
            {

                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, getType() + 20), 0.0F);
            }
        }
    }

    // ignores big entities, everything else is prey!
    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return ((super.shouldEntityBeIgnored(entity))
        		|| (entity instanceof MoCEntitySnake)
        		|| (entity.height > 0.5D && entity.width > 0.5D));
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:snakedying";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:snakehurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:snakehiss";
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return (checkSpawningBiome() && getCanSpawnHereCreature() && getCanSpawnHereLiving());
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        String biomeName = MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        int typeChance = rand.nextInt(10);


        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SANDY))
        {
            if (typeChance < 5)
            {
                setType(7); // rattlesnake
            }
            else
            {
                setType(2); // dark green/brown snake
            }
        }


        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.FOREST))
        {
            if (typeChance < 4)
            {
                setType(4); //bright green snake
            }
            else
            {
                setType(1); //small blackish snake
            }
        }
        
        
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.JUNGLE))
        {
            if (typeChance < 4)
            {
                setType(3); //bright orange snake
            }
            else if (typeChance < 7)
            {
                setType(4); //bright green snake
            }
            else
            {
                setType(6); //cobra
            }
        }
        
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SWAMP))
        {
            if (typeChance < 4)
            {
                setType(8); //python
            }
            else if (typeChance < 8)
            {
                setType(2); // dark green/brown snake
            }
            else
            {
                setType(1); // small blackish snake
            }
        }

        return true;
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
        return -20;
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return itemstack != null && 
        	(
        		itemstack.getItem() == MoCreatures.ratRaw
        		|| MoCreatures.isGregTech6Loaded &&
                	(	
                		OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodScrapmeat"
                	)
        	);
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 2;
    }
}