package com.SarryTools.GUIEditor;

import com.SarryTools.CFR;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class ContentPanel extends JPanel {

    JTextField nameField;
    JTree tree;

    public ContentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        UIManager.put("Tree.paintLines", true);
        UIManager.put("Tree.hash", new Color(82, 88, 130));
        UIManager.put("Component.arrowType", "triangle");

        readCFR(null);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if(node instanceof PropertyNode) {
                remove(2);
                add(editPanelProperty((PropertyNode) node), BorderLayout.EAST);
                revalidate();
            }else if(node instanceof ContainerNode) {
                remove(2);
                add(editPanelContainer((ContainerNode) node), BorderLayout.EAST);
                revalidate();
            }else {
                remove(2);
                add(new JPanel(), BorderLayout.EAST);
                revalidate();
            }
            revalidate();
        });

        JButton changeCFR = new JButton("Open a CFR file");
        changeCFR.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int i = chooser.showOpenDialog(null);
            if(i == JFileChooser.APPROVE_OPTION) {

                // TODO : check file extension and display a Popup if not a cfr file

                System.out.println(chooser.getSelectedFile().getName());
                readCFR(chooser.getSelectedFile().getAbsolutePath());
            }
        });


        add(tree,                    BorderLayout.CENTER);
        add(changeCFR,               BorderLayout.NORTH);
        add(new JPanel(),            BorderLayout.EAST);
    }

    public void readCFR(String filePath) {
        if(filePath == null) {
            tree = new JTree();
            tree.setOpaque(false);
            tree.putClientProperty(FlatClientProperties.STYLE,
                    "selectionArc: 10;" +
                    "selectionBackground: #454552;" +
                    "selectionInactiveBackground: #454552;" +
                    "border: 10, 10, 10, 10;" +
                    "rowHeight:25");
            expandAllNodes(tree, 0, tree.getRowCount());
            revalidate();
            return;
        }

        CFR.parseCFR(new File(filePath));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(CFR.getParsedCFRName() + " (CFR file)");

        // Containers
        for(String s : CFR.getContainersAsList()) {
            ContainerNode container = new ContainerNode(s, CFR.getProperties(s));

            // Properties
            for(Map.Entry<String, String> entry : CFR.getProperties(s).entrySet()) {
                String str = String.format("%-15s : %s", entry.getKey(), entry.getValue());
                PropertyNode property = new PropertyNode(str, entry.getKey(), entry.getValue());
                container.add(property);
            }
            root.add(container);
        }
        ((DefaultTreeModel)tree.getModel()).setRoot(root);
        expandAllNodes(tree, 0, tree.getRowCount());
        revalidate();
    }

    public static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public JPanel editPanelContainer(ContainerNode node) {
        JPanel p = new JPanel();
        p.setLayout(new MigLayout("fillx, gap 6, insets 10"));
        p.setPreferredSize(new Dimension(400, 0));
        p.putClientProperty(FlatClientProperties.STYLE,
                "background:darken(@background, 5%);" +
                "arc:10");

        JLabel paneLabel = new JLabel("─────────────── CONTAINER ───────────────");

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField();
        nameField.setText(node.getName());

        JLabel propertyCountLabel = new JLabel("Properties: " + node.getProperties().size());
        JTextPane keyDisplay = new JTextPane();
        keyDisplay.setEditable(false);
        StringBuilder keyBuilder = new StringBuilder();

        for(Map.Entry<String, String> prop : CFR.getProperties(node.getName()).entrySet()) {
            keyBuilder.append(String.format("%-15s :    %s", prop.getKey(), prop.getValue())).append("\n");
        }
        keyDisplay.setText(keyBuilder.toString());


        JButton save = new JButton("Commit");

        p.add(paneLabel, "span, grow, wrap 30");
        p.add(nameLabel, "split, width 60:60:60");
        p.add(nameField, "grow, wrap");
        p.add(new JLabel("─────────────────────────────────────────"), "wrap");
        p.add(propertyCountLabel, "span, grow, wrap");
        p.add(keyDisplay,       "height 200:200:200, span, grow");
        p.add(save, "span, alignx trailing");
        return p;
    }

    public JPanel editPanelProperty(PropertyNode node) {
        JPanel p = new JPanel();
        p.setLayout(new MigLayout("fillx, gap 6, insets 10"));
        p.setPreferredSize(new Dimension(400, 0));
        p.putClientProperty(FlatClientProperties.STYLE,
                "background:darken(@background, 5%);" +
                "arc:10");

        JLabel paneLabel = new JLabel("─────────────── PROPERTY ────────────────");

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField();
        nameField.setText(node.getName());


        JLabel valueLabel = new JLabel("Value");
        JTextField valueField = new JTextField();
        valueField.setText(node.getValue());

        JButton save = new JButton("Commit");
        save.putClientProperty(FlatClientProperties.STYLE,
                "border:5, 5, 5, 5");

        p.add(paneLabel, "span, grow, wrap 30");
        p.add(nameLabel, "split, width 60:60:60");
        p.add(nameField, "grow, wrap");
        p.add(valueLabel, "split, width 60:60:60");
        p.add(valueField, "grow, wrap");
        p.add(save, "span, alignx trailing");
        return p;
    }

}
