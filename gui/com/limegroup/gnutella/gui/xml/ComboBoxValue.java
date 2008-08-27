package com.limegroup.gnutella.gui.xml;

import com.limegroup.gnutella.util.NameValue;

/**
 * An object for use in ComboBoxes so we can display one
 * value, but internally use another.
 */
public class ComboBoxValue implements Comparable {
    
    private final String name;
    private final String value;
    
    public ComboBoxValue(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public ComboBoxValue(NameValue nameValue) {
        this(nameValue.getName(), (String)nameValue.getValue());
    }
    
    public ComboBoxValue(String name) {
        this(name, name);
    }
    
    public ComboBoxValue() {
        this("", "");
    }
    
    public String getValue() {
        return value;
    }
    
    public String toString() {
        return name;
    }
    
    public boolean equals(Object o) {
        if(o == this)
            return true;
        else if(!(o instanceof ComboBoxValue))
            return false;
        ComboBoxValue other = (ComboBoxValue)o;
        return value == null ? other.value == null : value.equals(other.value);
    }
    
    public int compareTo(Object o) {
        if(o == this)
            return 0;
        else if(!(o instanceof ComboBoxValue))
            return -1;
        ComboBoxValue other = (ComboBoxValue)o;
        return value == null ? (other.value == null ? 0 : 1) : value.compareTo(other.value);
    }
}