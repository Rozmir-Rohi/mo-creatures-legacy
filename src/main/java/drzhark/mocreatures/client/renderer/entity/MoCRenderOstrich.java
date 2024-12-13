package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.animal.MoCEntityOstrich;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderOstrich extends MoCRenderMoC {

    public MoCRenderOstrich(ModelBase modelBase, float f)
    {
        super(modelBase, 0.5F);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityOstrich)par1Entity).getTexture();
    }

    protected void adjustHeight(EntityLiving entityLiving, float FHeight)
    {
        GL11.glTranslatef(0.0F, FHeight, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityOstrich entityostrich = (MoCEntityOstrich) entityLiving;
        if (entityostrich.getType() == 1)
        {
            stretch(entityostrich);
        }

        super.preRenderCallback(entityLiving, f);

    }

    protected void stretch(MoCEntityOstrich entityostrich)
    {

        float f = entityostrich.getMoCAge() * 0.01F;
        GL11.glScalef(f, f, f);
    }

}