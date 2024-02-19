package drzhark.mocreatures;


import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.launchwrapper.IClassTransformer;

public class MoCClassTransformer implements IClassTransformer {
   public byte[] transform(String name, String transformedName, byte[] classBeingTransformed) 
   {
      if (name.equals("ModelWolfAltar") || name.equals("com.emoniph.witchery.client.model.ModelWolfAltar"))
      {
         System.out.println("********* INSIDE CLASS TRANSFORMER ABOUT TO PATCH: " + name);
         classBeingTransformed = this.patchClassInJar(name, classBeingTransformed, name, MoCFMLLoadingPlugin.location);
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
            System.out.println("[Mo' Creatures Legacy]: " + name + " not found in " + location.getName());
         }
         else
         {
            InputStream zin = zipFile.getInputStream(entry);
            bytes = new byte[(int)entry.getSize()];
            zin.read(bytes);
            zin.close();
            System.out.println("[Mo' Creatures Legacy]: Class " + name + " patched!");
         }

         zipFile.close();
         return bytes;
      }
      catch (Exception var8) 
      {
         throw new RuntimeException("[Mo' Creatures Legacy]: Error overriding " + name + " from " + location.getName(), var8);
      }
   }
}
