package drzhark.mocreatures.entity.monster;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.item.MoCEntityThrowableBlockForGolem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MoCEntityMiniGolem extends MoCEntityMob {

    public int throwCounter;
    public MoCEntityThrowableBlockForGolem tempBlock;
    
    public MoCEntityMiniGolem(World world)
    {
        super(world);
        texture = "minigolem.png";
        setSize(1.0F, 1.0F);
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // angry 0 = false, 1 = true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // hasRock 0 = false, 1 = true
    }

    public boolean getIsAngry()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setIsAngry(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public boolean getHasBlock()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    public void setHasBlock(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }
    

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (entityToAttack == null )
            {
                if (getIsAngry()) setIsAngry(false);
            }
            else
            {
                if (!getIsAngry()) setIsAngry(true);
            }

            if (this.worldObj.isDaytime())
            {
                float var1 = this.getBrightness(1.0F);
                if (var1 > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F)
                {
                    this.setFire(8);
                }
            }
            
            if (getIsAngry() && entityToAttack != null)
            {
                if (!getHasBlock() && rand.nextInt(30) == 0)
                {
                    acquireTileBlock();
                }
                
                if (getHasBlock()) 
                {
                    attackWithEntityBlock();
                }
            }
        }
    }

    protected void acquireTileBlock()
    {
        int[] tileBlockInfo = MoCTools.destroyRandomBlockWithMetadata(this, 3D);
        if ( //ignore following blocks
        		tileBlockInfo[0] == -1
        		|| tileBlockInfo[0] == 0 //air
        		|| tileBlockInfo[0] == 7 //bedrock
        		|| tileBlockInfo[0] == 8 //flowing water
        		|| tileBlockInfo[0] == 9 //water
        		|| tileBlockInfo[0] == 10 //flowing lava
        		|| tileBlockInfo[0] == 11 //lava
        	)
        {
            throwCounter = 1;
            setHasBlock(false);
            return;
        }
        //creates a dummy entityBock on top of it
        MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(this.worldObj, this, this.posX, this.posY + 2.0D, this.posZ);//, true, false);
        this.worldObj.spawnEntityInWorld(entityBlock);

        entityBlock.setType(tileBlockInfo[0]);
        entityBlock.setMetadata(tileBlockInfo[1]);
        entityBlock.setBehavior(1);
        this.tempBlock = entityBlock;
        setHasBlock(true);
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getHasBlock() && entityToAttack != null;
    }

    /**
     * 
     */
    protected void attackWithEntityBlock()
    {
        this.throwCounter++;
       
        if (this.throwCounter < 50)
        {
            //maintains position of entityBlock above head
            this.tempBlock.posX = this.posX;
            this.tempBlock.posY = (this.posY + 1.0D);
            this.tempBlock.posZ = this.posZ;
        }

        if (this.throwCounter >= 50)
        {
            //throws a newly spawned entityBlock and destroys the held entityBlock
            if (entityToAttack != null && this.getDistanceToEntity(entityToAttack) < 48F)
            {
                //System.out.println("distance = " + this.getDistanceToEntity(entityToAttack));
                throwBlockAtEntity(entityToAttack, this.tempBlock.getType(), this.tempBlock.getMetadata());
            }

            this.tempBlock.setDead();
            setHasBlock(false);
            this.throwCounter = 0;
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
    protected Entity findPlayerToAttack()
    {
        EntityPlayer var1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        return var1 != null && this.canEntityBeSeen(var1) ? var1 : null;
    }

    /**
     * Stretches the model to that size
     */
    @Override
    public float getSizeFactor()
    {
        return 1.0F;
    }

    /**
     * Throws stone at entity
     * 
     * @param targetEntity
     * @param blocktype
     * @param metadata
     */
    protected void throwBlockAtEntity(Entity targetEntity, int blocktype, int metadata)
    {
        throwBlockAtCoordinates((int) targetEntity.posX, (int) targetEntity.posY, (int) targetEntity.posZ, blocktype, metadata);
    }

    /**
     * Throws stone at X,Y,Z coordinates
     * 
     * @param X
     * @param Y
     * @param Z
     * @param blocktype
     * @param metadata
     */
    protected void throwBlockAtCoordinates(int X, int Y, int Z, int blocktype, int metadata)
    {
        MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(this.worldObj, this, this.posX, this.posY + 3.0D, this.posZ);//, false, false);
        this.worldObj.spawnEntityInWorld(entityBlock);
        entityBlock.setType(blocktype);
        entityBlock.setMetadata(metadata);
        entityBlock.setBehavior(0);
        entityBlock.motionX = ((X - this.posX) / 20.0D);
        entityBlock.motionY = ((Y - this.posY) / 20.0D + 0.5D);
        entityBlock.motionZ = ((Z - this.posZ) / 20.0D);
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:golemgrunt";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:golemgrunt";
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }
}