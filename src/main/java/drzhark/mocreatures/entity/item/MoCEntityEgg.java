package drzhark.mocreatures.entity.item;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.animal.MoCEntityKomodoDragon;
import drzhark.mocreatures.entity.animal.MoCEntityOstrich;
import drzhark.mocreatures.entity.animal.MoCEntityPetScorpion;
import drzhark.mocreatures.entity.animal.MoCEntitySnake;
import drzhark.mocreatures.entity.animal.MoCEntityWyvern;
import drzhark.mocreatures.entity.aquatic.MoCEntityFishy;
import drzhark.mocreatures.entity.aquatic.MoCEntityMediumFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityPiranha;
import drzhark.mocreatures.entity.aquatic.MoCEntityShark;
import drzhark.mocreatures.entity.aquatic.MoCEntitySmallFish;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityEgg extends EntityLiving {
    private int eggIncubationCounter;
    private int eggLostCounter;
    public int eggType;

    public MoCEntityEgg(World world, int type)
    {
        this(world);
        eggType = type;
    }

    public MoCEntityEgg(World world)
    {
        super(world);
        setSize(0.25F, 0.25F);
        eggIncubationCounter = 0;
        eggLostCounter = 0;
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "egg.png";
    }

    public MoCEntityEgg(World world, double d, double d1, double d2)
    {
        super(world);

        setSize(0.25F, 0.25F);
        eggIncubationCounter = 0;
        eggLostCounter = 0;
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "egg.png";
    }

    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture("egg.png");
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D); // setMaxHealth
    }

    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return null;
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    public boolean handleWaterMovement()
    {
        if (worldObj.handleMaterialAcceleration(boundingBox, Material.water, this))
        {
            inWater = true;
            return true;
        }
        else
        {
            inWater = false;
            return false;
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityPlayer)
    {
        int eggIndex = eggType;
        if (eggIndex == 30)
        {
            eggIndex = 31;
        }
        if ((eggLostCounter > 10) && entityPlayer.inventory.addItemStackToInventory(new ItemStack(MoCreatures.mocegg, 1, eggIndex)))
        {
            playSound("random.pop", 0.2F, (((rand.nextFloat() - rand.nextFloat()) * 0.7F) + 1.0F) * 2.0F);
            if (!worldObj.isRemote)
            {
                entityPlayer.onItemPickup(this, 1);
                
                if (eggIndex == 31) {entityPlayer.addStat(MoCAchievements.ostrich_egg, 1);}

            }
            setDead();
        }
    }

    @Override
    public void onLivingUpdate()
    {
        moveStrafing = 0.0F;
        moveForward = 0.0F;
        randomYawVelocity = 0.0F;
        moveEntityWithHeading(moveStrafing, moveForward);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (MoCreatures.isServer())
        {
            if (rand.nextInt(20) == 0)
            {
                eggLostCounter++;
            }

            if (eggLostCounter > 500)
            {
                EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 24D);
                if (entityPlayer1 == null)
                {
                    setDead();
                }
            }

            if (isInWater() && (getEggType() < 12 || getEggType() > 69)&& (rand.nextInt(20) == 0))
            {
                eggIncubationCounter++;
                if (eggIncubationCounter % 5 == 0)
                {
                    motionY += 0.2D;
                }

                if (eggIncubationCounter == 5)
                {
                    NotifyEggHatching();                    
                }
                
                if (eggIncubationCounter >= 30)
                {
                    if (getEggType() < 10) // fishy
                    {
                        MoCEntityFishy entityspawn = new MoCEntityFishy(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(getEggType());
                        entityspawn.setMoCAge(30);
                        worldObj.spawnEntityInWorld(entityspawn);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }

                    else if (getEggType() == 11) // shark
                    {
                        MoCEntityShark entityspawn = new MoCEntityShark(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setMoCAge(30);
                        worldObj.spawnEntityInWorld(entityspawn);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }

                    else if (getEggType()  == 90) // piranha
                    {
                        MoCEntityPiranha entityspawn = new MoCEntityPiranha(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        worldObj.spawnEntityInWorld(entityspawn);
                        entityspawn.setMoCAge(30);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }
                    
                    else if (getEggType() > 79 && getEggType() < (80 + MoCEntitySmallFish.fishNames.length)) // smallfish
                    {
                        MoCEntitySmallFish entityspawn = new MoCEntitySmallFish(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(getEggType() - 79);
                        worldObj.spawnEntityInWorld(entityspawn);
                        entityspawn.setMoCAge(30);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }
                    
                    else if (getEggType() > 69 && getEggType() < (80 + MoCEntityMediumFish.fishNames.length)) // mediumfish
                    {
                        MoCEntityMediumFish entityspawn = new MoCEntityMediumFish(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(getEggType() - 69);
                        worldObj.spawnEntityInWorld(entityspawn);
                        entityspawn.setMoCAge(30);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }
                    playSound("mob.chicken.plop", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
                    setDead();
                }
            }

            else if (getEggType() > 20 && (rand.nextInt(20) == 0)) // non aquatic creatures
            {
                eggIncubationCounter++;
                //if (getEggType() == 30) tCounter = 0; //with this, wild ostriches won't spawn eggs.

                if (eggIncubationCounter % 5 == 0)
                {
                    motionY += 0.2D;
                }

                if (eggIncubationCounter == 5)
                {
                    NotifyEggHatching();                    
                }

                if (eggIncubationCounter >= 30)
                {
                    if (getEggType() > 20 && getEggType() < 29) // snakes
                    {
                        MoCEntitySnake entityspawn = new MoCEntitySnake(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(getEggType() - 20);
                        entityspawn.setMoCAge(50);
                        worldObj.spawnEntityInWorld(entityspawn);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }

                    if (getEggType() == 30 || getEggType() == 31 || getEggType() == 32) // Ostriches. 30 = wild egg, 31 = stolen egg
                    {
                        MoCEntityOstrich entityspawn = new MoCEntityOstrich(worldObj);
                        int typeInt = 1;
                        if (worldObj.provider.isHellWorld || getEggType() == 32)
                        {
                            typeInt = 5;
                        }
                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(typeInt);
                        entityspawn.setMoCAge(35);
                        worldObj.spawnEntityInWorld(entityspawn);
                        entityspawn.setHealth(entityspawn.getMaxHealth());

                        if (getEggType() == 31)//stolen egg that hatches a tamed ostrich
                        {
                            EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                            if (entityPlayer != null)
                            {
                                MoCTools.tameWithName(entityPlayer, entityspawn);
                            }
                        }
                    }

                    if (getEggType() == 33) // Komodo
                    {
                        MoCEntityKomodoDragon entityspawn = new MoCEntityKomodoDragon(worldObj);

                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setMoCAge(30);
                        worldObj.spawnEntityInWorld(entityspawn);
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }
                    
                    if (getEggType() > 40 && getEggType() < 46) //scorpions for now it uses 41 - 45
                    {
                        MoCEntityPetScorpion entityspawn = new MoCEntityPetScorpion(worldObj);
                        int typeInt = getEggType() - 40;
                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(typeInt);
                        entityspawn.setAdult(false);
                        worldObj.spawnEntityInWorld(entityspawn);
                        entityspawn.setHealth(entityspawn.getMaxHealth());
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }
                    
                    if (getEggType() > 49 && getEggType() < 62) //wyverns for now it uses 50 - 61
                    {
                        MoCEntityWyvern entityspawn = new MoCEntityWyvern(worldObj);
                        int typeInt = getEggType() - 49;
                        entityspawn.setPosition(posX, posY, posZ);
                        entityspawn.setType(typeInt);
                        entityspawn.setAdult(false);
                        entityspawn.setMoCAge(30);
                        worldObj.spawnEntityInWorld(entityspawn);
                        entityspawn.setHealth(entityspawn.getMaxHealth());
                        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                        if (entityPlayer != null)
                        {
                            MoCTools.tameWithName(entityPlayer, entityspawn);
                        }
                    }
                    playSound("mob.chicken.plop", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
                    setDead();
                }
            }
        }
    }

    private void NotifyEggHatching()
    {
        EntityPlayer closestPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
        if (closestPlayer != null)
        {
            closestPlayer.addChatMessage(new ChatComponentTranslation("notify.MoCreatures.egg_hatching", new Object[] {(int)posX, (int)posY, (int)posZ}));
        }
    }
    public int getSize()
    {
        if (getEggType() == 30 || getEggType() == 31) { return 170; }
        return 100;
    }

    public int getEggType()
    {
        return eggType;
    }

    public void setEggType(int eggType)
    {
        this.eggType = eggType;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setEggType(nbtTagCompound.getInteger("EggType"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("EggType", getEggType());
    }
    
    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        return false;
    }
}