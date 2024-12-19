package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.monster.MoCEntityHellRat;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderHellRat extends MoCRenderRat {

    public MoCRenderHellRat(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    protected void stretch(EntityLivingBase entityLiving)
    {
        float f = 1.3F;
        GL11.glScalef(f, f, f);
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity entity) {
        return ((MoCEntityHellRat)entity).getTexture();
    }
}
