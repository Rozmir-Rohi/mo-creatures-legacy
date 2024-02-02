// todo freeze for some time if close to flower
// attack player if player attacks hive?
// hive block (honey, bee spawner)

package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class MoCEntityBee extends MoCEntityInsect

{
    private int soundCount;
    private boolean upset;

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
            EntityPlayer closest_player_nearby = worldObj.getClosestPlayerToEntity(this, 5D);
            
            if (closest_player_nearby != null && getIsFlying() && --soundCount == -1)
            {
                MoCTools.playCustomSound(this, getMySound(), this.worldObj);
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
    public boolean attackEntityFrom(DamageSource damagesource, float i)
    {
        if (super.attackEntityFrom(damagesource, i))
        {
            Entity entity = damagesource.getEntity();
            if ((entity != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entity;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {

        if (this.attackTime <= 0 && (f < 2.0D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 2);
        }
    }

    @Override
    public boolean isMyFollowFood(ItemStack itemstack)
    {
        return itemstack != null && 
        		(
        			itemstack.getItem() == Item.getItemFromBlock(Blocks.red_flower) 
        			|| itemstack.getItem() == Item.getItemFromBlock(Blocks.yellow_flower)
        		);
    }
}
