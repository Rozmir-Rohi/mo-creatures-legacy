package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.MoCEntityAmbient;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityPiranha extends MoCEntitySmallFish{

    public static final String fishNames[] = { "Piranha"};

    public MoCEntityPiranha(World world)
    {
        super(world);
        setSize(0.3F, 0.3F);
        setMoCAge(30 + rand.nextInt(70));
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }

    @Override
    public void selectType()
    {
        setType(1);
    }

    @Override
	public ResourceLocation getTexture()
    {
       return MoCreatures.proxy.getTexture("smallfish_piranha.png");
    }
    
    @Override
    protected Entity findPlayerToAttack()
    {
        if ((worldObj.difficultySetting.getDifficultyId() > 0))
        {
            EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 12D);
            if ((entityPlayer != null) && entityPlayer.isInWater() && !getIsTamed()) { return entityPlayer; }
            
            if (rand.nextInt(80) == 0)
            {
            	EntityLivingBase entityLiving = MoCTools.getClosestEntityLivingThatCanBeTargetted(this, 4D);
                return entityLiving;
            }
        }
        return null;
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
	public boolean isNotScared()
    {
        return true;
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (entity.isInWater() && (distanceToEntity < 0.8D))
        {
            if (entity instanceof EntityPlayer && ((EntityPlayer)entity).ridingEntity != null)
            {
                Entity playerMount = ((EntityPlayer)entity).ridingEntity;
                if (playerMount instanceof EntityBoat) 
                {
                    return;
                }
            }
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int fishDropChance = rand.nextInt(100);
        if (fishDropChance < 70)
        {
            entityDropItem(new ItemStack(Items.fish, 1, 0), 0.0F);
        }
        else
        {
            int amountOfEggsToDrop = rand.nextInt(2);
            for (int index = 0; index < amountOfEggsToDrop; index++)
            {
                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, 90), 0.0F); 
            }
        }
    }
    
    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (
        			!(entity.isInWater()) // do not hunt if entity is not in water
                    || !(entity instanceof EntityLivingBase) // do not hunt the following mobs below
                    || (entity instanceof MoCEntityPiranha) 
                    || (entity == entity.riddenByEntity) 
                    || (entity == entity.ridingEntity) 
                    || (entity instanceof MoCEntityAmbient)
                    || (entity instanceof MoCEntityKittyBed || entity instanceof MoCEntityLitterBox)
                    || (entity instanceof IMob || entity instanceof MoCEntityMob) 
                    || (entity instanceof EntityPlayer)
                    || (getIsTamed() && (entity instanceof IMoCEntity) && ((IMoCEntity)entity).getIsTamed() ) // do not hunt if this is tamed and the target is a tamed MOC mob
                    || ((entity instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)) 
                    || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves))
                );
    }
    
    @Override
    public boolean isMyHealFood(ItemStack itemStack)
    {
    	if (itemStack != null)
    	{
	    	Item item = itemStack.getItem();
	    	
	    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemStack);
	    	
	    	return
	    		(
	    			(item == Items.fish && itemStack.getItemDamage() != 3) //any vanilla mc raw fish except a pufferfish
        			|| oreDictionaryNameArray.contains("listAllfishraw")
        		);
    	}
    	return false;
    }
}