package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityMole extends MoCEntityTameableAnimal {

    public MoCEntityMole(World world) 
    {
        super(world);
        setSize(1F, 0.5F);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return MoCreatures.proxy.getTexture("mole.png");
    }


    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // state - 0 outside / 1 digging / 2 underground / 3 pick-a-boo 

    }


    public boolean isOnDirt()
    {
        Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(this.boundingBox.minY - 0.5D), MathHelper.floor_double(posZ));
        return isDiggableBlock(Block.getIdFromBlock(block));//(j == 2 | j == 3 | j == 12);
    }

    private boolean isDiggableBlock(int blockID)
    {
        return blockID == 2 | blockID == 3 | blockID == 12;
    }
    
    
    /**
     * Moves entity forward underground
     */
    private void digForward()
    {
        double coordY = posY;
        double coordZ = this.posZ;
        double coordX = this.posX;
        int x = 1;
        double newPosY = coordY - Math.cos( (this.rotationPitch- 90F) / 57.29578F) * x;
        double newPosX = coordX + Math.cos((MoCTools.realAngle(this.rotationYaw- 90F) / 57.29578F)) * (Math.sin( (this.rotationPitch- 90F) / 57.29578F) * x );
        double newPosZ = coordZ + Math.sin((MoCTools.realAngle(this.rotationYaw- 90F) / 57.29578F)) * (Math.sin( (this.rotationPitch- 90F) / 57.29578F) * x );
        Block block = this.worldObj.getBlock( MathHelper.floor_double(newPosX),  MathHelper.floor_double(newPosY),  MathHelper.floor_double(newPosZ)); 
        if (isDiggableBlock(Block.getIdFromBlock(block)))
        {
            this.setPosition(newPosX, newPosY, newPosZ);   
        }
    }

    /**
     * obtains State
     * @return 0 outside / 1 digging / 2 underground / 3 pick-a-boo
     */
    public byte getState()
    {
        return dataWatcher.getWatchableObjectByte(22);
    }

    /**
     * Changes the state
     * @param b 0 outside / 1 digging / 2 underground / 3 pick-a-boo
     */
    public void setState(byte b)
    {
        dataWatcher.updateObject(22, Byte.valueOf(b));
    }


    @Override
    public int pitchRotationOffset() 
    {

        int state = (int)getState();
        switch (state)
        {
        case 0:
            return 0;
        case 1:
            return -45;
        case 2:
            return 0;
        case 3:
            return 60;
        default: 
            return 0;
        }
    }

    @Override
    public float getAdjustedYOffset()
    {
        int state = (int)getState();
        switch (state)
        {
        case 0:
            return 0F;
        case 1:
            return 0.3F;
        case 2:
            return 1F;
        case 3:
            return 0.1F;
        default: 
            return 0F;
        }
    }


    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();


        if (MoCreatures.isServer())
        {
            if (rand.nextInt(10) == 0 && getState() == 1)
            {
                setState((byte)2);
            }

            if (getState() != 2 && getState() != 1 && isOnDirt())
            {
                EntityLivingBase entityLiving = getScaryEntity(4D);
                if ((entityLiving != null) && canEntityBeSeen(entityLiving))
                {
                    setState((byte)1);
                    setPathToEntity(null);
                }
            }

            //if underground and no enemies: pick a scary entity
            if (rand.nextInt(20) == 0 && getState() == 2 && (getScaryEntity(4D) == null))
            {
                setState((byte)3);
                setPathToEntity(null);
            }

            //if not on dirt, get out!
            if (getState() != 0 && !isOnDirt())
            {
                setState((byte)0);
            }

            
            if (rand.nextInt(30) == 0 && getState() == 3)
            {
                setState((byte)2);
            }
            
            //digging fx
            if ((getState() == 1 || getState() == 2))
            {
                setSprinting(true);
            }
            else
            {
                setSprinting(false);
            }
        }
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getState() == 1 || getState() == 3;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (getState() != 2) return super.attackEntityFrom(damageSource, damageTaken);
        return false;
    }
    
    @Override
    public boolean canBeCollidedWith()
    {
        return (getState() != 2);
    }
    
    @Override
    public boolean canBePushed()
    {
        return (getState() != 2);
    }
    
    @Override
    public boolean isInvisible() //stops mole's own shadow from rendering on the block above where it is digging
    {
		return (getState() == 2);
    	
    }
    
    @Override
    protected void collideWithEntity(Entity entity)
    {
        if (getState() != 2) {super.collideWithEntity(entity);}
    }
    
    @Override
    public boolean isEntityInsideOpaqueBlock()
    {
        if (getState() == 2) {return false;}
        
        return super.isEntityInsideOpaqueBlock();
    }
    
    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
    }
    
    @Override
    public boolean isEntityInvulnerable()
    {
        if (getState() == 2) {return true;}
        
        return super.isEntityInvulnerable();
    }
    
    @Override
    protected Item getDropItem()
    {
        return MoCreatures.fur;
    }
    
     @Override
    protected String getDeathSound()
    {
        return "mocreatures:rabbitdeath";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:rabbithurt";
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }
}
