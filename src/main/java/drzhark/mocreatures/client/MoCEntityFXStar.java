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
public class MoCEntityFXStar extends EntityFX {

    public MoCEntityFXStar(World worldObj, double posX, double posY, double posZ, float red, float green, float blue)
    {
        super(worldObj, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        motionX *= 0.8D;
        motionY *= 0.8D;
        motionZ *= 0.8D;
        motionY = rand.nextFloat() * 0.4F + 0.05F;

        particleRed = red;
        particleGreen = green;
        particleBlue = blue;

        setSize(0.01F, 0.01F);
        particleGravity = 0.06F;
        particleMaxAge = (int) (64.0D / (Math.random() * 0.8D + 0.2D));
        particleScale *= 0.6F; //it was 0.8 for the old star //0.4 if I'm not using the shrinking
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
        particleScale *= 0.995F; //slowly shrinks it

        motionY -= 0.03D;
        moveEntity(motionX, motionY, motionZ);
        motionX *= 0.9D;
        motionY *= 0.2D;
        motionZ *= 0.9D;
   
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

    @Override
    public void setParticleTextureIndex(int par1)
    {
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation("mocreatures", MoCProxy.MISC_TEXTURE + "fxstar.png"));
        float sizeFactor = 0.1F * particleScale;
        float var13 = (float) (prevPosX + (posX - prevPosX) * par2 - interpPosX);
        float var14 = (float) (prevPosY + (posY - prevPosY) * par2 - interpPosY);
        float var15 = (float) (prevPosZ + (posZ - prevPosZ) * par2 - interpPosZ);
        float var16 = 1.2F - ((float) Math.random() * 0.5F);
        par1Tessellator.setColorRGBA_F(particleRed * var16, particleGreen * var16, particleBlue * var16, 1.0F);
        par1Tessellator.addVertexWithUV(var13 - par3 * sizeFactor - par6 * sizeFactor, var14 - par4 * sizeFactor, var15 - par5 * sizeFactor - par7 * sizeFactor, 0D, 1D);
        par1Tessellator.addVertexWithUV(var13 - par3 * sizeFactor + par6 * sizeFactor, var14 + par4 * sizeFactor, var15 - par5 * sizeFactor + par7 * sizeFactor, 1D, 1D);
        par1Tessellator.addVertexWithUV(var13 + par3 * sizeFactor + par6 * sizeFactor, var14 + par4 * sizeFactor, var15 + par5 * sizeFactor + par7 * sizeFactor, 1D, 0D);
        par1Tessellator.addVertexWithUV(var13 + par3 * sizeFactor - par6 * sizeFactor, var14 - par4 * sizeFactor, var15 + par5 * sizeFactor - par7 * sizeFactor, 0D, 0D);
    }
}