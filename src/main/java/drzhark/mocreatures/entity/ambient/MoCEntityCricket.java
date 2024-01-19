package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;

public class MoCEntityCricket extends MoCEntityInsect

{
    public MoCEntityCricket(World world)
    {
        super(world);
        texture = "cricketa.png";
    }

    private int jumpCounter;
    private int soundCounter;

    @Override
    protected void updateEntityActionState()
    {
        //if (onGround && ((motionX > 0.05D) || (motionZ > 0.05D) || (motionX < -0.05D) || (motionZ < -0.05D)))
        if (jumpCounter == 0 && onGround && ((motionX > 0.05D) || (motionZ > 0.05D) || (motionX < -0.05D) || (motionZ < -0.05D)))
        {
            motionY = 0.45D;
            motionX *= 5D;
            motionZ *= 5D;
            jumpCounter = 1;
        }
        super.updateEntityActionState();
    }

    @Override
    public void selectType()
    {
    	checkSpawningBiome(); //apply type from the biome it spawns in
    	
        if (getType() == 0) // if the type is still 0, make it a random type
        {
            int i = rand.nextInt(100);
            if (i <= 50)
            {
                setType(1);
            }
            else
            {
                setType(2);
            }
        }
    }
    
    @Override
    public boolean checkSpawningBiome()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);
      
        //sets the cricket to have a more yellow shade to fit in with the grass
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SAVANNA)
        		|| BiomeDictionary.isBiomeOfType(currentbiome, Type.SANDY)
        	) 
        {
        	setType(2); //yellow shade cricket
            return true;
        }
        
        else
        {
        	setType(1); //green shade cricket
        	return true;
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        if (getType() == 1)
        {
            return MoCreatures.proxy.getTexture("cricketa.png");
        }
        else
        {
            return MoCreatures.proxy.getTexture("cricketb.png");
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (getIsFlying() && rand.nextInt(50) == 0)
            {
                setIsFlying(false);
            }

            if (getIsFlying() || !this.onGround)
            {
                EntityPlayer ep = worldObj.getClosestPlayerToEntity(this, 5D);
                if (ep != null && --soundCounter == -1)
                {
                    MoCTools.playCustomSound(this, "cricketfly", this.worldObj);
                    soundCounter = 10;
                }
            }
            else if (!DimensionManager.getWorld(0).isDaytime())
            {
                EntityPlayer ep = worldObj.getClosestPlayerToEntity(this, 12D);
                if (ep != null && --soundCounter == -1)
                {
                    MoCTools.playCustomSound(this, "cricket", this.worldObj);
                    soundCounter = 20;
                }
            }

            if (jumpCounter > 0 && ++jumpCounter > 30)
            {
                jumpCounter = 0;
            }
        }
    }

    @Override
    protected float getFlyingSpeed()
    {
        return 0.4F;
    }

    @Override
    protected float getWalkingSpeed()
    {
        return 0.5F;
    }
}