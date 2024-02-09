package drzhark.mocreatures.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class MoCEntityFlyerMob extends MoCEntityMob {
    protected int attackStrength;
    private PathEntity entitypath;

    public MoCEntityFlyerMob(World world)
    {
        super(world);
        isCollidedVertically = false;
        setSize(1.5F, 1.5F);
        attackStrength = 3;
        //health = 10;
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackStrength);
        }
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 20D);
        if ((entityPlayer != null) && canEntityBeSeen(entityPlayer))
        {
            return entityPlayer;
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return super.getCanSpawnHere();
    }

    @Override
    public boolean isOnLadder()
    {
        return false;
    }

    @Override
    public void moveEntityWithHeading(float strafeMovement, float forwardMovement)
    {
        if (handleWaterMovement())
        {
            double yCoordinate = posY;
            moveFlying(strafeMovement, forwardMovement, 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.80000001192092896D;
            motionY *= 0.80000001192092896D;
            motionZ *= 0.80000001192092896D;
        }
        else if (handleLavaMovement())
        {
            double yCoordinate1 = posY;
            moveFlying(strafeMovement, forwardMovement, 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
        }
        else
        {
            float movement = 0.91F;
            if (onGround)
            {
                movement = 0.5460001F;
                Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
                if (block != Blocks.air)
                {
                    movement = block.slipperiness * 0.91F;
                }
            }
            float f3 = 0.162771F / (movement * movement * movement);
            moveFlying(strafeMovement, forwardMovement, onGround ? 0.1F * f3 : 0.02F);
            movement = 0.91F;
            if (onGround)
            {
                movement = 0.5460001F;
                Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ));
                if (block != Blocks.air)
                {
                    movement = block.slipperiness * 0.91F;
                }
            }
            moveEntity(motionX, motionY, motionZ);
            motionX *= movement;
            motionY *= movement;
            motionZ *= movement;
            if (isCollidedHorizontally)
            {
                motionY = 0.20000000000000001D;
            }
            if (rand.nextInt(30) == 0)
            {
                motionY = -0.25D;
            }
        }
        double xDistanceTravelled = posX - prevPosX;
        double zDistanceTravelled = posZ - prevPosZ;
        float overallHorizontalDistanceTravelledSquared = MathHelper.sqrt_double((xDistanceTravelled * xDistanceTravelled) + (zDistanceTravelled * zDistanceTravelled)) * 4F;
        if (overallHorizontalDistanceTravelledSquared > 1.0F)
        {
            overallHorizontalDistanceTravelledSquared = 1.0F;
        }
    }

    @Override
    protected void updateEntityActionState()
    {
        hasAttacked = false;
        float f = 16F;
        if (entityToAttack == null)
        {
            entityToAttack = findPlayerToAttack();
            if (entityToAttack != null)
            {
                entitypath = worldObj.getPathEntityToEntity(this, entityToAttack, f, true, false, false, true);
            }
        }
        else if (!entityToAttack.isEntityAlive())
        {
            entityToAttack = null;
        }
        else
        {
            float distance = entityToAttack.getDistanceToEntity(this);
            if (canEntityBeSeen(entityToAttack))
            {
                attackEntity(entityToAttack, distance);
            }
        }
        if (!hasAttacked && (entityToAttack != null) && ((entitypath == null) || (rand.nextInt(10) == 0)))
        {
            entitypath = worldObj.getPathEntityToEntity(this, entityToAttack, f, true, false, false, true);
        }
        else if (((entitypath == null) && (rand.nextInt(80) == 0)) || (rand.nextInt(80) == 0))
        {
            boolean flag = false;
            int j = -1;
            int k = -1;
            int l = -1;
            float f2 = -99999F;
            for (int i1 = 0; i1 < 10; i1++)
            {
                int j1 = MathHelper.floor_double((posX + rand.nextInt(13)) - 6D);
                int k1 = MathHelper.floor_double((posY + rand.nextInt(7)) - 3D);
                int l1 = MathHelper.floor_double((posZ + rand.nextInt(13)) - 6D);
                float f3 = getBlockPathWeight(j1, k1, l1);
                if (f3 > f2)
                {
                    f2 = f3;
                    j = j1;
                    k = k1;
                    l = l1;
                    flag = true;
                }
            }

            if (flag)
            {
                entitypath = worldObj.getEntityPathToXYZ(this, j, k, l, 10F, true, false, false, true);
            }
        }
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        boolean isWaterMovement = handleWaterMovement();
        boolean isLavaMovement = handleLavaMovement();
        rotationPitch = 0.0F;
        if ((entitypath == null) || (rand.nextInt(100) == 0))
        {
            super.updateEntityActionState();
            entitypath = null;
            return;
        }
        //TODO 4FIX test!
        Vec3 vectorThreeDimensional = entitypath.getPosition(this); //Client
        //vectorThreeDimensional vectorThreeDimensional = entitypath.getPosition(this); //Server
        for (double d = width * 2.0F; (vectorThreeDimensional != null) && (vectorThreeDimensional.squareDistanceTo(posX, vectorThreeDimensional.yCoord, posZ) < (d * d));)
        {
            entitypath.incrementPathIndex();
            if (entitypath.isFinished())
            {
                vectorThreeDimensional = null;
                entitypath = null;
            }
            else
            {
                //TODO 4FIX test!
                vectorThreeDimensional = entitypath.getPosition(this); //client
                //vectorThreeDimensional = entitypath.getPosition(this); //server
            }
        }

        isJumping = false;
        if (vectorThreeDimensional != null)
        {
            double vectorDistanceX = vectorThreeDimensional.xCoord - posX;
            double vectorDistanceY = vectorThreeDimensional.yCoord - yCoordinate;
            double vectorDistanceZ = vectorThreeDimensional.zCoord - posZ;
            float angleInDegreesToNewLocation = (float) ((Math.atan2(vectorDistanceZ, vectorDistanceX) * 180D) / Math.PI) - 90F;
            float amountOfDegreesToChangeRotationYawBy = angleInDegreesToNewLocation - rotationYaw;
            
            moveForward = (float)getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
            
            for (; amountOfDegreesToChangeRotationYawBy < -180F; amountOfDegreesToChangeRotationYawBy += 360F)
            {
            }
            for (; amountOfDegreesToChangeRotationYawBy >= 180F; amountOfDegreesToChangeRotationYawBy -= 360F)
            {
            }
            if (amountOfDegreesToChangeRotationYawBy > 30F)
            {
                amountOfDegreesToChangeRotationYawBy = 30F;
            }
            if (amountOfDegreesToChangeRotationYawBy < -30F)
            {
                amountOfDegreesToChangeRotationYawBy = -30F;
            }
            rotationYaw += amountOfDegreesToChangeRotationYawBy;
            if (hasAttacked && (entityToAttack != null))
            {
                double xDistance = entityToAttack.posX - posX;
                double zDistance = entityToAttack.posZ - posZ;
                float previousRotationYaw = rotationYaw;
                rotationYaw = (float) ((Math.atan2(zDistance, xDistance) * 180D) / Math.PI) - 90F;
                float angleInDegreesBetweenPreviousAndNewRotationYaw = (((previousRotationYaw - rotationYaw) + 90F) * (float) Math.PI) / 180F;
                moveStrafing = -MathHelper.sin(angleInDegreesBetweenPreviousAndNewRotationYaw) * moveForward * 1.0F;
                moveForward = MathHelper.cos(angleInDegreesBetweenPreviousAndNewRotationYaw) * moveForward * 1.0F;
            }
            if (vectorDistanceY > 0.0D)
            {
                isJumping = true;
            }
        }
        if (entityToAttack != null)
        {
            faceEntity(entityToAttack, 30F, 30F);
        }
        if (isCollidedHorizontally)
        {
            isJumping = true;
        }
        if ((rand.nextFloat() < 0.8F) && (isWaterMovement || isLavaMovement))
        {
            isJumping = true;
        }
    }
}