package drzhark.mocreatures.entity.aquatic;

import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAquatic;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageHeart;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MoCEntityFishy extends MoCEntityTameableAquatic {

    public int gestationTime;
    private boolean hasEaten;

    public static final String fishNames[] = { "Blue", "Orange", "Cyan", "Greeny", "Green", "Purple", "Yellow", "Striped", "Yellowy", "Red" };

    public MoCEntityFishy(World world)
    {
        super(world);
        setSize(0.3F, 0.3F);
        setMoCAge(50 + rand.nextInt(50));
    }

    protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(6.0D);
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            setType(rand.nextInt(fishNames.length) + 1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("fishy1.png");
        case 2:
            return MoCreatures.proxy.getTexture("fishy2.png");
        case 3:
            return MoCreatures.proxy.getTexture("fishy3.png");
        case 4:
            return MoCreatures.proxy.getTexture("fishy4.png");
        case 5:
            return MoCreatures.proxy.getTexture("fishy5.png");
        case 6:
            return MoCreatures.proxy.getTexture("fishy6.png");
        case 7:
            return MoCreatures.proxy.getTexture("fishy7.png");
        case 8:
            return MoCreatures.proxy.getTexture("fishy8.png");
        case 9:
            return MoCreatures.proxy.getTexture("fishy9.png");
        case 10:
            return MoCreatures.proxy.getTexture("fishy10.png");
        default:
            return MoCreatures.proxy.getTexture("fishy1.png");
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // byte hasEaten 0 = false 1 = true
    }

    public boolean getHasEaten()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setHasEaten(boolean flag)
    {
        if (worldObj.isRemote) { return; }
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int dropChance = rand.nextInt(100);
        if (dropChance < 70)
        {
            entityDropItem(new ItemStack(Items.fish, 1, 0), 0.0F);
        }
        else
        {
            int amountOfEggsToDrop = rand.nextInt(2);
            for (int index = 0; index < amountOfEggsToDrop; index++)
            {
                entityDropItem(new ItemStack(MoCreatures.mocegg, 1, getType()), 0.0F);
            }

        }
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        
        if (!isInsideOfMaterial(Material.water))
        {
            prevRenderYawOffset = renderYawOffset = rotationYaw = prevRotationYaw;
            rotationPitch = prevRotationPitch;
        }

        if (MoCreatures.isServer())
        {
            if (!getIsAdult() && (rand.nextInt(100) == 0))
            {
                setMoCAge(getMoCAge() + 2);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                }
            }

            if (rand.nextInt(5) == 0 && !getIsTamed())
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

            if (!ReadyforParenting(this)) { return; }
            int i = 0;
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4D, 3D, 4D));
            for (int j = 0; j < list.size(); j++)
            {
                Entity entity = (Entity) list.get(j);
                if (entity instanceof MoCEntityFishy)
                {
                    i++;
                }
            }

            if (i > 1) { return; }
            List list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4D, 2D, 4D));
            for (int k = 0; k < list.size(); k++)
            {
                Entity entity1 = (Entity) list1.get(k);
                if (!(entity1 instanceof MoCEntityFishy) || (entity1 == this))
                {
                    continue;
                }
                MoCEntityFishy entityfishy = (MoCEntityFishy) entity1;
                if (!ReadyforParenting(this) || !ReadyforParenting(entityfishy) || (getType() != entityfishy.getType()))
                {
                    continue;
                }
                if (rand.nextInt(100) == 0)
                {
                    gestationTime++;
                }
                if (gestationTime % 3 == 0)
                {
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(getEntityId()), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                }
                if (gestationTime <= 50)
                {
                    continue;
                }
                int l = rand.nextInt(3) + 1;
                for (int i1 = 0; i1 < l; i1++)
                {
                    MoCEntityFishy entityfishy1 = new MoCEntityFishy(worldObj);
                    entityfishy1.setPosition(posX, posY, posZ);
                    worldObj.spawnEntityInWorld(entityfishy1);
                    playSound("mob.chicken.plop", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
                    setHasEaten(false);
                    entityfishy.setHasEaten(false);
                    gestationTime = 0;
                    entityfishy.gestationTime = 0;

                    EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 24D);
                    if (entityPlayer != null)
                    {
                        MoCTools.tameWithName(entityPlayer, entityfishy1);
                    }

                    entityfishy1.setMoCAge(20);
                    entityfishy1.setAdult(false);
                    entityfishy1.setTypeInt(getType());
                }

                break;
            }
        }

    }
    

    public static boolean isItemPlantMegaPackFishEdibleSaltWaterPlant(Item item)
    {
    	return (
    				(item.itemRegistry).getNameForObject(item).equals("plantmegapack:oceanCommonEelgrass")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:waterKelpGiantGRN")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:waterKelpGiantYEL")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:oceanMozuku")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:oceanSeaGrapes")
    				|| (item.itemRegistry).getNameForObject(item).equals("plantmegapack:oceanSeaLettuce")
    			);
    }
    
    
    @Override
    protected boolean isMyHealFood(ItemStack itemstack)
    {
    	if (itemstack == null) {return false;}
    	
    	Item item = itemstack.getItem();
    	
    	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);
    	
    	if (
    			item instanceof ItemSeeds
    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot_seeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:kelp")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:turnipSeeds")
    			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:coral1") && itemstack.getItemDamage() == 11 //BOP kelp
    			|| (item.itemRegistry).getNameForObject(item).equals("harvestcraft:seaweedItem")
    			|| isItemPlantMegaPackFishEdibleSaltWaterPlant(item)
    			|| (oreDictionaryNameArray.size() > 0 && oreDictionaryNameArray.contains("cropKelp"))
    			|| (
    					MoCreatures.isGregTech6Loaded
    					&& oreDictionaryNameArray.size() > 0
    					&& (
    							oreDictionaryNameArray.contains("listAllseed")
    							|| oreDictionaryNameArray.contains("foodRaisins")
    						)
    				)
	    
    		) {return true;}
    	
        return false;
    }

    public boolean ReadyforParenting(MoCEntityFishy entityfishy)
    {
        return false; //TODO: pending overhaul of breeding
    }
    
    @Override
    protected boolean canBeTrappedInNet() 
    {
        return true;
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
    public int rollRotationOffset()
    {
        if (!isInsideOfMaterial(Material.water))
        {
            return -90;
        }
        return 0;
    }
    
    @Override
    public float getAdjustedYOffset()
    {
        if (!isInsideOfMaterial(Material.water))
        {
            return -0.1F;
        }
        return 0.0F;
    }

    @Override
    public float getAdjustedXOffset()
    {
        if (!isInsideOfMaterial(Material.water))
        {
            return -0.2F;
        }
        return 0F;
    }

    @Override
    protected boolean isFisheable()
    {
        return !getIsTamed();
    }
}