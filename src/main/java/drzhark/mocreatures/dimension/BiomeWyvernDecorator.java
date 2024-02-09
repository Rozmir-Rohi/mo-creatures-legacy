package drzhark.mocreatures.dimension;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.BIG_SHROOM;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CACTUS;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.CLAY;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.DEAD_BUSH;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.FLOWERS;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.GRASS;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LILYPAD;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.PUMPKIN;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.REED;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SAND;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SAND_PASS2;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.TREE;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BiomeWyvernDecorator extends BiomeDecorator
{

    public BiomeWyvernDecorator()
    {
        generateLakes = true;
        grassPerChunk = 1;
        flowersPerChunk = -999;
        mushroomsPerChunk = 20;
        treesPerChunk = 4;
    }

    /**
     * The method that does the work of actually decorating chunks
    */
    protected void func_150513_a(BiomeGenBase biomegenbase)
    {
        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(currentWorld, randomGenerator, chunk_X, chunk_Z));
        generateOres();
        int i;
        int j;
        int k;

        boolean doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, SAND);
        for (i = 0; doGen && i < sandPerChunk2; ++i)
        {
            j = chunk_X + randomGenerator.nextInt(16) + 8;
            k = chunk_Z + randomGenerator.nextInt(16) + 8;
            sandGen.generate(currentWorld, randomGenerator, j, currentWorld.getTopSolidOrLiquidBlock(j, k), k);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, CLAY);
        for (i = 0; doGen && i < clayPerChunk; ++i)
        {
            j = chunk_X + randomGenerator.nextInt(16) + 8;
            k = chunk_Z + randomGenerator.nextInt(16) + 8;
            clayGen.generate(currentWorld, randomGenerator, j, currentWorld.getTopSolidOrLiquidBlock(j, k), k);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, SAND_PASS2);
        for (i = 0; doGen && i < sandPerChunk; ++i)
        {
            j = chunk_X + randomGenerator.nextInt(16) + 8;
            k = chunk_Z + randomGenerator.nextInt(16) + 8;
            gravelAsSandGen.generate(currentWorld, randomGenerator, j, currentWorld.getTopSolidOrLiquidBlock(j, k), k);
        }

        i = treesPerChunk;

        if (randomGenerator.nextInt(10) == 0)
        {
            ++i;
        }

        int l;
        int i1;

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, TREE);
        for (j = 0; doGen && j < i; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            i1 = currentWorld.getHeightValue(k, l);
            WorldGenAbstractTree worldgenabstracttree = biomegenbase.func_150567_a(randomGenerator);
            worldgenabstracttree.setScale(1.0D, 1.0D, 1.0D);

            if (worldgenabstracttree.generate(currentWorld, randomGenerator, k, i1, l))
            {
                worldgenabstracttree.func_150524_b(currentWorld, randomGenerator, k, i1, l);
            }
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, BIG_SHROOM);
        for (j = 0; doGen && j < bigMushroomsPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            bigMushroomGen.generate(currentWorld, randomGenerator, k, currentWorld.getHeightValue(k, l), l);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, FLOWERS);
        for (j = 0; doGen && j < flowersPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) + 32);
            String s = biomegenbase.func_150572_a(randomGenerator, k, i1, l);
            BlockFlower blockflower = BlockFlower.func_149857_e(s);

            if (blockflower.getMaterial() != Material.air)
            {
                yellowFlowerGen.func_150550_a(blockflower, BlockFlower.func_149856_f(s));
                yellowFlowerGen.generate(currentWorld, randomGenerator, k, i1, l);
            }
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, GRASS);
        for (j = 0; doGen && j < grassPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            i1 = currentWorld.getHeightValue(k, l);
            WorldGenerator worldgenerator = biomegenbase.getRandomWorldGenForGrass(randomGenerator);
            worldgenerator.generate(currentWorld, randomGenerator, k, i1, l);
            
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, DEAD_BUSH);
        for (j = 0; doGen && j < deadBushPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) * 2);
            (new WorldGenDeadBush(Blocks.deadbush)).generate(currentWorld, randomGenerator, k, i1, l);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, LILYPAD);
        for (j = 0; doGen && j < waterlilyPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            for (i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) * 2); i1 > 0 && currentWorld.isAirBlock(k, i1 - 1, l); --i1)
            {
                ;
            }

            waterlilyGen.generate(currentWorld, randomGenerator, k, i1, l);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, SHROOM);
        for (j = 0; doGen && j < mushroomsPerChunk; ++j)
        {
            if (randomGenerator.nextInt(4) == 0)
            {
                k = chunk_X + randomGenerator.nextInt(16) + 8;
                l = chunk_Z + randomGenerator.nextInt(16) + 8;
                i1 = currentWorld.getHeightValue(k, l);
                mushroomBrownGen.generate(currentWorld, randomGenerator, k, i1, l);
            }

            if (randomGenerator.nextInt(8) == 0)
            {
                k = chunk_X + randomGenerator.nextInt(16) + 8;
                l = chunk_Z + randomGenerator.nextInt(16) + 8;
                if (currentWorld.getHeightValue(k, l) == 0) continue;
                
                i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) * 2);
                mushroomRedGen.generate(currentWorld, randomGenerator, k, i1, l);
            }
        }

        if (doGen && randomGenerator.nextInt(4) == 0)
        {
            j = chunk_X + randomGenerator.nextInt(16) + 8;
            k = chunk_Z + randomGenerator.nextInt(16) + 8;
            
            l = randomGenerator.nextInt(1 + currentWorld.getHeightValue(j, k) * 2);
            mushroomBrownGen.generate(currentWorld, randomGenerator, j, l, k);
        }

        if (doGen && randomGenerator.nextInt(8) == 0)
        {
            j = chunk_X + randomGenerator.nextInt(16) + 8;
            k = chunk_Z + randomGenerator.nextInt(16) + 8;
            l = randomGenerator.nextInt(1 + currentWorld.getHeightValue(j, k) * 2);
            mushroomRedGen.generate(currentWorld, randomGenerator, j, l, k);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, REED);
        for (j = 0; doGen && j < reedsPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) * 2);
            reedGen.generate(currentWorld, randomGenerator, k, i1, l);
        }

        for (j = 0; doGen && j < 10; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) * 2);
            reedGen.generate(currentWorld, randomGenerator, k, i1, l);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, PUMPKIN);
        if (doGen && randomGenerator.nextInt(32) == 0)
        {
            j = chunk_X + randomGenerator.nextInt(16) + 8;
            k = chunk_Z + randomGenerator.nextInt(16) + 8;
            l = randomGenerator.nextInt(1 + currentWorld.getHeightValue(j, k) * 2);
            (new WorldGenPumpkin()).generate(currentWorld, randomGenerator, j, l, k);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, CACTUS);
        for (j = 0; doGen && j < cactiPerChunk; ++j)
        {
            k = chunk_X + randomGenerator.nextInt(16) + 8;
            l = chunk_Z + randomGenerator.nextInt(16) + 8;
            if (currentWorld.getHeightValue(k, l) == 0) continue;
            
            i1 = randomGenerator.nextInt(currentWorld.getHeightValue(k, l) * 2);
            cactusGen.generate(currentWorld, randomGenerator, k, i1, l);
        }

        doGen = TerrainGen.decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, LAKE);
        if (doGen && generateLakes)
        {
            for (j = 0; j < 50; ++j)
            {
                k = chunk_X + randomGenerator.nextInt(16) + 8;
                l = randomGenerator.nextInt(randomGenerator.nextInt(248) + 8);
                i1 = chunk_Z + randomGenerator.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.flowing_water)).generate(currentWorld, randomGenerator, k, l, i1);
            }

            for (j = 0; j < 20; ++j)
            {
                k = chunk_X + randomGenerator.nextInt(16) + 8;
                l = randomGenerator.nextInt(randomGenerator.nextInt(randomGenerator.nextInt(240) + 8) + 8);
                i1 = chunk_Z + randomGenerator.nextInt(16) + 8;
                (new WorldGenLiquids(Blocks.flowing_lava)).generate(currentWorld, randomGenerator, k, l, i1);
            }
        }

        MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(currentWorld, randomGenerator, chunk_X, chunk_Z));
    }
}
