package drzhark.mocreatures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import drzhark.mocreatures.configuration.MoCConfigCategory;
import drzhark.mocreatures.configuration.MoCConfiguration;
import drzhark.mocreatures.configuration.MoCProperty;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import drzhark.mocreatures.entity.monster.MoCEntityBigGolem;
import drzhark.mocreatures.utils.MoCLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;


public class MoCProxy implements IGuiHandler {

    public static String ARMOR_TEXTURE = "textures/armor/";
    public static String BLOCK_TEXTURE = "textures/blocks/";
    public static String ITEM_TEXTURE = "textures/items/";
    public static String MODEL_TEXTURE = "textures/models/";
    public static String GUI_TEXTURE = "textures/gui/";
    public static String MISC_TEXTURE = "textures/misc/";

    //CONFIG VARIABLES
    // Client Only
    public int displayPetHealthMode;
    public boolean displayPetName;
    public boolean displayPetIcons;
    public boolean animateTextures;
    public boolean useOriginalMoCreaturesTextures;

    //Server
    public boolean attackDolphins;
    public boolean attackWolves;
    public boolean attackHorses;
    public boolean specialHorsesFightBack;
    public boolean specialPetsDefendOwner;
    public boolean enableMoCPetDeathMessages;
    public boolean staticBed;
    public boolean staticLitter;

    public boolean easyBreeding;
    public boolean destroyDrops;
    public boolean enableStrictOwnership;
    public boolean emptyHandMountAndPickUpOnly;
    public boolean elephantBulldozer;
    
    public boolean enableRareGiantPandaVariant;
    
    public boolean replaceVanillaCreepers;
    
    //mod integration options
    public boolean replaceWitcheryWerewolfEntities;
    public boolean useHumanModelAndMCAVillagerTexturesForWitcheryHumanWerewolfEntities;
    public boolean replaceWitcheryPlayerWolf;
    public boolean replaceWitcheryPlayerWerewolf;
    public int colorForWitcheryPlayerWolfAndWerewolf;

    // griefing options
    public boolean golemDestroyBlocks;

    public int itemID;
    //new blocks IDs
    public int blockDirtID;
    public int blockGrassID;
    public int blockStoneID;
    public int blockLeafID;
    public int blockLogID;
    public int blockTallGrassID;
    public int blockPlanksID;
    public int wyvernDimension;
    public int wyvernBiomeID;

    public int maxTamed;
    public int maxOPTamed;
    public int ostrichEggDropChance;
    public int rareItemDropChance;
    public boolean slimyInsectsAndJellyfishDropSlimeballs;
    public int wyvernEggDropChance;
    public int motherWyvernEggDropChance;
    public int particleFX;
    
    // defaults
    public int frequency = 6;
    public int minGroup = 1;
    public int maxGroup = 2;
    public int maxSpawnInChunk = 3;
    public float strength = 1;

    // ogre settings
    public float ogreStrength;
    public float caveOgreStrength;
    public float fireOgreStrength;
    public short ogreAttackRange;
    public short fireOgreChance;
    public short caveOgreChance;
    
    public boolean wraithsCanGoThroughWalls;
    public boolean useRealisticHumanSoundsForWerewolf;

    public boolean debug = false;
    public boolean allowInstaSpawn;
    public boolean needsUpdate = false;
    public boolean worldInitDone = false;
    public int activeScreen = -1;

    public MoCConfiguration mocSettingsConfig;
    public MoCConfiguration mocEntityConfig;
    protected File configFile;

    protected static final String CATEGORY_MOC_GENERAL_SETTINGS = "global-settings";
    protected static final String CATEGORY_MOC_MOD_INTEGRATION_SETTINGS = "mod-integration-settings";
    protected static final String CATEGORY_MOC_CREATURE_GENERAL_SETTINGS = "creature-general-settings";
    protected static final String CATEGORY_MOC_MONSTER_GENERAL_SETTINGS = "monster-general-settings";
    protected static final String CATEGORY_MOC_WATER_CREATURE_GENERAL_SETTINGS = "water-mob-general-settings";
    protected static final String CATEGORY_MOC_AMBIENT_GENERAL_SETTINGS = "ambient-general-settings";
    protected static final String CATEGORY_MOC_ID_SETTINGS = "custom-id-settings";
    private static final String CATEGORY_VANILLA_CREATURE_FREQUENCIES = "vanilla-creature-frequencies";
    private static final String CATEGORY_CREATURES = "Creatures";
    private static final String CATEGORY_OWNERSHIP_SETTINGS = "ownership-settings";

