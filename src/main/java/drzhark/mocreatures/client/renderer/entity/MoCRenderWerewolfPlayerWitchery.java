package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelWere;
import drzhark.mocreatures.client.model.MoCModelWereHuman;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class MoCRenderWerewolfPlayerWitchery extends RendererLivingEntity {

    private final MoCModelWere tempWerewolf;

    public MoCRenderWerewolfPlayerWitchery(MoCModelWereHuman werehumanModel, ModelBase modelBase, float f)
    {
        super(modelBase, f);
        setRenderPassModel(werehumanModel);
        tempWerewolf = (MoCModelWere) modelBase;
        
        this.renderManager = RenderManager.instance;
    }    
    
    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(EntityLivingBase entityLivingBase, double d, double d1, double d2, float f, float f1)
    {
    	EntityPlayer player = (EntityPlayer) entityLivingBase;
        tempWerewolf.hunched = player.isSprinting();
    	
        
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.mainModel.onGround = this.renderSwingProgress(entityLivingBase, f1);

        if (this.renderPassModel != null)
        {
            this.renderPassModel.onGround = this.mainModel.onGround;
        }

        this.mainModel.isRiding = entityLivingBase.isRiding();

        if (this.renderPassModel != null)
        {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }
        
        try
        {  
        	
            float f2 = this.interpolateRotation(entityLivingBase.prevRenderYawOffset, entityLivingBase.renderYawOffset, f1);
            float f3 = this.interpolateRotation(entityLivingBase.prevRotationYawHead, entityLivingBase.rotationYawHead, f1);
            float f4;

            if (entityLivingBase.isRiding() && entityLivingBase.ridingEntity instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase1 = (EntityLivingBase)entityLivingBase.ridingEntity;
                f2 = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset, f1);
                f4 = MathHelper.wrapAngleTo180_float(f3 - f2);

                if (f4 < -85.0F)
                {
                    f4 = -85.0F;
                }

                if (f4 >= 85.0F)
                {
                    f4 = 85.0F;
                }

                f2 = f3 - f4;

                if (f4 * f4 > 2500.0F)
                {
                    f2 += f4 * 0.2F;
                }
            }

            float f13 = entityLivingBase.prevRotationPitch + (entityLivingBase.rotationPitch - entityLivingBase.prevRotationPitch) * f1;
            this.renderLivingAt(entityLivingBase, d, d1, d2);
            f4 = this.handleRotationFloat(entityLivingBase, f1);
            this.rotateCorpse(entityLivingBase, f4, f2, f1);
            float f5 = 0.0625F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entityLivingBase, f1);
            GL11.glTranslatef(0.0F, -24.0F * f5 - 0.0078125F, 0.0F);
            float f6 = entityLivingBase.prevLimbSwingAmount + (entityLivingBase.limbSwingAmount - entityLivingBase.prevLimbSwingAmount) * f1;
            float f7 = entityLivingBase.limbSwing - entityLivingBase.limbSwingAmount * (1.0F - f1);

            if (f6 > 1.0F)
            {
                f6 = 1.0F;
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            this.mainModel.setLivingAnimations(entityLivingBase, f7, f6, f1);
            
            renderModel(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
            
            int j;
            float f8;
            float f9;
            float f10;

            for (int i = 0; i < 4; ++i)
            {
                j = this.shouldRenderPass(entityLivingBase, i, f1);

                if (j > 0)
                {
                    this.renderPassModel.setLivingAnimations(entityLivingBase, f7, f6, f1);
                    this.renderPassModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);

                    if ((j & 240) == 16)
                    {
                        this.func_82408_c(entityLivingBase, i, f1);
                        this.renderPassModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                    }

                    if ((j & 15) == 15)
                    {
                        f8 = (float)entityLivingBase.ticksExisted + f1;
                        GL11.glEnable(GL11.GL_BLEND);
                        f9 = 0.5F;
                        GL11.glColor4f(f9, f9, f9, 1.0F);
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDepthMask(false);

                        for (int k = 0; k < 2; ++k)
                        {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            f10 = 0.76F;
                            GL11.glColor4f(0.5F * f10, 0.25F * f10, 0.8F * f10, 1.0F);
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL11.glMatrixMode(GL11.GL_TEXTURE);
                            GL11.glLoadIdentity();
                            float f11 = f8 * (0.001F + (float)k * 0.003F) * 20.0F;
                            float f12 = 0.33333334F;
                            GL11.glScalef(f12, f12, f12);
                            GL11.glRotatef(30.0F - (float)k * 60.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef(0.0F, f11, 0.0F);
                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
                            this.renderPassModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                }
            }

            GL11.glDepthMask(true);
            this.renderEquippedItems(entityLivingBase, f1);
            float f14 = entityLivingBase.getBrightness(f1);
            j = this.getColorMultiplier(entityLivingBase, f14, f1);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            if ((j >> 24 & 255) > 0 || entityLivingBase.hurtTime > 0 || entityLivingBase.deathTime > 0)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);

                if (entityLivingBase.hurtTime > 0 || entityLivingBase.deathTime > 0)
                {
                    GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                    this.mainModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);

                    for (int l = 0; l < 4; ++l)
                    {
                        if (this.inheritRenderPass(entityLivingBase, l, f1) >= 0)
                        {
                            GL11.glColor4f(f14, 0.0F, 0.0F, 0.4F);
                            this.renderPassModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                        }
                    }
                }

                if ((j >> 24 & 255) > 0)
                {
                    f8 = (float)(j >> 16 & 255) / 255.0F;
                    f9 = (float)(j >> 8 & 255) / 255.0F;
                    float f15 = (float)(j & 255) / 255.0F;
                    f10 = (float)(j >> 24 & 255) / 255.0F;
                    GL11.glColor4f(f8, f9, f15, f10);
                    this.mainModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);

                    for (int i1 = 0; i1 < 4; ++i1)
                    {
                        if (this.inheritRenderPass(entityLivingBase, i1, f1) >= 0)
                        {
                            GL11.glColor4f(f8, f9, f15, f10);
                            this.renderPassModel.render(entityLivingBase, f7, f6, f4, f3 - f2, f13, f5);
                        }
                    }
                }

                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        catch (Exception exception)
        {
            System.out.println("Couldn't render entity");
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        this.passSpecialRender(entityLivingBase, d, d1, d2);
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entityLivingBase, this, d, d1, d2));
    }
    
    
    /**
     * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
     * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
     * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
     */
    private float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_)
    {
        float f3;

        for (f3 = p_77034_2_ - p_77034_1_; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return p_77034_1_ + p_77034_3_ * f3;
    }
    
    
    

    protected int shouldRenderPass(EntityPlayer entitywerewolf, int i)
    {

        bindTexture(MoCreatures.proxy.getTexture("wereblank.png"));
        
        return 1;
    }
    
    //renders the model even if player is invisible
    @Override
    protected void renderModel(EntityLivingBase p_77036_1_, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_)
    {
        this.bindEntityTexture(p_77036_1_);

        this.mainModel.render(p_77036_1_, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
    }
    
    @Override
    protected boolean func_110813_b(EntityLivingBase p_110813_1_)
    {
        return Minecraft.isGuiEnabled() && p_110813_1_ != this.renderManager.livingPlayer && p_110813_1_.riddenByEntity == null;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entityLivingBase, int i, float f)
    {
        return shouldRenderPass((EntityPlayer) entityLivingBase, i);
    }


    protected ResourceLocation getEntityTexture(Entity entity) {
    	
    	switch (MoCreatures.proxy.colorForWitcheryPlayerWolfAndWerewolf)
    	{
	    	case 0:
	    		return MoCreatures.proxy.getTexture("wolfblack.png");
	    		
	        case 1:
	        	return MoCreatures.proxy.getTexture("wolftimber.png");
	        	
	        case 2:
	        	return MoCreatures.proxy.getTexture("wolfbrown.png");
	        	
	        default:
	        	return MoCreatures.proxy.getTexture("wolftimber.png");
        }
    }
}
