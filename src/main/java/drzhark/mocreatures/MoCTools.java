package drzhark.mocreatures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.IMoCTameable;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.entity.ambient.MoCEntityMaggot;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.animal.MoCEntityPetScorpion;
import drzhark.mocreatures.entity.monster.MoCEntityOgre;
import drzhark.mocreatures.inventory.MoCAnimalChest;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAttachedEntity;
import drzhark.mocreatures.network.message.MoCMessageNameGUI;
import drzhark.mocreatures.utils.MoCLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

public class MoCTools {
    /**
     * Drops saddle
     */
    public static void dropSaddle(MoCEntityAnimal entity, World worldObj)
    {
        if (!entity.getIsRideable() || !MoCreatures.isServer()) { return; }
        dropCustomItem(entity, worldObj, new ItemStack(MoCreatures.craftedSaddle, 1));
        entity.setRideable(false);
    }

    /**
     * Drops chest block
     */
    public static void dropBags(MoCEntityAnimal entity, World worldObj)
    {
        if (!MoCreatures.isServer()) { return; }
        dropCustomItem(entity, worldObj, new ItemStack(Blocks.chest, 1));
    }

    /**
     * Drops item
     */
    public static void dropCustomItem(Entity entity, World worldObj, ItemStack itemStack)
    {
        if (!MoCreatures.isServer()) { return; }

        EntityItem entityItem = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, itemStack);
        float f3 = 0.05F;
        entityItem.motionX = (float) worldObj.rand.nextGaussian() * f3;
        entityItem.motionY = ((float) worldObj.rand.nextGaussian() * f3) + 0.2F;
        entityItem.motionZ = (float) worldObj.rand.nextGaussian() * f3;
        worldObj.spawnEntityInWorld(entityItem);
    }
    
    /**
     * Returns all the ore dictionary entries for the itemStack as a string array
     * 
     * @param itemStack
     * @return
     */
    public static List<String> getOreDictionaryEntries(ItemStack itemStack) {
		int[] oreDictionaryIdList = OreDictionary.getOreIDs(itemStack);
		
		List<String> oreDictionaryNameArray = new ArrayList<String>();
		        	
		if (oreDictionaryIdList.length > 0)
		{
			for (int element : oreDictionaryIdList)
			{
				oreDictionaryNameArray.add(OreDictionary.getOreName(element));
			}
		}
		return oreDictionaryNameArray;
	}
    
    
    public static boolean isPlayerInWolfForm(EntityPlayer player)
    {
    	if (
    			MoCreatures.isWitcheryLoaded
    			&& 24 <= player.getMaxHealth() && player.getMaxHealth() <= 32
    			&& !(player.isPotionActive(Potion.field_76434_w)) //if heal bost potion effect is not active
    			&& isNightVisionPotionEffectActiveAndIsItFromWitcheryWerewolfMechanism(player)
    		)
		{
			return true;
		}
    	
    	return false;
    }
    

    public static boolean isPlayerInWerewolfForm(EntityPlayer player)
    {
    	if (
    			MoCreatures.isWitcheryLoaded
    			&& 40 <= player.getMaxHealth() && player.getMaxHealth() <= 60
    			&& !(player.isPotionActive(Potion.field_76434_w)) //if heal bost potion effect is not active
    			&& isNightVisionPotionEffectActiveAndIsItFromWitcheryWerewolfMechanism(player)
    		)
		{
			return true;
		}
    	
    	return false;
    }
    
    private static boolean isNightVisionPotionEffectActiveAndIsItFromWitcheryWerewolfMechanism(EntityPlayer entityPlayer)
    {
    	if (entityPlayer.getActivePotionEffect(Potion.nightVision) != null)
		{
			PotionEffect nightVisionPotionEffect = entityPlayer.getActivePotionEffect(Potion.nightVision);
			
			int potionEffectDuration = nightVisionPotionEffect.getDuration() / 20; //converts potion effect duration from ticks to seconds
			
			System.out.println(potionEffectDuration);
			
			if (	//makes sure the potion effect duration is the same as set by Witchery
					potionEffectDuration > 15
					&& potionEffectDuration < 21
				)
			{
				return true;
			}
		}
		return false;
    }

    
    /**
     * @param entityThatIsPushing
     * @param entityToPushBack
     * @param forceOfPush
     */
    public static void pushEntityBack(Entity entityThatIsPushing, Entity entityToPushBack, float forceOfPush)
    {
        double d = entityThatIsPushing.posX - entityToPushBack.posX;
        double d1 = entityThatIsPushing.posZ - entityToPushBack.posZ;
        for (d1 = entityThatIsPushing.posZ - entityToPushBack.posZ; ((d * d) + (d1 * d1)) < 0.0001D; d1 = (Math.random() - Math.random()) * 0.01D)
        {
            d = (Math.random() - Math.random()) * 0.01D;
        }

        float f = MathHelper.sqrt_double((d * d) + (d1 * d1));
        entityToPushBack.motionX /= 2D;
        entityToPushBack.motionY /= 2D;
        entityToPushBack.motionZ /= 2D;
        entityToPushBack.motionX -= (d / f) * forceOfPush;
        entityToPushBack.motionY += forceOfPush;
        entityToPushBack.motionZ -= (d1 / f) * forceOfPush;
        if (entityToPushBack.motionY > forceOfPush)
        {
            entityToPushBack.motionY = forceOfPush;
        }
    }
    
    /**
     * Used to make creatures ram
     * 
     * @param entityPerformingTheRam
     * @param damage
     * @param dististanceToBuckleEntities
     * @param worldObj
     */
    public static void buckleMobs(EntityLiving entityPerformingTheRam, float damage, Double dististanceToBuckleEntities, World worldObj)
    {
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entityPerformingTheRam, entityPerformingTheRam.boundingBox.expand(dististanceToBuckleEntities, 2D, dististanceToBuckleEntities));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entityTarget = (Entity) list.get(i);
            if (!(entityTarget instanceof EntityLiving) || (entityPerformingTheRam.riddenByEntity != null && entityTarget == entityPerformingTheRam.riddenByEntity))
            {
                continue;
            }
            

            entityTarget.attackEntityFrom(DamageSource.causeMobDamage(entityPerformingTheRam), damage);
            pushEntityBack(entityPerformingTheRam, entityTarget, 0.6F);
        }
    }

    public static void buckleMobsNotPlayers(EntityLiving entityattacker, float damage, Double distanceToBuckleEntities, World worldObj)
    {
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entityattacker, entityattacker.boundingBox.expand(distanceToBuckleEntities, 2D, distanceToBuckleEntities));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entityTarget = (Entity) list.get(i);
            if (!(entityTarget instanceof EntityLiving) || (entityTarget instanceof EntityPlayer) ||(entityattacker.riddenByEntity != null && entityTarget == entityattacker.riddenByEntity))
            {
                continue;
            }

            entityTarget.attackEntityFrom(DamageSource.causeMobDamage(entityattacker), damage);
            pushEntityBack(entityattacker, entityTarget, 0.6F);
        }
    }
    
    public static boolean isEntityAFishThatIsInTheOcean(Entity entity)
    {
    	if (entity instanceof MoCEntityAquatic || entity instanceof MoCEntityTameableAquatic) // don't go and hunt fish if they are in the ocean
        {
        	int x = MathHelper.floor_double(entity.posX);
            int y = MathHelper.floor_double(entity.posY);
            int z = MathHelper.floor_double(entity.posZ);

            BiomeGenBase biomeThatEntityIsIn = MoCTools.biomekind(entity.worldObj, x, y, z);

            if (
            		!(BiomeDictionary.isBiomeOfType(biomeThatEntityIsIn, Type.RIVER)) 
            		&& (BiomeDictionary.isBiomeOfType(biomeThatEntityIsIn, Type.OCEAN) || BiomeDictionary.isBiomeOfType(biomeThatEntityIsIn, Type.BEACH))
            	)
            {
            	 return true;
            }
        }
    	return false;
    }

    public static void spawnNearPlayer(EntityPlayer player, int entityId, int numberToSpawn)//, World worldObj)
    {
        WorldServer worldObj = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.worldObj.provider.dimensionId);
        for (int i = 0; i < numberToSpawn; i++)
        {
            EntityLiving entityLiving = null;
            try
            {
                Class entityClass =MoCreatures.instaSpawnerMap.get(entityId);
                entityLiving = (EntityLiving) entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { worldObj });
            }catch (Exception e) 
            { 
                System.out.println(e);
            }

            if (entityLiving != null)
            {
                entityLiving.setLocationAndAngles(player.posX - 1, player.posY, player.posZ - 1, player.rotationYaw, player.rotationPitch);
                worldObj.spawnEntityInWorld(entityLiving);
            }
        }
    }

    public static void spawnNearPlayerbyName(EntityPlayer player, String eName, int numberToSpawn) 
    {
        WorldServer worldObj = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(player.worldObj.provider.dimensionId);

        for (int i = 0; i < numberToSpawn; i++)
        {
            EntityLiving entityToSpawn = null;
            try
            {
                MoCEntityData entityData = MoCreatures.mocEntityMap.get(eName);
                Class myClass = entityData.getEntityClass();
                entityToSpawn = (EntityLiving) myClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { worldObj });
            }catch (Exception e) 
            { System.out.println(e);}
            
            if (entityToSpawn != null)
            {
                IEntityLivingData entityLivingdata = null;
                entityToSpawn.onSpawnWithEgg(entityLivingdata);
                entityToSpawn.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                worldObj.spawnEntityInWorld(entityToSpawn);
            }
        }
    }

    public static void playCustomSound(Entity entity, String customSound, World worldObj)
    {
        worldObj.playSoundAtEntity(entity, "mocreatures:" + customSound, 1.0F, 1.0F + ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F));
    }

    public static void playCustomSound(Entity entity, String customSound, World worldObj, float volume)
    {
        worldObj.playSoundAtEntity(entity, "mocreatures:" + customSound, volume, 1.0F + ((worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F));
    }

    /**
     * Returns a new instance of EntityLiving based on the name of the class
     * @param eName
     * @param worldObj
     * @return
     */
    public static EntityLiving spawnListByNameClass(String eName, World worldObj) 
    {
        EntityLiving entityToSpawn = null;
        try
        {
            MoCEntityData entityData = MoCreatures.mocEntityMap.get(eName);
            Class myClass = null;
            if (entityData == null && eName.contains("PetScorpion")) // since we don't add this to our map, we need to check for it manually
            {
                myClass = MoCEntityPetScorpion.class;
            }
            else myClass = entityData.getEntityClass();
            entityToSpawn = (EntityLiving) myClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { worldObj });
        }catch (Exception e) 
        { 
            if (MoCreatures.proxy.debug) MoCLog.logger.warn("Unable to find class for entity " + eName + ", " + e);}
        return entityToSpawn;        
    }

    public static boolean nearMaterialWithDistance(Entity entity, Double double1, Material mat)
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
                    Block block = entity.worldObj.getBlock(k1, l1, i2);
                    if ((block != Blocks.air) && (block.getMaterial() == mat)) { return true; }
                }
            }
        }
        return false;
    }

    public static boolean isNearBlockName(Entity entity, Double dist, String blockName)
    {
        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(dist, dist / 2D, dist);
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
                    Block block = entity.worldObj.getBlock(k1, l1, i2);

                    if (block != Blocks.air)
                    {
                        String nameToCheck = "";
                        nameToCheck = block.getUnlocalizedName();//.getBlockName();
                        if (nameToCheck != null && nameToCheck.length() > 0)
                        {
                            if (nameToCheck.equals(blockName)) { return true; }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static TileEntityJukebox nearJukeBoxRecord(Entity entity, Double dist)
    {
        AxisAlignedBB axisAlignedBoundingBox = entity.boundingBox.expand(dist, dist / 2D, dist);
        int xMin = MathHelper.floor_double(axisAlignedBoundingBox.minX);
        int xMax = MathHelper.floor_double(axisAlignedBoundingBox.maxX + 1.0D);
        int yMin = MathHelper.floor_double(axisAlignedBoundingBox.minY);
        int yMax = MathHelper.floor_double(axisAlignedBoundingBox.maxY + 1.0D);
        int zMin = MathHelper.floor_double(axisAlignedBoundingBox.minZ);
        int zNax = MathHelper.floor_double(axisAlignedBoundingBox.maxZ + 1.0D);
        for (int x = xMin; x < xMax; x++)
        {
            for (int y = yMin; y < yMax; y++)
            {
                for (int z = zMin; z < zNax; z++)
                {
                    Block block = entity.worldObj.getBlock(x, y, z);
                    
                    if (!entity.worldObj.isAirBlock(x, y, z))
                    {
                        if (block instanceof BlockJukebox)
                        {
                            TileEntityJukebox juky = (TileEntityJukebox) entity.worldObj.getTileEntity(x, y, z);
                            return juky;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void checkForTwistedEntities(World world)
    {
        int k = 0;
        for (int l = 0; l < world.loadedEntityList.size(); l++)
        {
            Entity entity = (Entity) world.loadedEntityList.get(l);
            if (entity instanceof EntityLivingBase)
            {
                EntityLivingBase twisted = (EntityLivingBase) entity;
                if (twisted.deathTime > 0 && twisted.ridingEntity == null && twisted.getHealth() > 0)
                {
                    twisted.deathTime = 0;
                }
            }
        }
    }

    public static double getSqDistanceTo(Entity entity, double i, double j, double k)
    {
        double l = entity.posX - i;
        double i1 = entity.posY - j;
        double j1 = entity.posZ - k;
        return Math.sqrt((l * l) + (i1 * i1) + (j1 * j1));
    }

    public static int[] ReturnNearestMaterialCoord(Entity entity, Material material, Double double1, Double yOffset)
    {
        double shortestDistance = -1D;
        double distance = 0D;
        int x = -9999;
        int y = -1;
        int z = -1;

        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(double1.doubleValue(), yOffset.doubleValue(), double1.doubleValue());
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
                    Block block = entity.worldObj.getBlock(k1, l1, i2);
                    if ((block != Blocks.air) && (block.getMaterial() == material))
                    {
                        distance = getSqDistanceTo(entity, k1, l1, i2);
                        if (shortestDistance == -1D)
                        {
                            x = k1;
                            y = l1;
                            z = i2;
                            shortestDistance = distance;
                        }

                        if (distance < shortestDistance)
                        {
                            x = k1;
                            y = l1;
                            z = i2;
                            shortestDistance = distance;
                        }
                    }
                }
            }
        }

        if (entity.posX > x)
        {
            x -= 2;
        }
        else
        {
            x += 2;
        }
        if (entity.posZ > z)
        {
            z -= 2;
        }
        else
        {
            z += 2;
        }
        return (new int[] { x, y, z });
    }

    public static int[] ReturnNearestBlockCoord(Entity entity, Block block1, Double dist)
    {
        double shortestDistance = -1D;
        double distance = 0D;
        int x = -9999;
        int y = -1;
        int z = -1;

        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(dist, dist, dist);
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
                    Block block = entity.worldObj.getBlock(k1, l1, i2);
                    if ((block != Blocks.air) && (block == block1))
                    {
                        distance = getSqDistanceTo(entity, k1, l1, i2);
                        if (shortestDistance == -1D)
                        {
                            x = k1;
                            y = l1;
                            z = i2;
                            shortestDistance = distance;
                        }

                        if (distance < shortestDistance)
                        {
                            x = k1;
                            y = l1;
                            z = i2;
                            shortestDistance = distance;
                        }
                    }
                }
            }
        }

        if (entity.posX > x)
        {
            x -= 2;
        }
        else
        {
            x += 2;
        }
        if (entity.posZ > z)
        {
            z -= 2;
        }
        else
        {
            z += 2;
        }
        return (new int[] { x, y, z });
    }

    public static void moveCreatureToXYZ(EntityCreature movingEntity, int x, int y, int z, float f)
    {
        //TODO works?
        PathEntity pathEntity = movingEntity.worldObj.getEntityPathToXYZ(movingEntity, x, y, z, f, true, false, false, true);
        if (pathEntity != null)
        {
            movingEntity.setPathToEntity(pathEntity);
        }
    }

    public static void moveToWater(EntityCreature entity)
    {
        int ai[] = MoCTools.ReturnNearestMaterialCoord(entity, Material.water, Double.valueOf(20D), 2D);
        if (ai[0] > -1000)
        {
            MoCTools.moveCreatureToXYZ(entity, ai[0], ai[1], ai[2], 24F);
        }
    }

    /**
     * Gives angles in the range 0-360 i.e. 361 will be returned like 1
     * 
     * @param origAngle
     * @return
     */
    public static float realAngle(float origAngle)
    {
        return origAngle % 360F;
    }

    public static void slideEntityToXYZ(Entity entity, int x, int y, int z)
    {
        if (entity != null)
        {
            if (entity.posY < y)
            {
                entity.motionY += 0.14999999999999999D;
            }
            if (entity.posX < x)
            {
                double d = x - entity.posX;
                if (d > 0.5D)
                {
                    entity.motionX += 0.050000000000000003D;
                }
            }
            else
            {
                double d1 = entity.posX - x;
                if (d1 > 0.5D)
                {
                    entity.motionX -= 0.050000000000000003D;
                }
            }
            if (entity.posZ < z)
            {
                double d2 = z - entity.posZ;
                if (d2 > 0.5D)
                {
                    entity.motionZ += 0.050000000000000003D;
                }
            }
            else
            {
                double d3 = entity.posZ - z;
                if (d3 > 0.5D)
                {
                    entity.motionZ -= 0.050000000000000003D;
                }
            }
        }
    }

    public static float distanceToWaterSurface(Entity entity)
    {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY);
        int z = MathHelper.floor_double(entity.posZ);
        Block block = entity.worldObj.getBlock(x, y, z);
        if (block != Blocks.air && block.getMaterial() == Material.water)
        {
            for (int index = 1; index < 64; index++)
            {
                block = entity.worldObj.getBlock(x, y + index, z);
                if ( 
                		block == Blocks.air
                		|| block.getMaterial() != Material.water
                	) 
                { 
                	return index;
                }
            }
        }
        return 0F;
    }

    public static int distanceToWaterFloor(Entity entity)
    {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY);
        int z = MathHelper.floor_double(entity.posZ);
        for (int index = 0; index < 64; index++)
        {
            Block block = entity.worldObj.getBlock(x, y - index, z);
            
            if (block != Blocks.air) { return index; }
        }

        return 0;
    }

    public boolean isInsideOfMaterial(Material material, Entity entity)
    {
        double yDistance = entity.posY + entity.getEyeHeight();
        
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_float(MathHelper.floor_double(yDistance));
        int z = MathHelper.floor_double(entity.posZ);
        
        Block block = entity.worldObj.getBlock(x, y, z);
        
        if (block != Blocks.air && block.getMaterial() == material)
        {
            float f = BlockLiquid.getLiquidHeightPercent(entity.worldObj.getBlockMetadata(x, y, z)) - 0.1111111F;
            float f1 = y + 1 - f;
            return yDistance < f1;
        }
        else
        {
            return false;
        }
    }

    public static void disorientEntity(Entity entity)
    {
        double rotation = 0;
        double motion = 0;
        
        double randDouble = entity.worldObj.rand.nextGaussian();
        double randDouble1 = 0.1D * randDouble;
        
        motion = (0.2D * randDouble1) + ((1.0D - 0.2D) * motion);
        entity.motionX += motion;
        entity.motionZ += motion;
        
        double randDouble2 = 0.78D * randDouble;
        
        rotation = (0.125D * randDouble2) + ((1.0D - 0.125D) * rotation);
        
        entity.rotationYaw += rotation;
        entity.rotationPitch += rotation;
    }

    public static void slowEntity(Entity entity)
    {
        entity.motionX *= 0.8D;
        entity.motionZ *= 0.8D;
    }

    public static int colorize(int i)
    {
        return ~i & 0xf;
    }

    public int countEntities(Class class1, World worldObj)
    {
        int numberOfEntities = 0;
        for (int index = 0; index < worldObj.loadedEntityList.size(); index++)
        {
            Entity entity = (Entity) worldObj.loadedEntityList.get(index);
            if (class1.isAssignableFrom(entity.getClass()))
            {
                numberOfEntities++;
            }
        }

        return numberOfEntities;
    }

    public static BiomeGenBase whatBiomeIsAtXYZ(World world, int x, int y, int z)
    {
        WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
        if (worldchunkmanager == null) { return null; }
        //TODO works?
        BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(x, z);

        if (biomegenbase == null)
        {
            return null;
        }
        else
        {
            return biomegenbase;
        }
    }

    public static float distToPlayer(Entity entity)
    {
        //TODO 
        return 0.0F;
    }

    public static String biomeName(World world, int x, int y, int z)
    {
        WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
        if (worldchunkmanager == null) { return null; }
        BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(x, z);
        //TODO works?

        if (biomegenbase == null)
        {
            return null;
        }
        else
        {
            return biomegenbase.biomeName;
        }
    }

    public static BiomeGenBase biomekind(World world, int i, int j, int k)
    {
        WorldChunkManager worldchunkmanager = world.getWorldChunkManager();
        if (worldchunkmanager == null) { return null; }
        BiomeGenBase biomegenbase = worldchunkmanager.getBiomeGenAt(i, k);
        if (biomegenbase == null)
        {
            return null;
        }
        else
        {
            return biomegenbase;
        }
    }

    /**
     * Drops the important stuff to get going fast
     * 
     * @param worldObj
     * @param entity
     */
    public static void dropGoodies(World worldObj, Entity entity)
    {
        if (!MoCreatures.isServer()) { return; }

        EntityItem entityItem = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.log, 16));
        entityItem.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem);

        EntityItem entityItem2 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.diamond, 64));
        entityItem2.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem2);

        EntityItem entityItem3 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.pumpkin, 6));
        entityItem3.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem3);

        EntityItem entityItem4 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.cobblestone, 64));
        entityItem4.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem4);

        EntityItem entityItem5 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.apple, 24));
        entityItem5.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem5);

        EntityItem entityItem6 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.leather, 64));
        entityItem6.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem6);

        EntityItem entityItem7 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(MoCreatures.recordShuffle, 6));
        entityItem7.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem7);

        EntityItem entityItem8 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.iron_ingot, 64));
        entityItem8.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem8);

        EntityItem entityItem9 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.gold_ingot, 12));
        entityItem9.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem9);

        EntityItem entityItem10 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.string, 32));
        entityItem10.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem10);

        EntityItem entityItem12 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.red_flower, 6));
        entityItem12.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem12);

        EntityItem entityItem13 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.blaze_rod, 12));
        entityItem13.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem13);

        EntityItem entityItem14 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.ender_pearl, 12));
        entityItem14.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem14);

        EntityItem entityItem15 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.ghast_tear, 12));
        entityItem15.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem15);

        EntityItem entityItem16 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.lapis_block, 2));
        entityItem16.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem16);

        EntityItem entityItem17 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Items.bone, 12));
        entityItem17.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem17);

        EntityItem entityItem18 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(MoCreatures.unicornHorn, 16));
        entityItem18.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem18);

        EntityItem entityItem19 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Blocks.fire, 32));
        entityItem19.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem19);

        EntityItem entityItem20 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(MoCreatures.essenceDarkness, 6));
        entityItem20.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem20);

        EntityItem entityItem21 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(MoCreatures.essenceUndead, 6));
        entityItem21.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem21);

        EntityItem entityItem22 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(MoCreatures.essenceFire, 6));
        entityItem22.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem22);

        EntityItem entityItem23 = new EntityItem(worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(Item.getItemFromBlock(Blocks.wool), 6, 15));
        entityItem23.delayBeforeCanPickup = 10;
        worldObj.spawnEntityInWorld(entityItem23);

    }

    public static boolean mobGriefing(World world)
    {
        return world.getGameRules().getGameRuleBooleanValue("mobGriefing");
    }
    
    public static void destroyBlast(Entity entity, double d, double d1, double d2, float f, boolean flag)
    {
        entity.worldObj.playSoundEffect(d, d1, d2, "destroy", 4F, (1.0F + ((entity.worldObj.rand.nextFloat() - entity.worldObj.rand.nextFloat()) * 0.2F)) * 0.7F);

        boolean mobGriefing = mobGriefing(entity.worldObj);
        
        HashSet hashset = new HashSet();
        float f1 = f;
        int i = 16;
        for (int index = 0; index < i; index++)
        {
            for (int index1 = 0; index1 < i; index1++)
            {
                label0: for (int j1 = 0; j1 < i; j1++)
                {
                    if ((index != 0) && (index != (i - 1)) && (index1 != 0) && (index1 != (i - 1)) && (j1 != 0) && (j1 != (i - 1)))
                    {
                        continue;
                    }
                    double d3 = ((index / (i - 1.0F)) * 2.0F) - 1.0F;
                    double d4 = ((index1 / (i - 1.0F)) * 2.0F) - 1.0F;
                    double d5 = ((j1 / (i - 1.0F)) * 2.0F) - 1.0F;
                    double d6 = Math.sqrt((d3 * d3) + (d4 * d4) + (d5 * d5));
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    float f2 = f * (0.7F + (entity.worldObj.rand.nextFloat() * 0.6F));
                    double d8 = d;
                    double d10 = d1;
                    double d12 = d2;
                    float f3 = 0.3F;
                    float f4 = 5F;
                    do
                    {
                        if (f2 <= 0.0F)
                        {
                            continue label0;
                        }
                        int k5 = MathHelper.floor_double(d8);
                        int l5 = MathHelper.floor_double(d10);
                        int i6 = MathHelper.floor_double(d12);
                        Block block = entity.worldObj.getBlock(k5, l5, i6);
                        if (block != Blocks.air)
                        {
                            f4 = block.getBlockHardness(entity.worldObj, k5, l5, i6);
                            f2 -= (block.getExplosionResistance(entity) + 0.3F) * (f3 / 10F);
                        }
                        if ((f2 > 0.0F) && (d10 > entity.posY) && (f4 < 3F))
                        {
                            hashset.add(new ChunkPosition(k5, l5, i6));
                        }
                        d8 += d3 * f3;
                        d10 += d4 * f3;
                        d12 += d5 * f3;
                        f2 -= f3 * 0.75F;
                    } while (true);
                }

            }

        }

        f *= 2.0F;
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            int k = MathHelper.floor_double(d - f - 1.0D);
            int i1 = MathHelper.floor_double(d + f + 1.0D);
            int k1 = MathHelper.floor_double(d1 - f - 1.0D);
            int l1 = MathHelper.floor_double(d1 + f + 1.0D);
            int i2 = MathHelper.floor_double(d2 - f - 1.0D);
            int j2 = MathHelper.floor_double(d2 + f + 1.0D);
            List list = entity.worldObj.getEntitiesWithinAABBExcludingEntity(entity, AxisAlignedBB.getBoundingBox(k, k1, i2, i1, l1, j2));
            Vec3 vectorThreeDimensional = Vec3.createVectorHelper(d, d1, d2);
            for (int k2 = 0; k2 < list.size(); k2++)
            {
                Entity entity1 = (Entity) list.get(k2);
                double d7 = entity1.getDistance(d, d1, d2) / f;
                if (d7 > 1.0D)
                {
                    continue;
                }
                double d9 = entity1.posX - d;
                double d11 = entity1.posY - d1;
                double d13 = entity1.posZ - d2;
                double d15 = MathHelper.sqrt_double((d9 * d9) + (d11 * d11) + (d13 * d13));
                d9 /= d15;
                d11 /= d15;
                d13 /= d15;
                double d17 = entity.worldObj.getBlockDensity(vectorThreeDimensional, entity1.boundingBox);
                double d19 = (1.0D - d7) * d17;

                //attacks entities in server
                if (!(entity1 instanceof MoCEntityOgre))
                {
                    entity1.attackEntityFrom(DamageSource.generic, (int) (((((d19 * d19) + d19) / 2D) * 3D * f) + 1.0D));
                    double d21 = d19;
                    entity1.motionX += d9 * d21;
                    entity1.motionY += d11 * d21;
                    entity1.motionZ += d13 * d21;
                }
            }
        }

        f = f1;
        ArrayList arraylist = new ArrayList();
        arraylist.addAll(hashset);

        for (int index = arraylist.size() - 1; index >= 0; index--)
        {
            ChunkPosition chunkposition = (ChunkPosition) arraylist.get(index);
            int j3 = chunkposition.chunkPosX;
            int l3 = chunkposition.chunkPosY;
            int j4 = chunkposition.chunkPosZ;
            Block block = entity.worldObj.getBlock(j3, l3, j4);
            for (int j5 = 0; j5 < 5; j5++)
            {
                double d14 = j3 + entity.worldObj.rand.nextFloat();
                double d16 = l3 + entity.worldObj.rand.nextFloat();
                double d18 = j4 + entity.worldObj.rand.nextFloat();
                double d20 = d14 - d;
                double d22 = d16 - d1;
                double d23 = d18 - d2;
                double d24 = MathHelper.sqrt_double((d20 * d20) + (d22 * d22) + (d23 * d23));
                d20 /= d24;
                d22 /= d24;
                d23 /= d24;
                double d25 = 0.5D / ((d24 / f) + 0.10000000000000001D);
                d25 *= (entity.worldObj.rand.nextFloat() * entity.worldObj.rand.nextFloat()) + 0.3F;
                d25--;
                d20 *= d25;
                d22 *= d25 - 1.0D;
                d23 *= d25;

                /**
                 * shows explosion on clients!
                 */
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
                {
                    entity.worldObj.spawnParticle("explode", (d14 + (d * 1.0D)) / 2D, (d16 + (d1 * 1.0D)) / 2D, (d18 + (d2 * 1.0D)) / 2D, d20, d22, d23);
                    entity.motionX -= 0.0010000000474974511D;
                    entity.motionY -= 0.0010000000474974511D;
                }

            }

            //destroys blocks on server!
            if (mobGriefing && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) && block != Blocks.air)
            {
                int metadata = entity.worldObj.getBlockMetadata(j3, l3, j4);
                BlockEvent.BreakEvent event = null;
                if (!entity.worldObj.isRemote)
                {
                    event = new BlockEvent.BreakEvent(j3, l3, j4, entity.worldObj, block, metadata, FakePlayerFactory.get(DimensionManager.getWorld(entity.worldObj.provider.dimensionId), MoCreatures.MOC_FAKE_PLAYER));
                }
                if (event != null && !event.isCanceled())
                {
                    block.dropBlockAsItemWithChance(entity.worldObj, j3, l3, j4, entity.worldObj.getBlockMetadata(j3, l3, j4), 0.3F, 1);
                    entity.worldObj.setBlock(j3, l3, j4, Blocks.air, 0, 3);
                    // pass explosion instance to fix BlockTNT NPE's
                    Explosion explosion = new Explosion(entity.worldObj, null, j3, l3, j4, 3f);
                    block.onBlockDestroyedByExplosion(entity.worldObj, j3, l3, j4, explosion);
                }
            }
        }

        //sets world on fire on server
        if (mobGriefing && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) && flag)
        {
            for (int index = arraylist.size() - 1; index >= 0; index--)
            {
                ChunkPosition chunkposition1 = (ChunkPosition) arraylist.get(index);
                int k3 = chunkposition1.chunkPosX;
                int i4 = chunkposition1.chunkPosY;
                int k4 = chunkposition1.chunkPosZ;
                Block block = entity.worldObj.getBlock(k3, i4, k4);
                if ((block == Blocks.air) && (entity.worldObj.rand.nextInt(8) == 0))
                {
                    int metadata = entity.worldObj.getBlockMetadata(k3, i4, k4);
                    BlockEvent.BreakEvent event = null;
                    if (!entity.worldObj.isRemote)
                    {
                        event = new BlockEvent.BreakEvent(k3, i4, k4, entity.worldObj, block, metadata, FakePlayerFactory.get((WorldServer)entity.worldObj, MoCreatures.MOC_FAKE_PLAYER));
                    }
                    if (event != null && !event.isCanceled())
                    {
                        entity.worldObj.setBlock(k3, i4, k4, Blocks.fire, 0, 3);
                    }
                }
            }
        }
    }

    /**
     * Forces a data sync between server/client. currently used to syncrhonize
     * mounts
     */
    public static void forceDataSync(IMoCEntity entityMoCreature)
    {
        if (entityMoCreature.updateMount() && ((Entity) entityMoCreature).ridingEntity != null)
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAttachedEntity(((Entity)entityMoCreature).getEntityId(), ((Entity) entityMoCreature).ridingEntity.getEntityId()), new TargetPoint(((Entity) entityMoCreature).ridingEntity.worldObj.provider.dimensionId, ((Entity) entityMoCreature).ridingEntity.posX, ((Entity) entityMoCreature).ridingEntity.posY, ((Entity) entityMoCreature).ridingEntity.posZ, 64));
        }
    }

    public static void updatePlayerArmorEffects(EntityPlayer player)
    {
        ItemStack itemstackInArmorSlot[] = new ItemStack[4];
        itemstackInArmorSlot[0] = player.inventory.armorItemInSlot(0); //boots
        itemstackInArmorSlot[1] = player.inventory.armorItemInSlot(1); //legs
        itemstackInArmorSlot[2] = player.inventory.armorItemInSlot(2); //plate
        itemstackInArmorSlot[3] = player.inventory.armorItemInSlot(3); //helmet

        //full scorpion cave armor set, enable night vision
        if (
        		itemstackInArmorSlot[0] != null && itemstackInArmorSlot[0].getItem() == MoCreatures.scorpBootsCave 
        		&& itemstackInArmorSlot[1] != null && itemstackInArmorSlot[1].getItem() == MoCreatures.scorpLegsCave
        		&& itemstackInArmorSlot[2] != null && itemstackInArmorSlot[2].getItem() == MoCreatures.scorpPlateCave
        		&& itemstackInArmorSlot[3] != null && itemstackInArmorSlot[3].getItem() == MoCreatures.scorpHelmetCave
        	)
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 300, 0));
            return;
        }

        //full scorpion nether armor set, enable fire resistance
        if (
        		itemstackInArmorSlot[0] != null && itemstackInArmorSlot[0].getItem() == MoCreatures.scorpBootsNether
        		&& itemstackInArmorSlot[1] != null && itemstackInArmorSlot[1].getItem() == MoCreatures.scorpLegsNether
        		&& itemstackInArmorSlot[2] != null && itemstackInArmorSlot[2].getItem() == MoCreatures.scorpPlateNether
        		&& itemstackInArmorSlot[3] != null && itemstackInArmorSlot[3].getItem() == MoCreatures.scorpHelmetNether
        	)
        {
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 300, 0));
            return;
        }

        //full scorpion frost armor set, enable water breathing
        if (
        		itemstackInArmorSlot[0] != null && itemstackInArmorSlot[0].getItem() == MoCreatures.scorpBootsFrost
        		&& itemstackInArmorSlot[1] != null && itemstackInArmorSlot[1].getItem() == MoCreatures.scorpLegsFrost
        		&& itemstackInArmorSlot[2] != null && itemstackInArmorSlot[2].getItem() == MoCreatures.scorpPlateFrost
        		&& itemstackInArmorSlot[3] != null && itemstackInArmorSlot[3].getItem() == MoCreatures.scorpHelmetFrost
        	)
        {
            player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 300, 0));
            return;
        }

        //full dirt scorpion armor set, regeneration effect
        if (
        		itemstackInArmorSlot[0] != null && itemstackInArmorSlot[0].getItem() == MoCreatures.scorpBootsDirt
        		&& itemstackInArmorSlot[1] != null && itemstackInArmorSlot[1].getItem() == MoCreatures.scorpLegsDirt
        		&& itemstackInArmorSlot[2] != null && itemstackInArmorSlot[2].getItem() == MoCreatures.scorpPlateDirt
        		&& itemstackInArmorSlot[3] != null && itemstackInArmorSlot[3].getItem() == MoCreatures.scorpHelmetDirt
        	)
        {
            player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 70, 0));
            return;
        }
    }

    /**
     * Finds a random block around the entity and returns the block's ID will
     * destroy the block in the process the block will be the top one of that
     * layer, without any other block around it
     * 
     * @param entity
     *            = the Entity around which the block is searched
     * @param distance
     *            = the distance around the entity used to look for the block
     * @return
     */
    public static int destroyRandomBlock(Entity entity, double distance)
    {
        int totalNumberOfBlocksFromDistanceInThreeDimensions = (int) (distance * distance * distance);
        for (int index = 0; index < totalNumberOfBlocksFromDistanceInThreeDimensions; index++)
        {
            int x = (int) (entity.posX + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            int y = (int) (entity.posY + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 4));
            int z = (int) (entity.posZ + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            Block block = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y + 1.1D), MathHelper.floor_double(z));
            Block block1 = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));

            if (block != Blocks.air && block1 == Blocks.air)
            {
                if (mobGriefing(entity.worldObj))
                {
                    block1 = entity.worldObj.getBlock(x, y, z);
                    int metadata = entity.worldObj.getBlockMetadata(x, y, z);
                    BlockEvent.BreakEvent event = null;
                    if (!entity.worldObj.isRemote)
                    {
                        event = new BlockEvent.BreakEvent(x, y, z, entity.worldObj, block, metadata, FakePlayerFactory.get((WorldServer)entity.worldObj, MoCreatures.MOC_FAKE_PLAYER));
                    }
                    if (event != null && !event.isCanceled())
                    {
                        entity.worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
                    }
                }
                return Block.getIdFromBlock(block);
            }
        }
        return 0;
    }

    public static int[] destroyRandomBlockWithMetadata(Entity entity, double distance)
    {
        int totalNumberOfBlocksFromDistanceInThreeDimensions = (int) (distance * distance * distance);
        int metaData = 0;
        for (int index = 0; index < totalNumberOfBlocksFromDistanceInThreeDimensions; index++)
        {
            int x = (int) (entity.posX + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            int y = (int) (entity.posY + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            int z = (int) (entity.posZ + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            Block blockAbove = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y + 1.1D), MathHelper.floor_double(z));
            Block block = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));

            if (	
            		// ignores if on of the following blocks
            		block != Blocks.air 
            		&& block != Blocks.bedrock
            		&& block != Blocks.water
            		&& block != Blocks.lava
            		&& !(block instanceof BlockLiquid) //ignores flowing liquid blocks (eg flowing_water, flowing_lava)
            		
            		&& blockAbove == Blocks.air //only destroy if the block above it is air
            	) 
            {
                metaData = entity.worldObj.getBlockMetadata(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
                if (mobGriefing(entity.worldObj))
                {
                    Block block2 = entity.worldObj.getBlock(x, y, z);
                    int metadata = entity.worldObj.getBlockMetadata(x, y, z);
                    BlockEvent.BreakEvent event = null;
                    if (!entity.worldObj.isRemote)
                    {
                        event = new BlockEvent.BreakEvent(x, y, z, entity.worldObj, block2, metadata, FakePlayerFactory.get((WorldServer)entity.worldObj, MoCreatures.MOC_FAKE_PLAYER));
                    }
                    if (event != null && !event.isCanceled())
                    {
                        entity.worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
                    }
                    else
                    {
                        block = null;
                    }
                }
                return (new int[] { block == null ? -1 : Block.getIdFromBlock(block), metaData });
            }
        }
        return (new int[] { -1, metaData });
    }

    /**
     * Finds a random block around the entity and returns the coordinates the
     * block will be the top one of that layer, without any other block around
     * it
     * 
     * @param entity
     *            = the Entity around which the block is searched
     * @param distance
     *            = the distance around the entity used to look for the block
     * @return
     */
    public static int[] getRandomBlockCoords(Entity entity, double distance)
    {
        int tempX = -9999;
        int tempY = -1;
        int tempZ = -1;
        int ii = (int) (distance * distance * (distance / 2));
        for (int i = 0; i < ii; i++)
        {
            int x = (int) (entity.posX + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            int y = (int) (entity.posY + entity.worldObj.rand.nextInt((int) (distance / 2)) - (int) (distance / 4));
            int z = (int) (entity.posZ + entity.worldObj.rand.nextInt((int) (distance)) - (int) (distance / 2));
            Block block1 = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y + 1.1D), MathHelper.floor_double(z));
            Block block2 = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
            Block block3 = entity.worldObj.getBlock(MathHelper.floor_double(x + 1.1D), MathHelper.floor_double(y), MathHelper.floor_double(z));
            Block block4 = entity.worldObj.getBlock(MathHelper.floor_double(x - 1.1D), MathHelper.floor_double(y), MathHelper.floor_double(z));
            Block block5 = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y - 1.1D), MathHelper.floor_double(z));
            Block block6 = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z + 1.1D));
            Block block7 = entity.worldObj.getBlock(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z - 1.1D));

            float tempX1 = x - (float) entity.posX;
            float tempY1 = y - (float) entity.posY;
            float tempZ1 = z - (float) entity.posZ;
            float spawnDist = tempX1 * tempX1 + tempY1 * tempY1 + tempZ1 * tempZ1;

            if (allowedBlock(Block.getIdFromBlock(block1)) && (block2 == Blocks.air || block3 == Blocks.air || block4 == Blocks.air || block5 == Blocks.air || block6 == Blocks.air || block7 == Blocks.air) & spawnDist > 100F)
            {
                tempX = x;
                tempY = y;
                tempZ = z;
                break;
            }
        }
        return (new int[] { tempX, tempY, tempZ });
    }

    public static boolean allowedBlock(int ID)
    {
        return ID != 0 && ID != 7 //bedrock
                && ID != 8 //water
                && ID != 9 //water
                && ID != 10//lava
                && ID != 11//lava
                && ID != 23//dispenser
                && ID != 37//plant
                && ID != 38//plant
                && ID != 50//torch
                && ID != 51//fire
                && ID != 54//chest
                && (ID < 63 || ID > 77) && ID != 95//lockedchest
                && ID != 90//portal
                && ID != 93//redstone
                && ID != 94//redstone
                && ID < 134;//the rest
    }

    /**
     * Method called to tame an entity, it will check that the player has slots
     * for taming, increase the taming count of the player, add the
     * player.getCommandSenderName() as the owner of the entity, and name the entity.
     * 
     * @param entityPlayer
     * @param creature
     * @return
     */
    public static boolean tameWithName(EntityPlayer entityPlayer, IMoCTameable creature) 
    {
        if (entityPlayer == null || creature == null)
        {
            return false;
        }
        
        //if the player interacting with creature is not the owner of the pet
        if (creature.getOwnerName().length() > 0 && !(creature.getOwnerName().equals(entityPlayer.getCommandSenderName())) && MoCreatures.instance.mapData != null)
        {
        	return false;
        }
        
        if (MoCreatures.proxy.enableStrictOwnership) 
        {
            int maxNumberOfPetsAllowed = 0;
            maxNumberOfPetsAllowed = MoCreatures.proxy.maxTamed;
            // only check count for new pets as owners may be changing the name
            if (!MoCreatures.instance.mapData.isExistingPet(entityPlayer.getCommandSenderName(), creature))
            {
                int petCount = MoCTools.numberTamedByPlayer(entityPlayer);
                if (isThisPlayerAnOP(entityPlayer)) 
                {
                    maxNumberOfPetsAllowed = MoCreatures.proxy.maxOPTamed;
                }
                if (petCount >= maxNumberOfPetsAllowed) 
                {
                	
                	if (MoCreatures.isServer())
                    {
                		String message = StatCollector.translateToLocalFormatted("notify.MoCreatures.max_pet_count_reached", new Object[] {maxNumberOfPetsAllowed});
                        entityPlayer.addChatMessage(new ChatComponentTranslation(message));
                    }
                    return false;
                }
            }
        }

        creature.setOwner(entityPlayer.getCommandSenderName()); // ALWAYS SET OWNER. Required for our new pet save system.
        MoCMessageHandler.INSTANCE.sendTo(new MoCMessageNameGUI(((Entity) creature).getEntityId()), (EntityPlayerMP)entityPlayer);
        creature.setTamed(true);
        return true;
    }

    /**
     * returns the number of entities already tamed by the player entityPlayer
     * 
     * @param entityPlayer
     * @return
     */
    public static int numberTamedByPlayer(EntityPlayer entityPlayer)
    {
        if (MoCreatures.instance.mapData != null)
        {
            if (MoCreatures.instance.mapData.getPetData(entityPlayer.getCommandSenderName()) != null)
            {
                return MoCreatures.instance.mapData.getPetData(entityPlayer.getCommandSenderName()).getTamedList().tagCount();
            }
        }
        return 0;
    }

    /**
     * Destroys blocks in front of entity
     * @param entity 
     * @param distance: used to calculate the distance where the target block is located
     * @param strength: int 1 - 3.  Checked against block hardness, also used to calculate how many blocks are recovered
     * @param height:  how many rows of blocks are destroyed in front of the entity
     * @return the count of blocks destroyed
     */
    public static int destroyBlocksInFront(Entity entity, double distance, int strength, int height)
    {
        if (strength == 0) { return 0; }
        int count = 0;
        float strengthF = strength;
        
        double newPosX = entity.posX - (distance * Math.cos((MoCTools.realAngle(entity.rotationYaw - 90F)) / 57.29578F));
        double newPosZ = entity.posZ - (distance * Math.sin((MoCTools.realAngle(entity.rotationYaw - 90F)) / 57.29578F));
        double newPosY = entity.posY;
        
        int x = MathHelper.floor_double(newPosX);
        int y = MathHelper.floor_double(newPosY);
        int z = MathHelper.floor_double(newPosZ);

        for (int index = 0; index < height; index++)
        {
            Block block = entity.worldObj.getBlock(x, y + index, z);
            if (block != Blocks.air && block != Blocks.bedrock)
            {
                int metadata = entity.worldObj.getBlockMetadata(x, y + index, z);
                BlockEvent.BreakEvent event = null;
                if (!entity.worldObj.isRemote)
                {
                    event = new BlockEvent.BreakEvent(x, y + index, z, entity.worldObj, block, metadata, FakePlayerFactory.get((WorldServer)entity.worldObj, MoCreatures.MOC_FAKE_PLAYER));
                }
                if (event != null && !event.isCanceled())
                {
                    block.dropBlockAsItemWithChance(entity.worldObj, x, y + index, z, entity.worldObj.getBlockMetadata(x, y + index, z), 0.20F * strengthF, 1);
                    entity.worldObj.setBlock(x, y + index, z, Blocks.air, 0, 3);//MC 1.5
                    if (entity.worldObj.rand.nextInt(3) == 0)
                    {
                        MoCTools.playCustomSound(entity, "golemwalk", entity.worldObj);
                        count++; //only counts recovered blocks
                    }
                }
            }
        }
        return count;
    }

    public static void dropInventory(Entity entity, MoCAnimalChest animalchest)
    {
        if (animalchest == null || !(MoCreatures.isServer()) ) return;
        
        int entityPosX = MathHelper.floor_double(entity.posX);
        int entityPosY = MathHelper.floor_double(entity.boundingBox.minY);
        int entityPosZ = MathHelper.floor_double(entity.posZ);
        
        for (int l = 0; l < animalchest.getSizeInventory(); l++)
        {
            ItemStack itemStack = animalchest.getStackInSlot(l);
            if (itemStack == null)
            {
                continue;
            }
            float xOffset = (entity.worldObj.rand.nextFloat() * 0.8F) + 0.1F;
            float yOffset = (entity.worldObj.rand.nextFloat() * 0.8F) + 0.1F;
            float zOffset = (entity.worldObj.rand.nextFloat() * 0.8F) + 0.1F;
            
            float motionMultiplier = 0.05F;

            EntityItem entityItem = new EntityItem(entity.worldObj,  entityPosX + xOffset, entityPosY + yOffset, entityPosZ + zOffset, itemStack);
            
            entityItem.motionX = ((float) entity.worldObj.rand.nextGaussian() * motionMultiplier);
            entityItem.motionY = (((float) entity.worldObj.rand.nextGaussian() * motionMultiplier) + 0.2F);
            entityItem.motionZ = ((float) entity.worldObj.rand.nextGaussian() * motionMultiplier);
            
            entity.worldObj.spawnEntityInWorld(entityItem);
            
            animalchest.setInventorySlotContents(l, null);
        }
    }

    /**
     * Drops an amulet with the stored information of the entity passed
     * @param entity
     */
    public static void dropHorseAmuletWithNewPetInformation(MoCEntityTameableAnimal entity)
    {
        if (MoCreatures.isServer())
        {
            ItemStack stack = getProperAmulet(entity);
            if (stack == null) 
            {
                return;
            }
            if( stack.stackTagCompound == null )
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound nbtTagCompound = stack.stackTagCompound;

            try
            {
                nbtTagCompound.setInteger("SpawnClass", 21);
                nbtTagCompound.setFloat("MaxHealth", ((float) ((EntityLiving) entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()));
                nbtTagCompound.setFloat("Health", entity.getHealth());
                nbtTagCompound.setInteger("Age", entity.getMoCAge());
                nbtTagCompound.setString("Name", entity.getName());
                nbtTagCompound.setBoolean("Rideable", entity.getIsRideable());
                nbtTagCompound.setByte("Armor", entity.getArmorType());
                nbtTagCompound.setInteger("CreatureType", entity.getType());
                nbtTagCompound.setBoolean("Adult", entity.getIsAdult());          
                nbtTagCompound.setString("OwnerName", entity.getOwnerName());
                nbtTagCompound.setInteger("PetId", entity.getOwnerPetId());
            }
            catch (Exception e)
            {
            }
            
            EntityPlayer ownerOfCreature = entity.worldObj.getPlayerEntityByName(entity.getOwnerName());

            if (ownerOfCreature != null && ownerOfCreature.inventory.getFirstEmptyStack() != -1) // don't attempt to set if player inventory is full
            {
                ownerOfCreature.inventory.addItemStackToInventory(stack);
            }
            else
            {
                EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, stack);
                entityItem.delayBeforeCanPickup = 20;
                entity.worldObj.spawnEntityInWorld(entityItem);
            }
        }
    }

    /**
     * Drops a new amulet/fishnet with the stored information of the entity
     */
    public static void dropAmuletWithNewPetInformation(IMoCTameable entity, int amuletType)
    {
        if (MoCreatures.isServer())
        {
            ItemStack stack = new ItemStack(MoCreatures.fishNet, 1, 1); 
            if (amuletType == 2)
            {
               stack = new ItemStack(MoCreatures.petAmulet, 1, 1);
            }

            if( stack.stackTagCompound == null )
            {
                stack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound nbtTagCompound = stack.stackTagCompound;

            try
            {
                String petClass = entity.getClass().getSimpleName().replace("MoCEntity", "");
                if (petClass.equalsIgnoreCase("Horse"))
                {
                    petClass = "Horse";
                }
                else if (petClass.equalsIgnoreCase("Komodo"))
                {
                    petClass = "KomodoDragon";
                }
                nbtTagCompound.setString("SpawnClass", petClass);
                nbtTagCompound.setFloat("MaxHealth", ((float) ((EntityLiving) entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()));
                nbtTagCompound.setFloat("Health", ((EntityLiving) entity).getHealth());
                nbtTagCompound.setInteger("Age", entity.getMoCAge());
                nbtTagCompound.setString("Name", entity.getName());
                nbtTagCompound.setInteger("CreatureType", entity.getType());
                nbtTagCompound.setString("OwnerName", entity.getOwnerName());
                nbtTagCompound.setBoolean("Adult", entity.getIsAdult());
                nbtTagCompound.setInteger("PetId", entity.getOwnerPetId());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            EntityPlayer epOwner = ((EntityLivingBase)entity).worldObj.getPlayerEntityByName(entity.getOwnerName());
            if (epOwner != null)
            {
                epOwner.inventory.addItemStackToInventory(stack);
            }
            else
            {
                EntityItem entityItem = new EntityItem(((EntityLivingBase)entity).worldObj, ((EntityLivingBase)entity).posX, ((EntityLivingBase)entity).posY, ((EntityLivingBase)entity).posZ, stack);
                entityItem.delayBeforeCanPickup = 20;
                ((EntityLivingBase)entity).worldObj.spawnEntityInWorld(entityItem);
            }
        }
    }

    /**
     * Returns the right full amulet based on the MoCEntityAnimal passed
     * @param entity
     * @return
     */
    public static ItemStack getProperAmulet(MoCEntityAnimal entity)
    {
        if (entity instanceof MoCEntityHorse)
        {
            if (entity.getType() == 26 || entity.getType() == 27 || entity.getType() == 28)
            {
                return new ItemStack(MoCreatures.amuletBoneFull, 1, entity.getType());
            }
            if (entity.getType() > 47 && entity.getType() < 60)
            {
                return new ItemStack(MoCreatures.amuletFairyFull, 1, entity.getType());
            }
            if (entity.getType() == 39 || entity.getType() == 40)
            {
                return new ItemStack(MoCreatures.amuletPegasusFull, 1, entity.getType());
            }
            if (entity.getType() == 21 || entity.getType() == 22)
            {
               return new ItemStack(MoCreatures.amuletGhostFull, 1, entity.getType());
            }
        }
        return null;
    }
    
    /**
     * Returns the right full empty based on the MoCEntityAnimal passed. Used when the amulet empties its contents
     * @param entity
     * @return
     */
    public static ItemStack getProperEmptyAmulet(MoCEntityAnimal entity)
    {
        if (entity instanceof MoCEntityHorse)
        {
            if (entity.getType() == 26 || entity.getType() == 27 || entity.getType() == 28)
            {
                return new ItemStack(MoCreatures.amuletBone, 1, entity.getType());
            }
            if (entity.getType() > 49 && entity.getType() < 60)
            {
                return new ItemStack(MoCreatures.amuletFairy, 1, entity.getType());
            }
            if (entity.getType() == 39 || entity.getType() == 40)
            {
                return new ItemStack(MoCreatures.amuletPegasus, 1, entity.getType());
            }
            if (entity.getType() == 21 || entity.getType() == 22)
            {
               return new ItemStack(MoCreatures.amuletGhost, 1, entity.getType());
            }
        }
        return null;
    }
    
    public static int countPlayersInDimension(WorldServer worldObj, int dimension)
    {
        int playersInDimension = 0;
        for (int j = 0; j < worldObj.playerEntities.size(); ++j)
        {
            EntityPlayerMP entityPlayermp = (EntityPlayerMP)worldObj.playerEntities.get(j);

            if (entityPlayermp.dimension == dimension)
            {
                playersInDimension++;
            }
        }
        return playersInDimension;
    }
    
    public static boolean isThisPlayerAnOP(EntityPlayer player)
    {
        if (!MoCreatures.isServer()) 
        {    
            return false;
        }

        return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152596_g(player.getGameProfile());
    }

    public static void spawnMaggots(World worldObj, Entity entity)
    {
         if (MoCreatures.isServer())
         {
             int amountOfMaggotsToSpawn = 1 + worldObj.rand.nextInt(4);
             for (int index = 0; index < amountOfMaggotsToSpawn; ++index)
             {
                 float maggotPosX = (index % 2 - 0.5F) * 1 / 4.0F;
                 float maggotPosZ = (index / 2 - 0.5F) * 1 / 4.0F;
                 
                 MoCEntityMaggot maggot = new MoCEntityMaggot(worldObj);
                 maggot.setLocationAndAngles(entity.posX + maggotPosX, entity.posY + 0.5D, entity.posZ + maggotPosZ, worldObj.rand.nextFloat() * 360.0F, 0.0F);
                 
                 worldObj.spawnEntityInWorld(maggot);
             }
         }
    }

    public static void getPathToEntity(EntityCreature creatureToMove, Entity entityTarget, float f)
    {
        PathEntity pathEntity = creatureToMove.worldObj.getPathEntityToEntity(creatureToMove, entityTarget, 16F, true, false, false, true);
        if (pathEntity != null && f < 12F)
        {
            creatureToMove.setPathToEntity(pathEntity);
        }
    }

    /**
     * Makes runningEntity run away from scaryEntity
     * 
     * @param runningEntity
     * @param scaryEntity
     */
    public static void runAway(EntityCreature runningEntity, Entity scaryEntity)
    {
        double xDistanceBetweenEntities = runningEntity.posX - scaryEntity.posX;
        double zDistanceBetweenEntities = runningEntity.posZ - scaryEntity.posZ;
        
        double angleInRadiansToNewLocation = Math.atan2(xDistanceBetweenEntities, zDistanceBetweenEntities);
        angleInRadiansToNewLocation += (runningEntity.worldObj.rand.nextFloat() - runningEntity.worldObj.rand.nextFloat()) * 0.75D;
        
        double tempNewPosX = runningEntity.posX + (Math.sin(angleInRadiansToNewLocation) * 8D);
        double tempNewPosZ = runningEntity.posZ + (Math.cos(angleInRadiansToNewLocation) * 8D);
        
        int temp1NewPosX = MathHelper.floor_double(tempNewPosX);
        int temp1NewPosY = MathHelper.floor_double(runningEntity.boundingBox.minY);
        int temp1NewPosZ = MathHelper.floor_double(tempNewPosZ);
        
        int index = 0;
        
        do
        {
            if (index >= 16)
            {
                break;
            }
            
            int newPosX = (temp1NewPosX + runningEntity.worldObj.rand.nextInt(4)) - runningEntity.worldObj.rand.nextInt(4);
            int newPosY = (temp1NewPosY + runningEntity.worldObj.rand.nextInt(3)) - runningEntity.worldObj.rand.nextInt(3);
            int newPosZ = (temp1NewPosZ + runningEntity.worldObj.rand.nextInt(4)) - runningEntity.worldObj.rand.nextInt(4);
            
            if (
            		(newPosY > 4)
            		&& ((runningEntity.worldObj.getBlock(newPosX, newPosY, newPosZ) == Blocks.air) || (runningEntity.worldObj.getBlock(newPosX, newPosY, newPosZ) == Blocks.snow))
            		&& (runningEntity.worldObj.getBlock(newPosX, newPosY - 1, newPosZ) != Blocks.air)
            	)
            {
                PathEntity pathEntity = runningEntity.worldObj.getEntityPathToXYZ(runningEntity, newPosX, newPosY, newPosZ, 16F, true, false, false, true);
                runningEntity.setPathToEntity(pathEntity);
                break;
            }
            index++;
        } while (true);
    }
    

    /**
     * Finds a near vulnerable player and poisons it if the player is in the water and not riding anything
     * @param poisoner
     * @param needsToBeInWater: the target needs to be in water for poison to be successful?
     * @return true if was able to poison the player
     */
    public static boolean findClosestPlayerAndPoisonThem(Entity poisoner, boolean needsToBeInWater)
    {
        EntityPlayer entityPlayerTarget = poisoner.worldObj.getClosestVulnerablePlayerToEntity(poisoner, 2D);
        if (entityPlayerTarget != null && ( (needsToBeInWater && entityPlayerTarget.isInWater()) || !needsToBeInWater) && poisoner.getDistanceToEntity(entityPlayerTarget) < 2.0F)
        {
            if (entityPlayerTarget.ridingEntity != null && entityPlayerTarget.ridingEntity instanceof EntityBoat)
            {
                //don't poison players on boats
            }
            else
            {
                entityPlayerTarget.addPotionEffect(new PotionEffect(Potion.poison.id, 120, 0));
                return true;
            }
        }
        return false;
    }

    public static boolean isTamed(Entity entity)
    {
        if (entity instanceof EntityTameable)
        {
            if (((EntityTameable)entity).isTamed())
            {
                return true;
            }
        }
        NBTTagCompound nbt = new NBTTagCompound();
        entity.writeToNBT(nbt);
        if (nbt != null)
        {
            if (nbt.hasKey("Owner") && !nbt.getString("Owner").equals(""))
            {
                return true; // ignore
            }
            if (nbt.hasKey("Tamed") && nbt.getBoolean("Tamed") == true)
            {
                return true; // ignore
            }
        }
        return false;
    }
}