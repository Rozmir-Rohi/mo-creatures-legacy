package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.inventory.MoCAnimalChest;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import drzhark.mocreatures.network.message.MoCMessageHeart;
import drzhark.mocreatures.network.message.MoCMessageVanish;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

public class MoCEntityHorse extends MoCEntityTameableAnimal {
    private int gestationTime;
    private int countEating;
    private int textureCounter;
    private int flickerCounter;
    public int shuffleCounter;
    public int wingFlapCounter;

    private float transparencyFloat = 0.2F;

    public MoCAnimalChest localHorseChest;
    public boolean hasEatenBreedingItem;

    private boolean hasReproduced;
    private int nightmareFireTrailCounter;

    public ItemStack localItemstack;

    public int mouthCounter;
    public int standCounter;
    public int tailCounter;
    public int vanishCounter;
    public int sprintCounter;
    public int transformType;
    public int transformCounter;
    
    private int forwardMovementCounterForWalkingSoundEffect;
    
    public boolean isJumpKeyDown;
	private int horseJumpPowerCounter;
	private float horseJumpPower;
	
	static final int JUMP_COUNTER_MARKER_INDICATING_THAT_JUMP_HAS_BEEN_EXECUTED = -100;

    public MoCEntityHorse(World world)
    {
        super(world);
        setSize(1.4F, 1.6F);
        gestationTime = 0;
        hasEatenBreedingItem = false;
        nightmareFireTrailCounter = 0;
        
        setMoCAge(50);
        setChestedHorse(false);
        roper = null;
        stepHeight = 1.0F;

        if (MoCreatures.isServer())
        {
            if (rand.nextInt(5) == 0)
            {
                setAdult(false);
            }
            else
            {
                setAdult(true);
            }
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // isRideable - 0 false 1 true
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // isChestedHorse - 0 false 1 true
        dataWatcher.addObject(24, Byte.valueOf((byte) 0)); // Eating - 0 false 1 true
        dataWatcher.addObject(25, Integer.valueOf(0)); // armor 0 by default, 1 metal, 2 gold, 3 diamond, 4 crystaline
        dataWatcher.addObject(26, Byte.valueOf((byte) 0)); // Bred - 0 false 1 true
    }
   
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
    	Entity entityThatAttackedThisCreature = damageSource.getEntity();
        
		float vanillaMinecraftHorseArmorProtection = 3.7F * getArmorType(); 
		
		damageTaken *= (1-(vanillaMinecraftHorseArmorProtection * 0.04F)); //final damage taken after applying armor values. The function uses same damage reduction value as vanilla minecraft.
		
		if (damageTaken < 0F) {damageTaken = 0F;}
		
		
		if (
				super.attackEntityFrom(damageSource, damageTaken)
				&& (entityThatAttackedThisCreature != null)
				&& canThisHorseFightBackAgainstTheAttacker(entityThatAttackedThisCreature)
			)
		{
		    entityToAttack = entityThatAttackedThisCreature;
		    return true;
		}
		
		return super.attackEntityFrom(damageSource, damageTaken);
    }
    
