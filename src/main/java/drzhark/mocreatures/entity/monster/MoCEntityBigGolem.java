package drzhark.mocreatures.entity.monster;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.achievements.MoCAchievements;
import drzhark.mocreatures.entity.MoCEntityMob;
import drzhark.mocreatures.entity.item.MoCEntityThrowableBlockForGolem;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import drzhark.mocreatures.network.message.MoCMessageTwoBytes;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.BlockEvent;

public class MoCEntityBigGolem extends MoCEntityMob implements IEntityAdditionalSpawnData {

    public int throwBlockCounter;
    public MoCEntityThrowableBlockForGolem tempBlock;
    private byte golemCubes[];
    private int hurtCounter = 0;
    private int smokeCounter;

    public MoCEntityBigGolem(World world)
    {
        super(world);
        texture = "golemt.png";
        setSize(1.5F, 4F);
    }

    @Override
	protected void applyEntityAttributes()
    {
      super.applyEntityAttributes();
      getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0D);
    }

    @Override
    public void writeSpawnData(ByteBuf data)
    {
        for (int index = 0; index < 23; index++)
        {
            data.writeByte(golemCubes[index]);
        }
    }

    @Override
    public void readSpawnData(ByteBuf data)
    {
        for (int index = 0; index < 23; index++)
        {
            golemCubes[index] = data.readByte();
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        initGolemCubes();
        dataWatcher.addObject(23, Byte.valueOf((byte) 0)); // gState - 0 spawned / 1 summoning blocks /2 has enemy /3 half life (harder) /4 dying
    }

    public int getGolemState()
    {
        return (dataWatcher.getWatchableObjectByte(23));
    }

    public void setGolemState(int b)
    {
        dataWatcher.updateObject(23, Byte.valueOf((byte) b));
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {
            if (getGolemState() == 0) //just spawned
            {
                EntityPlayer entityPlayer1 = worldObj.getClosestPlayerToEntity(this, 8D);
                if (entityPlayer1 != null)
                {
                    setGolemState(1); //activated
                }
            }

            if (getGolemState() == 1 && !isMissingCubes())//entityToAttack != null)
            {
                setGolemState(2); //is complete
            }

            if (getGolemState() > 2 && getGolemState() != 4 && entityToAttack == null)
            {
                setGolemState(1);
            }

            if (getGolemState() > 1 && entityToAttack != null && rand.nextInt(20) == 0)
            {
                if (getHealth() >= 30)
                {
                    setGolemState(2);
                }
                if (getHealth() < 30 && getHealth() >= 10)
                {
                    setGolemState(3); //more dangerous
                }
                if (getHealth() < 10)
                {
                    setGolemState(4); //dying
                }
            }

            if (getGolemState() != 0 && getGolemState() != 4 && isMissingCubes())
            {

                int freq = 21 - (getGolemState() * worldObj.difficultySetting.getDifficultyId());
                if (getGolemState() == 1)
                {
                    freq = 10;
                }
                if (rand.nextInt(freq) == 0)
                {
                    acquireBlock(2);
                }
            }

            if (getGolemState() == 4)
            {
                setPathToEntity(null);
                hurtCounter++;

                if (hurtCounter < 80 && rand.nextInt(3) == 0)
                {
                    acquireBlock(4);
                }

                if (hurtCounter == 120)
                {
                    MoCTools.playCustomSound(this, "golemdying", worldObj, 3F);
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 1), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
                }

                if (hurtCounter > 140)
                {
                    MoCTools.playCustomSound(this, "golemexplode", worldObj, 3F);
                    destroyGolem();
                }
            }
        }

        if (throwBlockCounter != 0)
        {
            if (throwBlockCounter++ == 50)
            {
                if (MoCreatures.isServer())
                {
                    shootBlock(entityToAttack);
                }

            }
            else if (throwBlockCounter > 70)
            {
                throwBlockCounter = 0;
            }

        }

        if (MoCreatures.proxy.getParticleFX() > 0 && getGolemState() == 4 && smokeCounter > 0)
        {
            for (int index = 0; index < 10; index++)
            {
                worldObj.spawnParticle("explode", posX, posY, posZ, rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
            }
        }
    }

    private void destroyGolem()
    {
        List<Integer> usedBlocks = usedCubes();
        if ((!usedBlocks.isEmpty()) && (MoCTools.mobGriefing(worldObj)) && (MoCreatures.proxy.golemDestroyBlocks))
        {
            for (int index = 0; index < usedBlocks.size(); index++)
            {
                Block block = Block.getBlockById(generateBlock(golemCubes[usedBlocks.get(index)]));
                EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(block, 1, 0));
                entityItem.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(entityItem);
            }
        }
        
        EntityPlayer player = worldObj.getClosestPlayerToEntity(this, 8D);
        if (player != null) {player.addStat(MoCAchievements.kill_big_golem, 1);}
        
        setDead();
    }

    @Override
    protected boolean isMovementCeased()
    {
        return getGolemState() == 4;
    }

    protected void acquireBlock(int type)
    {
        //finds a missing block spot in its body
        //looks for a random block around it
        int[] myBlockCoords = new int[] { -9999, -1, -1 };
        myBlockCoords = MoCTools.getRandomBlockCoords(this, 24D);
        if (myBlockCoords[0] == -9999) { return; }

        boolean canDestroyBlocks = MoCTools.mobGriefing(worldObj) && MoCreatures.proxy.golemDestroyBlocks;
        
            Block block = worldObj.getBlock(myBlockCoords[0], myBlockCoords[1], myBlockCoords[2]);
        
        int tileBlockID = Block.getIdFromBlock(worldObj.getBlock(MathHelper.floor_double(myBlockCoords[0]), MathHelper.floor_double(myBlockCoords[1]), MathHelper.floor_double(myBlockCoords[2])));
        
        if (tileBlockID == 0 || tileBlockID == 7) {return;} //ignore air blocks (0) and bedrock (7)
        
        int tileBlockMetadata = worldObj.getBlockMetadata(MathHelper.floor_double(myBlockCoords[0]), MathHelper.floor_double(myBlockCoords[1]), MathHelper.floor_double(myBlockCoords[2]));
        
            BlockEvent.BreakEvent event = null;
            if (!worldObj.isRemote)
            {
            event = new BlockEvent.BreakEvent(myBlockCoords[0], myBlockCoords[1], myBlockCoords[2], worldObj, block, tileBlockMetadata, FakePlayerFactory.get((WorldServer)worldObj, MoCreatures.MOC_FAKE_PLAYER));
            }
        if (canDestroyBlocks && event != null && !event.isCanceled())
            {
                //destroys the original block
                worldObj.setBlock(myBlockCoords[0], myBlockCoords[1], myBlockCoords[2], Blocks.air, 0, 3);
            }
        
        MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(worldObj, this, myBlockCoords[0], myBlockCoords[1]+1, myBlockCoords[2]);//, false, true);
        
        if (!canDestroyBlocks) //make cheap blocks
        {
            tileBlockID = returnRandomCheapBlock();
            tileBlockMetadata = 0;
        }

        entityBlock.setType(tileBlockID);
        entityBlock.setMetadata(tileBlockMetadata);
        entityBlock.setBehavior(type);//so the block: 2 follows the EntityGolem  or 3 - gets around the golem
        
        //spawns the new entityBlock
        worldObj.spawnEntityInWorld(entityBlock);
    }

    /**
     * returns a random block when the golem is unable to break blocks
     * @return
     */
    private int returnRandomCheapBlock()
    {
        int random_number = rand.nextInt(4);
        switch (random_number)
        {
        case 0:
            return 3; //dirt
        case 1:
            return 4; //cobblestone
        case 2:
            return 5; //wood
        case 3:
            return 79; //ice
        }
        return 3;
    }

    /**
     * When the golem receives the block, called from within EntityBlock
     * 
     * @param ID
     *            = block id
     * @param Metadata
     *            = block Metadata
     */
    public void receiveBlock(int ID, int Metadata)
    {
        if (MoCreatures.isServer())
        {
            byte myBlock = translateOre(ID);
            byte slot = (byte) getRandomCubeAdj();
            if ((slot != -1) && (slot < 23) && (myBlock != -1) && getGolemState() != 4)
            {
                MoCTools.playCustomSound(this, "golemattach", worldObj, 3F);
                
                int worldDifficulty = worldObj.difficultySetting.getDifficultyId();
                
                setHealth(getHealth() + worldDifficulty);
                
                if (getHealth() > getMaxHealth())
                {
                    setHealth(getMaxHealth());
                }
                saveGolemCube(slot, myBlock);
            }
            else
            {
                MoCTools.playCustomSound(this, "turtlehurt", worldObj, 2F);
                if ((MoCTools.mobGriefing(worldObj)) && (MoCreatures.proxy.golemDestroyBlocks))
                {
                    EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(Block.getBlockById(ID), 1, Metadata));
                    entityItem.delayBeforeCanPickup = 10;
                    entityItem.age = 4000;
                }
            }
        }
    }

    /**
     * Not used!
     */
    protected void attackWithEntityBlock()
    {
        //TODO add metadata!!
        throwBlockCounter++;// += 1;
        if (throwBlockCounter == 5)
        {
            //creates a dummy tileBlock on top of it
            MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(worldObj, this, posX, posY + 2.0D, posZ);//, true, false);
            worldObj.spawnEntityInWorld(entityBlock);
            //removes a block from the environment and uses its type for the tileBlock
            entityBlock.setType(MoCTools.destroyRandomBlock(this, 5D));
            entityBlock.setBehavior(1);
            tempBlock = entityBlock;
        }

        if ((throwBlockCounter >= 5) && (throwBlockCounter < 200))
        {
            //maintains position of entityBlock above head
            tempBlock.posX = posX;
            tempBlock.posY = (posY + 3.0D);
            tempBlock.posZ = posZ;
        }

        if (throwBlockCounter >= 200)
        {
            //throws a newly spawned entityBlock and destroys the held entityBlock
            if (entityToAttack != null)
            {
                throwBlockAtEntity(entityToAttack, tempBlock.getType(), 0);
            }

            tempBlock.setDead();
            throwBlockCounter = 0;
        }
    }

    @Override
    protected void attackEntity(Entity entity, float distanceToEntity)
    {

        if ((distanceToEntity > 5.0F) && entity != null && throwBlockCounter == 0 && canShoot()) //attackTime <= 0 &&
        {
            throwBlockCounter = 1;
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(getEntityId(), 0), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
            return;
        }

        if (attackTime <= 0 && (distanceToEntity < 2.5D) && (entity.boundingBox.maxY > boundingBox.minY) && (entity.boundingBox.minY < boundingBox.maxY))
        {
            attackTime = 20;
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10);
        }
    }

    @Override
    public void performAnimation(int animationType)
    {
        if (animationType == 0) //block throwing animation
        {
            throwBlockCounter = 1;
        }
        if (animationType == 1) //smoking animation
        {
            smokeCounter = 1;
        }
    }

    private void shootBlock(Entity entity)
    {
        if (entity == null) { return; }
        List<Integer> armBlocks = new ArrayList<Integer>();

        for (int index = 9; index < 15; index++)
        {
            if (golemCubes[index] != 30)
            {
                armBlocks.add(index);
            }
        }
        if (armBlocks.isEmpty()) { return; }

        int idOfLastBlockInArm = rand.nextInt(armBlocks.size());
        int tempBlockId = armBlocks.get(idOfLastBlockInArm);
        int idOfBlockToBeThrown = tempBlockId;

        if (tempBlockId == 9 || tempBlockId == 12)
        {
            if (golemCubes[tempBlockId + 2] != 30)
            {
                idOfBlockToBeThrown = tempBlockId + 2;
            }
            else if (golemCubes[tempBlockId + 1] != 30)
            {
                idOfBlockToBeThrown = tempBlockId + 1;
            }
        }

        if (tempBlockId == 10 || tempBlockId == 13)
        {
            if (golemCubes[tempBlockId + 1] != 30)
            {
                idOfBlockToBeThrown = tempBlockId + 1;
            }
        }
        MoCTools.playCustomSound(this, "golemshoot", worldObj, 3F);
        throwBlockAtEntity(entity, generateBlock(golemCubes[idOfBlockToBeThrown]), 0);
        saveGolemCube((byte) idOfBlockToBeThrown, (byte) 30);
        throwBlockCounter = 0;
    }

    private boolean canShoot()
    {
        int amountOfBlockThatCanBeThrown = 0;
        for (byte index = 9; index < 15; index++)
        {
            if (golemCubes[index] != 30)
            {
                amountOfBlockThatCanBeThrown++;
            }
        }
        return (amountOfBlockThatCanBeThrown != 0) && getGolemState() != 4 && getGolemState() != 1;
    }

    @Override
    protected Entity findPlayerToAttack()
    {
        EntityPlayer closestEntityPlayer = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
        return closestEntityPlayer != null && canEntityBeSeen(closestEntityPlayer) ? closestEntityPlayer : null;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageTaken)
    {
        if (getGolemState() == 4) { return false; }

        List missingChestBlocks = missingChestCubes();
        boolean isChestUncovered = (missingChestBlocks.size() == 4);
        if (!openChest() && !isChestUncovered && getGolemState() != 1)
        {
            int worldDifficulty = worldObj.difficultySetting.getDifficultyId();
            if (MoCreatures.isServer() && rand.nextInt(worldDifficulty) == 0)
            {
                destroyRandomGolemCube();
            }
            else
            {
                MoCTools.playCustomSound(this, "turtlehurt", worldObj, 2F);
            }

            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        if (damageTaken > 5)
        {
            damageTaken = 5; //so you can't hit a Golem too hard
        }
        if (getGolemState() != 1 && super.attackEntityFrom(damageSource, damageTaken))
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        if (getGolemState() == 1)
        {
            Entity entityThatAttackedThisCreature = damageSource.getEntity();
            if ((entityThatAttackedThisCreature != this) && (worldObj.difficultySetting.getDifficultyId() > 0))
            {
                entityToAttack = entityThatAttackedThisCreature;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Destroys a random cube, with the proper check for extremities and spawns
     * a block in world
     */
    private void destroyRandomGolemCube()
    {
        int listPositionOfUsedCube = getRandomUsedCube();
        if (listPositionOfUsedCube == 4) { return;
        //do not destroy the valueable back cube
        }

        int listPositionOfUsedCubeToDestroy = listPositionOfUsedCube;
        if (listPositionOfUsedCube == 10 || listPositionOfUsedCube == 13 || listPositionOfUsedCube == 16 || listPositionOfUsedCube == 19)
        {
            if (golemCubes[listPositionOfUsedCube + 1] != 30)
            {
                listPositionOfUsedCubeToDestroy = listPositionOfUsedCube + 1;
            }

        }

        if (listPositionOfUsedCube == 9 || listPositionOfUsedCube == 12 || listPositionOfUsedCube == 15 || listPositionOfUsedCube == 18)
        {
            if (golemCubes[listPositionOfUsedCube + 2] != 30)
            {
                listPositionOfUsedCubeToDestroy = listPositionOfUsedCube + 2;
            }
            else if (golemCubes[listPositionOfUsedCube + 1] != 30)
            {
                listPositionOfUsedCubeToDestroy = listPositionOfUsedCube + 1;
            }
        }

        if (listPositionOfUsedCubeToDestroy != -1 && golemCubes[listPositionOfUsedCubeToDestroy] != 30)
        {
            Block block = Block.getBlockById(generateBlock(golemCubes[listPositionOfUsedCubeToDestroy]));
            saveGolemCube((byte) listPositionOfUsedCubeToDestroy, (byte) 30);
            MoCTools.playCustomSound(this, "golemhurt", worldObj, 3F);
            if ((MoCTools.mobGriefing(worldObj)) && (MoCreatures.proxy.golemDestroyBlocks))
            {
                EntityItem entityItem = new EntityItem(worldObj, posX, posY, posZ, new ItemStack(block, 1, 0));
                entityItem.delayBeforeCanPickup = 10;
                worldObj.spawnEntityInWorld(entityItem);
            }
        }
    }

    @Override
    public float getAdjustedYOffset()
    {
        if (golemCubes[17] != 30 || golemCubes[20] != 30)
        {
            //has feet
            return 0F;
        }
        if (golemCubes[16] != 30 || golemCubes[19] != 30)
        {
            //has knees but not feet
            return 0.4F;
        }
        if (golemCubes[15] != 30 || golemCubes[18] != 30)
        {
            //has thighs but not knees or feet
            return 0.7F;
        }

        if (golemCubes[1] != 30 || golemCubes[3] != 30)
        {
            //has lower chest
            return 0.8F;
        }
        //missing everything
        return 1.45F;
    }

    /**
     * Stretches the model to that size
     */
    @Override
    public float getSizeFactor()
    {
        return 1.8F;
    }

    /**
     * Throws block at entity
     * 
     * @param targetEntity
     * @param blockType
     * @param metadata
     */
    protected void throwBlockAtEntity(Entity targetEntity, int blockType, int metadata)
    {
        throwBlockAtCoordinates((int) targetEntity.posX, (int) targetEntity.posY, (int) targetEntity.posZ, blockType, metadata);
    }

    /**
     * Throws block at X,Y,Z coordinates
     * 
     * @param X
     * @param Y
     * @param Z
     * @param blockType
     * @param metadata
     */
    protected void throwBlockAtCoordinates(int X, int Y, int Z, int blockType, int metadata)
    {
        MoCEntityThrowableBlockForGolem entityBlock = new MoCEntityThrowableBlockForGolem(worldObj, this, posX, posY + 3.0D, posZ);//, false, false);
        worldObj.spawnEntityInWorld(entityBlock);
        entityBlock.setType(blockType);
        entityBlock.setMetadata(metadata);
        entityBlock.setBehavior(0);
        entityBlock.motionX = ((X - posX) / 20.0D);
        entityBlock.motionY = ((Y - posY) / 20.0D + 0.5D);
        entityBlock.motionZ = ((Z - posZ) / 20.0D);
    }

    /**
     * @param i
     *            = slot
     * @return the block type stored in that slot. 30 = empty
     */
    public byte getBlockTexture(int i)
    {
        return golemCubes[i];
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeEntityToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("golemState", getGolemState());
        NBTTagList cubeLists = new NBTTagList();

        for (int index = 0; index < 23; index++)
        {
            NBTTagCompound nbttag = new NBTTagCompound();
            nbttag.setByte("Slot", golemCubes[index]);
            cubeLists.appendTag(nbttag);
        }
        nbtTagCompound.setTag("GolemBlocks", cubeLists);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readEntityFromNBT(nbtTagCompound);
        setGolemState(nbtTagCompound.getInteger("golemState"));
        NBTTagList nbttaglist = nbtTagCompound.getTagList("GolemBlocks", 10);
        for (int i = 0; i < 23; i++)
        {
            NBTTagCompound var4 = nbttaglist.getCompoundTagAt(i);
            golemCubes[i] = var4.getByte("Slot");
        }
    }

    /**
     * Initializes the goleCubes array
     */
    private void initGolemCubes()
    {
        golemCubes = new byte[23];

        for (int index = 0; index < 23; index++)
        {
            golemCubes[index] = 30;
        }

        int j = rand.nextInt(4);
        switch (j)
        {
            case 0:
                j = 7;
                break;
            case 1:
                j = 11;
                break;
            case 2:
                j = 15;
                break;
            case 3:
                j = 21;
                break;
        }
        saveGolemCube((byte) 4, (byte) j);
    }

    /**
     * Saves the type of Cube(value) on the given 'slot' if server, then sends a
     * packet to the clients
     * 
     * @param slot
     * @param value
     */
    public void saveGolemCube(byte slot, byte value)
    {
        golemCubes[slot] = value;
        if (MoCreatures.isServer() && MoCreatures.proxy.worldInitDone) // Fixes CMS initialization during world load
        {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageTwoBytes(getEntityId(), slot, value), new TargetPoint(worldObj.provider.dimensionId, posX, posY, posZ, 64));
        }
    }

    /**
     * returns a list of the empty blocks
     * 
     * @return
     */
    private List missingCubes()
    {
        List<Integer> emptyBlocks = new ArrayList<Integer>();

        for (int index = 0; index < 23; index++)
        {
            if (golemCubes[index] == 30)
            {
                emptyBlocks.add(index);
            }
        }
        return emptyBlocks;
    }

    /**
     * Returns true if is 'missing' any cube, false if it's full
     * 
     * @return
     */
    public boolean isMissingCubes()
    {
        for (int index = 0; index < 23; index++)
        {
            if (golemCubes[index] == 30) { return true; }
        }
        return false;
    }

    private List missingChestCubes()
    {
        List<Integer> emptyChestBlocks = new ArrayList<Integer>();

        for (int index = 0; index < 4; index++)
        {
            if (golemCubes[index] == 30)
            {
                emptyChestBlocks.add(index);

            }

        }
        return emptyChestBlocks;
    }

    /**
     * returns a list of the used block spots
     * 
     * @return
     */
    private List usedCubes()
    {
        List<Integer> usedBlocks = new ArrayList<Integer>();

        for (int index = 0; index < 23; index++)
        {
            if (golemCubes[index] != 30)
            {
                usedBlocks.add(index);
            }
        }
        return usedBlocks;
    }

    /**
     * Returns a random used cube position if the golem is empty, returns -1
     * 
     * @return
     */
    private int getRandomUsedCube()
    {
        List<Integer> usedBlocks = usedCubes();
        if (usedBlocks.isEmpty()) { return -1; }
        int randomEmptyBlock = rand.nextInt(usedBlocks.size());
        return usedBlocks.get(randomEmptyBlock);
    }

    /**
     * Returns a random empty cube position if the golem is full, returns -1
     * 
     * @return
     */
    private int getRandomMissingCube()
    {
        //first it makes sure it has the four chest cubes
        List<Integer> emptyChestBlocks = missingChestCubes();
        if (!emptyChestBlocks.isEmpty())
        {
            int randomEmptyBlock = rand.nextInt(emptyChestBlocks.size());
            return emptyChestBlocks.get(randomEmptyBlock);
        }

        //otherwise returns any other cube
        List<Integer> emptyBlocks = missingCubes();
        if (emptyBlocks.isEmpty())
        {
            return -1;
        }
        int randomEmptyBlock = rand.nextInt(emptyBlocks.size());
        return emptyBlocks.get(randomEmptyBlock);
    }

    /**
     * returns the position of the cube to be added, contains logic for the
     * extremities
     * 
     * @return
     */
    private int getRandomCubeAdj()
    {
        int emptyCubePosition = getRandomMissingCube();

        if (emptyCubePosition == 10 || emptyCubePosition == 13 || emptyCubePosition == 16 || emptyCubePosition == 19)
        {
            if (golemCubes[emptyCubePosition - 1] == 30)
            {
                return emptyCubePosition - 1;
            }
            else
            {
                saveGolemCube((byte) emptyCubePosition, golemCubes[emptyCubePosition - 1]);
                return emptyCubePosition - 1;
            }
        }

        if (emptyCubePosition == 11 || emptyCubePosition == 14 || emptyCubePosition == 17 || emptyCubePosition == 20)
        {
            if (golemCubes[emptyCubePosition - 2] == 30 && golemCubes[emptyCubePosition - 1] == 30) { return emptyCubePosition - 2; }
            if (golemCubes[emptyCubePosition - 1] == 30)
            {
                saveGolemCube((byte) (emptyCubePosition - 1), golemCubes[emptyCubePosition - 2]);
                return emptyCubePosition - 2;
            }
            else
            {
                saveGolemCube((byte) emptyCubePosition, golemCubes[emptyCubePosition - 1]);
                saveGolemCube((byte) (emptyCubePosition - 1), golemCubes[emptyCubePosition - 2]);
                return emptyCubePosition - 2;
            }
        }
        return emptyCubePosition;
    }

    @Override
	public int rollRotationOffset()
    {
        int leftLeg = 0;
        int rightLeg = 0;
        if (golemCubes[15] != 30)
        {
            leftLeg++;
        }
        if (golemCubes[16] != 30)
        {
            leftLeg++;
        }
        if (golemCubes[17] != 30)
        {
            leftLeg++;
        }
        if (golemCubes[18] != 30)
        {
            rightLeg++;
        }
        if (golemCubes[19] != 30)
        {
            rightLeg++;
        }
        if (golemCubes[20] != 30)
        {
            rightLeg++;
        }
        return (leftLeg - rightLeg) * 10;
    }

    /**
     * The chest opens when the Golem is missing cubes and the summoned blocks
     * are close
     * 
     * @return
     */
    public boolean openChest()
    {
        if (isMissingCubes())
        {
            List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(2D, 2D, 2D));
            int iterationLength = entitiesNearbyList.size();
            
            if (iterationLength > 0)
            {
	            for (int index = 0; index < iterationLength; index++)
	            {
	                Entity entityNearby = (Entity) entitiesNearbyList.get(index);
	                
	                if (entityNearby instanceof MoCEntityThrowableBlockForGolem)
	                {
	                    if (MoCreatures.proxy.getParticleFX() > 0)
	                    {
	                        MoCreatures.proxy.VacuumFX(this);
	                    }
	                    return true;
	                }
	            }
            }
        }
        return false;
    }

    /**
     * Converts the world block into the golem block texture if not found,
     * returns -1
     * 
     * @param blockType
     * @return
     */
    private byte translateOre(int blockType)
    {
        switch (blockType)
        {
	        case 0:
	            return 0;
	        case 1:
	            return 0;
	        case 18:
	            return 10; //leaves
	        case 2:
	        case 3:
	            return 1; //dirt, grass
	        case 4:
	        case 48:
	            return 2; //cobblestones
	        case 5:
	            return 3;
	        case 12:
	            return 4;
	        case 13:
	            return 5;
	        case 16:
	        case 21:
	        case 56:
	        case 74:
	        case 73:
	            return 24; //all ores are transformed into diamond ore
	        case 14:
	        case 41:
	            return 7; //ore gold and block gold = block gold
	        case 15:
	        case 42:
	            return 11;//iron ore and blocks = block iron
	        case 57:
	            return 15; //block diamond
	        case 17:
	            return 6; //wood
	        case 20:
	            return 8;
	        case 22:
	        case 35: //lapis and cloths
	            return 9;
	        case 45:
	            return 12; //brick
	        case 49:
	            return 14; //obsidian
	        case 58:
	            return 16; //workbench
	        case 61:
	        case 62:
	            return 17; //stonebench
	        case 78:
	        case 79:
	            return 18; //ice
	        case 81:
	            return 19; //cactus
	        case 82:
	            return 20; //clay
	        case 86:
	        case 91:
	        case 103:
	            return 22; //pumpkin pumpkin lantern melon
	        case 87:
	            return 23; //netherrack
	        case 89:
	            return 25; //glowstone
	        case 98:
	            return 26; //stonebrick
	        case 112:
	            return 27; //netherbrick
	        case 129:
	        case 133:
	            return 21; //emeralds
	        default:
	            return -1;
        }
    }

    /**
     * Provides the blockID originated from the golem's block
     * 
     * @param golemBlock
     * @return
     */
    private int generateBlock(int golemBlock)
    {
        switch (golemBlock)
        {
	        case 0:
	            return 1;
	        case 1:
	            return 3;
	        case 2:
	            return 4;
	        case 3:
	            return 5;
	        case 4:
	            return 12;
	        case 5:
	            return 13;
	        case 6:
	            return 17;
	        case 7:
	            return 41;
	        case 8:
	            return 20;
	        case 9:
	            return 35;
	        case 10:
	            return 18;
	        case 11:
	            return 42;
	        case 12:
	            return 45;
	        case 13: //unused
	            return 2;
	        case 14:
	            return 49;
	        case 15:
	            return 57;
	        case 16:
	            return 58;
	        case 17:
	            return 51;
	        case 18:
	            return 79;
	        case 19:
	            return 81;
	        case 20:
	            return 82;
	        case 21:
	            return 133;
	        case 22:
	            return 86;
	        case 23:
	            return 87;
	        case 24:
	            return 56;
	        case 25:
	            return 89;
	        case 26:
	            return 98;
	        case 27:
	            return 112;
	        default:
	            return 2;
        }
    }

    @Override
    public float getMoveSpeed()
    {
        return 0.4F * (countLegBlocks() / 6F);
    }

    private int countLegBlocks()
    {
        int amountOfLegBlocks = 0;
        for (byte index = 15; index < 21; index++)
        {
            if (golemCubes[index] != 30)
            {
                amountOfLegBlocks++;
            }
        }
        return amountOfLegBlocks;
    }

    /**
     * Used for the power texture used on the golem
     * 
     * @return
     */
    public ResourceLocation getEffectTexture()
    {
        switch (getGolemState())
        {
	        case 1:
	            return MoCreatures.proxy.getTexture("golemeffect1.png");
	        case 2:
	            return MoCreatures.proxy.getTexture("golemeffect2.png");
	        case 3:
	            return MoCreatures.proxy.getTexture("golemeffect3.png");
	        case 4:
	            return MoCreatures.proxy.getTexture("golemeffect4.png");
	        default:
	            return null;
        }
    }

    /**
     * Used for the particle FX
     * 
     * @param i
     * @return
     */
    public float colorFX(int i)
    {
        switch (getGolemState())
        {
	        case 1:
	            if (i == 1) { return 65F / 255F; }
	            if (i == 2) { return 157F / 255F; }
	            if (i == 3) { return 254F / 255F; }
	        case 2:
	            if (i == 1) { return 244F / 255F; }
	            if (i == 2) { return 248F / 255F; }
	            if (i == 3) { return 36F / 255F; }
	        case 3:
	            if (i == 1) { return 255F / 255F; }
	            if (i == 2) { return 154F / 255F; }
	            if (i == 3) { return 21F / 255F; }
	        case 4:
	            if (i == 1) { return 248F / 255F; }
	            if (i == 2) { return 10F / 255F; }
	            if (i == 3) { return 10F / 255F; }
        }
        return 0;
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    @Override
    protected void func_145780_a(int par1, int par2, int par3, Block par4)
    {
        playSound("mocreatures:golemwalk", 1.0F, 1.0F);
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        return "mocreatures:golemgrunt";
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return (super.getCanSpawnHere() && worldObj.canBlockSeeTheSky(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ)) && (posY > 50D));
    }

	@Override
	public boolean entitiesThatAreScary(Entity entityNearby)
	{
		return false;
	}
}