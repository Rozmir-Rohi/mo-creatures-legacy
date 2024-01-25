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
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntitySmallFish extends MoCEntityTameableAquatic{

    public static final String fishNames[] = { "Anchovy", "Angelfish", "Goldfish", "Anglerfish", "Mandarin"};

    private int latMovCounter;
    
    public MoCEntitySmallFish(World world)
    {
        super(world);
        setSize(0.3F, 0.3F);
        setMoCAge(30 + rand.nextInt(70));
        
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    }

    @Override
    public void selectType()
    {
    	checkSpawningBiome(); //try to apply the type based on the biome that it spawns in
    	
        if (getType() == 0) //if type is still 0 apply random type from fresh water fish
        {
            setType(rand.nextInt(3)+1); 
        }
        
    }
    
    @Override
    public boolean checkSpawningBiome()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);

        int type_chance = rand.nextInt(100);
        
        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.OCEAN))
        {
        	if (type_chance <= 30)
			{setType(4);} //Angler
        
        	else {setType(5);} //mandarin
        
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
            return MoCreatures.proxy.getTexture("smallfish_anchovy.png");
        case 2:
            return MoCreatures.proxy.getTexture("smallfish_angelfish.png");
        case 3:
            return MoCreatures.proxy.getTexture("smallfish_goldfish.png");
        case 4:
            return MoCreatures.proxy.getTexture("smallfish_anglerfish.png");
        case 5:
            return MoCreatures.proxy.getTexture("smallfish_mandarin.png");
        default:
        	return MoCreatures.proxy.getTexture("smallfish_anchovy.png");
        }
    }

    @Override
    protected boolean canBeTrappedInNet() 
    {
        return true;
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int i = rand.nextInt(100);
        if (i < 70)
        {
            entityDropItem(new ItemStack(Items.fish, 1, 0), 0.0F);
        }
        else
        {
            int j = rand.nextInt(2);
            for (int k = 0; k < j; k++)
            {
                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, getType() + 79), 0.0F); 
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
            if (getMoCAge() >= 100)
            {
                setAdult(true);
            }

            if (!isNotScared() && rand.nextInt(5) == 0 && !getIsTamed())
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
        if (!this.isInsideOfMaterial(Material.water))
        {
            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
            rotationPitch = prevRotationPitch;
        }
    }
    
    @Override
    protected boolean isMyHealFood(ItemStack itemstack)
    {
    	Item item = itemstack.getItem();
    	
    	if (
    			item instanceof ItemSeeds
    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:coral1") && itemstack.getItemDamage() == 11 //BOP kelp
    			|| (item.itemRegistry).getNameForObject(item).equals("harvestcraft:seaweedItem")
    			|| MoCreatures.isGregTech6Loaded &&
    				(
    					OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodRaisins"
    				)
    		) {return true;}
    	
        return false;
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
        return 0.3F;
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
        return -25;
    }

    @Override
    public float getAdjustedXOffset()
    {
        if (!this.isInsideOfMaterial(Material.water))
        {
            return -0.6F;
        }
        return 0F;
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.3F;
    }
}