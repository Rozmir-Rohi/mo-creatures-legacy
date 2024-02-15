package drzhark.mocreatures.client.renderer.entity;

import drzhark.mocreatures.client.model.MoCModelWerewolfHuman;
import drzhark.mocreatures.entity.witchery_integration.MoCEntityWerewolfMinecraftComesAliveVillagerWitchery;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

public class MoCRenderWerewolfMinecraftComesAliveVillagerWitchery extends RenderLiving {
	public MoCRenderWerewolfMinecraftComesAliveVillagerWitchery(MoCModelWerewolfHuman werewolfHumanModel, float f)
    {
        super(werewolfHumanModel, f);
    }
	
	@Override
    public void doRender(EntityLiving entityLiving, double x, double y, double z, float rotationYaw, float rotationPitch)
    {
        super.doRender(entityLiving, x, y, z, rotationYaw, rotationPitch);

    }

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{
        return ((MoCEntityWerewolfMinecraftComesAliveVillagerWitchery) entity).getTexture();
    }

}
