package com.SarryTools.GUIEditor;

import javax.swing.tree.DefaultMutableTreeNode;

public class PropertyNode extends DefaultMutableTreeNode {

    private String name, value;

    public PropertyNode(String display, String name, String value) {
        super(display);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
