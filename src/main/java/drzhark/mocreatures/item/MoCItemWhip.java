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
        Block block1 = world.getBlock(x, y + 1, z);
        
        if ((l != 0) && (block != null) && (block1 == Blocks.air) && (block != Blocks.air)&& (block != Blocks.standing_sign))
        {
            this.whipFX(world, x, y, z);
            
            world.playSoundAtEntity(entityPlayer, "mocreatures:whip", 0.5F, 0.4F / ((itemRand.nextFloat() * 0.4F) + 0.8F));
            
            itemstack.damageItem(1, entityPlayer);
            
            List listOfEntitiesNearPlayer = world.getEntitiesWithinAABBExcludingEntity(entityPlayer, entityPlayer.boundingBox.expand(5D, 5D, 5D));
            
            
            if (!(listOfEntitiesNearPlayer.isEmpty()))
            {
	            List<MoCEntityAnimal> whippableEntityList = new ArrayList<>();
	            
	            List<Double> whippableEntityDistanceToPlayerList = new ArrayList<>();
	            
	            int iterationLength = listOfEntitiesNearPlayer.size();
	            
	            findWhippableEntitiesNearPlayer(entityPlayer, listOfEntitiesNearPlayer, whippableEntityList, whippableEntityDistanceToPlayerList, iterationLength);
	            
	            
	            if (!(whippableEntityDistanceToPlayerList.isEmpty()) && !(whippableEntityList.isEmpty())) 
	            {
	            	int indexOfClosestWhippableEntityNearby = whippableEntityDistanceToPlayerList.indexOf(Collections.min(whippableEntityDistanceToPlayerList));
	            	
	            	
		            if ((indexOfClosestWhippableEntityNearby >= 0))
		            {
		            
		            	MoCEntityAnimal closestWhippableEntityToPlayer = whippableEntityList.get(indexOfClosestWhippableEntityNearby);    
			            
		            	//if the player using the whip is not the owner
				        if (
				        		closestWhippableEntityToPlayer.getOwnerName() != null 
				        		&& !closestWhippableEntityToPlayer.getOwnerName().equals("") 
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
            }
        }
        return true; //always true to play the item swing on use animation
    }
    
    

	private void findWhippableEntitiesNearPlayer(EntityPlayer entityPlayer, List listOfEntitiesNearPlayer, List<MoCEntityAnimal> whippableEntityList, List<Double> whippableEntityDistanceToPlayerList, int iterationLength)
	{
		for (int index = 0; index < iterationLength; index++)
		{
		    Entity entityNearPlayer = (Entity) listOfEntitiesNearPlayer.get(index);
		    
		    if (entityNearPlayer instanceof MoCEntityAnimal)
		    {
		        MoCEntityAnimal animalNearPlayer = (MoCEntityAnimal) entityNearPlayer;
		        
		        if (
		        		animalNearPlayer instanceof MoCEntityBigCat
		            	|| animalNearPlayer instanceof MoCEntityHorse
		            	|| animalNearPlayer instanceof MoCEntityKitty
		            	|| animalNearPlayer instanceof MoCEntityWyvern
		            	|| animalNearPlayer instanceof MoCEntityOstrich
		            	|| animalNearPlayer instanceof MoCEntityElephant
		        	)
		        {
		        	whippableEntityList.add(animalNearPlayer);
		        	whippableEntityDistanceToPlayerList.add(entityPlayer.getDistanceSq(animalNearPlayer.posX, animalNearPlayer.posY, animalNearPlayer.posZ));
		        }
		    }
		    else {continue;}
		}
	}

	
    
	private void performWhipActionOnWhippableEntity(EntityPlayer entityPlayer, MoCEntityAnimal closestWhippableEntityToPlayer, World world)
	{
		if (closestWhippableEntityToPlayer != null)
		{		
		    if (closestWhippableEntityToPlayer instanceof MoCEntityBigCat)
		    {
		        MoCEntityBigCat entityBigcat = (MoCEntityBigCat) closestWhippableEntityToPlayer;
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
		    
		    
		    if (closestWhippableEntityToPlayer instanceof MoCEntityHorse)
		    {
		        MoCEntityHorse entityHorse = (MoCEntityHorse) closestWhippableEntityToPlayer;
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
		                
		                if (entityHorse.isUndead()) {world.playSoundAtEntity(closestWhippableEntityToPlayer, "mocreatures:horsemadundead", 1.0F, 1.0F + 0.2F);}
		                
		                else if (entityHorse.isGhost()) {world.playSoundAtEntity(closestWhippableEntityToPlayer, "mocreatures:horsemadghost", 1.0F, 1.0F + 0.2F);}
		                
		                else {world.playSoundAtEntity(closestWhippableEntityToPlayer, "mocreatures:horsemad", 1.0F, 1.0F + 0.2F);}
		                
		            }
		        }
		    }
		    
		    
		    if ((closestWhippableEntityToPlayer instanceof MoCEntityKitty))
		    {
		        MoCEntityKitty entityKitty = (MoCEntityKitty) closestWhippableEntityToPlayer;
		        if ((entityKitty.getKittyState() > 2) && entityKitty.whipeable())
		        {
		            entityKitty.setSitting(!entityKitty.getIsSitting());
		        }
		    }
		    
		    
		    if ((closestWhippableEntityToPlayer instanceof MoCEntityWyvern))
		    {
		        MoCEntityWyvern entityWyvern = (MoCEntityWyvern) closestWhippableEntityToPlayer;
		        if (entityWyvern.getIsTamed() && !entityWyvern.isOnAir())
		        {
		            entityWyvern.setSitting(!entityWyvern.getIsSitting());
		        }
		    }
		    
		    
		    if (closestWhippableEntityToPlayer instanceof MoCEntityOstrich)
		    {
		        MoCEntityOstrich entityOstrich = (MoCEntityOstrich) closestWhippableEntityToPlayer;

		        //makes ridden ostrich sprint
		        if (entityOstrich.riddenByEntity != null && entityOstrich.sprintCounter == 0)
		        {
		            entityOstrich.sprintCounter = 1;
		            world.playSoundAtEntity(closestWhippableEntityToPlayer, "mocreatures:ostrichhurt", 1.0F, 1.0F + 0.2F);
		        }

		        //toggles hiding of tamed ostriches
		        if (entityOstrich.getIsTamed() && entityOstrich.riddenByEntity == null)
		        {
		            entityOstrich.setHiding(!entityOstrich.getHiding());
		        }
		    }
		    
		    
		    if (closestWhippableEntityToPlayer instanceof MoCEntityElephant)
		    {
		        MoCEntityElephant entityElephant = (MoCEntityElephant) closestWhippableEntityToPlayer;

		        //makes ridden elephants charge
		        if (entityElephant.riddenByEntity != null && entityElephant.sprintCounter == 0)
		        {
		            entityElephant.sprintCounter = 1;
		            world.playSoundAtEntity(closestWhippableEntityToPlayer, "mocreatures:elephantgrunt", 1.0F, 1.0F + 0.2F);
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