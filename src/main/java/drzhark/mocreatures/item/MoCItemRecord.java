package drzhark.mocreatures.item;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCItemRecord extends ItemRecord
{
    public static ResourceLocation RECORD_SHUFFLE_RESOURCE = new ResourceLocation("mocreatures", "shuffling");

    public MoCItemRecord(String string)
    {
        super(string);
        this.setCreativeTab(MoCreatures.tabMoC);
        this.setUnlocalizedName(string);
        GameRegistry.registerItem(this, string);
    }
    
    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityPlayer, World world, int xCoordinate, int yCoordinate, int zCoordinate, int l, float f1, float f2, float f3)
    {
    	super.onItemUse(itemstack, entityPlayer, world, xCoordinate, yCoordinate, zCoordinate, l, f1, f2, f3);
    	
    	/**
         * Makes zebras shuffle and normal horses nod their heads to "Party Rock Anthem" by LMFAO!
         */
    	if (world.getBlock(xCoordinate, yCoordinate, zCoordinate) == Blocks.jukebox && world.getBlockMetadata(xCoordinate, yCoordinate, zCoordinate) == 0)
    	{

    	    List entitiesNearbyList = entityPlayer.worldObj.getEntitiesWithinAABBExcludingEntity(entityPlayer, entityPlayer.boundingBox.expand(6.0D, 6.0D, 6.0D));

    	    int iterationLength = entitiesNearbyList.size();

    	    if (iterationLength > 0)
    	    {
    	    	for (int index = 0; index < iterationLength; index++)
    		    {
    	    		
    	    		Entity entityNearby = (Entity) entitiesNearbyList.get(index);
    	    		
    	    		if (entityNearby instanceof MoCEntityHorse)
    	    		{
    	    			MoCEntityHorse horseNearby = (MoCEntityHorse) entityNearby;
    	    			
    	    			if (horseNearby.getIsTamed())
    	    			{	
    	    				horseNearby.shuffle();
    	    			}
    	    		}
    	    		
    		    }
    	    }
    		
    		
    		return true;
    	}
    	
    	
    	return false;
    }
    

    @SideOnly(Side.CLIENT)

    /**
     * Return the title for this record.
     */
    public String getRecordTitle()
    {
        return "MoC - " + this.recordName;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("mocreatures:recordshuffle");
    }

    public ResourceLocation getRecordResource(String name)
    {
        return RECORD_SHUFFLE_RESOURCE;
    }
    
}