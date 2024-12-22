package drzhark.mocreatures.client.events;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import drzhark.mocreatures.entity.animal.MoCEntityHorse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

public class MoCRenderHorseJumpBarEvent extends Gui {
	
	private Minecraft mc;
	ScaledResolution res = null;

	public MoCRenderHorseJumpBarEvent(Minecraft mc)
	{
		super();
		this.mc = mc;
	}
	
	@SubscribeEvent
	public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
		EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
		
		if (
				entityPlayer.ridingEntity != null
				&& entityPlayer.ridingEntity instanceof MoCEntityHorse
				&& event.type == ElementType.EXPERIENCE
			)
		{
			event.setCanceled(true);
			
			res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			if (res != null)
			{
					int width = res.getScaledWidth();
			        int height = res.getScaledHeight();
					renderCustomJumpBar(width, height);
			}
		}
    }
	
	protected void renderCustomJumpBar(int width, int height)
    {
        bind(icons);
        //if (pre(JUMPBAR)) return;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);

        mc.mcProfiler.startSection("jumpBar");
        
        float charge = ((MoCEntityHorse)  Minecraft.getMinecraft().thePlayer.ridingEntity).getHorseJumpPower();
        
        final int barWidth = 182;
        int x = (width / 2) - (barWidth / 2);
        int filled = (int)(charge * (barWidth + 1));
        int top = height - 32 + 3;

        drawTexturedModalRect(x, top, 0, 84, barWidth, 5);

        if (filled > 0)
        {
            this.drawTexturedModalRect(x, top, 0, 89, filled, 5);
        }

        GL11.glEnable(GL11.GL_BLEND);
        mc.mcProfiler.endSection();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        //post(JUMPBAR);
    }
	
	private void bind(ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }
}
