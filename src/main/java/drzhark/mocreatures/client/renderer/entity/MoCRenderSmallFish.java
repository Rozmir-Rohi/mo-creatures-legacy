package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelSmallFish;
import drzhark.mocreatures.entity.aquatic.MoCEntitySmallFish;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class MoCRenderSmallFish extends MoCRenderMoC {
	
	private static final ResourceLocation anglerfishGlowingTextures = MoCreatures.proxy.getTexture("smallfish_anglerfish_emissive.png");

	public MoCRenderSmallFish(MoCModelSmallFish modelbase, float f) {
		super(modelbase, f);
		this.setRenderPassModel(new MoCModelSmallFish());
	}
	
	protected int shouldRenderPass(MoCEntitySmallFish p_77032_1_, int p_77032_2_, float p_77032_3_) //controls emissive textures for anglerfish
    {
        if (p_77032_1_.getType() != 4 //don't render emissive texture if the small fish isn't an anglerfish
        		|| p_77032_2_ != 0)
        {
            return -1;
        }
        else
        {
            this.bindTexture(anglerfishGlowingTextures);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            if (p_77032_1_.isInvisible())
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }

            char c0 = 61680;
            int j = c0 % 65536;
            int k = c0 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return 1;
        }
    }
	
	@Override
	protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        return this.shouldRenderPass((MoCEntitySmallFish)p_77032_1_, p_77032_2_, p_77032_3_);
    }

}