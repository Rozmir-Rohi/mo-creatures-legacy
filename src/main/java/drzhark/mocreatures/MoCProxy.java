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
    public boolean enableResetOwnership;
    public boolean elephantBulldozer;
    
    public boolean replaceVanillaCreepers;

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
    public int WyvernDimension;
    public int WyvernBiomeID;

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
        this.readGlobalConfigValues();
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
        this.readGlobalConfigValues();
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
    
    public void hammerFX(EntityPlayer entityplayer) {}

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

    public boolean getDisplayPetHealth(EntityLiving entityliving)
    {
    	if (displayPetHealth == true)
    	{
	        if (entityliving.getHealth() == entityliving.getMaxHealth())
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
        // general
        itemID = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "ItemID", 8772, "The starting ID used for MoCreatures items. Each item will increment this number by 1 for its ID.").getInt();
        debug = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "debug", false, "Turns on verbose logging.").getBoolean(false);
        maxTamed = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "maxTamedPerPlayer", 10, "Max tamed creatures a player can have. Requires enableStrictOwnership to be set to true.").getInt();
        maxOPTamed = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "maxTamedPerOP", 20, "Max tamed creatures an op can have. Requires enableStrictOwnership to be set to true.").getInt();
        enableStrictOwnership = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "enableStrictOwnership", false, "If true: only the owner of a pet can interact with the them. Assigns player as owner for each creature they tame.").getBoolean(false);
        enableResetOwnership = mocSettingsConfig.get(CATEGORY_OWNERSHIP_SETTINGS, "enableResetOwnerScroll", false, "Allows players to remove a tamed creatures owner essentially untaming it.").getBoolean(false);
        easyBreeding = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "EasyBreeding", false, "Makes horse breeding simpler.").getBoolean(true);
        elephantBulldozer = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "ElephantBulldozer", true, "Allows tamed elephants to break logs and leaves when ramming.").getBoolean(true);
        ostrichEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "OstrichEggDropChance", 3, "A value of 3 means ostriches have a 3% chance to drop an egg.").getInt();
        staticBed = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "StaticBed", true, "If true: kitty bed cannot be pushed.").getBoolean(true);
        staticLitter = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "StaticLitter", true, "If true: kitty litter box cannot be pushed.").getBoolean(true);
        particleFX = mocSettingsConfig.get(CATEGORY_MOC_GENERAL_SETTINGS, "particleFX", 3).getInt();
        attackDolphins = mocSettingsConfig.get(CATEGORY_MOC_WATER_CREATURE_GENERAL_SETTINGS, "AttackDolphins", false, "Allows aquatic predator creatures to hunt dolphins.").getBoolean(false);
        attackHorses = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "AttackHorses", true, "Allows predator creatures to hunt horses.").getBoolean(true);
        specialHorsesFightBack = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "specialHorsesFightBack", true, "If true: tamed horses made from essences will fight back if attacked (except if attacked by players) when not mounted.").getBoolean(true);
        specialPetsDefendOwner = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "specialPetsDefendOwner", true, "If true: tamed elephants and tamed predator creatures like big cats will defend their owner if their owner is attacked by an entity.").getBoolean(true);
        enableMoCPetDeathMessages =  mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "enableMoCPetDeathMessages", true, "If true: the owner of a pet will recieve a message in chat when their pet dies, the message will also include how the pet died. No other players than the owner will get the message.").getBoolean(true); 
        attackWolves = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "AttackWolves", false, "Allows predator creatures to hunt wolves.").getBoolean(false);
        destroyDrops = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "DestroyDrops", true, "If true: predator creatures will destroy the drops of their prey and heal themselves when they kill thier prey. Predator creatures will not destroy items dropped from player deaths.").getBoolean(true);
        rareItemDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "RareItemDropChance", 25, "A value of 25 means Horses/Ostriches/Scorpions/etc. have a 25% chance to drop a rare item such as a heart of darkness, unicorn horn, ect. when killed. Raise the value if you want higher drop rates.").getInt();
        wyvernEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "WyvernEggDropChance", 10, "A value of 10 means wyverns have a 10% chance to drop an egg.").getInt();
        motherWyvernEggDropChance = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "MotherWyvernEggDropChance", 33, "A value of 33 means mother wyverns have a 33% chance to drop an egg.").getInt();

        ogreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "OgreStrength", 2.5F, "The block destruction radius of green Ogres.").getString());
        caveOgreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "CaveOgreStrength", 3.0F, "The block destruction radius of Cave Ogres.").getString());
        fireOgreStrength = Float.parseFloat(mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "FireOgreStrength", 2.0F, "The block destruction radius of Fire Ogres.").getString());
        ogreAttackRange = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "OgreAttackRange", 12, "The block radius where ogres 'smell' players.").getInt();
        fireOgreChance = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "FireOgreChance", 25, "The chance percentage of spawning Fire ogres in the Overworld.").getInt();
        caveOgreChance = (short) mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "CaveOgreChance", 75, "The chance percentage of spawning Cave ogres at depth of 50 in the Overworld.").getInt();
        golemDestroyBlocks = mocSettingsConfig.get(CATEGORY_MOC_MONSTER_GENERAL_SETTINGS, "golemDestroyBlocks", true, "Allows Big Golems to break blocks.").getBoolean(true);
        WyvernDimension = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "WyvernLairDimensionID", -17).getInt();
        WyvernBiomeID = mocSettingsConfig.get(CATEGORY_MOC_ID_SETTINGS, "WyvernLairBiomeID", 207).getInt();
        replaceVanillaCreepers = mocSettingsConfig.get(CATEGORY_MOC_CREATURE_GENERAL_SETTINGS, "ReplaceVanillaCreepers", true, "THIS MAY BE INCOMPATIBLE WITH OTHER MODS THAT DO THINGS WITH CREEPERS - If true: will replace vanilla creepers in worlds with own extension of creeper code. This is used to make creepers scared of kitty. If this is causing problems with other mods set this to false to turn it off.").getBoolean(true);
        mocSettingsConfig.save();
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
     * @param mocanimal
     */
    public void setName(EntityPlayer player, IMoCEntity mocanimal) {
        //client side only
    }

    public void initGUI() {
        // client side only
    }
}