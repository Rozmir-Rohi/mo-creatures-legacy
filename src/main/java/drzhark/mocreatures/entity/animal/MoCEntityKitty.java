package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityKitty extends MoCEntityTameableAnimal {

    private int kittyTimer;
    private int madTimer;
    private boolean hasFoundTree;
    private final int treeCoord[] = { -1, -1, -1 };
    private int displayCount;

    private boolean isSwingingArm;
    private boolean isOnTree;

    public MoCEntityKitty(World world)
    {
        super(world);
        setSize(0.7F, 0.5F);
        setAdult(true);
        setMoCAge(40);
        setKittyState(1);
        kittyTimer = 0;
        madTimer = rand.nextInt(5);

        hasFoundTree = false;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            setType(rand.nextInt(8)+1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("pussycata.png");
        case 2:
            return MoCreatures.proxy.getTexture("pussycatb.png");
        case 3:
            return MoCreatures.proxy.getTexture("pussycatc.png");
        case 4:
            return MoCreatures.proxy.getTexture("pussycatd.png");
        case 5:
            return MoCreatures.proxy.getTexture("pussycate.png");
        case 6:
            return MoCreatures.proxy.getTexture("pussycatf.png");
        case 7:
            return MoCreatures.proxy.getTexture("pussycatg.png");
        case 8:
            return MoCreatures.proxy.getTexture("pussycath.png");

        default:
            return MoCreatures.proxy.getTexture("pussycata.png");
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Integer.valueOf(0)); // kittenstate int
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // isSitting - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // isHungry - 0 false 1 true
        dataWatcher.addObject(25, Byte.valueOf((byte) 0)); // isEmo - 0 false 1 true
    }

    public int getKittyState()
    {
        return dataWatcher.getWatchableObjectInt(22);
    }

    public boolean getIsSitting()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public boolean getIsHungry()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public boolean getIsEmo()
    {
        return (dataWatcher.getWatchableObjectByte(25) == 1);
    }

    public boolean getIsSwinging()
    {
        return isSwingingArm;
    }

    public boolean getOnTree()
    {
        return isOnTree;
    }

    public void setKittyState(int i)
    {
        dataWatcher.updateObject(22, Integer.valueOf(i));
    }

    public void setSitting(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public void setHungry(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }

    public void setIsEmo(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(25, Byte.valueOf(input));
    }

    public void setOnTree(boolean var1)
    {
        isOnTree = var1;
    }

    public void setSwinging(boolean var1)
    {
        isSwingingArm = var1;
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {

        if ((distanceToEntity > 2.0F) && (distanceToEntity < 6F) && (rand.nextInt(30) == 0) && onGround)
        {
            double xDistance = entity.posX - posX;
            double zDistance = entity.posZ - posZ;
            float overallHorizontalDistanceSquared = MathHelper.sqrt_double((xDistance * xDistance) + (zDistance * zDistance));
            motionX = ((xDistance / overallHorizontalDistanceSquared) * 0.5D * 0.8D) + (motionX * 0.2D);
            motionZ = ((zDistance / overallHorizontalDistanceSquared) * 0.5D * 0.8D) + (motionZ * 0.2D);
            motionY = 0.4D;
        }
        if ((distanceToEntity < 2D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            if ((getKittyState() != 18) && (getKittyState() != 10))
            {
                swingArm();
            }
            if (((getKittyState() == 13) && (entity instanceof EntityPlayer)) || ((getKittyState() == 8) && (entity instanceof EntityItem)) || ((getKittyState() == 18) && (entity instanceof MoCEntityKitty)) || (getKittyState() == 10)) { return; }

            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            if (entityThatAttackedThisCreature != this)
            {
                if (getKittyState() == 10)
                {
                    List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 6D, 16D));
                    for (int j = 0; j < list.size(); j++)
                    {
                        Entity entity1 = (Entity) list.get(j);
                        if ((entity1 instanceof MoCEntityKitty) && (((MoCEntityKitty) entity1).getKittyState() == 21))
                        {
                            ((MoCEntityKitty) entity1).entityToAttack = entityThatAttackedThisCreature;
                            return true;
                        }
                    }

                    return true;
                }
                if (entityThatAttackedThisCreature instanceof EntityPlayer)
                {
                    if (getKittyState() < 2)
                    {
                        entityToAttack = entityThatAttackedThisCreature;
                        setKittyState(-1);
                    }
                    if ((getKittyState() == 19) || (getKittyState() == 20) || (getKittyState() == 21))
                    {
                        entityToAttack = entityThatAttackedThisCreature;
                        setSitting(false);
                        return true;
                    }
                    if ((getKittyState() > 1) && (getKittyState() != 10) && (getKittyState() != 19) && (getKittyState() != 20) && (getKittyState() != 21))
                    {
                        setKittyState(13);
                        setSitting(false);
                    }
                    return true;
                }
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void changeKittyStateTo(int i)
    {
        setKittyState(i);
        if (MoCreatures.isServer())
        {
            mountEntity(null);
        }
        setSitting(false);
        kittyTimer = 0;
        setOnTree(false);
        hasFoundTree = false;
        entityToAttack = null;
    }

    public boolean climbingTree()
    {
        return (getKittyState() == 16) && isOnLadder();
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if ((worldObj.difficultySetting.getDifficultyId() > 0) && (getKittyState() != 8) && (getKittyState() != 10) && (getKittyState() != 15) && (getKittyState() != 18) && (getKittyState() != 19) && !isMovementCeased())
        {
            EntityLivingBase entityLiving = getClosestEntityLiving(this, 10D);
            return entityLiving;
        }
        else
        {
            return null;
        }
    }
    
    public boolean entitiesThatAreScary(Entity entity)
    {
        return 
        		(
        			entity.getClass() != getClass()
        			&& entity instanceof EntityLivingBase
        			&& ((entity.width >= 0.5D) || (entity.height >= 0.5D))
        			&& !(entity instanceof MoCEntityDeer)
        			&& !(entity instanceof MoCEntityHorse)
        			&& (getKittyState() == 1 || !(entity instanceof EntityPlayer))
        			
        		);
    }
    

    //TODO use MoCAnimal instead
    public EntityLiving getClosestEntityLiving(Entity entity, double distance)
    {
        double currentMinimumDistance = -1D;
        EntityLiving closestEntityLiving = null;
        
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
        int iterationLength = entitiesNearbyList.size();
        
        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	            
	            //do not hunt the following mobs below
	            if (
		            	!(entityNearby instanceof EntityLiving)
		            	|| entityNearby instanceof MoCEntityKitty
		            	|| entityNearby instanceof EntityPlayer
		            	|| (entityNearby instanceof IMob || entityNearby instanceof MoCEntityMob)
		            	|| entityNearby instanceof MoCEntityKittyBed
		            	|| entityNearby instanceof MoCEntityLitterBox
		            	|| entityNearby instanceof MoCEntityJellyFish
		            	|| entityNearby instanceof MoCEntityRay
		            	|| ((entityNearby.width > 0.5D) && (entityNearby.height > 0.5D))
	            	)            	
	            {
	                continue;
	            }
	            
	            if (entityNearby == null || MoCTools.isEntityAFishThatIsInTheOcean(entityNearby))
	            {
	            	continue;
	            }
	            
	            double overallDistanceSquared = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)) && ((EntityLiving) entityNearby).canEntityBeSeen(entity))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                closestEntityLiving = (EntityLiving) entityNearby;
	            }
	        }
        }

        return closestEntityLiving;
    }

    @Override
    protected String getDeathSound()
    {
        if (getKittyState() == 10)
        {
            return "mocreatures:kittendying";
        }
        else
        {
            return "mocreatures:kittydying";
        }
    }

    @Override
    protected Item getDropItem()
    {
        return null;
    }

    public ResourceLocation getEmoteIcon()
    {
        switch (getKittyState())
        {
        case -1:
            return null; //used to be: return MoCreatures.proxy.getTexture("emoticon2.png")

        case 3:   //wants food
        	return MoCreatures.proxy.getTexture("emoticon3.png");

        case 4:   // normal kitty
            return MoCreatures.proxy.getTexture("emoticon4.png");

        case 5:   // looking for litterbox
            return MoCreatures.proxy.getTexture("emoticon5.png");

        case 7: // happy kitty
            return MoCreatures.proxy.getTexture("emoticon7.png");

        case 8:   // very happy kitty
            return MoCreatures.proxy.getTexture("emoticon8.png");

        case 9: // in love - looking for mate
            return MoCreatures.proxy.getTexture("emoticon9.png");

        case 10:   // pleased kitty 
            return MoCreatures.proxy.getTexture("emoticon10.png");

        case 11:   // interested kitty 
            return MoCreatures.proxy.getTexture("emoticon11.png");

        case 12:   // sleeping 
            return MoCreatures.proxy.getTexture("emoticon12.png");

        case 13: // angry kitty - attacks player
            return MoCreatures.proxy.getTexture("emoticon13.png");

        case 16:   // tree
            return MoCreatures.proxy.getTexture("emoticon16.png");

        case 17:   //about to give birth
            return MoCreatures.proxy.getTexture("emoticon17.png");

        case 18:   // in love
            return MoCreatures.proxy.getTexture("emoticon9.png");

        case 19:   // giving birth1
            return MoCreatures.proxy.getTexture("emoticon19.png");

        case 20:   // giving birth2
            return MoCreatures.proxy.getTexture("emoticon19.png");

        case 21:   // pleased kitty
            return MoCreatures.proxy.getTexture("emoticon10.png");

        case 0: // '\0'
        case 1: // '\001'
        case 2: // '\002'
        case 6: // '\006'
        case 14: // '\016'
        case 15: // '\017'
        default:
            return MoCreatures.proxy.getTexture("emoticon1.png");
        }
    }

    @Override
    protected String getHurtSound()
    {
        if (getKittyState() == 10)
        {
            return "mocreatures:kittenhurt";
        }
        else
        {
            return "mocreatures:kittyhurt";
        }
    }

    public EntityLiving getKittyStuff(Entity entity, double d, boolean flag)
    {
        double d1 = -1D;
        Object obj = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, boundingBox.expand(d, d, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if (flag)
            {
                if (!(entity1 instanceof MoCEntityLitterBox))
                {
                    continue;
                }
                MoCEntityLitterBox entitylitterbox = (MoCEntityLitterBox) entity1;
                if (entitylitterbox.getUsedLitter())
                {
                    continue;
                }
                double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
                if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1.0D) || (d2 < d1)) && entitylitterbox.canEntityBeSeen(entity))
                {
                    d1 = d2;
                    obj = entitylitterbox;
                }
                continue;
            }
            if (!(entity1 instanceof MoCEntityKittyBed))
            {
                continue;
            }
            MoCEntityKittyBed entityKittyBed = (MoCEntityKittyBed) entity1;
            double d3 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((d < 0.0D) || (d3 < (d * d))) && ((d1 == -1.0D) || (d3 < d1)) && entityKittyBed.canEntityBeSeen(entity))
            {
                d1 = d3;
                obj = entityKittyBed;
            }
        }

        return ((EntityLiving) (obj));
    }

    @Override
    protected String getLivingSound()
    {
        if (getKittyState() == 4)
        {
            if (ridingEntity != null)
            {
                MoCEntityKittyBed entityKittyBed = (MoCEntityKittyBed) ridingEntity;
                if ((entityKittyBed != null) && !entityKittyBed.getHasMilk()) { return "mocreatures:kittyeatingm"; }
                if ((entityKittyBed != null) && !entityKittyBed.getHasFood()) { return "mocreatures:kittyeatingf"; }
            }
            return null;
        }
        if (getKittyState() == 6) { return "mocreatures:kittylitter"; }
        if (getKittyState() == 3) { return "mocreatures:kittyfood"; }
        if (getKittyState() == 10) { return "mocreatures:kittengrunt"; }
        if (getKittyState() == 13) { return "mocreatures:kittyupset"; }
        if (getKittyState() == 17) { return "mocreatures:kittytrapped"; }
        if ((getKittyState() == 18) || (getKittyState() == 12))
        {
            return "mocreatures:kittypurr";
        }
        else
        {
            return "mocreatures:kittygrunt";
        }
    }

    @Override
    public double getYOffset()
    {
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer())
        {
            if (getKittyState() == 10) { return (yOffset - 1.1F); }
            if (upsideDown()) { return (yOffset - 1.7F); }
            if (onPlayersBack()) { return (yOffset - 1.5F); }
        }

        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            if (getKittyState() == 10) { return (yOffset + 0.3F); }
            if (upsideDown()) { return (yOffset - 0.1F); }
            if (onPlayersBack()) { return (yOffset + 0.1F); }
        }

        return yOffset;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        //Ownership code
        //if (MoCreatures.proxy.enableStrictOwnership && getOwnerName() != null && !getOwnerName().equals("") && !entityPlayer.getCommandSenderName().equals(getOwnerName())) { return true; }

        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        if ((getKittyState() == 2) && (itemstack != null) && (itemstack.getItem() == MoCreatures.medallion))
        {
            if (MoCreatures.isServer())
            {
                MoCTools.tameWithName(entityPlayer, this);
                entityPlayer.addStat(MoCAchievements.tame_kitty, 1);
            }
            if (getIsTamed() && --itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            if (getIsTamed())
            {
                changeKittyStateTo(3);
                return true;
            }
            return false;
        }
        if ((getKittyState() == 7) && (itemstack != null) && 
        		(
        			itemstack.getItem() == Items.cake
        			|| (itemstack.getItem() == Items.fish && itemstack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
        			|| isItemStackInRawFishOreDictionary(itemstack)
        			|| itemstack.getItem() == Items.cooked_fished
        		)
        	)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            worldObj.playSoundAtEntity(this, "mocreatures:kittyeatingf", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            heal(8);
            changeKittyStateTo(9);
            return true;
        }
        if ((getKittyState() == 11) && (itemstack != null) && (itemstack.getItem() == MoCreatures.woolball) && MoCreatures.isServer())
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            setKittyState(8);
            EntityItem entityItem = new EntityItem(worldObj, posX, posY + 1.0D, posZ, new ItemStack(MoCreatures.woolball, 1));
            entityItem.delayBeforeCanPickup = 30;
            entityItem.age = -10000;
            worldObj.spawnEntityInWorld(entityItem);
            entityItem.motionY += worldObj.rand.nextFloat() * 0.05F;
            entityItem.motionX += (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.3F;
            entityItem.motionZ += (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.3F;
            entityToAttack = entityItem;
            return true;
        }
        if ((getKittyState() == 13) && (itemstack != null) && 
        		(
        			itemstack.getItem() == Items.fish
        			|| isItemStackInRawFishOreDictionary(itemstack)
        			|| itemstack.getItem() == Items.cooked_fished
        		)
        	)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            worldObj.playSoundAtEntity(this, "mocreatures:kittyeatingf", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            heal(5);
            changeKittyStateTo(7);
            return true;
        }
        if ((itemstack != null) && (getKittyState() > 2) && ((itemstack.getItem() == MoCreatures.medallion)))
        {
            if (MoCreatures.isServer())
            {
                MoCTools.tameWithName((EntityPlayerMP) entityPlayer, this);
            }

            return true;
        }
        if ((itemstack != null) && (getKittyState() > 2) && pickable() && (itemstack.getItem() == Items.lead) && (entityPlayer.ridingEntity == null))
        {
            changeKittyStateTo(14);
            if (MoCreatures.isServer())
            {
                mountEntity(entityPlayer);
            }
            return true;
        }
        if ((itemstack != null) && (getKittyState() > 2) && whipeable() && (itemstack.getItem() == MoCreatures.whip))
        {
            setSitting(!getIsSitting());
            return true;
        }
        if (
        		(
	    			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
	    			|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
        		)
        		&& !(entityPlayer.isSneaking()) && (getKittyState() == 10) && (ridingEntity != null)
        	)
        {
            ridingEntity = null;
            return true;
        }
        if (
	        	(
	    			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
	    			|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
	    		)
	        	&& !(entityPlayer.isSneaking()) && (getKittyState() > 2) && pickable() && (entityPlayer.ridingEntity == null)
	        )
        {
            changeKittyStateTo(15);
            if (MoCreatures.isServer())
            {
                mountEntity(entityPlayer);
            }
            return true;
        }
        if (
        		(
        				(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
        				|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
        		)
        		&& !(entityPlayer.isSneaking()) && (getKittyState() == 15)
        	)
        {
            changeKittyStateTo(7);
            return true;
        }
        if ((getKittyState() == 14) && ridingEntity != null)
        {
            changeKittyStateTo(7);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getIsSitting() || (getKittyState() == 6) || ((getKittyState() == 16) && getOnTree()) || (getKittyState() == 12) || (getKittyState() == 17) || (getKittyState() == 14) || (getKittyState() == 20) || (getKittyState() == 23);
    }

    @Override
    public boolean isOnLadder()
    {
        if (getKittyState() == 16)
        {
            return isCollidedHorizontally && getOnTree();
        }
        else
        {
            return super.isOnLadder();
        }
    }

    @Override
    public void onLivingUpdate()
    {
        if (MoCreatures.isServer())
        {
            if (!getIsAdult() && (getKittyState() != 10))
            {
                setKittyState(10);
            }
            if (getKittyState() != 12)
            {
                super.onLivingUpdate();
            }
            if (rand.nextInt(200) == 0)
            {
                setIsEmo(!getIsEmo());
            }
            if (!getIsAdult() && (rand.nextInt(200) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                }
            }
            
            if (getIsTamed() && isNight() && (ridingEntity == null) && !getIsSitting()) //find kittybed to sleep in else sleep on the spot
            {
            	MoCEntityKittyBed entityKittyBed = (MoCEntityKittyBed) getKittyStuff(this, 18D, false);
            	
                if ((entityKittyBed != null) && (entityKittyBed.riddenByEntity == null))
                {
                	float distanceToKittybed = entityKittyBed.getDistanceToEntity(this);
                    if (distanceToKittybed > 2.0F)
                    {
                        getMyOwnPath(entityKittyBed, distanceToKittybed);
                    }
                    if (distanceToKittybed < 2.0F) //sleep in kittybed
                    {
                        mountEntity(entityKittyBed);
                        setKittyState(12);
                    }
                }
                if (entityKittyBed == null)
                {	if ((rand.nextInt(500) == 0)) //sleep on the spot
                    {
                		setKittyState(12); 
                    }
                }
            }
            
            if (getIsTamed() && !isNight() && (getKittyState() == 12))
            {
            	changeKittyStateTo(10);
            }
            
            
            if (!getIsHungry() && !getIsSitting() && (rand.nextInt(100) == 0))
            {
                setHungry(true);
            }

            label0: switch (getKittyState())
            {
	            case -1:
	                break;
	            case 23: // '\027'
	                break;
	
	            case 1: // '\001'
	                if (rand.nextInt(10) == 0)
	                {
	                    EntityLivingBase entityLiving = getScaryEntity(6D);
	                    if (entityLiving != null)
	                    {
	                        MoCTools.runAway(this, entityLiving);
	                    }
	                    break;
	                }
	                
	                if (!getIsHungry() || (rand.nextInt(10) != 0))
	                {
	                    break;
	                }
	                
	                EntityItem entityItem = getClosestItem(this, 10D, Items.cooked_fished, Items.cooked_fished);
	               
	                if (entityItem == null)
	                {
	                    break;
	                }
	                
	                float f = entityItem.getDistanceToEntity(this);
	                
	                if (f > 2.0F)
	                {
	                    getMyOwnPath(entityItem, f);
	                }
	                
	                if ((f < 2.0F) && (entityItem != null) && (deathTime == 0))
	                {
	                    entityItem.setDead();
	                    worldObj.playSoundAtEntity(this, "mocreatures:kittyeatingf", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
	                    setHungry(false);
	                    setKittyState(2);
	                }
	                break;
	
	                
	                
	            case 2: // '\002'
	                EntityLivingBase entityLiving1 = getScaryEntity(6D);
	                
	                if (entityLiving1 != null)
	                {
	                    MoCTools.runAway(this, entityLiving1);
	                }
	                break;
	
	                
	                
	            case 3:   //wants food
	                kittyTimer++;
	                if (kittyTimer > 500)
	                {
	                    if (rand.nextInt(500) == 0)
	                    {
	                        changeKittyStateTo(7);
	                        break;
	                    }
	                }
	                
	                if (rand.nextInt(20) != 0)
	                {
	                    break;
	                }
	                
	                MoCEntityKittyBed entityKittyBed = (MoCEntityKittyBed) getKittyStuff(this, 18D, false);
	                
	                if ((entityKittyBed == null) || (entityKittyBed.riddenByEntity != null) || (!entityKittyBed.getHasMilk() && !entityKittyBed.getHasFood()))
	                {
	                    break;
	                }
	                
	                float f5 = entityKittyBed.getDistanceToEntity(this);
	                
	                if (f5 > 2.0F)
	                {
	                    getMyOwnPath(entityKittyBed, f5);
	                }
	                
	                if (f5 < 2.0F)
	                {
	                    changeKittyStateTo(4);
	                    mountEntity(entityKittyBed);
	                    setSitting(true);
	                    
	                }
	                break;
	
	                
	                
	            case 4:   // normal kitty
	                if (ridingEntity != null)
	                {
	                    MoCEntityKittyBed entityKittyBed1 = (MoCEntityKittyBed) ridingEntity;
	                    if ((entityKittyBed1 != null) && !entityKittyBed1.getHasMilk() && !entityKittyBed1.getHasFood())
	                    {
	                        heal(5);
	                        changeKittyStateTo(5);
	                    }
	                }
	                
	                else
	                {
	                    heal(5);
	                    changeKittyStateTo(5);
	                }
	                
	                if (rand.nextInt(2500) == 0)
	                {
	                    heal(5);
	                    changeKittyStateTo(7);
	                }
	                break;
	
	                
	                
	            case 5:   // looking for litterbox
	                kittyTimer++;
	                if ((kittyTimer > 2000) && (rand.nextInt(1000) == 0))
	                {
	                    changeKittyStateTo(13);
	                    break;
	                }
	                
	                if (rand.nextInt(20) != 0)
	                {
	                    break;
	                }
	                
	                MoCEntityLitterBox entityLitterbox = (MoCEntityLitterBox) getKittyStuff(this, 18D, true);
	                
	                if ((entityLitterbox == null) || (entityLitterbox.riddenByEntity != null) || entityLitterbox.getUsedLitter())
	                {
	                    break;
	                }
	                
	                
	                float f6 = entityLitterbox.getDistanceToEntity(this);
	                
	                if (f6 > 2.0F)
	                {
	                    getMyOwnPath(entityLitterbox, f6);
	                }
	                
	                if (f6 < 2.0F)
	                {
	                    changeKittyStateTo(6);
	                    mountEntity(entityLitterbox);
	                }
	                
	                break;
	
	                
	                
	            case 6: // '\006'
	                kittyTimer++;
	                if (kittyTimer <= 300)
	                {
	                    break;
	                }
	                
	                MoCEntityLitterBox entityLitterbox1 = (MoCEntityLitterBox) ridingEntity;
	                
	                if (entityLitterbox1 != null)
	                {
	                    entityLitterbox1.setUsedLitter(true);
	                    entityLitterbox1.litterTime = 0;
	                }
	                
	                changeKittyStateTo(7);
	                break;
	
	                
	                
	            case 7: // happy kitty
	                if (getIsSitting())
	                {
	                    break;
	                }
	                
	                if (rand.nextInt(20) == 0)
	                {
	                    EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 12D);
	                    if (entityPlayer != null)
	                    {
	                        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
	                        if ((itemstack != null) && (itemstack.getItem() == MoCreatures.woolball))
	                        {
	                            changeKittyStateTo(11);
	                            break;
	                        }
	                    }
	                }
	                
	                if (inWater && (rand.nextInt(500) == 0))
	                {
	                    changeKittyStateTo(13);
	                    break;
	                }
	                
	                if ((rand.nextInt(500) == 0) && !worldObj.isDaytime())
	                {
	                    changeKittyStateTo(12);
	                    break;
	                }
	                
	                if (rand.nextInt(2000) == 0)
	                {
	                    changeKittyStateTo(3);
	                    break;
	                }
	                
	                if (rand.nextInt(4000) == 0)
	                {
	                    changeKittyStateTo(16);
	                }
	                break;
	
	                
	                
	            case 8:   // very happy kitty
	                if (inWater && rand.nextInt(200) == 0)
	                {
	                    changeKittyStateTo(13);
	                    break;
	                }
	                
	                if ((entityToAttack != null) && (entityToAttack instanceof EntityItem))
	                {
	                    float f1 = getDistanceToEntity(entityToAttack);
	                    if (f1 < 1.5F)
	                    {
	                        swingArm();
	                        if (rand.nextInt(10) == 0)
	                        {
	                            MoCTools.pushEntityBack(this, entityToAttack, 0.3F);
	                        }
	                    }
	                }
	                
	                if ((entityToAttack == null) || (rand.nextInt(1000) == 0))
	                {
	                    changeKittyStateTo(7);
	                }
	                break;
	
	                
	                
	            case 9: // in love - looking for mate
	                kittyTimer++;
	                if (rand.nextInt(50) == 0)
	                {
	                    List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 6D, 16D));
	                    int j = 0;
	                    do
	                    {
	                        if (j >= list.size())
	                        {
	                            break;
	                        }
	                        Entity entity = (Entity) list.get(j);
	                        if ((entity instanceof MoCEntityKitty) && (entity instanceof MoCEntityKitty) && (((MoCEntityKitty) entity).getKittyState() == 9))
	                        {
	                            changeKittyStateTo(18);
	                            entityToAttack = entity;
	                            ((MoCEntityKitty) entity).changeKittyStateTo(18);
	                            ((MoCEntityKitty) entity).entityToAttack = this;
	                            break;
	                        }
	                        j++;
	                    } while (true);
	                }
	                
	                if (kittyTimer > 2000)
	                {
	                    changeKittyStateTo(7);
	                }
	                break;
	
	                
	                
	            case 10:   // pleased kitty 
	                if (getIsAdult())
	                {
	                    changeKittyStateTo(7);
	                    break;
	                }
	                
	                if (rand.nextInt(50) == 0)
	                {
	                    List list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 6D, 16D));
	                    for (int k = 0; k < list1.size(); k++)
	                    {
	                        Entity entity1 = (Entity) list1.get(k);
	                        if (!(entity1 instanceof MoCEntityKitty) || (((MoCEntityKitty) entity1).getKittyState() != 21))
	                        {
	                            continue;
	                        }
	                        float f9 = getDistanceToEntity(entity1);
	                        if (f9 > 12F)
	                        {
	                            entityToAttack = entity1;
	                        }
	                    }
	
	                }
	                
	                if ((entityToAttack == null) && (rand.nextInt(100) == 0))
	                {
	                    int i = rand.nextInt(10);
	                    if (i < 7)
	                    {
	                        entityToAttack = getClosestItem(this, 10D, null, null);
	                    }
	                    else
	                    {
	                        entityToAttack = worldObj.getClosestPlayerToEntity(this, 18D);
	                    }
	                }
	                
	                if ((entityToAttack != null) && (rand.nextInt(400) == 0))
	                {
	                    entityToAttack = null;
	                }
	                
	                if ((entityToAttack != null) && (entityToAttack instanceof EntityItem))
	                {
	                    float f2 = getDistanceToEntity(entityToAttack);
	                    if (f2 < 1.5F)
	                    {
	                        swingArm();
	                        if (rand.nextInt(10) == 0)
	                        {
	                            MoCTools.pushEntityBack(this, entityToAttack, 0.2F);
	                            //kittySmack(this, entityToAttack);
	                        }
	                    }
	                }
	                
	                if ((entityToAttack != null) && (entityToAttack instanceof MoCEntityKitty) && (rand.nextInt(20) == 0))
	                {
	                    float f3 = getDistanceToEntity(entityToAttack);
	                    if (f3 < 2.0F)
	                    {
	                        swingArm();
	                        setPathToEntity(null);
	                    }
	                }
	                
	                if ((entityToAttack == null) || !(entityToAttack instanceof EntityPlayer))
	                {
	                    break;
	                }
	                
	                float f4 = getDistanceToEntity(entityToAttack);
	                
	                if ((f4 < 2.0F) && (rand.nextInt(20) == 0))
	                {
	                    swingArm();
	                }
	                break;
	
	                
	                
	            case 11:   // interested kitty 
	                EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 18D);
	                
	                if ((entityPlayer1 == null) || (rand.nextInt(10) != 0))
	                {
	                    break;
	                }
	                
	                ItemStack itemstack1 = entityPlayer1.inventory.getCurrentItem();
	                
	                if ((itemstack1 == null) || ((itemstack1 != null) && (itemstack1.getItem() != MoCreatures.woolball)))
	                {
	                    changeKittyStateTo(7);
	                    break;
	                }
	                
	                float f8 = entityPlayer1.getDistanceToEntity(this);
	                
	                if (f8 > 5F)
	                {
	                    getPathOrWalkableBlock(entityPlayer1, f8);
	                }
	                break;
	
	                
	                
	            case 12:   // sleeping 
	                
	            	kittyTimer++;
	            	
	                if (worldObj.isDaytime() || ((kittyTimer > 500) && (rand.nextInt(500) == 0)))
	                {
	                    changeKittyStateTo(7);
	                    break;
	                }
	                
	                setSitting(true);
	                
	                if ((rand.nextInt(80) == 0) || !onGround)
	                {
	                    super.onLivingUpdate();
	                }
	                break;
	
	                
	                
	            case 13:     // Angry kitty  - attacks player
	                setHungry(false);
	                entityToAttack = worldObj.getClosestPlayerToEntity(this, 18D);
	                if (entityToAttack != null)
	                {
	                    float f7 = getDistanceToEntity(entityToAttack);
	                    if (f7 < 1.5F)
	                    {
	                        swingArm();
	                        if (rand.nextInt(20) == 0)
	                        {
	                            madTimer--;
	                            entityToAttack.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
	                            if (madTimer < 1)
	                            {
	                                changeKittyStateTo(7);
	                                madTimer = rand.nextInt(5);
	                            }
	                        }
	                    }
	                    if (rand.nextInt(500) == 0)
	                    {
	                        changeKittyStateTo(7);
	                    }
	                }
	                
	                else
	                {
	                    changeKittyStateTo(7);
	                }
	                break;
	
	                
	                
	            case 14: // held by rope
	                if (onGround)
	                {
	                    changeKittyStateTo(13);
	                    break;
	                }
	                
	                if (rand.nextInt(50) == 0)
	                {
	                    swingArm();
	                }
	                
	                if (ridingEntity == null)
	                {
	                    break;
	                }
	                
	                rotationYaw = ridingEntity.rotationYaw + 90F;
	                
	                EntityPlayer entityPlayer2 = (EntityPlayer) ridingEntity;
	                
	                if (entityPlayer2 == null)
	                {
	                    changeKittyStateTo(13);
	                    break;
	                }
	                
	                ItemStack itemstack2 = entityPlayer2.inventory.getCurrentItem();
	                
	                if (itemstack2 == null || ((itemstack2 != null) && (itemstack2.getItem() != Items.lead)))
	                {
	                    changeKittyStateTo(13);
	                }
	                break;
	
	                
	                
	            case 15: // '\017'
	                if (onGround)
	                {
	                    changeKittyStateTo(7);
	                }
	                
	                if (ridingEntity != null)
	                {
	                    rotationYaw = ridingEntity.rotationYaw + 90F;
	                }
	                break;
	
	                
	                
	            case 16:   // tree
	                kittyTimer++;
	                if ((kittyTimer > 500) && !getOnTree())
	                {
	                    changeKittyStateTo(7);
	                }
	                if (!getOnTree())
	                {
	                    if (!hasFoundTree && (rand.nextInt(50) == 0))
	                    {
	                        int ai[] = MoCTools.ReturnNearestMaterialCoord(this, Material.wood, Double.valueOf(18D), 4D);
	                        if (ai[0] != -1)
	                        {
	                            int i1 = 0;
	                            do
	                            {
	                                if (i1 >= 20)
	                                {
	                                    break;
	                                }
	                                Block block = worldObj.getBlock(ai[0], ai[1] + i1, ai[2]);
	                                if ((block.getMaterial() == Material.leaves))
	                                {
	                                    hasFoundTree = true;
	                                    treeCoord[0] = ai[0];
	                                    treeCoord[1] = ai[1];
	                                    treeCoord[2] = ai[2];
	                                    break;
	                                }
	                                i1++;
	                            } while (true);
	                        }
	                    }
	                    
	                    if (!hasFoundTree || (rand.nextInt(10) != 0))
	                    {
	                        break;
	                    }
	                    
	                    PathEntity pathEntity = worldObj.getEntityPathToXYZ(this, treeCoord[0], treeCoord[1], treeCoord[2], 24F, true, false, false, true);
	
	                    if (pathEntity != null)
	                    {
	                        setPathToEntity(pathEntity);
	                    }
	                    
	                    Double distanceToTree = Double.valueOf(getDistanceSq(treeCoord[0], treeCoord[1], treeCoord[2]));
	                    
	                    if (distanceToTree.doubleValue() < 7D)
	                    {
	                        setOnTree(true);
	                    }
	                    break;
	                }
	                
	                if (!getOnTree())
	                {
	                    break;
	                }
	                
	                int treeCoordX = treeCoord[0];
	                int treeCoordY = treeCoord[1];
	                int treeCoordZ = treeCoord[2];
	                faceItem(treeCoordX, treeCoordY, treeCoordZ, 30F);
	                
	                if ((treeCoordY - MathHelper.floor_double(posY)) > 2)
	                {
	                    motionY += 0.029999999999999999D;
	                }
	                
	                boolean flag = false;
	                boolean flag1 = false;
	                
	                if (posX < treeCoordX)
	                {
	                    int j2 = treeCoordX - MathHelper.floor_double(posX);
	                    motionX += 0.01D;
	                }
	                else
	                {
	                    int k2 = MathHelper.floor_double(posX) - treeCoordX;
	                    motionX -= 0.01D;
	                }
	                if (posZ < treeCoordZ)
	                {
	                    int j3 = treeCoordZ - MathHelper.floor_double(posZ);
	                    motionZ += 0.01D;
	                }
	                else
	                {
	                    int k3 = MathHelper.floor_double(posX) - treeCoordZ;
	                    motionZ -= 0.01D;
	                }
	                if (onGround || !isCollidedHorizontally || !isCollidedVertically)
	                {
	                    break;
	                }
	                int i4 = 0;
	                do
	                {
	                    if (i4 >= 30)
	                    {
	                        break label0;
	                    }
	                    Block block = worldObj.getBlock(treeCoord[0], treeCoord[1] + i4, treeCoord[2]);
	                    if (block == Blocks.air)
	                    {
	                        setLocationAndAngles(treeCoord[0], treeCoord[1] + i4, treeCoord[2], rotationYaw, rotationPitch);
	                        changeKittyStateTo(17);
	                        treeCoord[0] = -1;
	                        treeCoord[1] = -1;
	                        treeCoord[2] = -1;
	                        break label0;
	                    }
	                    i4++;
	                } while (true);
	
	                
	                
	            case 17:   //about to give birth
	                EntityPlayer entityPlayer3 = worldObj.getClosestPlayerToEntity(this, 2D);
	                if (entityPlayer3 != null)
	                {
	                    changeKittyStateTo(7);
	                }
	                break;
	
	                
	                
	            case 18:   // in love
	                if ((entityToAttack == null) || !(entityToAttack instanceof MoCEntityKitty))
	                {
	                    changeKittyStateTo(9);
	                    break;
	                }
	                
	                MoCEntityKitty entityKitty = (MoCEntityKitty) entityToAttack;
	                
	                if ((entityKitty != null) && (entityKitty.getKittyState() == 18))
	                {
	                    if (rand.nextInt(50) == 0)
	                    {
	                        swingArm();
	                    }
	                    float f10 = getDistanceToEntity(entityKitty);
	                    if (f10 < 5F)
	                    {
	                        kittyTimer++;
	                    }
	                    if ((kittyTimer > 500) && (rand.nextInt(50) == 0))
	                    {
	                        ((MoCEntityKitty) entityToAttack).changeKittyStateTo(7);
	                        changeKittyStateTo(19);
	                    }
	                }
	                
	                else
	                {
	                    changeKittyStateTo(9);
	                }
	                break;
	
	                
	                
	            case 19:   // giving birth1
	                if (rand.nextInt(20) != 0)
	                {
	                    break;
	                }
	                
	                MoCEntityKittyBed entityKittyBed2 = (MoCEntityKittyBed) getKittyStuff(this, 18D, false);
	                
	                if ((entityKittyBed2 == null) || (entityKittyBed2.riddenByEntity != null))
	                {
	                    break;
	                }
	                
	                float f11 = entityKittyBed2.getDistanceToEntity(this);
	                
	                if (f11 > 2.0F)
	                {
	                    getMyOwnPath(entityKittyBed2, f11);
	                }
	                
	                if (f11 < 2.0F)
	                {
	                    changeKittyStateTo(20);
	                    mountEntity(entityKittyBed2);
	                }
	                break;
	
	                
	                
	            case 20:   // giving birth2
	                if (ridingEntity == null)
	                {
	                    changeKittyStateTo(19);
	                    break;
	                }
	                
	                rotationYaw = 180F;
	                kittyTimer++;
	                
	                if (kittyTimer <= 1000)
	                {
	                    break;
	                }
	                
	                int i2 = rand.nextInt(3) + 1;
	                
	                MoCEntityKitty entityKitty1 = new MoCEntityKitty(worldObj);
                    
	                int babytype = getType();
                    
	                if (rand.nextInt(2) == 0)
                    {
                        babytype = (rand.nextInt(8)+1);
                    }
	                
                    entityKitty1.setType(babytype);
                    entityKitty1.setPosition(posX, posY, posZ);
                    worldObj.spawnEntityInWorld(entityKitty1);
                    entityKitty1.setAdult(false);
                    entityKitty1.changeKittyStateTo(10);
                    
                    EntityPlayer ownerOfKittyThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(getOwnerName());
                    
                    if (ownerOfKittyThatIsOnline != null)
                    {
                        MoCTools.tameWithName(ownerOfKittyThatIsOnline, entityKitty1);
                    }
	
	                changeKittyStateTo(21);
	                break;
	
	                
	                
	            case 21:   // pleased kitty
	                kittyTimer++;
	                
	                if (kittyTimer > 2000)
	                {
	                    List list2 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(24D, 8D, 24D));
	                    int i3 = 0;
	                    for (int l3 = 0; l3 < list2.size(); l3++)
	                    {
	                        Entity entity2 = (Entity) list2.get(l3);
	                        if ((entity2 instanceof MoCEntityKitty) && (((MoCEntityKitty) entity2).getKittyState() == 10))
	                        {
	                            i3++;
	                        }
	                    }
	
	                    if (i3 < 1)
	                    {
	                        changeKittyStateTo(7);
	                        break;
	                    }
	                    kittyTimer = 1000;
	                }
	                
	                if ((entityToAttack != null) && (entityToAttack instanceof EntityPlayer) && (rand.nextInt(300) == 0))
	                {
	                    entityToAttack = null;
	                }
	                break;
	
	                
	                
	            case 0:
	                changeKittyStateTo(1);
	                break;
	            
	                
	                
	            // case 22: // '\026'
	            default:
	                changeKittyStateTo(7);
	                break;
            }
        }
        else
        {
            super.onLivingUpdate();
        }
    }

    public boolean onPlayersBack()
    {
        return getKittyState() == 15;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (getIsSwinging())
        {
            swingProgress += 0.2F;
            if (swingProgress > 2.0F)
            {
                setSwinging(false);
                swingProgress = 0.0F;
            }
        }
    }

    private boolean pickable()
    {
        return (getKittyState() != 13) && (getKittyState() != 14) && (getKittyState() != 15) && (getKittyState() != 19) && (getKittyState() != 20) && (getKittyState() != 21);
    }
    
    private boolean isItemStackInRawFishOreDictionary(ItemStack itemstack)
    {
    	if (itemstack != null)
    	{
	    	Item item = itemstack.getItem();
	    	
	    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
	    	
	    	return oreDictionaryNameArray.contains("listAllfishraw");
	    	
    	}
    	
    	return false;
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (getKittyState() != 14) && (getKittyState() != 15) && (getKittyState() > 1);
    }

    @Override
    public void setDead()
    {
        if (MoCreatures.isServer() && getKittyState() > 2 && getHealth() > 0 && !MoCreatures.isMobConfinementLoaded)   // the "!MoCreatures.isMobConfinementLoaded" allows setDead() to work on tamed creatures if the Mob Confinement mod is loaded. This is so that the mob confinement items don't duplicate tamed creatures when they try to store them.
        {
            return;
        }
        super.setDead();
    }

    public void swingArm()
    {
        //to synchronize, uses the packet handler to invoke the same method in the clients
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }

        if (!getIsSwinging())
        {
            setSwinging(true);
            swingProgress = 0.0F;
        }
    }

    @Override
    public void performAnimation(int i)
    {
        swingArm();
    }

    public boolean upsideDown()
    {
        return getKittyState() == 14;
    }

    public boolean whipeable()
    {
        return getKittyState() != 13;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setSitting(nbtTagCompound.getBoolean("Sitting"));
        setKittyState(nbtTagCompound.getInteger("KittyState"));
        setDisplayName(nbtTagCompound.getBoolean("DisplayName"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Sitting", getIsSitting());
        nbtTagCompound.setInteger("KittyState", getKittyState());
        nbtTagCompound.setBoolean("DisplayName", getDisplayName());
    }

    @Override
    public boolean updateMount()
    {
        return true;
    }

    @Override
    public boolean forceUpdates()
    {
        return true;
    }
    
    
    //drops medallion on death
    @Override
    public void onDeath(DamageSource damageSource)
    {
        if (MoCreatures.isServer())
        {
            if (getIsTamed())
            {
                MoCTools.dropCustomItem(this, worldObj, new ItemStack(MoCreatures.medallion, 1));
            }
        }
        super.onDeath(damageSource);
    }
    
    private boolean isNight()
    {
        return !worldObj.isDaytime();
    }

    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }
}