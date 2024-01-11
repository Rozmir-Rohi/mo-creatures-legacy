package drzhark.mocreatures.entity.animal;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import drzhark.mocreatures.inventory.MoCAnimalChest;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import drzhark.mocreatures.network.message.MoCMessageHeart;
import drzhark.mocreatures.network.message.MoCMessageShuffle;
import drzhark.mocreatures.network.message.MoCMessageVanish;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

public class MoCEntityHorse extends MoCEntityTameableAnimal {
    private int gestationtime;
    private int countEating;
    private int textureCounter;
    private int flickerCounter;
    public int shuffleCounter;
    public int wingFlapCounter;

    private float transparencyFloat = 0.2F;

    public MoCAnimalChest localhorsechest;
    public boolean eatenpumpkin;

    private boolean hasReproduced;
    private int nightmareInt;

    public ItemStack localstack;

    public int mouthCounter;
    public int standCounter;
    public int tailCounter;
    public int vanishCounter;
    public int sprintCounter;
    public int transformType;
    public int transformCounter;

    public MoCEntityHorse(World world)
    {
        super(world);
        setSize(1.4F, 1.6F);
        //health = 20;
        gestationtime = 0;
        eatenpumpkin = false;
        nightmareInt = 0;
        
        setMoCAge(50);
        setChestedHorse(false);
        roper = null;
        this.stepHeight = 1.0F;
        
        if (this.getType() == 38 || this.getType() == 40) {this.isImmuneToFire = true;} //sets immunity to fire for nightmare horse and dark pegasus if they were spawned into the world without going through essence transformations

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
    public boolean attackEntityFrom(DamageSource damagesource, float damage_taken)
    {
       Entity entity = damagesource.getEntity();
        if ((riddenByEntity != null) && (entity == riddenByEntity)) { return false; }
        
        float vanilla_mc_armor_value = 3.7F * getArmorType(); 
        
        damage_taken = damage_taken *(1-(vanilla_mc_armor_value * 0.04F)); //final damage taken after applying armor values. The function uses same damage reduction value as vanilla minecraft.
        
        if (damage_taken < 0F) {damage_taken = 0F;}
        
        
        if (super.attackEntityFrom(damagesource, damage_taken)
        	&& (entity != null)
        	&& (MoCreatures.proxy.specialHorsesFightBack)
        	&& getIsAdult()
        	&& (riddenByEntity == null)
        	&& !(entity instanceof EntityPlayer)
        	&& !(entity instanceof MoCEntityHorse)
        	&& (
        			(getType() > 20 && getType() < 26) // ghost or undead
        			|| (getType() > 25 && getType() < 30) // skeleton horse
        			|| (getType() >= 30 && getType() < 40) //bat horse, nightmare horse, pegasus or unicorn
        			|| (getType() >= 40 && getType() < 60) // black pegasus and fairies
        		)
        	)
        {
        	Entity entity1 = damagesource.getEntity();
            entityToAttack = entity1;
            return true;
        }
        
        return super.attackEntityFrom(damagesource, damage_taken);
    }
    
    @Override
    protected void attackEntity(Entity par1Entity, float par2)
    {
        if (this.attackTime <= 0 && par2 < 2.5F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY)
        	
        {
            this.attackTime = 20;
            stand();
            openMouth();
            MoCTools.playCustomSound(this, getMadSound(), worldObj);
            par1Entity.attackEntityFrom(DamageSource.causeMobDamage(this), calculateAttackDamage());
        }
    }
    
    private float calculateAttackDamage()
    {	
    	int horse_type = this.getType();
    	
    	if ((horse_type > 20 && horse_type < 26) || (horse_type > 25 && horse_type < 30)) // ghost, undead, and skeleton
    	{return 2;}
    
        if (horse_type >= 30 && horse_type < 40 && horse_type != 36 && horse_type != 39) // magics except pegasus and unicorn
        {return 2;} 
        
        if (horse_type == 36 || horse_type == 39) //pure pegasus or pure unicorn
        {return 4;}
        
        if (horse_type >= 40 && horse_type < 60) // dark pegasus and fairies
        {return 6;}
        
        else {return 2;}
    }

    @Override
    public boolean canBeCollidedWith()
    {

        return riddenByEntity == null;
    }

    @Override
    public boolean checkSpawningBiome()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);

        BiomeGenBase currentbiome = MoCTools.Biomekind(worldObj, i, j, k);
        String biome_name = MoCTools.BiomeName(worldObj, i, j, k);

