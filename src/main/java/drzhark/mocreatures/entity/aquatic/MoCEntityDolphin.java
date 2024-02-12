package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageHeart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityDolphin extends MoCEntityTameableAquatic {

    public int gestationTime;

    public MoCEntityDolphin(World world)
    {
        super(world);
        setSize(1.5F, 0.8F);
        setMoCAge(80 + rand.nextInt(100));
    }

    protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            int typeChance = rand.nextInt(100);
            if (typeChance <= 35)
            {
                setType(1);
            }
            else if (typeChance <= 60)
            {
                setType(2);
            }
            else if (typeChance <= 85)
            {
                setType(3);
            }
            else if (typeChance <= 96)
            {
                setType(4);
            }
            else if (typeChance <= 98)
            {
                setType(5);
            }
            else
            {
                setType(6);
            }
            
            getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed according to type
        }
    }

    @Override
    public ResourceLocation getTexture()
    {

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("dolphin.png");
        case 2:
            return MoCreatures.proxy.getTexture("dolphin2.png");
        case 3:
            return MoCreatures.proxy.getTexture("dolphin3.png");
        case 4:
            return MoCreatures.proxy.getTexture("dolphin4.png");
        case 5:
            return MoCreatures.proxy.getTexture("dolphin5.png");
        case 6:
            return MoCreatures.proxy.getTexture("dolphin6.png");
        default:
            return MoCreatures.proxy.getTexture("dolphin.png");
        }
    }

    @Override
    public int getMaxTemper()
    {

        switch (getType())
        {
        case 1:
            return 50;
        case 2:
            return 100;
        case 3:
            return 150;
        case 4:
            return 200;
        case 5:
            return 250;
        case 6:
            return 300;
        default:
            return 100;
        }
    }

    public int getInitialTemper()
    {
        switch (getType())
        {
        case 1:
            return 50;
        case 2:
            return 100;
        case 3:
            return 150;
        case 4:
            return 200;
        case 5:
            return 250;
        case 6:
            return 300;
        default:
            return 50;
        }
    }

    @Override
    public double getCustomSpeed()
    {
        switch (getType())
        {
        case 1:
            return 1.5D;
        case 2:
            return 2.5D;
        case 3:
            return 3.5D;
        case 4:
            return 4.5D;
        case 5:
            return 5.5D;
        case 6:
            return 6.5D;
        default:
            return 1.5D;
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // byte isHungry 0 = false 1 = true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // byte hasEaten 0 = false 1 = true
    }

    public boolean getIsHungry()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getHasEaten()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setIsHungry(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setHasEaten(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }
    
    @Override
    public void updateRiderPosition()
    {
        if (riddenByEntity == null) { return; }

        float size_factor = getMoCAge() * 0.01F;
        
        double distance = size_factor / 4.0D;
        
        double newPosX = posX - (distance * Math.cos((MoCTools.realAngle(renderYawOffset - 90F)) / 57.29578F));
        double newPosZ = posZ - (distance * Math.sin((MoCTools.realAngle(renderYawOffset - 90F)) / 57.29578F));
        
        riddenByEntity.setPosition(newPosX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), newPosZ);

    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 3.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY) && (getMoCAge() >= 100))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken) && (worldObj.difficultySetting.getDifficultyId() > 0))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            
            if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
            
            if (entityThatAttackedThisCreature != this)
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return riddenByEntity == null;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if ((worldObj.difficultySetting.getDifficultyId() > 0) && (getMoCAge() >= 100) && MoCreatures.proxy.attackDolphins && (rand.nextInt(50) == 0))
        {
            EntityLivingBase entityLiving = FindTarget(this, 12D);
            if ((entityLiving != null) && entityLiving.isInWater()) { return entityLiving; }
        }
        return null;
    }

    public EntityLivingBase FindTarget(Entity entity, double distance)
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
	            
	            if (!(entityNearby instanceof MoCEntityShark) || (entityNearby instanceof MoCEntityShark && ((MoCEntityShark) entityNearby).getIsTamed()))
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

    private int genetics(MoCEntityDolphin entitydolphin, MoCEntityDolphin entitydolphin1)
    {
        if (entitydolphin.getType() == entitydolphin1.getType()) { return entitydolphin.getType(); }
        int typeToSet = entitydolphin.getType() + entitydolphin1.getType();
        
        boolean flag = rand.nextInt(3) == 0;
        
        boolean flag1 = rand.nextInt(10) == 0;
        
        if ((typeToSet < 5) && flag) { return typeToSet; }
        
        if (((typeToSet == 5) || (typeToSet == 6)) && flag1)
        {
            return typeToSet;
        }
        else
        {
            return 0;
        }
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:dolphindying";
    }

    @Override
    protected Item getDropItem()
    {
        return Items.fish;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:dolphinhurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:dolphin";
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    protected String getUpsetSound()
    {
        return "mocreatures:dolphinupset";
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        
        
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        if ((itemstack != null) && isMyHealFood(itemstack))
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            if (MoCreatures.isServer())
            {
                setTemper(getTemper() + 25);
                if (getTemper() > getMaxTemper())
                {
                    setTemper(getMaxTemper() - 1);
                }

                heal(5);

                if (!getIsAdult())
                {
                    setMoCAge(getMoCAge() + 1);
                }
            }

            worldObj.playSoundAtEntity(this, "mocreatures:eating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));

            return true;
        }
        if ((itemstack != null) && (itemstack.getItem() == Items.cooked_fished) && getIsTamed() && getIsAdult())
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            
            heal(5);
            
            setHasEaten(true);
            worldObj.playSoundAtEntity(this, "mocreatures:eating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            return true;
        }
        if (
        		(
        			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
        			|| !(MoCreatures.proxy.emptyHandMountAndPickUpOnly)
        		)
        		&& !(entityPlayer.isSneaking()) && riddenByEntity == null
        	)
        {
            entityPlayer.rotationYaw = rotationYaw;
            entityPlayer.rotationPitch = rotationPitch;
            entityPlayer.posY = posY;
            if (!worldObj.isRemote)
            {
                entityPlayer.mountEntity(this);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (!getIsAdult() && (rand.nextInt(50) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 150)
                {
                    setAdult(true);
                }
            }
            if (!getIsHungry() && (rand.nextInt(100) == 0))
            {
                setIsHungry(true);
            }
            
            if (!ReadyforParenting(this)) { return; }
            
            int amountOfOtherDolphinsNearby = 0;
            
            List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8D, 2D, 8D));
            
            for (int index = 0; index < entitiesNearbyList.size(); index++)
            {
                Entity entityNearby = (Entity) entitiesNearbyList.get(index);
                
                if (entityNearby instanceof MoCEntityDolphin)
                {
                    amountOfOtherDolphinsNearby++;
                }
            }

            if (amountOfOtherDolphinsNearby > 1) { return; }
            
            List entitiesNearbyList1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4D, 2D, 4D));
            
            for (int index1 = 0; index1 < entitiesNearbyList1.size(); index1++)
            {
                Entity entityNearby1 = (Entity) entitiesNearbyList1.get(index1);
                
                if (!(entityNearby1 instanceof MoCEntityDolphin) || (entityNearby1 == this))
                {
                    continue;
                }
                
                MoCEntityDolphin entityDolphinNearby = (MoCEntityDolphin) entityNearby1;
                
                if (!ReadyforParenting(this) || !ReadyforParenting(entityDolphinNearby))
                {
                    continue;
                }
                if (rand.nextInt(100) == 0)
                {
                    gestationTime++;
                }
                if (gestationTime % 3 == 0)
                {
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(getEntityId()), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                }
                if (gestationTime <= 50)
                {
                    continue;
                }
                
                MoCEntityDolphin babyDolphin = new MoCEntityDolphin(worldObj);
                babyDolphin.setPosition(posX, posY, posZ);
                
                if (worldObj.spawnEntityInWorld(babyDolphin))
                {
                    setHasEaten(false);
                    entityDolphinNearby.setHasEaten(false);
                    gestationTime = 0;
                    entityDolphinNearby.gestationTime = 0;
                    
                    int typeToSet = genetics(this, entityDolphinNearby);
                    babyDolphin.setMoCAge(35);
                    
                    babyDolphin.setAdult(false);
                    
                    babyDolphin.setOwner(getOwnerName());
                    babyDolphin.setTamed(true);
                    
                    EntityPlayer entityPlayer = worldObj.getPlayerEntityByName(getOwnerName());
                    
                    if (entityPlayer != null)
                    {
                        MoCTools.tameWithName(entityPlayer, babyDolphin);
                    }
                    
                    babyDolphin.setTypeInt(typeToSet);
                    
                    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed according to type
                    break;
                }
            }
        }
    }
    
    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
    	if (itemstack != null)
    	{
	    	Item item = itemstack.getItem();
	    	
	    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
	    	
	    	return
	    		(
	    			(item == Items.fish && itemstack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
        			|| oreDictionaryNameArray.contains("listAllfishraw")
        		);
    	}
    	return false;
    }

    public boolean ReadyforParenting(MoCEntityDolphin entitydolphin)
    {
        return (entitydolphin.riddenByEntity == null) && (entitydolphin.ridingEntity == null) && entitydolphin.getIsTamed() && entitydolphin.getHasEaten() && entitydolphin.getIsAdult();
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null);
    }

    @Override
    public void setDead()
    {
        if (MoCreatures.isServer() && getIsTamed() && (getHealth() > 0))
        {
            return;
        }
        else
        {
            super.setDead();
            return;
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setDisplayName(nbtTagCompound.getBoolean("DisplayName"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("DisplayName", getDisplayName());
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }
}