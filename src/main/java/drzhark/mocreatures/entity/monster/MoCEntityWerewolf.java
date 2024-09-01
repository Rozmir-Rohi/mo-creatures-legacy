package drzhark.mocreatures.entity.monster;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfMinecraftComesAliveVillagerWitchery;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfVillagerWitchery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityWerewolf extends MoCEntityMob {
    private boolean isTransforming;
    private boolean isHunched;
    private int transformCounter;
    private int textureCounter;
    
    private int attackDamage = worldObj.difficultySetting.getDifficultyId() + 3;

    public MoCEntityWerewolf(World world)
    {
        super(world);
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "werehuman.png";
        setSize(0.9F, 1.6F);
        isTransforming = false;
        transformCounter = 0;
        setHumanForm(true);
    }
    
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isHumanForm - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); //hunched
        dataWatcher.addObject(24, String.valueOf("")); //nameOfPlayerThatRecruitedThisCreature
        dataWatcher.addObject(25, Byte.valueOf((byte) 0)); //sitting
    }
    
    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            int chance = rand.nextInt(100);
            if (chance <= 28)
            {
                setType(1);
            }
            else if (chance <= 56)
            {
                setType(2);
            }
            else if (chance <= 85)
            {
                setType(3);
            }
            else
            {
                setType(4);
                isImmuneToFire = true; //fire werewolf is immune to fire
            }
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        if (getIsHumanForm()) { return MoCreatures.proxy.getTexture("wereblank.png"); }

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("wolfblack.png");
        case 2:
            return MoCreatures.proxy.getTexture("wolfbrown.png");
        case 3:
            return MoCreatures.proxy.getTexture("wolftimber.png");
        case 4:
            if (!MoCreatures.proxy.getAnimateTextures()) { return MoCreatures.proxy.getTexture("wolffire1.png"); }
            
            if (rand.nextInt(3)== 0) {textureCounter++;} //animation speed
            
            if (textureCounter < 10)
            {
                textureCounter = 10;
            }
            
            if (textureCounter > 39)
            {
                textureCounter = 10;
            }
            
            String NTA = "wolffire";
            String NTB = "" + textureCounter;
            NTB = NTB.substring(0, 1);
            String NTC = ".png";

            return MoCreatures.proxy.getTexture(NTA + NTB + NTC);
        default:
            return MoCreatures.proxy.getTexture("wolfbrown.png");
        }
    }
    
    protected boolean isMovementCeased()
    {
        return getIsSitting();
    }
    
    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {   
        if (MoCreatures.isWitcheryLoaded && entityToAttack == null)
        {
        	ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        	
        	if (itemstack == null)
        	{
	        	if (
	        			!getIsHumanForm()
	        			&& (MoCTools.isPlayerInWerewolfForm(entityPlayer) && entityPlayer.getMaxHealth() == 60) //checks for max level werewolf 
	        			&& getNameOfPlayerThatRecruitedThisCreature().length() == 0
	        		)
	        	{
	        		setNameOfPlayerThatRecruitedThisCreature(entityPlayer.getCommandSenderName());
	        		playRecruitmentParticleEffect(true);
	        		if (!getIsHumanForm())
	        		{
	        			MoCTools.playCustomSound(this, "werewolfhowl", worldObj);
	        		}
	        	}
	        	
	        	if (
	        			getNameOfPlayerThatRecruitedThisCreature().length() > 0
	        			&& entityPlayer.isSneaking()
	        			&& entityPlayer.getCommandSenderName() == getNameOfPlayerThatRecruitedThisCreature()
	        		)
	        	{
	        		setSitting(!getIsSitting()); //toggles sitting
	        	}

        	}
        }
        
        
		return false;
    }

    public boolean getIsHumanForm()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setHumanForm(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public boolean getIsHunched()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }
    
    public void setHunched(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }
    
    public boolean getIsSitting()
    {
        return (dataWatcher.getWatchableObjectByte(25) == 1);
    }
    
    public void setSitting(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(25, Byte.valueOf(input));
    }
    
    private void setNameOfPlayerThatRecruitedThisCreature(String playerName)
    {
        dataWatcher.updateObject(24, playerName);
    }
    
    private String getNameOfPlayerThatRecruitedThisCreature()
    {
        return (dataWatcher.getWatchableObjectString(24));
    }
    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (getIsHumanForm())
        {
            entityToAttack = null;
            return;
        }
        
        if (MoCreatures.isWitcheryLoaded && entity instanceof EntityPlayer)
        {
        	if (MoCTools.isPlayerInWolfForm((EntityPlayer) entity) || MoCTools.isPlayerInWerewolfForm((EntityPlayer) entity)) //don't hunt player if is in wolf or werewolf form
        	{	
        		entityToAttack = null;
        		return;
        	}
        }
        if ((distanceToEntity > 2.0F) && (distanceToEntity < 6F) && (rand.nextInt(15) == 0))
        {
            if (onGround)
            {
                setHunched(true);
                double xDistance = entity.posX - posX;
                double zDistance = entity.posZ - posZ;
                float overallHorizontalDistanceSquared = MathHelper.sqrt_double((xDistance * xDistance) + (zDistance * zDistance));
                motionX = ((xDistance / overallHorizontalDistanceSquared) * 0.5D * 0.80000001192092896D) + (motionX * 0.20000000298023221D);
                motionZ = ((zDistance / overallHorizontalDistanceSquared) * 0.5D * 0.80000001192092896D) + (motionZ * 0.20000000298023221D);
                motionY = 0.40000000596046448D;
            }
        }
        else
        {
            if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
            {
                attackTime = 20;
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
                if (getType() == 4)
                {
                    ((EntityLivingBase) entity).setFire(10);
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        Entity entityThatAttackedThisCreature = damageSource.getEntity();
        if (!getIsHumanForm())
        {
        	if (damageTaken > 0)
        	{
	        	damageTaken = 1;
	        	
		        if (entityThatAttackedThisCreature != null)
		        {
			        if (entityThatAttackedThisCreature instanceof EntityPlayer)
			        {
			        	if (
			        			MoCTools.isPlayerInWerewolfForm((EntityPlayer) entityThatAttackedThisCreature)
			        			|| MoCTools.isPlayerInWolfForm((EntityPlayer) entityThatAttackedThisCreature)
			        		)
			        	{
			        		damageTaken = 5;
			        		damageSource = DamageSource.generic; //don't fight back if attacked by a player werewolf
			        	}
			        	
			        	else 
			        	{
			        		EntityPlayer entityPlayer = (EntityPlayer) entityThatAttackedThisCreature;
			        		ItemStack itemstack = entityPlayer.getCurrentEquippedItem();
			        		damageTaken = calculateWerewolfDamageTakenFromPlayerAttack(damageSource, damageTaken, itemstack);
			        	}
			        }
			        
			        else if (entityThatAttackedThisCreature instanceof MoCEntitySilverSkeleton)
			        {
			        	damageTaken = 9;
			        }
		        }
        	}
        }
        
        if (entityThatAttackedThisCreature != null && !(entityThatAttackedThisCreature instanceof EntityPlayer))
        {		
	        if (MoCreatures.isWitcheryLoaded && EntityList.getEntityString(entityThatAttackedThisCreature).equals("witchery.witchhunter"))
	        {	//fixes bug with Witchery witch hunter silver bolt damage
	        	damageTaken = 5;
	        }
        }
        
        return super.attackEntityFrom(damageSource, damageTaken);
    }

	public static float calculateWerewolfDamageTakenFromPlayerAttack(DamageSource damageSource, float damageTaken, ItemStack itemstack)
	{
		if (itemstack != null)
		{
		    damageTaken = 1;
		    
		    Item itemHeldByPlayer = itemstack.getItem();
		    
		    if (damageSource.isProjectile())
		    {
		    	
			    if (
		    			MoCreatures.isWitcheryLoaded
		    			&& damageSource.getEntity() instanceof EntityPlayer
		    			&& !(itemHeldByPlayer instanceof ItemFishingRod)
		    			&& EntityList.getEntityString(damageSource.getSourceOfDamage()).equals("witchery.bolt")
		    		)
		    	{
		    		EntityPlayer playerThatShotThisWerewolf = (EntityPlayer) damageSource.getEntity();
		    		
		    		if (doesPlayerHaveSilverBoltsAndNoOtherTypesOfBoltsInTheirInventory(playerThatShotThisWerewolf))
		    		{
		    			damageTaken = 5; //silver bolt damage
		    		}
		    		else {damageTaken = 1;} //other bolt damage
		    	}
		    	
		    	
			    else if (damageSource.getSourceOfDamage() instanceof EntityArrow)
		    	{
		    		damageTaken = 1;
		    	}
		    	
		    	else
		    	{
		    		damageTaken = 0; //damage for snowballs and eggs
		    	}
		    }
		    
		    if (itemHeldByPlayer == Items.golden_shovel)
		    {
		        damageTaken = 3;
		    }	
		    	
		    if (
		    		itemHeldByPlayer == Items.golden_hoe
		    		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("BiomesOPlenty:scytheGold")))
		    		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:dagger.gold")))
		    		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:waraxe.gold"))) // 8 is the actual damage dealt to werewolf using golden war axe in-game because of the item's armor penetration ability 
		    	)
		    {
		        damageTaken = 6;
		    }
		    
		    if (
		    		itemHeldByPlayer == Items.golden_pickaxe
		    	) 
		    {
		    	damageTaken = 7;
		    }
		    
		    if (
		    		itemHeldByPlayer == Items.golden_axe
		    		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:mace.gold")))
		    		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:spear.gold")))
		    	)
		    {
		        damageTaken = 8;
		    }
		    
		    if (
		    		itemHeldByPlayer == Items.golden_sword
		    		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("witchery:silversword")))
		    	)
		    {
		    	damageTaken = 9;
		    }
		    
		    if (itemHeldByPlayer == MoCreatures.silverSword) {damageTaken = 10;}
		    
		}
		return damageTaken;
	}

	public static boolean doesPlayerHaveSilverBoltsAndNoOtherTypesOfBoltsInTheirInventory(EntityPlayer player)
	{
		ItemStack[] inventoryOfPlayerThatShotThisWerewolf = player.inventory.mainInventory;
		
		boolean doesPlayerHaveSilverBoltsInInventory = false;
		
		boolean doesPlayerHaveOtherTypesOfBoltsInInventory = false;
		
		int iterationLength = inventoryOfPlayerThatShotThisWerewolf.length;
		
		if (iterationLength > 0)
		{
			for (int index = 0; index < iterationLength; index++) //iterates through all slots of the player's inventory
			{
				ItemStack itemstackInInventorySlot = inventoryOfPlayerThatShotThisWerewolf[index];
				
				if (itemstackInInventorySlot != null)
				{
					Item itemInInventorySlot = itemstackInInventorySlot.getItem();
					String stringNameForItemInInventorySlot = (itemInInventorySlot.itemRegistry).getNameForObject(itemInInventorySlot);
					
					if (stringNameForItemInInventorySlot.equals("witchery:ingredient"))
					{	
						if (itemstackInInventorySlot.getItemDamage() == 155) //silver bolt 
						{
							doesPlayerHaveSilverBoltsInInventory = true;
							
						}
						
						if (
								itemstackInInventorySlot.getItemDamage() == 132 //wooden bolt
								|| itemstackInInventorySlot.getItemDamage() == 133 //nullifying bolt
								|| itemstackInInventorySlot.getItemDamage() == 134 //bone bolt
								|| itemstackInInventorySlot.getItemDamage() == 135 //splitting bolt
							)
						{
							doesPlayerHaveOtherTypesOfBoltsInInventory = true;
						}
					}
				}
			}
		}
		
		if (doesPlayerHaveSilverBoltsInInventory && !doesPlayerHaveOtherTypesOfBoltsInInventory)
		{
			return true;
		}
			
		return false;
	}

    @Override
    protected Entity findPlayerToAttack()
    {
        if (getIsHumanForm()) { return null; }
        
        EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
        
        EntityLivingBase entityLiving = getClosestEntityLiving(this, 16D);
        
        if ((entityPlayer != null) && canEntityBeSeen(entityPlayer))
        {
        	if (	//don't hunt player if is in wolf or werewolf form or if they have been recruited by that player
        			MoCTools.isPlayerInWolfForm(entityPlayer)
        			|| MoCTools.isPlayerInWerewolfForm(entityPlayer)
        			|| (getNameOfPlayerThatRecruitedThisCreature().length() > 0 && entityPlayer.getCommandSenderName() == getNameOfPlayerThatRecruitedThisCreature())
        		) 
        	{	
        		if (
            			(MoCTools.isPlayerInWerewolfForm(entityPlayer) && entityPlayer.getMaxHealth() == 60) //checks for max level werewolf
            			|| (MoCTools.isPlayerInWolfForm(entityPlayer) && entityPlayer.getMaxHealth() == 32) //checks for max level werewolf
            			|| (getNameOfPlayerThatRecruitedThisCreature().length() > 0 && entityPlayer.getCommandSenderName() == getNameOfPlayerThatRecruitedThisCreature()) //werewolf player that recruited this werewolf
            		)
            	{
            		EntityLivingBase entityThatAttackedMaxLevelWerewolfPlayer = entityPlayer.getAITarget();
        			
        			if (entityThatAttackedMaxLevelWerewolfPlayer != null)
        			{
        				return entityThatAttackedMaxLevelWerewolfPlayer; //defend the max level werewolf player from the entity that attacked them
        			}
            	}
        		
        		return null;
        	}
        	else {return entityPlayer;}
        }
        
        else if ((entityLiving != null) && !(entityLiving instanceof EntityPlayer) && canEntityBeSeen(entityLiving))
        {
        	return entityLiving;
        }
        
        else
        {
            return null;
        }
    }
    
    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return 
        	(
        		!(
        				(entity instanceof EntityVillager) // only hunt villagers
        				&& !(entity instanceof MoCEntityWerewolfVillagerWitchery) //don't hunt
        				&& !(entity instanceof MoCEntityWerewolfMinecraftComesAliveVillagerWitchery) //don't hunt
        		)
        	);
    }

    @Override
    protected Item getDropItem()
    {
        int randomNumber = rand.nextInt(12);
        if (getIsHumanForm())
        {
            switch (randomNumber)
            {
	            case 0: // '\0'
	                return Items.wooden_shovel;
	
	            case 1: // '\001'
	                return Items.wooden_axe;
	
	            case 2: // '\002'
	                return Items.wooden_sword;
	
	            case 3: // '\003'
	                return Items.wooden_hoe;
	
	            case 4: // '\004'
	                return Items.wooden_pickaxe;
            }
            return Items.stick;
        }
        
        switch (randomNumber)
        {
	        case 0: // '\0'
	            return Items.iron_hoe;
	
	        case 1: // '\001'
	            return Items.iron_shovel;
	
	        case 2: // '\002'
	            return Items.iron_axe;
	
	        case 3: // '\003'
	            return Items.iron_pickaxe;
	
	        case 4: // '\004'
	            return Items.iron_sword;
	
	        case 5: // '\005'
	            return Items.stone_hoe;
	
	        case 6: // '\006'
	            return Items.stone_shovel;
	
	        case 7: // '\007'
	            return Items.stone_axe;
	
	        case 8: // '\b'
	            return Items.stone_pickaxe;
	
	        case 9: // '\t'
	            return Items.stone_sword;
        }
        return Items.golden_apple;
    }
    
    private void followPlayer()
    {
        EntityPlayer playerToFollow = MinecraftServer.getServer().getConfigurationManager().func_152612_a(getNameOfPlayerThatRecruitedThisCreature());

        if (playerToFollow == null) { return; }
        
        double distanceFromPlayerToFollow = MoCTools.getSqDistanceTo(this, playerToFollow.posX, playerToFollow.posY, playerToFollow.posZ);
        
        if (distanceFromPlayerToFollow > 5.0D)
        {
        	setHunched(true);
        	
            PathEntity pathEntity = worldObj.getPathEntityToEntity(this, playerToFollow, 16F, true, false, false, true);
            setPathToEntity(pathEntity);
        }
    }
    
    /**
     * Play the recruitment effect, will either be hearts or smoke depending on status
     */
    public void playRecruitmentParticleEffect(boolean par1)
    {
        String particleName = "happyVillager";


        for (int index = 0; index < 7; ++index)
        {
            double xVelocity = rand.nextGaussian() * 0.02D;
            double yVelocity = rand.nextGaussian() * 0.02D;
            double zVelocity = rand.nextGaussian() * 0.02D;
            
            worldObj.spawnParticle(particleName, posX + (double)(rand.nextFloat() * width * 2.0F) - (double)width, posY + 0.5D + (double)(rand.nextFloat() * height), posZ + (double)(rand.nextFloat() * width * 2.0F) - (double)width, xVelocity, yVelocity, zVelocity);
        }
    }

    @Override
    protected String getHurtSound()
    {
        if (getIsHumanForm())
        {	
        	if (MoCreatures.proxy.useRealisticHumanSoundsForWerewolf)
        	{
        		return "mocreatures:werehumanhurt";
        	}
        	
        	return "game.neutral.hurt";
        }
        else
        {
            return "mocreatures:werewolfhurt";
        }
    }
    
    @Override
    protected String getLivingSound()
    {
        if (getIsHumanForm())
        {
            return null;
        }
        else
        {
            return "mocreatures:werewolfgrunt";
        }
    }
    
    @Override
    protected String getDeathSound()
    {
        if (getIsHumanForm())
        {
            if (MoCreatures.proxy.useRealisticHumanSoundsForWerewolf)
            {
            	return "mocreatures:werehumandying";
            }
            
            return "game.neutral.die";
        }
        else
        {
            return "mocreatures:werewolfdying";
        }
    }

    public boolean getIsUndead()
    {
        return true;
    }

    public boolean IsNight()
    {
        return !worldObj.isDaytime();
    }

    @Override
    public void moveEntityWithHeading(float f, float f1)
    {
        if (!getIsHumanForm() && onGround)
        {
            motionX *= 1.2D;
            motionZ *= 1.2D;
        }
        super.moveEntityWithHeading(f, f1);
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        Entity entity = damageSource.getEntity();
        if ((scoreValue > 0) && (entity != null))
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        if (entity != null)
        {
            entity.onKillEntity(this);
        }
        

        if ((damageSource.getEntity() != null) && (damageSource.getEntity() instanceof EntityPlayer) && !(getIsHumanForm()))
        {
        	EntityPlayer player = (EntityPlayer)damageSource.getEntity();
            if (player != null) {player.addStat(MoCAchievements.kill_werewolf, 1);}
        }

        if (!worldObj.isRemote)
        {
            for (int index = 0; index < 2; index++)
            {
                Item item = getDropItem();
                if (item != null)
                {
                    dropItem(item, 1);
                }
            }

        }
    }
    
    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!worldObj.isRemote)
        {
            if (((IsNight() && getIsHumanForm()) || (!IsNight() && !getIsHumanForm())) && (rand.nextInt(250) == 0))
            {
                isTransforming = true;
            }
            if (getIsHumanForm() && (entityToAttack != null))
            {
                entityToAttack = null;
            }
            if ((entityToAttack != null) && !getIsHumanForm() && ((entityToAttack.posX - posX) > 3D) && ((entityToAttack.posZ - posZ) > 3D))
            {
                setHunched(true);
            }
            if (getIsHunched() && (rand.nextInt(50) == 0))
            {
                setHunched(false);
            }
            if (isTransforming && (rand.nextInt(3) == 0))
            {
                transformCounter++;
                if ((transformCounter % 2) == 0)
                {
                    posX += 0.29999999999999999D;
                    posY += transformCounter / 30;
                    attackEntityFrom(DamageSource.causeMobDamage(this), 0);
                }
                if ((transformCounter % 2) != 0)
                {
                    posX -= 0.29999999999999999D;
                }
                if (MoCreatures.proxy.useRealisticHumanSoundsForWerewolf && transformCounter == 10)
                {
                    playSound("mocreatures:weretransform", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
                }
                if (transformCounter > 30)
                {
                    Transform();
                    transformCounter = 0;
                    isTransforming = false;
                }
            }
            if (rand.nextInt(300) == 0)
            {
                entityAge -= 100 * worldObj.difficultySetting.getDifficultyId();
                if (entityAge < 0)
                {
                    entityAge = 0;
                }
            }
            if (!isMovementCeased() && entityToAttack == null)
            {
                followPlayer();
            }
        }
    }

    private void Transform()
    {
        if (deathTime > 0) { return; }

        if (getIsHumanForm())
        {
            setHumanForm(false);
            
            if (getMaxHealth() != 40F)
            {
            	getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
            }
            
            setHealth(getMaxHealth());
            isTransforming = false;
        }
        else
        {
            setHumanForm(true);
            
            setNameOfPlayerThatRecruitedThisCreature("");
            setSitting(false);
            
            float healthForHumanForm = Math.round((getHealth() / getMaxHealth()) * 16.0D);
            
            if (getMaxHealth() != 16F)
            {
            	getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
            }
            
            
            setHealth(healthForHumanForm);
            isTransforming = false;
        }
    }

    @Override
    protected void updateEntityActionState()
    {
        if (!isTransforming)
        {
            super.updateEntityActionState();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setHumanForm(nbtTagCompound.getBoolean("HumanForm"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("HumanForm", getIsHumanForm());
    }

    @Override
    public float getMoveSpeed()
    { 	
        if (getIsHunched()) { return 0.9F; }
        return 0.7F;
    }
}