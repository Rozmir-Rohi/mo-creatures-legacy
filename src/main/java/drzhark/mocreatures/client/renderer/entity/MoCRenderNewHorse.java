package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.client.model.MoCModelNewHorse;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderNewHorse extends MoCRenderMoC {

    public MoCRenderNewHorse(MoCModelNewHorse modelBase)
    {
        super(modelBase, 0.5F);

    }

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityHorse)par1Entity).getTexture();
    }

    protected void adjustHeight(EntityLiving entityLiving, float FHeight)
    {
        GL11.glTranslatef(0.0F, FHeight, 0.0F);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityLiving, float f)
    {
        MoCEntityHorse entityhorse = (MoCEntityHorse) entityLiving;
        if (!entityhorse.getIsAdult() || entityhorse.getType() > 64)
        {
            stretch(entityhorse);
        }
        if (entityhorse.isGhost())
        {
            adjustHeight(entityhorse, -0.3F + (entityhorse.ghostHorseTransparencyFloat() / 5F));
        }
        super.preRenderCallback(entityLiving, f);

    }

    protected void stretch(MoCEntityHorse entityhorse)
    {
        float sizeFactor = entityhorse.getMoCAge() * 0.01F;
        if (entityhorse.getIsAdult())
        {
            sizeFactor = 1.0F;
        }
        if (entityhorse.getType() > 64) //donkey
        {
            sizeFactor *= 0.9F;
        }
        GL11.glScalef(sizeFactor, sizeFactor, sizeFactor);
    }

}
