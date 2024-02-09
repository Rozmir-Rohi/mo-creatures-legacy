package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityTameableAnimal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityBunny extends MoCEntityTameableAnimal {
    public boolean isPickedUp;
    public int bunnyReproduceTickerA;
    public int bunnyReproduceTickerB;

    // TODO check that bunnyLimit??

    public MoCEntityBunny(World world)
    {
        super(world);
        setAdult(true);
        setTamed(false);
        setMoCAge(50);
        setSize(0.4F, 0.4F);
        bunnyReproduceTickerA = rand.nextInt(64);
        bunnyReproduceTickerB = 0;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        dataWatcher.addObject(22, Byte.valueOf((byte) 0)); // hasEaten - 0 false 1 true
    }

    public boolean getHasEaten()
    {
        return (dataWatcher.getWatchableObjectByte(22) == 1);
    }

    public void setHasEaten(boolean flag)
    {
        byte input = (byte) (flag ? 1 : 0);
        dataWatcher.updateObject(22, Byte.valueOf(input));
    }

    @Override
    public float getMoveSpeed()
    {
        return 1.5F;
    }

    @Override
    public void selectType()
    {
        checkSpawningBiome();
        
        if (getType() == 0)
        {
            setType(rand.nextInt(5)+1);
        }

    }

    @Override
    public boolean checkSpawningBiome()
    {
        int xCoordinate = MathHelper.floor_double(posX);
        int yCoordinate = MathHelper.floor_double(boundingBox.minY);
        int zCoordinate = MathHelper.floor_double(posZ);

        BiomeGenBase currentBiome = MoCTools.Biomekind(worldObj, xCoordinate, yCoordinate, zCoordinate);
        if (BiomeDictionary.isBiomeOfType(currentBiome, Type.SNOWY))
        {
            setType(3); //snow white bunnies!
            return true;
        }
        return true;
    }

    @Override
    public ResourceLocation getTexture()
    {
        switch (getType())
        {
        case 1:
            return MoCreatures.proxy.getTexture("bunny.png");
        case 2:
            return MoCreatures.proxy.getTexture("bunnyb.png");
        case 3:
            return MoCreatures.proxy.getTexture("bunnyc.png");
        case 4:
            return MoCreatures.proxy.getTexture("bunnyd.png");
        case 5:
            return MoCreatures.proxy.getTexture("bunnye.png");

        default:
            return MoCreatures.proxy.getTexture("bunny.png");
        }
    }

    @Override
    protected void fall(float f)
    {
    }

    @Override
    protected String getDeathSound()
    {
        return "mocreatures:rabbitdeath";
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:rabbithurt";
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    public double getYOffset()
    {
        if (ridingEntity instanceof EntityPlayer && ridingEntity == MoCreatures.proxy.getPlayer() && !MoCreatures.isServer()) { return (yOffset - 1.3F); }

        if ((ridingEntity instanceof EntityPlayer) && !MoCreatures.isServer())
        {
            return (yOffset + 0.3F);
        }
        else
        {
            return yOffset;
        }
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer)
    {
        if (super.interact(entityPlayer)) { return false; }

        ItemStack itemstack = entityPlayer.inventory.getCurrentItem();
        
        if (itemstack != null)
        {	
        	Item item = itemstack.getItem();
        	
        	List<String> oreDictionaryNameArray = MoCTools.getOreDictionaryEntries(itemstack);

        	if (    	
        			item == Items.carrot
        			|| item == Items.golden_carrot
        			|| (item.itemRegistry).getNameForObject(item).equals("etfuturum:beetroot")
        			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:food") && itemstack.getItemDamage() == 2 //BoP Wild Carrots
        			|| (item.itemRegistry).getNameForObject(item).equals("BiomesOPlenty:food") && itemstack.getItemDamage() == 11 //BoP Turnip
        			|| oreDictionaryNameArray.size() > 0 && oreDictionaryNameArray.contains("listAllveggie") //BOP veg or GregTech6 veg or Palm's Harvest veg

        		)
        		
        	{
            	if (--itemstack.stackSize == 0)
            	{
                	entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            	}
            
            	if (MoCreatures.isServer() && !getIsTamed())
            	{
                	MoCTools.tameWithName(entityPlayer, this);
            	}
            
            	if (getIsTamed() && !getHasEaten())
            	{
            		setHasEaten(true);
            	}
            
            	MoCTools.playCustomSound(this, "eating", worldObj);
            	heal(5);
            
            	return true;
        	}
        }
        	
        else
        {
        	if (getIsTamed())
        	{
        		rotationYaw = entityPlayer.rotationYaw;
        		if ((ridingEntity == null) && (entityPlayer.ridingEntity == null))
        		{
        			// This is required since the server will send a Packet39AttachEntity which informs the client to mount
        			if (MoCreatures.isServer())
        			{
        				mountEntity(entityPlayer);
        			}
        			isPickedUp = true;
        			return true;
        		}
        		
        		else if (ridingEntity == entityPlayer)
        		{
        			worldObj.playSoundAtEntity(this, "mocreatures:rabbitlift", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
        			if (MoCreatures.isServer())
        			{
        				mountEntity(null);
        				motionX = entityPlayer.motionX * 5D;
            			motionY = (entityPlayer.motionY / 2D) + 0.5D;
                		motionZ = entityPlayer.motionZ * 5D;
        			}
        			isPickedUp = false;
            		return true;
        		}
        	}
        }        
        return true;
    }

    @Override
    public void onLivingUpdate()
    {

        if (MoCreatures.isServer() && !getIsAdult() && (rand.nextInt(200) == 0))
        {
            setMoCAge(getMoCAge() + 1);
            if (getMoCAge() >= 100)
            {
                setAdult(true);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (MoCreatures.isServer())
        {

            if (!getIsTamed() || !getIsAdult() || !getHasEaten() || (ridingEntity != null)) { return; }
            if (bunnyReproduceTickerA < 1023)
            {
                bunnyReproduceTickerA++;
            }
            else if (bunnyReproduceTickerB < 127)
            {
                bunnyReproduceTickerB++;
            }
            else
            {
                /*int k = worldObj.countEntities(getClass());
                if (k > MoCreatures.proxy.bunnyBreedThreshold)
                {
                    proceed();
                    return;
                }*/

                List list1 = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(4.0D, 4.0D, 4.0D));
                boolean flag = false;
                for (int i1 = 0; i1 < list1.size(); i1++)
                {
                    Entity entity1 = (Entity) list1.get(i1);
                    if (!(entity1 instanceof MoCEntityBunny) || (entity1 == this))
                    {
                        continue;
                    }
                    MoCEntityBunny entitybunny = (MoCEntityBunny) entity1;
                    if ((entitybunny.ridingEntity != null) || (entitybunny.bunnyReproduceTickerA < 1023) || !entitybunny.getIsAdult() || !entitybunny.getHasEaten())
                    {
                        continue;
                    }
                    MoCEntityBunny entitybunny1 = new MoCEntityBunny(worldObj);
                    entitybunny1.setPosition(posX, posY, posZ);
                    entitybunny1.setAdult(false);
                    int babytype = getType();
                    if (rand.nextInt(2) == 0)
                    {
                        babytype = entitybunny.getType();
                    }
                    entitybunny1.setType(babytype);
                    worldObj.spawnEntityInWorld(entitybunny1);
                    proceed();
                    entitybunny.proceed();
                    flag = true;
                    break;
                }
            }
        }
    }

    public void proceed()
    {
        setHasEaten(false);
        bunnyReproduceTickerB = 0;
        bunnyReproduceTickerA = rand.nextInt(64);
    }

    @Override
    protected void updateEntityActionState()
    {
        if (onGround && ((motionX > 0.05D) || (motionZ > 0.05D) || (motionX < -0.05D) || (motionZ < -0.05D)))
        {
            motionY = 0.45000000000000001D;
        }
        if (!isPickedUp)
        {
            super.updateEntityActionState();
        }
        else if (onGround && ridingEntity == null)
        {
            isPickedUp = false;
            //System.out.println("pickedOff");
            worldObj.playSoundAtEntity(this, "mocreatures:rabbitland", 1.0F, ((rand.nextFloat() - rand.nextFloat()) * 0.2F) + 1.0F);
            List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(12D, 12D, 12D));
            for (int k = 0; k < list.size(); k++)
            {
                Entity entity = (Entity) list.get(k);
                if (entity instanceof EntityMob)
                {
                    EntityMob entitymob = (EntityMob) entity;
                    entitymob.setAttackTarget(this);

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
    public int nameYOffset()
    {
        return -40;
    }

    @Override
    public double roperYOffset()
    {
        return 0.9D;
    }
    
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (MoCreatures.isServer())
        {
        	if (ridingEntity != null && 
        			(damageSource.getEntity() == ridingEntity || DamageSource.inWall.equals(damageSource)))
            {
         	   return false;
            }
        }
        
        return super.attackEntityFrom(damageSource, damageTaken);
    }
    
    @Override
    public boolean isSwimmerEntity()
    {
        return true;
    }
}