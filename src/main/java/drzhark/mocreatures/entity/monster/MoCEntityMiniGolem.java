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
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
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

            if (worldObj.isDaytime())
            {
                float brightness = getBrightness(1.0F);
                if (brightness > 0.5F && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F)
                {
                    setFire(8);
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
        MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(worldObj, this, posX, posY + 2.0D, posZ);//, true, false);
        worldObj.spawnEntityInWorld(entityBlock);

        entityBlock.setType(tileBlockInfo[0]);
        entityBlock.setMetadata(tileBlockInfo[1]);
        entityBlock.setBehavior(1);
        tempBlock = entityBlock;
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
        throwCounter++;
       
        if (throwCounter < 50)
        {
            //maintains position of entityBlock above head
            tempBlock.posX = posX;
            tempBlock.posY = (posY + 1.0D);
            tempBlock.posZ = posZ;
        }

        if (throwCounter >= 50)
        {
            //throws a newly spawned entityBlock and destroys the held entityBlock
            if (entityToAttack != null && getDistanceToEntity(entityToAttack) < 48F)
            {
                //System.out.println("distance = " + getDistanceToEntity(entityToAttack));
                throwBlockAtEntity(entityToAttack, tempBlock.getType(), tempBlock.getMetadata());
            }

            tempBlock.setDead();
            setHasBlock(false);
            throwCounter = 0;
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
    protected Entity findPlayerToAttack()
    {
        EntityPlayer closestEntityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        return closestEntityPlayer != null && canEntityBeSeen(closestEntityPlayer) ? closestEntityPlayer : null;
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
     * @param blockType
     * @param metadata
     */
    protected void throwBlockAtEntity(Entity targetEntity, int blockType, int metadata)
    {
        throwBlockAtCoordinates((int) targetEntity.posX, (int) targetEntity.posY, (int) targetEntity.posZ, blockType, metadata);
    }

    /**
     * Throws stone at X,Y,Z coordinates
     * 
     * @param X
     * @param Y
     * @param Z
     * @param blockType
     * @param metadata
     */
    protected void throwBlockAtCoordinates(int X, int Y, int Z, int blockType, int metadata)
    {
        MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(worldObj, this, posX, posY + 3.0D, posZ);//, false, false);
        worldObj.spawnEntityInWorld(entityBlock);
        entityBlock.setType(blockType);
        entityBlock.setMetadata(metadata);
        entityBlock.setBehavior(0);
        entityBlock.motionX = ((X - posX) / 20.0D);
        entityBlock.motionY = ((Y - posY) / 20.0D + 0.5D);
        entityBlock.motionZ = ((Z - posZ) / 20.0D);
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