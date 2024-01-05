package drzhark.mocreatures.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.model.MoCModelKittyBed;
import drzhark.mocreatures.client.model.MoCModelKittyBed2;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class MoCRenderKittyBed extends RenderLiving {

    public MoCModelKittyBed kittybed;
    private int mycolor;
    public static float fleeceColorTable[][] = { // all colors in RGB mode  [r, g, b]   with colors spanning from 0-1   | you can get their true RGB values by multiplying them by 255
    		{ 1.0F, 1.0F, 1.0F }, //0 white (item meta id: 15)  <--- the value for white in the matrix is only a placeholder and it is not actually used for colorizing
    		{ 0.92F, 0.52F, 0.25F }, //1 orange (item meta id: 14)
    		{ 0.9F, 0.5F, 0.85F }, //2 magenta (item meta id: 13)
    		{ 0.6F, 0.7F, 0.95F }, //3 light blue (item meta id: 12)
    		{ 0.9F, 0.9F, 0.2F }, //4 yellow (item meta id: 11)
    		{ 0.5F, 0.8F, 0.1F }, //5 lime (item meta id: 10)
    		{ 0.92F, 0.60F, 0.68F }, //6 pink (item meta id: 9)
    		{ 0.35F, 0.35F, 0.35F }, //7 gray (item meta id: 8)
    		{ 1.0F, 1.0F, 1.0F }, //8 light gray (item meta id: 7)
    		{ 0.3F, 0.6F, 0.7F }, //9 cyan (item meta id: 6)
    		{ 0.7F, 0.4F, 0.9F }, //10 purple (item meta id: 5)
    		{ 0.2F, 0.4F, 0.8F }, //11 blue (item meta id: 4)
    		{ 0.48F, 0.27F, 0F }, //12 brown (item meta id: 3)
    		{ 0.4F, 0.5F, 0.2F }, //13 green (item meta id: 2)
    		{ 0.8F, 0.24F, 0.24F }, //14 red (item meta id: 1)
    		{ 0.18F, 0.18F, 0.18F } }; //15 black (item meta id: 0) 

    public MoCRenderKittyBed(MoCModelKittyBed modelkittybed, MoCModelKittyBed2 modelkittybed2, float f)
    {
        super(modelkittybed, f);
        kittybed = modelkittybed;
        setRenderPassModel(modelkittybed2);
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entityliving, float f)
    {
        MoCEntityKittyBed entitykittybed = (MoCEntityKittyBed) entityliving;
        mycolor = entitykittybed.getSheetColor();
        kittybed.hasMilk = entitykittybed.getHasMilk();
        kittybed.hasFood = entitykittybed.getHasFood();
        kittybed.pickedUp = entitykittybed.getPickedUp();
        kittybed.milklevel = entitykittybed.milklevel;
    }

    protected int shouldRenderPass(MoCEntityKittyBed entityliving, int i)
    {
        this.bindTexture(MoCreatures.proxy.getTexture("kittybed_model.png"));
        float alpha = 0.5F;    // alpha value (opacity of color overlaid to default texture)
        int fleeceColorTable_row = MoCTools.colorize(mycolor);
        
        if (fleeceColorTable_row == 1 || fleeceColorTable_row == 6 ) //custom alpha for orange and pink kitty bed
        {alpha = 0.7F;}
        
        if (fleeceColorTable_row > 0) //colorize all kitty beds except white kitty bed
        	{
        		GL11.glColor3f(alpha * fleeceColorTable[fleeceColorTable_row][0], alpha * fleeceColorTable[fleeceColorTable_row][1], alpha * fleeceColorTable[fleeceColorTable_row][2]);
        	}
        return 1;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entityliving, int i, float f)
    {
        return shouldRenderPass((MoCEntityKittyBed)entityliving, i);
    }

    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return ((MoCEntityKittyBed)par1Entity).getTexture();
    }
}
