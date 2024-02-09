package drzhark.mocreatures;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraftforge.common.BiomeDictionary.Type;

public class MoCEntityData {

    private EnumCreatureType typeOfCreature;
    private SpawnListEntry spawnListEntry;
    private String entityName;
    private boolean canSpawn = true;
    private int entityId;
    private List<Type> biomeTypes;
    
    //@SideOnly(Side.CLIENT)
    private int frequency = 8;
    
    private int minGroup = 1;
    private int maxGroup = 1;
    private int maxSpawnInChunk = 1;

    public MoCEntityData(String name,  int maxchunk, EnumCreatureType type, SpawnListEntry spawnListEntry, List<Type> biomeTypes)
    {
        entityName = name;
        typeOfCreature = type;
        this.biomeTypes = biomeTypes;
        frequency = spawnListEntry.itemWeight;
        minGroup = spawnListEntry.minGroupCount;
        maxGroup = spawnListEntry.maxGroupCount;
        maxSpawnInChunk = maxchunk;
        this.spawnListEntry = spawnListEntry;
        MoCreatures.entityMap.put(spawnListEntry.entityClass, this);
    }

    public MoCEntityData(String name, int id, int maxchunk, EnumCreatureType type, SpawnListEntry spawnListEntry, List<Type> biomeTypes)
    {
        entityId = id;
        entityName = name;
        typeOfCreature = type;
        this.biomeTypes = biomeTypes;
        frequency = spawnListEntry.itemWeight;
        minGroup = spawnListEntry.minGroupCount;
        maxGroup = spawnListEntry.maxGroupCount;
        maxSpawnInChunk = maxchunk;
        this.spawnListEntry = spawnListEntry;
        MoCreatures.entityMap.put(spawnListEntry.entityClass, this);
    }

    public Class<? extends EntityLiving> getEntityClass()
    {
        return spawnListEntry.entityClass;
    }

    public EnumCreatureType getType()
    {
        if (typeOfCreature != null)
            return typeOfCreature;
        return null;
    }

    public void setType(EnumCreatureType type)
    {
        typeOfCreature = type;
    }

    public List<Type> getBiomeTypes()
    {
        return biomeTypes;
    }

    public int getEntityID()
    {
        return entityId;
    }

    public void setEntityID(int id)
    {
        entityId = id;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void setFrequency(int freq)
    {
        if (freq <= 0)
        {
            frequency = 0;
        }
        else 
        {
            frequency = freq;
        }
    }

    public int getMinSpawn()
    {
        return minGroup;
    }

    public void setMinSpawn(int min)
    {
        if (min <= 0)
        {
            minGroup = 0;
        }
        else 
        {
            minGroup = min;
        }
    }

    public int getMaxSpawn()
    {
        return maxGroup;
    }

    public void setMaxSpawn(int max)
    {
        if (max <= 0)
        {
            maxGroup = 0;
        }
        else 
        {
            maxGroup = max;
        }
    }

    public int getMaxInChunk()
    {
        return maxSpawnInChunk;
    }

    public void setMaxInChunk(int max)
    {
        if (max <= 0)
        {
            maxSpawnInChunk = 0;
        }
        else 
        {
            maxSpawnInChunk = max;
        }
    }

    public String getEntityName()
    {
        return entityName;
    }

    public void setEntityName(String name)
    {
        entityName = name;
    }

    public void setCanSpawn(boolean flag)
    {
        canSpawn = flag;
    }

    public boolean getCanSpawn()
    {
        return canSpawn;
    }

    public SpawnListEntry getSpawnListEntry()
    {
        return spawnListEntry;
    }
}
