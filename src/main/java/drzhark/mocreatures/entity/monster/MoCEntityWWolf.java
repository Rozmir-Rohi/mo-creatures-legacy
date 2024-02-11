package drzhark.mocreatures.entity.monster;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityAmbient;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.animal.MoCEntityBear;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
        	if (MoCTools.isPlayerInWerewolfForm(entityPlayer)) //don't hunt player if in werewolf form
        	{	
        		return null;
        	}
        	else {return entityPlayer;}
        }
        
        else if (rand.nextInt(80) == 0)
        {
            EntityLivingBase entityLiving = getClosestEntityLiving(this, 10D);
            if (entityLiving != null && !(entityLiving instanceof EntityPlayer) && canEntityBeSeen(entityLiving))
            {
            	return entityLiving;
            }
        }
        
        return null;
    }
    
    @Override
    public boolean shouldEntityBeIgnored(Entity entity)
    {
        return 
        	(
        		(!(entity instanceof EntityLiving)) 
                || entity instanceof EntityMob
                || entity instanceof MoCEntityEgg
                || (entity instanceof EntityPlayer) 
                || (entity instanceof MoCEntityKittyBed) || (entity instanceof MoCEntityLitterBox) 
                || (entity instanceof MoCEntityAnimal && ((MoCEntityAnimal) entity).getIsTamed()) 
                || ((entity instanceof EntityWolf) && !(MoCreatures.proxy.attackWolves)) 
                || ((entity instanceof MoCEntityHorse) && !(MoCreatures.proxy.attackHorses))
                || (entity instanceof MoCEntityAnimal || entity instanceof MoCEntityAmbient)
                || entity instanceof MoCEntityBigCat
        		|| entity instanceof MoCEntityBear
        	);
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return checkSpawningBiome() && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && super.getCanSpawnHere();
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