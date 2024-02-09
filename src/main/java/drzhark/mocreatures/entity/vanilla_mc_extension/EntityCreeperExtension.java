package drzhark.mocreatures.entity.vanilla_mc_extension;

import drzhark.mocreatures.entity.animal.MoCEntityKitty;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;

public class EntityCreeperExtension extends EntityCreeper{

	public EntityCreeperExtension(World world) {
		super(world);
		tasks.addTask(3, new EntityAIAvoidEntity(this, MoCEntityKitty.class, 6.0F, 1.0D, 1.2D));
	}

}
