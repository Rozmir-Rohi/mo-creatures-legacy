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
    private boolean transforming;
    private boolean hunched;
    private int transform_counter;
    private int textureCounter;

    public MoCEntityWerewolf(World world)
    {
        super(world);
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "werehuman.png";
        setSize(0.9F, 1.6F);
        transforming = false;
        transform_counter = 0;
        setHumanForm(true);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
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
            int k = rand.nextInt(100);
            if (k <= 28)
            {
                setType(1);
            }
            else if (k <= 56)
            {
                setType(2);
            }
            else if (k <= 85)
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
    protected void attackEntity(Entity entity, float f)
    {
        if (getIsHumanForm())
        {
            entityToAttack = null;
            return;
        }
        if ((f > 2.0F) && (f < 6F) && (rand.nextInt(15) == 0))
        {
            if (onGround)
            {
                setHunched(true);
                double x_distance = entity.posX - posX;
                double z_distance = entity.posZ - posZ;
                float xz_distance_squared = MathHelper.sqrt_double((x_distance * x_distance) + (z_distance * z_distance));
                motionX = ((x_distance / xz_distance_squared) * 0.5D * 0.80000001192092896D) + (motionX * 0.20000000298023221D);
                motionZ = ((z_distance / xz_distance_squared) * 0.5D * 0.80000001192092896D) + (motionZ * 0.20000000298023221D);
                motionY = 0.40000000596046448D;
            }
        }
        else
        {
            if (attackTime <= 0 && (f < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
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
    public boolean attackEntityFrom(DamageSource damagesource, float damage_dealt_to_werewolf)
    {
        Entity entity = damagesource.getEntity();
        if (!getIsHumanForm() && (entity != null) && (entity instanceof EntityPlayer))
        {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            ItemStack itemstack = entityplayer.getCurrentEquippedItem();
            if (itemstack != null)
            {
                damage_dealt_to_werewolf = 1;
                
                Item item_held_by_player = itemstack.getItem();
                
                if (damagesource.isProjectile())
                {
                	if (!(item_held_by_player instanceof ItemBow))
                	{
                		damage_dealt_to_werewolf = 0;
                	}
                }
                
                if (item_held_by_player == Items.golden_hoe
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("BiomesOPlenty:scytheGold")))
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:dagger.gold")))
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:waraxe.gold"))) // 8 is the actual damage dealt to werewolf using golden war axe in-game because of the item's armor penetration ability 
                		)
                {
                    damage_dealt_to_werewolf = 6;
                }
                
                if (item_held_by_player == Items.golden_pickaxe) {damage_dealt_to_werewolf = 7;}
                
                if (item_held_by_player == Items.golden_axe
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:mace.gold")))
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:spear.gold")))
                		)
                {
                    damage_dealt_to_werewolf = 8;
                }
                
                if (item_held_by_player == Items.golden_sword
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("witchery:silversword")))
                		)
                {
                	damage_dealt_to_werewolf = 9;
                }
                
                if (item_held_by_player == MoCreatures.silversword) {damage_dealt_to_werewolf = 10;}
                
            }
        }
        return super.attackEntityFrom(damagesource, damage_dealt_to_werewolf);
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if (getIsHumanForm()) { return null; }
        
        EntityPlayer entityplayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
        
        EntityLivingBase entityliving = getClosestTarget(this, 16D);
        
        if ((entityplayer != null) && canEntityBeSeen(entityplayer))
        {
            return entityplayer;
        }
        
        else if ((entityliving != null) && canEntityBeSeen(entityliving))
        {
        	return entityliving;
        }
        
        else
        {
            return null;
        }
    }
    
    public EntityLivingBase getClosestTarget(Entity entity, double d)
    {
        double d1 = -1D;
        
        EntityLivingBase entityliving = null;
        
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(d, d, d));
        
        
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            
           
            if (entity1 instanceof EntityVillager)
            {
	            double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)) && ((EntityLivingBase) entity1).canEntityBeSeen(entity))
	            {
	                d1 = d2;
	                entityliving = (EntityLivingBase) entity1;
	            }
            }
        }

        return entityliving;
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
        int i = rand.nextInt(12);
        if (getIsHumanForm())
        {
            switch (i)
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
        switch (i)
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
    public void onDeath(DamageSource source_of_damage)
    {
        Entity entity = source_of_damage.getEntity();
        if ((scoreValue > 0) && (entity != null))
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        if (entity != null)
        {
            entity.onKillEntity(this);
        }
        

        if ((source_of_damage.getEntity() != null) && (source_of_damage.getEntity() instanceof EntityPlayer) && !(getIsHumanForm()))
        {
        	EntityPlayer player = (EntityPlayer)source_of_damage.getEntity();
            if (player != null) {player.addStat(MoCAchievements.kill_werewolf, 1);}
        }

        if (!worldObj.isRemote)
        {
            for (int i = 0; i < 2; i++)
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
                transforming = true;
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
            if (transforming && (rand.nextInt(3) == 0))
            {
                transform_counter++;
                if ((transform_counter % 2) == 0)
                {
                    posX += 0.29999999999999999D;
                    posY += transform_counter / 30;
                    attackEntityFrom(DamageSource.causeMobDamage(this), 0);
                }
                if ((transform_counter % 2) != 0)
                {
                    posX -= 0.29999999999999999D;
                }
                if (transform_counter == 10)
                {
                    worldObj.playSoundAtEntity(this, "mocreatures:weretransform", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
                }
                if (transform_counter > 30)
                {
                    Transform();
                    transform_counter = 0;
                    transforming = false;
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
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY) + 1;
        int k = MathHelper.floor_double(posZ);
        float f = 0.1F;
        for (int l = 0; l < 30; l++)
        {
            double d = i + worldObj.rand.nextFloat();
            double d1 = j + worldObj.rand.nextFloat();
            double d2 = k + worldObj.rand.nextFloat();
            double d3 = d - i;
            double d4 = d1 - j;
            double d5 = d2 - k;
            double d6 = MathHelper.sqrt_double((d3 * d3) + (d4 * d4) + (d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5D / ((d6 / f) + 0.10000000000000001D);
            d7 *= (worldObj.rand.nextFloat() * worldObj.rand.nextFloat()) + 0.3F;
            d3 *= d7;
            d4 *= d7;
            d5 *= d7;
            worldObj.spawnParticle("explode", (d + (i * 1.0D)) / 2D, (d1 + (j * 1.0D)) / 2D, (d2 + (k * 1.0D)) / 2D, d3, d4, d5);
        }

        if (getIsHumanForm())
        {
            setHumanForm(false);
            
            if (getMaxHealth() != 40F)
            {
            	this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
            }
            
            this.setHealth(getMaxHealth());
            transforming = false;
        }
        else
        {
            setHumanForm(true);
            
            float health_for_human_form = Math.round((getHealth() / getMaxHealth()) * 16.0D);
            
            if (getMaxHealth() != 16F)
            {
            	this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(16.0D);
            }
            
            
            this.setHealth(health_for_human_form);
            transforming = false;
        }
    }

    @Override
    protected void updateEntityActionState()
    {
        if (!transforming)
        {
            super.updateEntityActionState();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setHumanForm(nbttagcompound.getBoolean("HumanForm"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("HumanForm", getIsHumanForm());
    }

    @Override
    public float getMoveSpeed()
    {
        if (getIsHunched()) { return 0.9F; }
        return 0.7F;
    }
}