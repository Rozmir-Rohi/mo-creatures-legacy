package drzhark.mocreatures.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.FMLInjectionData;

public class MoCConfiguration {
  public static final String CATEGORY_GENERAL = "general";
  
  public static final String CATEGORY_BLOCK = "block";
  
  public static final String CATEGORY_ITEM = "item";
  
  public static final String ALLOWED_CHARS = "._-";
  
  public static final String DEFAULT_ENCODING = "UTF-8";
  
  public static final String CATEGORY_SPLITTER = ".";
  
  public static final String NEW_LINE;
  
  private static final Pattern CONFIG_START = Pattern.compile("START: \"([^\\\"]+)\"");
  
  private static final Pattern CONFIG_END = Pattern.compile("END: \"([^\\\"]+)\"");
  
  public static final CharMatcher allowedProperties = CharMatcher.JAVA_LETTER_OR_DIGIT.or(CharMatcher.anyOf("._-"));
  
  private static MoCConfiguration PARENT = null;
  
  File file;
  
  public Map<String, MoCConfigCategory> categories = new TreeMap<String, MoCConfigCategory>();
  
  private Map<String, MoCConfiguration> children = new TreeMap<String, MoCConfiguration>();
  
  private boolean caseSensitiveCustomCategories;
  
  public String defaultEncoding = "UTF-8";
  
  private String fileName = null;
  
  public boolean isChild = false;
  
  private boolean changed = false;
  
  static {
    NEW_LINE = System.getProperty("line.separator");
  }
  
  public MoCConfiguration(File file) {
    this.file = file;
    String basePath = ((File)FMLInjectionData.data()[6]).getAbsolutePath().replace(File.separatorChar, '/').replace("/.", "");
    String path = file.getAbsolutePath().replace(File.separatorChar, '/').replace("/./", "/").replace(basePath, "");
    if (PARENT != null) {
      PARENT.setChild(path, this);
      isChild = true;
    } else {
      fileName = path;
      load();
    } 
  }
  
  public MoCConfiguration(File file, boolean caseSensitiveCustomCategories) {
    this(file);
    this.caseSensitiveCustomCategories = caseSensitiveCustomCategories;
  }
  
  public MoCConfiguration(File file, boolean caseSensitiveCustomCategories, boolean useNewLine) {
    this(file);
  }
  
  public MoCProperty get(String category, String key) {
    MoCConfigCategory cat = getCategory(category);
    if (cat.containsKey(key)) {
      MoCProperty prop = cat.get(key);
      return prop;
    } 
    return null;
  }
  
  public MoCProperty get(String category, String key, int defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, int defaultValue, String comment) {
    MoCProperty prop = get(category, key, Integer.toString(defaultValue), comment, MoCProperty.Type.INTEGER);
    if (!prop.isIntValue())
      prop.set(defaultValue); 
    return prop;
  }
  
  public MoCProperty get(String category, String key, boolean defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, boolean defaultValue, String comment) {
    MoCProperty prop = get(category, key, Boolean.toString(defaultValue), comment, MoCProperty.Type.BOOLEAN);
    if (!prop.isBooleanValue())
      prop.set(defaultValue); 
    return prop;
  }
  
  public MoCProperty get(String category, String key, double defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, double defaultValue, String comment) {
    MoCProperty prop = get(category, key, Double.toString(defaultValue), comment, MoCProperty.Type.DOUBLE);
    if (!prop.isDoubleValue())
      prop.set(defaultValue); 
    return prop;
  }
  
  public MoCProperty get(String category, String key, String defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, String defaultValue, String comment) {
    return get(category, key, defaultValue, comment, MoCProperty.Type.STRING);
  }
  
  public MoCProperty get(String category, String key, List<String> defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, List<String> defaultValue, String comment) {
    return get(category, key, defaultValue, comment, MoCProperty.Type.STRING);
  }
  
  public MoCProperty get(String category, String key, int[] defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, int[] defaultValue, String comment) {
    List<String> values = new ArrayList<String>();
    for (int i = 0; i < defaultValue.length; i++)
      values.add(Integer.toString(defaultValue[i])); 
    MoCProperty prop = get(category, key, values, comment, MoCProperty.Type.INTEGER);
    if (!prop.isIntList())
      prop.valueList = values; 
    return prop;
  }
  
