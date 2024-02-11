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
import drzhark.mocreatures.entity.monster.MoCEntityGolem;
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
    public boolean displayPetHealth;
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
    public boolean elephantBulldozer;
    
    public boolean replaceVanillaCreepers;
    
    //mod integration options
    public boolean replaceWitcheryWerewolves;
    public boolean replaceWitcheryPlayerWerewolf;

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

    public void VacuumFX(MoCEntityGolem entity) {}
    
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

    public boolean getDisplayPetHealth(EntityLiving entityLiving)
    {
    	if (displayPetHealth == true)
    	{
	        if (entityLiving.getHealth() == entityLiving.getMaxHealth())
	        {
	        	return false;
	        }
	        else
	        {
	        	return true;
	        }
    	}
    	else {return false;}
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
                MoCConfigCategory cat = mocEntityConfig.getCategory(entityData.getEntityName().toLowerCase());
                if (!cat.containsKey("frequency"))
                {
                    cat.put("frequency", new MoCProperty("frequency", Integer.toString(entityData.getFrequency()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setFrequency(Integer.parseInt(cat.get("frequency").value));
                }
                if (!cat.containsKey("minspawn"))
                {
                    cat.put("minspawn", new MoCProperty("minspawn", Integer.toString(entityData.getMinSpawn()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setMinSpawn(Integer.parseInt(cat.get("minspawn").value));
                }
                if (!cat.containsKey("maxspawn"))
                {
                    cat.put("maxspawn", new MoCProperty("maxspawn", Integer.toString(entityData.getMaxSpawn()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setMaxSpawn(Integer.parseInt(cat.get("maxspawn").value));
                }
                if (!cat.containsKey("maxchunk"))
                {
                    cat.put("maxchunk", new MoCProperty("maxchunk", Integer.toString(entityData.getMaxInChunk()), MoCProperty.Type.INTEGER));
                }
                else
                {
                    entityData.setMaxInChunk(Integer.parseInt(cat.get("maxchunk").value));
                }
                if (!cat.containsKey("canspawn"))
                {
                    cat.put("canspawn", new MoCProperty("canspawn", Boolean.toString(entityData.getCanSpawn()), MoCProperty.Type.BOOLEAN));
                }
                else
                {
                    entityData.setCanSpawn(Boolean.parseBoolean(cat.get("canspawn").value));
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
        displayPetHealth = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "displayPetHealth", true, "Shows health bar for pets if they are hurt.").getBoolean(true);
        displayPetName = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "displayPetName", true, "Shows pet name.").getBoolean(true);
        displayPetIcons = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "displayPetIcons", true, "Shows pet emotes.").getBoolean(true);
        animateTextures = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "animateTextures", true, "Animate the textures for entities that have animated textures.").getBoolean(true);
        useOriginalMoCreaturesTextures = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "useOriginalMoCreaturesTextures", false, "If true: uses the the original Mo' Creatures textures instead of the 16x Mo' Creatures textures. This works on the client side, meaning you can set this to any mode you wish without affecting worlds/servers. Setting this to true also disables resourcepack effects on Mo' Creatures entities and emoticons.").getBoolean(false);
        
        // general
        debug = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "debug", false, "Turns on verbose logging.").getBoolean(false);
        replaceVanillaCreepers = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "replaceVanillaCreepers", true, "THIS MAY BE INCOMPATIBLE WITH OTHER MODS THAT DO THINGS WITH CREEPERS - If true: will replace vanilla creepers in worlds with own extension of creeper code. This is used to make creepers scared of kitty. If this is causing problems with other mods set this to false to turn it off.").getBoolean(true);
        enableMoCPetDeathMessages =  mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "enableMoCPetDeathMessages", true, "If true: the owner of a pet will recieve a message in chat when their pet dies, the message will also include how the pet died. No other players than the owner will get the message.").getBoolean(true); 
        particleFX = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "particleFX", 3).getInt();
        
        itemID = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "itemID", 8772, "The starting ID used for MoCreatures items. Each item will increment this number by 1 for its ID.").getInt();
        wyvernDimension = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "wyvernLairDimensionID", -17).getInt();
        wyvernBiomeID = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "wyvernLairBiomeID", 207).getInt();
        
        maxTamed = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "maxTamedPerPlayer", 10, "Max tamed creatures a player can have. Requires enableStrictOwnership to be set to true.").getInt();
        maxOPTamed = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "maxTamedPerOP", 20, "Max tamed creatures an op can have. Requires enableStrictOwnership to be set to true.").getInt();
        enableStrictOwnership = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "enableStrictOwnership", false, "If true: only the owner of a pet can interact with the them. This also adds a limit to the amount of tamed creatures a player can have (see 'maxTamedPerPlayer' and 'maxTamedPerOP').").getBoolean(false);
        
        easyBreeding = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "easyBreeding", false, "Makes horse breeding simpler.").getBoolean(true);
        elephantBulldozer = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "elephantBulldozer", true, "Allows tamed elephants to break logs and leaves when ramming.").getBoolean(true);
        ostrichEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "ostrichEggDropChance", 3, "A value of 3 means ostriches have a 3% chance to drop an egg.").getInt();
        staticBed = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "staticBed", true, "If true: kitty bed cannot be pushed.").getBoolean(true);
        staticLitter = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "staticLitter", true, "If true: kitty litter box cannot be pushed.").getBoolean(true);
        
        attackHorses = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "attackHorses", true, "Allows predator creatures to hunt horses.").getBoolean(true);
        specialHorsesFightBack = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "specialHorsesFightBack", true, "If true: tamed horses made from essences will fight back if attacked (except if attacked by players) when not mounted.").getBoolean(true);
        specialPetsDefendOwner = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "specialPetsDefendOwner", true, "If true: tamed elephants and tamed predator creatures like big cats will defend their owner if their owner is attacked by an entity.").getBoolean(true);
        attackWolves = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "attackWolves", false, "Allows predator creatures to hunt wolves.").getBoolean(false);
        destroyDrops = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "destroyDrops", true, "If true: predator creatures will destroy the drops of their prey and heal themselves when they kill thier prey. Predator creatures will not destroy items dropped from player deaths.").getBoolean(true);
        rareItemDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "rareItemDropChance", 25, "A value of 25 means Horses/Ostriches/Scorpions/etc. have a 25% chance to drop a rare item such as a heart of darkness, unicorn horn, ect. when killed. Raise the value if you want higher drop rates.").getInt();
        wyvernEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "wyvernEggDropChance", 10, "A value of 10 means wyverns have a 10% chance to drop an egg.").getInt();
        motherWyvernEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "motherWyvernEggDropChance", 33, "A value of 33 means mother wyverns have a 33% chance to drop an egg.").getInt();

        attackDolphins = mocSettingsConfig.get(CATEGORY_MOC_WATER_CREATURE_GENERAL_SETTINGS, "attackDolphins", false, "Allows aquatic predator creatures to hunt dolphins.").getBoolean(false);        
        
        ogreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "ogreStrength", 2.5F, "The block destruction radius of green Ogres.").getString());
        caveOgreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "caveOgreStrength", 3.0F, "The block destruction radius of Cave Ogres.").getString());
        fireOgreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "fireOgreStrength", 2.0F, "The block destruction radius of Fire Ogres.").getString());
        ogreAttackRange = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "ogreAttackRange", 12, "The block radius where ogres 'smell' players.").getInt();
        fireOgreChance = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "fireOgreChance", 25, "The chance percentage of spawning Fire ogres in the Overworld.").getInt();
        caveOgreChance = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "caveOgreChance", 75, "The chance percentage of spawning Cave ogres at depth of 50 in the Overworld.").getInt();
        golemDestroyBlocks = mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "golemDestroyBlocks", true, "Allows Big Golems to break blocks.").getBoolean(true);
        
        replaceWitcheryWerewolves = mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "replaceWitcheryWerewolves", true, "ONLY HAS AN EFFECT IF THE WITCHERY MOD IS INSTALLED. Replaces the werewolves from the Witchery mod with Witchery integration werewolves from Mo' Creatures Legacy. This will also consequently disable the method of gaining lycanthropy from the wolf altar ritual. Instead, lycanthropy will only be gained through the Curse of the Wolf witch coven ritual.").getBoolean(true);
        replaceWitcheryPlayerWerewolf = mocSettingsConfig.get(CATEGORY_MOC_MOD_INTEGRATION_SETTINGS, "replaceWitcheryPlayerWerewolf", true, "ONLY HAS AN EFFECT IF THE WITCHERY MOD IS INSTALLED. THIS IS NOT COMPATIBLE WITH ANY OTHER MODS THAT CAN PERMANENTLY CHANGE THE PLAYER'S MAX HEALTH. Replaces the Witchery player werewolf model with the Mo' Creatures werewolf model.").getBoolean(true);
        
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