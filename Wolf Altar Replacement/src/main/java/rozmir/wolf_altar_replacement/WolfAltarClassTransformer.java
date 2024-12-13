package rozmir.wolf_altar_replacement;


import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.launchwrapper.IClassTransformer;

public class WolfAltarClassTransformer implements IClassTransformer {
   public byte[] transform(String name, String transformedName, byte[] classBeingTransformed) 
   {
	   if (name.equals("ModelWolfAltar") || name.equals("com.emoniph.witchery.client.model.ModelWolfAltar"))
	   {
		   System.out.println("[Wolf Altar Replacement]: Currently inside the transformer and about to patch: " + name);
		   classBeingTransformed = patchClassInJar(name, classBeingTransformed, name, WolfAltarFMLLoadingPlugin.location);
	   }
	   return classBeingTransformed;
   }

   public byte[] patchClassInJar(String name, byte[] bytes, String obfuscatedName, File location)
   {
	      try 
	      {
	         ZipFile zipFile = new ZipFile(location);
	         ZipEntry entry = zipFile.getEntry(name.replace('.', '/') + ".class");
	         if (entry == null) 
	         {
	            System.out.println("[Wolf Altar Replacement]: " + name + " not found in " + location.getName());
	         }
	         else
	         {
	            InputStream zin = zipFile.getInputStream(entry);
	            bytes = new byte[(int)entry.getSize()];
	            zin.read(bytes);
	            zin.close();
	            System.out.println("[Wolf Altar Replacement]: Class " + name + " has been patched!");
	         }
	
	         zipFile.close();
	         return bytes;
	      }
	      catch (Exception var8) 
	      {
	         throw new RuntimeException("[Wolf Altar Replacement]: Error overriding " + name + " from " + location.getName(), var8);
	      }
   }
}
