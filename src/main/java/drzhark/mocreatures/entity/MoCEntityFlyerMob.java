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
    protected void attackEntity(Entity entity, float f)
    {
        if (attackTime <= 0 && (f < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
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
        EntityPlayer entityplayer = worldObj.getClosestPlayerToEntity(this, 20D);
        if ((entityplayer != null) && canEntityBeSeen(entityplayer))
        {
            return entityplayer;
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
    public void moveEntityWithHeading(float strafe_movement, float forward_movement)
    {
        if (handleWaterMovement())
        {
            double y_coordinate = posY;
            moveFlying(strafe_movement, forward_movement, 0.02F);
            moveEntity(motionX, motionY, motionZ);
            motionX *= 0.80000001192092896D;
            motionY *= 0.80000001192092896D;
            motionZ *= 0.80000001192092896D;
        }
        else if (handleLavaMovement())
        {
            double y_coordinate1 = posY;
            moveFlying(strafe_movement, forward_movement, 0.02F);
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
            moveFlying(strafe_movement, forward_movement, onGround ? 0.1F * f3 : 0.02F);
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
        double x_distance_travelled = posX - prevPosX;
        double z_distance_travelled = posZ - prevPosZ;
        float overall_horizontal_distance_travelled_squared = MathHelper.sqrt_double((x_distance_travelled * x_distance_travelled) + (z_distance_travelled * z_distance_travelled)) * 4F;
        if (overall_horizontal_distance_travelled_squared > 1.0F)
        {
            overall_horizontal_distance_travelled_squared = 1.0F;
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
        int y_coordinate = MathHelper.floor_double(boundingBox.minY);
        boolean is_water_movement = handleWaterMovement();
        boolean is_lava_movement = handleLavaMovement();
        rotationPitch = 0.0F;
        if ((entitypath == null) || (rand.nextInt(100) == 0))
        {
            super.updateEntityActionState();
            entitypath = null;
            return;
        }
        //TODO 4FIX test!
        Vec3 vector_3D = entitypath.getPosition(this); //Client
        //vector_3D vector_3D = entitypath.getPosition(this); //Server
        for (double d = width * 2.0F; (vector_3D != null) && (vector_3D.squareDistanceTo(posX, vector_3D.yCoord, posZ) < (d * d));)
        {
            entitypath.incrementPathIndex();
            if (entitypath.isFinished())
            {
                vector_3D = null;
                entitypath = null;
            }
            else
            {
                //TODO 4FIX test!
                vector_3D = entitypath.getPosition(this); //client
                //vector_3D = entitypath.getPosition(this); //server
            }
        }

        isJumping = false;
        if (vector_3D != null)
        {
            double x_vector_distance = vector_3D.xCoord - posX;
            double y_vector_distance = vector_3D.yCoord - y_coordinate;
            double z_vector_distance = vector_3D.zCoord - posZ;
            float angle_in_degrees_to_new_location = (float) ((Math.atan2(z_vector_distance, x_vector_distance) * 180D) / Math.PI) - 90F;
            float amount_of_degrees_to_change_rotationYaw_by = angle_in_degrees_to_new_location - rotationYaw;
            
            moveForward = (float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
            
            for (; amount_of_degrees_to_change_rotationYaw_by < -180F; amount_of_degrees_to_change_rotationYaw_by += 360F)
            {
            }
            for (; amount_of_degrees_to_change_rotationYaw_by >= 180F; amount_of_degrees_to_change_rotationYaw_by -= 360F)
            {
            }
            if (amount_of_degrees_to_change_rotationYaw_by > 30F)
            {
                amount_of_degrees_to_change_rotationYaw_by = 30F;
            }
            if (amount_of_degrees_to_change_rotationYaw_by < -30F)
            {
                amount_of_degrees_to_change_rotationYaw_by = -30F;
            }
            rotationYaw += amount_of_degrees_to_change_rotationYaw_by;
            if (hasAttacked && (entityToAttack != null))
            {
                double x_distance = entityToAttack.posX - posX;
                double z_distance = entityToAttack.posZ - posZ;
                float previous_rotationYaw = rotationYaw;
                rotationYaw = (float) ((Math.atan2(z_distance, x_distance) * 180D) / Math.PI) - 90F;
                float angle_in_degrees_between_previous_and_new_rotationYaw = (((previous_rotationYaw - rotationYaw) + 90F) * (float) Math.PI) / 180F;
                moveStrafing = -MathHelper.sin(angle_in_degrees_between_previous_and_new_rotationYaw) * moveForward * 1.0F;
                moveForward = MathHelper.cos(angle_in_degrees_between_previous_and_new_rotationYaw) * moveForward * 1.0F;
            }
            if (y_vector_distance > 0.0D)
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
        if ((rand.nextFloat() < 0.8F) && (is_water_movement || is_lava_movement))
        {
            isJumping = true;
        }
    }
}