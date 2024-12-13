package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.entity.item.MoCEntityThrowableBlockForGolem;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderThrowableBlockForGolem extends Render {

    private final RenderBlocks blockRenderer = new RenderBlocks();
    private static final ResourceLocation TEXTURE_TERRAIN = new ResourceLocation("terrain.png");

    public MoCRenderThrowableBlockForGolem()
    {
        shadowSize = 0.5F;
    }

    public void renderMyRock(MoCEntityThrowableBlockForGolem entity_throwable_block, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glRotatef(((100 - entity_throwable_block.acceleration) / 10F) * 36F, 0F, -1F, 0.0F);
        bindEntityTexture(entity_throwable_block);
        blockRenderer.renderBlockAsItem(entity_throwable_block.getMyBlock(), entity_throwable_block.getMetadata(), entity_throwable_block.getBrightness(par9));
        GL11.glPopMatrix();
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        renderMyRock((MoCEntityThrowableBlockForGolem) par1Entity, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation func_110808_a(MoCEntityThrowableBlockForGolem throwable_block)
    {
        return TextureMap.locationBlocksTexture;
    }

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return func_110808_a((MoCEntityThrowableBlockForGolem)par1Entity);
    }
}
