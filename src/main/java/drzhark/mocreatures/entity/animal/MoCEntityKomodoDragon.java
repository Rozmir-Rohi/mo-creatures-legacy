package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAmbient;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import drzhark.mocreatures.entity.aquatic.MoCEntityShark;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityKomodoDragon extends MoCEntityTameableAnimal
{
    public int sitCounter;
    public int tailCounter;
    public int tongueCounter;
    public int mouthCounter;

    public MoCEntityKomodoDragon(World world)
    {
        super(world);
        setSize(1.6F, 0.5F);
        texture = "komododragon.png";
        setTamed(false);
        setAdult(false);
        stepHeight = 1.0F;

        if(rand.nextInt(6) == 0)
        {
            setMoCAge(30 + rand.nextInt(40));
        }
        else
        {
            setMoCAge(90 + rand.nextInt(30));
        }
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
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
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // rideable: 0 nothing, 1 saddle
    }
    
    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && ticksExisted > 2400;
    }
   
    @Override
	public void setRideable(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    @Override
	public boolean getIsRideable()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return getCanSpawnHereCreature() && getCanSpawnHereLiving();
    }

    @Override
    protected String getDeathSound()
    {
        openmouth();
        
        return "mocreatures:snakedying";//"komododying";
    }

    @Override
    protected String getHurtSound()
    {
        openmouth();
        return "mocreatures:snakehurt";//"komodohurt";
    }

    @Override
    protected String getLivingSound()
    {
        openmouth();
        return "mocreatures:snakehiss";//"komodo";
    }

    @Override
    public int getTalkInterval()
    {
        return 500;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        if (entityToAttack != null && entityToAttack == riddenByEntity)
    	{
    		if (!(riddenByEntity instanceof EntityPlayer && riddenByEntity.getCommandSenderName().equals(getOwnerName()))) //if not the owner of this entity
    		{
    			riddenByEntity.mountEntity(null); //forcefully make the entity that is riding this entity dismount
    		}
    	}

        if (tailCounter > 0 && ++tailCounter > 30)
        {
            tailCounter = 0;
        }

        if (rand.nextInt(100) == 0)
        {
            tailCounter = 1;
        }
        
        
        if (sitCounter > 0 && (riddenByEntity != null || ++sitCounter > 150))
        {
             sitCounter = 0;
        }
        
        if (rand.nextInt(100) == 0)
        {
            tongueCounter = 1;
        }
        
        if (mouthCounter > 0 && ++mouthCounter > 30)
        {
            mouthCounter = 0;
        }
        
        if (tongueCounter > 0 && ++tongueCounter > 20)
        {
            tongueCounter = 0;
        }

        if (MoCreatures.isServer())
        {
            if (riddenByEntity == null && sitCounter == 0 && rand.nextInt(500) == 0)
            {
               sit();
            }
        }
        if ((MoCreatures.isServer()) && !getIsAdult() && (rand.nextInt(500) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 120)
            {
                setAdult(true);
            }
        }
    }

    private void openmouth()
    {
        mouthCounter = 1;
    }

    private void sit()
    {
        sitCounter = 1;
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        setPathToEntity(null);
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType == 0) //sitting animation
        {
            sitCounter = 1;
            setPathToEntity(null);
        }
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        boolean flag2 = (getMoCAge() > 90 && rand.nextInt(5) == 0);
        
       if (flag2)
        {
            int j = rand.nextInt(2)+1;
            for (int k = 0; k < j; k++)
            {
                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, 33), 0.0F);
            }
        }
       else
       {
       
        entityDropItem(new ItemStack(MoCreatures.hideReptile, 1, 0), 0.0F);
       }
    }

    @Override
    public float getSizeFactor() 
    {   
        if (!getIsAdult())
        {
            return getMoCAge() * 0.01F;
        }
        return 1.2F;
    }

    @Override
    public boolean isNotScared()
    {
        return true;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }

        ItemStack itemStack = entityPlayer.getHeldItem();
        
        if ((itemStack != null) && getIsTamed() && !getIsRideable() && getMoCAge() > 90 &&
                (itemStack.getItem() == Items.saddle || itemStack.getItem() == MoCreatures.craftedSaddle))
        {
            if (--itemStack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            setRideable(true);
            MoCTools.playCustomSound(this, "roping", worldObj);
            return true;
        }
        
        if (getIsRideable() && getIsTamed() && getMoCAge() > 90 && (riddenByEntity == null))
        {
            if (
            		MoCreatures.isServer()
            		&&
            			(
            				(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemStack == null)
            				|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
            			)
            		&& !(entityPlayer.isSneaking()) && (riddenByEntity == null)
            	)
            {
            	entityPlayer.rotationYaw = rotationYaw;
                entityPlayer.rotationPitch = rotationPitch;
                entityPlayer.mountEntity(this);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isMovementCeased()
    {
        return sitCounter != 0 || (riddenByEntity != null);
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
    }

    @Override
    public int nameYOffset()
    {
        if (getIsAdult())
        {
            return (-55);
        }
        return (60/getMoCAge()) * (-50);
    }

    @Override
    public double roperYOffset()
    {
        double r = (150 - getMoCAge()) * 0.012D;
        if (r < 0.55D)
        {
            r = 0.55D;
        }
        if (r > 1.2D)
        {
            r = 1.2D;
        }
        return r;
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

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Saddle", getIsRideable());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setRideable(nbtTagCompound.getBoolean("Saddle"));
    }

    @Override
    public double getMountedYOffset()
    {
        double yOffset = 0.15F;
        boolean sit = (sitCounter != 0);
        if (sit)
        {
            //yOffset = -0.5F;
        }
        if (getIsAdult())
        {
            return yOffset + (height);
        }
        return height * (120/getMoCAge());
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        
        if (attackTime <= 0 && (distanceToEntity < 3.0D))
        {
            attackTime = 20;
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 150, 0));
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
         
            if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName())))) 
            { 
            	return false;
            }

            if ((riddenByEntity != null) && (entityThatAttackedThisCreature == riddenByEntity)) 
            { 
                return false; 
            }

            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        return false;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if (worldObj.difficultySetting.getDifficultyId() > 0)
        {
            EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 6D);
            if (!getIsTamed() && (entityPlayer != null) && getMoCAge()>70)
            {
                    return entityPlayer;
            }
            if ((rand.nextInt(80) == 0))
            {
                EntityLivingBase entityLiving = MoCTools.getClosestEntityLivingThatCanBeTargetted(this, 8D);
                return entityLiving;
            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
        {
	        if (getIsTamed() && riddenByEntity == null) //defend owner if they are attacked by an entity
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
    public boolean isMyHealFood(ItemStack itemStack)
    {
        if (itemStack != null)
        {
        	Item item = itemStack.getItem();
        	
        	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemStack);
        	
        	return 
	        	(
	        		item == Items.porkchop
	    			|| item == Items.beef 
	    			|| item == Items.chicken
	    			|| (item == Items.fish && itemStack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
	        		|| item == Items.rotten_flesh
	    			|| item == MoCreatures.ratRaw
	        		|| item == MoCreatures.turkeyRaw
	            	|| item == MoCreatures.ostrichRaw
	        		|| (Item.itemRegistry).getNameForObject(item).equals("etfuturum:rabbit_raw")
	    			|| oreDictionaryNameArray.contains("listAllmeatraw")
	    			|| oreDictionaryNameArray.contains("listAllfishraw")
	    			|| MoCreatures.isGregTech6Loaded &&
	    				(
	    					oreDictionaryNameArray.contains("foodScrapmeat")
	    				)
	        	);
        }
        
        return false;
    }

    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (super.shouldEntityBeIgnored(entity)
        		|| (entity instanceof MoCEntityKomodoDragon)
        		|| (entity instanceof EntityPlayer)
            	|| (entity instanceof MoCEntityBigCat)
            	|| (entity instanceof MoCEntityShark)
            	|| (entity instanceof IMob || entity instanceof MoCEntityMob) // don't hunt mobs (eg: slime)
            	|| (entity instanceof MoCEntityKittyBed || entity instanceof MoCEntityLitterBox)
            	|| (entity instanceof MoCEntityJellyFish || entity instanceof MoCEntityRay || entity instanceof EntitySquid)
                || (getIsTamed() && (entity instanceof IMoCEntity) && ((IMoCEntity)entity).getIsTamed()) 
                || ((entity instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves))
        		|| (entity instanceof MoCEntityAmbient) // don't hunt insects
        		|| (entity.width > 0.9D && entity.height > 0.9D)); //don't try to hunt anything larger than it
    }

    @Override
    public void dropMyStuff() 
    {
        if (MoCreatures.isServer())
        {
            dropArmor();
            MoCTools.dropSaddle(this, worldObj);
        }
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 2;
    }
}