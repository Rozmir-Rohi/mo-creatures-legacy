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
    	NBTTagList book_enchantment_NBT_tag_list = (NBTTagList) book.getTagCompound().getTag("StoredEnchantments");
    	
    	List<Short> book_enchantment_id_list = new ArrayList<>();
    	
    	if (book_enchantment_NBT_tag_list != null)
        {
            for (int i = 0; i < book_enchantment_NBT_tag_list.tagCount(); ++i)
            {
            	
            	short enchantment_id = book_enchantment_NBT_tag_list.getCompoundTagAt(i).getShort("id");
            	
                book_enchantment_id_list.add(enchantment_id);
                
            }
            
            
            if (book_enchantment_id_list.size() == 1 && book_enchantment_id_list.get(0) == (short) 34) //34 is the id for the unbreaking enchantment
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
            
            List list_of_entities_near_player = world.getEntitiesWithinAABBExcludingEntity(entityPlayer, entityPlayer.boundingBox.expand(5D, 5D, 5D));
            
            
            if (!(list_of_entities_near_player.isEmpty()))
            {
	            List<MoCEntityAnimal> whippable_entity_list = new ArrayList<>();
	            
	            List<Double> whippable_entityDistance_to_player_list = new ArrayList<>();
	            
	            int iterationLength = list_of_entities_near_player.size();
	            
	            findWhippableEntitiesNearPlayer(entityPlayer, list_of_entities_near_player, whippable_entity_list, whippable_entityDistance_to_player_list, iterationLength);
	            
	            
	            if (!(whippable_entityDistance_to_player_list.isEmpty()) && !(whippable_entity_list.isEmpty())) 
	            {
	            	int index_of_closest_whippable_entityNearby = whippable_entityDistance_to_player_list.indexOf(Collections.min(whippable_entityDistance_to_player_list));
	            	
	            	
		            if ((index_of_closest_whippable_entityNearby >= 0))
		            {
		            
		            	MoCEntityAnimal closest_whippable_entity_to_player = whippable_entity_list.get(index_of_closest_whippable_entityNearby);    
			            
		            	//if the player using the whip is not the owner
				        if (
				        		closest_whippable_entity_to_player.getOwnerName() != null 
				        		&& !closest_whippable_entity_to_player.getOwnerName().equals("") 
				        		&& !(entityPlayer.getCommandSenderName().equals(closest_whippable_entity_to_player.getOwnerName())) 
				        	) 
				        { 
				        	if (closest_whippable_entity_to_player.isPredator() && closest_whippable_entity_to_player.riddenByEntity == null)
				        	{
				        		closest_whippable_entity_to_player.setTarget(entityPlayer); 
				        	}
				        }
		            	
				        else
				        {
				        	performWhipActionOnWhippableEntity(entityPlayer, closest_whippable_entity_to_player, world); 
				        }	
			        }
	            }
            }
        }
        return true; //always true to play the item swing on use animation
    }
    
    

	private void findWhippableEntitiesNearPlayer(EntityPlayer entityPlayer, List list_of_entities_near_player, List<MoCEntityAnimal> whippable_entity_list, List<Double> whippable_entityDistance_to_player_list, int iterationLength)
	{
		for (int index = 0; index < iterationLength; index++)
		{
		    Entity entity_near_player = (Entity) list_of_entities_near_player.get(index);
		    
		    if (entity_near_player instanceof MoCEntityAnimal)
		    {
		        MoCEntityAnimal animal_near_player = (MoCEntityAnimal) entity_near_player;
		        
		        if (
		        		animal_near_player instanceof MoCEntityBigCat
		            	|| animal_near_player instanceof MoCEntityHorse
		            	|| animal_near_player instanceof MoCEntityKitty
		            	|| animal_near_player instanceof MoCEntityWyvern
		            	|| animal_near_player instanceof MoCEntityOstrich
		            	|| animal_near_player instanceof MoCEntityElephant
		        	)
		        {
		        	whippable_entity_list.add(animal_near_player);
		        	whippable_entityDistance_to_player_list.add(entityPlayer.getDistanceSq(animal_near_player.posX, animal_near_player.posY, animal_near_player.posZ));
		        }
		    }
		    else {continue;}
		}
	}

	
    
	private void performWhipActionOnWhippableEntity(EntityPlayer entityPlayer, MoCEntityAnimal closest_whippable_entity_to_player, World world)
	{
		if (closest_whippable_entity_to_player != null)
		{		
		    if (closest_whippable_entity_to_player instanceof MoCEntityBigCat)
		    {
		        MoCEntityBigCat entitybigcat = (MoCEntityBigCat) closest_whippable_entity_to_player;
		        if (entitybigcat.getIsTamed())
		        {
		            entitybigcat.setSitting(!entitybigcat.getIsSitting());
		            bigCatWhipCounter++;
		        }
		        else if ((world.difficultySetting.getDifficultyId() > 0) && entitybigcat.getIsAdult())
		        {
		            entitybigcat.setTarget(entityPlayer);
		        }
		    }
		    if (bigCatWhipCounter > 6)
		    {
		    	entityPlayer.addStat(MoCAchievements.indiana, 1);
		    	bigCatWhipCounter = 0;
		    }
		    
		    
		    if (closest_whippable_entity_to_player instanceof MoCEntityHorse)
		    {
		        MoCEntityHorse entityhorse = (MoCEntityHorse) closest_whippable_entity_to_player;
		        if (entityhorse.getIsTamed())
		        {
		            if (entityhorse.riddenByEntity == null)
		            {
		                entityhorse.setEating(!entityhorse.getEating());
		            }
		            else if (entityhorse.isNightmare())
		            {
		                entityhorse.setNightmareFireTrailCounter(250);
		            }
		            else if (entityhorse.sprintCounter == 0)
		            {
		                entityhorse.sprintCounter = 1;
		                
		                if (entityhorse.isUndead()) {world.playSoundAtEntity(closest_whippable_entity_to_player, "mocreatures:horsemadundead", 1.0F, 1.0F + 0.2F);}
		                
		                else if (entityhorse.isGhost()) {world.playSoundAtEntity(closest_whippable_entity_to_player, "mocreatures:horsemadghost", 1.0F, 1.0F + 0.2F);}
		                
		                else {world.playSoundAtEntity(closest_whippable_entity_to_player, "mocreatures:horsemad", 1.0F, 1.0F + 0.2F);}
		                
		            }
		        }
		    }
		    
		    
		    if ((closest_whippable_entity_to_player instanceof MoCEntityKitty))
		    {
		        MoCEntityKitty entitykitty = (MoCEntityKitty) closest_whippable_entity_to_player;
		        if ((entitykitty.getKittyState() > 2) && entitykitty.whipeable())
		        {
		            entitykitty.setSitting(!entitykitty.getIsSitting());
		        }
		    }
		    
		    
		    if ((closest_whippable_entity_to_player instanceof MoCEntityWyvern))
		    {
		        MoCEntityWyvern entitywyvern = (MoCEntityWyvern) closest_whippable_entity_to_player;
		        if (entitywyvern.getIsTamed() && !entitywyvern.isOnAir())
		        {
		            entitywyvern.setSitting(!entitywyvern.getIsSitting());
		        }
		    }
		    
		    
		    if (closest_whippable_entity_to_player instanceof MoCEntityOstrich)
		    {
		        MoCEntityOstrich entityostrich = (MoCEntityOstrich) closest_whippable_entity_to_player;

		        //makes ridden ostrich sprint
		        if (entityostrich.riddenByEntity != null && entityostrich.sprintCounter == 0)
		        {
		            entityostrich.sprintCounter = 1;
		            world.playSoundAtEntity(closest_whippable_entity_to_player, "mocreatures:ostrichhurt", 1.0F, 1.0F + 0.2F);
		        }

		        //toggles hiding of tamed ostriches
		        if (entityostrich.getIsTamed() && entityostrich.riddenByEntity == null)
		        {
		            entityostrich.setHiding(!entityostrich.getHiding());
		        }
		    }
		    
		    
		    if (closest_whippable_entity_to_player instanceof MoCEntityElephant)
		    {
		        MoCEntityElephant entityelephant = (MoCEntityElephant) closest_whippable_entity_to_player;

		        //makes ridden elephants charge
		        if (entityelephant.riddenByEntity != null && entityelephant.sprintCounter == 0)
		        {
		            entityelephant.sprintCounter = 1;
		            world.playSoundAtEntity(closest_whippable_entity_to_player, "mocreatures:elephantgrunt", 1.0F, 1.0F + 0.2F);
		        }
		    }
		}
	}

	
    public void whipFX(World world, int i, int j, int k)
    {
        double d = i + 0.5F;
        double d1 = j + 1.0F;
        double d2 = k + 0.5F;
        double d3 = 0.2199999988079071D;
        double d4 = 0.27000001072883606D;
        world.spawnParticle("smoke", d - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", d - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", d + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", d + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", d, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", d, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", d, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", d, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("smoke", d, d1, d2, 0.0D, 0.0D, 0.0D);
        world.spawnParticle("flame", d, d1, d2, 0.0D, 0.0D, 0.0D);
    }
}