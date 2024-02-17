package drzhark.mocreatures.entity.witchery_integration;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.item.MoCItemWhip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MoCEntityWerewolfVillagerWitchery extends EntityVillager {

	private boolean isTransforming;
	private int transformCounter;
	private int werewolfType = 0;
	
	public MoCEntityWerewolfVillagerWitchery(World world)
	{
		super(world);
		selectType();
	}
	
	public MoCEntityWerewolfVillagerWitchery(World world, int werewolfType)
	{
		super(world);
		this.werewolfType = werewolfType;
		selectType(); //this is here just in-case the inputed werwolfType goes wrong
	}
	
    public void selectType()
    {
    	if (werewolfType == 0)
    	{
    		int chance = rand.nextInt(100);
            if (chance <= 28)
            {
            	werewolfType = 1; //black
            }
            else if (chance <= 56)
            {
            	werewolfType = 3; //brown
            }
            else
            {
            	werewolfType = 2; //white
            }
    	}
    }
	
	@Override
    public boolean interact(EntityPlayer entityPlayer)
    {
		if(isTransforming) {return false;}
		
		if (super.interact(entityPlayer)) { return false; }
		
		ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        
        if (itemstack != null)
        {
        	Item item = itemstack.getItem();
        	
        	
        	//detects if player is interacting with villager using wolf bane from the Witchery mod
        	if (entityPlayer.isSneaking() && (item.itemRegistry).getNameForObject(item).equals("witchery:ingredient") && itemstack.getItemDamage() == 156)
        	{
        		MoCTools.playCustomSound(this, "werewolfhowl", worldObj);
        		
        		MoCItemWhip.whipFX(worldObj, MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)); //reusing this method from whip item to avoid code duplication
        		
        		return true;
        	}
        }
        
        return false;
    }
	
	@Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!worldObj.isRemote)
        {
            if (IsNight() && (rand.nextInt(250) == 0) && !isTransforming)
            {
                isTransforming = true;
            }
        }
        
        if (isTransforming && (rand.nextInt(3) == 0))
        {
            transformCounter++;
            if ((transformCounter % 2) == 0)
            {
                posX += 0.29999999999999999D;
                posY += transformCounter / 30;
                attackEntityFrom(DamageSource.causeMobDamage(this), 0);
            }
            if ((transformCounter % 2) != 0)
            {
                posX -= 0.29999999999999999D;
            }
            if (transformCounter > 30)
            {
                Transform();
                transformCounter = 0;
                isTransforming = false;
            }
        }
    }
	
	@Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
    	
        Entity entityThatAttackedThisCreature = damageSource.getEntity();
        
        if (entityThatAttackedThisCreature != null && !(entityThatAttackedThisCreature instanceof EntityPlayer))
        {		
	        if (MoCreatures.isWitcheryLoaded && EntityList.getEntityString(entityThatAttackedThisCreature).equals("witchery.witchhunter"))
	        {
	        	damageTaken = 5;
	        	damageSource = DamageSource.generic;
	        }
        }
        
        return super.attackEntityFrom(damageSource, damageTaken);
    }
	
	private void Transform()
    {
        if (deathTime > 0) { return; }
        
        isTransforming = false;
        
        MoCEntityWerewolfWitchery werewolf = new MoCEntityWerewolfWitchery(worldObj, getProfession(), werewolfType);
        werewolf.copyLocationAndAnglesFrom((Entity) this);
        setDead();
        werewolf.worldObj.spawnEntityInWorld((Entity) werewolf); 
    }
	
	public boolean IsNight()
    {
        return !worldObj.isDaytime();
    }


}
