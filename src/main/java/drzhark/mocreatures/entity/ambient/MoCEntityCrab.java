package drzhark.mocreatures.entity.ambient;


import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAmbient;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityCrab extends MoCEntityTameableAmbient

{
    public MoCEntityCrab(World world)
    {
        super(world);
        setSize(0.3F, 0.3F);
        setMoCAge(50 + rand.nextInt(50));
    }

    @Override
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.15F;
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            setType(rand.nextInt(2) + 1);
        }

    }
    
    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        ItemStack itemStack = entityPlayer.getHeldItem();
        
        if (itemStack != null)
    	{
	    	Item item = itemStack.getItem();
	    	
	    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemStack);
	    	
	    	if  (
        			(item instanceof ItemSeeds) //any vanilla mc raw fish except a pufferfish
        			|| (Item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
        			|| (Item.itemRegistry).getNameForObject(item).equals("etfuturum:kelp")
        			|| (Item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
        			|| (Item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:coral1") && itemStack.getItemDamage() == 11 //BOP kelp
        			|| (Item.itemRegistry).getNameForObject(item).equals("harvestcraft:seaweedItem")
        			|| MoCTools.isItemPlantMegaPackEdibleSaltWaterPlant(item)
        			|| (
        					oreDictionaryNameArray.size() > 0
        					&& (
        							oreDictionaryNameArray.contains("cropKelp")
        							|| oreDictionaryNameArray.contains("cropSeaweed")
    				    			|| (
    				    					MoCreatures.isGregTech6Loaded
    				    					&& (
    				    							oreDictionaryNameArray.contains("listAllseed")
    				    							|| oreDictionaryNameArray.contains("foodRaisins")
    				    						)
    				    				)
    				    		)
        				)
        		)
	    	{
	    		if (--itemStack.stackSize == 0)
	            {
	                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
	            }
	            
	            heal(5);

	            return true;
	    	}
        }
        
        return super.interact(entityPlayer);
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("craba.png");
        case 2:
            return MoCreatures.proxy.getTexture("crabb.png");
        default:
            return MoCreatures.proxy.getTexture("craba.png");
        }
    }

    

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (fleeingTick == 3)
            {
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 1), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
            }
        }
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType == 1) //fleeing animation finishes
        {
            fleeingTick = 0;
        }
    }

    @Override
    protected Item getDropItem()
    {
        return MoCreatures.crabRaw;
    }

    @Override
    public boolean isOnLadder()
    {
        return isCollidedHorizontally;
    }

    public boolean climbing()
    {
        return !onGround && isOnLadder();
    }

    @Override
    public void jump()
    {
    }


    @Override
    public float getSizeFactor() 
    {   
        return 0.7F * getMoCAge() * 0.01F;
    }
    
    @Override
    public boolean canBreatheUnderwater()
    {
        return true;
    }
    
    public boolean isFleeing()
    {
        return fleeingTick != 0;
    }
    
    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
	public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }
    
    @Override
	protected boolean canBeTrappedInNet() 
    {
        return true;
    }
    
    @Override
    public boolean shouldRenderName()
    {
        return getShouldDisplayName() && (riddenByEntity == null);
    }
    
    @Override
    public int nameYOffset()
    {
        return -20;
    }
}