    public void resetAllData()
    {
        //registerEntities();
        readGlobalConfigValues();
    }

    //----------------CONFIG INITIALIZATION
    public void ConfigInit(FMLPreInitializationEvent event) 
    {
        mocSettingsConfig = new MoCConfiguration(new File(event.getSuggestedConfigurationFile().getParent(), "MoCreatures" + File.separator + "MoC_General_Settings.cfg"));
        mocEntityConfig = new MoCConfiguration(new File(event.getSuggestedConfigurationFile().getParent(), "MoCreatures" + File.separator + "MoC_Spawn_List.cfg"));
        configFile = event.getSuggestedConfigurationFile();
        mocSettingsConfig.load();
        mocEntityConfig.load();
        //registerEntities();
        readGlobalConfigValues();
        if (debug) MoCLog.logger.info("Initializing MoCreatures Config File at " + event.getSuggestedConfigurationFile().getParent() + "MoC_General_Settings.cfg");
    }

    public int getFrequency(String entityName)//, EnumCreatureType type)
    {
        if (MoCreatures.mocEntityMap.get(entityName) != null)
            return MoCreatures.mocEntityMap.get(entityName).getFrequency();
        else return frequency;
    }

    //-----------------THE FOLLOWING ARE CLIENT SIDE ONLY, NOT TO BE USED IN SERVER AS THEY AFFECT ONLY DISPLAY / SOUNDS

    public void UndeadFX(Entity entity) {} //done client side

    public void StarFX(MoCEntityHorse moCEntityHorse) {}

    public void LavaFX(Entity entity) {}

    public void VanishFX(MoCEntityHorse entity) {}

    public void MaterializeFX(MoCEntityHorse entity) {}

    public void VacuumFX(MoCEntityBigGolem entity) {}
    
    public void hammerFX(EntityPlayer entityPlayer) {}

    public void teleportFX(EntityPlayer entity) {}
    
    public boolean getAnimateTextures() {
        return false;
    }

    public boolean getDisplayPetName()
    {
        return displayPetName;
    }

    public boolean getDisplayPetIcons()
    {
        return displayPetIcons;
    }

    public boolean getDisplayPetHealthMode(EntityLiving entityLiving)
    {
    	switch(displayPetHealthMode)
    	{
    		case 0:
    			return false;
    			
    		case 1:
    			return true;
    			
    		case 2:
    			if (entityLiving.getHealth() == entityLiving.getMaxHealth())
    	        {
    	        	return false;
    	        }
    	        else
    	        {
    	        	return true;
    	        }
    		default:
    			return true;
    	}
    }

    public int getParticleFX()
    {
        return 0;
    }

    public void initTextures() {}

    public ResourceLocation getTexture(String texture) 
    {
        return null;
    }

    public EntityPlayer getPlayer()
    {
        return null;
    }

    public void printMessageToPlayer(String msg)
    {
    }

    public List<String> parseName(String biomeConfigEntry)
    {
        String tag = biomeConfigEntry.substring(0, biomeConfigEntry.indexOf('|'));
        String biomeName = biomeConfigEntry.substring(biomeConfigEntry.indexOf('|') + 1, biomeConfigEntry.length());
        List<String> biomeParts = new ArrayList();
        biomeParts.add(tag);
        biomeParts.add(biomeName);
        return biomeParts;
    }

