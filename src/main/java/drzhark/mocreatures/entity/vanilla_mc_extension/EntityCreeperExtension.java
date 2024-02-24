package drzhark.mocreatures.entity.vanilla_mc_extension;

import java.util.List;

import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.animal.MoCEntityKitty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityCreeperExtension extends EntityCreeper {
	
	private boolean isAffectedByChemicalX = false;

	public EntityCreeperExtension(World world)
	{
		super(world);
		tasks.addTask(3, new EntityAIAvoidEntity(this, MoCEntityKitty.class, 6.0F, 1.0D, 1.2D));
	}

	public void onUpdate()
    {
		super.onUpdate();
		
		if (MoCreatures.isMutantCreaturesLoaded) //fixes creeper extension not having a chance to mutate into a mutant creeper if the Mutant Creatures mod is installed
		{
			if (!isAffectedByChemicalX)
			{
				List entitiesNearbyList = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(1, 1, 1));
	
		        int iterationLength = entitiesNearbyList.size();
	
		        if (iterationLength > 0)
		        {
			        for (int index = 0; index < iterationLength; index++)
			        {
			            Entity entityNearby = (Entity) entitiesNearbyList.get(index);
			            
			            if (	//ignore entityNearby if it is an instance of the following
			            		entityNearby instanceof EntityCreature
			            		|| entityNearby instanceof EntityMob
			            		|| entityNearby instanceof EntityItem
			            		|| entityNearby instanceof EntityPlayer
			            	)
			            	
			            {
			            	continue;
			            }
			            
			            if (EntityList.getEntityString(entityNearby).equals("MutantCreatures.SkullSpirit"))
			            {
			            	isAffectedByChemicalX = true;
			            }
			        }
		        }
			}
			
			if (isAffectedByChemicalX)
			{
				if (MoCreatures.isServer() && rand.nextInt(100) == 0)
				{
					Entity mutantCreeper = EntityList.createEntityByName("MutantCreatures.MutantCreeper", worldObj);
					
					mutantCreeper.copyLocationAndAnglesFrom(this);
					
					worldObj.createExplosion(this, posX, posY, posZ, 4F, false);
					
		            worldObj.spawnEntityInWorld((Entity) mutantCreeper);
		            
					setDead();
				}
			}
		}
    }
}
