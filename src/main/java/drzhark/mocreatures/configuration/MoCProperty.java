package drzhark.mocreatures.configuration;

import java.util.ArrayList;
import java.util.List;

public class MoCProperty {
  private String name;
  
  public String value;
  
  public String comment;
  
  public List<String> valueList;
  
  private final boolean wasRead;
  
  private final boolean isList;
  
  private final Type type;
  
  public enum Type {
    STRING, INTEGER, BOOLEAN, DOUBLE;
    
    private static Type[] values = new Type[] { STRING, INTEGER, BOOLEAN, DOUBLE };
    
    static {
    
    }
    
    public static Type tryParse(char id) {
      for (int x = 0; x < values.length; x++) {
        if (values[x].getID() == id)
          return values[x]; 
      } 
      return STRING;
    }
    
    public char getID() {
      return name().charAt(0);
    }
  }
  
  private boolean changed = false;
  
  public MoCProperty() {
    wasRead = false;
    type = null;
    isList = false;
  }
  
  public MoCProperty(String name, String value, Type type) {
    this(name, value, type, false);
  }
  
  public MoCProperty(String name, String value, Type type, String comment) {
    this(name, value, type, false);
    this.comment = comment;
  }
  
  MoCProperty(String name, String value, Type type, boolean read) {
    setName(name);
    this.value = value;
    this.type = type;
    wasRead = read;
    isList = false;
  }
  
  public MoCProperty(String name, List<String> values, Type type) {
    this(name, values, type, false);
  }
  
  public MoCProperty(String name, List<String> values, Type type, String comment) {
    this(name, values, type, false);
    this.comment = comment;
  }
  
  MoCProperty(String name, List<String> values, Type type, boolean read) {
    setName(name);
    this.type = type;
    valueList = values;
    wasRead = read;
    isList = true;
  }
  
  public String getString() {
    return value;
  }
  
  public int getInt() {
    return getInt(-1);
  }
  
  public int getInt(int _default) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return _default;
    } 
  }
  
  public boolean isIntValue() {
    try {
      Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    } 
  }
  
  public boolean getBoolean(boolean _default) {
    if (isBooleanValue())
      return Boolean.parseBoolean(value); 
    return _default;
  }
  
  public boolean isBooleanValue() {
    return ("true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase()));
  }
  
  public boolean isDoubleValue() {
    try {
      Double.parseDouble(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    } 
  }
  
  public double getDouble(double _default) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return _default;
    } 
  }
  
  public List<String> getStringList() {
    return valueList;
  }
  
  public int[] getIntList() {
    ArrayList<Integer> nums = new ArrayList<Integer>();
    for (String value : valueList) {
      try {
        nums.add(Integer.valueOf(Integer.parseInt(value)));
      } catch (NumberFormatException e) {}
    } 
    int[] primitives = new int[nums.size()];
    for (int i = 0; i < nums.size(); i++)
      primitives[i] = nums.get(i).intValue(); 
    return primitives;
  }
  
  public boolean isIntList() {
    for (String value : valueList) {
      try {
        Integer.parseInt(value);
      } catch (NumberFormatException e) {
        return false;
      } 
    } 
    return true;
  }
  
  public boolean[] getBooleanList() {
    ArrayList<Boolean> tmp = new ArrayList<Boolean>();
    for (String value : valueList) {
      try {
        tmp.add(Boolean.valueOf(Boolean.parseBoolean(value)));
      } catch (NumberFormatException e) {}
    } 
    boolean[] primitives = new boolean[tmp.size()];
    for (int i = 0; i < tmp.size(); i++)
      primitives[i] = tmp.get(i).booleanValue(); 
    return primitives;
  }
  
  public boolean isBooleanList() {
    for (String value : valueList) {
      if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value))
        return false; 
    } 
    return true;
  }
  
  public double[] getDoubleList() {
    ArrayList<Double> tmp = new ArrayList<Double>();
    for (String value : valueList) {
      try {
        tmp.add(Double.valueOf(Double.parseDouble(value)));
      } catch (NumberFormatException e) {}
    } 
    double[] primitives = new double[tmp.size()];
    for (int i = 0; i < tmp.size(); i++)
      primitives[i] = tmp.get(i).doubleValue(); 
    return primitives;
  }
  
  public boolean isDoubleList() {
    for (String value : valueList) {
      try {
        Double.parseDouble(value);
      } catch (NumberFormatException e) {
        return false;
      } 
    } 
    return true;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setValueList(List<String> list) {
    valueList = list;
  }
  
  public boolean wasRead() {
    return wasRead;
  }
  
  public Type getType() {
    return type;
  }
  
  public boolean isList() {
    return isList;
  }
  
  public boolean hasChanged() {
    return changed;
  }
  
  void resetChangedState() {
    changed = false;
  }
  
  public void set(String value) {
    this.value = value;
    changed = true;
  }
  
  public void set(List<String> values) {
    valueList = values;
    changed = true;
  }
  
  public void set(int value) {
    set(Integer.toString(value));
  }
  
  public void set(boolean value) {
    set(Boolean.toString(value));
  }
  
  public void set(double value) {
    set(Double.toString(value));
  }
}
