package drzhark.mocreatures;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import drzhark.mocreatures.entity.IMoCTameable;
import drzhark.mocreatures.entity.MoCEntityAquatic;
import drzhark.mocreatures.entity.animal.MoCEntityBigCat;
import drzhark.mocreatures.entity.animal.MoCEntityElephant;
import drzhark.mocreatures.entity.animal.MoCEntityTurkey;
import drzhark.mocreatures.entity.monster.MoCEntityScorpion;
import drzhark.mocreatures.entity.vanilla_mc_extension.EntityCreeperExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;


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
    public void livingUpdate(final LivingEvent.LivingUpdateEvent event)
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
        	
        	if (event.entityLiving instanceof MoCEntityTurkey) //remove newly spawned Turkeys from biomes that they are not supposed to spawn in
        	{
        		MoCEntityTurkey turkey = (MoCEntityTurkey) event.entityLiving;
        		
        		if (!turkey.checkSpawningBiome() && !turkey.getIsTamed())
        		{
        			event.entityLiving.setDead();
        		}
        	}
        	
        	if (event.entityLiving instanceof MoCEntityScorpion) //remove newly spawned Scorpions from biomes that they are not supposed to spawn in
        	{
        		MoCEntityScorpion scorpion = (MoCEntityScorpion) event.entityLiving;
        		
        		if (!scorpion.checkSpawningBiome() && scorpion.getType() == 0)
        		{
        			event.entityLiving.setDead();
        		}
        	}
        	
        	if (MoCreatures.isBiomesOPlentyLoaded)
        	{
	        	if (event.entityLiving instanceof MoCEntityBigCat) //remove newly spawned Big Cats from biomes that they are not supposed to spawn in
	        	{
	        		MoCEntityBigCat big_cat = (MoCEntityBigCat) event.entityLiving;
	        		
	        		if ((big_cat.getType() == 0) && !big_cat.checkSpawningBiome() && !big_cat.getIsTamed())
	        		{
	        			event.entityLiving.setDead();
	        		}
	        	}
	        	
	        	if (event.entityLiving instanceof MoCEntityElephant) //remove newly spawned Elephants from biomes that they are not supposed to spawn in
	        	{
	        		MoCEntityElephant elephant = (MoCEntityElephant) event.entityLiving;
	        		
	        		if ((elephant.getType() == 0) && !elephant.checkSpawningBiome() && !elephant.getIsTamed())
	        		{
	        			event.entityLiving.setDead();
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
                    
	                    //ONLY WORKS IN SINGLEPLAYER - get the owner of the entity by their username 
	                    EntityPlayer owner_of_moc_entity_that_is_online = MinecraftServer.getServer().getConfigurationManager().func_152612_a(mocEntity.getOwnerName());
	                    
	                    Entity attacker = event.source.getEntity();
	                    
	                    String attacker_string_name = null;
	                    
	                    DamageSource last_damage_before_death = event.source;
	                    
	                    if (owner_of_moc_entity_that_is_online != null)
	                    {
	                    	if (attacker != null)
	                    	{
	                    		if (attacker instanceof IMoCTameable && ((IMoCTameable) attacker).getIsTamed()) {attacker_string_name = ((IMoCTameable) attacker).getName();}
	                    		else {attacker_string_name = attacker.getCommandSenderName();}
	                    		
	                    		if (last_damage_before_death.isProjectile()) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.arrow", new Object[] {mocEntity.getName(), attacker_string_name}));}
	                    			
	                    		else if (last_damage_before_death.isMagicDamage()) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.indirectMagic", new Object[] {mocEntity.getName(), attacker_string_name}));}
	                    		
	                    		else if (last_damage_before_death.isExplosion()) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.explosion.player", new Object[] {mocEntity.getName(), attacker_string_name}));}
	                    		
	                    		else {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.mob", new Object[] {mocEntity.getName(), attacker_string_name}));}
	                    	}
	                    
	                    	if (attacker == null)
	                    	{
	                    		if (((last_damage_before_death == DamageSource.onFire) || (last_damage_before_death == DamageSource.inFire)) && (last_damage_before_death != DamageSource.lava)) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.onFire", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (last_damage_before_death == DamageSource.lava) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.lava", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (last_damage_before_death == DamageSource.inWall) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.inWall", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (last_damage_before_death == DamageSource.drown)
	                    		{
	                    			if (mocEntity instanceof MoCEntityAquatic) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.MoCreatures.attack.dehydration", new Object[] {mocEntity.getName()}));}
	                    			
	                    			else {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.drown", new Object[] {mocEntity.getName()}));}
	                    		}
	                    	
	                    		else if (last_damage_before_death == DamageSource.cactus) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.cactus", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (last_damage_before_death.isExplosion()) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.explosion", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (last_damage_before_death == DamageSource.magic) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.magic", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (last_damage_before_death == DamageSource.fall) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.fell.accident.generic", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (last_damage_before_death == DamageSource.outOfWorld) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.outOfWorld", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (last_damage_before_death == DamageSource.wither) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.wither", new Object[] {mocEntity.getName()}));}
	                    		
	                    		else if (last_damage_before_death == DamageSource.anvil) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.anvil", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else if (last_damage_before_death == DamageSource.fallingBlock) {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.fallingBlock", new Object[] {mocEntity.getName()}));}
	                    	
	                    		else {owner_of_moc_entity_that_is_online.addChatMessage(new ChatComponentTranslation("death.attack.generic", new Object[] {mocEntity.getName()}));}
	                    	}
                    
	                    }
                    
                    }
                    
                }
            }
        }
    }
}