package drzhark.mocreatures.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCProxy;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class MoCEntityFXVanish extends EntityFX {

    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;
    private final boolean implode;
    public MoCEntityFXVanish(World world, double par2, double par4, double par6, double par8, double par10, double par12, float red, float green, float blue, boolean flag)
    {
        super(world, par2, par4, par6, 0.0D, 0.0D, 0.0D);

        particleRed = red;
        particleGreen = green;
        particleBlue = blue;
        motionX = par8;
        motionY = par10 * 5D;
        motionZ = par12;
        portalPosX = posX = par2;
        portalPosY = posY = par4;// + 0.7D;
        portalPosZ = posZ = par6;
        noClip = true;
        portalParticleScale = particleScale = rand.nextFloat() * 0.3F + 0.5F;
        implode = flag;
        particleMaxAge = (int) (Math.random() * 10.0D) + 70;
    }

    /**
     * sets which texture to use (2 = items.png)
     */
    @Override
    public int getFXLayer()
    {
        return 1;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        int speeder = 0;
        float sizeExp = 2.0F;
        if (implode)
        {
            speeder = (particleMaxAge / 2);
            sizeExp = 5.0F;
        }

        float var1 = (float) (particleAge + speeder) / (float) particleMaxAge;
        float var2 = var1;
        var1 = -var1 + var1 * var1 * sizeExp;//5 insteaf of 2 makes an explosion
        var1 = 1.0F - var1;
        posX = portalPosX + motionX * var1;
        posY = portalPosY + motionY * var1 + (1.0F - var2);
        posZ = portalPosZ + motionZ * var1;

        if (particleAge++ >= particleMaxAge)
        {
            setDead();
        }
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("mocreatures", MoCProxy.MISC_TEXTURE + "fxvanish.png"));
        float scale = 0.1F * particleScale;
        float xPos = (float) (prevPosX + (posX - prevPosX) * par2 - interpPosX);
        float yPos = (float) (prevPosY + (posY - prevPosY) * par2 - interpPosY);
        float zPos = (float) (prevPosZ + (posZ - prevPosZ) * par2 - interpPosZ);
        float colorIntensity = 1.0F;
        par1Tessellator.setColorOpaque_F(particleRed * colorIntensity, particleGreen * colorIntensity, particleBlue * colorIntensity);//, 1.0F);
        par1Tessellator.addVertexWithUV(xPos - par3 * scale - par6 * scale, yPos - par4 * scale, zPos - par5 * scale - par7 * scale, 0D, 1D);
        par1Tessellator.addVertexWithUV(xPos - par3 * scale + par6 * scale, yPos + par4 * scale, zPos - par5 * scale + par7 * scale, 1D, 1D);
        par1Tessellator.addVertexWithUV(xPos + par3 * scale + par6 * scale, yPos + par4 * scale, zPos + par5 * scale + par7 * scale, 1D, 0D);
        par1Tessellator.addVertexWithUV(xPos + par3 * scale - par6 * scale, yPos - par4 * scale, zPos + par5 * scale - par7 * scale, 0D, 0D);
    }
}