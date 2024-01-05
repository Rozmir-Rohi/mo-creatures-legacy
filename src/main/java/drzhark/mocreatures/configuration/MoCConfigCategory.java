package drzhark.mocreatures.configuration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

public class MoCConfigCategory implements Map<String, MoCProperty> {
  private String name;
  
  private String comment;
  
  private ArrayList<MoCConfigCategory> children = new ArrayList<MoCConfigCategory>();
  
  private Map<String, MoCProperty> properties = new TreeMap<String, MoCProperty>();
  
  public final MoCConfigCategory parent;
  
  private boolean changed = false;
  
  public MoCConfigCategory(String name) {
    this(name, null, true);
  }
  
  public MoCConfigCategory(String name, MoCConfigCategory parent) {
    this(name, parent, true);
  }
  
  public MoCConfigCategory(String name, boolean newline) {
    this(name, null, newline);
  }
  
  public MoCConfigCategory(String name, MoCConfigCategory parent, boolean newline) {
    this.name = name;
    this.parent = parent;
    if (parent != null)
      parent.children.add(this); 
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof MoCConfigCategory) {
      MoCConfigCategory cat = (MoCConfigCategory)obj;
      return (this.name.equals(cat.name) && this.children.equals(cat.children));
    } 
    return false;
  }
  
  public String getQualifiedName() {
    return getQualifiedName(this.name, this.parent);
  }
  
  public static String getQualifiedName(String name, MoCConfigCategory parent) {
    return (parent == null) ? name : (parent.getQualifiedName() + "." + name);
  }
  
  public MoCConfigCategory getFirstParent() {
    return (this.parent == null) ? this : this.parent.getFirstParent();
  }
  
  public boolean isChild() {
    return (this.parent != null);
  }
  
  public Map<String, MoCProperty> getValues() {
    return this.properties;
  }
  
  public void setComment(String comment) {
    this.comment = comment;
  }
  
  public boolean containsKey(String key) {
    return this.properties.containsKey(key);
  }
  
  public MoCProperty get(String key) {
    return this.properties.get(key);
  }
  
  public void set(String key, MoCProperty value) {
    this.properties.put(key, value);
  }
  
  private void write(BufferedWriter out, String... data) throws IOException {
    write(out, true, data);
  }
  
  private void write(BufferedWriter out, boolean new_line, String... data) throws IOException {
    for (int x = 0; x < data.length; x++) {
      if (data[x] != null)
        out.write(data[x]); 
    } 
    if (new_line)
      out.write(MoCConfiguration.NEW_LINE); 
  }
  
  public void write(BufferedWriter out, int indent) throws IOException {
    String pad0 = getIndent(indent);
    String pad1 = getIndent(indent + 1);
    String pad2 = getIndent(indent + 2);
    write(out, new String[] { pad0, "####################" });
    write(out, new String[] { pad0, "# ", this.name });
    if (this.comment != null) {
      write(out, new String[] { pad0, "#===================" });
      Splitter splitter = Splitter.onPattern("\r?\n");
      for (String line : splitter.split(this.comment)) {
        write(out, new String[] { pad0, "# ", line });
      } 
    } 
    write(out, new String[] { pad0, "####################", MoCConfiguration.NEW_LINE });
    if (!MoCConfiguration.allowedProperties.matchesAllOf(this.name))
      this.name = '"' + this.name + '"'; 
    write(out, new String[] { pad0, this.name, " {" });
    MoCProperty[] props = (MoCProperty[])this.properties.values().toArray((Object[])new MoCProperty[this.properties.size()]);
    for (int x = 0; x < props.length; x++) {
      MoCProperty prop = props[x];
      if (prop.comment != null) {
        if (x != 0)
          out.newLine(); 
        Splitter splitter = Splitter.onPattern("\r?\n");
        for (String commentLine : splitter.split(prop.comment)) {
          write(out, new String[] { pad1, "# ", commentLine });
        } 
      } 
      String propName = prop.getName();
      if (!MoCConfiguration.allowedProperties.matchesAllOf(propName))
        propName = '"' + propName + '"'; 
      if (prop.isList()) {
        char type = prop.getType().getID();
        write(out, false, new String[] { pad1 + String.valueOf(type), ":", propName, " <" });
        for (int i = 0; i < prop.valueList.size(); i++) {
          String line = prop.valueList.get(i);
          if (prop.valueList.size() == i + 1) {
            write(out, false, new String[] { line });
          } else {
            write(out, false, new String[] { line + ":" });
          } 
        } 
        write(out, false, new String[] { ">", MoCConfiguration.NEW_LINE });
      } else if (prop.getType() == null) {
        write(out, false, new String[] { propName, "=", prop.getString() });
      } else {
        char type = prop.getType().getID();
        write(out, new String[] { pad1, String.valueOf(type), ":", propName, "=", prop.getString() });
      } 
    } 
    for (MoCConfigCategory child : this.children)
      child.write(out, indent + 1); 
    write(out, new String[] { pad0, "}", MoCConfiguration.NEW_LINE });
  }
  
  private String getIndent(int indent) {
    StringBuilder buf = new StringBuilder("");
    for (int x = 0; x < indent; x++)
      buf.append("    "); 
    return buf.toString();
  }
  
  public boolean hasChanged() {
    if (this.changed)
      return true; 
    for (MoCProperty prop : this.properties.values()) {
      if (prop.hasChanged())
        return true; 
    } 
    return false;
  }
  
  void resetChangedState() {
    this.changed = false;
    for (MoCProperty prop : this.properties.values())
      prop.resetChangedState(); 
  }
  
  public int size() {
    return this.properties.size();
  }
  
  public boolean isEmpty() {
    return this.properties.isEmpty();
  }
  
  public boolean containsKey(Object key) {
    return this.properties.containsKey(key);
  }
  
  public boolean containsValue(Object value) {
    return this.properties.containsValue(value);
  }
  
  public MoCProperty get(Object key) {
    return this.properties.get(key);
  }
  
  public MoCProperty put(String key, MoCProperty value) {
    this.changed = true;
    return this.properties.put(key, value);
  }
  
  public MoCProperty remove(Object key) {
    this.changed = true;
    return this.properties.remove(key);
  }
  
  public void putAll(Map<? extends String, ? extends MoCProperty> m) {
    this.changed = true;
    this.properties.putAll(m);
  }
  
  public void clear() {
    this.changed = true;
    this.properties.clear();
  }
  
  public Set<String> keySet() {
    return this.properties.keySet();
  }
  
  public Collection<MoCProperty> values() {
    return this.properties.values();
  }
  
  public Set<Map.Entry<String, MoCProperty>> entrySet() {
    return (Set<Map.Entry<String, MoCProperty>>)ImmutableSet.copyOf(this.properties.entrySet());
  }
  
  public Set<MoCConfigCategory> getChildren() {
    return (Set<MoCConfigCategory>)ImmutableSet.copyOf(this.children);
  }
  
  public void removeChild(MoCConfigCategory child) {
    if (this.children.contains(child)) {
      this.children.remove(child);
      this.changed = true;
    } 
  }
}
