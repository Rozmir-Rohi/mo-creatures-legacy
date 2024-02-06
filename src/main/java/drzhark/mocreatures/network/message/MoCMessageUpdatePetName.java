package drzhark.mocreatures.network.message;

import java.lang.Character.UnicodeScript;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import drzhark.mocreatures.MoCPetData;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.IMoCEntity;
import drzhark.mocreatures.entity.IMoCTameable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
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
    	int byteLength = 0;
    	
    	char[] characterArray = this.name.toCharArray();
    	
    	for (char character : characterArray) //this loop goes through each character in the string and assigns them the amount of bytes they need
    	{	
    		
    		UnicodeScript scriptThatCharacterIsWrittenIn = Character.UnicodeScript.of(character);
    		
    		if (scriptThatCharacterIsWrittenIn == Character.UnicodeScript.LATIN) //English characters
    		{
    			byteLength += 1;
    		}
    		
    		if (Character.UnicodeBlock.of(character) == Character.UnicodeBlock.LATIN_1_SUPPLEMENT) //Latin characters needed for certain western languages (such as Spanish, German, Norwegian... ect)
    		{
    			byteLength += Character.charCount(Character.getNumericValue(character));
    		}
    		
    		if (scriptThatCharacterIsWrittenIn == Character.UnicodeScript.GREEK
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.CYRILLIC //Russian
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.ARABIC
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.HEBREW
    				)
    		{
    			byteLength += 2;
    		}
    		
    		if (scriptThatCharacterIsWrittenIn == Character.UnicodeScript.HAN //Chinese
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.HIRAGANA //Japanese Hiragana
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.KATAKANA //Japanese Katakana
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.HANGUL //Korean
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.THAI
    				|| scriptThatCharacterIsWrittenIn == Character.UnicodeScript.DEVANAGARI //Hindi or Sanskrit
    				
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
    			byteLength += 3;
    		}
    	}
        
        buffer.writeInt(byteLength);
        
        
        buffer.writeBytes(StandardCharsets.UTF_8.encode(this.name)); //encodes to UTF-8 before writeBytes to properly process special characters

        
        buffer.writeInt(this.entityId);
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        int nameLength = buffer.readInt();
        
        this.name = new String(buffer.readBytes(nameLength).array(), StandardCharsets.UTF_8); //set the encoding to UTF-8 within the string to properly output special characters
        
        this.entityId = buffer.readInt();
    }

    @Override
    public IMessage onMessage(MoCMessageUpdatePetName message, MessageContext context)
    {
        Entity pet = null;
        List<Entity> entityList = context.getServerHandler().playerEntity.worldObj.loadedEntityList;
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
            for (int index = 0; index < tag.tagCount(); index++)
            {
                NBTTagCompound nbt = (NBTTagCompound)tag.getCompoundTagAt(index);
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