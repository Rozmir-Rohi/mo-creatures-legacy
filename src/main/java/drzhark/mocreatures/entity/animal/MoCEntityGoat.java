package drzhark.mocreatures.entity.animal;

import cpw.mods.fml.common.registry.GameRegistry;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityGoat extends MoCEntityTameableAnimal {
    private boolean hungry;
    private boolean swingLeg;
    private boolean swingEar;
    private boolean swingTail;
    private boolean bleat;
    private boolean eating;
    private int bleatcount;
    private int attackingCounter;
    public int movecount;
    private int chargecount;
    private int tailcount; // 90 to -45
    private int earcount; // 20 to 40 default = 30
    private int eatcount;
    //private float moveSpeed;

   
    public MoCEntityGoat(World world)
    {
        super(world);
        setSize(1.4F, 0.9F);
        setMoCAge(70);
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isUpset - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // isCharging - 0 false 1 true
    }

    

    public boolean getUpset()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getCharging()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setUpset(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setCharging(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    @Override
    public void selectType()
    {
        /*
         * type 1 = baby
         * type 2 = female
         * type 3 = female 2
         * type 4 = female 3
         * type 5 = male 1
         * type 6 = male 2
         * type 7 = male 3
         */
        if (getType() == 0)
        {
            int i = rand.nextInt(100);
            if (i <= 15)
            {
                setType(1);
                setMoCAge(50);
            }
            else if (i <= 30)
            {
                setType(2);
                setMoCAge(70);
            }
            else if (i <= 45)
            {
                setType(3);
                setMoCAge(70);
            }
            else if (i <= 60)
            {
                setType(4);
                setMoCAge(70);
            }
            else if (i <= 75)
            {
                setType(5);
                setMoCAge(90);
            }
            else if (i <= 90)
            {
                setType(6);
                setMoCAge(90);
            }
            else
            {
                setType(7);
                setMoCAge(90);
            }
        }

    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("goat1.png");
        case 2:
            return MoCreatures.proxy.getTexture("goat2.png");
        case 3:
            return MoCreatures.proxy.getTexture("goat3.png");
        case 4:
            return MoCreatures.proxy.getTexture("goat4.png");
        case 5:
            return MoCreatures.proxy.getTexture("goat5.png");
        case 6:
            return MoCreatures.proxy.getTexture("goat6.png");
        case 7:
            return MoCreatures.proxy.getTexture("goat1.png");

        default:
            return MoCreatures.proxy.getTexture("goat1.png");
        }
    }

    public void becomeCalm()
    {
        entityToAttack = null;
        setUpset(false);
        setCharging(false);
        moveSpeed = 0.7F;
        attackingCounter = 0;
        chargecount = 0;
    }

    @Override
    protected void jump()
    {
        if (getType() == 1)
        {
            motionY = 0.41D;
        }
        else if (getType() < 5)
        {
            motionY = 0.45D;
        }
        else
        {
            motionY = 0.5D;
        }

        if (isPotionActive(Potion.jump))
        {
            motionY += (getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }
        if (isSprinting())
        {
            float f = rotationYaw * 0.01745329F;
            motionX -= MathHelper.sin(f) * 0.2F;
            motionZ += MathHelper.cos(f) * 0.2F;
        }
        isAirBorne = true;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (rand.nextInt(100) == 0)
        {
            setSwingEar(true);
        }

        if (rand.nextInt(80) == 0)
        {
            setSwingTail(true);
        }

        if (rand.nextInt(50) == 0)
        {
            setEating(true);
        }

        if ((hungry) && (rand.nextInt(20) == 0))
        {
            hungry = false;
        }
        if (getBleating())
        {
            bleatcount++;
            if (bleatcount > 15)
            {
                bleatcount = 0;
                setBleating(false);
            }

        }
        if (MoCreatures.isServer() && (getMoCAge() < 90 || getType() > 4 && getMoCAge() < 100) && rand.nextInt(500) == 0)
        {
            setMoCAge(getMoCAge() + 1);
            if (getType() == 1 && getMoCAge() > 70)
            {
                int i = rand.nextInt(6) + 2;
                setType(i);

            }
        }

        if (getUpset())
        {
            attackingCounter += (rand.nextInt(4)) + 2;
            if (attackingCounter > 75)
            {
                attackingCounter = 75;
            }

            if (rand.nextInt(500) == 0 || entityToAttack == null)
            {
                becomeCalm();
            }

            if (!getCharging() && rand.nextInt(35) == 0)
            {
                swingLeg();
            }

            if (!getCharging())
            {
                setPathToEntity(null);
            }

            if (entityToAttack != null)// && rand.nextInt(100)==0)
            {
                faceEntity(entityToAttack, 10F, 10F);
                if (rand.nextInt(80) == 0)
                {
                    setCharging(true);
                }
            }
        }

        if (getCharging())
        {
            chargecount++;
            if (chargecount > 120)
            {
                chargecount = 0;
                moveSpeed = 0.7F;
            }
            else
            {
                moveSpeed = 1.0F;
            }

            if (entityToAttack == null)
            {
                becomeCalm();
            }
        }

        if (!getUpset() && !getCharging())
        {
            EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 24D);
            if (entityPlayer1 != null)
            {// Behaviour that happens only close to player :)

                // is there food around? only check with player near
                EntityItem entityItem = getClosestEntityItem(this, 10D);
                if (entityItem != null)
                {
                    float distanceToEntityItem = entityItem.getDistanceToEntity(this);
                    if (distanceToEntityItem > 2.0F)
                    {
                        int entityItemPosX = MathHelper.floor_double(entityItem.posX);
                        int entityItemPosY = MathHelper.floor_double(entityItem.posY);
                        int entityItemPosZ = MathHelper.floor_double(entityItem.posZ);
                        
                        faceLocation(entityItemPosX, entityItemPosY, entityItemPosZ, 30F);

                        getMyOwnPath(entityItem, distanceToEntityItem);
                        return;
                    }
                    if ((distanceToEntityItem < 2.0F) && (entityItem != null) && (deathTime == 0) && rand.nextInt(50) == 0)
                    {
                        playSound("mocreatures:goateating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                        setEating(true);

                        entityItem.setDead();
                        return;
                    }
                }

                // find another goat nearby to play fight!
                if (getType() > 4 && rand.nextInt(200) == 0)
                {
                    MoCEntityGoat entityTarget = (MoCEntityGoat) getClosestEntityLiving(this, 14D);
                    
                    if (entityTarget != null)
                    {
                        setUpset(true);
                        entityToAttack = entityTarget;
                        entityTarget.setUpset(true);
                        entityTarget.entityToAttack = this;
                    }
                }

            }// end of close to player behavior
        }// end of !upset !charging
    }

    @Override
    public boolean isMyFollowFood(ItemStack itemStack)
    {
        Item item = null;
        
        if (itemStack != null)
        {
            item = itemStack.getItem();
        }
        return (item != null && isItemEdible(item));
    }

    @Override
    public int getTalkInterval()
    {
        if (hungry) { return 20; }

        return 120;
    }

    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (
        			!(entity instanceof MoCEntityGoat)
        			|| ((MoCEntityGoat) entity).getType() < 5
        			|| ((MoCEntityGoat) entity).roper != null
        		);
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getUpset() && !getCharging();
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 3.0D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY) && attackingCounter > 70)
        {
            attackTime = 30;

            attackingCounter = 30;

            playSound("mocreatures:goatsmack", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            if (entity instanceof MoCEntityGoat)
            {
                MoCTools.pushEntityBack(this, entity, 0.4F);
                if (rand.nextInt(10) == 0)
                {
                    becomeCalm();
                    ((MoCEntityGoat) entity).becomeCalm();
                }

            }
            else
            {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
                MoCTools.pushEntityBack(this, entity, 0.8F);
                if (rand.nextInt(3) == 0)
                {
                    becomeCalm();
                }
            }
        }
    }

    @Override
    public boolean isNotScared()
    {
        return getType() > 4;
    }

    private void swingLeg()
    {
        if (!getSwingLeg())
        {
            setSwingLeg(true);
            movecount = 0;
        }
    }

    public boolean getSwingLeg()
    {
        return swingLeg;
    }

    public void setSwingLeg(boolean flag)
    {
        swingLeg = flag;
    }

    public boolean getSwingEar()
    {
        return swingEar;
    }

    public void setSwingEar(boolean flag)
    {
        swingEar = flag;
    }

    public boolean getSwingTail()
    {
        return swingTail;
    }

    public void setSwingTail(boolean flag)
    {
        swingTail = flag;
    }

    public boolean getEating()
    {
        return eating;
    }

    @Override
    public void setEating(boolean flag)
    {
        eating = flag;
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
            
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && getType() > 4)
            {
                entityToAttack = entityThatAttackedThisCreature;
                setUpset(true);
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onUpdate()
    {

        if (getSwingLeg())
        {
            movecount += 5;
            if (movecount == 30)
            {
                playSound("mocreatures:goatdigg", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }

            if (movecount > 100)
            {
                setSwingLeg(false);
                movecount = 0;
            }
        }

        if (getSwingEar())
        {
            earcount += 5;
            if (earcount > 40)
            {
                setSwingEar(false);
                earcount = 0;
            }
        }

        if (getSwingTail())
        {
            tailcount += 15;
            if (tailcount > 135)
            {
                setSwingTail(false);
                tailcount = 0;
            }
        }

        if (getEating())
        {
            eatcount += 1;
            if (eatcount == 2)
            {
                EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 3D);
                if (entityPlayer1 != null)
                {
                    playSound("mocreatures:goateating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                }
            }
            if (eatcount > 25)
            {
                setEating(false);
                eatcount = 0;
            }
        }

        super.onUpdate();
    }

    public int legMovement()
    {
        if (!getSwingLeg()) { return 0; }

        if (movecount < 21) { return movecount * -1; }
        if (movecount < 70) { return movecount - 40; }
        return -movecount + 100;
    }

    public int earMovement()
    {
        // 20 to 40 default = 30
        if (!getSwingEar()) { return 0; }
        if (earcount < 11) { return earcount + 30; }
        if (earcount < 31) { return -earcount + 50; }
        return earcount - 10;
    }

    public int tailMovement()
    {
        // 90 to -45
        if (!getSwingTail()) { return 90; }

        return tailcount - 45;
    }

    public int mouthMovement()
    {
        if (!getEating()) { return 0; }
        if (eatcount < 6) { return eatcount; }
        if (eatcount < 16) { return -eatcount + 10; }
        return eatcount - 20;
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        ItemStack itemStack = entityPlayer.getHeldItem();
        if (itemStack != null && itemStack.getItem() == Items.bucket)
        {
            if (getType() > 4)
            {
                setUpset(true);
                entityToAttack = entityPlayer;
                return false;
            }
            if (getType() == 1) { return false; }

            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.milk_bucket));
            return true;
        }

        if (getIsTamed())
        {
            if ((itemStack != null) && (isItemEdible(itemStack.getItem())))
            {
                if (--itemStack.stackSize == 0)
                {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                }
                heal(5);
                playSound("mocreatures:goateating", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                return true;
            }
        }

        if (MoCreatures.isServer() && !getIsTamed() && (itemStack != null) && isItemEdible(itemStack.getItem()))
        {
            if (MoCTools.tameWithName(entityPlayer, this))
            {
                return true;
            }
        }

        return false;

    }

    public boolean getBleating()
    {
        return bleat && (getAttacking() == 0);
    }

    public void setBleating(boolean flag)
    {
        bleat = flag;
    }

    public int getAttacking()
    {
        return attackingCounter;
    }

    public void setAttacking(int flag)
    {
        attackingCounter = flag;
    }

    @Override
    public boolean shouldRenderName()
    {
        return getDisplayName();
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:goathurt";
    }

    @Override
    protected String getLivingSound()
    {
        setBleating(true);
        if (getType() == 1) { return "mocreatures:goatkid"; }
        if (getType() > 2 && getType() < 5) { return "mocreatures:goatfemale"; }

        return "mocreatures:goatgrunt";
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:goatdying";
    }

    @Override
    protected void dropFewItems(boolean hasEntityBeenHitByPlayer, int levelOfLootingEnchantmentUsedToKillThisEntity)
    {
        int randomAmount = rand.nextInt(3);
    
		if (MoCreatures.isNovacraftLoaded)
		{
			if (isBurning())
			{
				dropItem(GameRegistry.findItem("nova_craft", "cooked_chevon"), randomAmount);
			}
			else {dropItem(GameRegistry.findItem("nova_craft", "raw_chevon"), randomAmount);}
		}
        
        dropItem(Items.leather, randomAmount);
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
}