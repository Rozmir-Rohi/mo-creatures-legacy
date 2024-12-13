package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityBird extends MoCEntityTameableAnimal {
    public boolean isFleeing;
    public float wingB;
    public float wingC;
    public float wingD;
    public float wingE;
    public float wingH;
    public boolean textureSet;
    private boolean isPicked;

    public static final String birdNames[] = { "Dove", "Crow", "Parrot", "Blue", "Canary", "Red" };

    public MoCEntityBird(World world)
    {
        super(world);
        setSize(0.4F, 0.3F);
        isCollidedVertically = true;
        wingB = 0.0F;
        wingC = 0.0F;
        wingH = 1.0F;
        isFleeing = false;
        textureSet = false;
        setTamed(false);
    }

    @Override
	protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    }
    
    @Override
    public boolean doesForageForFood()
    {
    	return true;
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            setType(rand.nextInt(6)+1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {

        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("birdwhite.png");
        case 2:
            return MoCreatures.proxy.getTexture("birdblack.png");
        case 3:
            return MoCreatures.proxy.getTexture("birdgreen.png");
        case 4:
            return MoCreatures.proxy.getTexture("birdblue.png");
        case 5:
            return MoCreatures.proxy.getTexture("birdyellow.png");
        case 6:
            return MoCreatures.proxy.getTexture("birdred.png");

        default:
            return MoCreatures.proxy.getTexture("birdblue.png");
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // preTamed - 0 false 1 true
    }

    public boolean getPreTamed()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }
    
    public void setPreTamed(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }
    
    
    @Override
    protected void fall(float f)
    {
    }

    private int[] FindTreeTop(int i, int j, int k)
    {
        int l = i - 5;
        int i1 = k - 5;
        int j1 = i + 5;
        int k1 = j + 7;
        int l1 = k + 5;
        for (int i2 = l; i2 < j1; i2++)
        {
            label0: for (int j2 = i1; j2 < l1; j2++)
            {
                Block block = worldObj.getBlock(i2, j, j2);
                if ((block.isAir(worldObj, i2, j, j2)) || (block.getMaterial() != Material.wood))
                {
                    continue;
                }
                int l2 = j;
                do
                {
                    if (l2 >= k1)
                    {
                        continue label0;
                    }
                    Block block1 = worldObj.getBlock(i2, l2, j2);
                    if (block1.isAir(worldObj, i2, l2, j2)) { return (new int[] { i2, l2 + 2, j2 }); }
                    l2++;
                } while (true);
            }

        }

        return (new int[] { 0, 0, 0 });
    }

    public boolean flyToNextEntity(Entity entity)
    {
        if (entity != null)
        {
            int entityPosX = MathHelper.floor_double(entity.posX);
            int entityPosY = MathHelper.floor_double(entity.posY);
            int entityPosZ = MathHelper.floor_double(entity.posZ);
            
            faceLocation(entityPosX, entityPosY, entityPosZ, 30F);
            
            if (MathHelper.floor_double(posY) < entityPosY)
            {
                motionY += 0.14999999999999999D;
            }
            if (posX < entity.posX)
            {
                double xDistance = entity.posX - posX;
                if (xDistance > 0.5D)
                {
                    motionX += 0.050000000000000003D;
                }
            }
            else
            {
                double xDistance = posX - entity.posX;
                if (xDistance > 0.5D)
                {
                    motionX -= 0.050000000000000003D;
                }
            }
            if (posZ < entity.posZ)
            {
                double zDistance = entity.posZ - posZ;
                if (zDistance > 0.5D)
                {
                    motionZ += 0.050000000000000003D;
                }
            }
            else
            {
                double zDistance = posZ - entity.posZ;
                if (zDistance > 0.5D)
                {
                    motionZ -= 0.050000000000000003D;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean FlyToNextTree()
    {
        int coordinatesOfLeaves[] = ReturnNearestMaterialCoord(this, Material.leaves, Double.valueOf(20D));
        int coordinatesOfTreeTop[] = FindTreeTop(coordinatesOfLeaves[0], coordinatesOfLeaves[1], coordinatesOfLeaves[2]);
        if (coordinatesOfTreeTop[1] != 0)
        {
            int xCoordinate = coordinatesOfTreeTop[0];
            int yCoordinate = coordinatesOfTreeTop[1];
            int zCoordinate = coordinatesOfTreeTop[2];
            faceLocation(xCoordinate, yCoordinate, zCoordinate, 30F);
            if ((yCoordinate - MathHelper.floor_double(posY)) > 2)
            {
                motionY += 0.14999999999999999D;
            }
            int xDistance = 0;
            int zDistance = 0;
            if (posX < xCoordinate)
            {
                xDistance = xCoordinate - MathHelper.floor_double(posX);
                motionX += 0.050000000000000003D;
            }
            else
            {
                xDistance = MathHelper.floor_double(posX) - xCoordinate;
                motionX -= 0.050000000000000003D;
            }
            if (posZ < zCoordinate)
            {
                zDistance = zCoordinate - MathHelper.floor_double(posZ);
                motionZ += 0.050000000000000003D;
            }
            else
            {
                zDistance = MathHelper.floor_double(posX) - zCoordinate;
                motionZ -= 0.050000000000000003D;
            }
            
            double overallDistance = xDistance + zDistance;
            
            if (overallDistance < 3D) { return true; }
        }
        return false;
    }

    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return (entity instanceof MoCEntityBird) || ((entity.height <= height) && (entity.width <= width)) || super.shouldEntityBeIgnored(entity);
    }

    @Override
    protected String getDeathSound()
    {
        return null; // used to be "mocreatures:birddying"
    }

    @Override
    protected void dropFewItems(boolean hasEntityBeenHitByPlayer, int levelOfLootingEnchantmentUsedToKillThisEntity)
    {   
        int randomAmount = rand.nextInt(3);

        dropItem(Items.feather, randomAmount);
        
    	if (MoCreatures.isExoticBirdsLoaded)
    	{
    		if (isBurning())
    		{
    			dropItem(GameRegistry.findItem("exoticbirds", "cooked_birdmeat_small"), 1);
    		}
    		else 
    		{
    			dropItem(GameRegistry.findItem("exoticbirds", "birdmeat_small"), 1);
    		}
    	}
    }

    @Override
    protected String getHurtSound()
    {
        return null; // used to be "mocreatures:birdhurt"
    }

    @Override
    protected String getLivingSound()
    {
        if (getType() == 1) { return "mocreatures:birdwhite"; }
        if (getType() == 2) { return "mocreatures:birdblack"; }
        if (getType() == 3) { return "mocreatures:birdgreen"; }
        if (getType() == 4) { return "mocreatures:birdblue"; }
        if (getType() == 5)
        {
            return "mocreatures:birdyellow";
        }
        else
        {
            return "mocreatures:birdred";
        }
    }

    public boolean getPicked()
    {
        return isPicked;
    }

    @Override
    public double getYOffset()
    {
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer()) { return (yOffset - 1.15F); }

        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            return (yOffset + 0.45F);
        }
        else
        {
            return yOffset;
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        
        if (super.interact(entityPlayer)) { return false; }
        ItemStack itemStack = entityPlayer.getHeldItem();
        
        if (itemStack != null)
        {		
        	if (isMyHealFood(itemStack))
        	{
        		if (--itemStack.stackSize == 0)
        		{
        			entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
        		}
        		if (MoCreatures.isServer() && !getIsTamed() && getPreTamed())
        		{
        			MoCTools.tameWithName(entityPlayer, this);
        			entityPlayer.addStat(MoCAchievements.tame_bird, 1);
        		}
        		heal(5);
        		return true;
        	}
        }

        if (
        		getIsTamed()
        		&& 
        			(
        				(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemStack == null)
        				|| (!(MoCreatures.proxy.emptyHandMountAndPickUpOnly))
        			)
        	)
    	{
    		rotationYaw = entityPlayer.rotationYaw;
    		if ((ridingEntity == null) && (entityPlayer.ridingEntity == null))
    		{
    			if (MoCreatures.isServer())
    			{
    				mountEntity(entityPlayer);
    				setPicked(true);
    				return true;
    			}
    		}

    		if ((ridingEntity == entityPlayer))
    		{
    			if (MoCreatures.isServer())
    			{
    				mountEntity(null);
    				motionX = entityPlayer.motionX * 5D;
    		        motionY = (entityPlayer.motionY / 2D) + 0.5D;
    		        motionZ = entityPlayer.motionZ * 5D;
    		        return true;
    			}
    		}
    	}
        
        return false;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        // fixes glide issue in SMP
        if (worldObj.isRemote)
        {
            if (ridingEntity != null)
            {
                updateEntityActionState();
            }
            else
            {
                //return; 
                //commenting this fixes the wing movement bug
            }
        }

        wingE = wingB;
        wingD = wingC;
        
        //wingC controls whether the bird flaps it's wings or not
        wingC = (float) (wingC + (((onGround && !isFleeing) || (ridingEntity != null && ridingEntity.motionY >= -0.08) ? -1 : 4) * 0.29999999999999999D));
       
        
        if (wingC < 0.0F)
        {
            wingC = 0.0F;
        }
        if (wingC > 1.0F)
        {
            wingC = 1.0F;
        }
        if (!onGround && (wingH < 1.0F))
        {
            wingH = 1.0F;
        }
        wingH = (float) (wingH * 0.90000000000000002D);
        if (!onGround && (motionY < 0.0D))
        {
            motionY *= 0.80000000000000004D;
        }
        wingB += wingH * 2.0F;

        //check added to avoid duplicating behavior on client / server
        if (MoCreatures.isServer())
        {
            EntityLivingBase entityLiving = getScaryEntity(5D);
            if (rand.nextInt(10) == 0 && (entityLiving != null) && !getIsTamed() && !getPreTamed() && canEntityBeSeen(entityLiving))
            {
                isFleeing = true;
            }
            if (rand.nextInt(200) == 0)
            {
                isFleeing = true;
            }
            if (isFleeing)
            {
                if (FlyToNextTree())
                {
                    isFleeing = false;
                }
                int coordinatesOfLeaves[] = ReturnNearestMaterialCoord(this, Material.leaves, Double.valueOf(16D));
                if (coordinatesOfLeaves[0] == -1)
                {
                    for (int index = 0; index < 2; index++)
                    {
                        WingFlap();
                    }

                    isFleeing = false;
                }
                if (rand.nextInt(50) == 0)
                {
                    isFleeing = false;
                }
            }
            if (rand.nextInt(10) == 0 && isInsideOfMaterial(Material.water))
            {
                WingFlap();
            }
        }
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (MoCreatures.isServer())
        {
        	if (ridingEntity != null && 
        			(damageSource.getEntity() == ridingEntity || DamageSource.inWall.equals(damageSource)))
            {
         	   return false;
            }
        	
        	if (!isFleeing) {isFleeing = true;}
        }
        
        return super.attackEntityFrom(damageSource, damageTaken);
    }

    public int[] ReturnNearestMaterialCoord(Entity entity, Material material, Double double1)
    {
        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(double1.doubleValue(), double1.doubleValue(), double1.doubleValue());
        int i = MathHelper.floor_double(axisalignedbb.minX);
        int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
        int k = MathHelper.floor_double(axisalignedbb.minY);
        int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
        int i1 = MathHelper.floor_double(axisalignedbb.minZ);
        int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);
        for (int k1 = i; k1 < j; k1++)
        {
            for (int l1 = k; l1 < l; l1++)
            {
                for (int i2 = i1; i2 < j1; i2++)
                {
                    Block block = worldObj.getBlock(k1, l1, i2);
                    if ((block != null && !block.isAir(worldObj, k1, l1, i2)) && (block.getMaterial() == material)) { return (new int[] { k1, l1, i2 }); }
                }

            }

        }

        return (new int[] { -1, 0, 0 });
    }

    public void setPicked(boolean var1)
    {
        isPicked = var1;
    }

    @Override
    protected void updateEntityActionState()
    {
        if (onGround && (rand.nextInt(10) == 0) && ((motionX > 0.050000000000000003D) || (motionZ > 0.050000000000000003D) || (motionX < -0.050000000000000003D) || (motionZ < -0.050000000000000003D)))
        {
            motionY = 0.25D;
        }
        if ((ridingEntity != null) && (ridingEntity instanceof EntityPlayer))
        {
            EntityPlayer entityPlayer = (EntityPlayer) ridingEntity;
            if (entityPlayer != null)
            {
                rotationYaw = entityPlayer.rotationYaw;
                entityPlayer.fallDistance = 0.0F;
                if (entityPlayer.motionY < -0.10000000000000001D)
                {
                    entityPlayer.motionY = -0.10000000000000001D;
                }
            }
        }
        if (!isFleeing || !getPicked())
        {
            super.updateEntityActionState();
        }
        else if (onGround)
        {
            setPicked(false);
        }
    }

    private void WingFlap()
    {
        motionY += 0.05D;
        if (rand.nextInt(30) == 0)
        {
            motionX += 0.2D;
        }
        if (rand.nextInt(30) == 0)
        {
            motionX -= 0.2D;
        }
        if (rand.nextInt(30) == 0)
        {
            motionZ += 0.2D;
        }
        if (rand.nextInt(30) == 0)
        {
            motionZ -= 0.2D;
        }
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
	    			item instanceof ItemSeeds
	    			|| (Item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
	    			|| (Item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
	    			|| oreDictionaryNameArray.size() > 0 &&
	    				(
	    					oreDictionaryNameArray.contains("listAllseed")  //BOP seeds or Palm's Harvest Seeds
	    					|| oreDictionaryNameArray.contains("foodRaisins") //GregTech6 seeds/raisins or Palm's Harvest raisins
	    				)
	    		);
    	}
    	else {return false;}
    }

    @Override
    public boolean updateMount()
    {
        return getIsTamed();
    }

    @Override
    public boolean forceUpdates()
    {
        return getIsTamed();
    }

    @Override
    public int nameYOffset()
    {
        return -40;

    }

    @Override
    public double roperYOffset()
    {
        return 0.9D;
    }
}