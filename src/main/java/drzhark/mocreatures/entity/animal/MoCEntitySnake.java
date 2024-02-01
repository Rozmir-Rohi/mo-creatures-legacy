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

    private float fTongue;
    private float fMouth;
    private boolean isBiting;
    private int fRattle;
    private boolean isPissed;
    private int hissCounter;

    private int movInt;
    private boolean isNearPlayer;
    public float bodyswing;

    public static final String snakeNames[] = { "Dark", "Spotted", "Orange", "Green", "Coral", "Cobra", "Rattle", "Python" };

    public MoCEntitySnake(World world)
    {
        super(world);
        setSize(1.4F, 0.5F);
        bodyswing = 2F;
        movInt = rand.nextInt(10);
        setMoCAge(50 + rand.nextInt(50));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
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
        return !getIsTamed() && this.ticksExisted > 2400;
    }

    public boolean pickedUp()
    {
        return (this.ridingEntity != null);
    }

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {

        if (entityplayer.riddenByEntity != null && entityplayer.riddenByEntity instanceof MoCEntityMouse)
        {
        	entityplayer.riddenByEntity.setDead();
        	
        	MoCTools.playCustomSound(this, "eating", worldObj);
        	
        	heal(5);
        	
        	entityplayer.addStat(MoCAchievements.feed_snake_with_live_mouse, 1);
        	
        	return false;
        	
        }
        if (super.interact(entityplayer)) { return false; }
        if (!getIsTamed()) { return false; }

        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        
        if (itemstack == null)
        {
	        rotationYaw = entityplayer.rotationYaw;
	        if (this.ridingEntity == null)
	        {
	            if (MoCreatures.isServer() && (entityplayer.ridingEntity == null))
	            {
	                mountEntity(entityplayer);
	            }
	        }
	        else
	        {
	            if (MoCreatures.isServer())
	            {
	                this.mountEntity(null);
	            }
	        }
	        motionX = entityplayer.motionX * 5D;
	        motionY = (entityplayer.motionY / 2D) + 0.5D;
	        motionZ = entityplayer.motionZ * 5D;
	        return true;
        }
        return false;
    }

    @Override
    public boolean isNotScared()
    {
        // TODO depending on size!
        if ((getType() > 2 && getMoCAge() > 50) || this.getType()== 7) { return true; }
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

    public int getMovInt()
    {
        return movInt;
    }

    @Override
    public boolean swimmerEntity()
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
        if (flag == false) {this.fRattle = 0;}
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

        return this.getMoCAge() * 0.01F * factor;// */
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
            movInt = 0;
        }

        if (isResting())
        {

            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;

        }

        if (!onGround && (ridingEntity != null))
        {
            rotationYaw = ridingEntity.rotationYaw;// -90F;
        }

        if (getfTongue() != 0.0F)
        {
            setfTongue(getfTongue() + 0.2F);
            if (getfTongue() > 8.0F)
            {
                setfTongue(0.0F);
            }
        }
        
        

        if (worldObj.difficultySetting.getDifficultyId() > 0 && getNearPlayer() && !getIsTamed() && isNotScared())
        {

            hissCounter++;

            // TODO synchronize and get sound
            // hiss
            if (hissCounter % 25 == 0)
            {
                setfMouth(0.3F);
                worldObj.playSoundAtEntity(this, "mocreatures:snakeupset", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }
            if (hissCounter % 35 == 0)
            {
                setfMouth(0.0F);
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

        if (getfMouth() != 0.0F && hissCounter == 0) //biting
        {
            setfMouth(getfMouth() + 0.1F);
            if (getfMouth() > 0.5F)
            {
                setfMouth(0.0F);
            }
        }
        
        if ((this.getType() == 7) && getfRattle() != 0 && !getIsTamed()) //rattling
        {
        	this.fRattle += 1;
        	
        	setPissed(true);
                
            if (this.fRattle % 20 == 0 )
            {
            	worldObj.playSoundAtEntity(this, "mocreatures:snakerattle", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }
            if (getfRattle() > 40)
            {
            	setfRattle(0);
            }
        }
    }

    /**
     * from 0.0 to 4.0F 0.0 = inside mouth 2.0 = completely stuck out 3.0 =
     * returning 4.0 = in.
     * 
     * @return
     */
    public float getfTongue()
    {
        return fTongue;
    }

    public void setfTongue(float fTongue)
    {
        this.fTongue = fTongue;
    }

    public float getfMouth()
    {
        return fMouth;
    }

    public void setfMouth(float fMouth)
    {
        this.fMouth = fMouth;
    }

    public float getfRattle()
    {
        return fRattle;
    }

    public void setfRattle(int fRattle)
    {
        this.fRattle = fRattle;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        /**
         * stick tongue
         */
        if (rand.nextInt(50) == 0 && getfTongue() == 0.0F)
        {
            setfTongue(0.1F);
        }

        /**
         * Open mouth
         */
        if (rand.nextInt(100) == 0 && getfMouth() == 0.0F)
        {
            setfMouth(0.1F);
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
                setfRattle(1);
            }
        }
        /**
         * change in movement pattern
         */
        if (!isResting() && !pickedUp() && rand.nextInt(50) == 0)
        {
            movInt = rand.nextInt(10);
        }

        /**
         * Biting animation
         */
        if (isBiting())
        {
            bodyswing -= 0.5F;
            setfMouth(0.3F);

            if (bodyswing < 0F)
            {
                worldObj.playSoundAtEntity(this, "mocreatures:snakesnap", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                bodyswing = 2.5F;
                setfMouth(0.0F);
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
        EntityPlayer entityplayer1 = worldObj.getClosestPlayerToEntity(this, 12D);
        if (entityplayer1 != null)
        {
            double player_distance_to_snake = MoCTools.getSqDistanceTo(entityplayer1, posX, posY, posZ);
            
            
            if (isNotScared())
            {
            	if (this.getType() == 7 && player_distance_to_snake <=8D)  //distance for rattle snake to only rattle as a warning to player
            	{
            		setNearPlayer(true);
            		this.faceEntity(entityplayer1, 8F, 8F); //stare at player
            	}
            	else if (this.getType() != 7 && player_distance_to_snake <= 5D) {setNearPlayer(true);}
                else {setNearPlayer(false);}

                if (entityplayer1.riddenByEntity != null && (entityplayer1.riddenByEntity instanceof MoCEntityMouse || entityplayer1.riddenByEntity instanceof MoCEntityBird))
                {
                    PathEntity pathentity = worldObj.getPathEntityToEntity(this, entityplayer1, 16F, true, false, false, true);
                    setPathToEntity(pathentity);
                    setPissed(false);
                    hissCounter = 0;
                }
            }
            else
            {
                setNearPlayer(false);
                if (player_distance_to_snake < 2D && !getIsTamed() && this.getType() != 7)
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
    protected void attackEntity(Entity entity, float f)
    {

        if ((getType() < 3 || getIsTamed()) && entity instanceof EntityPlayer)
        {
            entityToAttack = null;
            return;
        }

        // attack only after hissing/rattling!
        if (!isPissed() && !getIsTamed()) { return; }

        if (attackTime <= 0 && (f < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            setBiting(true);
            attackTime = 20;

            // venom!
            if (rand.nextInt(2) == 0 && entity instanceof EntityPlayer && getType() > 2 && getType() < 8)
            {
                MoCreatures.poisonPlayer((EntityPlayer) entity);
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
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
        }
        this.isBiting = flag;
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
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
    	if (MoCreatures.isServer())
        {
        	if (this.ridingEntity != null && 
        			(damagesource.getEntity() == this.ridingEntity || DamageSource.inWall.equals(damagesource)))
            {
         	   return false;
            }
        	
        	else if (getType() < 3) { return super.attackEntityFrom(damagesource, i); }

        	else if (super.attackEntityFrom(damagesource, i))
            {
                Entity entity = damagesource.getEntity();
                
                if (entity != null && getIsTamed() && (entity instanceof EntityPlayer && (entity.getCommandSenderName().equals(getOwnerName()))))
                { 
                	return false; 
                }

                if ((riddenByEntity == entity) || (ridingEntity == entity)) { return true; }
                if ((entity != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
                {
                    setPissed(true);
                    entityToAttack = entity;
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
            EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 4D);
            if (!getIsTamed() && (entityplayer != null)) // && getIsAdult() )
            {
                if (isNotScared() && isPissed()) { return entityplayer; }
            }
            if ((rand.nextInt(100) == 0))
            {
                EntityLivingBase entityliving = getClosestEntityLiving(this, 8D);
                return entityliving;
            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
        {
	        if (this.getIsTamed() && this.ridingEntity == null) //defend owner if they are attacked by an entity
	    	{
	    		EntityPlayer owner_of_entity_that_is_online = MinecraftServer.getServer().getConfigurationManager().func_152612_a(this.getOwnerName());
	    		
	    		if (owner_of_entity_that_is_online != null)
	    		{
	    			EntityLivingBase entity_that_attacked_owner = owner_of_entity_that_is_online.getAITarget();
	    			
	    			if (entity_that_attacked_owner != null)
	    			{
	    				return entity_that_attacked_owner;
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
    public boolean entitiesToIgnore(Entity entity)
    {
        return ((super.entitiesToIgnore(entity))
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
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        String s = MoCTools.BiomeName(worldObj, i, j, k);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);
        int l = rand.nextInt(10);


        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SANDY))
        {
            if (l < 5)
            {
                setType(7); // rattlesnake
            }
            else
            {
                setType(2); // dark green/brown snake
            }
        }


        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.FOREST))
        {
            if (l < 4)
            {
                setType(4); //bright green snake
            }
            else
            {
                setType(1); //small blackish snake
            }
        }
        
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.JUNGLE))
        {
            if (l < 4)
            {
                setType(3); //bright orange snake
            }
            else if (l < 7)
            {
                setType(4); //bright green snake
            }
            else
            {
                setType(6); //cobra
            }
        }
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SWAMP))
        {
            // python or bright green bright orange
            if (l < 4)
            {
                setType(8); //python
            }
            else if (l < 8)
            {
                setType(4); //bright green snake
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