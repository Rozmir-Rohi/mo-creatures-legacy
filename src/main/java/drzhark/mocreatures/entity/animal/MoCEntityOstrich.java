package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.inventory.MoCAnimalChest;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityOstrich extends MoCEntityTameableAnimal {

    private int eggCounter;
    private int hidingCounter;
    public int mouthCounter;
    public int wingCounter;
    public int sprintCounter;
    public int jumpCounter;
    public int transformCounter;
    public int transformType;
    public boolean canLayEggs;

    public MoCAnimalChest localChest;
    public ItemStack localItemstack;

    public MoCEntityOstrich(World world)
    {
        super(world);
        setSize(1.0F, 1.6F);
        setMoCAge(35);
        roper = null;
        eggCounter = rand.nextInt(1000) + 1000;
        stepHeight = 1.0F;
        canLayEggs = false;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isRideable - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // eggWatch - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // hiding - 0 false 1 true
        dataWatcher.addObject(25, Byte.valueOf((byte) 0)); // helmet - 0 none
        dataWatcher.addObject(26, Byte.valueOf((byte) 0)); // flagcolor - 0 white
        dataWatcher.addObject(27, Byte.valueOf((byte) 0)); // bagged - 0 false 1 true
    }

    public boolean getIsRideable()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setRideable(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public boolean getEggWatching()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setEggWatching(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public boolean getHiding()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public void setHiding(boolean flag)
    {
        if (worldObj.isRemote) { return; }
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }

    public byte getHelmet()
    {
        return (dataWatcher.getWatchableObjectByte(25));
    }

    public void setHelmet(byte b)
    {
        dataWatcher.updateObject(25, Byte.valueOf(b));
    }
    
    public byte getFlagColor()
    {
        return (dataWatcher.getWatchableObjectByte(26));
    }

    public void setFlagColor(byte b)
    {
        dataWatcher.updateObject(26, Byte.valueOf(b));
    }

    public boolean getIsChested()
    {
        return (dataWatcher.getWatchableObjectByte(27) == 1);
    }

    public void setIsChested(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(27, Byte.valueOf(input));
    }
    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null);
    }

    @Override
    protected boolean isMovementCeased()
    {
        return (getHiding() || riddenByEntity != null);
    }

    @Override
    public boolean isNotScared()
    {
        return (getType() == 2 && entityToAttack != null) || (getType() > 2);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        //damage reduction
        if (getIsTamed() && getHelmet() != 0)
        {
            int damageReduction = 0;
            switch (getHelmet())
            {
	            case 5: // hide helmet
	            case 6: // fur helmet
	            case 1:
	                damageReduction = 1; // leather helmet
	                break;
	                
	            case 7: //croc helmet
	            case 2: // iron helmet
	                damageReduction = 2;
	                break;
	                
	            case 3: //gold helmet
	                damageReduction = 3;
	                break;
	                
	            case 4: //diamond helmet
	            case 9: //dirt scorpion helmet
	            case 10: //frost scorpion helmet
	            case 11: //cave scorpion helmet
	            case 12: // nether scorpion helmet
	                damageReduction = 4;
	                break;
            }
            damageTaken -= damageReduction;
            if (damageTaken <= 0) damageTaken = 1;
        }

        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();

            if ( ((riddenByEntity != null) && (entityThatAttackedThisCreature == riddenByEntity)) ) { return false; }
            
            if (entityThatAttackedThisCreature != null && getIsTamed() && (entityThatAttackedThisCreature instanceof EntityPlayer && (entityThatAttackedThisCreature.getCommandSenderName().equals(getOwnerName()))))
            { 
            	return false; 
            }

            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && getType() > 2)
            {
                entityToAttack = entityThatAttackedThisCreature;
                flapWings();
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
        dropMyStuff();
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && distanceToEntity < 2.0F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
        {
            attackTime = 20;
            openMouth();
            flapWings();
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
        }
    }

    public float calculateMaxHealth()
    {
        switch (getType())
        {
	        case 1:
	            return 10;
	        case 2:
	            return 15;
	        case 3:
	            return 20;
	        case 4:
	            return 20;
	        case 5:
	            return 20;
	
	        default:
	            return 20;
        }
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return riddenByEntity == null;
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            /**
             * 1 = chick 2 = female 3 = male 4 = albino male 5 = demon ostrich
             */
            int j = rand.nextInt(100);
            if (j <= (20))
            {
                setType(1);
            }
            else if (j <= (65))
            {
                setType(2);
            }
            else if (j <= (95))
            {
                setType(3);
            }
            else
            {
                setType(4);
            }
        }
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed());
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth());
        setHealth(getMaxHealth());
    }

    @Override
    public ResourceLocation getTexture()
    {
       if (transformCounter != 0 && transformType > 4)
        {
            String newText = "ostricha.png";
            if (transformType == 5)
            {
                newText = "ostriche.png";
            }
            if (transformType == 6)
            {
                newText = "ostrichf.png";
            }
            if (transformType == 7)
            {
                newText = "ostrichg.png";
            }
            if (transformType == 8)
            {
                newText = "ostrichh.png";
            }
            
            if ((transformCounter % 5) == 0) { return MoCreatures.proxy.getTexture(newText); }
            if (transformCounter > 50 && (transformCounter % 3) == 0) { return MoCreatures.proxy.getTexture(newText); }

            if (transformCounter > 75 && (transformCounter % 4) == 0) { return MoCreatures.proxy.getTexture(newText); }
        }

        switch (getType())
        {
	        case 1:
	            return MoCreatures.proxy.getTexture("ostrichc.png"); //chick
	        case 2:
	            return MoCreatures.proxy.getTexture("ostrichb.png"); //female
	        case 3:
	            return MoCreatures.proxy.getTexture("ostricha.png"); //male
	        case 4:
	            return MoCreatures.proxy.getTexture("ostrichd.png"); //albino
	        case 5:
	            return MoCreatures.proxy.getTexture("ostriche.png"); //nether
	        case 6:
	            return MoCreatures.proxy.getTexture("ostrichf.png"); //black wyvern
	        case 7:
	            return MoCreatures.proxy.getTexture("ostrichg.png"); //undead
	        case 8:
	            return MoCreatures.proxy.getTexture("ostrichh.png"); //unicorned
	        default:
	            return MoCreatures.proxy.getTexture("ostricha.png");
        }
    }

    @Override
    public double getCustomSpeed()
    {
        double ostrichSpeed = 0.8D;
        
        switch (getType())
        {
	        case 1:
	        	ostrichSpeed = 0.8;
	        	break;
	        case 2:
	        	ostrichSpeed = 0.8D;
	        	break;
	        case 3:
	        	ostrichSpeed = 1.1D;
	        	break;
	        case 4:
	        	ostrichSpeed = 1.3D;
	        	break;
	        case 5:
	        	ostrichSpeed = 1.4D;
	            isImmuneToFire = true;
	            break;
	            
	        default:
	            return 20;
        }
        
        
        if (sprintCounter > 0 && sprintCounter < 200)
        {
            ostrichSpeed *= 1.5D;
        }
        if (sprintCounter > 200)
        {
            ostrichSpeed *= 0.5D;
        }
        
        return ostrichSpeed;
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (getHiding())
        {
            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
        }

        if (mouthCounter > 0 && ++mouthCounter > 20)
        {
            mouthCounter = 0;
        }

        if (wingCounter > 0 && ++wingCounter > 80)
        {
            wingCounter = 0;
        }
        
        if (jumpCounter > 0 && ++jumpCounter > 8)
        {
            jumpCounter = 0;
        }

        if (sprintCounter > 0 && ++sprintCounter > 300)
        {
            sprintCounter = 0;
        }
        
        if (transformCounter > 0)
        {
            if (transformCounter == 40)
            {
                MoCTools.playCustomSound(this, "transform", worldObj);
            }

            if (++transformCounter > 100)
            {
                transformCounter = 0;
                if (transformType != 0)
                {
                    dropArmor();
                    setType(transformType);
                }
            }
        }
    }

    public void transform(int tType)
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), tType), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
        transformType = tType;
        if (riddenByEntity == null && transformType != 0)
        {
            dropArmor();
            transformCounter = 1;
        }
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType >= 5 && animationType < 9) //transform 5 - 8
        {
            transformType = animationType;
            transformCounter = 1;
        }
        
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        

        if (getIsTamed() && MoCreatures.isServer() && (rand.nextInt(300) == 0) && (getHealth() <= getMaxHealth()) && (deathTime == 0))
        {
            setHealth(getHealth() + 1);
        }

        if (MoCreatures.isServer())
        {
            //unicorn ostrich ramming!
            if (getType() == 8 && (sprintCounter > 0 && sprintCounter < 150) && (riddenByEntity != null))
            {
                MoCTools.buckleMobs(this, 2, 2D, worldObj);
            }
            //shy ostriches will run and hide
            if (!isNotScared() && fleeingTick > 0 && fleeingTick < 2)
            {
                fleeingTick = 0;
                setHiding(true);
                setPathToEntity(null);
            }

            if (getHiding())
            {
                //wild shy ostriches will hide their heads only for a short term
                //tamed ostriches will keep their heads hidden until the whip is used again
                if (++hidingCounter > 500 && !getIsTamed())
                {
                    setHiding(false);
                    hidingCounter = 0;
                }

            }

            //to add collision detection
            if (getType() == 1 && (rand.nextInt(200) == 0))
            {
                //when is chick and becomes adult, change over to different type
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                    setType(0);
                    selectType();
                }
            }

            //egg laying
            if (canLayEggs && (getType() == 2) && !getEggWatching() && --eggCounter <= 0 && rand.nextInt(5) == 0)// &&
            {
                EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 12D);
                if (entityPlayer1 != null)
                {
                    double distP = MoCTools.getSqDistanceTo(entityPlayer1, posX, posY, posZ);
                    if (distP < 10D)
                    {
                        int OstrichEggType = 30;
                        MoCEntityOstrich maleOstrich = getClosestMaleOstrich(this, 8D);
                        if (maleOstrich != null && rand.nextInt(100) < MoCreatures.proxy.ostrichEggDropChance)
                        {
                            MoCEntityEgg entityegg = new MoCEntityEgg(worldObj, OstrichEggType);
                            entityegg.setPosition(posX, posY, posZ);
                            worldObj.spawnEntityInWorld(entityegg);
    
                            if (!getIsTamed())
                            {
                                setEggWatching(true);
                                if (maleOstrich != null)
                                {
                                    maleOstrich.setEggWatching(true);
                                }
                                openMouth();
                            }
        
                            //TODO change sound
                            worldObj.playSoundAtEntity(this, "mob.chicken.plop", 1.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
                            //finds a male and makes it eggWatch as well
                            //MoCEntityOstrich entityOstrich = (MoCEntityOstrich) getClosestSpecificEntity(this, MoCEntityOstrich.class, 12D);
                            eggCounter = rand.nextInt(2000) + 2000;
                            canLayEggs = false;
                        }
                    }
                }
            }

            //egg protection
            if (getEggWatching())
            {
                //look for and protect eggs and move close
                MoCEntityEgg myEgg = (MoCEntityEgg) getScaryEntity(8D);
                if ((myEgg != null) && (MoCTools.getSqDistanceTo(myEgg, posX, posY, posZ) > 4D))
                {
                    PathEntity pathEntity = worldObj.getPathEntityToEntity(this, myEgg, 16F, true, false, false, true);
                    setPathToEntity(pathEntity);
                }
                if (myEgg == null) //didn't find egg
                {
                    setEggWatching(false);

                    EntityPlayer eggStealer = worldObj.getClosestPlayerToEntity(this, 10D);
                    if (eggStealer != null)
                    {
                        if (!getIsTamed() && worldObj.difficultySetting != worldObj.difficultySetting.PEACEFUL)
                        {
                            entityToAttack = eggStealer;
                            flapWings();
                        }
                    }
                }
            }
        }
    }

    protected MoCEntityOstrich getClosestMaleOstrich(Entity entity, double d)
    {
        double d1 = -1D;
        MoCEntityOstrich entityLiving = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if (!(entity1 instanceof MoCEntityOstrich) || ((entity1 instanceof MoCEntityOstrich) && ((MoCEntityOstrich) entity1).getType() < 3))
            {
                continue;
            }

            double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)))
            {
                d1 = d2;
                entityLiving = (MoCEntityOstrich) entity1;
            }
        }

        return entityLiving;
    }

    @Override
    public boolean entitiesThatAreScary(Entity entity)
    {
        return ((entity instanceof MoCEntityEgg) && (((MoCEntityEgg) entity).eggType == 30)

        );
    }
    
    public boolean isItemEdible(Item item) //healing foods
    {
        return (
        			item instanceof ItemSeeds
        			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
        			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
        			|| OreDictionary.getOreName(OreDictionary.getOreID(new ItemStack(item))) == "listAllseed" //BOP seeds or Palm's Harvest Seeds
        			|| OreDictionary.getOreName(OreDictionary.getOreID(new ItemStack(item))) == "foodRaisins" //GregTech6 seeds/raisins or Palm's Harvest raisins
    			);
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();

        if (getIsTamed() && (getType() > 1) && (itemstack != null) && !getIsRideable() && (itemstack.getItem() == MoCreatures.craftedSaddle || itemstack.getItem() == Items.saddle))
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            setRideable(true);
            return true;
        }

        if (!getIsTamed() && itemstack != null && getType() == 2 && itemstack.getItem() == Items.melon_seeds) //breeding item
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            openMouth();
            MoCTools.playCustomSound(this, "eating", worldObj);
            canLayEggs = true;
            return true;
        }

        //makes the ostrich stay by hiding their heads
        if ((itemstack != null) && (itemstack.getItem() == MoCreatures.whip) && getIsTamed() && (riddenByEntity == null))
        {
            setHiding(!getHiding());
            return true;
        }

        if ((itemstack != null) && getIsTamed() && getType()> 1 && itemstack.getItem() == MoCreatures.essenceDarkness)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            else
            {
                entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
            if (getType() == 6)
            {
                setHealth(getMaxHealth());
            }
            else
            {
                transform(6);
            }
            MoCTools.playCustomSound(this, "drinking", worldObj);
            return true;
        }

        if ((itemstack != null) && getIsTamed() && getType()> 1 && itemstack.getItem() == MoCreatures.essenceUndead)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            else
            {
                entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
            if (getType() == 7)
            {
                setHealth(getMaxHealth());
            }
            else
            {
                transform(7);
            }
            MoCTools.playCustomSound(this, "drinking", worldObj);
            return true;
        }

        if ((itemstack != null) && getIsTamed() && getType()> 1 && itemstack.getItem() == MoCreatures.essenceLight)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            else
            {
                entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
            if (getType() == 8)
            {
                setHealth(getMaxHealth());
            }
            else
            {
                transform(8);
            }
            MoCTools.playCustomSound(this, "drinking", worldObj);
            return true;
        }

        if ((itemstack != null) && getIsTamed() && getType()> 1 && itemstack.getItem() == MoCreatures.essenceFire)
        {
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
            }
            else
            {
                entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
            }
            if (getType() == 5)
            {
                setHealth(getMaxHealth());
            }
            else
            {
                transform(5);
            }
            MoCTools.playCustomSound(this, "drinking", worldObj);
            return true;
        }
        if (getIsTamed() && getIsChested() && (getType() > 1) && itemstack!= null && itemstack.getItem() == Item.getItemFromBlock(Blocks.wool))
        {
            int colorInt = (itemstack.getItemDamage());
            if (colorInt == 0) colorInt = 16;
            if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
            dropFlag();
            setFlagColor((byte)colorInt);
            entityPlayer.addStat(MoCAchievements.ostrich_flag, 1);
            return true;
        }
        
        if ((itemstack != null) && (getType() > 1) && getIsTamed() && !getIsChested() && (itemstack.getItem() == Item.getItemFromBlock(Blocks.chest)))
        {
             if (--itemstack.stackSize == 0)
            {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }

            entityPlayer.inventory.addItemStackToInventory(new ItemStack(MoCreatures.key));
            setIsChested(true);
            entityPlayer.addStat(MoCAchievements.ostrich_chest, 1);
            return true;
        }
        
        if ((itemstack != null) && (itemstack.getItem() == MoCreatures.key) && getIsChested())
        {
            // if first time opening horse chest, we must initialize it
            if (localChest == null)
            {
                localChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.OstrichChest"), 9);
            }
            // only open this chest on server side
            if (MoCreatures.isServer())
            {
                entityPlayer.displayGUIChest(localChest);
            }
            return true;
        }

        if (getIsTamed() && (getType() > 1) && itemstack!= null)
        {
            
            Item item = itemstack.getItem();
            if (item instanceof ItemArmor)
            {
                byte helmetType = 0;
                if (itemstack.getItem() == Items.leather_helmet)
                {
                    helmetType = 1;
                }
                else if (itemstack.getItem() == Items.iron_helmet)
                {
                    helmetType = 2;
                }
                else if (itemstack.getItem() == Items.golden_helmet)
                {
                    helmetType = 3;
                }
                else if (itemstack.getItem() == Items.diamond_helmet)
                {
                    helmetType = 4;
                }
                else if (itemstack.getItem() == MoCreatures.helmetHide)
                {
                    helmetType = 5;
                }
                else if (itemstack.getItem() == MoCreatures.helmetFur)
                {
                    helmetType = 6;
                }
                else if (itemstack.getItem() == MoCreatures.helmetCroc)
                {
                    helmetType = 7;
                }
                /*else if (itemstack.getItem() == MoCreatures.helmetGreen)
                {
                    helmetType = 8;
                }*/
                else if (itemstack.getItem() == MoCreatures.scorpHelmetDirt)
                {
                    helmetType = 9;
                }
                else if (itemstack.getItem() == MoCreatures.scorpHelmetFrost)
                {
                    helmetType = 10;
                }
                else if (itemstack.getItem() == MoCreatures.scorpHelmetCave)
                {
                    helmetType = 11;
                }
                else if (itemstack.getItem() == MoCreatures.scorpHelmetNether)
                {
                    helmetType = 12;
                }

                if (helmetType != 0)
                {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                    dropArmor();
                    MoCTools.playCustomSound(this, "armoroff", worldObj);
                    setHelmet(helmetType);
                    entityPlayer.addStat(MoCAchievements.ostrich_helmet, 1);
                    return true;
                }
               
            }
        }
        if ((itemstack == null) && getIsRideable() && getIsAdult() && (riddenByEntity == null))
        {
            entityPlayer.rotationYaw = rotationYaw;
            entityPlayer.rotationPitch = rotationPitch;
            setHiding(false);
            if (!worldObj.isRemote && (riddenByEntity == null || riddenByEntity == entityPlayer))
            {
                entityPlayer.mountEntity(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Drops a block of the color of the flag if carrying one
     */
    private void dropFlag() 
    {
        if (MoCreatures.isServer() && getFlagColor() != 0)
        {
            int color = getFlagColor();
            if (color == 16) color = 0;
            EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Blocks.wool, 1, color));
            entityItem.delayBeforeCanPickup = 10;
            worldObj.spawnEntityInWorld(entityItem);
            setFlagColor((byte)0);
        }
    }

    private void openMouth()
    {
        mouthCounter = 1;
    }

    private void flapWings()
    {
        wingCounter = 1;
    }

    @Override
    protected String getHurtSound()
    {
        openMouth();
        return "mocreatures:ostrichhurt";
    }

    @Override
    protected String getLivingSound()
    {
        openMouth();
        if (getType() == 1) { return "mocreatures:ostrichchick"; }

        return "mocreatures:ostrichgrunt";
    }

    @Override
    protected String getDeathSound()
    {
        openMouth();
        return "mocreatures:ostrichdying";
    }

    @Override
    protected Item getDropItem()
    {
        boolean flag = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);
        if (flag && (getType() == 8)) // unicorn
        { return MoCreatures.unicornHorn; }
        if (getType() == 5 && flag) 
        { return MoCreatures.heartFire; }
        if (getType() == 6 && flag) // bat horse
        { return MoCreatures.heartDarkness; }
        if (getType() == 7 )
        {
            if (flag) { return MoCreatures.heartundead; }
            return Items.rotten_flesh;
        }
         return MoCreatures.ostrichRaw;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setRideable(nbtTagCompound.getBoolean("Saddle"));
        setEggWatching(nbtTagCompound.getBoolean("EggWatch"));
        setHiding(nbtTagCompound.getBoolean("Hiding"));
        setHelmet(nbtTagCompound.getByte("Helmet"));
        setFlagColor(nbtTagCompound.getByte("FlagColor"));
        setIsChested(nbtTagCompound.getBoolean("Bagged"));
        if (getIsChested())
        {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("Items", 10);
            localChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.OstrichChest"), 18);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                NBTTagCompound nbtTagCompound1 = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
                int j = nbtTagCompound1.getByte("Slot") & 0xff;
                if ((j >= 0) && j < localChest.getSizeInventory())
                {
                    localChest.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbtTagCompound1));
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Saddle", getIsRideable());
        nbtTagCompound.setBoolean("EggWatch", getEggWatching());
        nbtTagCompound.setBoolean("Hiding", getHiding());
        nbtTagCompound.setByte("Helmet", getHelmet());
        nbtTagCompound.setByte("FlagColor", getFlagColor());
        nbtTagCompound.setBoolean("Bagged", getIsChested());
        
        if (getIsChested() && localChest != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < localChest.getSizeInventory(); i++)
            {
                localItemstack = localChest.getStackInSlot(i);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) i);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbttaglist.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items", nbttaglist);
        }
    }

    @Override
    public boolean getCanSpawnHere()
    {
        //spawns in deserts and plains
        return getCanSpawnHereCreature() && getCanSpawnHereLiving();
    }

    @Override
    public int nameYOffset()
    {
        if (getType() > 1)
        {
            return -105;
        }
        else
        {
            return  (-5 - getMoCAge());
        }
    }

    @Override
    public double roperYOffset()
    {
        if (getType() > 1)
        {
            return 0D;
        }
        else
        {
            return (double) ((120 - getMoCAge()) * 0.01D);
        }
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
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return isItemEdible(itemstack.getItem());
    }

    @Override
    public void dropMyStuff() 
    {
        if (MoCreatures.isServer())
        {
            dropArmor();
            MoCTools.dropSaddle(this, worldObj);
            
            if (getIsChested())
            {
               MoCTools.dropInventory(this, localChest);
               MoCTools.dropCustomItem(this, worldObj, new ItemStack(Blocks.chest, 1));
               setIsChested(false);
            }
        }
        
    }

    /**
     * Drops the helmet
     */
    @Override
    public void dropArmor() 
    {
        if (MoCreatures.isServer())
        {
            EntityItem entityItem = null;// = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Blocks.wool, 1, color));
            
            switch (getHelmet())
            {
            case 0:
            case 8:
                return;
                //break;
            case 1:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.leather_helmet, 1));
                break;
            case 2:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.iron_helmet, 1));
                break;
            case 3:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.golden_helmet, 1));
                break;
            case 4:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Items.diamond_helmet, 1));
                break;
            case 5:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.helmetHide, 1));
                break;
            case 6:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.helmetFur, 1));
                break;
            case 7:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.helmetCroc, 1));
                break;
            case 9:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetDirt, 1));
                break;
            case 10:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetFrost, 1));
                break;
            case 11:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetCave, 1));
                break;
            case 12:
                entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.scorpHelmetNether, 1));
                break;
            }

            if (entityItem != null)
            {
                entityItem.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(entityItem);
            }
            setHelmet((byte)0);
        }
    }

    @Override
    public boolean isFlyer()
    {
        return (getType() == 5 || getType() == 6);
    }

    @Override
    protected void fall(float f)
    {
        if (isFlyer()) { return; }
    }

    @Override
    protected double myFallSpeed()
    {
        return 0.99D;
    }

    @Override
    protected double flyerThrust()
    {
        return 0.6D;
    }

    @Override
    protected float flyerFriction()
    {
        return 0.96F;
    }

    @Override
    protected boolean selfPropelledFlyer()
    {
        return getType() == 6;
    }

    @Override
    public void makeEntityJump()
    {
        if (jumpCounter > 5)
        {
            //return;
            jumpCounter = 1;
        }
        if (jumpCounter == 0)
        {
            MoCTools.playCustomSound(this, "wingflap", worldObj);
            jumpPending = true;
            jumpCounter = 1;
        }
        
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        if (getType() == 7) 
        {
            return EnumCreatureAttribute.UNDEAD;
        }
        return super.getCreatureAttribute();
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }
}