    private boolean canThisHorseFightBackAgainstTheAttacker(Entity entityAttacker)
    {
    	return 
    			(
					MoCreatures.proxy.specialHorsesFightBack
					&& entityAttacker != null
		        	&& getIsAdult()
		        	&& riddenByEntity == null
		        	&& !(entityAttacker instanceof EntityPlayer && (entityAttacker.getCommandSenderName().equals(getOwnerName())))
		        	&& !(entityAttacker instanceof MoCEntityHorse)
		        	&& (
		        			(getType() > 20 && getType() < 26) // ghost or undead
		        			|| (getType() > 25 && getType() < 30) // skeleton horse
		        			|| (getType() >= 30 && getType() < 40) //bat horse, nightmare horse, pegasus or unicorn
		        			|| (getType() >= 40 && getType() < 60) // black pegasus and fairies
		        		)
		        );	
    }
    
    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {
        if (attackTime <= 0 && distanceToEntity < 2.5F && entity.boundingBox.maxY > boundingBox.minY && entity.boundingBox.minY < boundingBox.maxY)
        	
        {
            attackTime = 20;
            stand();
            openMouth();
            MoCTools.playCustomSound(this, getMadSound(), worldObj);
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), calculateAttackDamage());
        }
    }
    
    private float calculateAttackDamage()
    {	
    	int horseType = getType();
    	
    	if ((horseType > 20 && horseType < 26) || (horseType > 25 && horseType < 30)) // ghost, undead, and skeleton
    	{return 2;}
    
        if (horseType >= 30 && horseType < 40 && horseType != 36 && horseType != 39) // magic horses except pegasus and unicorn
        {return 2;} 
        
        if (horseType == 36 || horseType == 39) //pure pegasus or pure unicorn
        {return 4;}
        
        if (horseType >= 40 && horseType < 60) // dark pegasus and fairy horses
        {return 6;}
        
        else {return 2;}
    }    

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentBiome = MoCTools.biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        MoCTools.biomeName(worldObj, xCoordinate, yCoordinate, zCoordinate);

        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SAVANNA) && !(currentBiome.biomeName.toLowerCase().contains("outback")))
        {
        	setType(60);// zebra
        }
        
        return true;
    }

    /**
     * returns one of the RGB color codes
     * 
     * @param particleColour
     *            : 1 will return the Red component, 2 will return the Green and
     *            3 the blue
     * @param typeInt
     *            : which set of colors to inquiry about, corresponds with the
     *            horse types.
     * @return
     */
    public float getColourForFX(int particleColour, int typeInt)
    {
        if (typeInt == 48) // yellow
        {
            if (particleColour == 1) { return (float) 179 / 256; }
            if (particleColour == 2) { return (float) 160 / 256; }
            return (float) 22 / 256;
        }
        
        if (typeInt == 49) // purple
        {
            if (particleColour == 1) { return (float) 147 / 256; }
            if (particleColour == 2) { return (float) 90 / 256; }
            return (float) 195 / 256;
        }

        if (typeInt == 51) // blue
        {
            if (particleColour == 1) { return (float) 30 / 256; }
            if (particleColour == 2) { return (float) 144 / 256; }
            return (float) 255 / 256;
        }
        if (typeInt == 52) // pink
        {
            if (particleColour == 1) { return (float) 255 / 256; }
            if (particleColour == 2) { return (float) 105 / 256; }
            return (float) 180 / 256;
        }

        if (typeInt == 53) // lightgreen
        {
            if (particleColour == 1) { return (float) 188 / 256; }
            if (particleColour == 2) { return (float) 238 / 256; }
            return (float) 104 / 256;
        }
        
        if (typeInt == 54) // black fairy
        {
            if (particleColour == 1) { return (float) 110 / 256; }
            if (particleColour == 2) { return (float) 123 / 256; }
            return (float) 139 / 256;
        }
        
        if (typeInt == 55) // red fairy
        {
            if (particleColour == 1) { return (float) 194 / 256; }
            if (particleColour == 2) { return (float) 29 / 256; }
            return (float) 34 / 256;
        }
        
        if (typeInt == 56) // dark blue fairy
        {
            if (particleColour == 1) { return (float) 63 / 256; }
            if (particleColour == 2) { return (float) 45 / 256; }
            return (float) 255 / 256;
        }
        
        if (typeInt == 57) // cyan
        {
            if (particleColour == 1) { return (float) 69 / 256; }
            if (particleColour == 2) { return (float) 146 / 256; }
            return (float) 145 / 256;
        }

        if (typeInt == 58) // green
        {
            if (particleColour == 1) { return (float) 90 / 256; }
            if (particleColour == 2) { return (float) 136 / 256; }
            return (float) 43 / 256;
        }
        
        if (typeInt == 59) // orange
        {
            if (particleColour == 1) { return (float) 218 / 256; }
            if (particleColour == 2) { return (float) 40 / 256; }
            return (float) 0 / 256;
        }
        
        if (typeInt > 22 && typeInt < 26) // green for undeads
        {
            if (particleColour == 1) { return (float) 60 / 256; }
            if (particleColour == 2) { return (float) 179 / 256; }
            return (float) 112 / 256;

        }
        if (typeInt == 40) // dark red for black pegasus
        {
            if (particleColour == 1) { return (float) 139 / 256; }
            if (particleColour == 2) { return 0F; }
            return 0F;

        }

        // by default will return clear gold
        if (particleColour == 1) { return (float) 255 / 256; }
        if (particleColour == 2) { return (float) 236 / 256; }
        return (float) 139 / 256;
    }

    /**
     * Called to vanish a Horse without FX
     */
    public void disappearHorse()
    {
        isDead = true;
    }

    private void drinkingHorse()
    {
        openMouth();
        MoCTools.playCustomSound(this, "drinking", worldObj);
    }

    /**
     * Drops the current armor if the horse has one
     */
    @Override
	public void dropArmor()
    {
        if (MoCreatures.isServer())
        {
            int armorType = getArmorType();
            
            Item horseArmorToDrop = Items.iron_horse_armor; //default item as placeholder
            
            if (armorType == 1) {horseArmorToDrop = Items.iron_horse_armor;}
            if (armorType == 2) {horseArmorToDrop = Items.golden_horse_armor;}
            if (armorType == 3) {horseArmorToDrop = Items.diamond_horse_armor;}
            if (armorType == 4) {horseArmorToDrop = MoCreatures.horseArmorCrystal;}
            
            if (armorType != 0)
            {
                MoCTools.playCustomSound(this, "armoroff", worldObj);
                
                EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(horseArmorToDrop, 1));
                entityItem.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(entityItem);
                
                setArmorType((byte) 0);
            }
        }
    }

    /**
     * Drops a chest block if the horse is bagged
     */
    public void dropBags()
    {
        if (!isBagger() || !getIsChestedHorse() || !MoCreatures.isServer()) { return; }

        EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Blocks.chest, 1));
        float f3 = 0.05F;
        entityItem.motionX = (float) worldObj.rand.nextGaussian() * f3;
        entityItem.motionY = ((float) worldObj.rand.nextGaussian() * f3) + 0.2F;
        entityItem.motionZ = (float) worldObj.rand.nextGaussian() * f3;
        worldObj.spawnEntityInWorld(entityItem);
        setChestedHorse(false);
    }

    private void eatingHorse()
    {
        openMouth();
        MoCTools.playCustomSound(this, "eating", worldObj);
    }

    @Override
    protected void fall(float fallDistance)
    {
        if (isFlyer() || isFloater()) { return; }

        float adjustedFallDistance = (float) (Math.ceil(fallDistance - 3F)/2F);
        
        if (MoCreatures.isServer() && (adjustedFallDistance > 0))
        {
            if (getType() >= 10)
            {
                adjustedFallDistance /= 2;
            }
            if (adjustedFallDistance > 1F)
            {
                attackEntityFrom(DamageSource.fall, adjustedFallDistance);
            }
            if ((riddenByEntity != null) && (adjustedFallDistance > 1F))
            {
                riddenByEntity.attackEntityFrom(DamageSource.fall, adjustedFallDistance);
            }

            Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.20000000298023221D - prevRotationPitch), MathHelper.floor_double(posZ));
            if (block != Blocks.air)
            {
                SoundType stepSound = block.stepSound;
                playSound(stepSound.getStepResourcePath(), stepSound.getVolume() * 0.5F, stepSound.getPitch() * 0.75F);
            }
        }
    }

    @Override
    public byte getArmorType()
    {
        return (byte) dataWatcher.getWatchableObjectInt(25);
    }

    public int getInventorySize()
    {
        if (getType() == 40)
        {
            return 18;
        }
        else if (getType() > 64) { return 27; }
        return 9;
    }

    public boolean getIsChestedHorse()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    protected MoCEntityHorse getClosestMotherHorse(Entity entity, double range)
    {
        double d1 = -1D;
        MoCEntityHorse closestEntityHorseNearby = null;
        List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(range, range, range));
        for (int index = 0; index < listOfEntitiesNearby.size(); index++)
        {
            Entity entityNearby = (Entity) listOfEntitiesNearby.get(index);
            if ((!(entityNearby instanceof MoCEntityHorse)) || ((entityNearby instanceof MoCEntityHorse) && !((MoCEntityHorse) entityNearby).getHasBred()))
            {
                continue;
            }

            double distanceToEntityNearby = entityNearby.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((range < 0.0D) || (distanceToEntityNearby < (range * range))) && ((d1 == -1D) || (distanceToEntityNearby < d1)))
            {
                d1 = distanceToEntityNearby;
                closestEntityHorseNearby = (MoCEntityHorse) entityNearby;
            }
        }

        return closestEntityHorseNearby;
    }

    @Override
    public double getCustomJump()
    {
        double horseJump = 0.4D;
        int horseType = getType();
        
        if (horseType < 6) // tier 1
        {
            horseJump = 0.4;
        }
        else if (horseType > 5 && horseType < 11) // tier 2
        {
            horseJump = 0.45D;
        }
        else if (horseType > 10 && horseType < 16) // tier 3
        {
            horseJump = 0.5D;
        }
        else if (horseType > 15 && horseType < 21) // tier 4
        {
            horseJump = 0.55D;
        }
        else if (horseType > 20 && horseType < 26) // ghost and undead
        {
            horseJump = 0.45D;
        }
        else if (horseType > 25 && horseType < 30) // skeleton
        {
            horseJump = 0.5D;
        }
        else if (horseType >= 30 && horseType < 40) // magics
        {
            horseJump = 0.55D;
        }
        else if (horseType >= 40 && horseType < 60) // black pegasus and fairies
        {
            horseJump = 0.6D;
        }
        else if (horseType >= 60) // donkeys - zebras and the like
        {
            horseJump = 0.45D;
        }
        
        if (doesHaveHorn() && !isFairyHorse()) //unicorns
        {
        	horseJump = 2.5D; //unicorns need a higher jump strength to bypass the constant deceleration from it's slow fall mechanism
        }
        
        
        return horseJump;
    }

    @Override
    public double getCustomSpeed() //controls both land and flying speed
    {
    	//NOTE: speed values affect the following animals differently: land only | floater | flyer
    	// land only will get more speed, this is followed by the floater. The slowest is flyer.
    	// this is because flyer deals with more deceleration than land only.
    	// Whereas floater deals with more deceleration than flyer.
    	
    	//Here are some rough maths equations to convert these horseSpeed values into blocks per second:
    	//NOTE: These equations work as long as they are not implemented in the actual entity code. If they are implemented, they will end up changing the deceleration values in the parent entity class causing these equations to become inaccurate.
    	
    		//Land Only Speed in Blocks per Second = -8.0227*horseSpeed^3 + 13.668*horseSpeed^2 + 3.1135*horseSpeed + 0.2193
    		
    		//Floater Speed in Blocks per Second = 9.3689*horseSpeed^3 - 8.718*horseSpeed^2 + 7.0036*horseSpeed - 0.0349
    		
    		//Flyer Speed in Blocks per Second = 0.0531*EXP(5.5317*horseSpeed)   <- NOTE: The output of this equation maxes out at around 36.1 . Any higher outputs cause the entity to accelerate to infinity.
    	
    	
        double horseSpeed = 0.8D;  //default horse speed if no other criteria is met
        
        int horseType = getType();
        
        if (horseType < 6) // tier 1
        {
            horseSpeed = 0.9;
        }
        else if (horseType > 5 && horseType < 11) // tier 2
        {
            horseSpeed = 1.0D;
        }
        else if (horseType > 10 && horseType < 16) // tier 3
        {
            horseSpeed = 1.1D;
        }
        else if (horseType > 15 && horseType < 21) // tier 4
        {
            horseSpeed = 1.2D;
        }

        else if ((horseType > 20 && horseType < 26) && !isGhostHorse() && horseType != 25) //zombie horses except zombie pegasus
        {
            horseSpeed = 1.2D;
        }
        else if (horseType == 21) //normal ghost horse
        {
        	horseSpeed = 1.37;
        }
        else if (horseType == 21) //flying ghost horse
        {
        	horseSpeed = 1.25;
        }
        else if ((horseType > 25 && horseType < 30) && horseType != 28) //all skeleton horses except skeleton pegasus
        {
            horseSpeed = 1.2D;
        }
        else if (horseType == 32) // bat horse
        {
            horseSpeed = 1.2D;
        }
        else if(horseType == 38) // nightmare horse
        {
            horseSpeed = 1.2D;	
        }
        else if (((doesHaveHorn() && !isFairyHorse()) || isPegasus()) && horseType != 40) //all unicorns and all pegasus except dark pegasus
        {
        	horseSpeed = 1.25;
        }
        else if (horseType == 40) //dark pegasus
        {
        	horseSpeed = 1.28;
        }
        else if (horseType > 40 && horseType < 60) // fairies
        {
            horseSpeed = 1.37D;
        }
        else if (horseType == 60 || horseType == 61) // zebras and zorse
        {
            horseSpeed = 1.1D;
        }
        else if (horseType == 65) // donkeys
        {
            horseSpeed = 0.8D;
        }
        else if (horseType > 65) // mule and zorky
        {
            horseSpeed = 1.0D;
        }
        
        
        if (!isFlyer()) //Makes all horses run faster when whipped except flying horses. Don't make flying horse run faster when whipped as they become too fast
        {
	        if (sprintCounter > 0 && sprintCounter < 150)
	        {
	        	horseSpeed *= 1.3D; //this is the vanilla sprint multiplier value
	        	return horseSpeed;
	        }
        	else if (doesHaveChargeAbility() && sprintCounter > 150) //horses with special charge abilities become tired from sprinting
        	{
        		horseSpeed *= 0.9D;
        		return horseSpeed;
        	}
        }
        
        
        return horseSpeed;
    }

    @Override
    protected String getDeathSound()
    {
        openMouth();
        if (isUndead()) { return "mocreatures:horsedyingundead"; }
        if (isGhostHorse()) { return "mocreatures:horsedyingghost"; }
        if (getType() == 60 || getType() == 61) { return "mocreatures:zebrahurt"; }
        if (getType() >= 65 && getType() <= 67) { return "mocreatures:donkeydying"; }
        return "mocreatures:horsedying";
    }

    @Override
    public boolean getShouldDisplayName()
    {
        if (isGhostHorse() && getMoCAge() < 10) { return false; }

        return (getName() != null && !getName().equals(""));
    }

    @Override
    protected void dropFewItems(boolean hasEntityBeenHitByPlayer, int levelOfLootingEnchantmentUsedToKillThisEntity)
    {
        boolean canDropRareItem = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);
        
        int randomAmount = rand.nextInt(3);

        if (canDropRareItem && (getType() == 36 || (getType() >= 50 && getType() < 60))) // unicorn
        { dropItem(MoCreatures.unicornHorn, 1); }
        
        if (getType() == 39 || (getType() == 40))// pegasus and dark pegasus
        { dropItem(Items.feather, randomAmount); }
        
        if (getType() == 38 && canDropRareItem && worldObj.provider.isHellWorld) // nightmare
        { dropItem(MoCreatures.heartFire, 1); }
        
        if (getType() == 32 && canDropRareItem) // bat horse
        { dropItem(MoCreatures.heartDarkness, 1); }
        
        if (getType() == 26)// skeleton
        { dropItem(Items.bone, randomAmount); }
        
        if ((getType() == 23 || getType() == 24 || getType() == 25)) //undead horse
        {
            if (canDropRareItem) { dropItem(MoCreatures.heartundead, 1); }
            
            else {dropItem(Items.rotten_flesh, randomAmount);}
        }
        
        if (getType() == 21 || getType() == 22) //ghost horse
        { dropItem(Items.ghast_tear, randomAmount);}
        
        if(!isMagicHorse()
        		&& !doesHaveHorn() //not a unicorn or fairy horse
        		&& !(
        				(getType() == 23 || getType() == 24 || getType() == 25) //not an undead horse
        				|| (getType() == 26)// not a skeleton horse
        				|| (getType() == 21 || getType() == 22) //not a ghost horse
        		))
        {
        	if (!MoCreatures.isGregTech6Loaded)
        	{
        		if (MoCreatures.isFoodExpansionLoaded)
        		{
        			if (isBurning())
        			{
        				dropItem(GameRegistry.findItem("FoodExpansion", "ItemCookedHorseMeat"), randomAmount);
        			}
        			else {dropItem(GameRegistry.findItem("FoodExpansion", "ItemHorseMeat"), randomAmount);}
        		}
        		
        		else if (MoCreatures.isLotsOfFoodLoaded)
        		{
        			if (isBurning())
        			{
        				dropItem(GameRegistry.findItem("LotsOfFood", "chevalcuit"), randomAmount);
        			}
        			else {dropItem(GameRegistry.findItem("LotsOfFood", "chevalcru"), randomAmount);}
        		}
        	
        		else if (MoCreatures.isImprovingMinecraftLoaded)
        		{
        			if (isBurning())
        			{
        				dropItem(GameRegistry.findItem("imc", "item_cooked_horse"), randomAmount);
        			}
        			else {dropItem(GameRegistry.findItem("imc", "item_raw_horse"), randomAmount);}
        		}
        	}
        	
        	dropItem(Items.leather, randomAmount);
        }
    }

    public boolean getEating()
    {
        return (dataWatcher.getWatchableObjectByte(24) == 1);
    }

    public boolean getHasBred()
    {
        return (dataWatcher.getWatchableObjectByte(26) == 1);
    }

    public boolean getHasReproduced()
    {
        return hasReproduced;
    }

    @Override
    protected String getHurtSound()
    {
    	return getMadSound();
    }

    @Override
	public boolean getIsRideable()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    @Override
    protected String getLivingSound()
    {
        openMouth();
        if (rand.nextInt(10) == 0 && !isMovementCeased())
        {
            stand();
        }
        if (isUndead()) { return "mocreatures:horsegruntundead"; }
        if (isGhostHorse()) { return "mocreatures:horsegruntghost"; }
        if (getType() == 60 || getType() == 61) { return "mocreatures:zebragrunt"; }
        if (getType() >= 65 && getType() <= 67) { return "mocreatures:donkeygrunt"; }
        return "mocreatures:horsegrunt";
    }

    /**
     * sound played when an untamed mount buckles rider
     */
    @Override
    protected String getMadSound()
    {
    	openMouth();
        if (isFlyer() && riddenByEntity == null)
        {
            wingFlap();
        }
        else
        {
            if (rand.nextInt(3) == 0)
            {
                stand();
            }
        }
        if (isUndead()) { return "mocreatures:horsehurtundead"; }
        if (isGhostHorse()) { return "mocreatures:horsehurtghost"; }
        if (getType() == 60 || getType() == 61) { return "mocreatures:zebrahurt"; }
        if (getType() >= 65 && getType() <= 67) { return "mocreatures:donkeyhurt"; }
        
        return "mocreatures:horsehurt";
    }

    public double calculateMaxHealth()
    {
        double maximumHealth = 10.0D;;
        int horseType = getType();
        
        
        if (horseType < 6) // tier 1
        {
            maximumHealth = 15.0D;
        }
        else if (horseType > 5 && horseType < 11) // tier 2
        {
            maximumHealth = 20.0D;
        }
        else if (horseType > 10 && horseType < 16) // tier 3
        {
            maximumHealth = 25.0D;
        }
        else if (horseType > 15 && horseType < 21) // tier 4
        {
            maximumHealth = 25.0D;
        }

        else if (horseType > 20 && horseType < 26) // ghost and undead
        {
            maximumHealth = 25.0D;
        }
        else if (horseType > 25 && horseType < 30) // skeleton horses
        {
            maximumHealth = 15.0D;
        }
        else if (horseType >= 30 && horseType < 40) // magics
        {
            maximumHealth = 30.0D;
        }
        else if (horseType == 40) // black pegasus
        {
            maximumHealth = 40.0D;
        }
        else if (horseType > 40 && horseType < 60) // fairy horses
        {
            maximumHealth = 35.0D;
        }
        else if (horseType >= 60) // donkeys - zebras and the like
        {
            maximumHealth = 20.0D;
        }

        return maximumHealth;
    }

    /**
     * How difficult is the creature to be tamed? the Higher the number, the
     * more difficult
     */
    @Override
    public int getMaxTemper()
    {

        if (getType() == 60) { return 200; // zebras are harder to tame
        }
        return 100;
    }

    public int getNightmareFireTrailCounter()
    {
        return nightmareFireTrailCounter;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.8F;
    }

    @Override
    public int getTalkInterval()
    {
        return 400;
    }

    /**
     * Overridden for the dynamic nightmare texture.
     */
    @Override
    public ResourceLocation getTexture()
    {
        String tempTexture;

        switch (getType())
        {
	        case 1:
	            tempTexture = "horsewhite.png";
	            break;
	        case 2:
	            tempTexture = "horsecreamy.png";
	            break;
	        case 3:
	            tempTexture = "horsebrown.png";
	            break;
	        case 4:
	            tempTexture = "horsedarkbrown.png";
	            break;
	        case 5:
	            tempTexture = "horseblack.png";
	            break;
	        case 6:
	            tempTexture = "horsebrightcreamy.png";
	            break;
	        case 7:
	            tempTexture = "horsespeckled.png";
	            break;
	        case 8:
	            tempTexture = "horsepalebrown.png";
	            break;
	        case 9:
	            tempTexture = "horsegrey.png";
	            break;
	        case 11:
	            tempTexture = "horsepinto.png";
	            break;
	        case 12:
	            tempTexture = "horsebrightpinto.png";
	            break;
	        case 13:
	            tempTexture = "horsepalespeckles.png";
	            break;
	        case 16:
	            tempTexture = "horsespotted.png";
	            break;
	        case 17:
	            tempTexture = "horsecow.png";
	            break;
	        case 21:
	            tempTexture = "horseghost.png";
	            break;
	        case 22:
	            tempTexture = "horseghostb.png";
	            break;
	        case 23:
	            tempTexture = "horseundead.png";
	            break;
	        case 24:
	            tempTexture = "horseundeadunicorn.png";
	            break;
	        case 25:
	            tempTexture = "horseundeadpegasus.png";
	            break;
	        case 26:
	            tempTexture = "horseskeleton.png";
	            break;
	        case 27:
	            tempTexture = "horseunicornskeleton.png";
	            break;
	        case 28:
	            tempTexture = "horsepegasusskeleton.png";
	            break;
	        case 30:
	            tempTexture = "horsebug.png";
	            break;
	        case 32:
	            tempTexture = "horsebat.png";
	            break;
	        case 36:
	            tempTexture = "horseunicorn.png";
	            break;
	        case 38:
	            tempTexture = "horsenightmare.png";
	            break;
	        case 39:
	            tempTexture = "horsepegasus.png";
	            break;
	        case 40:
	            tempTexture = "horsedarkpegasus.png";
	            break;
	        case 48:
	            tempTexture = "horsefairyyellow.png";
	            break;
	        case 49:
	            tempTexture = "horsefairypurple.png";
	            break;
	        case 50:
	            tempTexture = "horsefairywhite.png";
	            break;
	        case 51:
	            tempTexture = "horsefairyblue.png";
	            break;
	        case 52:
	            tempTexture = "horsefairypink.png";
	            break;
	        case 53:
	            tempTexture = "horsefairylightgreen.png";
	            break;
	        case 54:
	            tempTexture = "horsefairyblack.png";
	            break;
	        case 55:
	            tempTexture = "horsefairyred.png";
	            break;
	        case 56:
	            tempTexture = "horsefairydarkblue.png";
	            break;
	        case 57:
	            tempTexture = "horsefairycyan.png";
	            break;
	        case 58:
	            tempTexture = "horsefairygreen.png";
	            break;
	        case 59:
	            tempTexture = "horsefairyorange.png";
	            break;
	        
	        case 60:
	            tempTexture = "horsezebra.png";
	            break;
	        case 61:
	            tempTexture = "horsezorse.png";
	            break;
	        case 65:
	            tempTexture = "horsedonkey.png";
	            break;
	        case 66:
	            tempTexture = "horsemule.png";
	            break;
	        case 67:
	            tempTexture = "horsezonky.png";
	            break;
	
	        default:
	            tempTexture = "horsebug.png";
        }

        if ((canWearRegularArmor() || isMagicHorse()) && getArmorType() > 0)
        {
            String armorTexture = "";
            if (getArmorType() == 1)
            {
                armorTexture = "metal.png";
            }
            if (getArmorType() == 2)
            {
                armorTexture = "gold.png";
            }
            if (getArmorType() == 3)
            {
                armorTexture = "diamond.png";
            }
            if (getArmorType() == 4)
            {
                armorTexture = "crystaline.png";
            }
            return MoCreatures.proxy.getTexture(tempTexture.replace(".png", armorTexture));
        }

        
        if (isUndead() && getType() < 26)
        {
            String baseTex = "horseundead";
            int max = 79;
            if (getType() == 25) // undead pegasus
            {
                baseTex = "horseundeadpegasus";
                // max = 79; //undead pegasus have an extra animation

            }
            if (getType() == 24)// undead unicorn
            {
                baseTex = "horseundeadunicorn";
                max = 69; // undead unicorn have an animation less
            }
            
            String iteratorTex = "1";
            if (MoCreatures.proxy.getAnimateTextures()) //undead pegasus and undead unicorn dynamic textures
            {
                if (rand.nextInt(3) == 0)
                {
                    textureCounter++;
                }
                if (textureCounter < 10)
                {
                    textureCounter = 10;
                }
                if (textureCounter > max)
                {
                    textureCounter = 10;
                }
                iteratorTex = "" + textureCounter;
                iteratorTex = iteratorTex.substring(0, 1);
            }
           
            String decayTex = "" + (getMoCAge() / 100);
            decayTex = decayTex.substring(0, 1);
            return MoCreatures.proxy.getTexture(baseTex + decayTex + iteratorTex + ".png");
        }
        
        // if animate textures is off, return plain textures
        if (!MoCreatures.proxy.getAnimateTextures()) { return MoCreatures.proxy.getTexture(tempTexture); }

        
        if (isNightmare()) //nightmare horse dynamic textures
        {
            if (rand.nextInt(3) == 0)  //animation speed
            {
                textureCounter++;
            }
            if (textureCounter < 10)
            {
                textureCounter = 10;
            }
            if (textureCounter > 59)
            {
                textureCounter = 10;
            }
            String NTA = "horsenightmare";
            String NTB = "" + textureCounter;
            NTB = NTB.substring(0, 1);
            String NTC = ".png";

            return MoCreatures.proxy.getTexture(NTA + NTB + NTC);
        }

        

        if (transformCounter != 0 && transformType != 0)
        {
            String newTexture = "horseundead.png";
            if (transformType == 23)
            {
                newTexture =  "horseundead.png";
            }
            if (transformType == 24)
            {
                newTexture = "horseundeadunicorn.png";
            }
            if (transformType == 25)
            {
                newTexture = "horseundeadpegasus.png";
            }
            if (transformType == 36)
            {
                newTexture = "horseunicorn.png";
            }
            if (transformType == 39)
            {
                newTexture = "horsepegasus.png";
            }
            if (transformType == 40)
            {
                newTexture = "horseblackpegasus.png";
            }
            
            if (transformType == 48)
            {
                newTexture = "horsefairyyellow.png";
            }
            if (transformType == 49)
            {
                newTexture = "horsefairypurple.png";
            }
            if (transformType == 50)
            {
                newTexture = "horsefairywhite.png";
            }
            if (transformType == 51)
            {
                newTexture = "horsefairyblue.png";
            }
            if (transformType == 52)
            {
                newTexture = "horsefairypink.png";
            }
            if (transformType == 53)
            {
                newTexture = "horsefairylightgreen.png";
            }
            if (transformType == 54)
            {
                newTexture = "horsefairyblack.png";
            }
            if (transformType == 55)
            {
                newTexture = "horsefairyred.png";
            }
            if (transformType == 56)
            {
                newTexture = "horsefairydarkblue.png";
            }
            
            if (transformType == 57)
            {
                newTexture = "horsefairycyan.png";
            }
            
            if (transformType == 58)
            {
                newTexture = "horsefairygreen.png";
            }
            
            if (transformType == 59)
            {
                newTexture = "horsefairyorange.png";
            }
            
            if (transformType == 32)
            {
                newTexture = "horsebat.png";
            }
            if (transformType == 38)
            {
                newTexture = "horsenightmare1.png";
            }
            if ((transformCounter % 5) == 0) { return MoCreatures.proxy.getTexture(newTexture); }
            if (transformCounter > 50 && (transformCounter % 3) == 0) { return MoCreatures.proxy.getTexture(newTexture); }

            if (transformCounter > 75 && (transformCounter % 4) == 0) { return MoCreatures.proxy.getTexture(newTexture); }
        }

        return MoCreatures.proxy.getTexture(tempTexture);

    }

    /**
     * New networked to fix SMP issues
     * 
     * @return
     */
    public byte getVanishC()
    {
        return (byte) vanishCounter;
    }

    /**
     * Breeding rules for the horses
     * 
     * @param entityhorse
     * @param entityhorse1
     * @return
     */
    //private int horseGenetics(MoCEntityHorse entityhorse, MoCEntityHorse entityhorse1)
    private int horseGenetics(int parentHorseTypeA, int parentHorseTypeB)
    {
        boolean shouldBecomeSterile = MoCreatures.proxy.hardHorseBreeding;
        //int typeA = entityhorse.getType();
        //int typeB = entityhorse1.getType();

        // identical horses have so spring
        if (parentHorseTypeA == parentHorseTypeB) { return parentHorseTypeA; }

        // zebras plus any horse
        if (parentHorseTypeA == 60 && parentHorseTypeB < 21 || parentHorseTypeB == 60 && parentHorseTypeA < 21) { return 61; // zorse
        }

        // donkey plus any horse
        if (parentHorseTypeA == 65 && parentHorseTypeB < 21 || parentHorseTypeB == 65 && parentHorseTypeA < 21) { return 66; // mule
        }

        // zebra plus donkey
        if (parentHorseTypeA == 60 && parentHorseTypeB == 65 || parentHorseTypeB == 60 && parentHorseTypeA == 65) { return 67; // zonky
        }

        if (parentHorseTypeA > 20 && parentHorseTypeB < 21 || parentHorseTypeB > 20 && parentHorseTypeA < 21) // rare horses plus  ordinary horse always returns ordinary horse
        {
            if (parentHorseTypeA < parentHorseTypeB) { return parentHorseTypeA; }
            return parentHorseTypeB;
        }

        // unicorn plus white pegasus (they will both vanish!)
        if (parentHorseTypeA == 36 && parentHorseTypeB == 39 || parentHorseTypeB == 36 && parentHorseTypeA == 39)
        {
            return 50; // white fairy
        }

        // rare horse mixture: produces a regular horse 1-5
        if (parentHorseTypeA > 20 && parentHorseTypeB > 20 && (parentHorseTypeA != parentHorseTypeB)) { return (rand.nextInt(5)) + 1; }

        // rest of cases will return either typeA, typeB or new mix
        int chanceInt = (rand.nextInt(4)) + 1;
        if (shouldBecomeSterile)
        {
            if (chanceInt == 1) // 25%
            {
                return parentHorseTypeA;
            }
            else if (chanceInt == 2) // 25%
            { return parentHorseTypeB; }
        }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 2) || (parentHorseTypeA == 2 && parentHorseTypeB == 1)) { return 6; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 3) || (parentHorseTypeA == 3 && parentHorseTypeB == 1)) { return 2; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 4) || (parentHorseTypeA == 4 && parentHorseTypeB == 1)) { return 7; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 5) || (parentHorseTypeA == 5 && parentHorseTypeB == 1)) { return 9; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 7) || (parentHorseTypeA == 7 && parentHorseTypeB == 1)) { return 12; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 8) || (parentHorseTypeA == 8 && parentHorseTypeB == 1)) { return 7; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 9) || (parentHorseTypeA == 9 && parentHorseTypeB == 1)) { return 13; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 11) || (parentHorseTypeA == 11 && parentHorseTypeB == 1)) { return 12; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 12) || (parentHorseTypeA == 12 && parentHorseTypeB == 1)) { return 13; }

        if ((parentHorseTypeA == 1 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 1)) { return 16; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 4) || (parentHorseTypeA == 4 && parentHorseTypeB == 2)) { return 3; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 5) || (parentHorseTypeA == 5 && parentHorseTypeB == 2)) { return 4; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 7) || (parentHorseTypeA == 7 && parentHorseTypeB == 2)) { return 8; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 8) || (parentHorseTypeA == 8 && parentHorseTypeB == 2)) { return 3; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 12) || (parentHorseTypeA == 12 && parentHorseTypeB == 2)) { return 6; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 2)) { return 13; }

        if ((parentHorseTypeA == 2 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 2)) { return 12; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 4) || (parentHorseTypeA == 4 && parentHorseTypeB == 3)) { return 8; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 5) || (parentHorseTypeA == 5 && parentHorseTypeB == 3)) { return 8; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 6) || (parentHorseTypeA == 6 && parentHorseTypeB == 3)) { return 2; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 7) || (parentHorseTypeA == 7 && parentHorseTypeB == 3)) { return 11; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 9) || (parentHorseTypeA == 9 && parentHorseTypeB == 3)) { return 8; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 12) || (parentHorseTypeA == 12 && parentHorseTypeB == 3)) { return 11; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 3)) { return 11; }

        if ((parentHorseTypeA == 3 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 3)) { return 11; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 6) || (parentHorseTypeA == 6 && parentHorseTypeB == 4)) { return 3; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 7) || (parentHorseTypeA == 7 && parentHorseTypeB == 4)) { return 8; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 9) || (parentHorseTypeA == 9 && parentHorseTypeB == 4)) { return 7; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 11) || (parentHorseTypeA == 11 && parentHorseTypeB == 4)) { return 7; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 12) || (parentHorseTypeA == 12 && parentHorseTypeB == 4)) { return 7; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 13) || (parentHorseTypeA == 13 && parentHorseTypeB == 4)) { return 7; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 4)) { return 13; }

        if ((parentHorseTypeA == 4 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 4)) { return 5; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 6) || (parentHorseTypeA == 6 && parentHorseTypeB == 5)) { return 4; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 7) || (parentHorseTypeA == 7 && parentHorseTypeB == 5)) { return 4; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 8) || (parentHorseTypeA == 8 && parentHorseTypeB == 5)) { return 4; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 11) || (parentHorseTypeA == 11 && parentHorseTypeB == 5)) { return 17; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 12) || (parentHorseTypeA == 12 && parentHorseTypeB == 5)) { return 13; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 13) || (parentHorseTypeA == 13 && parentHorseTypeB == 5)) { return 16; }

        if ((parentHorseTypeA == 5 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 5)) { return 17; }

        if ((parentHorseTypeA == 6 && parentHorseTypeB == 8) || (parentHorseTypeA == 8 && parentHorseTypeB == 6)) { return 2; }

        if ((parentHorseTypeA == 6 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 6)) { return 7; }

        if ((parentHorseTypeA == 7 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 7)) { return 13; }

        if ((parentHorseTypeA == 8 && parentHorseTypeB == 11) || (parentHorseTypeA == 11 && parentHorseTypeB == 8)) { return 7; }

        if ((parentHorseTypeA == 8 && parentHorseTypeB == 12) || (parentHorseTypeA == 12 && parentHorseTypeB == 8)) { return 7; }

        if ((parentHorseTypeA == 8 && parentHorseTypeB == 13) || (parentHorseTypeA == 13 && parentHorseTypeB == 8)) { return 7; }

        if ((parentHorseTypeA == 8 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 8)) { return 7; }

        if ((parentHorseTypeA == 8 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 8)) { return 7; }

        if ((parentHorseTypeA == 9 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 9)) { return 13; }

        if ((parentHorseTypeA == 11 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 11)) { return 13; }

        if ((parentHorseTypeA == 11 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 11)) { return 7; }

        if ((parentHorseTypeA == 12 && parentHorseTypeB == 16) || (parentHorseTypeA == 16 && parentHorseTypeB == 12)) { return 13; }

        if ((parentHorseTypeA == 13 && parentHorseTypeB == 17) || (parentHorseTypeA == 17 && parentHorseTypeB == 13)) { return 9; }

        return parentHorseTypeA; // breed is not in the table so it will return the first
                        // parent type
    }

      

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }
        
        int horseType = getType();
        
        if (horseType == 60 && !getIsTamed() && isZebraRunningAwayFromPlayer()) // zebra
        { return false; }
        
        ItemStack itemStack = entityPlayer.getHeldItem();
        EntityPlayer owner = worldObj.getPlayerEntityByName(getOwnerName());
        
        if (itemStack != null)
        {
        	Item item = itemStack.getItem();
        
	        if (!getIsRideable() && (item == Items.saddle) || (item == MoCreatures.craftedSaddle))
	        {
	            if (--itemStack.stackSize == 0)
	            {
	                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
	            }
	            setRideable(true);
	            return true;
	        }
	        
	        List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemStack);
	        
	        
	        if (interactIfThisHorseIsANormalHorseAndItemstackIsHealFood(entityPlayer, horseType, itemStack, item, oreDictionaryNameArray)) {return true;};
	        
	        if (interactIfThisHorseIsANormalHorseAndItemstackIsBreedingFood(entityPlayer, itemStack, item)) {return true;};
	        
	        if (getIsTamed())
	        {
	        	if (interactIfItemstackIsAmulet(entityPlayer, horseType, item)) {return true;};
	        	
		        if (interactIfItemstackIsHorseArmor(entityPlayer, itemStack, item)) {return true;};
		
		        if (interactIfItemstackIsEssenceOfDarkness(entityPlayer, horseType, itemStack, owner, item)) {return true;};
		        
		        if (interactIfItemstackisEssenceOfFire(entityPlayer, horseType, itemStack, owner, item)) {return true;};
		
		        if (interactIfItemstackIsEssenceOfLight(entityPlayer, horseType, itemStack, owner, item)) {return true;};
		        
		        if (interactIfItemstackIsEssenceOfUndead(entityPlayer, horseType, itemStack, owner, item)) {return true;};
		
		        if (item == Item.getItemFromBlock(Blocks.chest) && (isBagger()))
		        {
		            if (getIsChestedHorse()) { return false; }
		            if (--itemStack.stackSize == 0)
		            {
		                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		            }
		
		            setChestedHorse(true);
		            return true;
		        }
		        
		        if (interactIfThisHorseIsAZebraAndItemstackIsRecord(entityPlayer, horseType, item)) {return true;};
		        
		        if (interactIfThisHorseIsAFairyAndItemstackIsDye(entityPlayer, horseType, itemStack, item)) {return true;};
        	}
        }
        
        if (	//try to mount player on horse - THIS MUST TO BE AT THE VERY LAST OF THE INTERACT FUNCTION so that any interactable items are used first before the player mounts the horse
	        	(
	    			(MoCreatures.proxy.emptyHandMountAndPickUpOnly && itemStack == null)
	    			|| !(MoCreatures.proxy.emptyHandMountAndPickUpOnly)
	    		)
        	)
        {
    		if (entityPlayer.isSneaking() && getIsChestedHorse())
	        {
	            // if first time opening horse chest, we must initialize it
	            if (localHorseChest == null)
	            {
	                localHorseChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.HorseChest"), getInventorySize());// , new
	            }
	            // only open this chest on server side
	            if (!worldObj.isRemote)
	            {
	                entityPlayer.displayGUIChest(localHorseChest);
	            }
	            return true;
	        }
        	else if
        		(
        				!(entityPlayer.isSneaking()) && getIsRideable() && getIsAdult() && (riddenByEntity == null)
    	        	&& !(isFlyer() && entityPlayer.riddenByEntity != null) //stops players from riding a flying horse with a creature picked up or on their head. This fixes the flying speed glitch.
    	        )
	        {
	            entityPlayer.rotationYaw = rotationYaw;
	            entityPlayer.rotationPitch = rotationPitch;
	            setEating(false);
	            if (MoCreatures.isServer()) {entityPlayer.mountEntity(this);}
	            gestationTime = 0;
	            return true;
	        }
        }
        
        return false;
    }

	private boolean interactIfThisHorseIsANormalHorseAndItemstackIsBreedingFood(EntityPlayer entityPlayer, ItemStack itemStack, Item item)
	{
		if (
				item == Item.getItemFromBlock(Blocks.pumpkin)  //normal horse breeding items
				|| item == Items.mushroom_stew
				|| item == Items.cake
			)
		{
		    if (!getIsAdult() || isMagicHorse() || isUndead()) { return false; }
		    
		    if (item == Items.mushroom_stew)
		    {
		        if (--itemStack.stackSize == 0)
		        {
		            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.bowl));
		        }
		        else
		        {
		            entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.bowl));
		        }
		    }
		    else if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		    }
		    hasEatenBreedingItem = true;
		    heal(25);
		    eatingHorse();
		    return true;
		}
		return false;
	}

	private boolean interactIfThisHorseIsANormalHorseAndItemstackIsHealFood(EntityPlayer entityPlayer, int horseType, ItemStack itemStack, Item item, List<String> oreDictionaryNameArray)
	{
		if (
				!isUndead() && !isMagicHorse() && 
					( 
						//food items for normal horses
						item == Items.wheat
						|| item == MoCreatures.sugarLump
						|| item == Items.bread
						|| item == Items.apple
						|| item == Items.golden_apple
						|| item == MoCreatures.haystack
						|| oreDictionaryNameArray.size() > 0 && 
							(
								oreDictionaryNameArray.contains("listAllwheats") //GregTech6 wheat items
								|| oreDictionaryNameArray.contains("listAllgrain") //Palm's Harvest wheat items
								
								|| MoCreatures.isGregTech6Loaded &&
									(
										oreDictionaryNameArray.contains("itemGrass")
										|| oreDictionaryNameArray.contains("itemGrassDry")
										|| oreDictionaryNameArray.contains("cropGrain")
									)
							)
					)
			)
		{
			int temperIncrease = 0;
			int healAmount = 0;
			int ageIncrease = 0;

			if (
					item == Items.wheat 
					|| oreDictionaryNameArray.contains("listAllwheats")
					|| oreDictionaryNameArray.contains("itemGrass")
					|| oreDictionaryNameArray.contains("itemGrassDry")
					|| oreDictionaryNameArray.contains("cropGrain")
					|| oreDictionaryNameArray.contains("listAllgrain")
					
				)
			{
				temperIncrease = 25; healAmount = 5; ageIncrease = 1;
			}
			
			if (item == MoCreatures.sugarLump) {temperIncrease = 25; healAmount = 10; ageIncrease = 2;}
			if (item == Items.bread) {temperIncrease = 100; healAmount = 20; ageIncrease = 3;}
			if (item == Items.apple || item == Items.golden_apple) {temperIncrease = 0; healAmount = 25; ageIncrease = 1;}
			if (item == MoCreatures.haystack) {temperIncrease = 0; healAmount = 25; ageIncrease = 1;}


			if (--itemStack.stackSize == 0)
			{
				entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
			}


			if (MoCreatures.isServer())
		    {
				setTemper(getTemper() + temperIncrease);
		        if (getTemper() > getMaxTemper())
		        {
		        	setTemper(getMaxTemper() - 5);
		        }
		    }
			
			if ((item == MoCreatures.haystack) && !isMagicHorse() && !isUndead()) {setEating(true);} //eating haystack

			heal(healAmount);
				

			eatingHorse(); //play eating sound


		    if (!getIsAdult() && (getMoCAge() < 100))
		    {
		    	setMoCAge(getMoCAge() + ageIncrease);
		    }



			 if (MoCreatures.isServer() && !(getIsTamed()) && (item == Items.apple || item == Items.golden_apple))
		     {
				 MoCTools.tameWithName(entityPlayer, this);
				 
				 if (entityPlayer != null && (horseType > 5 && horseType < 11)) //tier 2
				 {
					 entityPlayer.addStat(MoCAchievements.tier2_horse, 1);
				 }
				 else if (entityPlayer != null && (horseType == 60)) //zebra
				 {
					 entityPlayer.addStat(MoCAchievements.zebra, 1);
				 }
		     }
			 
			 return true;
		}
		return false;
	}

	private boolean interactIfThisHorseIsAZebraAndItemstackIsRecord(EntityPlayer entityPlayer, int horseType, Item item)
	{
		// zebra easter egg
		if (
				horseType == 60
				&& (
						item == Items.record_11
						|| item == Items.record_13
						|| item == Items.record_cat
						|| item == Items.record_chirp
						|| item == Items.record_far
						|| item == Items.record_mall
						|| item == Items.record_mellohi
						|| item == Items.record_stal
						|| item == Items.record_strad
						|| item == Items.record_ward
					)
			)
		{
		    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		    if (MoCreatures.isServer())
		    {
		        EntityItem entityItem1 = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(MoCreatures.recordShuffle, 1));
		        entityItem1.delayBeforeCanPickup = 20;
		        worldObj.spawnEntityInWorld(entityItem1);
		    }
		    eatingHorse();
		    return true;
		}
		return false;
	}

	private boolean interactIfThisHorseIsAFairyAndItemstackIsDye(EntityPlayer entityPlayer, int horseType, ItemStack itemStack, Item item)
	{
		if ((horseType == 50) && (item == Items.dye)) //set color of fairy horse based on dye player is interacting with
		{

		    int colorInt = BlockColored.func_150031_c(itemStack.getItemDamage());
		    switch (colorInt)
		    {
		    case 1: //orange
		        transform(59);
		        break;
		    case 2: //magenta TODO
		        //transform(46);
		        break;
		    case 3: //light blue
		        transform(51);
		        break;
		    case 4: //yellow
		        transform(48);
		        break;
		    case 5: //light green
		        transform(53);
		        break;
		    case 6: //pink
		        transform(52);
		        break;
		    case 7: //gray TODO
		        //transform(50);
		        break;
		    case 8: //light gray TODO
		        //transform(50);
		        break;
		    case 9: //cyan
		        transform(57);
		        break;
		    case 10: //purple
		        transform(49);
		        break;
		    case 11: //dark blue
		        transform(56);
		        break;
		    case 12: //brown TODO
		        //transform(50);
		        break;
		    case 13: //green
		        transform(58);
		        break;
		    case 14: //red
		        transform(55);
		        break;
		    case 15: //black
		        transform(54);
		        break;
		    
		    }
		    
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		    }
		    eatingHorse();
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackIsAmulet(EntityPlayer entityPlayer, int horseType, Item item)
	{
		if (isAmuletHorse())
		{
		    if ((horseType == 26 || horseType == 27 || horseType == 28) && item == MoCreatures.amuletBone)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		        vanishHorse();
		        return true;
		    }

		    if ((horseType > 47 && horseType < 60) && item == MoCreatures.amuletFairy)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		        vanishHorse();
		        return true;
		    }

		    if ((horseType == 39 || horseType == 40) && (item == MoCreatures.amuletPegasus))
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		        vanishHorse();
		        return true;
		    }

		    if ((horseType == 21 || horseType == 22) && (item == MoCreatures.amuletGhost))
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		        vanishHorse();
		        return true;
		    }

		}
		return false;
	}

	private boolean interactIfItemstackIsEssenceOfLight(EntityPlayer entityPlayer, int horseType, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		if (item == MoCreatures.essenceLight)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }

		    if (isMagicHorse())
		    {
		        if (getIsAdult() && getHealth() == getMaxHealth())
		        {
		            hasEatenBreedingItem = true;
		        }
		        setHealth(getMaxHealth());
		    }

		    if (isNightmare())
		    {
		        // unicorn
		        transform(36);
		        
			 	if (owner != null) {owner.addStat(MoCAchievements.unicorn, 1);};
		    }
		    if (horseType == 32 && posY > 128D) // bathorse to pegasus
		    {
		        // pegasus
		        transform(39);
		        
		        if (owner != null) {owner.addStat(MoCAchievements.pegasus, 1);};
		    }
		    // to return undead horses to pristine conditions
		    if (isUndead() && getIsAdult() && MoCreatures.isServer())
		    {
		        setMoCAge(10);
		        if (horseType > 26)
		        {
		            setType(horseType - 3);
		        }
		    }
		    drinkingHorse();
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackIsEssenceOfDarkness(EntityPlayer entityPlayer, int horseType, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		// transform to dark pegasus
		if (item == MoCreatures.essenceDarkness)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }

		    if (horseType == 32)
		    {
		        if (getIsAdult() && getHealth() == getMaxHealth())
		        {
		            hasEatenBreedingItem = true;
		        }
		        setHealth(getMaxHealth());
		    }

		    if (horseType == 61)
		    {
		        //bat horse
		        transform(32);
			 	if (owner != null) {owner.addStat(MoCAchievements.bat_horse, 1);};
		    }

		    if (horseType == 39) // pegasus to darkpegasus
		    {
		        //darkpegasus
		        transform(40);
		        isImmuneToFire = true;
		        
			 	if (owner != null) {owner.addStat(MoCAchievements.dark_pegasus, 1);};
		    }
		    drinkingHorse();
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackisEssenceOfFire(EntityPlayer entityPlayer, int horseType, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		// to transform to nightmares: only pure breeds
		if (item == MoCreatures.essenceFire)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }

		    if (isNightmare())
		    {
		        if (getIsAdult() && getHealth() == getMaxHealth())
		        {
		            hasEatenBreedingItem = true;
		        }
		        setHealth(getMaxHealth());

		    }
		    if (horseType == 61)
		    {
		        //nightmare
		        transform(38);
		        isImmuneToFire = true;
		        
			 	if (owner != null) {owner.addStat(MoCAchievements.nightmare_horse, 1);};
		    }
		    
		    drinkingHorse();
		    return true;
		}
		
		return false;
	}

	private boolean interactIfItemstackIsEssenceOfUndead(EntityPlayer entityPlayer, int horseType, ItemStack itemStack, EntityPlayer owner, Item item)
	{
		// transform to undead, or heal undead horse
		if (item == MoCreatures.essenceUndead)
		{
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		    }
		    else
		    {
		        entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		    }

		    if (isUndead() || isGhostHorse())
		    {
		        setHealth(getMaxHealth());

		    }
		    
		    else
		    {
			 	if (owner != null) {owner.addStat(MoCAchievements.undead_horse, 1);};
		    }
		    
		    

		    // pegasus, dark pegasus, or bat horse
		    if (horseType == 39 || horseType == 32 || horseType == 40)
		    {

		        // transformType = 25; //undead pegasus
		        transform(25);

		    }
		    else if (horseType == 36 || (horseType > 47 && horseType < 60)) // unicorn or fairies
		    {

		        // transformType = 24; //undead unicorn
		        transform(24);
		    }
		    else if (horseType < 21 || horseType == 60 || horseType == 61) // regular horses or zebras
		    {

		        // transformType = 23; //undead
		        transform(23);
		    }

		    drinkingHorse();
		    return true;
		}
		return false;
	}

	private boolean interactIfItemstackIsHorseArmor(EntityPlayer entityPlayer, ItemStack itemStack, Item item) {
		if (
				canWearRegularArmor() &&
				(
					item == Items.iron_horse_armor
					|| item == Items.golden_horse_armor
					|| item == Items.diamond_horse_armor
				)
			)
		{
		    if (getArmorType() == 0) {MoCTools.playCustomSound(this, "armorput", worldObj);}
		    
		    dropArmor();
		    
		    byte regularArmorType = 0;
		    
		    if (item == Items.iron_horse_armor) {regularArmorType = 1;}
		    if (item == Items.golden_horse_armor) {regularArmorType = 2;}
			if (item == Items.diamond_horse_armor) {regularArmorType = 3;}
		    
		    setArmorType(regularArmorType);
		    
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		    }
		    
		    return true;
		}

		if ((item == MoCreatures.horseArmorCrystal) && isMagicHorse())
		{
		    if (getArmorType() == 0) {MoCTools.playCustomSound(this, "armorput", worldObj);}
		    
		    dropArmor();
		    
		    setArmorType((byte) 4);
		    
		    if (--itemStack.stackSize == 0)
		    {
		        entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
		    }
		    return true;
		}
		return false;
	}

    /**
     * Can this horse be trapped in a special amulet?
     */
    public boolean isAmuletHorse()
    {

        return (
        			(getType() >= 48 && getType() < 60)
	        		|| getType() == 40
	        		|| getType() == 39
	        		|| getType() == 21
	        		|| getType() == 22
	        		|| getType() == 26
	        		|| getType() == 27
	        		|| getType() == 28
        		);
    }

    /**
     * Can wear regular armor
     */
    public boolean canWearRegularArmor()
    {
        return (getType() < 21);
    }

    /**
     * able to carry bags
     * 
     * @return
     */
    public boolean isBagger()
    {
        return (getType() == 66) // mule
                || (getType() == 65) // donkey
                || (getType() == 67) // zonkey
                || (getType() == 39) // pegasus
                || (getType() == 40) // black pegasus
                || (getType() == 25) // undead pegasus
                || (getType() == 28) // skeleton pegasus
                || isFairyHorse()
        ;
    }

    /**
     * Falls slowly
     */
    public boolean isFloater()
    {
        return getType() == 36 // unicorn
                || getType() == 27 // skeleton unicorn
                || getType() == 24 // undead unicorn
                || getType() == 22; // not winged ghost

    }

    @Override
    public boolean isFlyer()
    {
        return
        	(
        		getType() == 32 // bat horse
        		|| isPegasus()
                || isFairyHorse()
                || getType() == 21 // ghost winged
            );
                
                
    }
    

    /**
     * Is this a ghost horse?
     * 
     * @return
     */
    public boolean isGhostHorse()
    {

        return getType() == 21 || getType() == 22;
    }

    /**
     * Can wear magic armor
     */
    public boolean isMagicHorse()
    {
        return

        getType() == 39
        || getType() == 36
        || getType() == 32
        || getType() == 40
        || isFairyHorse()
        || getType() == 21
        || getType() == 22;
    }
    
    /**
     * Is this a horse a type of pegasus?
     * 
     * @return
     */
    private boolean isPegasus()
    {
    	return 
    		(
    			getType() == 39 // pegasus
                || getType() == 40 // dark pegasus
                || getType() == 25 // undead pegasus
                || getType() == 28// skeleton pegasus
    		);
    }

    /**
     * Is this a fairy horse?
     * 
     * @return
     */
	private boolean isFairyHorse()
	{
		return getType() >= 45 && getType() < 60;
	}
    
    @Override
    protected void func_145780_a(int xCoord, int yCoord_, int zCoord, Block blockThatThisEntityIsWalkingOn)
    {
    	if(!isGhostHorse())
    	{
	        Block.SoundType soundType = blockThatThisEntityIsWalkingOn.stepSound;
	
	        if (worldObj.getBlock(xCoord, yCoord_ + 1, zCoord) == Blocks.snow_layer)
	        {
	            soundType = Blocks.snow_layer.stepSound;
	        }
	
	        if (!blockThatThisEntityIsWalkingOn.getMaterial().isLiquid())
	        {
	            if (riddenByEntity != null)
	            {
	                ++forwardMovementCounterForWalkingSoundEffect;
	
	                if (forwardMovementCounterForWalkingSoundEffect > 5 && forwardMovementCounterForWalkingSoundEffect % 3 == 0)
	                {
	                    playSound("mob.horse.gallop", soundType.getVolume() * 0.15F, soundType.getPitch());
	
	                    if (!isUndead() && rand.nextInt(10) == 0)
	                    {
	                        playSound("mob.horse.breathe", soundType.getVolume() * 0.6F, soundType.getPitch());
	                    }
	                }
	                else if (forwardMovementCounterForWalkingSoundEffect <= 5)
	                {
	                    playSound("mob.horse.wood", soundType.getVolume() * 0.15F, soundType.getPitch());
	                }
	            }
	            else if (soundType == Block.soundTypeWood)
	            {
	                playSound("mob.horse.wood", soundType.getVolume() * 0.15F, soundType.getPitch());
	            }
	            else
	            {
	                playSound("mob.horse.soft", soundType.getVolume() * 0.15F, soundType.getPitch());
	            }
	        }
    	}
    }
    
    @Override
	public void moveEntityWithHeading(float strafeMovement, float forwardMovement)
    {
        if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase)
        {
            float movementForward = ((EntityLivingBase)riddenByEntity).moveForward;

            if (movementForward <= 0.0F)
            {
                forwardMovementCounterForWalkingSoundEffect = 0;
            }
        }
        super.moveEntityWithHeading(strafeMovement, forwardMovement);
    }

    @Override
    protected boolean isMovementCeased()
    {
        return 
        		(
	        		getEating()
	        		|| (riddenByEntity != null)
	        		|| standCounter != 0
	        		|| shuffleCounter != 0
	        		|| getVanishC() != 0
        		);
    }

    /**
     * Is this a Nightmare horse?
     */
    public boolean isNightmare()
    {
        return getType() == 38;
    }

    /**
     * Rare horses that can be transformed into Nightmares or Bathorses or give
     * ghost horses on death
     */
    public boolean isPureBreed()
    {

        return (getType() > 10 && getType() < 21);
    }

    /**
     * Mobs don't attack you if you're riding one of these. They won't reproduce
     * either
     * 
     * @return
     */
    public boolean isUndead()
    {
        return 
        	(
        		isGhostHorse()
        		|| (getType() == 23) || (getType() == 24) || (getType() == 25) || (getType() == 26) // skeleton
                || (getType() == 27) // skeleton unicorn
                || (getType() == 28) // skeleton pegasus
        	);
    }

    /**
     * Has an unicorn? to render it and buckle entities!
     * 
     * @return
     */
    public boolean doesHaveHorn()
    {

        return (
        			getType() == 36 //pure
        			|| isFairyHorse()
        			|| getType() == 27 //skeleton
        			|| getType() == 24 //zombie
        		);
    }
    
    
    /**
     * Has an unicorn? to render it and buckle entities!
     * 
     * @return
     */
    public boolean doesHaveChargeAbility()
    {
        return (
        			getType() == 38 //nightmare horse
        			|| doesHaveHorn() //unicorns and fairies
        		);
    }

    public boolean isZebraRunningAwayFromPlayer()
    {
        boolean flag = false;
        EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 8D);
        if (entityPlayer != null)
        {
            flag = true;
            if (entityPlayer.ridingEntity != null && entityPlayer.ridingEntity instanceof MoCEntityHorse)
            {
                MoCEntityHorse horseThatPlayerIsRiding = (MoCEntityHorse) entityPlayer.ridingEntity;
                if (
                		horseThatPlayerIsRiding.getType() == 16
                		|| horseThatPlayerIsRiding.getType() == 17
                		|| horseThatPlayerIsRiding.getType() == 60
                		|| horseThatPlayerIsRiding.getType() == 61
                	)
                {
                    flag = false;
                }
            }

        }
        if (flag)
        {
            MoCTools.runAway(this, entityPlayer);
        }
        return flag;
    }

    public void LavaFX()
    {
        MoCreatures.proxy.LavaFX(this);
    }

    public void MaterializeFX()
    {
        MoCreatures.proxy.MaterializeFX(this);
    }

    private void moveTail()
    {
        tailCounter = 1;
    }

    @Override
    public int nameYOffset()
    {
        if (getIsAdult())
        {
            return -80;
        }
        else
        {
            return (-5 - getMoCAge());
        }
    }

    private void checkShufflingForTimeOut()
    {
    	if (shuffleCounter > 0)
    	{
	    	TileEntityJukebox jukeboxNearby = MoCTools.nearJukeBoxRecord(this, 8D);
    	
	    	if (shuffleCounter > 1000 && jukeboxNearby != null) //supposed to take the record out of the jukebox, but instead it stops the jukebox
        	{
	    		BlockJukebox blockjukebox = (BlockJukebox) worldObj.getBlock(jukeboxNearby.xCoord, jukeboxNearby.yCoord, jukeboxNearby.zCoord);
                
	    		if (blockjukebox != null)
                {
                    blockjukebox.func_149925_e(worldObj, jukeboxNearby.xCoord, jukeboxNearby.yCoord, jukeboxNearby.zCoord);
                }
        	}
	    	
	    	if (shuffleCounter > 1000 || jukeboxNearby == null) //reset the shuffleCounter
        	{
        		shuffleCounter = 0;
        	}
    	}
    }

    // changed to public since we need to send this info to server
    public void executeNightmareHorseFireTrail()
    {
        int xCoordinate = Math.round((float) posX);
        int yCoordinate = Math.round((float) boundingBox.minY);
        int zCoordinate = Math.round((float) posZ);
        Block block = worldObj.getBlock(xCoordinate, yCoordinate, zCoordinate); 
        int metadata = worldObj.getBlockMetadata(xCoordinate, yCoordinate, zCoordinate);
        BlockEvent.BreakEvent event = null;
        if (!worldObj.isRemote)
        {
            event = new BlockEvent.BreakEvent(xCoordinate, yCoordinate, zCoordinate, worldObj, block, metadata, FakePlayerFactory.get(DimensionManager.getWorld(worldObj.provider.dimensionId), MoCreatures.MOC_FAKE_PLAYER));
        }
        if (event != null && !event.isCanceled())
        {
            worldObj.setBlock(xCoordinate, yCoordinate, zCoordinate, Blocks.fire, 0, 3);//MC1.5
            EntityPlayer entityPlayer = (EntityPlayer) riddenByEntity;
            if ((entityPlayer != null) && (entityPlayer.isBurning()))
            {
                entityPlayer.extinguish();
            }
            setNightmareFireTrailCounter(getNightmareFireTrailCounter() - 1);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        super.onDeath(damageSource);
        if (MoCreatures.isServer())
        {
            if ((rand.nextInt(10) == 0) && (getType() == 23) || (getType() == 24) || (getType() == 25))
            {
                MoCTools.spawnMaggots(worldObj, this);
            }

            if (getIsTamed() && (isMagicHorse() || isPureBreed()) && !isGhostHorse() && rand.nextInt(4) == 0)
            {
                MoCEntityHorse newGhostHorse = new MoCEntityHorse(worldObj);
                newGhostHorse.setPosition(posX, posY, posZ);
                worldObj.spawnEntityInWorld(newGhostHorse);
                MoCTools.playCustomSound(this, "appearmagic", worldObj);

                EntityPlayer owner = worldObj.getPlayerEntityByName(getOwnerName());
                
                newGhostHorse.setOwner(getOwnerName());
                newGhostHorse.setTamed(true);
                

                if (owner != null)
                {
                    MoCTools.tameWithName(owner, newGhostHorse);
                    owner.addStat(MoCAchievements.ghost_horse, 1);
                }
                

                newGhostHorse.setAdult(false);
                newGhostHorse.setMoCAge(1);
                
                int ghostHorseType = 22;
                
                if (isFlyer()) {ghostHorseType = 21;}
                
                newGhostHorse.setType(ghostHorseType);
            }
            
        }
    }

    @Override
    public void onLivingUpdate()
    {
    	if (
    			isHorsePurelyMadeFromEssenseOfLight()
    			&& (getHealth() < getMaxHealth())
    			&& rand.nextInt(100) == 0
    		)
    	{
    		heal(1);
    	}
    	
    	if (entityToAttack != null && entityToAttack == riddenByEntity)
    	{
    		if (!(riddenByEntity instanceof EntityPlayer && riddenByEntity.getCommandSenderName().equals(getOwnerName()))) //if not the owner of this entity
    		{
    			riddenByEntity.mountEntity(null); //forcefully make the entity that is riding this entity dismount
    		}
    	}
    	
        /**
         * slow falling
         */
        if (isFlyer() || isFloater())
        {
            if (!onGround && (motionY < 0.0D))
            {
                motionY *= 0.6D;
            }
        }

        if ((jumpPending))
        {
            if (isFlyer() && wingFlapCounter == 0)
            {
                MoCTools.playCustomSound(this, "wingflap", worldObj);
            }
            wingFlapCounter = 1;
        }

        if (rand.nextInt(200) == 0)
        {
            moveTail();
        }

        if ((getType() == 38) && (rand.nextInt(50) == 0) && !MoCreatures.isServer())
        {
            LavaFX();
        }

        if ((getType() == 36) && isOnAir() && !MoCreatures.isServer())
        {
            StarFX();
        }

        if (isOnAir() && isFlyer() && rand.nextInt(30) == 0)
        {
            wingFlapCounter = 1;
        }

        if ((rand.nextInt(300) == 0) && (deathTime == 0))
        {
            heal(1);
        }
        

        if (isUndead() && (getType() < 26) && getIsAdult() && (rand.nextInt(20) == 0))
        {
            if (MoCreatures.isServer())
            {
                if (rand.nextInt(16) == 0)
                {
                    setMoCAge(getMoCAge() + 1);
                }
                if (getMoCAge() >= 399)
                {
                    setType(getType() + 3);
                }
            }
            else
            {
                UndeadFX();
            }

        }

        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
        	
            if (!getEating() && !getIsTamed() && rand.nextInt(300) == 0)
            {
                setEating(true);
            }

            if (getEating() && ++countEating > 50 && !getIsTamed())
            {
                countEating = 0;
                setEating(false);
            }

            if ((getType() == 38) && (riddenByEntity != null) && (getNightmareFireTrailCounter() > 0) && (rand.nextInt(2) == 0))
            {
                executeNightmareHorseFireTrail();
            }

            /**
             * zebras on the run!
             */
            if (getType() == 60 && !getIsTamed())
            {
                isZebraRunningAwayFromPlayer();

            }

            /**
             * foal following mommy!
             */
            if (!getIsAdult() && (rand.nextInt(200) == 0))
            {
                setMoCAge(getMoCAge() + 1);
                if (getMoCAge() >= 100)
                {
                    setAdult(true);
                    setBred(false);
                    MoCEntityHorse mommyHorse = getClosestMotherHorse(this, 16D);
                    if (mommyHorse != null)
                    {
                        mommyHorse.setBred(false);
                    }
                }
            }

            // TODO test in MP or move out of this !isRemote
            /**
             * Horse Ramming
             */
            if ((sprintCounter > 0 && sprintCounter < 150) && doesHaveHorn() && (riddenByEntity != null))
            {
            	
                MoCTools.buckleMobs(this, 2F, 2D, worldObj);
            }

            if (isFlyer() && rand.nextInt(100) == 0 && !isMovementCeased() && !getEating())
            {
                wingFlap();
            }

            if (getHasBred() && !getIsAdult() && (roper == null) && !getEating())
            {

                MoCEntityHorse mommy = getClosestMotherHorse(this, 16D);
                if ((mommy != null) && (MoCTools.getSqDistanceTo(mommy, posX, posY, posZ) > 4D))
                {
                    PathEntity pathEntity = worldObj.getPathEntityToEntity(this, mommy, 16F, true, false, false, true);
                    setPathToEntity(pathEntity);
                }

            }

            if (!isReadyforParenting(this)) { return; }

            int i = 0;

            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8D, 3D, 8D));
            for (int j = 0; j < list.size(); j++)
            {
                Entity entity = (Entity) list.get(j);
                if (entity instanceof MoCEntityHorse || entity instanceof EntityHorse)
                {
                    i++;
                }
            }

            if (i > 1) { return; }
            List list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4D, 2D, 4D));
            
            for (int k = 0; k < list1.size(); k++)
            {
                Entity horsemate = (Entity) list1.get(k);
                boolean flag = (horsemate instanceof EntityHorse);
                if (!(horsemate instanceof MoCEntityHorse || flag) || (horsemate == this))
                {
                    continue;
                }
                
                if (!isReadyforParenting(this)) return;
                
                if (!flag)
                {
                    if (!isReadyforParenting((MoCEntityHorse)horsemate))
                    {    
                        return;
                    }
                }
                
                if (rand.nextInt(100) == 0)
                {
                    gestationTime++;
                }

                if (gestationTime % 3 == 0)
                {
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(getEntityId()), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                }

                if (gestationTime <= 50)
                {
                    continue;
                }
                MoCEntityHorse babyHorse = new MoCEntityHorse(worldObj);
                babyHorse.setPosition(posX, posY, posZ);
                worldObj.spawnEntityInWorld(babyHorse);
                hasEatenBreedingItem = false;
                gestationTime = 0;
                setBred(true);
                
                int horsemateType;// = 0;
                if (flag)
                {
                    horsemateType = translateVanillaHorseType((EntityHorse) horsemate);   
                    if (horsemateType == -1) return;
                }else
                {
                    horsemateType = ((MoCEntityHorse)horsemate).getType();
                    ((MoCEntityHorse)horsemate).hasEatenBreedingItem = false;
                    ((MoCEntityHorse)horsemate).gestationTime = 0;
                }
                int type = horseGenetics(getType(), horsemateType);
                
                
                babyHorse.setOwner(getOwnerName());
                babyHorse.setTamed(true);
                babyHorse.setBred(true);
                babyHorse.setAdult(false);
                EntityPlayer owner = worldObj.getPlayerEntityByName(getOwnerName());
                
                if (owner != null)
                {
                    MoCTools.tameWithName(owner, babyHorse);
                }
               
                
                if ((type > 10 && type < 16) && owner != null) //tier 3
                {
                	owner.addStat(MoCAchievements.tier3_horse, 1);
                }
                
                else if ((type > 15 && type < 21) && owner != null) //tier 4
                {
                	owner.addStat(MoCAchievements.tier4_horse, 1);
                }
                
                
                else if (type == 50) // fairy horse!
                {
                    MoCTools.playCustomSound(this, "appearmagic", worldObj);
                    
                    if (owner != null) {owner.addStat(MoCAchievements.fairy_horse, 1);}
                    
                    if (!flag) 
                        {
                        ((MoCEntityHorse)horsemate).disappearHorse();
                        }
                    disappearHorse();
                }
                
                else if ((type == 61) && owner != null) //zorse
                {
                	owner.addStat(MoCAchievements.zorse, 1);
                }
                
                babyHorse.setType(type);
                
                babyHorse.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth()); //set max health to the new type
                babyHorse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed to new type
                
                break;
            }
        }

    }

    private boolean isHorsePurelyMadeFromEssenseOfLight()
    {
    	int horseType = getType();
    	
		return
			(
				horseType == 36  //pure unicorn 
				|| horseType == 39 //pure pegasus
				|| isFairyHorse() //fairy horse
			);
	}

	/**
     * Obtains the 'Type' of vanilla horse for inbreeding with MoC Horses
     * @param horse
     * @return
     */
    private int translateVanillaHorseType(EntityHorse horse)
    {
        if (horse.getHorseType() == 1)
        {
            return 65; // donkey
        }
        if (horse.getHorseType() == 0)
        {
            switch((byte)horse.getHorseVariant())
            {
                case 0: //white
                    return 1;
                case 1: //creamy
                    return 2;
                case 3: //brown
                    return 3;
                case 4: //black
                    return 5;
                case 5: //gray
                    return 9;
                case 6: //dark brown
                    return 4;
                default:
                    return 3;
            }
            
        }
        return -1;
    }
    
    
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        
        if (
        		riddenByEntity == null
        		&& (
        				horseJumpPowerCounter != 0
        				|| horseJumpPower != 0
        			)
        	)
        {
        	horseJumpPower = 0;
    		horseJumpPowerCounter = 0;
        }
        
        if(!isFlyer())
        {
	        if (
	        		isJumpKeyDown
	        		&& getCustomIsOnGround() //need to use this custom method because onGround field bugs out when player disconnects and reconnects while riding horse
	        		&& horseJumpPowerCounter != JUMP_COUNTER_MARKER_INDICATING_THAT_JUMP_HAS_BEEN_EXECUTED
	        	)
	        {
	        	++horseJumpPowerCounter;
	
	            if (horseJumpPowerCounter < 10)
	            {
	                horseJumpPower = horseJumpPowerCounter * 0.1F;
	            }
	            else
	            {
	                horseJumpPower = 0.8F + 2.0F / (horseJumpPowerCounter - 9) * 0.1F;
	            }
	        }
	        
	        else if (
	        			!isJumpKeyDown
	        			&& horseJumpPower > 0
	        		)
	        {
	        	if (horseJumpPowerCounter != JUMP_COUNTER_MARKER_INDICATING_THAT_JUMP_HAS_BEEN_EXECUTED)
	        	{
	        		standCounter = 1; //use vanilla horse jump animation, need to do it this way rather than use stand(), to force use the animation
	        	
		        	if (MoCreatures.isServer())
		        	{
		        		double adjustedJumpStrength = getCustomJump() * horseJumpPower;
		        		
		        		
		        		//need to do this manually rather than calling the parent function to apply the jump strength adjustment
		        		if (handleLavaMovement())
		        		{
		        			motionY =  adjustedJumpStrength;
		        		}
		        		else
		        		{
		        			motionY =  adjustedJumpStrength * 2;
		        		}
		        		
		        		fallDistance = -25; 
	                    setIsJumping(true);
	                    jumpPending = false;
		        		
		        		if (!isGhostHorse())
		        		{
		        			playSound("mob.horse.jump", 0.4F, 1.0F);
		        		}
		        	}
		        	
		        	horseJumpPowerCounter = JUMP_COUNTER_MARKER_INDICATING_THAT_JUMP_HAS_BEEN_EXECUTED;
	        	}
	        	
	        	else if (
	        				horseJumpPowerCounter == JUMP_COUNTER_MARKER_INDICATING_THAT_JUMP_HAS_BEEN_EXECUTED
	        				&& standCounter == 0 //freezes the jumpBar until the standCounter is 0 again
	        			) 
	        	{
	        		horseJumpPower = 0;
	        		horseJumpPowerCounter = 0;
	        	}
	        }
        }
        
        
        
        if (
        		(
        				getType() == 38 //nightmare horse
        				|| getType() == 40 //dark pegasus
        		)
        		&& !isImmuneToFire
        	)
        { //sets immunity to fire for nightmare horse and dark pegasus in-case they get reset, which does sometimes happen when worlds are reloaded
        	isImmuneToFire = true;
        }

        if (shuffleCounter > 0)
        {
            ++shuffleCounter;
            
            if (!MoCreatures.isServer() && getType() == 60 && shuffleCounter % 20 == 0) //spawns note particles for tamed zebras when shuffling
            {
                double xVelocity = rand.nextGaussian() * 0.5D;
                double yVelocity = rand.nextGaussian() * -0.1D;
                double zVelocity = rand.nextGaussian() * 0.02D;
                
                worldObj.spawnParticle("note", posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, xVelocity, yVelocity, zVelocity);
            }
        }
        
        checkShufflingForTimeOut();
        
        
        if (mouthCounter > 0 && ++mouthCounter > 30)
        {
            mouthCounter = 0;
        }

        if (standCounter > 0 && ++standCounter > 20)
        {
            standCounter = 0;
        }

        if (tailCounter > 0 && ++tailCounter > 8)
        {
            tailCounter = 0;
        }

        if (getVanishC() > 0)
        {

            setVanishC((byte) (getVanishC() + 1));

            if (getVanishC() < 15 && !MoCreatures.isServer())
            {
                VanishFX();

            }

            if (getVanishC() > 100)
            {
                setVanishC((byte) 101);
                MoCTools.dropHorseAmuletWithNewPetInformation(this);
                disappearHorse();
            }

            if (getVanishC() == 1)
            {
                MoCTools.playCustomSound(this, "vanish", worldObj);
            }

            if (getVanishC() == 70)
            {
                stand();
            }
        }

        if (sprintCounter > 0)
        {
            ++sprintCounter;
            if (sprintCounter < 150 && sprintCounter % 2 == 0 && !MoCreatures.isServer())
            {
                StarFX();
            }

            if (sprintCounter > 300)
            {
                sprintCounter = 0;
            }
        }

        if (wingFlapCounter > 0)
        {
            ++wingFlapCounter;
            if (wingFlapCounter % 5 == 0 && !MoCreatures.isServer())
            {
                StarFX();
            }
            if (wingFlapCounter > 20)
            {
                wingFlapCounter = 0;

            }
        }

        if (transformCounter > 0)
        {
            if (transformCounter == 40)
            {
                MoCTools.playCustomSound(this, "transform", worldObj);
            }

            if (++transformCounter > 100)
            {
                transformCounter = 0;
                if (transformType != 0)
                {
                    dropArmor();
                    setType(transformType); //change to the new type
                    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth()); //set max health to the new type
                    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed to new type
                }
            }
        }

        if (isGhostHorse() && getMoCAge() < 10 && rand.nextInt(7) == 0)
        {
            setMoCAge(getMoCAge() + 1);
        }

        if (isGhostHorse() && getMoCAge() == 9)
        {
            setMoCAge(100);
            setAdult(true);
        }
    }

    private void openMouth()
    {
        mouthCounter = 1;
    }

    public boolean isReadyforParenting(MoCEntityHorse entityhorse)
    {
        int i = entityhorse.getType();
        return (entityhorse.riddenByEntity == null) && (entityhorse.ridingEntity == null) && entityhorse.getIsTamed() && entityhorse.hasEatenBreedingItem && entityhorse.getIsAdult() && !entityhorse.isUndead() && !entityhorse.isGhostHorse() && (i != 61) && (i < 66);
    }

    @Override
    public boolean shouldRenderName()
    {
        return getShouldDisplayName() && (riddenByEntity == null);
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
    }

    @Override
    public double roperYOffset()
    {
        if (getIsAdult())
        {
            return 0D;
        }
        else
        {
            return (130 - getMoCAge()) * 0.01D;
        }
    }

    /**
     * Horse Types
     * 
     * 1 White . 2 Creamy. 3 Brown. 4 Dark Brown. 5 Black.
     * 
     * 6 Bright Creamy. 7 Speckled. 8 Pale Brown. 9 Grey. 10 11 Pinto . 12
     * Bright Pinto . 13 Pale Speckles.
     * 
     * 16 Spotted 17 Cow.
     * 
     * 
     * 
     * 
     * 21 Ghost (winged) 22 Ghost B
     * 
     * 23 Undead 24 Undead Unicorn 25 Undead Pegasus
     * 
     * 26 skeleton 27 skeleton unicorn 28 skeleton pegasus
     * 
     * 30 bug horse
     * 
     * 32 Bat Horse
     * 
     * 36 Unicorn
     * 
     * 38 Nightmare? 39 White Pegasus 40 Black Pegasus
     * 
     * 50 fairy white 51 fairy blue 52 fairy pink 53 fairy light green
     * 
     * 60 Zebra 61 Zorse
     * 
     * 65 Donkey 66 Mule 67 Zonky
     */

    @Override
    public void selectType()
    {
        checkSpawningBiome(); //try to apply type from spawning biome
        
        if (getType() == 0) //if the type is still unknown
        {
            if (rand.nextInt(5) == 0) {setAdult(false);}
            
            int typeChance = rand.nextInt(100);
            
            if (typeChance <= 56) {setType(7);}
            else if (typeChance <= 89) {setType(8);}
            else {setType(6);}
        }
        
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth()); //set max health according to the type
    }
    
    @Override
    public void setArmorType(byte i)
    {
        dataWatcher.updateObject(25, Integer.valueOf(i));
    }

    public void setBred(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(26, Byte.valueOf(input));
    }

    public void setChestedHorse(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(23, Byte.valueOf(input));
    }

    @Override
    public void setEating(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(24, Byte.valueOf(input));
    }

    public void setNightmareFireTrailCounter(int i)
    {
        nightmareFireTrailCounter = i;
    }

    public void setReproduced(boolean var1)
    {
        hasReproduced = var1;
    }

    @Override
	public void setRideable(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    public void setRoped(boolean var1)
    {
    }

    /**
     * New networked to fix SMP issues
     * 
     * @return
     */
    public void setVanishC(byte i)
    {
        vanishCounter = i;
    }

    public void shuffle()
    {
        if (shuffleCounter == 0)
        {
            shuffleCounter = 1;
        }

        EntityPlayer closestEntityPlayer = worldObj.getClosestPlayerToEntity(this, 8D);
        if (closestEntityPlayer != null)
        {
            faceEntity(closestEntityPlayer, 30F, 30F); // stare at player
        }
    }

    public void stand()
    {
        if (
        		(!isFlyer() && getIsJumping())
        		|| (riddenByEntity == null && !isOnAir())
        	)
        {
            standCounter = 1;
        }
    }
    
    @Override
	public void updateRiderPosition()
    {
        super.updateRiderPosition();
        
        if (standCounter > 0)
        {
        	float factor = 1F;
        	
            float f = MathHelper.sin(renderYawOffset * (float)Math.PI / 180.0F);
            float f1 = MathHelper.cos(renderYawOffset * (float)Math.PI / 180.0F);
            float f2 = 0.7F * factor;
            float f3 = 0.15F * factor;
            riddenByEntity.setPosition(posX + f2 * f, posY + getMountedYOffset() + riddenByEntity.getYOffset() + f3, posZ - f2 * f1);

            if (riddenByEntity instanceof EntityLivingBase)
            {
                ((EntityLivingBase)riddenByEntity).renderYawOffset = renderYawOffset;
            }
        }
    }

    public void StarFX()
    {
        MoCreatures.proxy.StarFX(this);
    }

    /**
     * Used to flicker ghosts
     * 
     * @return
     */
    public float ghostHorseTransparencyFloat()
    {
        if (++flickerCounter > 60)
        {
            flickerCounter = 0;
            transparencyFloat = (rand.nextFloat() * (0.6F - 0.3F) + 0.3F);
        }

        if (isGhostHorse() && getMoCAge() < 10)
        {
            transparencyFloat = 0;
        }
        return transparencyFloat;
    }

    public void transform(int tType)
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), tType), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }

        transformType = tType;
        if (riddenByEntity == null && transformType != 0)
        {
            dropArmor();
            transformCounter = 1;
        }
    }

    public void UndeadFX()
    {
        MoCreatures.proxy.UndeadFX(this);
    }

    public void VanishFX()
    {
        MoCreatures.proxy.VanishFX(this);
    }

    /**
     * Called to vanish Horse
     */

    public void vanishHorse()
    {
        setPathToEntity(null);
        motionX = 0D;
        motionZ = 0D;

        if (isBagger())
        {
            MoCTools.dropInventory(this, localHorseChest);
            dropBags();
        }
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageVanish(getEntityId()), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
            setVanishC((byte) 1);
        }
        MoCTools.playCustomSound(this, "vanish", worldObj);
    }

    @Override
    public void dropMyStuff() 
    {
        dropArmor(); 
        MoCTools.dropSaddle(this, worldObj); 
        if (isBagger())
        {
            MoCTools.dropInventory(this, localHorseChest);
            dropBags();
        }
    }
    
    public void wingFlap()
    {

        if (isFlyer() && wingFlapCounter == 0)
        {
            MoCTools.playCustomSound(this, "wingflap", worldObj);
        }
        wingFlapCounter = 1;
        motionY = 0.5D;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("Saddle", getIsRideable());
        nbtTagCompound.setBoolean("EatingHaystack", getEating());
        nbtTagCompound.setBoolean("ChestedHorse", getIsChestedHorse());
        nbtTagCompound.setBoolean("HasReproduced", getHasReproduced());
        nbtTagCompound.setBoolean("Bred", getHasBred());
        nbtTagCompound.setBoolean("DisplayName", getShouldDisplayName());
        nbtTagCompound.setInteger("ArmorType", getArmorType());

        if (getIsChestedHorse() && localHorseChest != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int index = 0; index < localHorseChest.getSizeInventory(); index++)
            {
                // grab the current item stack
                localItemstack = localHorseChest.getStackInSlot(index);
                if (localItemstack != null)
                {
                    NBTTagCompound nbtTagCompound1 = new NBTTagCompound();
                    nbtTagCompound1.setByte("Slot", (byte) index);
                    localItemstack.writeToNBT(nbtTagCompound1);
                    nbttaglist.appendTag(nbtTagCompound1);
                }
            }
            nbtTagCompound.setTag("Items", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setRideable(nbtTagCompound.getBoolean("Saddle"));
        setEating(nbtTagCompound.getBoolean("EatingHaystack"));
        setBred(nbtTagCompound.getBoolean("Bred"));
        setChestedHorse(nbtTagCompound.getBoolean("ChestedHorse"));
        setReproduced(nbtTagCompound.getBoolean("HasReproduced"));
        setDisplayName(nbtTagCompound.getBoolean("DisplayName"));
        setArmorType((byte) nbtTagCompound.getInteger("ArmorType"));
        if (getIsChestedHorse())
        {
            NBTTagList nbttaglist = nbtTagCompound.getTagList("Items", 10);
            localHorseChest = new MoCAnimalChest(StatCollector.translateToLocal("container.MoCreatures.HorseChest"), getInventorySize());

            for (int index = 0; index < nbttaglist.tagCount(); index++)
            {
                ItemStack itemStack = localHorseChest.getStackInSlot(index);

                if (itemStack != null)
                {
                    localHorseChest.setInventorySlotContents(index, itemStack.copy());
                }
            }
        }
    }

    @Override
    public boolean updateMount()
    {
        return getIsTamed();
    }

    @Override
    public boolean forceUpdates()
    {
        return getIsTamed();
    }
    
    @Override
    public void performAnimation(int animationType)
    {
        //23,24,25,32,36,38,39,40,51,52,53
        if (animationType >= 23 && animationType < 60) //transform
        {
            transformType = animationType;
            transformCounter = 1;
        }
    }
    
    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        if (isUndead()) 
        {
            return EnumCreatureAttribute.UNDEAD;
        }
        return super.getCreatureAttribute();
    }

    @Override
    protected boolean canBeTrappedInAmulet() 
    {
        return getIsTamed() && !isAmuletHorse();
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

	public void setJumpKeyDown(boolean flag)
	{
		isJumpKeyDown = flag;
	}
	
	public boolean getIsJumpKeyDown()
	{
		return isJumpKeyDown;
	}

	public float getHorseJumpPower()
	{
		return horseJumpPower;
	}
	
	private boolean getCustomIsOnGround()
	{
		return !worldObj.isAirBlock((int) Math.round(posX), (int) Math.round(posY - 1), (int) Math.round(posZ));	
	}
}