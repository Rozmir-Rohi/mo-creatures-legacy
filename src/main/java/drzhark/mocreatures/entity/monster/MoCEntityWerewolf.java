package drzhark.mocreatures.entity.monster;

import java.util.List;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityWerewolf extends MoCEntityMob {
    private boolean isTransforming;
    private boolean isHunched;
    private int transformCounter;
    private int textureCounter;

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
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
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
            }
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        if (this.getIsHumanForm()) { return MoCreatures.proxy.getTexture("wereblank.png"); }

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

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (getIsHumanForm())
        {
            entityToAttack = null;
            return;
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
                entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
                if (this.getType() == 4)
                {
                    ((EntityLivingBase) entity).setFire(10);
                }
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageDealtToWerewolf)
    {
        Entity entityThatAttackedThisCreature = damageSource.getEntity();
        if (!getIsHumanForm() && (entityThatAttackedThisCreature != null) && (entityThatAttackedThisCreature instanceof EntityPlayer))
        {
            EntityPlayer entityPlayer = (EntityPlayer) entityThatAttackedThisCreature;
            ItemStack itemstack = entityPlayer.getCurrentEquippedItem();
            if (itemstack != null)
            {
                damageDealtToWerewolf = 1;
                
                Item itemHeldByPlayer = itemstack.getItem();
                
                if (damageSource.isProjectile())
                {
                	if (!(itemHeldByPlayer instanceof ItemBow))
                	{
                		damageDealtToWerewolf = 0;
                	}
                }
                
                if (itemHeldByPlayer == Items.golden_shovel)
                {
                    damageDealtToWerewolf = 3;
                }	
                	
                if (
                		itemHeldByPlayer == Items.golden_hoe
                		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("BiomesOPlenty:scytheGold")))
                		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:dagger.gold")))
                		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:waraxe.gold"))) // 8 is the actual damage dealt to werewolf using golden war axe in-game because of the item's armor penetration ability 
                	)
                {
                    damageDealtToWerewolf = 6;
                }
                
                if (
                		itemHeldByPlayer == Items.golden_pickaxe
                	) 
                {
                	damageDealtToWerewolf = 7;
                }
                
                if (
                		itemHeldByPlayer == Items.golden_axe
                		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:mace.gold")))
                		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("battlegear2:spear.gold")))
                	)
                {
                    damageDealtToWerewolf = 8;
                }
                
                if (
                		itemHeldByPlayer == Items.golden_sword
                		|| (((itemHeldByPlayer.itemRegistry).getNameForObject(itemHeldByPlayer).equals("witchery:silversword")))
                	)
                {
                	damageDealtToWerewolf = 9;
                }
                
                if (itemHeldByPlayer == MoCreatures.silversword) {damageDealtToWerewolf = 10;}
                
            }
        }
        return super.attackEntityFrom(damageSource, damageDealtToWerewolf);
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if (getIsHumanForm()) { return null; }
        
        EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
        
        EntityLivingBase entityLiving = getClosestTarget(this, 16D);
        
        if ((entityPlayer != null) && canEntityBeSeen(entityPlayer))
        {
            return entityPlayer;
        }
        
        else if ((entityLiving != null) && canEntityBeSeen(entityLiving))
        {
        	return entityLiving;
        }
        
        else
        {
            return null;
        }
    }
    
    public EntityLivingBase getClosestTarget(Entity entity, double distance)
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
	            
	           
	            if (entityNearby instanceof EntityVillager)
	            {
		            double overallDistanceSquared = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
		            
		            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)) && ((EntityLivingBase) entityNearby).canEntityBeSeen(entity))
		            {
		                currentMinimumDistance = overallDistanceSquared;
		                entityLiving = (EntityLivingBase) entityNearby;
		            }
	            }
	        }
        }

        return entityLiving;
    }

    @Override
    protected String getDeathSound()
    {
        if (getIsHumanForm())
        {
            return "mocreatures:werehumandying";
        }
        else
        {
            return "mocreatures:werewolfdying";
        }
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

    @Override
    protected String getHurtSound()
    {
        if (getIsHumanForm())
        {
            return "mocreatures:werehumanhurt";
        }
        else
        {
            return "mocreatures:werewolfhurt";
        }
    }

    public boolean getIsUndead()
    {
        return true;
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
                if (transformCounter == 10)
                {
                    worldObj.playSoundAtEntity(this, "mocreatures:weretransform", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
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
            	this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
            }
            
            this.setHealth(getMaxHealth());
            isTransforming = false;
        }
        else
        {
            setHumanForm(true);
            
            float healthForHumanForm = Math.round((getHealth() / getMaxHealth()) * 16.0D);
            
            if (getMaxHealth() != 16F)
            {
            	this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
            }
            
            
            this.setHealth(healthForHumanForm);
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