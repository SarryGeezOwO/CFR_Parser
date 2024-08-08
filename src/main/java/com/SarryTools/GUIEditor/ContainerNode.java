package com.SarryTools.GUIEditor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.Map;

public class ContainerNode extends DefaultMutableTreeNode {

    private String name;
    private Map<String, String> properties = new HashMap<>();

    public ContainerNode(String name, Map<String, String> properties) {
        super(name);
        this.name = name;
        this.properties = properties;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
