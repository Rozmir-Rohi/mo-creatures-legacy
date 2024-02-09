package drzhark.mocreatures.entity.monster;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.animal.MoCEntityBear;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityWWolf extends MoCEntityMob {

    public int mouthCounter;
    public int tailCounter;

    public MoCEntityWWolf(World world)
    {
        super(world);
        setSize(0.9F, 1.3F);
    }
    
    @Override
    public boolean isPredator()
    {
    	return true;
    }

    @Override
    protected double getAttackStrength() 
    {
        return 3D;
    }
    
    @Override
    public void selectType()
    {
    	checkSpawningBiome(); //try to apply type from the biome it spawns in
    	
        if (getType() == 0) //if type is still zero, make it a random wolf
        {
            setType(rand.nextInt(4)+1);
        }
    }
    
    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase biome = MoCTools.Biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (BiomeDictionary.isBiomeOfType(biome, Type.SNOWY))
        {
            setType(3); //snow wolf
        }
        
        return true;
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("wolfblack.png");
        case 2:
            return MoCreatures.proxy.getTexture("wolfwild.png");
        case 3:
            return MoCreatures.proxy.getTexture("wolftimber.png"); //snow wolf
        case 4:
            return MoCreatures.proxy.getTexture("wolfdark.png");
        case 5:
            return MoCreatures.proxy.getTexture("wolfbright.png");

        default:
            return MoCreatures.proxy.getTexture("wolfwild.png");
        }
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            openMouth();
            attackTime = 20;
            attackEntityAsMob(entity);
        }
    }

    private void openMouth()
    {
        mouthCounter = 1;
    }

    private void moveTail()
    {
        tailCounter = 1;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (rand.nextInt(200) == 0)
        {
            moveTail();
        }

        if (mouthCounter > 0 && ++mouthCounter > 15)
        {
            mouthCounter = 0;
        }

        if (tailCounter > 0 && ++tailCounter > 8)
        {
            tailCounter = 0;
        }
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        EntityPlayer entityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16D);
        
        if (entityPlayer != null)
        {
        	return entityPlayer;
        }
        
        else if (rand.nextInt(80) == 0)
        {
            EntityLivingBase entityLiving = getClosestTarget(this, 10D);
            return entityLiving;
        }
        
        else
        {
            return null;
        }
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return checkSpawningBiome() && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && super.getCanSpawnHere();
    }

    //TODO move this
    public EntityLivingBase getClosestTarget(Entity entity, double distance)
    {
        double currentMinimumDistance = -1D;
        EntityLivingBase entityLiving = null;
        
        List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(distance, distance, distance));
        
        int iterationLength = entitiesNearbyList.size();
        
        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	            
	            if (//don't hunt the following entities below
	            		!(entityNearby instanceof EntityLivingBase)
	            		|| entityNearby == entity
	            		|| entityNearby == entity.riddenByEntity
	            		|| entityNearby == entity.ridingEntity
	            		|| entityNearby instanceof EntityPlayer
	            		|| entityNearby instanceof EntityMob
	            		|| entityNearby instanceof MoCEntityBigCat
	            		|| entityNearby instanceof MoCEntityBear
	            		|| entityNearby instanceof EntityCow
	            		|| (
	            				(entityNearby instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)
	            			)
	            		|| (
	            				(entityNearby instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses)
	            			)
	            	)
	            {
	                continue;
	            }
	            
	            double overallDistanceSquared = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
	            
	            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)) && ((EntityLivingBase) entityNearby).canEntityBeSeen(entity))
	            {
	                currentMinimumDistance = overallDistanceSquared;
	                entityLiving = (EntityLivingBase) entityNearby;
	            }
	        }
        }

        return entityLiving;
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:wolfdeath";
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.fur;
    }

    @Override
    protected String getHurtSound()
    {
        openMouth();
        return "mocreatures:wolfhurt";
    }

    @Override
    protected String getLivingSound()
    {
        openMouth();
        return "mocreatures:wolfgrunt";
    }
}