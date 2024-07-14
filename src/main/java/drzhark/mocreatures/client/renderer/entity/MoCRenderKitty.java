package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelKitty;
import drzhark.mocreatures.entity.animal.MoCEntityKitty;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderKitty extends RenderBiped {

    public MoCModelKitty kitty1;

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityKitty)par1Entity).getTexture();
    }

    public MoCRenderKitty(MoCModelKitty modelkitty, float f)
    {
        super(modelkitty, f);
        kitty1 = modelkitty;
    }

    @Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        MoCEntityKitty entityKitty = (MoCEntityKitty) entityLiving;
        super.doRender(entityKitty, x, y, z, rotationYaw, rotationPitch);
        boolean flag = MoCreatures.proxy.getDisplayPetName() && !(entityKitty.getName()).isEmpty();
        boolean flag1 = MoCreatures.proxy.getDisplayPetHealthMode(entityLiving);
        boolean flag2 = MoCreatures.proxy.getDisplayPetIcons();
        if (entityKitty.shouldRenderName())
        {
            float f2 = 1.6F;
            float f3 = 0.01666667F * f2;
            float f4 = entityLiving.getDistanceToEntity(renderManager.livingPlayer);
            String s = "";
            s = (new StringBuilder()).append(s).append(entityKitty.getName()).toString();
            if (f4 < 12F)
            {
                float f5 = 0.2F;
                if (kitty1.isSitting)
                {
                    f5 = 0.4F;
                }
                FontRenderer fontrenderer = getFontRendererFromRenderManager();
                GL11.glPushMatrix();
                GL11.glTranslatef((float) x + 0.0F, (float) y - f5, (float) z);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f3, -f3, f3);
                GL11.glDisable(2896 /* GL_LIGHTING */);
                Tessellator tessellator = Tessellator.instance;
                byte byte0 = -48;
                if (flag2 && ((MoCEntityKitty) entityLiving).getIsEmo())
                {
                    bindTexture(((MoCEntityKitty) entityLiving).getEmoteIcon());

                    int i = -83;
                    if (!flag)
                    {
                        i += 8;
                    }
                    if (!flag1)
                    {
                        i += 8;
                    }
                    int k = 32;
                    int l = (k / 2) * -1;
                    float f9 = 0.0F;
                    float f11 = 1.0F / k;
                    float f12 = 1.0F / k;
                    tessellator.startDrawingQuads();
                    tessellator.addVertexWithUV(l, i + k, f9, 0.0D, k * f12);
                    tessellator.addVertexWithUV(l + k, i + k, f9, k * f11, k * f12);
                    tessellator.addVertexWithUV(l + k, i, f9, k * f11, 0.0D);
                    tessellator.addVertexWithUV(l, i, f9, 0.0D, 0.0D);
                    tessellator.draw();
                }
                if (flag1)
                {
                    if (!flag)
                    {
                        byte0 += 8;
                    }
                    GL11.glDisable(3553 /* GL_TEXTURE_2D */);
                    tessellator.startDrawingQuads();
                    float f6 = entityKitty.getHealth();
                    float f7 = entityKitty.getMaxHealth();
                    float f8 = f6 / f7;
                    float f10 = 30F * f8;
                    tessellator.setColorRGBA_F(0.7F, 0.0F, 0.0F, 1.0F);
                    tessellator.addVertex(-15F + f10, -8 + byte0, 0.0D);
                    tessellator.addVertex(-15F + f10, -4 + byte0, 0.0D);
                    tessellator.addVertex(15D, -4 + byte0, 0.0D);
                    tessellator.addVertex(15D, -8 + byte0, 0.0D);
                    tessellator.setColorRGBA_F(0.0F, 0.7F, 0.0F, 1.0F);
                    tessellator.addVertex(-15D, -8 + byte0, 0.0D);
                    tessellator.addVertex(-15D, -4 + byte0, 0.0D);
                    tessellator.addVertex(f10 - 15F, -4 + byte0, 0.0D);
                    tessellator.addVertex(f10 - 15F, -8 + byte0, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(3553 /* GL_TEXTURE_2D */);
                }
                if (flag)
                {
                    GL11.glDepthMask(false);
                    GL11.glDisable(2929 /* GL_DEPTH_TEST */);
                    GL11.glEnable(3042 /* GL_BLEND */);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553 /* GL_TEXTURE_2D */);
                    tessellator.startDrawingQuads();
                    int j = fontrenderer.getStringWidth(s) / 2;
                    tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                    tessellator.addVertex(-j - 1, -1 + byte0, 0.0D);
                    tessellator.addVertex(-j - 1, 8 + byte0, 0.0D);
                    tessellator.addVertex(j + 1, 8 + byte0, 0.0D);
                    tessellator.addVertex(j + 1, -1 + byte0, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(3553 /* GL_TEXTURE_2D */);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, 0x20ffffff);
                    GL11.glEnable(2929 /* GL_DEPTH_TEST */);
                    GL11.glDepthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, byte0, -1);
                    GL11.glDisable(3042 /* GL_BLEND */);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
                GL11.glEnable(2896 /* GL_LIGHTING */);
                GL11.glPopMatrix();
            }
        }
    }

    public void doRender2(EntityLiving entityLiving, double d, double d1, double d2, float f, float f1)
    {
        MoCEntityKitty entityKitty = (MoCEntityKitty) entityLiving;
        super.doRender(entityKitty, d, d1, d2, f, f1);
    }

    @Override
    protected float handleRotationFloat(EntityLivingBase entityLiving, float f)
    {
        MoCEntityKitty entityKitty = (MoCEntityKitty) entityLiving;
        if (!entityKitty.getIsAdult())
        {
            stretch(entityKitty);
        }
        return entityLiving.ticksExisted + f;
    }

    protected void onPlayersBack(EntityLivingBase entityLiving)
    {
        GL11.glRotatef(90F, 0F, 0.0F, -1F);
        
        GL11.glRotatef(55F, -1.0F, 0.0F, 0F);

        if (!entityLiving.worldObj.isRemote && (entityLiving.ridingEntity != null))
        {
            GL11.glTranslatef(-1.5F, 0.2F, -0.2F);
        }
        else
        {
            GL11.glTranslatef(0.1F, 0.2F, -0.2F);
        }

    }

    protected void onTheSide(EntityLivingBase entityLiving)
    {
        GL11.glRotatef(90F, 0.0F, 0.0F, -1F);
        GL11.glRotatef(165F, -1.0F, 0.0F, 0F);
        
        GL11.glTranslatef(0.2F, 0.3F, -0.2F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityKitty entityKitty = (MoCEntityKitty) entityLiving;
        kitty1.isSitting = entityKitty.getIsSitting();
        kitty1.isSleeping = entityKitty.getKittyState() == 12; //TODO
        kitty1.isSwinging = entityKitty.getIsSwinging();
        kitty1.swingProgress = entityKitty.swingProgress;
        kitty1.kittystate = entityKitty.getKittyState();
        if (entityKitty.getKittyState() == 20)
        {
            onTheSide(entityLiving);
        }
        if (entityKitty.isClimbingTree())
        {
            rotateAnimal(entityLiving);
        }
        if (entityKitty.isUpsideDown())
        {
            upsideDown(entityLiving);
        }
        if (entityKitty.isOnPlayersBack())
        {
            onPlayersBack(entityLiving);
        }
    }

    protected void rotateAnimal(EntityLivingBase entityLiving)
    {
        if (!entityLiving.onGround)
        {
            GL11.glRotatef(90F, -1F, 0.0F, 0.0F);
        }
    }

    protected void stretch(MoCEntityKitty entityKitty)
    {
        GL11.glScalef(entityKitty.getMoCAge() * 0.01F, entityKitty.getMoCAge() * 0.01F, entityKitty.getMoCAge() * 0.01F);
    }

    protected void upsideDown(EntityLivingBase entityLiving)
    {
        GL11.glRotatef(180F, 0.0F, 0.0F, -1F);
        GL11.glTranslatef(-0.35F, 0F, -0.55F);
    }
}
