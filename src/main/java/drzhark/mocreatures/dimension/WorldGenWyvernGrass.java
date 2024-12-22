package drzhark.mocreatures.dimension;


//perhaps not needed.
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenWyvernGrass extends WorldGenerator
{
    /** Stores ID for WorldGenTallGrass */
    private Block tallGrass;
    private int tallGrassMetadata;

    public WorldGenWyvernGrass(Block block, int metaData)
    {
        tallGrass = block;
        tallGrassMetadata = metaData;
    }

    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        Block block;

        do
        {
            block = world.getBlock(x, y, z);
            if (!(block.isLeaves(world, x, y, z) || block.isAir(world, x, y, z)))
            {
                break;
            }
            --y;
        } while (y > 0);

        for (int l = 0; l < 128; ++l)
        {
            int i1 = x + rand.nextInt(8) - rand.nextInt(8);
            int j1 = y + rand.nextInt(4) - rand.nextInt(4);
            int k1 = z + rand.nextInt(8) - rand.nextInt(8);

            if (world.isAirBlock(i1, j1, k1) && tallGrass.canBlockStay(world, i1, j1, k1))
            {
                world.setBlock(i1, j1, k1, tallGrass, tallGrassMetadata, 3);
            }
        }

        return true;
    }
}