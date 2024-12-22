package drzhark.mocreatures.dimension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;

//this one is a copy of the end world provider

public class WorldProviderWyvernEnd extends WorldProviderSurface
{
    /**
     * creates a new world chunk manager for WorldProvider
     */
    @Override
	public void registerWorldChunkManager()
    {
        worldChunkMgr = new WorldChunkManagerWyvernLair(MoCreatures.WyvernLairBiome, 0.5F, 0.0F);
        setDimension(MoCreatures.wyvernLairDimensionID);
        setCustomSky();
    }

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
    @Override
	public IChunkProvider createChunkGenerator()
    {
        return new MoCChunkProviderWyvernLair(worldObj, worldObj.getSeed(), 0);
    }
    
   
    private void setCustomSky()
    {
        if (MoCreatures.isServer())
        {
            return;
        }
        setSkyRenderer(new MoCSkyRenderer());
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns array with sunrise/sunset colors
     */
    @Override
    public float[] calcSunriseSunsetColors(float par1, float par2)
    {
        return null;
    }

    @Override
	@SideOnly(Side.CLIENT)

    /**
     * Return vectorThreeDimensional with biome specific fog color
     */
    public Vec3 getFogColor(float par1, float par2)
    {
        float var4 = MathHelper.cos(par1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;

        if (var4 < 0.0F)
        {
            var4 = 0.0F;
        }

        if (var4 > 1.0F)
        {
            var4 = 1.0F;
        }

        float var5 = 0/255.0F;
        float var6 = 98/255.0F;
        float var7 = 73/255.0F;
        
        var5 *= var4 * 0.0F + 0.15F;
        var6 *= var4 * 0.0F + 0.15F;
        var7 *= var4 * 0.0F + 0.15F;
        return Vec3.createVectorHelper(var5, var6, var7);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public boolean isSkyColored()
    {
        return false;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    @Override
	public boolean canRespawnHere()
    {
        return false;
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    @Override
	public boolean isSurfaceWorld()
    {
        return false;
    }

    @Override
	@SideOnly(Side.CLIENT)

    /**
     * the y level at which clouds are rendered.
     */
    public float getCloudHeight()
    {
        return 76.0F;
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    @Override
	public boolean canCoordinateBeSpawn(int par1, int par2)
    {
        return worldObj.getTopBlock(par1, par2).getMaterial().blocksMovement();
    }

    /**
     * A Message to display to the user when they transfer out of this dismension.
     *
     * @return The message to be displayed
     */
    @Override
    public String getDepartMessage()
    {
           return "Leaving the Wyvern Lair";
    }
    
    @Override
    public String getWelcomeMessage()
    {
        return "Entering the Wyvern Lair";
    }
    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    @Override
	public ChunkCoordinates getEntrancePortalLocation()
    {
        return new ChunkCoordinates(0, 70, 0);
    }

    @Override
	public int getAverageGroundLevel()
    {
        return 50;
    }

    @Override
	@SideOnly(Side.CLIENT)

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    public boolean doesXZShowFog(int par1, int par2)
    {
        return true;
    }

    /**
     * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
     */
    @Override
	public String getDimensionName()
    {
        return "Wyvern Lair";
    }

     public String getSunTexture()
     {
         return "/mocreatures.twinsuns.png";
     }

    @Override
    public String getSaveFolder()
    {
        return "MoCWyvernLair";
    }

    @Override
    public double getMovementFactor()
    {
        return 1.0;
    }
}