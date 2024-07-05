package drzhark.mocreatures.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCItemArmor extends ItemArmor
{
    public MoCItemArmor(String name, ArmorMaterial enumarmormaterial, int j, int k)
    {
        super(enumarmormaterial, j, k);
        
        setCreativeTab(MoCreatures.MOC_CREATIVE_TAB);
        setUnlocalizedName(name);
        GameRegistry.registerItem(this, name);
        
        
        if (name.contains("helmet")) //detects the helmet for an armor set and applies the repair item for all the armor pieces in that set
        {
	        if (name.contains("reptile"))
	        {
	        	enumarmormaterial.customCraftingMaterial = MoCreatures.hideReptile;
	        }
	        
	        if (name.contains("fur"))
	        {
	        	enumarmormaterial.customCraftingMaterial = MoCreatures.fur;
	        }
	        
	        if (name.contains("hide"))
	        {
	        	enumarmormaterial.customCraftingMaterial = MoCreatures.hide;
	        }
	        if (name.contains("scorp")) //scorpion armor
	        {
	        	if (name.contains("dirt"))
	            {
	        		enumarmormaterial.customCraftingMaterial = MoCreatures.chitin;
	            }
	        	
	        	if (name.contains("frost"))
	            {
	        		enumarmormaterial.customCraftingMaterial = MoCreatures.chitinFrost;
	            }
	        	
	        	if (name.contains("nether"))
	            {
	        		enumarmormaterial.customCraftingMaterial = MoCreatures.chitinNether;
	            }
	        	
	        	if (name.contains("cave"))
	            {
	        		enumarmormaterial.customCraftingMaterial = MoCreatures.chitinCave;
	            }
	        }
        }
    }

    @Override
    public String getArmorTexture(ItemStack itemstack, Entity entity, int slot, String layer)
    {
        String tempArmorTexture = "croc_1.png";;
        if ((itemstack.getItem() == MoCreatures.helmetReptile) || (itemstack.getItem() == MoCreatures.plateReptile) || (itemstack.getItem() == MoCreatures.bootsReptile))
        {
            tempArmorTexture = "croc_1.png";
        }
        if (itemstack.getItem() == MoCreatures.legsReptile)
        {
            tempArmorTexture = "croc_2.png";
        }

        if ((itemstack.getItem() == MoCreatures.helmetFur) || (itemstack.getItem() == MoCreatures.chestFur) || (itemstack.getItem() == MoCreatures.bootsFur))
        {
            tempArmorTexture = "fur_1.png";
        }
        if (itemstack.getItem() == MoCreatures.legsFur)
        {
            tempArmorTexture = "fur_2.png";;
        }

        if ((itemstack.getItem() == MoCreatures.helmetHide) || (itemstack.getItem() == MoCreatures.chestHide) || (itemstack.getItem() == MoCreatures.bootsHide))
        {
            tempArmorTexture = "hide_1.png";
        }
        if (itemstack.getItem() == MoCreatures.legsHide)
        {
            tempArmorTexture = "hide_2.png";
        }

        if ((itemstack.getItem() == MoCreatures.scorpHelmetDirt) || (itemstack.getItem() == MoCreatures.scorpPlateDirt) || (itemstack.getItem() == MoCreatures.scorpBootsDirt))
        {
            tempArmorTexture = "scorpd_1.png";
        }
        if (itemstack.getItem() == MoCreatures.scorpLegsDirt)
        {
            tempArmorTexture = "scorpd_2.png";
        }

        if ((itemstack.getItem() == MoCreatures.scorpHelmetFrost) || (itemstack.getItem() == MoCreatures.scorpPlateFrost) || (itemstack.getItem() == MoCreatures.scorpBootsFrost))
        {
            tempArmorTexture = "scorpf_1.png";
        }
        if (itemstack.getItem() == MoCreatures.scorpLegsFrost)
        {
            tempArmorTexture = "scorpf_2.png";
        }

        if ((itemstack.getItem() == MoCreatures.scorpHelmetCave) || (itemstack.getItem() == MoCreatures.scorpPlateCave) || (itemstack.getItem() == MoCreatures.scorpBootsCave))
        {
            tempArmorTexture = "scorpc_1.png";
        }
        if (itemstack.getItem() == MoCreatures.scorpLegsCave)
        {
            tempArmorTexture = "scorpc_2.png";
        }

        if ((itemstack.getItem() == MoCreatures.scorpHelmetNether) || (itemstack.getItem() == MoCreatures.scorpPlateNether) || (itemstack.getItem() == MoCreatures.scorpBootsNether))
        {
            tempArmorTexture = "scorpn_1.png";
        }
        if (itemstack.getItem() == MoCreatures.scorpLegsNether)
        {
            tempArmorTexture = "scorpn_2.png";
        }

        return "mocreatures:" + MoCreatures.proxy.ARMOR_TEXTURE + tempArmorTexture;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        itemIcon = par1IconRegister.registerIcon("mocreatures"+ getUnlocalizedName().replaceFirst("item.", ":"));
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     *
     * @param world
     * @param player
     * @param itemStack
     */
    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {
        if(world.rand.nextInt(50)==0 && player.getCurrentArmor(3) != null)
        {
            ItemStack myStack = player.getCurrentArmor(3);
            if (myStack != null && myStack.getItem() instanceof MoCItemArmor)
            {
                MoCTools.updatePlayerArmorEffects(player);
            }
        }
    }
}