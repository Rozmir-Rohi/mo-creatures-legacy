package drzhark.mocreatures.item;

import com.google.common.collect.Multimap;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class MoCItemWeapon extends ItemSword {
    private float attackDamage;
    private final ToolMaterial toolMaterial;
    private int specialWeaponType = 0;
    private boolean breakable = false;
    

    public MoCItemWeapon(String name, ToolMaterial toolMaterial)
    {
        super(toolMaterial);
        
        this.setCreativeTab(MoCreatures.tabMoC);
        this.setUnlocalizedName(name);
        GameRegistry.registerItem(this, name);
        
        this.toolMaterial = toolMaterial;
        this.maxStackSize = 1;
        this.setMaxDamage(toolMaterial.getMaxUses());
        this.attackDamage = 4 + toolMaterial.getDamageVsEntity();
        
        this.maxStackSize = 1;
        
      //the operations below set the repair item for the tool material
        
        if (name.contains("scorp")) //scorpion sword
        {
        	toolMaterial.customCraftingMaterial = Items.diamond;
        }
        if (name.contains("shark"))
        {
        	toolMaterial.customCraftingMaterial = MoCreatures.sharkteeth;
        }  
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("mocreatures"+ this.getUnlocalizedName().replaceFirst("item.", ":"));
    }
    
    @Override
    public boolean getIsRepairable(ItemStack itemstack_weapon, ItemStack itemstack_in_anvil)
    {    	
    	ItemStack repair_material = this.toolMaterial.getRepairItemStack();
    	
        if (repair_material != null && OreDictionary.itemMatches(repair_material, itemstack_in_anvil, false))
        {
    	
        	String weapon_id = itemstack_weapon.getItem().itemRegistry.getNameForObject(itemstack_weapon.getItem());
        	
        	if( weapon_id.contains("sting") || weapon_id.contains("MoCreatures:bo")) //stingers and bo staff can't be repaired
            {
            	return false;
            }
            
            return true;
        }
        
        return false;
    }

    /**
     * 
     * @param par1
     * @param toolMaterial
     * @param damageType
     *            0 = default, 1 = poison, 2 = slow down, 3 = fire, 4 =
     *            nausea, 5 = blindness
     */
    public MoCItemWeapon(String name, ToolMaterial toolMaterial, int damageType, boolean fragile)
    {
        this(name, toolMaterial);
        this.specialWeaponType = damageType;
        this.breakable = fragile;
    }

    /**
     * Returns the strength of the stack against a given block. 1.0F base,
     * (Quality+1)*2 if correct blocktype, 1.5F if sword
     */
    @Override
    public float func_150893_a(ItemStack par1ItemStack, Block par2Block)
    {
        return par2Block == Blocks.web ? 15.0F : 1.5F;
    }

    /**
     * Current implementations of this method in child classes do not use the
     * entry argument beside ev. They just raise the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack itemStack, EntityLivingBase entityLiving_that_has_been_hit, EntityLivingBase par3EntityLiving)
    {
        int i = 1;
        if (breakable)
        {
            i = 10;
        }
        itemStack.damageItem(i, par3EntityLiving);
        int potionTime = 100;
        switch (specialWeaponType)
        {
        case 1: //poison
            entityLiving_that_has_been_hit.addPotionEffect(new PotionEffect(Potion.poison.id, potionTime, 0));
            break;
        case 2: //frost slowdown
            entityLiving_that_has_been_hit.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, potionTime, 0));
            break;
        case 3: //fire
            entityLiving_that_has_been_hit.setFire(10);
            break;
        case 4: //nausea
            entityLiving_that_has_been_hit.addPotionEffect(new PotionEffect(Potion.confusion.id, potionTime, 0));
            break;
        case 5: //blindness
            entityLiving_that_has_been_hit.addPotionEffect(new PotionEffect(Potion.blindness.id, potionTime, 0));
            break;
        default:
            break;
        }

        return true;
    }

    public boolean onBlockDestroyed(ItemStack itemStack, int par2, int par3, int par4, int par5, EntityLiving par6EntityLiving)
    {
        itemStack.damageItem(2, par6EntityLiving);
        return true;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @Override
    public boolean isFull3D()
    {
        return true;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack itemStack)
    {
        return EnumAction.block;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    /**
     * Returns if the item (tool) can harvest results from the block type.
     */
    @Override
    public boolean func_150897_b(Block block)
    {
        return block == Blocks.web;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based
     * on material.
     */
    @Override
    public int getItemEnchantability()
    {
        return this.toolMaterial.getEnchantability();
    }
    
    public Multimap func_111205_h() {
        final Multimap multimap = super.getItemAttributeModifiers();
        multimap.put((Object)SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), (Object)new AttributeModifier(MoCItemWeapon.field_111210_e, "Weapon modifier", (double)this.attackDamage, 0));
        return multimap;
    }
}
