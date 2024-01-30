package drzhark.mocreatures.entity.animal;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityKomodo extends MoCEntityTameableAnimal
{
    public int sitCounter;
    public int tailCounter;
    public int tongueCounter;
    public int mouthCounter;

    public MoCEntityKomodo(World world)
    {
        super(world);
        setSize(1.6F, 0.5F);
        //health = 20;
        texture = "komododragon.png";
        setTamed(false);
        setAdult(false);
        this.stepHeight = 1.0F;

        if(rand.nextInt(6) == 0)
        {
            setMoCAge(30 + rand.nextInt(40));
        }
        else
        {
            setMoCAge(90 + rand.nextInt(30));
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }
    
    @Override
    public boolean isScavenger()
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
        return !getIsTamed() && this.ticksExisted > 2400;
    }
   
    public void setRideable(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

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
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
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
       
        entityDropItem(new ItemStack(MoCreatures.hideCroc, 1, 0), 0.0F);
       }
    }

    @Override
    public float getSizeFactor() 
    {   
        if (!getIsAdult())
        {
            return (float)getMoCAge() * 0.01F;
        }
        return 1.2F;
    }

    @Override
    public boolean isNotScared()
    {
        return true;
    }

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {
        if (super.interact(entityplayer)) { return false; }

        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        
        if ((itemstack != null) && getIsTamed() && !getIsRideable() && getMoCAge() > 90 &&
                (itemstack.getItem() == Items.saddle || itemstack.getItem() == MoCreatures.horsesaddle))
        {
            if (--itemstack.stackSize == 0)
            {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
            }
            setRideable(true);
            MoCTools.playCustomSound(this, "roping", worldObj);
            return true;
        }
        
        if (getIsRideable() && getIsTamed() && getMoCAge() > 90 && (riddenByEntity == null))
        {
            if (MoCreatures.isServer() && (itemstack == null) && (this.riddenByEntity == null))
            {
            	entityplayer.rotationYaw = rotationYaw;
                entityplayer.rotationPitch = rotationPitch;
                entityplayer.mountEntity(this);
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
        return (int) ((60/getMoCAge()) * (-50));
    }

    @Override
    public double roperYOffset()
    {
        double r = (double) ((150 - getMoCAge()) * 0.012D);
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
    public boolean swimmerEntity()
    {
        return true;
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", getIsRideable());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setRideable(nbttagcompound.getBoolean("Saddle"));
    }

    @Override
    public double getMountedYOffset()
    {
        double yOff = 0.15F;
        boolean sit = (sitCounter != 0);
        if (sit)
        {
            //yOff = -0.5F;
        }
        if (getIsAdult())
        {
            return (double) (yOff + (this.height) );
        }
        return (double) (this.height * (120/getMoCAge()) );
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
        
        if (attackTime <= 0 && (f < 3.0D))
        {
            attackTime = 20;
            boolean entity_to_attack_is_a_player = (entity instanceof EntityPlayer);
            if (entity_to_attack_is_a_player)
            {
                MoCreatures.poisonPlayer((EntityPlayer) entity);
            }
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.poison.id, 150, 0));
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        if (super.attackEntityFrom(damagesource, i))
        {
            Entity entity = damagesource.getEntity();
         
            
            if (entity != null && getIsTamed() && entity instanceof EntityPlayer) 
            { 
                return false; 
            }

            if ((riddenByEntity != null) && (entity == riddenByEntity)) 
            { 
                return false; 
            }

            if ((entity != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entity;
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
            EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 6D);
            if (!getIsTamed() && (entityplayer != null) && getMoCAge()>70)
            {
                    return entityplayer;
            }
            if ((rand.nextInt(80) == 0))
            {
                EntityLivingBase entityliving = getClosestEntityLiving(this, 8D);
                return entityliving;
            }
        }
        
        if (MoCreatures.proxy.specialPetsDefendOwner)
        {
	        if (this.getIsTamed() && this.riddenByEntity == null) //defend owner if they are attacked by an entity
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
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return itemstack != null && 
        	(
        		itemstack.getItem() == Items.porkchop
    			|| itemstack.getItem() == Items.beef 
    			|| itemstack.getItem() == Items.chicken
    			|| itemstack.getItem() == Items.fish
        		|| itemstack.getItem() == MoCreatures.ratRaw
        		|| itemstack.getItem() == MoCreatures.rawTurkey
            	|| itemstack.getItem() == MoCreatures.ostrichraw
        		|| (itemstack.getItem().itemRegistry).getNameForObject(itemstack.getItem()).equals("etfuturum:rabbit_raw")
    			|| (itemstack.getItem().itemRegistry).getNameForObject(itemstack.getItem()).equals("etfuturum:rabbit_raw")
    			|| OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "listAllmeatraw"
    			|| MoCreatures.isGregTech6Loaded &&
    				(
    					OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodScrapmeat"
    				)
        	);
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return riddenByEntity == null;
    }

    @Override
    public boolean entitiesToIgnore(Entity entity)
    {
        return (super.entitiesToIgnore(entity)
        		|| (entity instanceof MoCEntityKomodo)
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