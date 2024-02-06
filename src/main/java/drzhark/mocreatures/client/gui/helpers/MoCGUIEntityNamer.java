package drzhark.mocreatures.client.gui.helpers;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.IMoCTameable;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageUpdatePetName;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class MoCGUIEntityNamer extends GuiScreen {
    protected String screenTitle;
    private final IMoCEntity NamedEntity;
    private int updateCounter;
    private String nameToSet;
    protected int xSize;
    protected int ySize;
    private static TextureManager textureManager = MoCClientProxy.mc.getTextureManager();
    private static final ResourceLocation TEXTURE_MOCNAME = new ResourceLocation("mocreatures", MoCreatures.proxy.GUI_TEXTURE + "mocname.png");

    public MoCGUIEntityNamer(IMoCEntity mocAnimal, String string)
    {
        xSize = 256;
        ySize = 181;
        screenTitle = StatCollector.translateToLocal("gui_namer.MoCreatures.chooseName");
        NamedEntity = mocAnimal;
        nameToSet = string;
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButton(0, (width / 2) - 100, (height/ 2) + 55, StatCollector.translateToLocal("gui_namer.MoCreatures.done"))); //1.5
    }

    public void updateName()
    {
        NamedEntity.setName(nameToSet);
        
        
        MoCMessageHandler.INSTANCE.sendToServer(new MoCMessageUpdatePetName(((EntityLiving) NamedEntity).getEntityId(), nameToSet));
        mc.displayGuiScreen(null);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        if (!guiButton.enabled) { return; }
        if ((guiButton.id == 0) && (this.nameToSet != null) && (!this.nameToSet.equals("")))
        {
            updateName();
        }
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        textureManager.bindTexture(TEXTURE_MOCNAME);
        int guiPositionX = (width - xSize) / 2;
        int guiPositionY = (height - (ySize + 16)) / 2;
        
        drawTexturedModalRect(guiPositionX, guiPositionY, 0, 0, xSize, ySize);
        drawCenteredString(fontRendererObj, screenTitle, width / 2, (int) (height / 2) - 20 , 0xffffff);
        drawCenteredString(fontRendererObj, nameToSet, width / 2, (int) (height / 2) , 0xffffff);
        super.drawScreen(x, y, f);
    }

    @Override
    public void handleKeyboardInput()
    {
        if (Keyboard.getEventKeyState())
        {
            if (Keyboard.getEventKey() == 28) // Handle Enter Key
            {
                updateName();
            }
        }
        super.handleKeyboardInput();
    }

    @Override
    protected void keyTyped(char character, int keyCode)
    {
        if ((keyCode == 14) && (nameToSet.length() > 0))
        {
            nameToSet = nameToSet.substring(0, nameToSet.length() - 1);
        }
        
        
        if ( ((character != 22) && (!ChatAllowedCharacters.isAllowedCharacter(character))) || (nameToSet.length() >= 15))
        {
        }
        
        
        else
        {
        	String characterString = "";
        			
        	if (character == 22) //22 is the crtl + V  (paste function)
            {
            	characterString = getClipboardString();
            }
        	else {characterString = Character.toString(character);}
        	
            StringBuilder name = new StringBuilder(nameToSet);
            
            
            name.append(characterString);
            nameToSet = name.toString();
        }
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        if (NamedEntity instanceof IMoCTameable)
        {
            IMoCTameable tamedEntity = (IMoCTameable)NamedEntity;
            tamedEntity.playTameEffect(true);
        }
    }

    @Override
    public void updateScreen()
    {
        updateCounter++;
    }
}