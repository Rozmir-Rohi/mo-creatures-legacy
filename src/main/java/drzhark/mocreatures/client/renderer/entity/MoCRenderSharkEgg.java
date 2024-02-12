package drzhark.mocreatures.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

// TODO 4FIX NOT NEEDED?
@SideOnly(Side.CLIENT)
public class MoCRenderSharkEgg extends RenderLiving {

    public MoCRenderSharkEgg(ModelBase modelBase, float f)
    {
        super(modelBase, f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }
}
