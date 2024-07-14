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

    private int lateralMovCounter;
    
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
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);

        int typeChance = rand.nextInt(100);
        
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.OCEAN))
        {
        	if (typeChance <= 30)
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
            return MoCreatures.proxy.getTexture("smallfish_angler.png");
        case 5:
            return MoCreatures.proxy.getTexture("smallfish_manderin.png");
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
                EntityLivingBase entityLiving = getScaryEntity(8D);
                if (entityLiving != null && entityLiving.isInsideOfMaterial(Material.water))
                {
                   MoCTools.runAway(this, entityLiving);
                }
            }
            if (getIsTamed() && rand.nextInt(100) == 0 && getHealth() < getMaxHealth())
            {
               heal(1);
            }
        }
        if (!isInsideOfMaterial(Material.water))
        {
            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
            rotationPitch = prevRotationPitch;
        }
    }
    
    protected boolean isItemPlantMegaPackFishEdibleFreshWaterPlant(Item item)
    {
    	return (
    				(item.itemRegistry).getNameForObject(item).equals("plantmegapack:riverAmazonSword")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:riverCanadianWaterweed")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:riverCoonsTail")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:waterCryptWendtii")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:waterDwarfHairGrass")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:riverEelgrass")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:riverWaterWisteria")
    			);
    }
    
    @Override
    protected boolean isMyHealFood(ItemStack itemstack)
    {
    	if (itemstack == null) {return false;}
    	
    	Item item = itemstack.getItem();
    	
    	if (
    			item instanceof ItemSeeds
    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:kelp")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:coral1") && itemstack.getItemDamage() == 11 //BOP kelp
    			|| (item.itemRegistry).getNameForObject(item).equals("harvestcraft:seaweedItem")
    			|| isItemPlantMegaPackFishEdibleFreshWaterPlant(item)
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
        if (!isInsideOfMaterial(Material.water))
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
        if (!isInsideOfMaterial(Material.water))
        {
            return 90;
        }

        if (rand.nextInt(3) == 0)
        {
            if (++lateralMovCounter > 40) lateralMovCounter = 0;
        }

        int lateralOffset = 0;
        if (lateralMovCounter < 21) 
        {
            lateralOffset = lateralMovCounter;
        }
        else
        {
            lateralOffset = -lateralMovCounter + 40;
        }
         return 80 + lateralOffset;
    }

    @Override
    public int rollRotationOffset()
    {
        if (!isInsideOfMaterial(Material.water))
        {
            return -90;
        }
        return 0;
    }

    @Override
    public boolean shouldRenderName()
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
        if (!isInsideOfMaterial(Material.water))
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