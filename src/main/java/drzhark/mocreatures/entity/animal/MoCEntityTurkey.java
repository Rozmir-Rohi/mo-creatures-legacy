package drzhark.mocreatures.entity.animal;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityTurkey extends MoCEntityTameableAnimal {

    public MoCEntityTurkey(World world)
    {
        super(world);
        setSize(0.8F, 1.0F);
        texture = "turkey.png";
        checkSpawningBiome();
    }

    protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }
    
    @Override
    public boolean checkSpawningBiome()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);
        String biome_name = MoCTools.BiomeName(worldObj, i, j, k);

        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SAVANNA)
        		|| BiomeDictionary.isBiomeOfType(currentbiome, Type.SANDY))
        {
        	return false; //do not spawn in savannah or desert biomes (this is mainly fix spawns when Biomes O' Plenty is used). The code for this continues is MoCEventHooks.java
        }
        
        return true;
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:turkeyhurt";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:turkeyhurt";
    }

    @Override
    protected String getLivingSound()
    {
        return "mocreatures:turkey";
    }

    @Override
    protected Item getDropItem()
    {
        boolean flag = (rand.nextInt(2) == 0);
        if (flag) { return MoCreatures.rawTurkey; }
        return Items.feather;
    }

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {
        if (super.interact(entityplayer)) { return false; }

        ItemStack itemstack = entityplayer.inventory.getCurrentItem();

        if (MoCreatures.isServer() && !getIsTamed() && (itemstack != null) && (isItemstackFoodItem(itemstack)))
        {
            MoCTools.tameWithName(entityplayer, this);
        }

        return true;
    }

    @Override
    public boolean isMyHealFood(ItemStack itemstack)
    {
        return itemstack != null && isItemstackFoodItem(itemstack);
    }
    
    private boolean isItemstackFoodItem(ItemStack itemstack)
    {
    	Item item = itemstack.getItem();
    	
    	if (
    			item instanceof ItemSeeds
    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
    			|| OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "listAllseed" //BOP seeds or GregTech6 seeds or Palms Harvest seeds
    			|| OreDictionary.getOreName(OreDictionary.getOreID(itemstack)) == "foodRaisins" //GregTech6 raisins or Palm's Harvest raisins
    		) {return true;}
    	
    	return false;
    }

    @Override
    public int nameYOffset()
    {
        return -50;
    }

    @Override
    public double roperYOffset()
    {
        return 0.8D;
    }
    
    @Override
    public int getTalkInterval()
    {
        return 400;
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 2;
    }
}