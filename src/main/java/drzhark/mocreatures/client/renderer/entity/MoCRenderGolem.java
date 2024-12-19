package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.model.MoCModelGolem;
import drzhark.mocreatures.entity.monster.MoCEntityBigGolem;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderGolem extends MoCRenderMoC {

    private final ModelBase MoCModelG = new MoCModelGolem();

    public MoCRenderGolem(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    /**
     * A method used to render a creeper's powered form as a pass model.
     */
    protected int renderGPassModel(MoCEntityBigGolem entity, int par2, float par3)
    {
        boolean depth = true;

        ResourceLocation effectTexture = entity.getEffectTexture();
        if (effectTexture != null)
        {
            if (depth)
            {
                GL11.glDepthMask(false);
            }
            else
            {
                GL11.glDepthMask(true);
            }

            if (par2 == 1)
            {
                float var4 = entity.ticksExisted + par3;
                bindTexture(effectTexture);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                float var5 = var4 * 0.01F;
                float var6 = var4 * 0.01F;
                GL11.glTranslatef(var5, var6, 0.0F);
                setRenderPassModel(MoCModelG);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_BLEND);
                float var7 = 0.5F;
                GL11.glColor4f(var7, var7, var7, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
                return 1;
            }

            if (par2 == 2)
            {
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }

        return -1;
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    @Override
    protected int shouldRenderPass(EntityLivingBase entityLiving, int par2, float par3)
    {
        return renderGPassModel((MoCEntityBigGolem) entityLiving, par2, par3);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityBigGolem)entity).getTexture();
    }
}
