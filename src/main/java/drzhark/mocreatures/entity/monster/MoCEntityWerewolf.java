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
                float overall_horizontal_distance_squared = MathHelper.sqrt_double((x_distance * x_distance) + (z_distance * z_distance));
                motionX = ((x_distance / overall_horizontal_distance_squared) * 0.5D * 0.80000001192092896D) + (motionX * 0.20000000298023221D);
                motionZ = ((z_distance / overall_horizontal_distance_squared) * 0.5D * 0.80000001192092896D) + (motionZ * 0.20000000298023221D);
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
                
                if (
                		item_held_by_player == Items.golden_hoe
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("BiomesOPlenty:scytheGold")))
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:dagger.gold")))
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:waraxe.gold"))) // 8 is the actual damage dealt to werewolf using golden war axe in-game because of the item's armor penetration ability 
                	)
                {
                    damage_dealt_to_werewolf = 6;
                }
                
                if (
                		item_held_by_player == Items.golden_shovel
                		|| item_held_by_player == Items.golden_pickaxe
                	) 
                {
                	damage_dealt_to_werewolf = 7;
                }
                
                if (
                		item_held_by_player == Items.golden_axe
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:mace.gold")))
                		|| (((item_held_by_player.itemRegistry).getNameForObject(item_held_by_player).equals("battlegear2:spear.gold")))
                	)
                {
                    damage_dealt_to_werewolf = 8;
                }
                
                if (
                		item_held_by_player == Items.golden_sword
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
    
    public EntityLivingBase getClosestTarget(Entity entity, double distance)
    {
        double current_minimum_distance = -1D;
        
        EntityLivingBase entityliving = null;
        
        List entities_nearby_list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
        int iteration_length = entities_nearby_list.size();
        
        if (iteration_length > 0)
        {
	        for (int index = 0; index < iteration_length; index++)
	        {
	            Entity entity_nearby = (Entity) entities_nearby_list.get(index);
	            
	           
	            if (entity_nearby instanceof EntityVillager)
	            {
		            double overall_distance_squared = entity_nearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
		            
		            if (((distance < 0.0D) || (overall_distance_squared < (distance * distance))) && ((current_minimum_distance == -1D) || (overall_distance_squared < current_minimum_distance)) && ((EntityLivingBase) entity_nearby).canEntityBeSeen(entity))
		            {
		                current_minimum_distance = overall_distance_squared;
		                entityliving = (EntityLivingBase) entity_nearby;
		            }
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
        int random_number = rand.nextInt(12);
        if (getIsHumanForm())
        {
            switch (random_number)
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
        
        switch (random_number)
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
            for (int intex = 0; intex < 2; intex++)
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
        int x_coordinate = MathHelper.floor_double(posX);
        int y_coordinate = MathHelper.floor_double(boundingBox.minY) + 1;
        int z_coordinate = MathHelper.floor_double(posZ);
        float f = 0.1F;
        
        for (int index = 0; index < 30; index++)
        {
            double x_offset = x_coordinate + worldObj.rand.nextFloat();
            double y_offset = y_coordinate + worldObj.rand.nextFloat();
            double z_offset = z_coordinate + worldObj.rand.nextFloat();
            
            double x_velocity = x_offset - x_coordinate;
            double y_velocity = y_offset - y_coordinate;
            double z_velocity = z_offset - z_coordinate;
            
            double overall_velocity_squared = MathHelper.sqrt_double((x_velocity * x_velocity) + (y_velocity * y_velocity) + (z_velocity * z_velocity));
            
            x_velocity /= overall_velocity_squared;
            y_velocity /= overall_velocity_squared;
            z_velocity /= overall_velocity_squared;
           
            double velocity_multiplier = 0.5D / ((overall_velocity_squared / f) + 0.10000000000000001D);
            
            velocity_multiplier *= (worldObj.rand.nextFloat() * worldObj.rand.nextFloat()) + 0.3F;
            x_velocity *= velocity_multiplier;
            y_velocity *= velocity_multiplier;
            z_velocity *= velocity_multiplier;
           
            worldObj.spawnParticle("explode", (x_offset + (x_coordinate * 1.0D)) / 2D, (y_offset + (y_coordinate * 1.0D)) / 2D, (z_offset + (z_coordinate * 1.0D)) / 2D, x_velocity, y_velocity, z_velocity);
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