  public MoCProperty get(String category, String key, double[] defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, double[] defaultValue, String comment) {
    List<String> values = new ArrayList<String>();
    for (int i = 0; i < defaultValue.length; i++)
      values.add(Double.toString(defaultValue[i])); 
    MoCProperty prop = get(category, key, values, comment, MoCProperty.Type.DOUBLE);
    if (!prop.isDoubleList())
      prop.valueList = values; 
    return prop;
  }
  
  public MoCProperty get(String category, String key, boolean[] defaultValue) {
    return get(category, key, defaultValue, (String)null);
  }
  
  public MoCProperty get(String category, String key, boolean[] defaultValue, String comment) {
    List<String> values = new ArrayList<String>();
    for (int i = 0; i < defaultValue.length; i++)
      values.add(Boolean.toString(defaultValue[i])); 
    MoCProperty prop = get(category, key, values, comment, MoCProperty.Type.BOOLEAN);
    if (!prop.isBooleanList())
      prop.valueList = values; 
    return prop;
  }
  
  public MoCProperty get(String category, String key, String defaultValue, String comment, MoCProperty.Type type) {
    if (!caseSensitiveCustomCategories)
      category = category.toLowerCase(Locale.ENGLISH); 
    MoCConfigCategory cat = getCategory(category);
    if (cat.containsKey(key)) {
      MoCProperty prop = cat.get(key);
      if (prop.getType() == null) {
        prop = new MoCProperty(prop.getName(), prop.value, type);
        cat.set(key, prop);
      } 
      prop.comment = comment;
      return prop;
    } 
    if (defaultValue != null) {
      MoCProperty prop = new MoCProperty(key, defaultValue, type);
      prop.set(defaultValue);
      cat.put(key, prop);
      prop.comment = comment;
      return prop;
    } 
    return null;
  }
  
  public MoCProperty get(String category, String key, List<String> defaultValue, String comment, MoCProperty.Type type) {
    if (!caseSensitiveCustomCategories)
      category = category.toLowerCase(Locale.ENGLISH); 
    MoCConfigCategory cat = getCategory(category);
    if (cat.containsKey(key)) {
      MoCProperty prop = cat.get(key);
      if (prop.getType() == null) {
        prop = new MoCProperty(prop.getName(), prop.getString(), type);
        cat.put(key, prop);
      } 
      prop.comment = comment;
      return prop;
    } 
    if (defaultValue != null) {
      MoCProperty prop = new MoCProperty(key, defaultValue, type);
      prop.comment = comment;
      cat.put(key, prop);
      return prop;
    } 
    return null;
  }
  
  public boolean hasCategory(String category) {
    return (categories.get(category) != null);
  }
  
  public boolean hasKey(String category, String key) {
    MoCConfigCategory cat = categories.get(category);
    return (cat != null && cat.containsKey(key));
  }
  
