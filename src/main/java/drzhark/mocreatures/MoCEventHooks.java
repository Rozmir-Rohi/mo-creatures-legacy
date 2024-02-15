package drzhark.mocreatures;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import drzhark.mocreatures.entity.IMoCTameable;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.ambient.MoCEntityBee;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityElephant;
import drzhark.mocreatures.entity.animal.MoCEntityOstrich;
import drzhark.mocreatures.entity.animal.MoCEntityTurkey;
import drzhark.mocreatures.entity.monster.MoCEntityScorpion;
import drzhark.mocreatures.entity.vanilla_mc_extension.EntityCreeperExtension;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfMinecraftComesAliveVillagerWitchery;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfVillagerWitchery;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfWitchery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;;


public class MoCEventHooks {

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        // if overworld has been deleted or unloaded, reset our flag
        if (event.world.provider.dimensionId == 0)
        {
            MoCreatures.proxy.worldInitDone = false;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (DimensionManager.getWorld(0) != null && !MoCreatures.proxy.worldInitDone) // if overworld has loaded, use its mapstorage
        {
            MoCPetMapData data = (MoCPetMapData)DimensionManager.getWorld(0).mapStorage.loadData(MoCPetMapData.class, "mocreatures");
            if (data == null)
            {
                data = new MoCPetMapData("mocreatures");
            }

            DimensionManager.getWorld(0).mapStorage.setData("mocreatures", data);
            DimensionManager.getWorld(0).mapStorage.saveAllData();
            MoCreatures.instance.mapData = data;
            MoCreatures.proxy.worldInitDone = true;
        }
    }
    
    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event)
	{
        if (!event.entityLiving.worldObj.isRemote)
        { 
        	if (MoCreatures.proxy.replaceVanillaCreepers) //replace vanilla creepers with creeper extension if it is enabled in the MoC config files
			{
        		if (event.entityLiving.getClass() == EntityCreeper.class)
        		{
		            EntityCreeperExtension creeper = new EntityCreeperExtension(event.entityLiving.worldObj);
		            creeper.copyLocationAndAnglesFrom((Entity) event.entityLiving);
		            creeper.onSpawnWithEgg((IEntityLivingData) null);
		            event.entityLiving.setDead();
		            creeper.worldObj.spawnEntityInWorld((Entity) creeper);   
        		}
	        }
        	
        	if (event.entityLiving instanceof MoCEntityTurkey) //remove newly spawned Turkeys from biomes that they are not supposed to spawn in (mainly savannas)
        	{
        		MoCEntityTurkey turkey = (MoCEntityTurkey) event.entityLiving;
        		
        		if (!turkey.getIsTamed() && turkey.ticksExisted < 3 && !turkey.checkSpawningBiome())
        		{
        			event.entityLiving.setDead();
        		}
        	}
        	
        	if (event.entityLiving instanceof MoCEntityScorpion) //remove newly spawned Scorpions from biomes that they are not supposed to spawn in (mainly beaches)
        	{
        		MoCEntityScorpion scorpion = (MoCEntityScorpion) event.entityLiving;
        		
        		if (scorpion.getType() == 0 && !scorpion.checkSpawningBiome())
        		{
        			event.entityLiving.setDead();
        		}
        	}
        	
        	if (MoCreatures.isWitcheryLoaded)
        	{
        		if (MoCreatures.proxy.replaceWitcheryWerewolfEntities)
        		{
		        	if (event.entity instanceof EntityMob && EntityList.getEntityString(event.entity).equals("witchery.wolfman"))
		        	{
		        		Random rand = new Random();
		        		if (MoCreatures.isMinecraftComesAliveLoaded && MoCreatures.proxy.useHumanModelAndMinecraftComesAliveVillagerTexturesForWitcheryWerewolfEntities)
		            	{
		        			
		        			int[] villagerInformation = generateRandomDataForMinecraftComesAliveVillagerWerewolf();
		        			
        					MoCEntityWerewolfWitchery werewolf = new MoCEntityWerewolfWitchery(event.entity.worldObj, villagerInformation[0] + 1, villagerInformation[1], villagerInformation[2]);
				            werewolf.copyLocationAndAnglesFrom((Entity) event.entity);
				            event.entity.setDead();
				            werewolf.worldObj.spawnEntityInWorld((Entity) werewolf);
        					
		            	}
		        		
		        		else
		        		{
			        		MoCEntityWerewolfWitchery werewolf = new MoCEntityWerewolfWitchery(event.entity.worldObj, rand.nextInt(5), rand.nextInt(3) + 1); //the random number from 0-4 sets a random vanilla minecraft villager profession, the random integar from 1-3 sets the werewolf type
				            werewolf.copyLocationAndAnglesFrom((Entity) event.entity);
				            event.entity.setDead();
				            werewolf.worldObj.spawnEntityInWorld((Entity) werewolf);
		        		}
		        	}
		        	
		        	if (event.entity instanceof EntityVillager && EntityList.getEntityString(event.entity).equals("witchery.werevillager"))
		        	{
		        		EntityVillager oldVillager = (EntityVillager) event.entity;
		        		
		        		if (MoCreatures.isMinecraftComesAliveLoaded && MoCreatures.proxy.useHumanModelAndMinecraftComesAliveVillagerTexturesForWitcheryWerewolfEntities)
		            	{
        					
		        			int[] villagerInformation = generateRandomDataForMinecraftComesAliveVillagerWerewolf();
		        			
			        		MoCEntityWerewolfMinecraftComesAliveVillagerWitchery werewolfMinecraftComesAliveVillager = new MoCEntityWerewolfMinecraftComesAliveVillagerWitchery(event.entity.worldObj, villagerInformation[0], villagerInformation[1], villagerInformation[2]);
				            werewolfMinecraftComesAliveVillager.copyLocationAndAnglesFrom((Entity) event.entity);
				            event.entity.setDead();
				            werewolfMinecraftComesAliveVillager.worldObj.spawnEntityInWorld((Entity) werewolfMinecraftComesAliveVillager);
		        			
		            	}
		        		
		        		else
		        		{	
			        		int professionToSet = oldVillager.getProfession();
			        		
			        		MoCEntityWerewolfVillagerWitchery werewolfVillager = new MoCEntityWerewolfVillagerWitchery(event.entity.worldObj);
				            werewolfVillager.copyLocationAndAnglesFrom((Entity) event.entity);
				            werewolfVillager.setProfession(professionToSet);
				            event.entity.setDead();
				            werewolfVillager.worldObj.spawnEntityInWorld((Entity) werewolfVillager);
		        		}
		        	}
        		}
        		
        		if (
        				(MoCreatures.proxy.replaceWitcheryPlayerWolf || MoCreatures.proxy.replaceWitcheryPlayerWerewolf)
        				&& event.entity instanceof EntityPlayer
        			)
        		{
        			EntityPlayer entityPlayer = (EntityPlayer) event.entity;
        			
        			if (
        					!(entityPlayer.isInvisible())
        					&& (
        							(MoCTools.isPlayerInWolfForm(entityPlayer) && MoCreatures.proxy.replaceWitcheryPlayerWolf) 
        							|| (MoCTools.isPlayerInWerewolfForm(entityPlayer) && MoCreatures.proxy.replaceWitcheryPlayerWerewolf)
        						)
        				)
        			{
        				entityPlayer.setInvisible(true); //Makes the player hand, player model, witchery player wolf model, and witchery player werewolf model invisible. Have to do this instead of canceling RenderPlayerEvent.Pre event to disable both the vanilla minecraft player model and Witchery models
        			}
        			else if (
        						entityPlayer.isInvisible()
        						&& !(entityPlayer.isPotionActive(Potion.invisibility))
        						&& !MoCTools.isPlayerInWolfForm(entityPlayer)
        						&& !MoCTools.isPlayerInWerewolfForm(entityPlayer)
        					)
        			{
        				entityPlayer.setInvisible(false); //Makes the player model visible again
        			}
        		}
        	}
        	
        	if (MoCreatures.isBiomesOPlentyLoaded)
        	{
	        	if (event.entityLiving instanceof MoCEntityBigCat) //remove newly spawned Big Cats from biomes that they are not supposed to spawn in
	        	{
	        		MoCEntityBigCat bigCat = (MoCEntityBigCat) event.entityLiving;
	        		
	        		if (bigCat.getType() == 0 && !bigCat.checkSpawningBiome() && !bigCat.getIsTamed())
	        		{
	        			event.entityLiving.setDead();
	        		}
	        	}
	        	
	        	if (event.entityLiving instanceof MoCEntityElephant) //remove newly spawned Elephants from biomes that they are not supposed to spawn in
	        	{
	        		MoCEntityElephant elephant = (MoCEntityElephant) event.entityLiving;
	        		
	        		if (elephant.getType() == 0 && !elephant.checkSpawningBiome() && !elephant.getIsTamed())
	        		{
	        			event.entityLiving.setDead();
	        		}
	        	}
	        	
	        	if (event.entityLiving instanceof MoCEntityOstrich) //remove newly spawned Ostriches from biomes that they are not supposed to spawn in
	        	{
	        		MoCEntityOstrich ostrich = (MoCEntityOstrich) event.entityLiving;
	        		
	        		if (ostrich.getType() == 0 && !ostrich.checkSpawningBiome())
	        		{
	        			event.entityLiving.setDead();
	        		}
	        	}
        	}
        }
    }
    
    @SubscribeEvent
    public void BreakEvent(BlockEvent.BreakEvent event) 
    {
    	if (!event.world.isRemote)
        { 
	    	if (MoCreatures.isPalmsHarvestLoaded)
	    	{	//spawn bees when a player breaks a hive from the Palm's Harvest mod
	    		if ((event.block.blockRegistry).getNameForObject(event.block).equals("harvestcraft:beehive"))
	    		{   
	    			Random rand = new Random();
		            int amount_of_bees_to_spawn = 2 + rand.nextInt(5); // 2-6 bees
		            
		            for (int index = 0; index < amount_of_bees_to_spawn; index++)
		            {
		            	MoCEntityBee bee = new MoCEntityBee(event.world);
			            bee.setPosition(event.x, event.y, event.z);
			            bee.onSpawnWithEgg((IEntityLivingData) null);
			            
			            bee.setTarget(event.getPlayer());
			            
			            bee.motionY += 0.3D;
			            bee.setIsFlying(true);
			            
		            	bee.worldObj.spawnEntityInWorld((Entity) bee); 	
		            }
	    		}
	    	}
        }
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) 
    {
        if (MoCreatures.isServer())
        {
            if (IMoCTameable.class.isAssignableFrom(event.entityLiving.getClass())) 
            {
                IMoCTameable mocEntity = (IMoCTameable)event.entityLiving;
                if (mocEntity.getIsTamed() && mocEntity.getPetHealth() > 0 && !mocEntity.isRiderDisconnecting())
                {
                    return;
                }

                if (mocEntity.getOwnerPetId() != -1) // required since getInteger will always return 0 if no key is found
                {
                    MoCreatures.instance.mapData.removeOwnerPet(mocEntity, mocEntity.getOwnerPetId());
                    
                    
                    if (MoCreatures.proxy.enableMoCPetDeathMessages)
                    {
                    
	                    //get the owner of the entity by their username 
	                    EntityPlayer ownerOfMocEntityThatIsOnline = MinecraftServer.getServer().getConfigurationManager().func_152612_a(mocEntity.getOwnerName());
	                    
	                    Entity attacker = event.source.getEntity();
	                    
	                    String attackerStringName = null;
	                    
	                    DamageSource lastDamageBeforeDeath = event.source;
	                    
	                    if (ownerOfMocEntityThatIsOnline != null)
	                    {
	                    	if (attacker != null)
	                    	{
	                    		if (attacker instanceof IMoCTameable && ((IMoCTameable) attacker).getIsTamed()) {attackerStringName = ((IMoCTameable) attacker).getName();}
	                    		else {attackerStringName = attacker.getCommandSenderName();}
	                    		
	                    		if (lastDamageBeforeDeath.isProjectile()) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.arrow", new Object[] {mocEntity.getName(), attackerStringName}));}
	                    			
	                    		else if (lastDamageBeforeDeath.isMagicDamage()) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.indirectMagic", new Object[] {mocEntity.getName(), attackerStringName}));}
	                    		
	                    		else if (lastDamageBeforeDeath.isExplosion()) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.explosion.player", new Object[] {mocEntity.getName(), attackerStringName}));}
	                    		
	                    		else {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.mob", new Object[] {mocEntity.getName(), attackerStringName}));}
	                    	}
	                    
	                    	if (attacker == null)
	                    	{
	                    		if (((lastDamageBeforeDeath == DamageSource.onFire) || (lastDamageBeforeDeath == DamageSource.inFire)) && (lastDamageBeforeDeath != DamageSource.lava)) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.onFire", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (lastDamageBeforeDeath == DamageSource.lava) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.lava", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (lastDamageBeforeDeath == DamageSource.inWall) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.inWall", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (lastDamageBeforeDeath == DamageSource.drown)
	                    		{
	                    			if (mocEntity instanceof MoCEntityAquatic) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.MoCreatures.attack.dehydration", new Object[] {mocEntity.getName()}));}
	                    			
	                    			else {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.drown", new Object[] {mocEntity.getName()}));}
	                    		}
	                    	
	                    		else if (lastDamageBeforeDeath == DamageSource.cactus) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.cactus", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (lastDamageBeforeDeath.isExplosion()) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.explosion", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (lastDamageBeforeDeath == DamageSource.magic) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.magic", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (lastDamageBeforeDeath == DamageSource.fall) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.fell.accident.generic", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (lastDamageBeforeDeath == DamageSource.outOfWorld) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.outOfWorld", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (lastDamageBeforeDeath == DamageSource.wither) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.wither", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (lastDamageBeforeDeath == DamageSource.anvil) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.anvil", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (lastDamageBeforeDeath == DamageSource.fallingBlock) {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.fallingBlock", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else {ownerOfMocEntityThatIsOnline.addChatMessage(new ChatComponentTranslation("death.attack.generic", new Object[] {mocEntity.getName()}));}
	                    	}
                    
	                    }
                    
                    }
                    
                }
            }
        }
    }
    
    private int[] generateRandomDataForMinecraftComesAliveVillagerWerewolf()
    {
    	Random rand = new Random();
		
		int[] villagerInformation = new int[3]; // indexes: (0 = hairColor | 1 = profession | 2 = skinID)
    	
		int hairColor = rand.nextInt(2 + 1); //0-2 (0 = black | 1 = white | 2 = brown)
		
		
		int profession = 0;

		if(hairColor == 0 || hairColor == 2) //0 black hair | 2 brown hair
		{
			profession = rand.nextInt(6 + 1); //0-6	
		}
		else //1 white hair
		{
			profession = rand.nextInt(2 + 1); //0-2
		}
		
		int skinID = generateSkinIdForMinecraftComesAliveVillagerWerewolf(hairColor, profession);
    	
		villagerInformation[0] = hairColor;
		villagerInformation[1] = profession;
		villagerInformation[2] = skinID;
		
		return villagerInformation;
    }
    
    private int generateSkinIdForMinecraftComesAliveVillagerWerewolf(int hairColor, int profession)
    {
    	Random rand = new Random();
    	
    	if (hairColor == 0) //black hair
    	{
    		switch (profession)
    		{
    			case 0: //farmer
    				return rand.nextInt(8 + 1);
    			case 1: //librarian
    				return rand.nextInt(2 + 1);
    			case 2: //priest
    				return rand.nextInt(1 + 1);
    			case 3: //smith
    				return rand.nextInt(3 + 1);
    			case 4: //butcher
    				return rand.nextInt(1 + 1);
    			default:
    				return 0;
    		}		
    	}
    	if (hairColor == 1) //white hair
    	{
    		switch (profession)
    		{
    			case 0: //farmer
    				return rand.nextInt(1+ 1);
    			case 1: //librarian
    				return 0;
    			case 2: //priest
    				return rand.nextInt(2 + 1);
    			default:
    				return 0;
    		}		
    	}
    	if (hairColor == 2) //brown hair
    	{
    		switch (profession)
    		{
    			case 0: //farmer
    				return rand.nextInt(14 + 1);
    			case 1: //librarian
    				return rand.nextInt(7 + 1);
    			case 2: //priest
    				return rand.nextInt(1 + 1);
    			case 3: //smith
    				return rand.nextInt(8 + 1);
    			case 4: //butcher
    				return rand.nextInt(2 + 1);
    			default:
    				return 0;
    		}		
    	}
		return 0;
    }
}