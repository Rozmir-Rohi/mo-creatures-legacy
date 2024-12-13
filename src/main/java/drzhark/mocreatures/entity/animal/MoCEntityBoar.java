package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityBoar extends MoCEntityAnimal {
    protected int attackDamage;
    protected double attackRange;

    public MoCEntityBoar(World world)
    {
        super(world);
        //texture = MoCreatures.proxy.MODEL_TEXTURE + "boara.png";
        setSize(0.9F, 0.9F);
        attackDamage = 1;
        attackRange = 1.0D;
        setMoCAge(50);
        if (rand.nextInt(4) == 0)
        {
            setAdult(false);

        }
        else
        {
            setAdult(true);
        }
    }

    @Override
	protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        if (getIsAdult()) { return MoCreatures.proxy.getTexture("boara.png"); }
        return MoCreatures.proxy.getTexture("boarb.png");

    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            if ((riddenByEntity == entityThatAttackedThisCreature) || (ridingEntity == entityThatAttackedThisCreature)) { return true; }
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0) && getIsAdult())
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
        return getIsAdult();
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        if (worldObj.difficultySetting.getDifficultyId() > 0)
        {
            EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, attackRange);
            if ((entityPlayer != null) && (rand.nextInt(50) == 0)) { return entityPlayer; }
        }
        return null;
    }

    

    @Override
    public void onLivingUpdate()
    {
        if (worldObj.difficultySetting.getDifficultyId() == 1)
        {
            attackRange = 2D;
            attackDamage = 1;
        }
        else if (worldObj.difficultySetting.getDifficultyId() > 1)
        {
            attackRange = 3D;
            attackDamage = 2;
        }
        super.onLivingUpdate();

        if ((MoCreatures.isServer()) && !getIsAdult() && (rand.nextInt(250) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 100)
            {
                setAdult(true);
            }
        }
    }

    @Override
    protected Item getDropItem()
    {

        if (rand.nextInt(2) == 0) { return Items.porkchop; }

        return MoCreatures.hide;
    }

    @Override
    protected String getLivingSound()
    {
        return "mob.pig.say";
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.pig.say";
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.pig.death";
    }
}