  public void load() {
    if (PARENT != null && PARENT != this)
      return; 
    BufferedReader buffer = null;
    UnicodeInputStreamReader input = null;
    try {
      if (file.getParentFile() != null)
        file.getParentFile().mkdirs(); 
      if (!file.exists() && !file.createNewFile())
        return; 
      if (file.canRead()) {
        input = new UnicodeInputStreamReader(new FileInputStream(file), defaultEncoding);
        defaultEncoding = input.getEncoding();
        buffer = new BufferedReader(input);
        MoCConfigCategory currentCat = null;
        MoCProperty.Type type = null;
        ArrayList<String> tmpList = null;
        int lineNum = 0;
        String name = null;
        while (true) {
          lineNum++;
          String line = buffer.readLine();
          if (line == null)
            break; 
          Matcher start = CONFIG_START.matcher(line);
          Matcher end = CONFIG_END.matcher(line);
          if (start.matches()) {
            fileName = start.group(1);
            categories = new TreeMap<String, MoCConfigCategory>();
            continue;
          } 
          if (end.matches()) {
            fileName = end.group(1);
            MoCConfiguration child = new MoCConfiguration();
            child.categories = categories;
            children.put(fileName, child);
            continue;
          } 
          int nameStart = -1, nameEnd = -1;
          boolean skip = false;
          boolean quoted = false;
          for (int i = 0; i < line.length() && !skip; i++) {
            if (Character.isLetterOrDigit(line.charAt(i)) || "._-".indexOf(line.charAt(i)) != -1 || (quoted && line.charAt(i) != '"')) {
              if (nameStart == -1)
                nameStart = i; 
              nameEnd = i;
            } else if (!Character.isWhitespace(line.charAt(i))) {
              String qualifiedName;
              MoCConfigCategory cat;
              MoCProperty prop;
              switch (line.charAt(i)) {
                case '#':
                  skip = true;
                  break;
                case '"':
                  if (quoted)
                    quoted = false; 
                  if (!quoted && nameStart == -1)
                    quoted = true; 
                  break;
                case '{':
                  name = line.substring(nameStart, nameEnd + 1);
                  qualifiedName = MoCConfigCategory.getQualifiedName(name, currentCat);
                  cat = categories.get(qualifiedName);
                  if (cat == null) {
                    currentCat = new MoCConfigCategory(name, currentCat);
                    categories.put(qualifiedName, currentCat);
                  } else {
                    currentCat = cat;
                  } 
                  name = null;
                  break;
                case '}':
                  if (currentCat == null)
                    throw new RuntimeException(String.format("Config file corrupt, attepted to close to many categories '%s:%d'", new Object[] { fileName, Integer.valueOf(lineNum) })); 
                  currentCat = currentCat.parent;
                  break;
                case '=':
                  name = line.substring(nameStart, nameEnd + 1);
                  if (currentCat == null)
                    throw new RuntimeException(String.format("'%s' has no scope in '%s:%d'", new Object[] { name, fileName, Integer.valueOf(lineNum) })); 
                  prop = new MoCProperty(name, line.substring(i + 1), type, true);
                  i = line.length();
                  currentCat.set(name, prop);
                  break;
                case ':':
                  type = MoCProperty.Type.tryParse(line.substring(nameStart, nameEnd + 1).charAt(0));
                  nameStart = nameEnd = -1;
                  break;
                case '<':
                  if (tmpList != null)
                    throw new RuntimeException(String.format("Malformed list MoCProperty \"%s:%d\"", new Object[] { fileName, Integer.valueOf(lineNum) })); 
                  name = line.substring(nameStart, nameEnd + 1);
                  if (currentCat == null)
                    throw new RuntimeException(String.format("'%s' has no scope in '%s:%d'", new Object[] { name, fileName, Integer.valueOf(lineNum) })); 
                  tmpList = new ArrayList<String>();
                  if (line.length() > i + 1) {
                    if (line.charAt(i + 1) == '>') {
                      i++;
                    } else {
                      line = line.substring(i + 1, line.length());
                      String[] values = line.split(":|\\>");
                      for (int j = 0; j < values.length; j++)
                        tmpList.add(values[j]); 
                      i = line.length() - 1;
                    } 
                  } else {
                    skip = true;
                    break;
                  } 
                case '>':
                  if (tmpList == null)
                    throw new RuntimeException(String.format("Malformed list MoCProperty \"%s:%d\"", new Object[] { fileName, Integer.valueOf(lineNum) })); 
                  currentCat.set(name, new MoCProperty(name, tmpList, type));
                  name = null;
                  tmpList = null;
                  type = null;
                  break;
                default:
                  throw new RuntimeException(String.format("Unknown character '%s' in '%s:%d'", new Object[] { Character.valueOf(line.charAt(i)), fileName, Integer.valueOf(lineNum) }));
              } 
            } 
          } 
          if (quoted)
            throw new RuntimeException(String.format("Unmatched quote in '%s:%d'", new Object[] { fileName, Integer.valueOf(lineNum) })); 
          if (tmpList != null && !skip)
            tmpList.add(line.trim()); 
        } 
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (buffer != null)
        try {
          buffer.close();
        } catch (IOException e) {} 
      if (input != null)
        try {
          input.close();
        } catch (IOException e) {} 
    } 
    resetChangedState();
  }
  
  public void save() {
    if (PARENT != null && PARENT != this) {
      PARENT.save();
      return;
    } 
    try {
      if (file.getParentFile() != null)
        file.getParentFile().mkdirs(); 
      if (!file.exists() && !file.createNewFile())
        return; 
      if (file.canWrite()) {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, defaultEncoding));
        buffer.write("# MoC Settings" + NEW_LINE + NEW_LINE);
        if (children.isEmpty()) {
          save(buffer);
        } else {
          for (Map.Entry<String, MoCConfiguration> entry : children.entrySet()) {
            buffer.write("START: \"" + entry.getKey() + "\"" + NEW_LINE);
            entry.getValue().save(buffer);
            buffer.write("END: \"" + entry.getKey() + "\"" + NEW_LINE + NEW_LINE);
          } 
        } 
        buffer.close();
        fos.close();
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private void save(BufferedWriter out) throws IOException {
    for (MoCConfigCategory cat : categories.values()) {
      if (!cat.isChild()) {
        cat.write(out, 0);
        out.newLine();
      } 
    } 
  }
  
  public MoCConfigCategory getCategory(String category) {
    MoCConfigCategory ret = categories.get(category.toLowerCase());
    if (ret == null)
      if (category.contains(".")) {
        String[] hierarchy = category.split("\\.");
        MoCConfigCategory parent = categories.get(hierarchy[0]);
        if (parent == null) {
          parent = new MoCConfigCategory(hierarchy[0]);
          categories.put(parent.getQualifiedName(), parent);
          changed = true;
        } 
        for (int i = 1; i < hierarchy.length; i++) {
          String name = MoCConfigCategory.getQualifiedName(hierarchy[i], parent);
          MoCConfigCategory child = categories.get(name);
          if (child == null) {
            child = new MoCConfigCategory(hierarchy[i], parent);
            categories.put(name, child);
            changed = true;
          } 
          ret = child;
          parent = child;
        } 
      } else {
        ret = new MoCConfigCategory(category);
        categories.put(category, ret);
        changed = true;
      }  
    return ret;
  }
  
  public void removeCategory(MoCConfigCategory category) {
    for (MoCConfigCategory child : category.getChildren())
      removeCategory(child); 
    if (categories.containsKey(category.getQualifiedName())) {
      categories.remove(category.getQualifiedName());
      if (category.parent != null)
        category.parent.removeChild(category); 
      changed = true;
    } 
  }
  
  public void addCustomCategoryComment(String category, String comment) {
    if (!caseSensitiveCustomCategories)
      category = category.toLowerCase(Locale.ENGLISH); 
    getCategory(category).setComment(comment);
  }
  
  private void setChild(String name, MoCConfiguration child) {
    if (!children.containsKey(name)) {
      children.put(name, child);
      changed = true;
    } else {
      MoCConfiguration old = children.get(name);
      child.categories = old.categories;
      child.fileName = old.fileName;
      old.changed = true;
    } 
  }
  
  public static void enableGlobalConfig() {
    PARENT = new MoCConfiguration(new File(Loader.instance().getConfigDir(), "global.cfg"));
    PARENT.load();
  }
  
  public static class UnicodeInputStreamReader extends Reader {
    private final InputStreamReader input;
    
    public UnicodeInputStreamReader(InputStream source, String encoding) throws IOException {
      String enc = encoding;
      byte[] data = new byte[4];
      PushbackInputStream pbStream = new PushbackInputStream(source, data.length);
      int read = pbStream.read(data, 0, data.length);
      int size = 0;
      int bom16 = (data[0] & 0xFF) << 8 | data[1] & 0xFF;
      int bom24 = bom16 << 8 | data[2] & 0xFF;
      int bom32 = bom24 << 8 | data[3] & 0xFF;
      if (bom24 == 15711167) {
        enc = "UTF-8";
        size = 3;
      } else if (bom16 == 65279) {
        enc = "UTF-16BE";
        size = 2;
      } else if (bom16 == 65534) {
        enc = "UTF-16LE";
        size = 2;
      } else if (bom32 == 65279) {
        enc = "UTF-32BE";
        size = 4;
      } else if (bom32 == -131072) {
        enc = "UTF-32LE";
        size = 4;
      } 
      if (size < read)
        pbStream.unread(data, size, read - size); 
      input = new InputStreamReader(pbStream, enc);
    }
    
    public String getEncoding() {
      return input.getEncoding();
    }
    
    @Override
	public int read(char[] cbuf, int off, int len) throws IOException {
      return input.read(cbuf, off, len);
    }
    
    @Override
	public void close() throws IOException {
      input.close();
    }
  }
  
  public boolean hasChanged() {
    if (changed)
      return true; 
    for (MoCConfigCategory cat : categories.values()) {
      if (cat.hasChanged())
        return true; 
    } 
    for (MoCConfiguration child : children.values()) {
      if (child.hasChanged())
        return true; 
    } 
    return false;
  }
  
  private void resetChangedState() {
    changed = false;
    for (MoCConfigCategory cat : categories.values())
      cat.resetChangedState(); 
    for (MoCConfiguration child : children.values())
      child.resetChangedState(); 
  }
  
  public Set<String> getCategoryNames() {
    return ImmutableSet.copyOf(categories.keySet());
  }
  
  public String getFileName() {
    if (file != null) {
      String fullName = file.getName();
      return fullName.substring(0, fullName.indexOf('.'));
    } 
    return "undefined";
  }
  
  public File getFile() {
    return file;
  }
  
  public MoCConfiguration() {}
}
