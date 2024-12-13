package drzhark.mocreatures.entity.animal;

import java.util.List;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

public class MoCEntityEnt extends MoCEntityAnimal{

    public MoCEntityEnt(World world) 
    {
        super(world);
        setSize(1.4F, 7F);
        stepHeight = 2F;
    }

    @Override
    public void selectType()
    {
        if (getType() == 0)
        {
            setType(rand.nextInt(2)+1);
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
    	String fileName = "ent_oak.png";
    	
        switch (getType())
        {
	        case 2:
	            fileName = "ent_birch.png";
	            break;
	        default:
	            break;
        }
        
        return new ResourceLocation("mocreatures" + ":" + "textures/models/" + fileName);
    }
     
    @Override
    public float getMoveSpeed()
    {
         return 0.5F;
    }

    public float calculateMaxHealth()
    {
        return 40F;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (damageSource.getEntity() != null && damageSource.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer entityPlayer = (EntityPlayer)damageSource.getEntity();
            ItemStack currentItem = entityPlayer.getHeldItem();
            if (currentItem != null)
            {
                Item itemHeldByPlayer = currentItem.getItem();
                if (itemHeldByPlayer != null && itemHeldByPlayer instanceof ItemAxe)
                {
                    if ((worldObj.difficultySetting != EnumDifficulty.PEACEFUL) )
                    {
                        entityToAttack = entityPlayer;
                        
                    }
                    return super.attackEntityFrom(damageSource, damageTaken);
                }
            }
        }
        if (damageSource.isFireDamage())
        {
            return super.attackEntityFrom(damageSource, damageTaken);
        }
        return false;
    }

    @Override
    protected void dropFewItems(boolean flag, int x)
    {
        int chance = rand.nextInt(3);
        int amountToDrop = rand.nextInt(12)+ 4;
        int treeType = 0;
        if (getType() == 2) treeType = 2;
        if (chance == 0)
        {
            entityDropItem(new ItemStack(Blocks.log, amountToDrop, treeType), 0.0F);
            return;
        }
        if (chance == 1)
        {
            entityDropItem(new ItemStack(Items.stick, amountToDrop, 0), 0.0F);
            return;

        }
        entityDropItem(new ItemStack(Blocks.sapling, amountToDrop, treeType), 0.0F);
    }

    @Override
    protected String getDeathSound()
    {
        return "mob.zombie.woodbreak";
    }

    @Override
    protected String getHurtSound()
    {
        return "mob.zombie.wood";
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (entityToAttack == null && rand.nextInt(300) == 0)
        {
            plantOnFertileGround();
        }

        if (rand.nextInt(100) == 0)
        {
            attractCritter();
        }
    }

    private void attractCritter() 
    {
        List listOfEntitiesNearby = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(8D, 3D, 8D));
        int n = rand.nextInt(5)+1;
        int j = 0;
        for (int index = 0; index < listOfEntitiesNearby.size(); index++)
        {
            Entity entityNearby = (Entity) listOfEntitiesNearby.get(index);
            if (entityNearby instanceof EntityAnimal && entityNearby.width < 0.6F && entityNearby.height < 0.6F)
            {
                EntityAnimal entityAnimalNearby = (EntityAnimal) entityNearby;
                if (entityAnimalNearby.getEntityToAttack() == null && !MoCTools.isTamed(entityAnimalNearby)) 
                {
                    PathEntity pathEntity = this.worldObj.getPathEntityToEntity(this, entityAnimalNearby, 16.0F, true, false, false, true);
                    //entityanimal.setPathToEntity(pathentity);
                    entityAnimalNearby.setAttackTarget(this);
                    entityAnimalNearby.setPathToEntity(pathEntity);
                    j++;
                    //System.out.println("attracting " + entityanimal);
                    if (j>n) return;
                }

            }
        }
    }

    private boolean plantOnFertileGround() 
    {
        Block blockUnderFeet = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ));
        Block blockOnFeet = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));

        if (blockUnderFeet == Blocks.dirt)
        {
            int xCoord = MathHelper.floor_double(this.posX);
            int yCoord = MathHelper.floor_double(this.posY - 1);
            int zCoord = MathHelper.floor_double(this.posZ);
            Block block = Blocks.grass;
            BlockEvent.BreakEvent event = null;
            if (!this.worldObj.isRemote)
            {
                event = new BlockEvent.BreakEvent(xCoord, yCoord, zCoord, this.worldObj, block, 0, FakePlayerFactory.get((WorldServer)this.worldObj, MoCreatures.MOC_FAKE_PLAYER));
            }
            if (event != null && !event.isCanceled())
            {
                this.worldObj.setBlock(xCoord, yCoord, zCoord, block, 0, 3);
            }
            return false;
        }

        if (blockUnderFeet == Blocks.grass && blockOnFeet == Blocks.air)
        {
            int metaD = 0;
            Block fertileB = Block.getBlockById(getBlockToPlant());

            if (fertileB == Blocks.sapling)
            {
                if (getType() == 2) metaD = 2; //to place the right sapling
            }
            if (fertileB == Blocks.tallgrass)
            {
                metaD = rand.nextInt(2)+1; //to place grass or fern
            }

            boolean canPlant = true;
            // check perms first
            for (int x = -1; x <2; x++)
            {
                for (int z = -1; z <2; z++)
                {
                    int xCoord = MathHelper.floor_double(this.posX);
                    int yCoord = MathHelper.floor_double(this.posY);
                    int zCoord = MathHelper.floor_double(this.posZ);
                    BlockEvent.BreakEvent event = null;
                    if (!this.worldObj.isRemote)
                    {
                        event = new BlockEvent.BreakEvent(xCoord, yCoord, zCoord, this.worldObj, fertileB, metaD, FakePlayerFactory.get((WorldServer)this.worldObj, MoCreatures.MOC_FAKE_PLAYER));
                    }
                    if (event != null && event.isCanceled())
                    {
                        canPlant = false;
                        break;
                    }
                }
            }
            // plant if perm check passed
            if (canPlant)
            {
                for (int x = -1; x <2; x++)
                {
                    for (int z = -1; z <2; z++)
                    {
                        this.worldObj.setBlock(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z, fertileB, metaD, 3);
                    }
                }
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Returns a random BlockID to plant on fertile ground
     * @return
     */
    private int getBlockToPlant() 
    {
        switch(rand.nextInt(15))
        {
        case 0:
            return 31; //shrub
        case 1:
            return 37; //dandelion
        case 2:
            return 38; //rose
        case 3:
            return 39; //brown mushroom
        case 4:
            return 40; //red mushroom
        case 5:
            return 6; //sapling
        default:
            return 31;
        }
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected void attackEntity(Entity entity, float f)
    {
        if (this.attackTime <= 0 && (f < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 200;
            playSound("mocreatures:goatsmack", 1.0F, 1.0F + ((rand.nextFloat() - rand.nextFloat()) * 0.2F));
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3);
            MoCTools.pushEntityBack(this, entity, 2F);
        }
    }

    @Override
    public boolean isNotScared()
    {
        return true;
    }
}