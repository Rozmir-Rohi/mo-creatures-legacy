package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.aquatic.MoCEntityJellyFish;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
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
        MoCEntityJellyFish jellyfish = (MoCEntityJellyFish) entityLiving;
        if (!jellyfish.isSwimming() && jellyfish.onGround)
        {

            adjustHeight(jellyfish);
            rotateAnimal(jellyfish);
        }
        else
        {
            pulse(jellyfish);
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
        MoCEntityJellyFish entityJellyfish = (MoCEntityJellyFish) entityLiving;
        
        if (!entityJellyfish.isSwimming())
        {
            depth = 0.09F;
        }
        else
        {
            depth = 0.3F;
        }
        GL11.glPushMatrix();
        GL11.glEnable(3042 /*GL_BLEND*/);
        
        super.doRender(entityLiving, x, y, z, rotationYaw, rotationPitch);
        GL11.glDisable(3042/*GL_BLEND*/);
        GL11.glPopMatrix();

    }


    protected void stretch(MoCEntityJellyFish entityJellyfish)
    {
        GL11.glScalef(entityJellyfish.getMoCAge() * 0.01F, entityJellyfish.getMoCAge() * 0.01F, entityJellyfish.getMoCAge() * 0.01F);
    }

    protected void pulse(MoCEntityJellyFish entityJellyfish)
    {

        float pulseSize = entityJellyfish.pulsingSize;
        if (pulseSize > 0.2F)
        {
            pulseSize = 0.2F - (pulseSize - 0.2F);
        }
        float scale = entityJellyfish.getMoCAge() * 0.01F + (pulseSize/4);
        float verticalScale = entityJellyfish.getMoCAge() * 0.01F + (pulseSize/4);
        GL11.glScalef(scale, verticalScale, scale);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity)
    {
        return ((MoCEntityJellyFish) entity).getTexture();
    }
}
