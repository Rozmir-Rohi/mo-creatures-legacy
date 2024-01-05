package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAmbient;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import drzhark.mocreatures.entity.aquatic.MoCEntityRay;
import drzhark.mocreatures.entity.aquatic.MoCEntityShark;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityCrocodile extends MoCEntityTameableAnimal {
    // TODO
    // fix floating so it moves faster if it's deep and caughts prey underneath

    // for later?
    // birds to clean their mouths?
    // implement taming? (pick up small crocs to insta tame them)

    public float biteProgress;
    public float spin;
    public int spinInt;
    private float myMoveSpeed;
    private boolean waterbound;
    private int hunting;
    private float spin_attack_strength;
    private int catch_prey_in_mouth_attack_speed;

    public MoCEntityCrocodile(World world)
    {
        super(world);
        texture = "crocodile.png";
        setSize(2F, 0.6F);
        myMoveSpeed = 0.5F;
        //health = 25;
        setEdad(50 + rand.nextInt(50));
        setTamed(false);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(25.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isBiting - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // isResting - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // caughtPrey - 0 false 1 true
    }
    
    @Override
    protected boolean canDespawn()
    {
        return !getIsTamed() && this.ticksExisted > 2400;
    }

    public boolean getIsBiting()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public boolean getIsResting()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public boolean getHasCaughtPrey()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public void setBiting(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setIsResting(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    public void setHasCaughtPrey(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }

    @Override
    protected void jump()
    {

        if (isInsideOfMaterial(Material.water))
        {
            if (getHasCaughtPrey() || (entityToAttack == null && rand.nextInt(20) != 0)) 
            {
                return;
            }

            motionY = 0.3D;
            if (isSprinting())
            {
                float f = rotationYaw * 0.01745329F;
                motionX -= MathHelper.sin(f) * 0.2F;
                motionZ += MathHelper.cos(f) * 0.2F;
            }
            isAirBorne = true;

        }
        else if (entityToAttack != null || getHasCaughtPrey())
        {
            super.jump();
        }
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getIsResting();
    }

    @Override
    protected void updateEntityActionState()
    {
        if (!getIsResting())
        {
            super.updateEntityActionState();
        }
    }

    @Override
    public boolean swimmerEntity()
    {
        return true;
    }

    @Override
    public void onLivingUpdate()
    {	
    	if (getIsResting())
        {
            rotationPitch = -5F;
            if (!isInsideOfMaterial(Material.water) && biteProgress < 0.3F && rand.nextInt(5) == 0)
            {
                biteProgress += 0.005F;
            }
            entityToAttack = findPlayerToAttack();
            if (entityToAttack != null)
            {
                setIsResting(false);
                getMyOwnPath(entityToAttack, 16F);
            }
            if (MoCreatures.isServer() && entityToAttack != null || getHasCaughtPrey() || rand.nextInt(500) == 0)// isInsideOfMaterial(Material.water)
            {
                setIsResting(false);
                biteProgress = 0;
                hunting = 1;
            }

        }
        else
        {
            if (MoCreatures.isServer() && (rand.nextInt(500) == 0) && entityToAttack == null && !getHasCaughtPrey() && !(this.isInWater()))
            {
                setIsResting(true);
                setPathToEntity(null);
            }

        }

        if (isInsideOfMaterial(Material.water))
        {
            myMoveSpeed = 0.8F;
        }
        else
        {
            myMoveSpeed = 0.4F;

        }
        if (hunting > 0)
        {
            hunting++;
            if (hunting > 120)
            {
                hunting = 0;
                myMoveSpeed = 0.5F;
            }
            else
            {
                myMoveSpeed = 1.0F;
            }

            if (entityToAttack == null)
            {
                hunting = 0;
                myMoveSpeed = 0.5F;
            }

        }

        if (rand.nextInt(80) == 0 && !getHasCaughtPrey() && !getIsResting())
        {
            crocBite();
        }

        if (MoCreatures.isServer() && rand.nextInt(500) == 0 && !waterbound && !getIsResting() && !isInsideOfMaterial(Material.water))
        {
            MoCTools.MoveToWater(this);
        }

        if (MoCreatures.isServer() && getEdad() < 150 && (rand.nextInt(200) == 0))
        {
            setEdad(getEdad() + 1);
            if (getEdad() >= 90)
            {
                setAdult(true);
            }
        }

        if (waterbound)
        {
            if (!isInsideOfMaterial(Material.water))
            {
                MoCTools.MoveToWater(this);
            }
            else
            {
                waterbound = false;
            }
        }

        if (getHasCaughtPrey())
        {
            if (riddenByEntity != null)
            {
                entityToAttack = null;

                biteProgress = 0.4F;
                setIsResting(false);

                if (!isInsideOfMaterial(Material.water))
                {
                    waterbound = true;
                    if (riddenByEntity instanceof EntityLiving && ((EntityLivingBase) riddenByEntity).getHealth() > 0)
                    {
                        ((EntityLivingBase) riddenByEntity).deathTime = 0;
                    }
                    
                    if (getEdad() < 90) //if is child (higher number for attack speed means the slower it is)
                    {catch_prey_in_mouth_attack_speed = 15;}
                    else
                    {catch_prey_in_mouth_attack_speed = 5;}
                    

                    if (MoCreatures.isServer() && rand.nextInt(catch_prey_in_mouth_attack_speed) == 0)  //cause damage to creature in mouth
                    {
                        riddenByEntity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
                        if (!(riddenByEntity instanceof EntityPlayer))
                        {
                            MoCTools.destroyDrops(this, 3D);
                        }
                    }
                }
            }
            else
            {
                setHasCaughtPrey(false);
                MoCTools.checkForTwistedEntities(worldObj);
                biteProgress = 0F;
                waterbound = false;
            }

            if (isSpinning())
            {
            	if (getEdad() < 90) {spin_attack_strength = 2;} //child spin attack strength
            	else {spin_attack_strength = 7;} //adult spin attack strength
            	
                spinInt += 3;
                if ((spinInt % 20) == 0)
                {
                    worldObj.playSoundAtEntity(this, "mocreatures:crocroll", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
                }
                if (spinInt > 80)
                {
                    spinInt = 0;
                    riddenByEntity.attackEntityFrom(DamageSource.causeMobDamage(this), spin_attack_strength);
                    if (!(riddenByEntity instanceof EntityPlayer))
                    {
                        MoCTools.destroyDrops(this, 3D);
                    }
                }
            }
        }

        super.onLivingUpdate();
    }

    @Override
    public boolean isNotScared()
    {
        return true;
    }

    public void crocBite()
    {
        if (!getIsBiting())
        {
            setBiting(true);
            biteProgress = 0.0F;
        }
    }

    @Override
    public void onUpdate()
    {
        if (getIsBiting() && !getHasCaughtPrey())// && biteProgress <0.3)
        {
            biteProgress += 0.1F;
            if (biteProgress == 0.4F)
            {
                worldObj.playSoundAtEntity(this, "mocreatures:crocjawsnap", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            }
            if (biteProgress > 0.6F)
            {

                setBiting(false);
                biteProgress = 0.0F;
            }
        }

        super.onUpdate();
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
    	if (this.getIsResting()) {this.setIsResting(false);}
    	
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            if (player.capabilities.isCreativeMode)
                return;
        }
        if (getHasCaughtPrey()) { return; }

        if (attackTime <= 0 && (f < 2.5F) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
        	float attack_strength;
        	if (getEdad() < 90) {attack_strength = 2;} // if child attack strength is 2
        	else {attack_strength = 10;} // else it is an adult and attack strength is 10
        	
            attackTime = 20;
            if (entity.ridingEntity == null && rand.nextInt(3) == 0)
            {
                entity.mountEntity(this);
                setHasCaughtPrey(true);
            }
            else
            {
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), attack_strength);
                if (!(entity instanceof EntityPlayer))
                {
                    MoCTools.destroyDrops(this, 3D);
                }
                crocBite();
                setHasCaughtPrey(false);
            }

        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
    	if (this.getIsResting()) {this.setIsResting(false);} // TODO: Here marker
    	
        if (riddenByEntity != null)
        {

            Entity entity = damagesource.getEntity();
            
            if (entity != null && riddenByEntity == entity)
            {
                if (rand.nextInt(2) != 0)
                {
                    return false;
                }
                else
                {
                    unMount();
                }
            }

        }
        if (super.attackEntityFrom(damagesource, i))
        {
            Entity entity = damagesource.getEntity();
            
            if ((entity != null) && (entity != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
            	entityToAttack = entity;
            	
                return true;
            }

            if (riddenByEntity != null && riddenByEntity == entity)
            {
                if ((entity != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
                {
                    entityToAttack = entity;
                }
                return true;
            }
        }
        return false;
    }


    @Override
    protected Entity findPlayerToAttack()
    {
        if (getHasCaughtPrey()) { return null; }

        if (rand.nextInt(80) == 0 && worldObj.difficultySetting.getDifficultyId() > 0)
        {
            double attackDistance = 6D;
            
            EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, attackDistance); 
            if((entityplayer != null) && getIsAdult()) 
            {
                 return entityplayer; 
            }
            
            EntityLivingBase entityliving = getClosestEntityLiving(this, attackDistance);
            if (entityliving instanceof MoCEntityAquatic || entityliving instanceof MoCEntityTameableAquatic) // don't go and hunt fish if they are in the ocean
            	{
                	int x = MathHelper.floor_double(entityliving.posX);
                    int y = MathHelper.floor_double(entityliving.posY);
                    int z = MathHelper.floor_double(entityliving.posZ);

                    BiomeGenBase biome_that_prey_is_in = MoCTools.Biomekind(worldObj, x, y, z);

                    if (BiomeDictionary.isBiomeOfType(biome_that_prey_is_in, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biome_that_prey_is_in, Type.BEACH))
                    {
                    	 return null;
                    }
            	}
                
            else {return entityliving;}
        }
        return null;
    }

    
    @Override
    public boolean entitiesToIgnore(Entity entity) //don't hunt the following mobs below
    {
        return ((!(entity instanceof EntityLiving)) 
                || (entity instanceof IMob || entity instanceof MoCEntityMob) //don't hunt the creature if it is a mob 
                || (entity instanceof EntityPlayer) 
                || (entity instanceof MoCEntityKittyBed || entity instanceof MoCEntityLitterBox) 
                || (this.getIsTamed() && (entity instanceof IMoCEntity && ((IMoCEntity) entity).getIsTamed())) 
                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)) 
                || (entity instanceof MoCEntityHorse && !(MoCreatures.proxy.attackHorses)) 
                || (entity instanceof MoCEntityEgg)
            	|| (entity instanceof MoCEntityCrocodile) 
            	|| ((entity.width > 0.9D) && (entity.height > 0.9D)) //don't try to hunt creatures larger than itself   
            	|| (entity instanceof MoCEntityBigCat)
            	|| (entity instanceof MoCEntityAmbient) //don't hunt insects
            	|| (entity instanceof MoCEntityShark)
            	|| (entity instanceof MoCEntityJellyFish || entity instanceof MoCEntityRay || entity instanceof EntitySquid)
        		);
    }

    @Override
    public void updateRiderPosition()
    {
        if (riddenByEntity == null) { return; }
        int direction = 1;

        double dist = getEdad() * 0.01F + riddenByEntity.width - 0.4D;
        double newPosX = posX - (dist * Math.cos((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F));
        double newPosZ = posZ - (dist * Math.sin((MoCTools.realAngle(rotationYaw - 90F)) / 57.29578F));
        riddenByEntity.setPosition(newPosX, posY + getMountedYOffset() + riddenByEntity.getYOffset(), newPosZ);

        if (spinInt > 40)
        {
            direction = -1;
        }
        else
        {
            direction = 1;
        }

        ((EntityLivingBase) riddenByEntity).renderYawOffset = this.rotationYaw * direction;
        ((EntityLivingBase) riddenByEntity).prevRenderYawOffset = this.rotationYaw * direction;
    }

    @Override
    public double getMountedYOffset()
    {
        return (double) height * 0.35D;
    }

    @Override
    public void floating()
    {
        if ((entityToAttack != null && ((entityToAttack.posY < (posY - 0.5D)) && getDistanceToEntity(entityToAttack) < 10F))) // || caughtPrey)
        {
            if (motionY < -0.1)
            {
                motionY = -0.1;
            }
        }
        else
        {
            super.floating();
        }
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:crocdying";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:crochurt";
    }

    @Override
    protected String getLivingSound()
    {
        if (getIsResting()) { return "mocreatures:crocresting"; }
        return "mocreatures:crocgrunt";
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.hideCroc;
    }

    public boolean isSpinning()
    {
        return getHasCaughtPrey() && (riddenByEntity != null) && (isInsideOfMaterial(Material.water));
    }

    @Override
    public void onDeath(DamageSource damagesource)
    {

        unMount();
        MoCTools.checkForTwistedEntities(worldObj);
        super.onDeath(damagesource);
    }

    public void unMount()
    {

        if (riddenByEntity != null)
        {
            if (riddenByEntity instanceof EntityLiving && !(riddenByEntity instanceof EntityPlayer) && ((EntityLivingBase) riddenByEntity).getHealth() > 0)
            {
                ((EntityLivingBase) riddenByEntity).deathTime = 0;
            }

            riddenByEntity.mountEntity(null);
            setHasCaughtPrey(false);
        }
    }

    @Override
    public int getTalkInterval()
    {
        return 120;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
    }
}