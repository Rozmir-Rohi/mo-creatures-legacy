package drzhark.mocreatures.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class MoCEntityFXUndead extends EntityFX {

    public MoCEntityFXUndead(World par1World, double par2, double par4, double par6)
    {
        super(par1World, par2, par4, par6, 0.0D, 0.0D, 0.0D);
        motionX *= 0.8D;
        motionY *= 0.8D;
        motionZ *= 0.8D;
        motionY = (double) (rand.nextFloat() * 0.4F + 0.05F);

        setSize(0.01F, 0.01F);
        particleGravity = 0.06F;
        particleMaxAge = (int) (32.0D / (Math.random() * 0.8D + 0.2D));
        particleScale *= 0.8F;
    }

    /**
     * sets which texture to use (2 = items.png)
     */
    public int getFXLayer()
    {
        if (onGround)
        {
        return 1;
        }
        return 2;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        motionY -= 0.03D;
        moveEntity(motionX, motionY, motionZ);

        motionX *= 0.8D;
        motionY *= 0.5D;
        motionZ *= 0.8D;

        if (onGround)
        {
            motionX *= 0.7D;
            motionZ *= 0.7D;
        }

        if (particleMaxAge-- <= 0)
        {
            setDead();
        }
    }

    private String getCurrentTexture()
    {
        if (onGround)
        {
        return "fxundead1.png";
        }
        return "fxundead2.png";
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("mocreatures", MoCreatures.proxy.MISC_TEXTURE + getCurrentTexture()));
        float sizeFactor = 0.1F * particleScale;
        float var13 = (float) (prevPosX + (posX - prevPosX) * (double) par2 - interpPosX);
        float var14 = (float) (prevPosY + (posY - prevPosY) * (double) par2 - interpPosY);
        float var15 = (float) (prevPosZ + (posZ - prevPosZ) * (double) par2 - interpPosZ);
        float var16 = 1F;
        par1Tessellator.setColorOpaque_F(particleRed * var16, particleGreen * var16, particleBlue * var16);
        par1Tessellator.addVertexWithUV((double) (var13 - par3 * sizeFactor - par6 * sizeFactor), (double) (var14 - par4 * sizeFactor), (double) (var15 - par5 * sizeFactor - par7 * sizeFactor), 0D, 1D);
        par1Tessellator.addVertexWithUV((double) (var13 - par3 * sizeFactor + par6 * sizeFactor), (double) (var14 + par4 * sizeFactor), (double) (var15 - par5 * sizeFactor + par7 * sizeFactor), 1D, 1D);
        par1Tessellator.addVertexWithUV((double) (var13 + par3 * sizeFactor + par6 * sizeFactor), (double) (var14 + par4 * sizeFactor), (double) (var15 + par5 * sizeFactor + par7 * sizeFactor), 1D, 0D);
        par1Tessellator.addVertexWithUV((double) (var13 + par3 * sizeFactor - par6 * sizeFactor), (double) (var14 - par4 * sizeFactor), (double) (var15 + par5 * sizeFactor - par7 * sizeFactor), 0D, 0D);
    }
}