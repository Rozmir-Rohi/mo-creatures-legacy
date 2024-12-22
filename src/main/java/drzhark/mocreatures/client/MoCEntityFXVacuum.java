package drzhark.mocreatures.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class MoCEntityFXVacuum extends EntityFX {
    private final float portalParticleScale;
    private final double portalPosX;
    private final double portalPosY;
    private final double portalPosZ;

    public MoCEntityFXVacuum(World world, double par2, double par4, double par6, double par8, double par10, double par12, float red, float green, float blue, int partTexture)
    {
        super(world, par2, par4, par6, par8, par10, par12);

        particleRed = red;
        particleGreen = green;
        particleBlue = blue;

        motionX = par8;
        motionY = par10;
        motionZ = par12;
        portalPosX = posX = par2;
        portalPosY = posY = par4;// + 0.7D;
        portalPosZ = posZ = par6;
        noClip = true;
        rand.nextFloat();
        portalParticleScale = particleScale = rand.nextFloat() * 0.2F + 0.5F;
        setParticleTextureIndex(partTexture);
        particleMaxAge = (int) (Math.random() * 10.0D) + 30;
    }

    @Override
    public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
    {
        float var8 = (particleAge + par2) / particleMaxAge;
        var8 = 1.0F - var8;
        var8 *= var8;
        var8 = 1.0F - var8;
        particleScale = portalParticleScale * var8;
        super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
    }

    @Override
    public int getBrightnessForRender(float par1)
    {
        int var2 = super.getBrightnessForRender(par1);
        float var3 = (float) particleAge / (float) particleMaxAge;
        var3 *= var3;
        var3 *= var3;
        int var4 = var2 & 255;
        int var5 = var2 >> 16 & 255;
        var5 += (int) (var3 * 15.0F * 16.0F);

        if (var5 > 240)
        {
            var5 = 240;
        }

        return var4 | var5 << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
    public float getBrightness(float par1)
    {
        float var2 = super.getBrightness(par1);
        float var3 = (float) particleAge / (float) particleMaxAge;
        var3 = var3 * var3 * var3 * var3;
        return var2 * (1.0F - var3) + var3;
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
        float var1 = (float) particleAge / (float) particleMaxAge;
        float var2 = var1;
        var1 = -var1 + var1 * var1 * 2.0F;
        var1 = 1.0F - var1;
        posX = portalPosX + motionX * var1;
        posY = portalPosY + motionY * var1 + (1.0F - var2);
        posZ = portalPosZ + motionZ * var1;

        if (particleAge++ >= particleMaxAge)
        {
            setDead();
        }
    }
}