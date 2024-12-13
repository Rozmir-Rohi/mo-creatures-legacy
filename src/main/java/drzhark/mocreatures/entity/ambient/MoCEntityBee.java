// todo freeze for some time if close to flower
// attack player if player attacks hive?
// hive block (honey, bee spawner)

package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityBee extends MoCEntityInsect

{
    private int soundCount;

    public MoCEntityBee(World world)
    {
        super(world);
        texture = "bee.png";
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            EntityPlayer closestPlayerNearby = worldObj.getClosestPlayerToEntity(this, 5D);
            
            if (closestPlayerNearby != null && getIsFlying() && --soundCount == -1)
            {
                MoCTools.playCustomSound(this, getMySound(), worldObj);
                soundCount = 20;
            }

            if (entityToAttack == null && getIsFlying() && rand.nextInt(500) == 0)
            {
                setIsFlying(false);
            }
            
            if (entityToAttack != null && (!getIsFlying() || onGround))
            {
            	motionY += 0.3D;
            	setIsFlying(true);
            }
            
        }
    }

    private String getMySound()
    {
        if (entityToAttack != null) { return "beeupset"; }
        return "bee";
    }

    @Override
    protected float getFlyingSpeed()
    {
    	return 0.5F;
    }

    @Override
    protected float getWalkingSpeed()
    {
        return 0.2F;
    }

    @Override
    public int getTalkInterval()
    {
        return 2000;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.1F;
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
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            
            if (entityThatAttackedThisCreature != this && worldObj.difficultySetting.getDifficultyId() > 0)
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
    protected void attackEntity(Entity entity, float distanceToEntity)
    {

        if (attackTime <= 0 && (distanceToEntity < 2.0D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    public boolean isMyFollowFood(ItemStack itemStack)
    {
        return itemStack != null && 
        		(
        			itemStack.getItem() == Item.getItemFromBlock(Blocks.red_flower) 
        			|| itemStack.getItem() == Item.getItemFromBlock(Blocks.yellow_flower)
        		);
    }
}
