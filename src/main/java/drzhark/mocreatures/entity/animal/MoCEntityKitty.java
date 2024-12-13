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
    
    final static int KITTY_STATE_UNTAMED = 1;
    final static int KITTY_STATE_PRETAMED = 2;
    final static int KITTY_STATE_WANTS_FOOD = 3;
    final static int KITTY_STATE_USING_KITTY_BED = 4;
    final static int KITTY_STATE_LOOKING_FOR_LITTER_BOX = 5;
    final static int KITTY_STATE_USING_LITTER_BOX = 6;
    final static int KITTY_STATE_NORMAL_HAPPY = 7;
    final static int KITTY_STATE_PLAYING_WITH_WOOLBALL = 8;
    final static int KITTY_STATE_IN_LOVE_STAGE_ONE = 9;
    final static int KITTY_STATE_KITTEN = 10;
    final static int KITTY_STATE_INTERESTED = 11;
    final static int KITTY_STATE_SLEEPING = 12;
    final static int KITTY_STATE_ANGRY = 13;
    final static int KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE = 14;
    final static int KITTY_STATE_ON_PLAYERS_BACK = 15;
    final static int KITTY_STATE_TREE = 16;
    final static int KITTY_STATE_GOING_INTO_LABOUR = 17;
    final static int KITTY_STATE_IN_LOVE_STAGE_TWO = 18;
    final static int KITTY_STATE_GIVING_BIRTH_STAGE_ONE = 19;
    final static int KITTY_STATE_GIVING_BIRTH_STAGE_TWO = 20;
    final static int KITTY_STATE_PARENTING = 21;
    
    public MoCEntityKitty(World world)
    {
        super(world);
        setSize(0.7F, 0.5F);
        setAdult(true);
        setMoCAge(40);
        setKittyState(KITTY_STATE_UNTAMED);
        kittyTimer = 0;
        madTimer = rand.nextInt(5);

        hasFoundTree = false;
    }

    @Override
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
            if ((getKittyState() != KITTY_STATE_IN_LOVE_STAGE_TWO) && (getKittyState() != KITTY_STATE_KITTEN))
            {
                swingArm();
            }
            if (((getKittyState() == KITTY_STATE_ANGRY) && (entity instanceof EntityPlayer)) || ((getKittyState() == KITTY_STATE_PLAYING_WITH_WOOLBALL) && (entity instanceof EntityItem)) || ((getKittyState() == KITTY_STATE_IN_LOVE_STAGE_TWO) && (entity instanceof MoCEntityKitty)) || (getKittyState() == KITTY_STATE_KITTEN)) { return; }

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
                if (getKittyState() == KITTY_STATE_KITTEN)
                {
                    List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 6D, 16D));
                    for (int index = 0; index < listOfEntitiesNearby.size(); index++)
                    {
                        Entity entityNearby = (Entity) listOfEntitiesNearby.get(index);
                        if ((entityNearby instanceof MoCEntityKitty) && (((MoCEntityKitty) entityNearby).getKittyState() == KITTY_STATE_PARENTING))
                        {
                            ((MoCEntityKitty) entityNearby).entityToAttack = entityThatAttackedThisCreature;
                            return true;
                        }
                    }

                    return true;
                }
                if (entityThatAttackedThisCreature instanceof EntityPlayer)
                {
                    if (getKittyState() < KITTY_STATE_PRETAMED)
                    {
                        entityToAttack = entityThatAttackedThisCreature;
                        setKittyState(-1);
                    }
                    if ((getKittyState() == KITTY_STATE_GIVING_BIRTH_STAGE_ONE) || (getKittyState() == KITTY_STATE_GIVING_BIRTH_STAGE_TWO) || (getKittyState() == KITTY_STATE_PARENTING))
                    {
                        entityToAttack = entityThatAttackedThisCreature;
                        setSitting(false);
                        return true;
                    }
                    if ((getKittyState() > KITTY_STATE_UNTAMED) && (getKittyState() != KITTY_STATE_KITTEN) && (getKittyState() != KITTY_STATE_GIVING_BIRTH_STAGE_ONE) && (getKittyState() != KITTY_STATE_GIVING_BIRTH_STAGE_TWO) && (getKittyState() != KITTY_STATE_PARENTING))
                    {
                        setKittyState(KITTY_STATE_ANGRY);
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

    public boolean isClimbingTree()
    {
        return (getKittyState() == KITTY_STATE_TREE) && isOnLadder();
    }

    @Override
    protected void fall(float f)
    { //ignore fall damage for this entity
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if ((worldObj.difficultySetting.getDifficultyId() > 0) && (getKittyState() != KITTY_STATE_PLAYING_WITH_WOOLBALL) && (getKittyState() != KITTY_STATE_KITTEN) && (getKittyState() != KITTY_STATE_ON_PLAYERS_BACK) && (getKittyState() != KITTY_STATE_IN_LOVE_STAGE_TWO) && (getKittyState() != KITTY_STATE_GIVING_BIRTH_STAGE_ONE) && !isMovementCeased())
        {
            EntityLivingBase entityLiving = getClosestEntityLiving(this, 10D);
            return entityLiving;
        }
        else
        {
            return null;
        }
    }
    
    @Override
	public boolean entitiesThatAreScary(Entity entity)
    {
        return 
        		(
        			entity.getClass() != getClass()
        			&& entity instanceof EntityLivingBase
        			&& ((entity.width >= 0.5D) || (entity.height >= 0.5D))
        			&& !(entity instanceof MoCEntityDeer)
        			&& !(entity instanceof MoCEntityHorse)
        			&& (getKittyState() == KITTY_STATE_UNTAMED || !(entity instanceof EntityPlayer))
        			
        		);
    }
    

    //TODO use MoCAnimal instead
    @Override
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
        if (getKittyState() == KITTY_STATE_KITTEN)
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

        case KITTY_STATE_WANTS_FOOD:
        	return MoCreatures.proxy.getTexture("emoticon3.png");

        case KITTY_STATE_USING_KITTY_BED:
            return MoCreatures.proxy.getTexture("emoticon4.png");

        case KITTY_STATE_LOOKING_FOR_LITTER_BOX:
            return MoCreatures.proxy.getTexture("emoticon5.png");

        case KITTY_STATE_NORMAL_HAPPY:
            return MoCreatures.proxy.getTexture("emoticon7.png");

        case KITTY_STATE_PLAYING_WITH_WOOLBALL:
            return MoCreatures.proxy.getTexture("emoticon8.png");

        case KITTY_STATE_IN_LOVE_STAGE_ONE:
            return MoCreatures.proxy.getTexture("emoticon9.png");

        case KITTY_STATE_KITTEN:
            return MoCreatures.proxy.getTexture("emoticon10.png");

        case KITTY_STATE_INTERESTED:
            return MoCreatures.proxy.getTexture("emoticon11.png");

        case KITTY_STATE_SLEEPING:
            return MoCreatures.proxy.getTexture("emoticon12.png");

        case KITTY_STATE_ANGRY:
            return MoCreatures.proxy.getTexture("emoticon13.png");

        case KITTY_STATE_TREE:
            return MoCreatures.proxy.getTexture("emoticon16.png");

        case KITTY_STATE_GOING_INTO_LABOUR:
            return MoCreatures.proxy.getTexture("emoticon17.png");

        case KITTY_STATE_IN_LOVE_STAGE_TWO:
            return MoCreatures.proxy.getTexture("emoticon9.png");

        case KITTY_STATE_GIVING_BIRTH_STAGE_ONE:
            return MoCreatures.proxy.getTexture("emoticon19.png");

        case KITTY_STATE_GIVING_BIRTH_STAGE_TWO:
            return MoCreatures.proxy.getTexture("emoticon19.png");

        case KITTY_STATE_PARENTING:
            return MoCreatures.proxy.getTexture("emoticon10.png");

        case 0: // '\0'
        case KITTY_STATE_UNTAMED: // '\001'
        case KITTY_STATE_PRETAMED: // '\002'
        case KITTY_STATE_USING_LITTER_BOX : // '\006'
        case KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE: // '\016'
        case KITTY_STATE_ON_PLAYERS_BACK: // '\017'
        default:
            return MoCreatures.proxy.getTexture("emoticon1.png");
        }
    }

    @Override
    protected String getHurtSound()
    {
        if (getKittyState() == KITTY_STATE_KITTEN)
        {
            return "mocreatures:kittenhurt";
        }
        else
        {
            return "mocreatures:kittyhurt";
        }
    }

    public EntityLiving findKittyStuffNearby(Entity entity, double searchDistance, boolean lookForKittyLitterBox)
    {
        double distanceToTheTargetEntity = -1D;
        Object targetEntityObject = null;
        List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(entity, boundingBox.expand(searchDistance, searchDistance, searchDistance));
        for (int index = 0; index < listOfEntitiesNearby.size(); index++)
        {
            Entity entityNearby = (Entity) listOfEntitiesNearby.get(index);
            if (lookForKittyLitterBox)
            {
                if (!(entityNearby instanceof MoCEntityLitterBox))
                {
                    continue;
                }
                MoCEntityLitterBox entityLitterBox = (MoCEntityLitterBox) entityNearby;
                if (entityLitterBox.getUsedLitter())
                {
                    continue;
                }
                double distanceToEntityNearby = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
                if (((searchDistance < 0.0D) || (distanceToEntityNearby < (searchDistance * searchDistance))) && ((distanceToTheTargetEntity == -1.0D) || (distanceToEntityNearby < distanceToTheTargetEntity)) && entityLitterBox.canEntityBeSeen(entity))
                {
                    distanceToTheTargetEntity = distanceToEntityNearby;
                    targetEntityObject = entityLitterBox;
                }
                continue;
            }
            if (!(entityNearby instanceof MoCEntityKittyBed))
            {
                continue;
            }
            MoCEntityKittyBed entityKittyBed = (MoCEntityKittyBed) entityNearby;
            double distanceToEntityNearby = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((searchDistance < 0.0D) || (distanceToEntityNearby < (searchDistance * searchDistance))) && ((distanceToTheTargetEntity == -1.0D) || (distanceToEntityNearby < distanceToTheTargetEntity)) && entityKittyBed.canEntityBeSeen(entity))
            {
                distanceToTheTargetEntity = distanceToEntityNearby;
                targetEntityObject = entityKittyBed;
            }
        }

        return ((EntityLiving) (targetEntityObject));
    }

    @Override
    protected String getLivingSound()
    {
        if (getKittyState() == KITTY_STATE_USING_KITTY_BED)
        {
            if (ridingEntity != null)
            {
                MoCEntityKittyBed entityKittyBed = (MoCEntityKittyBed) ridingEntity;
                if ((entityKittyBed != null) && !entityKittyBed.getHasMilk()) { return "mocreatures:kittyeatingm"; }
                if ((entityKittyBed != null) && !entityKittyBed.getHasFood()) { return "mocreatures:kittyeatingf"; }
            }
            return null;
        }
        if (getKittyState() == KITTY_STATE_USING_LITTER_BOX ) { return "mocreatures:kittylitter"; }
        if (getKittyState() == KITTY_STATE_WANTS_FOOD) { return "mocreatures:kittyfood"; }
        if (getKittyState() == KITTY_STATE_KITTEN) { return "mocreatures:kittengrunt"; }
        if (getKittyState() == KITTY_STATE_ANGRY) { return "mocreatures:kittyupset"; }
        if (getKittyState() == KITTY_STATE_GOING_INTO_LABOUR ) { return "mocreatures:kittytrapped"; }
        if ((getKittyState() == KITTY_STATE_IN_LOVE_STAGE_TWO) || (getKittyState() == KITTY_STATE_SLEEPING))
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
            if (getKittyState() == KITTY_STATE_KITTEN) { return (yOffset - 1.1F); }
            if (isUpsideDown()) { return (yOffset - 1.7F); }
            if (isOnPlayersBack()) { return (yOffset - 1.5F); }
        }

        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            if (getKittyState() == KITTY_STATE_KITTEN) { return (yOffset + 0.3F); }
            if (isUpsideDown()) { return (yOffset - 0.1F); }
            if (isOnPlayersBack()) { return (yOffset + 0.1F); }
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
        if ((getKittyState() == KITTY_STATE_PRETAMED) && (itemstack != null) && (itemstack.getItem() == MoCreatures.medallion))
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
                changeKittyStateTo(KITTY_STATE_WANTS_FOOD);
                return true;
            }
            return false;
        }
        if ((getKittyState() == KITTY_STATE_NORMAL_HAPPY) && (itemstack != null) && 
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
            playSound("mocreatures:kittyeatingf", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            heal(8);
            changeKittyStateTo(KITTY_STATE_IN_LOVE_STAGE_ONE);
            return true;
        }
        if ((getKittyState() == KITTY_STATE_INTERESTED) && (itemstack != null) && (itemstack.getItem() == MoCreatures.woolball) && MoCreatures.isServer())
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            setKittyState(KITTY_STATE_PLAYING_WITH_WOOLBALL);
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
        if ((getKittyState() == KITTY_STATE_ANGRY) && (itemstack != null) && 
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
            playSound("mocreatures:kittyeatingf", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            heal(5);
            changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
            return true;
        }
        if ((itemstack != null) && (getKittyState() > KITTY_STATE_PRETAMED) && ((itemstack.getItem() == MoCreatures.medallion)))
        {
            if (MoCreatures.isServer())
            {
                MoCTools.tameWithName(entityPlayer, this);
            }

            return true;
        }
        if ((itemstack != null) && (getKittyState() > KITTY_STATE_PRETAMED) && isPickable() && (itemstack.getItem() == Items.lead) && (entityPlayer.ridingEntity == null))
        {
            changeKittyStateTo(KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE);
            if (MoCreatures.isServer())
            {
                mountEntity(entityPlayer);
            }
            return true;
        }
        if ((itemstack != null) && (getKittyState() > KITTY_STATE_PRETAMED) && isWhipeable() && (itemstack.getItem() == MoCreatures.whip))
        {
            setSitting(!getIsSitting());
            return true;
        }
        if (
        		(
	    			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemstack == null)
	    			|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
        		)
        		&& !(entityPlayer.isSneaking()) && (getKittyState() == KITTY_STATE_KITTEN) && (ridingEntity != null)
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
	        	&& !(entityPlayer.isSneaking()) && (getKittyState() > KITTY_STATE_PRETAMED) && isPickable() && (entityPlayer.ridingEntity == null)
	        )
        {
            changeKittyStateTo(KITTY_STATE_ON_PLAYERS_BACK);
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
        		&& !(entityPlayer.isSneaking()) && (getKittyState() == KITTY_STATE_ON_PLAYERS_BACK)
        	)
        {
            changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
            return true;
        }
        if ((getKittyState() == KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE) && ridingEntity != null)
        {
            changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
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
        return getIsSitting() || (getKittyState() == KITTY_STATE_USING_LITTER_BOX ) || ((getKittyState() == KITTY_STATE_TREE) && getOnTree()) || (getKittyState() == KITTY_STATE_SLEEPING) || (getKittyState() == KITTY_STATE_GOING_INTO_LABOUR ) || (getKittyState() == KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE) || (getKittyState() == KITTY_STATE_GIVING_BIRTH_STAGE_TWO) || (getKittyState() == 23);
    }

    @Override
    public boolean isOnLadder()
    {
        if (getKittyState() == KITTY_STATE_TREE)
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
            if (!getIsAdult() && (getKittyState() != KITTY_STATE_KITTEN))
            {
                setKittyState(KITTY_STATE_KITTEN);
            }
            if (getKittyState() != KITTY_STATE_SLEEPING)
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
            	MoCEntityKittyBed entityKittyBedNearby = (MoCEntityKittyBed) findKittyStuffNearby(this, 18D, false);
            	
                if ((entityKittyBedNearby != null) && (entityKittyBedNearby.riddenByEntity == null))
                {
                	float distanceToKittybed = entityKittyBedNearby.getDistanceToEntity(this);
                    if (distanceToKittybed > 2.0F)
                    {
                        getMyOwnPath(entityKittyBedNearby, distanceToKittybed);
                    }
                    if (distanceToKittybed < 2.0F) //sleep in kittybed
                    {
                        mountEntity(entityKittyBedNearby);
                        setKittyState(KITTY_STATE_SLEEPING);
                    }
                }
                if (entityKittyBedNearby == null)
                {	if ((rand.nextInt(500) == 0)) //sleep on the spot
                    {
                		setKittyState(KITTY_STATE_SLEEPING); 
                    }
                }
            }
            
            if (getIsTamed() && !isNight() && (getKittyState() == KITTY_STATE_SLEEPING))
            {
            	changeKittyStateTo(KITTY_STATE_KITTEN);
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
	
	            case KITTY_STATE_UNTAMED: // '\001'
	                if (rand.nextInt(10) == 0)
	                {
	                    EntityLivingBase scaryEntityNearby = getScaryEntity(6D);
	                    if (scaryEntityNearby != null)
	                    {
	                        MoCTools.runAway(this, scaryEntityNearby);
	                    }
	                    break;
	                }
	                
	                if (!getIsHungry() || (rand.nextInt(10) != 0))
	                {
	                    break;
	                }
	                
	                EntityItem closestEntityItemForTaming = getClosestItem(this, 10D, Items.cooked_fished, Items.cooked_fished);
	               
	                if (closestEntityItemForTaming == null)
	                {
	                    break;
	                }
	                
	                float distanceToEntityItemForTaming = closestEntityItemForTaming.getDistanceToEntity(this);
	                
	                if (distanceToEntityItemForTaming > 2.0F)
	                {
	                    getMyOwnPath(closestEntityItemForTaming, distanceToEntityItemForTaming);
	                }
	                
	                if ((distanceToEntityItemForTaming < 2.0F) && (closestEntityItemForTaming != null) && (deathTime == 0))
	                {
	                    closestEntityItemForTaming.setDead();
	                    playSound("mocreatures:kittyeatingf", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
	                    setHungry(false);
	                    setKittyState(KITTY_STATE_PRETAMED);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_PRETAMED: // '\002'
	                EntityLivingBase scaryEntityNearby = getScaryEntity(6D);
	                
	                if (scaryEntityNearby != null)
	                {
	                    MoCTools.runAway(this, scaryEntityNearby);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_WANTS_FOOD:
	                kittyTimer++;
	                if (kittyTimer > 500)
	                {
	                    if (rand.nextInt(500) == 0)
	                    {
	                        changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                        break;
	                    }
	                }
	                
	                if (rand.nextInt(20) != 0)
	                {
	                    break;
	                }
	                
	                MoCEntityKittyBed entityKittyBedNearby = (MoCEntityKittyBed) findKittyStuffNearby(this, 18D, false);
	                
	                if ((entityKittyBedNearby == null) || (entityKittyBedNearby.riddenByEntity != null) || (!entityKittyBedNearby.getHasMilk() && !entityKittyBedNearby.getHasFood()))
	                {
	                    break;
	                }
	                
	                float distanceToNearbyKittyBed = entityKittyBedNearby.getDistanceToEntity(this);
	                
	                if (distanceToNearbyKittyBed > 2.0F)
	                {
	                    getMyOwnPath(entityKittyBedNearby, distanceToNearbyKittyBed);
	                }
	                
	                if (distanceToNearbyKittyBed < 2.0F)
	                {
	                    changeKittyStateTo(KITTY_STATE_USING_KITTY_BED);
	                    mountEntity(entityKittyBedNearby);
	                    setSitting(true);
	                    
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_USING_KITTY_BED:
	                if (ridingEntity != null)
	                {
	                    MoCEntityKittyBed kittyBedNearby = (MoCEntityKittyBed) ridingEntity;
	                    if ((kittyBedNearby != null) && !kittyBedNearby.getHasMilk() && !kittyBedNearby.getHasFood())
	                    {
	                        heal(5);
	                        changeKittyStateTo(KITTY_STATE_LOOKING_FOR_LITTER_BOX);
	                    }
	                }
	                
	                else
	                {
	                    heal(5);
	                    changeKittyStateTo(KITTY_STATE_LOOKING_FOR_LITTER_BOX);
	                }
	                
	                if (rand.nextInt(2500) == 0)
	                {
	                    heal(5);
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_LOOKING_FOR_LITTER_BOX:
	                kittyTimer++;
	                if ((kittyTimer > 2000) && (rand.nextInt(1000) == 0))
	                {
	                    changeKittyStateTo(KITTY_STATE_ANGRY);
	                    break;
	                }
	                
	                if (rand.nextInt(KITTY_STATE_GIVING_BIRTH_STAGE_TWO) != 0)
	                {
	                    break;
	                }
	                
	                MoCEntityLitterBox entityLitterBoxNearby = (MoCEntityLitterBox) findKittyStuffNearby(this, 18D, true);
	                
	                if ((entityLitterBoxNearby == null) || (entityLitterBoxNearby.riddenByEntity != null) || entityLitterBoxNearby.getUsedLitter())
	                {
	                    break;
	                }
	                
	                
	                float distanceToLitterBoxNearby = entityLitterBoxNearby.getDistanceToEntity(this);
	                
	                if (distanceToLitterBoxNearby > 2.0F)
	                {
	                    getMyOwnPath(entityLitterBoxNearby, distanceToLitterBoxNearby);
	                }
	                
	                if (distanceToLitterBoxNearby < 2.0F)
	                {
	                    changeKittyStateTo(KITTY_STATE_USING_LITTER_BOX );
	                    mountEntity(entityLitterBoxNearby);
	                }
	                
	                break;
	
	                
	                
	            case KITTY_STATE_USING_LITTER_BOX : // '\006'
	                kittyTimer++;
	                if (kittyTimer <= 300)
	                {
	                    break;
	                }
	                
	                MoCEntityLitterBox litterBoxNearby = (MoCEntityLitterBox) ridingEntity;
	                
	                if (litterBoxNearby != null)
	                {
	                    litterBoxNearby.setUsedLitter(true);
	                    litterBoxNearby.litterTime = 0;
	                }
	                
	                changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                break;
	
	                
	                
	            case KITTY_STATE_NORMAL_HAPPY:
	                if (getIsSitting())
	                {
	                    break;
	                }
	                
	                if (rand.nextInt(KITTY_STATE_GIVING_BIRTH_STAGE_TWO) == 0)
	                {
	                    EntityPlayer entityPlayerNearby = worldObj.getClosestPlayerToEntity(this, 12D);
	                    if (entityPlayerNearby != null)
	                    {
	                        ItemStack itemstackHeldByPlayer = entityPlayerNearby.inventory.getCurrentItem();
	                        if ((itemstackHeldByPlayer != null) && (itemstackHeldByPlayer.getItem() == MoCreatures.woolball))
	                        {
	                            changeKittyStateTo(KITTY_STATE_INTERESTED);
	                            break;
	                        }
	                    }
	                }
	                
	                if (inWater && (rand.nextInt(500) == 0))
	                {
	                    changeKittyStateTo(KITTY_STATE_ANGRY);
	                    break;
	                }
	                
	                if ((rand.nextInt(500) == 0) && !worldObj.isDaytime())
	                {
	                    changeKittyStateTo(KITTY_STATE_SLEEPING);
	                    break;
	                }
	                
	                if (rand.nextInt(2000) == 0)
	                {
	                    changeKittyStateTo(KITTY_STATE_WANTS_FOOD);
	                    break;
	                }
	                
	                if (rand.nextInt(4000) == 0)
	                {
	                    changeKittyStateTo(KITTY_STATE_TREE);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_PLAYING_WITH_WOOLBALL:
	                if (inWater && rand.nextInt(200) == 0)
	                {
	                    changeKittyStateTo(KITTY_STATE_ANGRY);
	                    break;
	                }
	                
	                if ((entityToAttack != null) && (entityToAttack instanceof EntityItem))
	                {
	                    float distanceToEntityItem = getDistanceToEntity(entityToAttack);
	                    if (distanceToEntityItem < 1.5F)
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
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_IN_LOVE_STAGE_ONE:
	                kittyTimer++;
	                if (rand.nextInt(50) == 0)
	                {
	                    List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 6D, 16D));
	                    int minimumListSize = 0;
	                    do
	                    {
	                        if (minimumListSize >= listOfEntitiesNearby.size())
	                        {
	                            break;
	                        }
	                        Entity entityNearby = (Entity) listOfEntitiesNearby.get(minimumListSize);
	                        if ((entityNearby instanceof MoCEntityKitty) && (entityNearby instanceof MoCEntityKitty) && (((MoCEntityKitty) entityNearby).getKittyState() == KITTY_STATE_IN_LOVE_STAGE_ONE))
	                        {
	                            changeKittyStateTo(KITTY_STATE_IN_LOVE_STAGE_TWO);
	                            entityToAttack = entityNearby;
	                            ((MoCEntityKitty) entityNearby).changeKittyStateTo(KITTY_STATE_IN_LOVE_STAGE_TWO);
	                            ((MoCEntityKitty) entityNearby).entityToAttack = this;
	                            break;
	                        }
	                        minimumListSize++;
	                    } while (true);
	                }
	                
	                if (kittyTimer > 2000)
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_KITTEN:
	                if (getIsAdult())
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                    break;
	                }
	                
	                if (rand.nextInt(50) == 0)
	                {
	                    List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(16D, 6D, 16D));
	                    for (int index = 0; index < listOfEntitiesNearby.size(); index++)
	                    {
	                        Entity entityNearby = (Entity) listOfEntitiesNearby.get(index);
	                        if (!(entityNearby instanceof MoCEntityKitty) || (((MoCEntityKitty) entityNearby).getKittyState() != KITTY_STATE_PARENTING))
	                        {
	                            continue;
	                        }
	                        float distanceToOtherKitty = getDistanceToEntity(entityNearby);
	                        if (distanceToOtherKitty > 12F)
	                        {
	                            entityToAttack = entityNearby;
	                        }
	                    }
	
	                }
	                
	                if ((entityToAttack == null) && (rand.nextInt(100) == 0))
	                {
	                    if (rand.nextInt(10) < 7) //70% chance
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
	                    float distanceToEntityItemToAttack = getDistanceToEntity(entityToAttack);
	                    if (distanceToEntityItemToAttack < 1.5F)
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
	                    float distanceToOtherKittyToAttack = getDistanceToEntity(entityToAttack);
	                    if (distanceToOtherKittyToAttack < 2.0F)
	                    {
	                        swingArm();
	                        setPathToEntity(null);
	                    }
	                }
	                
	                if ((entityToAttack == null) || !(entityToAttack instanceof EntityPlayer))
	                {
	                    break;
	                }
	                
	                float distanceToPlayerToAttack = getDistanceToEntity(entityToAttack);
	                
	                if ((distanceToPlayerToAttack < 2.0F) && (rand.nextInt(20) == 0))
	                {
	                    swingArm();
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_INTERESTED:
	                EntityPlayer closestEntityPlayerNearby = worldObj.getClosestPlayerToEntity(this, 18D);
	                
	                if ((closestEntityPlayerNearby == null) || (rand.nextInt(10) != 0))
	                {
	                    break;
	                }
	                
	                ItemStack itemstackHeldByPlyer = closestEntityPlayerNearby.inventory.getCurrentItem();
	                
	                if ((itemstackHeldByPlyer == null) || ((itemstackHeldByPlyer != null) && (itemstackHeldByPlyer.getItem() != MoCreatures.woolball)))
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                    break;
	                }
	                
	                float distanceToPlayer = closestEntityPlayerNearby.getDistanceToEntity(this);
	                
	                if (distanceToPlayer > 5F)
	                {
	                    getPathOrWalkableBlock(closestEntityPlayerNearby, distanceToPlayer);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_SLEEPING:
	                
	            	kittyTimer++;
	            	
	                if (worldObj.isDaytime() || ((kittyTimer > 500) && (rand.nextInt(500) == 0)))
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                    break;
	                }
	                
	                setSitting(true);
	                
	                if ((rand.nextInt(80) == 0) || !onGround)
	                {
	                    super.onLivingUpdate();
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_ANGRY:
	                setHungry(false);
	                entityToAttack = worldObj.getClosestPlayerToEntity(this, 18D);
	                if (entityToAttack != null)
	                {
	                    float distanceToEntityToAttack = getDistanceToEntity(entityToAttack);
	                    if (distanceToEntityToAttack < 1.5F)
	                    {
	                        swingArm();
	                        if (rand.nextInt(20) == 0)
	                        {
	                            madTimer--;
	                            entityToAttack.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
	                            if (madTimer < 1)
	                            {
	                                changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                                madTimer = rand.nextInt(5);
	                            }
	                        }
	                    }
	                    if (rand.nextInt(500) == 0)
	                    {
	                        changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                    }
	                }
	                
	                else
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE:
	                if (onGround)
	                {
	                    changeKittyStateTo(KITTY_STATE_ANGRY);
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
	                
	                EntityPlayer playerThatThisKittyIsMountedOn = (EntityPlayer) ridingEntity;
	                
	                if (playerThatThisKittyIsMountedOn == null)
	                {
	                    changeKittyStateTo(KITTY_STATE_ANGRY);
	                    break;
	                }
	                
	                ItemStack itemstackHeldByPlayer = playerThatThisKittyIsMountedOn.inventory.getCurrentItem();
	                
	                if (itemstackHeldByPlayer == null || ((itemstackHeldByPlayer != null) && (itemstackHeldByPlayer.getItem() != Items.lead)))
	                {
	                    changeKittyStateTo(KITTY_STATE_ANGRY);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_ON_PLAYERS_BACK: // '\017'
	                if (onGround)
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                
	                if (ridingEntity != null)
	                {
	                    rotationYaw = ridingEntity.rotationYaw + 90F;
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_TREE:
	                kittyTimer++;
	                if ((kittyTimer > 500) && !getOnTree())
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                if (!getOnTree())
	                {
	                    if (!hasFoundTree && (rand.nextInt(50) == 0))
	                    {
	                        int coordinatesOfNearestLogBlock[] = MoCTools.ReturnNearestMaterialCoord(this, Material.wood, Double.valueOf(18D), 4D);
	                        if (coordinatesOfNearestLogBlock[0] != -1)
	                        {
	                            int index = 0;
	                            do
	                            {
	                                if (index >= 20)
	                                {
	                                    break;
	                                }
	                                Block blockThatIsNearLogBlock = worldObj.getBlock(coordinatesOfNearestLogBlock[0], coordinatesOfNearestLogBlock[1] + index, coordinatesOfNearestLogBlock[2]);
	                                if ((blockThatIsNearLogBlock.getMaterial() == Material.leaves))
	                                {
	                                    hasFoundTree = true;
	                                    treeCoord[0] = coordinatesOfNearestLogBlock[0];
	                                    treeCoord[1] = coordinatesOfNearestLogBlock[1];
	                                    treeCoord[2] = coordinatesOfNearestLogBlock[2];
	                                    break;
	                                }
	                                index++;
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
	                        changeKittyStateTo(KITTY_STATE_GOING_INTO_LABOUR );
	                        treeCoord[0] = -1;
	                        treeCoord[1] = -1;
	                        treeCoord[2] = -1;
	                        break label0;
	                    }
	                    i4++;
	                } while (true);
	
	                
	                
	            case KITTY_STATE_GOING_INTO_LABOUR :
	                EntityPlayer closestPlayerNearby = worldObj.getClosestPlayerToEntity(this, 2D);
	                if (closestPlayerNearby != null)
	                {
	                    changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_IN_LOVE_STAGE_TWO:
	                if ((entityToAttack == null) || !(entityToAttack instanceof MoCEntityKitty))
	                {
	                    changeKittyStateTo(KITTY_STATE_IN_LOVE_STAGE_ONE);
	                    break;
	                }
	                
	                MoCEntityKitty otherKittyToMateWith = (MoCEntityKitty) entityToAttack;
	                
	                if ((otherKittyToMateWith != null) && (otherKittyToMateWith.getKittyState() == KITTY_STATE_IN_LOVE_STAGE_TWO))
	                {
	                    if (rand.nextInt(50) == 0)
	                    {
	                        swingArm();
	                    }
	                    float distanceToOtherKittyToMateWith = getDistanceToEntity(otherKittyToMateWith);
	                    if (distanceToOtherKittyToMateWith < 5F)
	                    {
	                        kittyTimer++;
	                    }
	                    if ((kittyTimer > 500) && (rand.nextInt(50) == 0))
	                    {
	                        ((MoCEntityKitty) entityToAttack).changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                        changeKittyStateTo(KITTY_STATE_GIVING_BIRTH_STAGE_ONE);
	                    }
	                }
	                
	                else
	                {
	                    changeKittyStateTo(KITTY_STATE_IN_LOVE_STAGE_ONE);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_GIVING_BIRTH_STAGE_ONE:
	                if (rand.nextInt(20) != 0)
	                {
	                    break;
	                }
	                
	                MoCEntityKittyBed kittyBedNearby = (MoCEntityKittyBed) findKittyStuffNearby(this, 18D, false);
	                
	                if ((kittyBedNearby == null) || (kittyBedNearby.riddenByEntity != null))
	                {
	                    break;
	                }
	                
	                float distanceToKittyBedNearby = kittyBedNearby.getDistanceToEntity(this);
	                
	                if (distanceToKittyBedNearby > 2.0F)
	                {
	                    getMyOwnPath(kittyBedNearby, distanceToKittyBedNearby);
	                }
	                
	                if (distanceToKittyBedNearby < 2.0F)
	                {
	                    changeKittyStateTo(KITTY_STATE_GIVING_BIRTH_STAGE_TWO);
	                    mountEntity(kittyBedNearby);
	                }
	                break;
	
	                
	                
	            case KITTY_STATE_GIVING_BIRTH_STAGE_TWO:
	                if (ridingEntity == null)
	                {
	                    changeKittyStateTo(KITTY_STATE_GIVING_BIRTH_STAGE_ONE);
	                    break;
	                }
	                
	                rotationYaw = 180F;
	                kittyTimer++;
	                
	                if (kittyTimer <= 1000)
	                {
	                    break;
	                }
	                
	                MoCEntityKitty entityKittyToSpawn = new MoCEntityKitty(worldObj);
                    
	                int babyType = getType();
                    
	                if (rand.nextInt(2) == 0)
                    {
                        babyType = (rand.nextInt(8)+1);
                    }
	                
                    entityKittyToSpawn.setType(babyType);
                    entityKittyToSpawn.setPosition(posX, posY, posZ);
                    worldObj.spawnEntityInWorld(entityKittyToSpawn);
                    entityKittyToSpawn.setAdult(false);
                    entityKittyToSpawn.changeKittyStateTo(KITTY_STATE_KITTEN);
                    
                    EntityPlayer ownerOfKittyThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(getOwnerName());
                    
                    if (ownerOfKittyThatIsOnline != null)
                    {
                        MoCTools.tameWithName(ownerOfKittyThatIsOnline, entityKittyToSpawn);
                    }
	
	                changeKittyStateTo(KITTY_STATE_PARENTING);
	                break;
	
	                
	                
	            case KITTY_STATE_PARENTING:
	                kittyTimer++;
	                
	                if (kittyTimer > 2000)
	                {
	                    List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(24D, 8D, 24D));
	                    int numberOfKittensNearby = 0;
	                    for (int index = 0; index < listOfEntitiesNearby.size(); index++)
	                    {
	                        Entity entity2 = (Entity) listOfEntitiesNearby.get(index);
	                        if ((entity2 instanceof MoCEntityKitty) && (((MoCEntityKitty) entity2).getKittyState() == KITTY_STATE_KITTEN))
	                        {
	                            numberOfKittensNearby++;
	                        }
	                    }
	
	                    if (numberOfKittensNearby < 1)
	                    {
	                        changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
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
	                changeKittyStateTo(KITTY_STATE_NORMAL_HAPPY);
	                break;
            }
        }
        else
        {
            super.onLivingUpdate();
        }
    }

    public boolean isOnPlayersBack()
    {
        return getKittyState() == KITTY_STATE_ON_PLAYERS_BACK;
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

    private boolean isPickable()
    {
        return (getKittyState() != KITTY_STATE_ANGRY) && (getKittyState() != KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE) && (getKittyState() != KITTY_STATE_ON_PLAYERS_BACK) && (getKittyState() != KITTY_STATE_GIVING_BIRTH_STAGE_ONE) && (getKittyState() != KITTY_STATE_GIVING_BIRTH_STAGE_TWO) && (getKittyState() != KITTY_STATE_PARENTING);
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
    public boolean shouldRenderName()
    {
        return getDisplayName() && (getKittyState() != KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE) && (getKittyState() != KITTY_STATE_ON_PLAYERS_BACK) && (getKittyState() > KITTY_STATE_UNTAMED);
    }

    @Override
    public void setDead()
    {
        if (MoCreatures.isServer() && getKittyState() > KITTY_STATE_PRETAMED && getHealth() > 0 && !MoCreatures.isMobConfinementLoaded)   // the "!MoCreatures.isMobConfinementLoaded" allows setDead() to work on tamed creatures if the Mob Confinement mod is loaded. This is so that the mob confinement items don't duplicate tamed creatures when they try to store them.
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

    public boolean isUpsideDown()
    {
        return getKittyState() == KITTY_STATE_HELD_ON_PLAYERS_HAND_USING_ROPE;
    }

    public boolean isWhipeable()
    {
        return getKittyState() != KITTY_STATE_ANGRY;
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