package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityTurkey extends MoCEntityTameableAnimal {

    public MoCEntityTurkey(World world)
    {
        super(world);
        setSize(0.8F, 1.0F);
        texture = "turkey.png";
        checkSpawningBiome();
    }

    @Override
	protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }
    
    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        String biomeName = MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (
        		BiomeDictionary.isBiomeOfType(currentBiome, Type.SAVANNA)
        		|| BiomeDictionary.isBiomeOfType(currentBiome, Type.SANDY)
        	)
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
        if (flag) { return MoCreatures.turkeyRaw; }
        return Items.feather;
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }

        ItemStack itemStack = entityPlayer.getHeldItem();

        if (
        		MoCreatures.isServer()
        		&& !getIsTamed()
        		&& (itemStack != null)
        		&& isMyHealFood(itemStack)
        	)
        {
            MoCTools.tameWithName(entityPlayer, this);
        }

        return true;
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
							oreDictionaryNameArray.contains("listAllseed") //BOP seeds or Palm's Harvest Seeds
							|| oreDictionaryNameArray.contains("foodRaisins") //GregTech6 seeds/raisins or Palm's Harvest raisins
						)
	    		);
	    }
	    else {return false;}
    }
   
    @Override
    public boolean getCanSpawnHere()
    {
        if (checkSpawningBiome()) //don't let Turkeys spawn in biomes that they are not supposed to spawn in (mainly savannas)
        {
        	return super.getCanSpawnHere();
        }
       
        else {return false;}
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