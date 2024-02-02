package drzhark.mocreatures.entity.aquatic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityMediumFish extends MoCEntityTameableAquatic{

    public static final String fishNames[] = { "Salmon", "Cod", "Bass", "Red Salmon"};

    private int latMovCounter;
    
    public MoCEntityMediumFish(World world)
    {
        super(world);
        setSize(0.6F, 0.3F);
        setMoCAge(30 + rand.nextInt(70));
    }

    protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0D);
    }

    @Override
    public void selectType()
    {
    	checkSpawningBiome(); //try to apply the type based on the biome that it spawns in
    	
        if (getType() == 0) //if the type is still 0, make it a random type
        {
            setType(rand.nextInt(3) + 1);
        }
    }
    
    @Override
    public boolean checkSpawningBiome()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);
        
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SWAMP))
        {
            setType(3); //bass
            return true;
        }

        int type_chance = rand.nextInt(100);
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.RIVER))
        {
            if (type_chance <= 40)
    			{ //red salmon
            		setType(4);
            		setMoCAge(110); //sets as adult on spawn since salmon only go into freshwater in real life when they are adults and ready to breed
    			} 
            
            else {setType(3);} //bass
            
            return true;
        }
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.OCEAN))
        {
        	if (type_chance <= 50)
			{setType(1);} //blue salmon
        
        	else {setType(2);} //cod
        
        	return true;
        }
        
        return true;
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("mediumfish_salmon.png");
        case 2:
            return MoCreatures.proxy.getTexture("mediumfish_cod.png");
        case 3:
            return MoCreatures.proxy.getTexture("mediumfish_bass.png");
        case 4:
            return MoCreatures.proxy.getTexture("mediumfish_salmon1.png");
        default:
            return MoCreatures.proxy.getTexture("mediumfish_salmon.png");
        }
        
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int i = rand.nextInt(100);
        if (i < 70)
        {
        	if (this.getType() == 4) //red salmon
        	{
        		entityDropItem(new ItemStack(Items.fish, 1, 1), 0.0F); //drops salmon
        	}
        	
        	else
            {
        		entityDropItem(new ItemStack(Items.fish, 1, 0), 0.0F);
            }
        }
        else
        {
        	if (getIsAdult() && getType() != 1) //not a blue salmon - because salmon only lay eggs when they go to fresh water and become red
        	{
	            int j = rand.nextInt(2);
	            for (int k = 0; k < j; k++)
	            {
	            	if (getType() == 4) //red salmon
	            	{
	            		entityDropItem(new ItemStack(MoCreatures.mocegg, 1, 1 + 69), 0.0F); //drop salmon egg
	            	}
	            	
	            	else
	            	{
	            		entityDropItem(new ItemStack(MoCreatures.mocegg, 1, getType() + 69), 0.0F);
	            	}
	            }
        	}
        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if ((MoCreatures.isServer()) && !getIsAdult() && (rand.nextInt(500) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 110)
            {
                setAdult(true);
            }
            
            if (rand.nextInt(5) == 0 && !getIsTamed())
            {
                EntityLivingBase entityliving = getScaryEntity(8D);
                if (entityliving != null && entityliving.isInsideOfMaterial(Material.water))
                {
                   MoCTools.runLikeHell(this, entityliving);
                }
            }

            if (getIsTamed() && rand.nextInt(100) == 0 && getHealth() < getMaxHealth())
            {
                heal(1);
            }
        }
        if (getType() == 1 && getIsTamed() && getIsAdult()) //turns tamed blue salmon into red salmon when they become adults
        {
        	setType(4);
        }
        
        if (!this.isInsideOfMaterial(Material.water))
        {
            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
            rotationPitch = prevRotationPitch;
        }
    }

    @Override
    public float getSizeFactor() 
    {   
        return (float)getMoCAge() * 0.01F;
    }

    @Override
    public float getAdjustedYOffset()
    {
        if (!this.isInsideOfMaterial(Material.water))
        {
            return -0.1F;
        }
        return 0.7F;
    }

    @Override
    protected boolean isFisheable()
    {
        return !getIsTamed();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int yawRotationOffset()
    {
        if (!this.isInsideOfMaterial(Material.water))
        {
            return 90;
        }
        
        
        if (rand.nextInt(3) == 0)
        {
            if (++latMovCounter > 40) latMovCounter = 0;
        }
        
        int latOffset = 0;
        if (latMovCounter < 21) 
        {
            latOffset = latMovCounter;
        }
        else
        {
            latOffset = -latMovCounter + 40;
        }
         return 80 + latOffset;
    }


    @Override
    public int rollRotationOffset()
    {
        if (!this.isInsideOfMaterial(Material.water))
        {
            return -90;
        }
        return 0;
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null);
    }

    @Override
    public int nameYOffset()
    {
        return -30;
    }

    @Override
    public float getAdjustedZOffset()
    {
        return 0F;
    }

    @Override
    public float getAdjustedXOffset()
    {
        if (!this.isInsideOfMaterial(Material.water))
        {
            return -0.8F;
        }
        return 0F;
    }

    @Override
    protected boolean canBeTrappedInNet() 
    {
        return true;
    }
}