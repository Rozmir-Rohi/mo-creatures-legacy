package drzhark.mocreatures.dimension;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.Event.Result;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class MoCChunkProviderWyvernLair implements IChunkProvider
{
    private Random RNGa;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;
    public NoiseGeneratorOctaves noiseGen5;
    private World worldObj;
    private double[] densities;

    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;
    //int[][] field_73203_h = new int[32][32];
    byte[] metadat = new byte[32768];

    public MoCChunkProviderWyvernLair(World par1World, long par2)
    {
        worldObj = par1World;
        RNGa = new Random(par2);
        noiseGen1 = new NoiseGeneratorOctaves(RNGa, 16);
        noiseGen2 = new NoiseGeneratorOctaves(RNGa, 16);
        noiseGen3 = new NoiseGeneratorOctaves(RNGa, 8);
        noiseGen4 = new NoiseGeneratorOctaves(RNGa, 10);
        noiseGen5 = new NoiseGeneratorOctaves(RNGa, 16);

        NoiseGenerator[] noiseGens = {noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5};
        noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, RNGa, noiseGens);
        noiseGen1 = (NoiseGeneratorOctaves)noiseGens[0];
        noiseGen2 = (NoiseGeneratorOctaves)noiseGens[1];
        noiseGen3 = (NoiseGeneratorOctaves)noiseGens[2];
        noiseGen4 = (NoiseGeneratorOctaves)noiseGens[3];
        noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
   }

    /**
     * Calls ChunkProvider constructor, adding metadata that will be saved to every terrain block generated.
     * @param par1World
     * @param par2
     * @param metadata
     */
    public MoCChunkProviderWyvernLair(World par1World, long par2, int metadata)
    {
        this(par1World, par2);

        for (int i = 0; i<32768; i++)
        {
            metadat[i] = (byte)metadata;
        }
    }

    public void func_147420_a(int par1, int par2, Block[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        byte var5 = 2;
        int var6 = var5 + 1;
        byte var7 = 33;
        int var8 = var5 + 1;
        densities = initializeNoiseField(densities, par1 * var5, 0, par2 * var5, var6, var7, var8);

        for (int var9 = 0; var9 < var5; ++var9)
        {
            for (int var10 = 0; var10 < var5; ++var10)
            {
                for (int var11 = 0; var11 < 32; ++var11)
                {
                    double var12 = 0.25D;
                    double var14 = densities[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var16 = densities[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var18 = densities[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 0];
                    double var20 = densities[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 0];
                    double var22 = (densities[((var9 + 0) * var8 + var10 + 0) * var7 + var11 + 1] - var14) * var12;
                    double var24 = (densities[((var9 + 0) * var8 + var10 + 1) * var7 + var11 + 1] - var16) * var12;
                    double var26 = (densities[((var9 + 1) * var8 + var10 + 0) * var7 + var11 + 1] - var18) * var12;
                    double var28 = (densities[((var9 + 1) * var8 + var10 + 1) * var7 + var11 + 1] - var20) * var12;

                    for (int var30 = 0; var30 < 4; ++var30)
                    {
                        double var31 = 0.125D;
                        double var33 = var14;
                        double var35 = var16;
                        double var37 = (var18 - var14) * var31;
                        double var39 = (var20 - var16) * var31;

                        for (int var41 = 0; var41 < 8; ++var41)
                        {
                            int var42 = var41 + var9 * 8 << 11 | 0 + var10 * 8 << 7 | var11 * 4 + var30;
                            short var43 = 128;
                            double var44 = 0.125D;
                            double var46 = var33;
                            double var48 = (var35 - var33) * var44;

                            for (int var50 = 0; var50 < 8; ++var50)
                            {
                                Block block = null;

                                if (var46 > 0.0D)
                                {
                                    block = MoCreatures.mocStone;
                                }

                                par3ArrayOfByte[var42] = block;
                                var42 += var43;
                                var46 += var48;
                            }

                            var33 += var37;
                            var35 += var39;
                        }

                        var14 += var22;
                        var16 += var24;
                        var18 += var26;
                        var20 += var28;
                    }
                }
            }
        }
    }

    
    /**
     * Replaces the stone that was placed in with blocks that match the biome
     */
    public void func_147421_b(int par1, int par2, Block[] par3ArrayOfByte, BiomeGenBase[] par4ArrayOfBiomeGenBase)
    {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, par1, par2, par3ArrayOfByte, par4ArrayOfBiomeGenBase);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) 
        {
            return;
        }

        for (int var5 = 0; var5 < 16; ++var5)
        {
            for (int var6 = 0; var6 < 16; ++var6)
            {
                byte var7 = 5;
                int var8 = -1;
 
                BiomeGenBase biome = par4ArrayOfBiomeGenBase[var6 + var5 * 16];
                Block var9 = biome.topBlock;
                Block var10 = biome.fillerBlock;

                for (int var11 = 127; var11 >= 0; --var11)
                {
                    int var12 = (var6 * 16 + var5) * 128 + var11;
                    Block var13 = par3ArrayOfByte[var12];

                    if (var13 == null || var13.getMaterial() == Material.air)
                    {
                        var8 = -1;
                    }
                    else if (var13 == MoCreatures.mocStone)
                    {
                        if (var8 == -1)
                        {
                            var8 = var7;

                            if (var11 >= 0)
                            {
                                par3ArrayOfByte[var12] = var9;
                            }
                            else
                            {
                                par3ArrayOfByte[var12] = var10;
                            }
                        }
                        else if (var8 > 0)
                        {
                            --var8;
                            par3ArrayOfByte[var12] = var10;
                        }
                    }
                }
            }
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    @Override
	public Chunk loadChunk(int par1, int par2)
    {
        return provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    @Override
	public Chunk provideChunk(int par1, int par2)
    {
        RNGa.setSeed(par1 * 341873128712L + par2 * 132897987541L);
        Block[] var3 = new Block[32768];
        biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
        func_147420_a(par1, par2, var3, biomesForGeneration);
        func_147421_b(par1, par2, var3, biomesForGeneration);
        
        //to add metadata specific dimension info, used to reduce the number of block IDs with multiBLocks
        //changed constructor to add metadata
        Chunk var4 = new Chunk(worldObj, var3, metadat, par1, par2);
        
        byte[] var5 = var4.getBiomeArray();

        for (int var6 = 0; var6 < var5.length; ++var6)
        {
            var5[var6] = (byte)biomesForGeneration[var6].biomeID;
        }

        var4.generateSkylightMap();
        return var4;
    }

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7)
    {
        ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(this, par1ArrayOfDouble, par2, par3, par4, par5, par6, par7);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return event.noisefield;

        if (par1ArrayOfDouble == null)
        {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }

        double var8 = 684.412D;
        double var10 = 684.412D;
        noiseData4 = noiseGen4.generateNoiseOctaves(noiseData4, par2, par4, par5, par7, 1.121D, 1.121D, 0.5D);
        noiseData5 = noiseGen5.generateNoiseOctaves(noiseData5, par2, par4, par5, par7, 200.0D, 200.0D, 0.5D);
        var8 *= 2.0D;
        noiseData1 = noiseGen3.generateNoiseOctaves(noiseData1, par2, par3, par4, par5, par6, par7, var8 / 80.0D, var10 / 160.0D, var8 / 80.0D);
        noiseData2 = noiseGen1.generateNoiseOctaves(noiseData2, par2, par3, par4, par5, par6, par7, var8, var10, var8);
        noiseData3 = noiseGen2.generateNoiseOctaves(noiseData3, par2, par3, par4, par5, par6, par7, var8, var10, var8);
        int var12 = 0;
        int var13 = 0;

        for (int var14 = 0; var14 < par5; ++var14)
        {
            for (int var15 = 0; var15 < par7; ++var15)
            {
                double var16 = (noiseData4[var13] + 256.0D) / 512.0D;

                if (var16 > 1.0D)
                {
                    var16 = 1.0D;
                }

                double var18 = noiseData5[var13] / 8000.0D;

                if (var18 < 0.0D)
                {
                    var18 = -var18 * 0.3D;
                }

                var18 = var18 * 3.0D - 2.0D;
                float var20 = (var14 + par2 - 0) / 1.0F;
                float var21 = (var15 + par4 - 0) / 1.0F;
                float var22 = 100.0F - MathHelper.sqrt_float(var20 * var20 + var21 * var21) * 8.0F;

                if (var22 > 80.0F)
                {
                    var22 = 80.0F;
                }

                if (var22 < -100.0F)
                {
                    var22 = -100.0F;
                }

                if (var18 > 1.0D)
                {
                    var18 = 1.0D;
                }

                var18 /= 8.0D;
                var18 = 0.0D;

                if (var16 < 0.0D)
                {
                    var16 = 0.0D;
                }

                var16 += 0.5D;
                var18 = var18 * par6 / 16.0D;
                ++var13;
                double var23 = par6 / 2.0D;

                for (int var25 = 0; var25 < par6; ++var25)
                {
                    double var26 = 0.0D;
                    double var28 = (var25 - var23) * 8.0D / var16;

                    if (var28 < 0.0D)
                    {
                        var28 *= -1.0D;
                    }

                    double var30 = noiseData2[var12] / 512.0D;
                    double var32 = noiseData3[var12] / 512.0D;
                    double var34 = (noiseData1[var12] / 10.0D + 1.0D) / 2.0D;

                    if (var34 < 0.0D)
                    {
                        var26 = var30;
                    }
                    else if (var34 > 1.0D)
                    {
                        var26 = var32;
                    }
                    else
                    {
                        var26 = var30 + (var32 - var30) * var34;
                    }

                    var26 -= 8.0D;
                    var26 += var22;
                    byte var36 = 2;
                    double var37;

                    if (var25 > par6 / 2 - var36)
                    {
                        var37 = (var25 - (par6 / 2 - var36)) / 64.0F;

                        if (var37 < 0.0D)
                        {
                            var37 = 0.0D;
                        }

                        if (var37 > 1.0D)
                        {
                            var37 = 1.0D;
                        }

                        var26 = var26 * (1.0D - var37) + -3000.0D * var37;
                    }

                    var36 = 8;

                    if (var25 < var36)
                    {
                        var37 = (var36 - var25) / (var36 - 1.0F);
                        var26 = var26 * (1.0D - var37) + -30.0D * var37;
                    }

                    par1ArrayOfDouble[var12] = var26;
                    ++var12;
                }
            }
        }

        return par1ArrayOfDouble;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    @Override
	public boolean chunkExists(int par1, int par2)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    @Override
	public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        BlockFalling.fallInstantly = true;

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(par1IChunkProvider, worldObj, worldObj.rand, par2, par3, false));
        
        int var4 = par2 * 16;
        int var5 = par3 * 16;
        BiomeGenBase var6 = worldObj.getBiomeGenForCoords(var4 + 16, var5 + 16);

        long var7 = RNGa.nextLong() / 2L * 2L + 1L;
        long var9 = RNGa.nextLong() / 2L * 2L + 1L;
        boolean var11 = false;

        int var12;
        int var13;
        int var14;

        if (!var11 && RNGa.nextInt(2) == 0)
        {
            var12 = var4 + RNGa.nextInt(16) + 8;
            var13 = RNGa.nextInt(128);
            var14 = var5 + RNGa.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.water)).generate(worldObj, RNGa, var12, var13, var14);
        }

        if (!var11 && RNGa.nextInt(8) == 0)
        {
            var12 = var4 + RNGa.nextInt(16) + 8;
            var13 = RNGa.nextInt(RNGa.nextInt(120) + 8);
            var14 = var5 + RNGa.nextInt(16) + 8;

            if (var13 < 63 || RNGa.nextInt(10) == 0)
            {
                (new WorldGenLakes(Blocks.lava)).generate(worldObj, RNGa, var12, var13, var14);
            }
        }

        var6.decorate(worldObj, RNGa, var4, var5);

        if (par2 == 0 && par3 == 0 && !portalDone) 
        {
            createPortal(worldObj, RNGa);
        }

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(par1IChunkProvider, worldObj, worldObj.rand, par2, par3, false));
        BlockFalling.fallInstantly = false;
    }

    private boolean towerDone = false;
    private boolean portalDone = false;
    
    public void generateTower(World par1World, Random par2Random, int par3, int par4)
    {
        WorldGenTower myTower = new WorldGenTower(Blocks.grass, Blocks.double_stone_slab, Blocks.lapis_ore);
        if (!towerDone)
        {
            int randPosX = par3 + par2Random.nextInt(16) + 8;
            int randPosZ = par4 + par2Random.nextInt(16) + 8;
            towerDone = myTower.generate(par1World, par2Random, randPosX, 61, randPosZ);
        }
    }

    public void createPortal(World par1World, Random par2Random)
    {
        MoCWorldGenPortal myPortal = new MoCWorldGenPortal(Blocks.quartz_block, 2, Blocks.quartz_stairs, 0, Blocks.quartz_block, 1, Blocks.quartz_block, 0);
        for (int i = 0; i< 16; i++)
        {
            if (!portalDone)
            {
                int randPosY = 56 + i;
                portalDone = myPortal.generate(par1World, par2Random, 0, randPosY, 0);
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    @Override
	public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
    {
        return true;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    @Override
	public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    @Override
	public String makeString()
    {
        return "RandomLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    @Override
	public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
    {
        BiomeGenBase var5 = worldObj.getBiomeGenForCoords(par2, par4);
        return var5 == null ? null : var5.getSpawnableList(par1EnumCreatureType);
    }

    /**
     * Returns the location of the closest structure of the specified type. If not found returns null.
     */
    @Override
	public ChunkPosition func_147416_a(World par1World, String par2Str, int par3, int par4, int par5)
    {
        return null;
    }

    @Override
	public int getLoadedChunkCount()
    {
        return 0;
    }

    @Override
    public void recreateStructures(int par1, int par2) 
    {
    }

    /**
     * Unloads the 100 oldest chunks from memory, due to a bug with chunkSet.add() never being called it thinks the list
     * is always empty and will not remove any chunks.
     */
    @Override
    public boolean unloadQueuedChunks() {
        return true;
    }

    @Override
    public void saveExtraData() {
    }
}