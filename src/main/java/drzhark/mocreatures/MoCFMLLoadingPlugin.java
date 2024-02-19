package drzhark.mocreatures;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;


@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.Name("MOC Legacy ASM")
public class MoCFMLLoadingPlugin implements IFMLLoadingPlugin {
	public static File location;
	
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { MoCClassTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }
   
   @Override
   public void injectData(Map<String, Object> data)
   {
      location = (File)data.get("coremodLocation");
   }

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

}
