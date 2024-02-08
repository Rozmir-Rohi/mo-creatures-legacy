package drzhark.mocreatures.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityElephant;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.animal.MoCEntityKitty;
import drzhark.mocreatures.entity.animal.MoCEntityOstrich;
import drzhark.mocreatures.entity.animal.MoCEntityWyvern;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;;

public class MoCItemWhip extends MoCItem {
	
	int bigCatWhipCounter;
	
    public MoCItemWhip(String name)
    {
        super(name);
        maxStackSize = 1;
        setMaxDamage(24);
    }
    
    @Override
    public boolean isFull3D()
    {
        return true;
    }
    
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) //only the unbreaking enchantment can be applied to whips
    {	
    	NBTTagList bookEnchantmentNbtTagList = (NBTTagList) book.getTagCompound().getTag("StoredEnchantments");
    	
    	List<Short> bookEnchantmentIdList = new ArrayList<>();
    	
    	if (bookEnchantmentNbtTagList != null)
        {
            for (int index = 0; index < bookEnchantmentNbtTagList.tagCount(); ++index)
            {
            	
            	short enchantmentId = bookEnchantmentNbtTagList.getCompoundTagAt(index).getShort("id");
            	
                bookEnchantmentIdList.add(enchantmentId);
                
            }
            
            
            if (bookEnchantmentIdList.size() == 1 && bookEnchantmentIdList.get(0) == (short) 34) //34 is the id for the unbreaking enchantment
            {
            	return true;
            }
        }
    	
        return false;
    }

    
    
    public ItemStack onItemRightClick2(ItemStack itemstack, World world, EntityPlayer entityPlayer)
    {
        return itemstack;
    }
    
    

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityPlayer, World world, int x, int y, int z, int l, float f1, float f2, float f3)
    {
        Block block = world.getBlock(x, y, z);
        Block blockAbove = world.getBlock(x, y + 1, z);
        
        if ((l != 0) && (block != null) && (blockAbove == Blocks.air) && (block != Blocks.air)&& (block != Blocks.standing_sign))
        {
            whipFX(world, x, y, z);
            
            world.playSoundAtEntity(entityPlayer, "mocreatures:whip", 0.5F, 0.4F / ((itemRand.nextFloat() * 0.4F) + 0.8F));
            
            itemstack.damageItem(1, entityPlayer);
            
	            
            MoCEntityAnimal closestWhippableEntityToPlayer = findClosestWhippableEntityNearPlayer(entityPlayer);
            
            
            if (closestWhippableEntityToPlayer != null) 
            {
            	
            	//if the player using the whip is not the owner
		        if (
		        		closestWhippableEntityToPlayer.getOwnerName() != null 
		        		&& (closestWhippableEntityToPlayer.getOwnerName().length() > 0)
		        		&& !(entityPlayer.getCommandSenderName().equals(closestWhippableEntityToPlayer.getOwnerName())) 
		        	) 
		        { 
		        	if (closestWhippableEntityToPlayer.isPredator() && closestWhippableEntityToPlayer.riddenByEntity == null)
		        	{
		        		closestWhippableEntityToPlayer.setTarget(entityPlayer); 
		        	}
		        }
            	
		        else
		        {
		        	performWhipActionOnWhippableEntity(entityPlayer, closestWhippableEntityToPlayer, world); 
		        }
            }
        }
        return true; //always true to play the item swing on use animation
    }
    
    

	private MoCEntityAnimal findClosestWhippableEntityNearPlayer(EntityPlayer entityPlayer)
	{
        double currentMinimumDistance = -1D;
        
        double distance = 8.0D;

        MoCEntityAnimal entityAnimal = null;

        List entitiesNearbyList = entityPlayer.worldObj.getEntitiesWithinAABBExcludingEntity(entityPlayer, entityPlayer.boundingBox.expand(distance, distance, distance));

        int iterationLength = entitiesNearbyList.size();

        if (iterationLength > 0)
        {
	        for (int index = 0; index < iterationLength; index++)
	        {
	            Entity entityNearby = (Entity) entitiesNearbyList.get(index);

	            if (!(entityNearby instanceof MoCEntityAnimal))
	            {
	                continue;
	            }
	            
	            if (
		        		entityNearby instanceof MoCEntityBigCat
		            	|| entityNearby instanceof MoCEntityHorse
		            	|| entityNearby instanceof MoCEntityKitty
		            	|| entityNearby instanceof MoCEntityWyvern
		            	|| entityNearby instanceof MoCEntityOstrich
		            	|| entityNearby instanceof MoCEntityElephant
		        	)
	            {
		            double overallDistanceSquared = entityNearby.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
	
		            if (((distance < 0.0D) || (overallDistanceSquared < (distance * distance))) && ((currentMinimumDistance == -1D) || (overallDistanceSquared < currentMinimumDistance)) && ((EntityLivingBase) entityNearby).canEntityBeSeen(entityPlayer))
		            {
		                currentMinimumDistance = overallDistanceSquared;
		                entityAnimal = (MoCEntityAnimal) entityNearby;
		            }
	            }
	        }
        }

        return entityAnimal;
    }

	
    
	private void performWhipActionOnWhippableEntity(EntityPlayer entityPlayer, MoCEntityAnimal entityAnimal, World world)
	{
		if (entityAnimal != null)
		{		
		    if (entityAnimal instanceof MoCEntityBigCat)
		    {
		        MoCEntityBigCat entityBigcat = (MoCEntityBigCat) entityAnimal;
		        if (entityBigcat.getIsTamed())
		        {
		            entityBigcat.setSitting(!entityBigcat.getIsSitting());
		            bigCatWhipCounter++;
		        }
		        else if ((world.difficultySetting.getDifficultyId() > 0) && entityBigcat.getIsAdult())
		        {
		            entityBigcat.setTarget(entityPlayer);
		        }
		    }
		    if (bigCatWhipCounter > 6)
		    {
		    	entityPlayer.addStat(MoCAchievements.indiana, 1);
		    	bigCatWhipCounter = 0;
		    }
		    
		    
		    if (entityAnimal instanceof MoCEntityHorse)
		    {
		        MoCEntityHorse entityHorse = (MoCEntityHorse) entityAnimal;
		        if (entityHorse.getIsTamed())
		        {
		            if (entityHorse.riddenByEntity == null)
		            {
		                entityHorse.setEating(!entityHorse.getEating());
		            }
		            else if (entityHorse.isNightmare())
		            {
		                entityHorse.setNightmareFireTrailCounter(250);
		            }
		            else if (entityHorse.sprintCounter == 0)
		            {
		                entityHorse.sprintCounter = 1;
		                
		                if (entityHorse.isUndead()) {world.playSoundAtEntity(entityAnimal, "mocreatures:horsemadundead", 1.0F, 1.0F + 0.2F);}
		                
		                else if (entityHorse.isGhost()) {world.playSoundAtEntity(entityAnimal, "mocreatures:horsemadghost", 1.0F, 1.0F + 0.2F);}
		                
		                else {world.playSoundAtEntity(entityAnimal, "mocreatures:horsemad", 1.0F, 1.0F + 0.2F);}
		                
		            }
		        }
		    }
		    
		    
		    if ((entityAnimal instanceof MoCEntityKitty))
		    {
		        MoCEntityKitty entityKitty = (MoCEntityKitty) entityAnimal;
		        if ((entityKitty.getKittyState() > 2) && entityKitty.whipeable())
		        {
		            entityKitty.setSitting(!entityKitty.getIsSitting());
		        }
		    }
		    
		    
		    if ((entityAnimal instanceof MoCEntityWyvern))
		    {
		        MoCEntityWyvern entityWyvern = (MoCEntityWyvern) entityAnimal;
		        if (entityWyvern.getIsTamed() && !entityWyvern.isOnAir())
		        {
		            entityWyvern.setSitting(!entityWyvern.getIsSitting());
		        }
		    }
		    
		    
		    if (entityAnimal instanceof MoCEntityOstrich)
		    {
		        MoCEntityOstrich entityOstrich = (MoCEntityOstrich) entityAnimal;

		        //makes ridden ostrich sprint
		        if (entityOstrich.riddenByEntity != null && entityOstrich.sprintCounter == 0)
		        {
		            entityOstrich.sprintCounter = 1;
		            world.playSoundAtEntity(entityAnimal, "mocreatures:ostrichhurt", 1.0F, 1.0F + 0.2F);
		        }

		        //toggles hiding of tamed ostriches
		        if (entityOstrich.getIsTamed() && entityOstrich.riddenByEntity == null)
		        {
		            entityOstrich.setHiding(!entityOstrich.getHiding());
		        }
		    }
		    
		    
		    if (entityAnimal instanceof MoCEntityElephant)
		    {
		        MoCEntityElephant entityElephant = (MoCEntityElephant) entityAnimal;

		        //makes ridden elephants charge
		        if (entityElephant.riddenByEntity != null && entityElephant.sprintCounter == 0)
		        {
		            entityElephant.sprintCounter = 1;
		            world.playSoundAtEntity(entityAnimal, "mocreatures:elephantgrunt", 1.0F, 1.0F + 0.2F);
		        }
		    }
		}
	}

	
    public void whipFX(World world, int x, int y, int z)
    {
        double particleBasePositionX = x + 0.5F;
        double particleBasePositionY = y + 1.0F;
        double particleBasePositionZ = z + 0.5F;
        double yOffset = 0.2199999988079071D;
        double xzOffset = 0.27000001072883606D;
        world.spawnParticle("smoke", particleBasePositionX - xzOffset, particleBasePositionY + yOffset, particleBasePositionZ, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", particleBasePositionX - xzOffset, particleBasePositionY + yOffset, particleBasePositionZ, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", particleBasePositionX + xzOffset, particleBasePositionY + yOffset, particleBasePositionZ, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", particleBasePositionX + xzOffset, particleBasePositionY + yOffset, particleBasePositionZ, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", particleBasePositionX, particleBasePositionY + yOffset, particleBasePositionZ - xzOffset, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", particleBasePositionX, particleBasePositionY + yOffset, particleBasePositionZ - xzOffset, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", particleBasePositionX, particleBasePositionY + yOffset, particleBasePositionZ + xzOffset, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", particleBasePositionX, particleBasePositionY + yOffset, particleBasePositionZ + xzOffset, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", particleBasePositionX, particleBasePositionY, particleBasePositionZ, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", particleBasePositionX, particleBasePositionY, particleBasePositionZ, 0.0D, 0.0D, 0.0D);
    }
}