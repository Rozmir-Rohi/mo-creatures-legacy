package drzhark.mocreatures.network.message;

import java.lang.Character.UnicodeScript;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.CharMatcher;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import drzhark.mocreatures.MoCPetData;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.client.MoCClientProxy;
import drzhark.mocreatures.client.gui.helpers.MoCGUIEntityNamer;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.IMoCTameable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


public class MoCMessageUpdatePetName implements IMessage, IMessageHandler<MoCMessageUpdatePetName, IMessage> {

    String name;
    int entityId;

    public MoCMessageUpdatePetName() {}

    public MoCMessageUpdatePetName(int entityId)
    {
        this.entityId = entityId;
    }

    public MoCMessageUpdatePetName(int entityId, String name)
    {
        this.entityId = entityId;
        this.name = name;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
    	int byte_length = 0;
    	
    	char[] character_array = this.name.toCharArray();
    	
    	for (char character : character_array) //this loop goes through each character in the string and assigns them the amount of bytes they need
    	{
    		UnicodeScript script_that_character_is_written_in = Character.UnicodeScript.of(character);
    		
    		if (script_that_character_is_written_in == Character.UnicodeScript.LATIN)
    		{
    			byte_length += 1;
    		}
    		
    		if (Character.UnicodeBlock.of(character) == Character.UnicodeBlock.LATIN_1_SUPPLEMENT) //Latin characters needed for western languages (such as Spanish, German, Norwegian... ect)
    		{
    			byte_length += Character.charCount(Character.getNumericValue(character));
    		}
    		
    		if (script_that_character_is_written_in == Character.UnicodeScript.GREEK
    				|| script_that_character_is_written_in == Character.UnicodeScript.CYRILLIC //Russian
    				|| script_that_character_is_written_in == Character.UnicodeScript.ARABIC
    				|| script_that_character_is_written_in == Character.UnicodeScript.HEBREW
    				)
    		{
    			byte_length += 2;
    		}
    		
    		if (script_that_character_is_written_in == Character.UnicodeScript.HAN //Chinese
    				|| script_that_character_is_written_in == Character.UnicodeScript.HIRAGANA //Japanese Hiragana
    				|| script_that_character_is_written_in == Character.UnicodeScript.KATAKANA //Japanese Katakana
    				|| script_that_character_is_written_in == Character.UnicodeScript.HANGUL //Korean
    				|| script_that_character_is_written_in == Character.UnicodeScript.THAI
    				|| script_that_character_is_written_in == Character.UnicodeScript.DEVANAGARI //Hindi or Sanskrit
    				
    				//Below are more characters required for certain eastern languages (such as Kanji for Japanese)
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_COMPATIBILITY
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_STROKES
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
    				|| Character.UnicodeBlock.of(character) == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS
    				)
    		{
    			byte_length += 3;
    		}
    	}
        
        buffer.writeInt(byte_length);
        
        
        buffer.writeBytes(this.name.getBytes());

        
        buffer.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        int nameLength = buffer.readInt();
        
        this.name = new String(buffer.readBytes(nameLength).array(), StandardCharsets.UTF_8);
        this.entityId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageUpdatePetName message, MessageContext ctx)
    {
        Entity pet = null;
        List<Entity> entityList = ctx.getServerHandler().playerEntity.worldObj.loadedEntityList;
        String ownerName = "";

        for (Entity entity : entityList)
        {
            if (entity.getEntityId() == message.entityId && entity instanceof IMoCTameable)
            {
                ((IMoCEntity) entity).setName(message.name);
                ownerName = ((IMoCEntity) entity).getOwnerName();
                pet = entity;
                break;
            }
        }
        // update petdata
        MoCPetData petData = MoCreatures.instance.mapData.getPetData(ownerName);
        if (petData != null && pet != null && ((IMoCTameable)pet).getOwnerPetId() != -1)
        {
            int id = ((IMoCTameable)pet).getOwnerPetId();
            NBTTagList tag = petData.getOwnerRootNBT().getTagList("TamedList", 10);
            for (int i = 0; i < tag.tagCount(); i++)
            {
                NBTTagCompound nbt = (NBTTagCompound)tag.getCompoundTagAt(i);
                if (nbt.getInteger("PetId") == id)
                {
                    nbt.setString("Name", message.name);
                    ((IMoCTameable)pet).setName(message.name);
                }
            }
        }
        return null;
    }

    @Override
    public String toString()
    {
        return String.format("MoCMessageUpdatePetName - entityId:%s, name:%s", this.entityId, this.name);
    }
}