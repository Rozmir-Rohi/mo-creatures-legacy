package drzhark.mocreatures.item;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class MoCItemSpawnEgg extends ItemMonsterPlacer {
	private IIcon theIcon;
	
	public MoCItemSpawnEgg()
    {
       setHasSubtypes(true);
       setCreativeTab(MoCreatures.MOC_CREATIVE_TAB);
       setUnlocalizedName("spawnEgg");
       setTextureName("spawn_egg");
       GameRegistry.registerItem(this, getUnlocalizedName());
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack itemStack, int p_82790_2_)
    {
        EntityList.EntityEggInfo entityEggInfo = (EntityList.EntityEggInfo) MoCreatures.entityEggs.get(Integer.valueOf(itemStack.getItemDamage()));
        return entityEggInfo != null ? (p_82790_2_ == 0 ? entityEggInfo.primaryColor : entityEggInfo.secondaryColor) : 16777215;
    }
	
	/**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static Entity spawnCreature(World world, int entityId, double x, double y, double z)
    {
        if (!MoCreatures.entityEggs.containsKey(Integer.valueOf(entityId)))
        {
            return null;
        }
        else
        {
            Entity entity = null;

            for (int j = 0; j < 1; ++j)
            {
                entity = EntityList.createEntityByID(entityId, world);

                if (entity != null && entity instanceof EntityLivingBase)
                {
                    EntityLiving entityLiving = (EntityLiving)entity;
                    entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                    entityLiving.rotationYawHead = entityLiving.rotationYaw;
                    entityLiving.renderYawOffset = entityLiving.rotationYaw;
                    entityLiving.onSpawnWithEgg((IEntityLivingData)null);
                    world.spawnEntityInWorld(entity);
                    entityLiving.playLivingSound();
                }
            }

            return entity;
        }
    }
    
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if (world.isRemote)
        {
            return itemStack;
        }
        else
        {
            MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, entityPlayer, true);

            if (movingobjectposition == null)
            {
                return itemStack;
            }
            else
            {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!world.canMineBlock(entityPlayer, i, j, k))
                    {
                        return itemStack;
                    }

                    if (!entityPlayer.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStack))
                    {
                        return itemStack;
                    }

                    if (world.getBlock(i, j, k) instanceof BlockLiquid)
                    {
                        Entity entity = spawnCreature(world, itemStack.getItemDamage(), i, j, k);

                        if (entity != null)
                        {
                            if (entity instanceof EntityLivingBase && itemStack.hasDisplayName())
                            {
                                ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
                            }

                            if (!entityPlayer.capabilities.isCreativeMode)
                            {
                                --itemStack.stackSize;
                            }
                        }
                    }
                }

                return itemStack;
            }
        }
    }
    
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            Block block = world.getBlock(x, y, z);
            x += Facing.offsetsXForSide[p_77648_7_];
            y += Facing.offsetsYForSide[p_77648_7_];
            z += Facing.offsetsZForSide[p_77648_7_];
            double yOffset = 0.0D;

            if (p_77648_7_ == 1 && block.getRenderType() == 11)
            {
                yOffset = 0.5D;
            }

            Entity entity = spawnCreature(world, itemStack.getItemDamage(), x + 0.5D, y + yOffset, z + 0.5D);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && itemStack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(itemStack.getDisplayName());
                }

                if (!entityPlayer.capabilities.isCreativeMode)
                {
                    --itemStack.stackSize;
                }
            }

            return true;
        }
    }
    
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        Iterator iterator = MoCreatures.entityEggs.values().iterator();

        while (iterator.hasNext())
        {
            EntityList.EntityEggInfo entityEggInfo = (EntityList.EntityEggInfo)iterator.next();
            list.add(new ItemStack(item, 1, entityEggInfo.spawnedID));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        this.theIcon = iconRegister.registerIcon("spawn_egg_overlay");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int i1, int i2)
    {
        return i2 > 0 ? theIcon : super.getIconFromDamageForRenderPass(i1, i2);
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        String stringName = (StatCollector.translateToLocal("item.monsterPlacer.name")).trim();
        String entityNameSuffix = EntityList.getStringFromID(p_77653_1_.getItemDamage());

        if (entityNameSuffix != null)
        {
            stringName = stringName + " " + StatCollector.translateToLocal("entity." + entityNameSuffix + ".name");
        }

        return stringName;
    }
}

