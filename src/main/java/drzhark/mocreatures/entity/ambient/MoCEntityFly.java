package drzhark.mocreatures.entity.ambient;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityInsect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MoCEntityFly extends MoCEntityInsect
{
    public MoCEntityFly(World world)
    {
        super(world);
        texture = "fly.png";
    }

    private int soundCount;// = 50;

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();

        if (MoCreatures.isServer())
        {

            if (getIsFlying() && rand.nextInt(200) == 0)
            {
                setIsFlying(false);
            }

            EntityPlayer entityPlayer = worldObj.getClosestPlayerToEntity(this, 5D);
            if (entityPlayer != null && getIsFlying() && --soundCount == -1)
            {
                MoCTools.playCustomSound(this, "fly", this.worldObj);
                soundCount = 55;
            }
        }
    }

    @Override
    protected float getFlyingSpeed()
    {
        return 0.7F;
    }

    @Override
    protected float getWalkingSpeed()
    {
        return 0.3F;
    }

    @Override
    public boolean isMyFollowFood(ItemStack itemstack)
    {
        return itemstack != null && itemstack.getItem() == Items.rotten_flesh;
    }
}