    public void readMocConfigValues()
    {
        if (MoCreatures.mocEntityMap != null && !MoCreatures.mocEntityMap.isEmpty())
        {
            for (MoCEntityData entityData : MoCreatures.mocEntityMap.values())
            {
                MoCConfigCategory entityConfigData = mocEntityConfig.getCategory(entityData.getEntityName().toLowerCase());
                if (!entityConfigData.containsKey("frequency"))
                {
                    entityConfigData.put("frequency", new MoCProperty("frequency", Integer.toString(entityData.getFrequency()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setFrequency(Integer.parseInt(entityConfigData.get("frequency").value));
                }
                if (!entityConfigData.containsKey("minspawn"))
                {
                    entityConfigData.put("minspawn", new MoCProperty("minspawn", Integer.toString(entityData.getMinSpawn()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setMinSpawn(Integer.parseInt(entityConfigData.get("minspawn").value));
                }
                if (!entityConfigData.containsKey("maxspawn"))
                {
                    entityConfigData.put("maxspawn", new MoCProperty("maxspawn", Integer.toString(entityData.getMaxSpawn()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setMaxSpawn(Integer.parseInt(entityConfigData.get("maxspawn").value));
                }
                if (!entityConfigData.containsKey("maxchunk"))
                {
                    entityConfigData.put("maxchunk", new MoCProperty("maxchunk", Integer.toString(entityData.getMaxInChunk()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setMaxInChunk(Integer.parseInt(entityConfigData.get("maxchunk").value));
                }
                if (!entityConfigData.containsKey("canspawn"))
                {
                    entityConfigData.put("canspawn", new MoCProperty("canspawn", Boolean.toString(entityData.getCanSpawn()), MoCProperty.Type.BOOLEAN));
                }
                else
                {
                    entityData.setCanSpawn(Boolean.parseBoolean(entityConfigData.get("canspawn").value));
                }
            }
        }
        mocEntityConfig.save();
    }

    /**
     * Reads values from file
     */
    public void readGlobalConfigValues() 
    {
        // client-side only
        displayPetHealthMode = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "displayPetHealthMode", 2, "Modes: (0 = Do not show health bar for pets | 1 = Always show health bar for pets | 2 = Only show health bar for pets if they are hurt).").getInt();
        displayPetName = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "displayPetName", true, "Shows pet name.").getBoolean(true);
        displayPetIcons = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "displayPetIcons", true, "Shows pet emotes.").getBoolean(true);
        animateTextures = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "animateTextures", true, "Animate the textures for entities that have animated textures.").getBoolean(true);
        useOriginalMoCreaturesTextures = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "useOriginalMoCreaturesTextures", false, "If true: uses the the original Mo' Creatures textures instead of the 16x Mo' Creatures textures. This works on the client side, meaning that you can set this to any mode you wish without affecting worlds/servers. Setting this to true also disables texture pack effects on Mo' Creatures entity and emoticon textures.").getBoolean(false);
        
        // general
        debug = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "debug", false, "Turns on verbose logging.").getBoolean(false);
        replaceVanillaCreepers = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "replaceVanillaCreepers", true, "This feature does not cause problems with the Et Futurum Requiem mod, or the Mutant Creatures mod. However, THIS MAY BE INCOMPATIBLE WITH OTHER MODS THAT DO THINGS WITH CREEPERS - If true: will replace vanilla creepers in worlds with own extension of creeper code. This is used to make creepers scared of kitty. If this is causing problems with other mods set this to false to turn it off.").getBoolean(true);
        enableMoCPetDeathMessages =  mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "enableMoCPetDeathMessages", true, "If true: the owner of a pet will recieve a message in chat when their pet dies, the message will also include how the pet died. No other players than the pet owner will get the message.").getBoolean(true); 
        particleFX = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "particleFX", 3, "Determines the amount of particles to be spawned for particles relating to Mo' Creatures.").getInt();
        
        itemID = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "itemID", 8772, "The starting ID used for MoCreatures items. Each item will increment this number by 1 for its ID.").getInt();
        wyvernDimension = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "wyvernLairDimensionID", -17).getInt();
        wyvernBiomeID = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "wyvernLairBiomeID", 207).getInt();
        
        maxTamed = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "maxTamedPerPlayer", 10, "Max tamed creatures a player can have. Requires enableStrictOwnership to be set to true.").getInt();
        maxOPTamed = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "maxTamedPerOP", 20, "Max tamed creatures an op can have. Requires enableStrictOwnership to be set to true.").getInt();
        enableStrictOwnership = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "enableStrictOwnership", false, "If true: only the owner of a pet can interact with the their pets. This also adds a limit to the amount of tamed creatures a player can have (see 'maxTamedPerPlayer' and 'maxTamedPerOP').").getBoolean(false);
        
        easyBreeding = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "easyBreeding", false, "Makes horse breeding simpler.").getBoolean(true);
        elephantBulldozer = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "elephantBulldozer", true, "Allows tamed elephants to break blocks in front when ramming with tusks equipped.").getBoolean(true);
        emptyHandMountAndPickUpOnly = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "emptyHandMountAndPickUpOnly", true, "If true: mountable creatures can only be mounted with an empty hand, creatures that can be picked up can only be picked up with an empty hand, if a player switches to an item while holding a creature in their hand, they will drop that creature.").getBoolean(true);
        enableRareGiantPandaVariant = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "enableRareGiantPandaVariant", false, "If true: pandas that spawn will have a 10% to be a giant panda.").getBoolean(false);
        ostrichEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "ostrichEggDropChance", 3, "A value of 3 means ostriches have a 3% chance to drop an egg.").getInt();
        staticBed = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "staticBed", true, "If true: kitty bed cannot be pushed.").getBoolean(true);
        staticLitter = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "staticLitter", true, "If true: kitty litter box cannot be pushed.").getBoolean(true);
        
        attackHorses = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "attackHorses", true, "Allows predator creatures to hunt horses.").getBoolean(true);
        specialHorsesFightBack = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "specialHorsesFightBack", true, "If true: tamed horses made from essences will fight back if attacked (except if attacked by thier owners), they will only fight back if not mounted.").getBoolean(true);
        specialPetsDefendOwner = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "specialPetsDefendOwner", true, "If true: tamed elephants and tamed predator creatures like big cats will defend their owner if their owner is attacked by an entity.").getBoolean(true);
        attackWolves = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "attackWolves", false, "Allows predator creatures to hunt minecraft wolves.").getBoolean(false);
        destroyDrops = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "destroyDrops", true, "If true: predator creatures will destroy the drops of their prey and heal themselves when they kill thier prey. Predator creatures will not destroy items dropped from player deaths, but they may eat raw meat items if the player drops them on death.").getBoolean(true);
        rareItemDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "rareItemDropChance", 25, "A value of 25 means Horses/Ostriches/Scorpions/etc. have a 25% chance to drop a rare item such as a heart of darkness, unicorn horn, ect. when killed. Raise the value if you want higher drop rates.").getInt();
        slimyInsectsAndJellyfishDropSlimeballs = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "slimyInsectsAndJellyfishDropSlimeballs", false, "If true: maggots, snails, and jellyfish will drop slimeballs on death.").getBoolean(false);
        wyvernEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "wyvernEggDropChance", 10, "A value of 10 means wyverns have a 10% chance to drop an egg.").getInt();
        motherWyvernEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "motherWyvernEggDropChance", 33, "A value of 33 means mother wyverns have a 33% chance to drop an egg.").getInt();

        attackDolphins = mocSettingsConfig.get(CATEGORY_MOC_WATER_CREATURE_GENERAL_SETTINGS, "attackDolphins", false, "Allows aquatic predator creatures to hunt dolphins.").getBoolean(false);        
        
        
        wraithsCanGoThroughWalls = mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "wraithsCanGoThroughWalls", false, "If true: wraiths and flame wraiths will be able to go through walls.").getBoolean(false);
        useRealisticHumanSoundsForWerewolf = mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "useRealisticHumanSoundsForWerewolf", false, "If true: uses realistic man hurt and man screaming sound for the human form of werewolves.").getBoolean(false);
        ogreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "ogreStrength", 2.5F, "The block destruction radius of green Ogres.").getString());
        caveOgreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "caveOgreStrength", 3.0F, "The block destruction radius of Cave Ogres.").getString());
        fireOgreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "fireOgreStrength", 2.0F, "The block destruction radius of Fire Ogres.").getString());
        ogreAttackRange = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "ogreAttackRange", 12, "The block radius where ogres 'smell' players.").getInt();
        fireOgreChance = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "fireOgreChance", 25, "The chance percentage of spawning Fire ogres in the Overworld.").getInt();
        caveOgreChance = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "caveOgreChance", 75, "The chance percentage of spawning Cave ogres at depth of 50 in the Overworld.").getInt();
        golemDestroyBlocks = mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "golemDestroyBlocks", true, "Allows Big Golems to break blocks.").getBoolean(true);
        
        replaceWitcheryWerewolfEntities = mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "replaceWitcheryWerewolfEntities", true, "Only has an effect if the Witchery mod is installed. Replaces the werewolves from the Witchery mod with Witchery integration werewolves from Mo' Creatures Legacy. This will also consequently disable the method of gaining lycanthropy from the wolf altar ritual. Instead, lycanthropy will only be gained through the Curse of the Wolf witch coven ritual or by contracting it from a werewolf player. Also, silver bolts will only work on werewolves if the player does not have any other types of bolts in their inventory.").getBoolean(true);
        useHumanModelAndMCAVillagerTexturesForWitcheryHumanWerewolfEntities = mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "useHumanModelAndMCAVillagerTexturesForWitcheryHumanWerewolfEntities", true, "Only has an effect if replaceWitcheryWerewolfEntities is true and both the Witchery mod and Minecraft Comes Alive mod are installed. Uses human model and Minecraft Comes Alive textures for the human forms of the Witchery wolfman entity. This does not affect Witchery werewolf villagers.").getBoolean(true);
        replaceWitcheryPlayerWolf = mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "replaceWitcheryPlayerWolf", false, "ONLY WORKS FOR SINGLE PLAYER  - causes visual glitches in the perspectives of players that have this setting turned on while on servers. THIS IS NOT COMPATIBLE WITH ANY OTHER MODS THAT CAN CHANGE THE PLAYER'S MAX HEALTH. Only has an effect if the Witchery mod is installed. Replaces the Witchery player wolf model with the Mo' Creatures dire wolf model for player werewolves that are level 7 and above. Werewolf and dire wolf respect will not work for level 7+ player wolf form if this is false.").getBoolean(false);
        replaceWitcheryPlayerWerewolf = mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "replaceWitcheryPlayerWerewolf", false, "ONLY WORKS FOR SINGLE PLAYER - causes visual glitches in the perspectives of players that have this setting turned on while on servers. THIS IS NOT COMPATIBLE WITH ANY OTHER MODS THAT CAN CHANGE THE PLAYER'S MAX HEALTH. Only has an effect if the Witchery mod is installed. Replaces the Witchery player werewolf model with the Mo' Creatures werewolf model. Werewolf and dire wolf respect will not work for player werewolf form if this is false.").getBoolean(false);
        colorForWitcheryPlayerWolfAndWerewolf = (short) mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "colorForWitcheryPlayerWolfAndWerewolf", 1, "Only has an effect if the Witchery mod is installed and if either replaceWitcheryPlayerWolf or replaceWitcheryPlayerWerewolf is true. This setting determines which textures are used for the player wolf and player werewolf forms. Colors: (0 = black | 1 = white | 2 = brown).").getInt();
        
        mocSettingsConfig.save();
        
        if (useOriginalMoCreaturesTextures)
        {
        	MODEL_TEXTURE = "textures/models_original/";
        	
        	MISC_TEXTURE = "textures/misc_original/";
        }
    }

    // Client stuff
    public void registerRenderers() {
        // Nothing here as this is the server side proxy
    }

    public void registerRenderInformation() {
        //client
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // TODO Auto-generated method stub
        return null;
    }

    /***
     * Dummy to know if is dedicated server or not
     * 
     * @return
     */
    public int getProxyMode() {
        return 1;
    }

    /**
     * Sets the name client side. Name is synchronized with datawatchers
     * 
     * @param player
     * @param mocAnimal
     */
    public void setName(EntityPlayer player, IMoCEntity mocAnimal) {
        //client side only
    }

    public void initGUI() {
        // client side only
    }
}