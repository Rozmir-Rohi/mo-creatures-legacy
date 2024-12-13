package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderMoC extends RenderLiving {

    public MoCRenderMoC(ModelBase modelBase, float f)
    {
        super(modelBase, f);

    }

    @Override
    public void doRender(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        doRenderMoC(entity, d, d1, d2, f, f1);
    }

    public void doRenderMoC(Entity entity, double d, double d1, double d2, float f, float f1)
    {
        super.doRender(entity, d, d1, d2, f, f1);

        IMoCEntity entityMoC = (IMoCEntity) entity;

        boolean shouldDisplayPetName = MoCreatures.proxy.getDisplayPetName() && !(entityMoC.getName()).isEmpty();
        boolean shouldDisplayPetHealth = MoCreatures.proxy.getDisplayPetHealthMode((EntityLiving) entity);
        boolean shouldDisplayPetIcons = MoCreatures.proxy.getDisplayPetIcons();
        if (entityMoC.shouldRenderName())
        {
            float f2 = 1.6F;
            float f3 = 0.01666667F * f2;
            float distanceOfThisEntityToPlayer = ((Entity) entityMoC).getDistanceToEntity(renderManager.livingPlayer);
            if (distanceOfThisEntityToPlayer < 16F)
            {
                String petName = "";
                petName = (new StringBuilder()).append(petName).append(entityMoC.getName()).toString();
                float f7 = 0.1F;
                FontRenderer fontRenderer = getFontRendererFromRenderManager();
                GL11.glPushMatrix();
                GL11.glTranslatef((float) d + 0.0F, (float) d1 + f7, (float) d2);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f3, -f3, f3);
                GL11.glDisable(2896 /* GL_LIGHTING */);
                Tessellator tessellator1 = Tessellator.instance;
                int yOffset = entityMoC.nameYOffset(); //the default offset for pet name tags is -80
               
                
                if (shouldDisplayPetHealth)
                {
                    GL11.glDisable(3553 /* GL_TEXTURE_2D */);
                    if (!shouldDisplayPetName)
                    {
                        yOffset += 8;
                    }
                    tessellator1.startDrawingQuads();
                    // might break SSP
                    float f8 = ((EntityLiving)entityMoC).getHealth();
                    float f9 = ((EntityLiving)entityMoC).getMaxHealth();
                    float f10 = f8 / f9;
                    float f11 = 40F * f10;
                    tessellator1.setColorRGBA_F(0.7F, 0.0F, 0.0F, 1.0F);
                    tessellator1.addVertex(-20F + f11, -10 + yOffset, 0.0D);
                    tessellator1.addVertex(-20F + f11, -6 + yOffset, 0.0D);
                    tessellator1.addVertex(20D, -6 + yOffset, 0.0D);
                    tessellator1.addVertex(20D, -10 + yOffset, 0.0D);
                    tessellator1.setColorRGBA_F(0.0F, 0.7F, 0.0F, 1.0F);
                    tessellator1.addVertex(-20D, -10 + yOffset, 0.0D);
                    tessellator1.addVertex(-20D, -6 + yOffset, 0.0D);
                    tessellator1.addVertex(f11 - 20F, -6 + yOffset, 0.0D);
                    tessellator1.addVertex(f11 - 20F, -10 + yOffset, 0.0D);
                    tessellator1.draw();
                    GL11.glEnable(3553 /* GL_TEXTURE_2D */);
                }
                if (shouldDisplayPetName)
                {
                    GL11.glDepthMask(false);
                    GL11.glDisable(2929 /* GL_DEPTH_TEST */);
                    GL11.glEnable(3042 /* GL_BLEND */);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553 /* GL_TEXTURE_2D */);
                    tessellator1.startDrawingQuads();
                    int stringWidthHalf = fontRenderer.getStringWidth(petName) / 2;
                    tessellator1.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    tessellator1.addVertex(-stringWidthHalf - 1, -1 + yOffset, 0.0D);
                    tessellator1.addVertex(-stringWidthHalf - 1, 8 + yOffset, 0.0D);
                    tessellator1.addVertex(stringWidthHalf + 1, 8 + yOffset, 0.0D);
                    tessellator1.addVertex(stringWidthHalf + 1, -1 + yOffset, 0.0D);
                    tessellator1.draw();
                    GL11.glEnable(3553 /* GL_TEXTURE_2D */);
                    fontRenderer.drawString(petName, -fontRenderer.getStringWidth(petName) / 2, yOffset, 0x20ffffff);
                    GL11.glEnable(2929 /* GL_DEPTH_TEST */);
                    GL11.glDepthMask(true);
                    fontRenderer.drawString(petName, -fontRenderer.getStringWidth(petName) / 2, yOffset, -1);
                    GL11.glDisable(3042 /* GL_BLEND */);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
                GL11.glEnable(2896 /* GL_LIGHTING */);
                GL11.glPopMatrix();
            }
        }
        if (entityMoC.getRoper() != null)
        {
            d1 -= entityMoC.roperYOffset();
            Tessellator tessellator = Tessellator.instance;
            float f4 = ((entityMoC.getRoper().prevRotationYaw + ((entityMoC.getRoper().rotationYaw - entityMoC.getRoper().prevRotationYaw) * f1 * 0.5F)) * (float) Math.PI) / 180F;
            float f6 = ((entityMoC.getRoper().prevRotationPitch + ((entityMoC.getRoper().rotationPitch - entityMoC.getRoper().prevRotationPitch) * f1 * 0.5F)) * (float) Math.PI) / 180F;
            double d3 = MathHelper.sin(f4);
            double d4 = MathHelper.cos(f4);
            double d5 = MathHelper.sin(f6);
            double d6 = MathHelper.cos(f6);
            double d7 = (entityMoC.getRoper().prevPosX + ((entityMoC.getRoper().posX - entityMoC.getRoper().prevPosX) * f1)) - (d4 * 0.69999999999999996D) - (d3 * 0.5D * d6);
            double d8 = (entityMoC.getRoper().prevPosY + ((entityMoC.getRoper().posY - entityMoC.getRoper().prevPosY) * f1)) - (d5 * 0.5D);
            double d9 = ((entityMoC.getRoper().prevPosZ + ((entityMoC.getRoper().posZ - entityMoC.getRoper().prevPosZ) * f1)) - (d3 * 0.69999999999999996D)) + (d4 * 0.5D * d6);
            double d10 = ((Entity) entityMoC).prevPosX + ((((Entity) entityMoC).posX - ((Entity) entityMoC).prevPosX) * f1);
            double d11 = ((Entity) entityMoC).prevPosY + ((((Entity) entityMoC).posY - ((Entity) entityMoC).prevPosY) * f1) + 0.25D;
            double d12 = ((Entity) entityMoC).prevPosZ + ((((Entity) entityMoC).posZ - ((Entity) entityMoC).prevPosZ) * f1);
            double d13 = (float) (d7 - d10);
            double d14 = (float) (d8 - d11);
            double d15 = (float) (d9 - d12);
            GL11.glDisable(3553 /* GL_TEXTURE_2D */);
            GL11.glDisable(2896 /* GL_LIGHTING */);
            for (double d16 = 0.0D; d16 < 0.029999999999999999D; d16 += 0.01D)
            {
                tessellator.startDrawing(3);
                tessellator.setColorRGBA_F(0.5F, 0.4F, 0.3F, 1.0F);
                int j = 16;
                for (int k = 0; k <= j; k++)
                {
                    float f12 = (float) k / (float) j;
                    tessellator.addVertex(d + (d13 * f12) + d16, d1 + (d14 * ((f12 * f12) + f12) * 0.5D) + ((((float) j - (float) k) / (j * 0.75F)) + 0.125F), d2 + (d15 * f12));
                }

                tessellator.draw();
            }

            GL11.glEnable(2896 /* GL_LIGHTING */);
            GL11.glEnable(3553 /* GL_TEXTURE_2D */);
        }
    }

    protected void stretch(IMoCEntity mocreature)
    {
        float f = mocreature.getSizeFactor();
        if (f != 0)
        {
            GL11.glScalef(f, f, f);
        }
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        IMoCEntity mocreature = (IMoCEntity) entityLiving;
        /*if (mocreature.getSizeFactor() != 1.0F)
        {
            stretch(mocreature);
        }*/
        stretch(mocreature);
        adjustOffsets(mocreature.getAdjustedXOffset(), mocreature.getAdjustedYOffset(), mocreature.getAdjustedZOffset());
        
        adjustPitch(mocreature);
        adjustRoll(mocreature);
        adjustYaw(mocreature);
        
        
        super.preRenderCallback(entityLiving, f);

    }

    /**
     * Changes the YOffset of the creature, i.e. sitting animals
     * @param mocreature
     */
    protected void adjustYOffset(IMoCEntity mocreature)
    {
        float f = mocreature.getAdjustedYOffset();
        if (f != 0)
        {
            GL11.glTranslatef(0.0F, f, 0.0F);
        }
    }
    
   
    /**
     * Tilts the creature to the front / back
     * @param mocreature
     */
    protected void adjustPitch(IMoCEntity mocreature)
    {
        int i = mocreature.pitchRotationOffset();

        if (i != 0)
        {
            GL11.glRotatef(i, -1F, 0.0F, 0.0F);
        }
    }
    
    /**
     * Rolls creature
     * @param mocreature
     */
    protected void adjustRoll(IMoCEntity mocreature)
    {
        int i = mocreature.rollRotationOffset();

        if (i != 0)
        {
            GL11.glRotatef(i, 0F, 0F, -1F);
        }
    }
    
    protected void adjustYaw(IMoCEntity mocreature)
    {
        int i = mocreature.yawRotationOffset();
        if (i != 0)
        {
            GL11.glRotatef(i, 0.0F, -1.0F, 0.0F);
        }
    }

   
    /*protected void adjustZOffset(MoCIMoCreature mocreature)
    {
        float f = mocreature.getAdjustedZOffset();
        if (f != 0)
        {
            GL11.glTranslatef(0.0F, 0.0F, f);
        }
    }*/
    
    /**
     * translates the model
     * 
     */
    protected void adjustOffsets(float xOffset, float yOffset, float zOffset)
    {
            GL11.glTranslatef(xOffset, yOffset, zOffset);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((IMoCEntity)entity).getTexture();
    }
}