        if (BiomeDictionary.isBiomeOfType(currentbiome, Type.SAVANNA))
        {
        	setType(60);// zebra
        }
        
        
        if (biome_name.toLowerCase().contains("prairie"))//prairies spawn only regular horses, no zebras there
        {
        	setType(rand.nextInt(5) + 1);
        }
        return true;
    }

    /**
     * returns one of the RGB color codes
     * 
     * @param sColor
     *            : 1 will return the Red component, 2 will return the Green and
     *            3 the blue
     * @param typeInt
     *            : which set of colors to inquiry about, corresponds with the
     *            horse types.
     * @return
     */
    public float colorFX(int sColor, int typeInt)
    {
        if (typeInt == 48) // yellow
        {
            if (sColor == 1) { return (float) 179 / 256; }
            if (sColor == 2) { return (float) 160 / 256; }
            return (float) 22 / 256;
        }
        
        if (typeInt == 49) // purple
        {
            if (sColor == 1) { return (float) 147 / 256; }
            if (sColor == 2) { return (float) 90 / 256; }
            return (float) 195 / 256;
        }

        if (typeInt == 51) // blue
        {
            if (sColor == 1) { return (float) 30 / 256; }
            if (sColor == 2) { return (float) 144 / 256; }
            return (float) 255 / 256;
        }
        if (typeInt == 52) // pink
        {
            if (sColor == 1) { return (float) 255 / 256; }
            if (sColor == 2) { return (float) 105 / 256; }
            return (float) 180 / 256;
        }

        if (typeInt == 53) // lightgreen
        {
            if (sColor == 1) { return (float) 188 / 256; }
            if (sColor == 2) { return (float) 238 / 256; }
            return (float) 104 / 256;
        }
        
        if (typeInt == 54) // black fairy
        {
            if (sColor == 1) { return (float) 110 / 256; }
            if (sColor == 2) { return (float) 123 / 256; }
            return (float) 139 / 256;
        }
        
        if (typeInt == 55) // red fairy
        {
            if (sColor == 1) { return (float) 194 / 256; }
            if (sColor == 2) { return (float) 29 / 256; }
            return (float) 34 / 256;
        }
        
        if (typeInt == 56) // dark blue fairy
        {
            if (sColor == 1) { return (float) 63 / 256; }
            if (sColor == 2) { return (float) 45 / 256; }
            return (float) 255 / 256;
        }
        
        if (typeInt == 57) // cyan
        {
            if (sColor == 1) { return (float) 69 / 256; }
            if (sColor == 2) { return (float) 146 / 256; }
            return (float) 145 / 256;
        }

        if (typeInt == 58) // green
        {
            if (sColor == 1) { return (float) 90 / 256; }
            if (sColor == 2) { return (float) 136 / 256; }
            return (float) 43 / 256;
        }
        
        if (typeInt == 59) // orange
        {
            if (sColor == 1) { return (float) 218 / 256; }
            if (sColor == 2) { return (float) 40 / 256; }
            return (float) 0 / 256;
        }
        
        if (typeInt > 22 && typeInt < 26) // green for undeads
        {
            if (sColor == 1) { return (float) 60 / 256; }
            if (sColor == 2) { return (float) 179 / 256; }
            return (float) 112 / 256;

        }
        if (typeInt == 40) // dark red for black pegasus
        {
            if (sColor == 1) { return (float) 139 / 256; }
            if (sColor == 2) { return 0F; }
            return 0F;

        }

        // by default will return clear gold
        if (sColor == 1) { return (float) 255 / 256; }
        if (sColor == 2) { return (float) 236 / 256; }
        return (float) 139 / 256;
    }

    /**
     * Called to vanish a Horse without FX
     */
    public void dissapearHorse()
    {
        this.isDead = true;
    }

    private void drinkingHorse()
    {
        openMouth();
        MoCTools.playCustomSound(this, "drinking", worldObj);
    }

    /**
     * Drops the current armor if the horse has one
     */
    public void dropArmor()
    {
        if (MoCreatures.isServer())
        {
            int armor_index = getArmorType();
            
            Item horse_armor_to_drop = Items.iron_horse_armor; //default item as placeholder
            
            if (getArmorType() == 1) {horse_armor_to_drop = Items.iron_horse_armor;}
            if (getArmorType() == 2) {horse_armor_to_drop = Items.golden_horse_armor;}
            if (getArmorType() == 3) {horse_armor_to_drop = Items.diamond_horse_armor;}
            if (getArmorType() == 4) {horse_armor_to_drop = MoCreatures.horsearmorcrystal;}
            
            if (armor_index != 0)
            {
                MoCTools.playCustomSound(this, "armoroff", worldObj);
                
                EntityItem entityitem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, new ItemStack(horse_armor_to_drop, 1));
                entityitem.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(entityitem);
                
                setArmorType((byte) 0);
            }
        }
    }

    /**
     * Drops a chest block if the horse is bagged
     */
    public void dropBags()
    {
        if (!isBagger() || !getChestedHorse() || !MoCreatures.isServer()) { return; }

        EntityItem entityitem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Blocks.chest, 1));
        float f3 = 0.05F;
        entityitem.motionX = (float) worldObj.rand.nextGaussian() * f3;
        entityitem.motionY = ((float) worldObj.rand.nextGaussian() * f3) + 0.2F;
        entityitem.motionZ = (float) worldObj.rand.nextGaussian() * f3;
        worldObj.spawnEntityInWorld(entityitem);
        setChestedHorse(false);
    }

    private void eatingHorse()
    {
        openMouth();
        MoCTools.playCustomSound(this, "eating", worldObj);
    }

    @Override
    protected void fall(float f)
    {
        if (isFlyer() || isFloater()) { return; }

        float i = (float) (Math.ceil(f - 3F)/2F);
        if (MoCreatures.isServer() && (i > 0))
        {
            if (getType() >= 10)
            {
                i /= 2;
            }
            if (i > 1F)
            {
                attackEntityFrom(DamageSource.fall, i);
            }
            if ((riddenByEntity != null) && (i > 1F))
            {
                riddenByEntity.attackEntityFrom(DamageSource.fall, i);
            }

            Block block = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 0.20000000298023221D - prevRotationPitch), MathHelper.floor_double(posZ));
            if (block != Blocks.air)
            {
                SoundType stepsound = block.stepSound;
                worldObj.playSoundAtEntity(this, stepsound.getStepResourcePath(), stepsound.getVolume() * 0.5F, stepsound.getPitch() * 0.75F);
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

    public boolean getChestedHorse()
    {
        return (dataWatcher.getWatchableObjectByte(23) == 1);
    }

    protected MoCEntityHorse getClosestMommy(Entity entity, double d)
    {
        double d1 = -1D;
        MoCEntityHorse entityliving = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.expand(d, d, d));
        for (int i = 0; i < list.size(); i++)
        {
            Entity entity1 = (Entity) list.get(i);
            if ((!(entity1 instanceof MoCEntityHorse)) || ((entity1 instanceof MoCEntityHorse) && !((MoCEntityHorse) entity1).getHasBred()))
            {
                continue;
            }

            double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1D) || (d2 < d1)))
            {
                d1 = d2;
                entityliving = (MoCEntityHorse) entity1;
            }
        }

        return entityliving;
    }

    @Override
    public double getCustomJump()
    {
        double HorseJump = 0.4D;
        int horse_type = this.getType();
        
        if (horse_type < 6) // tier 1
        {
            HorseJump = 0.4;
        }
        else if (horse_type > 5 && horse_type < 11) // tier 2
        {
            HorseJump = 0.45D;
        }
        else if (horse_type > 10 && horse_type < 16) // tier 3
        {
            HorseJump = 0.5D;
        }
        else if (horse_type > 15 && horse_type < 21) // tier 4
        {
            HorseJump = 0.55D;
        }

        else if (horse_type > 20 && horse_type < 26) // ghost and undead
        {
            HorseJump = 0.45D;
        }
        else if (horse_type > 25 && horse_type < 30) // skeleton
        {
            HorseJump = 0.5D;
        }
        else if (horse_type >= 30 && horse_type < 40) // magics
        {
            HorseJump = 0.55D;
        }
        else if (horse_type >= 40 && horse_type < 60) // black pegasus and fairies
        {
            HorseJump = 0.6D;
        }
        else if (horse_type >= 60) // donkeys - zebras and the like
        {
            HorseJump = 0.45D;
        }
        return HorseJump;
    }

    @Override
    public double getCustomSpeed() //controls both land and flying speed
    {
        double horseSpeed = 0.8D;  //default horse speed if no other criteria is met
        
        int horse_type = this.getType();
        
        if (horse_type < 6) // tier 1
        {
            horseSpeed = 0.9;
        }
        else if (horse_type > 5 && horse_type < 11) // tier 2
        {
            horseSpeed = 1.0D;
        }
        else if (horse_type > 10 && horse_type < 16) // tier 3
        {
            horseSpeed = 1.1D;
        }
        else if (horse_type > 15 && horse_type < 21) // tier 4
        {
            horseSpeed = 1.2D;
        }

        else if (horse_type > 20 && horse_type < 26) // ghost and undead
        {
            horseSpeed = 0.8D;
        }
        else if (horse_type > 25 && horse_type < 30) // skeleton Horses
        {
            horseSpeed = 1.0D;
        }
        else if (horse_type > 30 && horse_type < 40) // bat horse, nightmare horse, pegasus, and unicorn
        {
            horseSpeed = 1.2D;
        }
        
        else if (horse_type == 40) //dark pegasus
        {
        	horseSpeed = 1.1;
        }
        
        else if (horse_type > 40 && horse_type < 60) // fairies
        {
            horseSpeed = 1.3D;
        }
        else if (horse_type == 60 || horse_type == 61) // zebras and zorse
        {
            horseSpeed = 1.1D;
        }
        else if (horse_type == 65) // donkeys
        {
            horseSpeed = 0.7D;
        }
        else if (horse_type > 65) // mule and zorky
        {
            horseSpeed = 0.9D;
        }
        
        
        if (!isFlyer()) //Makes all horses run faster when whipped except flying horses. Don't make flying horse run faster when whipped as they become too fast
        {
	        if (sprintCounter > 0 && sprintCounter < 150)
	        {
	        	horseSpeed *= 1.5D;
	        }
        	else if (sprintCounter > 150) //horse becomes tired from sprinting
        	{
        		horseSpeed *= 0.5D;
        	}
        }
        
        
        return horseSpeed;
    }

    @Override
    protected String getDeathSound()
    {
        openMouth();
        if (this.isUndead()) { return "mocreatures:horsedyingundead"; }
        if (this.isGhost()) { return "mocreatures:horsedyingghost"; }
        if (this.getType() == 60 || this.getType() == 61) { return "mocreatures:zebrahurt"; }
        if (this.getType() >= 65 && this.getType() <= 67) { return "mocreatures:donkeydying"; }
        return "mocreatures:horsedying";
    }

    @Override
    public boolean getDisplayName()
    {
        if (isGhost() && getMoCAge() < 10) { return false; }

        return (getName() != null && !getName().equals(""));
    }

    @Override
    protected Item getDropItem()
    {
        boolean flag = (rand.nextInt(100) < MoCreatures.proxy.rareItemDropChance);

        if (flag && (this.getType() == 36 || (this.getType() >= 50 && this.getType() < 60))) // unicorn
        { return MoCreatures.unicornhorn; }
        if (this.getType() == 39) // pegasus
        { return Items.feather; }
        if (this.getType() == 40) // dark pegasus
        { return Items.feather; }
        if (this.getType() == 38 && flag && worldObj.provider.isHellWorld) // nightmare
        { return MoCreatures.heartfire; }
        if (this.getType() == 32 && flag) // bat horse
        { return MoCreatures.heartdarkness; }
        if (this.getType() == 26)// skeleton
        { return Items.bone; }
        if ((this.getType() == 23 || this.getType() == 24 || this.getType() == 25))
        {
            if (flag) { return MoCreatures.heartundead; }
            return Items.rotten_flesh;
        }
        if (this.getType() == 21 || this.getType() == 22) { return Items.ghast_tear; }

        return Items.leather;
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
        if (this.isUndead()) { return "mocreatures:horsehurtundead"; }
        if (this.isGhost()) { return "mocreatures:horsehurtghost"; }
        if (this.getType() == 60 || this.getType() == 61) { return "mocreatures:zebrahurt"; }
        if (this.getType() >= 65 && this.getType() <= 67) { return "mocreatures:donkeyhurt"; }
        return "mocreatures:horsehurt";
    }

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
        if (this.isUndead()) { return "mocreatures:horsegruntundead"; }
        if (this.isGhost()) { return "mocreatures:horsegruntghost"; }
        if (this.getType() == 60 || this.getType() == 61) { return "mocreatures:zebragrunt"; }
        if (this.getType() >= 65 && this.getType() <= 67) { return "mocreatures:donkeygrunt"; }
        return "mocreatures:horsegrunt";
    }

    /**
     * sound played when an untamed mount buckles rider
     */
    @Override
    protected String getMadSound()
    {
        openMouth();
        stand();
        if (this.isUndead()) { return "mocreatures:horsemadundead"; }
        if (this.isGhost()) { return "mocreatures:horsemadghost"; }
        if (this.getType() == 60 || this.getType() == 61) { return "mocreatures:zebrahurt"; }
        if (this.getType() >= 65 && this.getType() <= 67) { return "mocreatures:donkeyhurt"; }
        return "mocreatures:horsemad";
    }

    public double calculateMaxHealth()
    {
        double maximumHealth = 10.0D;;
        int horse_type = this.getType();
        
        
        if (horse_type < 6) // tier 1
        {
            maximumHealth = 15.0D;
        }
        else if (horse_type > 5 && horse_type < 11) // tier 2
        {
            maximumHealth = 20.0D;
        }
        else if (horse_type > 10 && horse_type < 16) // tier 3
        {
            maximumHealth = 25.0D;
        }
        else if (horse_type > 15 && horse_type < 21) // tier 4
        {
            maximumHealth = 25.0D;
        }

        else if (horse_type > 20 && horse_type < 26) // ghost and undead
        {
            maximumHealth = 25.0D;
        }
        else if (horse_type > 25 && horse_type < 30) // skeleton horses
        {
            maximumHealth = 15.0D;
        }
        else if (horse_type >= 30 && horse_type < 40) // magics
        {
            maximumHealth = 30.0D;
        }
        else if (horse_type == 40) // black pegasus
        {
            maximumHealth = 40.0D;
        }
        else if (horse_type > 40 && horse_type < 60) // fairy horses
        {
            maximumHealth = 35.0D;
        }
        else if (horse_type >= 60) // donkeys - zebras and the like
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

    public int getNightmareInt()
    {
        return nightmareInt;
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
            /*
        case 44:
            tempTexture = "horsefairydarkblue.png";
            break;
        case 45:
            tempTexture = "horsefairydarkblue.png";
            break;
        case 46:
            tempTexture = "horsefairydarkblue.png";
            break; 
        case 47:
            tempTexture = "horsefairydarkblue.png";
            break;*/
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
            String armorTex = "";
            if (getArmorType() == 1)
            {
                armorTex = "metal.png";
            }
            if (getArmorType() == 2)
            {
                armorTex = "gold.png";
            }
            if (getArmorType() == 3)
            {
                armorTex = "diamond.png";
            }
            if (getArmorType() == 4)
            {
                armorTex = "crystaline.png";
            }
            return MoCreatures.proxy.getTexture(tempTexture.replace(".png", armorTex));
        }

        
        if (this.isUndead() && this.getType() < 26)
        {
            String baseTex = "horseundead";
            int max = 79;
            if (this.getType() == 25) // undead pegasus
            {
                baseTex = "horseundeadpegasus";
                // max = 79; //undead pegasus have an extra animation

            }
            if (this.getType() == 24)// undead unicorn
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

        
        if (this.isNightmare()) //nightmare horse dynamic textures
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
    //private int HorseGenetics(MoCEntityHorse entityhorse, MoCEntityHorse entityhorse1)
    private int HorseGenetics(int typeA, int typeB)
    {
        boolean flag = MoCreatures.proxy.easyBreeding;
        //int typeA = entityhorse.getType();
        //int typeB = entityhorse1.getType();

        // identical horses have so spring
        if (typeA == typeB) { return typeA; }

        // zebras plus any horse
        if (typeA == 60 && typeB < 21 || typeB == 60 && typeA < 21) { return 61; // zorse
        }

        // donkey plus any horse
        if (typeA == 65 && typeB < 21 || typeB == 65 && typeA < 21) { return 66; // mule
        }

        // zebra plus donkey
        if (typeA == 60 && typeB == 65 || typeB == 60 && typeA == 65) { return 67; // zonky
        }

        if (typeA > 20 && typeB < 21 || typeB > 20 && typeA < 21) // rare horses plus  ordinary horse always returns ordinary horse
        {
            if (typeA < typeB) { return typeA; }
            return typeB;
        }

        // unicorn plus white pegasus (they will both vanish!)
        if (typeA == 36 && typeB == 39 || typeB == 36 && typeA == 39)
        {
            return 50; // white fairy
        }

        // rare horse mixture: produces a regular horse 1-5
        if (typeA > 20 && typeB > 20 && (typeA != typeB)) { return (rand.nextInt(5)) + 1; }

        // rest of cases will return either typeA, typeB or new mix
        int chanceInt = (rand.nextInt(4)) + 1;
        if (!flag)
        {
            if (chanceInt == 1) // 25%
            {
                return typeA;
            }
            else if (chanceInt == 2) // 25%
            { return typeB; }
        }

        if ((typeA == 1 && typeB == 2) || (typeA == 2 && typeB == 1)) { return 6; }

        if ((typeA == 1 && typeB == 3) || (typeA == 3 && typeB == 1)) { return 2; }

        if ((typeA == 1 && typeB == 4) || (typeA == 4 && typeB == 1)) { return 7; }

        if ((typeA == 1 && typeB == 5) || (typeA == 5 && typeB == 1)) { return 9; }

        if ((typeA == 1 && typeB == 7) || (typeA == 7 && typeB == 1)) { return 12; }

        if ((typeA == 1 && typeB == 8) || (typeA == 8 && typeB == 1)) { return 7; }

        if ((typeA == 1 && typeB == 9) || (typeA == 9 && typeB == 1)) { return 13; }

        if ((typeA == 1 && typeB == 11) || (typeA == 11 && typeB == 1)) { return 12; }

        if ((typeA == 1 && typeB == 12) || (typeA == 12 && typeB == 1)) { return 13; }

        if ((typeA == 1 && typeB == 17) || (typeA == 17 && typeB == 1)) { return 16; }

        if ((typeA == 2 && typeB == 4) || (typeA == 4 && typeB == 2)) { return 3; }

        if ((typeA == 2 && typeB == 5) || (typeA == 5 && typeB == 2)) { return 4; }

        if ((typeA == 2 && typeB == 7) || (typeA == 7 && typeB == 2)) { return 8; }

        if ((typeA == 2 && typeB == 8) || (typeA == 8 && typeB == 2)) { return 3; }

        if ((typeA == 2 && typeB == 12) || (typeA == 12 && typeB == 2)) { return 6; }

        if ((typeA == 2 && typeB == 16) || (typeA == 16 && typeB == 2)) { return 13; }

        if ((typeA == 2 && typeB == 17) || (typeA == 17 && typeB == 2)) { return 12; }

        if ((typeA == 3 && typeB == 4) || (typeA == 4 && typeB == 3)) { return 8; }

        if ((typeA == 3 && typeB == 5) || (typeA == 5 && typeB == 3)) { return 8; }

        if ((typeA == 3 && typeB == 6) || (typeA == 6 && typeB == 3)) { return 2; }

        if ((typeA == 3 && typeB == 7) || (typeA == 7 && typeB == 3)) { return 11; }

        if ((typeA == 3 && typeB == 9) || (typeA == 9 && typeB == 3)) { return 8; }

        if ((typeA == 3 && typeB == 12) || (typeA == 12 && typeB == 3)) { return 11; }

        if ((typeA == 3 && typeB == 16) || (typeA == 16 && typeB == 3)) { return 11; }

        if ((typeA == 3 && typeB == 17) || (typeA == 17 && typeB == 3)) { return 11; }

        if ((typeA == 4 && typeB == 6) || (typeA == 6 && typeB == 4)) { return 3; }

        if ((typeA == 4 && typeB == 7) || (typeA == 7 && typeB == 4)) { return 8; }

        if ((typeA == 4 && typeB == 9) || (typeA == 9 && typeB == 4)) { return 7; }

        if ((typeA == 4 && typeB == 11) || (typeA == 11 && typeB == 4)) { return 7; }

        if ((typeA == 4 && typeB == 12) || (typeA == 12 && typeB == 4)) { return 7; }

        if ((typeA == 4 && typeB == 13) || (typeA == 13 && typeB == 4)) { return 7; }

        if ((typeA == 4 && typeB == 16) || (typeA == 16 && typeB == 4)) { return 13; }

        if ((typeA == 4 && typeB == 17) || (typeA == 17 && typeB == 4)) { return 5; }

        if ((typeA == 5 && typeB == 6) || (typeA == 6 && typeB == 5)) { return 4; }

        if ((typeA == 5 && typeB == 7) || (typeA == 7 && typeB == 5)) { return 4; }

        if ((typeA == 5 && typeB == 8) || (typeA == 8 && typeB == 5)) { return 4; }

        if ((typeA == 5 && typeB == 11) || (typeA == 11 && typeB == 5)) { return 17; }

        if ((typeA == 5 && typeB == 12) || (typeA == 12 && typeB == 5)) { return 13; }

        if ((typeA == 5 && typeB == 13) || (typeA == 13 && typeB == 5)) { return 16; }

        if ((typeA == 5 && typeB == 16) || (typeA == 16 && typeB == 5)) { return 17; }

        if ((typeA == 6 && typeB == 8) || (typeA == 8 && typeB == 6)) { return 2; }

        if ((typeA == 6 && typeB == 17) || (typeA == 17 && typeB == 6)) { return 7; }

        if ((typeA == 7 && typeB == 16) || (typeA == 16 && typeB == 7)) { return 13; }

        if ((typeA == 8 && typeB == 11) || (typeA == 11 && typeB == 8)) { return 7; }

        if ((typeA == 8 && typeB == 12) || (typeA == 12 && typeB == 8)) { return 7; }

        if ((typeA == 8 && typeB == 13) || (typeA == 13 && typeB == 8)) { return 7; }

        if ((typeA == 8 && typeB == 16) || (typeA == 16 && typeB == 8)) { return 7; }

        if ((typeA == 8 && typeB == 17) || (typeA == 17 && typeB == 8)) { return 7; }

        if ((typeA == 9 && typeB == 16) || (typeA == 16 && typeB == 9)) { return 13; }

        if ((typeA == 11 && typeB == 16) || (typeA == 16 && typeB == 11)) { return 13; }

        if ((typeA == 11 && typeB == 17) || (typeA == 17 && typeB == 11)) { return 7; }

        if ((typeA == 12 && typeB == 16) || (typeA == 16 && typeB == 12)) { return 13; }

        if ((typeA == 13 && typeB == 17) || (typeA == 17 && typeB == 13)) { return 9; }

        return typeA; // breed is not in the table so it will return the first
                        // parent type
    }

      

    @Override
    public boolean interact(EntityPlayer entityplayer)
    {
        if (super.interact(entityplayer)) { return false; }
        
        int horse_type = this.getType();
        
        if (horse_type == 60 && !getIsTamed() && isZebraRunning()) // zebra
        { return false; }
        
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        EntityPlayer owner = worldObj.getPlayerEntityByName(this.getOwnerName());
        
        if (itemstack != null)
        {
        	Item item = itemstack.getItem();
        
	        if (!getIsRideable() && (item == Items.saddle) || (item == MoCreatures.horsesaddle))
	        {
	            if (--itemstack.stackSize == 0)
	            {
	                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
	            }
	            setRideable(true);
	            return true;
	        }
	        
	        if (this.getIsTamed())
	        {
	       
		
		        if (canWearRegularArmor() && (
		        		item == Items.iron_horse_armor
		        		|| item == Items.golden_horse_armor
		        		|| item == Items.diamond_horse_armor
		        	))
		        {
		            if (getArmorType() == 0) {MoCTools.playCustomSound(this, "armorput", worldObj);}
		            
		            dropArmor();
		            
		            byte regular_armor_type = 0;
		            
		            if (item == Items.iron_horse_armor) {regular_armor_type = 1;}
		            if (item == Items.golden_horse_armor) {regular_armor_type = 2;}
	        		if (item == Items.diamond_horse_armor) {regular_armor_type = 3;}
		            
		            setArmorType(regular_armor_type);
		            
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		            }
		            
		            return true;
		        }
		
		        if ((item == MoCreatures.horsearmorcrystal) && isMagicHorse())
		        {
		            if (getArmorType() == 0) {MoCTools.playCustomSound(this, "armorput", worldObj);}
		            
		            dropArmor();
		            
		            setArmorType((byte) 4);
		            
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		            }
		            return true;
		        }
		
		        // transform to undead, or heal undead horse
		        if (item == MoCreatures.essenceundead)
		        {
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		            }
		            else
		            {
		                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		            }
		
		            if (this.isUndead() || isGhost())
		            {
		                this.setHealth(getMaxHealth());
		
		            }
		            
		            else
		            {
		       		 	if (owner != null) {owner.addStat(MoCAchievements.undead_horse, 1);};
		            }
		            
		            
		
		            // pegasus, dark pegasus, or bat horse
		            if (horse_type == 39 || horse_type == 32 || horse_type == 40)
		            {
		
		                // transformType = 25; //undead pegasus
		                transform(25);
		
		            }
		            else if (horse_type == 36 || (horse_type > 47 && horse_type < 60)) // unicorn or fairies
		            {
		
		                // transformType = 24; //undead unicorn
		                transform(24);
		            }
		            else if (horse_type < 21 || horse_type == 60 || horse_type == 61) // regular horses or zebras
		            {
		
		                // transformType = 23; //undead
		                transform(23);
		            }
		
		            drinkingHorse();
		            return true;
		        }
		
		        // to transform to nightmares: only pure breeds
		        if (item == MoCreatures.essencefire)
		        {
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		            }
		            else
		            {
		                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		            }
		
		            if (this.isNightmare())
		            {
		                if (getIsAdult() && getHealth() == getMaxHealth())
		                {
		                    this.eatenpumpkin = true;
		                }
		                this.setHealth(getMaxHealth());
		
		            }
		            if (horse_type == 61)
		            {
		                //nightmare
		                transform(38);
		                this.isImmuneToFire = true;
		                
		       		 	if (owner != null) {owner.addStat(MoCAchievements.nightmare_horse, 1);};
		            }
		            
		            drinkingHorse();
		            return true;
		        }
		
		        // transform to dark pegasus
		        if (item == MoCreatures.essencedarkness)
		        {
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		            }
		            else
		            {
		                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		            }
		
		            if (horse_type == 32)
		            {
		                if (getIsAdult() && getHealth() == getMaxHealth())
		                {
		                    this.eatenpumpkin = true;
		                }
		                this.setHealth(getMaxHealth());
		            }
		
		            if (horse_type == 61)
		            {
		                //bat horse
		                transform(32);
		       		 	if (owner != null) {owner.addStat(MoCAchievements.bat_horse, 1);};
		            }
		
		            if (horse_type == 39) // pegasus to darkpegasus
		            {
		                //darkpegasus
		                transform(40);
		                this.isImmuneToFire = true;
		                
		       		 	if (owner != null) {owner.addStat(MoCAchievements.dark_pegasus, 1);};
		            }
		            drinkingHorse();
		            return true;
		        }
		
		        if (item == MoCreatures.essencelight)
		        {
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.glass_bottle));
		            }
		            else
		            {
		                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		            }
		
		            if (this.isMagicHorse())
		            {
		                if (getIsAdult() && getHealth() == getMaxHealth())
		                {
		                    this.eatenpumpkin = true;
		                }
		                this.setHealth(getMaxHealth());
		            }
		
		            if (this.isNightmare())
		            {
		                // unicorn
		                transform(36);
		                
		       		 	if (owner != null) {owner.addStat(MoCAchievements.unicorn, 1);};
		            }
		            if (horse_type == 32 && this.posY > 128D) // bathorse to pegasus
		            {
		                // pegasus
		                transform(39);
		                
		                if (owner != null) {owner.addStat(MoCAchievements.pegasus, 1);};
		            }
		            // to return undead horses to pristine conditions
		            if (this.isUndead() && this.getIsAdult() && MoCreatures.isServer())
		            {
		                setMoCAge(10);
		                if (horse_type > 26)
		                {
		                    setType(horse_type - 3);
		                }
		            }
		            drinkingHorse();
		            return true;
		        }
		
		        if (this.isAmuletHorse())
		        {
		            if ((horse_type == 26 || horse_type == 27 || horse_type == 28) && item == MoCreatures.amuletbone)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		                vanishHorse();
		                return true;
		            }
		
		            if ((horse_type > 47 && horse_type < 60) && item == MoCreatures.amuletfairy)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		                vanishHorse();
		                return true;
		            }
		
		            if ((horse_type == 39 || horse_type == 40) && (item == MoCreatures.amuletpegasus))
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		                vanishHorse();
		                return true;
		            }
		
		            if ((horse_type == 21 || horse_type == 22) && (item == MoCreatures.amuletghost))
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		                vanishHorse();
		                return true;
		            }
		
		        }
		
		        if ((item == Items.dye) && (horse_type == 50)) //set color of fairy horse based on dye player is interacting with
		        {
		
		            int colorInt = BlockColored.func_150031_c(itemstack.getItemDamage());
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
		            
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		            }
		            eatingHorse();
		            return true;
		        }
		
		        // zebra easter egg
		        if ((horse_type == 60) && (
		        		item == Items.record_11)
		        		|| (item == Items.record_13)
		        		|| (item == Items.record_cat)
		        		|| (item == Items.record_chirp)
		        		|| (item == Items.record_far)
		        		||(item == Items.record_mall)
		        		|| (item == Items.record_mellohi)
		        		|| (item == Items.record_stal)
		        		|| (item == Items.record_strad)
		        		|| (item == Items.record_ward))
		        {
		            entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		            if (MoCreatures.isServer())
		            {
		                EntityItem entityitem1 = new EntityItem(worldObj, this.posX, this.posY, this.posZ, new ItemStack(MoCreatures.recordshuffle, 1));
		                entityitem1.delayBeforeCanPickup = 20;
		                worldObj.spawnEntityInWorld(entityitem1);
		            }
		            eatingHorse();
		            return true;
		        }
		        
		        if (item == Item.getItemFromBlock(Blocks.chest) && (isBagger()))
		        {
		            if (getChestedHorse()) { return false; }
		            if (--itemstack.stackSize == 0)
		            {
		                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
		            }
		
		            entityplayer.inventory.addItemStackToInventory(new ItemStack(MoCreatures.key));
		            setChestedHorse(true);
		            return true;
		        }
	        
        	}
	        
	        
	        if (!isUndead() && !isMagicHorse() && ( //food items for normal horses
	        		item == Items.wheat
	        		|| item == MoCreatures.sugarlump
	        		|| item == Items.bread
	        		|| item == Items.apple
	        		|| item == Items.golden_apple
	        		|| item == MoCreatures.haystack))
	        {
	        	int temperIncrease = 0;
	        	int healthIncrease = 0;
	        	int ageIncrease = 0;
	
	        	if (item == Items.wheat) {temperIncrease = 25; healthIncrease = 5; ageIncrease = 1;}
	        	if (item == MoCreatures.sugarlump) {temperIncrease = 25; healthIncrease = 10; ageIncrease = 2;}
	        	if (item == Items.bread) {temperIncrease = 100; healthIncrease = 20; ageIncrease = 3;}
	        	if (item == Items.apple || item == Items.golden_apple) {temperIncrease = 0; healthIncrease = 25; ageIncrease = 1;}
	        	if (item == MoCreatures.haystack) {temperIncrease = 0; healthIncrease = 25; ageIncrease = 1;}
	
	
	        	if (--itemstack.stackSize == 0)
	        	{
	        		entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
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
	
	        	if ((getHealth() + healthIncrease) > getMaxHealth())  {this.setHealth(getMaxHealth());}
	        	else {this.setHealth(getHealth() + healthIncrease);}
	        		
	
	        	eatingHorse(); //play eating sound
	
	
	            if (!getIsAdult() && (getMoCAge() < 100))
	            {
	            	setMoCAge(getMoCAge() + ageIncrease);
	            }
	
	
	
	        	 if (MoCreatures.isServer() && !(this.getIsTamed()) && (item == Items.apple || item == Items.golden_apple))
	             {
	        		 MoCTools.tameWithName(entityplayer, this);
	        		 
	        		 if (entityplayer != null && (horse_type > 5 && horse_type < 11)) //tier 2
	        		 {
	        			 entityplayer.addStat(MoCAchievements.tier2_horse, 1);
	        		 }
	        		 else if (entityplayer != null && (horse_type == 60)) //zebra
	        		 {
	        			 entityplayer.addStat(MoCAchievements.zebra, 1);
	        		 }
	             }
	
	
	        	 return true;
	        }
	        
	        if ((item == MoCreatures.key) && getChestedHorse())
	        {
	            // if first time opening horse chest, we must initialize it
	            if (localhorsechest == null)
	            {
	                localhorsechest = new MoCAnimalChest(I18n.format("container.MoCreatures.HorseChest"), getInventorySize());// , new
	            }
	            // only open this chest on server side
	            if (!worldObj.isRemote)
	            {
	                entityplayer.displayGUIChest(localhorsechest);
	            }
	            return true;
	
	        }
	        if (item == Item.getItemFromBlock(Blocks.pumpkin)  //normal horse breeding items
	        		|| item == Items.mushroom_stew
	        		|| item == Items.cake)
	        {
	            if (!getIsAdult() || isMagicHorse() || isUndead()) { return false; }
	            
	            if (item == Items.mushroom_stew)
	            {
	                if (--itemstack.stackSize == 0)
	                {
	                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.bowl));
	                }
	                else
	                {
	                    entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.bowl));
	                }
	            }
	            else if (--itemstack.stackSize == 0)
	            {
	                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
	            }
	            eatenpumpkin = true;
	            this.setHealth(getMaxHealth());
	            eatingHorse();
	            return true;
	        }
        }

        else if (getIsRideable() && getIsAdult() && (riddenByEntity == null) && (entityplayer.riddenByEntity == null)) 
        {
            entityplayer.rotationYaw = rotationYaw;
            entityplayer.rotationPitch = rotationPitch;
            setEating(false);
            if (MoCreatures.isServer()) {entityplayer.mountEntity(this);}
            gestationtime = 0;
            return true;
        }
        
        return false;
    }

    /**
     * Can this horse be trapped in a special amulet?
     */
    public boolean isAmuletHorse()
    {

        return (this.getType() >= 48 && this.getType() < 60) || this.getType() == 40 || this.getType() == 39 || this.getType() == 21 || this.getType() == 22 || this.getType() == 26 || this.getType() == 27 || this.getType() == 28;
    }

    /**
     * Can wear regular armor
     */
    public boolean canWearRegularArmor()
    {
        return (this.getType() < 21);
    }

    /**
     * able to carry bags
     * 
     * @return
     */
    public boolean isBagger()
    {
        return (this.getType() == 66) // mule
                || (this.getType() == 65) // donkey
                || (this.getType() == 67) // zonkey
                || (this.getType() == 39) // pegasus
                || (this.getType() == 40) // black pegasus
                || (this.getType() == 25) // undead pegasus
                || (this.getType() == 28) // skeleton pegasus
                || (this.getType() >= 45 && this.getType() < 60) // fairy
        ;
    }

    /**
     * Falls slowly
     */
    public boolean isFloater()
    {
        return this.getType() == 36 // unicorn
                || this.getType() == 27 // skeleton unicorn
                || this.getType() == 24 // undead unicorn
                || this.getType() == 22; // not winged ghost

    }

    @Override
    public boolean isFlyer()
    {
        return this.getType() == 39 // pegasus
                || this.getType() == 40 // dark pegasus
                || (this.getType() >= 45 && this.getType() < 60) //fairy
                || this.getType() == 32 // bat horse
                || this.getType() == 21 // ghost winged
                || this.getType() == 25 // undead pegasus
                || this.getType() == 28;// skeleton pegasus
    }

    /**
     * Is this a ghost horse?
     * 
     * @return
     */
    public boolean isGhost()
    {

        return this.getType() == 21 || this.getType() == 22;
    }

    /**
     * Can wear magic armor
     */
    public boolean isMagicHorse()
    {
        return

        this.getType() == 39 || this.getType() == 36 || this.getType() == 32 || this.getType() == 40 || (this.getType() >= 45 && this.getType() < 60) //fairy
                || this.getType() == 21 || this.getType() == 22;
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getEating() || (riddenByEntity != null) || this.standCounter != 0 || this.shuffleCounter != 0 || this.getVanishC() != 0;
    }

    /**
     * Is this a Nightmare horse?
     */
    public boolean isNightmare()
    {

        return this.getType() == 38;
    }

    /**
     * Rare horse that can be transformed into Nightmares or Bathorses or give
     * ghost horses on dead
     */
    public boolean isPureBreed()
    {

        return (this.getType() > 10 && this.getType() < 21);
    }

    /**
     * Mobs don't attack you if you're riding one of these they won't reproduce
     * either
     * 
     * @return
     */
    public boolean isUndead()
    {
        return (this.getType() == 23) || (this.getType() == 24) || (this.getType() == 25) || (this.getType() == 26) // skeleton
                || (this.getType() == 27) // skeleton unicorn
                || (this.getType() == 28); // skeleton pegasus
    }

    /**
     * Has an unicorn? to render it and buckle entities!
     * 
     * @return
     */
    public boolean isUnicorned()
    {

        return this.getType() == 36 || (this.getType() >= 45 && this.getType() < 60) || this.getType() == 27 || this.getType() == 24;
    }

    public boolean isZebraRunning()
    {
        boolean flag = false;
        EntityPlayer ep1 = worldObj.getClosestPlayerToEntity(this, 8D);
        if (ep1 != null)
        {
            flag = true;
            if (ep1.ridingEntity != null && ep1.ridingEntity instanceof MoCEntityHorse)
            {
                MoCEntityHorse playerHorse = (MoCEntityHorse) ep1.ridingEntity;
                if (playerHorse.getType() == 16 || playerHorse.getType() == 17 || playerHorse.getType() == 60 || playerHorse.getType() == 61)
                {
                    flag = false;
                }
            }

        }
        if (flag)
        {
            MoCTools.runLikeHell(this, ep1);
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
        if (this.getIsAdult())
        {
            return -80;
        }
        else
        {
            return (-5 - getMoCAge());
        }
    }

    private boolean nearMusicBox()
    {
        // only works server side
        if (!MoCreatures.isServer()) { return false; }

        boolean flag = false;
        TileEntityJukebox jukebox = MoCTools.nearJukeBoxRecord(this, 6D);
        if (jukebox != null && jukebox.func_145856_a() != null)
        {
            Item record = jukebox.func_145856_a().getItem();

            if (record == MoCreatures.recordshuffle)
            {
                flag = true;
                if (shuffleCounter > 1000)
                {
                    shuffleCounter = 0;
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageShuffle(this.getEntityId(), false), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
                    BlockJukebox blockjukebox = (BlockJukebox) worldObj.getBlock(jukebox.xCoord, jukebox.yCoord, jukebox.zCoord);
                    if (blockjukebox != null)
                    {
                        blockjukebox.func_149925_e(worldObj, jukebox.xCoord, jukebox.yCoord, jukebox.zCoord);
                    }
                    flag = false;
                }
            }
        }
        return flag;
    }

    // changed to public since we need to send this info to server
    public void NightmareEffect()
    {
        int i = MathHelper.floor_double(posX);
        int j = MathHelper.floor_double(boundingBox.minY);
        int k = MathHelper.floor_double(posZ);
        Block block = worldObj.getBlock(i - 1, j, k - 1);
        int metadata = worldObj.getBlockMetadata(i - 1, j, k - 1);
        BlockEvent.BreakEvent event = null;
        if (!this.worldObj.isRemote)
        {
            event = new BlockEvent.BreakEvent(i - 1, j, k - 1, worldObj, block, metadata, FakePlayerFactory.get(DimensionManager.getWorld(this.worldObj.provider.dimensionId), MoCreatures.MOCFAKEPLAYER));
        }
        if (event != null && !event.isCanceled())
        {
            worldObj.setBlock(i - 1, j, k - 1, Blocks.fire, 0, 3);//MC1.5
            EntityPlayer entityplayer = (EntityPlayer) riddenByEntity;
            if ((entityplayer != null) && (entityplayer.isBurning()))
            {
                entityplayer.extinguish();
            }
            setNightmareInt(getNightmareInt() - 1);
        }
    }

    @Override
    public void onDeath(DamageSource damagesource)
    {
        super.onDeath(damagesource);
        if (MoCreatures.isServer())
        {
            if ((rand.nextInt(10) == 0) && (this.getType() == 23) || (this.getType() == 24) || (this.getType() == 25))
            {
                MoCTools.spawnMaggots(worldObj, this);
            }

            if (getIsTamed() && (isMagicHorse() || isPureBreed()) && !isGhost() && rand.nextInt(4) == 0)
            {
                MoCEntityHorse new_ghost_horse = new MoCEntityHorse(worldObj);
                new_ghost_horse.setPosition(posX, posY, posZ);
                worldObj.spawnEntityInWorld(new_ghost_horse);
                MoCTools.playCustomSound(this, "appearmagic", worldObj);

                EntityPlayer owner = worldObj.getPlayerEntityByName(this.getOwnerName());
                
                new_ghost_horse.setOwner(this.getOwnerName());
                new_ghost_horse.setTamed(true);
                

                if (owner != null)
                {
                    MoCTools.tameWithName(owner, new_ghost_horse);
                    owner.addStat(MoCAchievements.ghost_horse, 1);
                }
                

                new_ghost_horse.setAdult(false);
                new_ghost_horse.setMoCAge(1);
                
                int ghost_horse_type = 22;
                
                if (this.isFlyer()) {ghost_horse_type = 21;}
                
                new_ghost_horse.setType(ghost_horse_type);
            }
            
        }
    }

    @Override
    public void onLivingUpdate()
    {
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

        if ((this.jumpPending))
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
        
        
        

        if (isUndead() && (this.getType() < 26) && getIsAdult() && (rand.nextInt(20) == 0))
        {
            if (MoCreatures.isServer())
            {
                if (rand.nextInt(16) == 0)
                {
                    setMoCAge(getMoCAge() + 1);
                }
                if (getMoCAge() >= 399)
                {
                    setType(this.getType() + 3);
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
            /**
             * Shuffling from "Party Rock Anthem" by LMFAO!
             */
            if (this.getType() == 60 && getIsTamed() && rand.nextInt(50) == 0 && nearMusicBox())
            {
                shuffle();
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageShuffle(this.getEntityId(), true), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
            }

            if ((rand.nextInt(300) == 0) && (deathTime == 0))
            {
                this.setHealth(getHealth() + 1);
                if (getHealth() > getMaxHealth())
                {
                    this.setHealth(getMaxHealth());
                }
            }

            if (!getEating() && !getIsTamed() && rand.nextInt(300) == 0)
            {
                setEating(true);
            }

            if (getEating() && ++countEating > 50 && !getIsTamed())
            {
                countEating = 0;
                setEating(false);
            }

            if ((getType() == 38) && (riddenByEntity != null) && (getNightmareInt() > 0) && (rand.nextInt(2) == 0))
            {
                NightmareEffect();
            }

            /**
             * zebras on the run!
             */
            if (this.getType() == 60 && !getIsTamed())
            {
                boolean flag = isZebraRunning();

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
                    MoCEntityHorse mommy = getClosestMommy(this, 16D);
                    if (mommy != null)
                    {
                        mommy.setBred(false);
                    }
                }
            }

            // TODO test in MP or move out of this !isRemote
            /**
             * Horse Ramming
             */
            if ((sprintCounter > 0 && sprintCounter < 150) && isUnicorned() && (riddenByEntity != null))
            {
            	
                MoCTools.buckleMobs(this, 2F, 2D, worldObj);
            }

            if (isFlyer() && rand.nextInt(100) == 0 && !isMovementCeased() && !getEating())
            {
                wingFlap();
            }

            if (getHasBred() && !getIsAdult() && (roper == null) && !getEating())
            {

                MoCEntityHorse mommy = getClosestMommy(this, 16D);
                if ((mommy != null) && (MoCTools.getSqDistanceTo(mommy, this.posX, this.posY, this.posZ) > 4D))
                {
                    // System.out.println("following mommy!");
                    PathEntity pathentity = worldObj.getPathEntityToEntity(this, mommy, 16F, true, false, false, true);
                    setPathToEntity(pathentity);
                }

            }

            if (!ReadyforParenting(this)) { return; }

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
                
                if (!ReadyforParenting(this)) return;
                
                if (!flag)
                {
                    if (!ReadyforParenting((MoCEntityHorse)horsemate))
                    {    
                        return;
                    }
                }
                
                if (rand.nextInt(100) == 0)
                {
                    gestationtime++;
                }

                if (this.gestationtime % 3 == 0)
                {
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(this.getEntityId()), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
                }

                if (gestationtime <= 50)
                {
                    continue;
                }
                MoCEntityHorse baby = new MoCEntityHorse(worldObj);
                baby.setPosition(posX, posY, posZ);
                worldObj.spawnEntityInWorld(baby);
                eatenpumpkin = false;
                gestationtime = 0;
                this.setBred(true);
                
                int horsemateType;// = 0;
                if (flag)
                {
                    horsemateType = TranslateVanillaHorseType((EntityHorse) horsemate);   
                    if (horsemateType == -1) return;
                }else
                {
                    horsemateType = ((MoCEntityHorse)horsemate).getType();
                    ((MoCEntityHorse)horsemate).eatenpumpkin = false;
                    ((MoCEntityHorse)horsemate).gestationtime = 0;
                }
                int type = HorseGenetics(this.getType(), horsemateType);
                
                
                baby.setOwner(this.getOwnerName());
                baby.setTamed(true);
                baby.setBred(true);
                baby.setAdult(false);
                EntityPlayer owner = worldObj.getPlayerEntityByName(this.getOwnerName());
                
                if (owner != null)
                {
                    MoCTools.tameWithName(owner, baby);
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
                        ((MoCEntityHorse)horsemate).dissapearHorse();
                        }
                    this.dissapearHorse();
                }
                
                else if ((type == 61) && owner != null) //zorse
                {
                	owner.addStat(MoCAchievements.zorse, 1);
                }
                
                baby.setType(type);
                
                baby.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth()); //set max health to the new type
                baby.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed to new type
                
                break;
            }
        }

    }

    /**
     * Obtains the 'Type' of vanilla horse for inbreeding with MoC Horses
     * @param horse
     * @return
     */
    private int TranslateVanillaHorseType(EntityHorse horse)
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

        if (shuffleCounter > 0)
        {
            ++shuffleCounter; //TODO: Fix Zebras not shuffling. For some reason whenever shuffleCounter is called outside MoCEntityHorse.java it is always 0. It may have something to do with the nearMusicBox() function
            if (!MoCreatures.isServer() && this.shuffleCounter % 20 == 0)
            {
                double var2 = this.rand.nextGaussian() * 0.5D;
                double var4 = this.rand.nextGaussian() * -0.1D;
                double var6 = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle("note", this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width, this.posY + 0.5D + this.rand.nextFloat() * this.height, this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, var2, var4, var6);
            }

            if ((MoCreatures.isServer() && !nearMusicBox()))
            {
                shuffleCounter = 0;
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageShuffle(this.getEntityId(), false), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
            }
            
        }

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
                MoCTools.dropHorseAmulet(this);
                dissapearHorse();
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
                    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth()); //set max health to the new type
                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed to new type
                }
            }
        }

        if (isGhost() && getMoCAge() < 10 && rand.nextInt(7) == 0)
        {
            setMoCAge(getMoCAge() + 1);
        }

        if (isGhost() && getMoCAge() == 9)
        {
            setMoCAge(100);
            setAdult(true);
        }
    }

    private void openMouth()
    {
        mouthCounter = 1;
    }

    public boolean ReadyforParenting(MoCEntityHorse entityhorse)
    {
        int i = entityhorse.getType();
        return (entityhorse.riddenByEntity == null) && (entityhorse.ridingEntity == null) && entityhorse.getIsTamed() && entityhorse.eatenpumpkin && entityhorse.getIsAdult() && !entityhorse.isUndead() && !entityhorse.isGhost() && (i != 61) && (i < 66);
    }

    @Override
    public boolean renderName()
    {
        return getDisplayName() && (riddenByEntity == null);
    }

    @Override
    public boolean rideableEntity()
    {
        return true;
    }

    @Override
    public double roperYOffset()
    {
        if (this.getIsAdult())
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
            
            int j = rand.nextInt(100);
            
            if (j <= 56) {setType(7);}
            else if (j <= 89) {setType(8);}
            else {setType(6);}
        }
        
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(calculateMaxHealth()); //set max health according to the type
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(getCustomSpeed()); //set speed according to type
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

    public void setNightmareInt(int i)
    {
        nightmareInt = i;
    }

    public void setReproduced(boolean var1)
    {
        hasReproduced = var1;
    }

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
        if (this.shuffleCounter == 0)
        {
            this.shuffleCounter = 1;
        }

        EntityPlayer ep1 = worldObj.getClosestPlayerToEntity(this, 8D);
        if (ep1 != null)
        {
            this.faceEntity(ep1, 30F, 30F); // stare at player
        }
    }

    private void stand()
    {
        if (this.riddenByEntity == null && !this.isOnAir())
        {
            standCounter = 1;
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
        if (++this.flickerCounter > 60)
        {
            this.flickerCounter = 0;
            this.transparencyFloat = (rand.nextFloat() * (0.6F - 0.3F) + 0.3F);
        }

        if (isGhost() && getMoCAge() < 10)
        {
            transparencyFloat = 0;
        }
        return this.transparencyFloat;
    }

    public void transform(int tType)
    {
        if (MoCreatures.isServer())
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), tType), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
        }

        transformType = tType;
        if (this.riddenByEntity == null && transformType != 0)
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
        this.motionX = 0D;
        this.motionZ = 0D;

        if (this.isBagger())
        {
            MoCTools.dropInventory(this, this.localhorsechest);
            dropBags();
        }
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageVanish(this.getEntityId()), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 64));
            setVanishC((byte) 1);
        }
        MoCTools.playCustomSound(this, "vanish", worldObj);
    }

    @Override
    public void dropMyStuff() 
    {
        dropArmor(); 
        MoCTools.dropSaddle(this, worldObj); 
        if (this.isBagger())
        {
            MoCTools.dropInventory(this, this.localhorsechest);
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
    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", getIsRideable());
        nbttagcompound.setBoolean("EatingHaystack", getEating());
        nbttagcompound.setBoolean("ChestedHorse", getChestedHorse());
        nbttagcompound.setBoolean("HasReproduced", getHasReproduced());
        nbttagcompound.setBoolean("Bred", getHasBred());
        nbttagcompound.setBoolean("DisplayName", getDisplayName());
        nbttagcompound.setInteger("ArmorType", getArmorType());

        if (getChestedHorse() && localhorsechest != null)
        {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < localhorsechest.getSizeInventory(); i++)
            {
                // grab the current item stack
                localstack = localhorsechest.getStackInSlot(i);
                if (localstack != null)
                {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    localstack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbttagcompound.setTag("Items", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
        setRideable(nbttagcompound.getBoolean("Saddle"));
        setEating(nbttagcompound.getBoolean("EatingHaystack"));
        setBred(nbttagcompound.getBoolean("Bred"));
        setChestedHorse(nbttagcompound.getBoolean("ChestedHorse"));
        setReproduced(nbttagcompound.getBoolean("HasReproduced"));
        setDisplayName(nbttagcompound.getBoolean("DisplayName"));
        setArmorType((byte) nbttagcompound.getInteger("ArmorType"));
        if (getChestedHorse())
        {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
            localhorsechest = new MoCAnimalChest(I18n.format("container.MoCreatures.HorseChest"), getInventorySize());

            for (int i = 0; i < nbttaglist.tagCount(); i++)
            {
                ItemStack itemstack = localhorsechest.getStackInSlot(i);

                if (itemstack != null)
                {
                    localhorsechest.setInventorySlotContents(i, itemstack.copy());
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
    protected boolean canBeTrappedInNet() 
    {
        return getIsTamed() && !isAmuletHorse();
    }

    @Override
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }
}