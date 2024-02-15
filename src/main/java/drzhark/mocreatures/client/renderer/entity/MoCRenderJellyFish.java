package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderJellyFish extends RenderLiving {

    float depth = 0F;

    public MoCRenderJellyFish(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        GL11.glTranslatef(0.0F, depth, 0.0F);
        MoCEntityJellyFish jelly = (MoCEntityJellyFish) entityLiving;
        if (!jelly.isSwimming() && jelly.onGround)
        {

            adjustHeight(jelly);
            rotateAnimal(jelly);
        }
        else
        {
            pulse(jelly);
        }

    }

    protected void rotateAnimal(EntityLiving entityLiving)
    {
        GL11.glRotatef(90F, -1F, 0.0F, 0.0F);
    }

    protected void adjustHeight(EntityLiving entityLiving)
    {
        GL11.glTranslatef(0.0F, -0.3F, 0.0F);
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityJellyFish entityjellyfish = (MoCEntityJellyFish) entityLiving;
        boolean isGlowing = entityjellyfish.isGlowing();
        
        //TODO: fix jellyfish not glowing
        
        if (!entityjellyfish.isSwimming())
        {
            depth = 0.09F;
        }
        else

        {
            depth = 0.3F;
        }
        GL11.glPushMatrix();
        GL11.glEnable(3042 /*GL_BLEND*/);
        if (!isGlowing)
        {
            float transparency = 0.7F;
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(0.8F, 0.8F, 0.8F, transparency);
        }
        else
        {
            GL11.glBlendFunc(770, 1);
            //GL11.glBlendFunc(770, GL11.GL_ONE);
        }
        super.doRender(entityLiving, x, y, z, rotationYaw, rotationPitch);
        GL11.glDisable(3042/*GL_BLEND*/);
        GL11.glPopMatrix();

    }


    protected void stretch(MoCEntityJellyFish entityjellyfish)
    {
        GL11.glScalef(entityjellyfish.getMoCAge() * 0.01F, entityjellyfish.getMoCAge() * 0.01F, entityjellyfish.getMoCAge() * 0.01F);
    }

    protected void pulse(MoCEntityJellyFish entityjellyfish)
    {

        float pulseSize = entityjellyfish.pulsingSize;
        if (pulseSize > 0.2F)
        {
            pulseSize = 0.2F - (pulseSize - 0.2F);
        }
        float scale = entityjellyfish.getMoCAge() * 0.01F + (pulseSize/4);
        float scale2 = entityjellyfish.getMoCAge() * 0.01F + (pulseSize / 4);
        GL11.glScalef(scale, scale2, scale);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityJellyFish)par1Entity).getTexture();
    }
}
