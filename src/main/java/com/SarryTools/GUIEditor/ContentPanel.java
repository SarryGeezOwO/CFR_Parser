package com.SarryTools.GUIEditor;

import com.SarryTools.CFR;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class ContentPanel extends JPanel {

    JTextField nameField;

    public ContentPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        Font ff = new Font(FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 16);
        UIManager.put("Tree.paintLines", true);
        UIManager.put("Tree.hash", new Color(82, 88, 130));
        UIManager.put("Component.arrowType", "triangle");

        CFR.parseCFR(new File("Sample.cfr"));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(CFR.getParsedCFRName() + " (CFR file)");

        // Containers
        for(String s : CFR.getContainersAsList()) {
            ContainerNode container = new ContainerNode(s, CFR.getProperties(s));
            for(Map.Entry<String, String> entry : CFR.getProperties(s).entrySet()) {
                String str = String.format("%-15s : %s", entry.getKey(), entry.getValue());
                PropertyNode property = new PropertyNode(str, entry.getKey(), entry.getValue());
                container.add(property);
            }
            root.add(container);
        }

        JTree tree = new JTree(root);
        tree.setFont(ff);
        tree.setOpaque(false);
        tree.putClientProperty(FlatClientProperties.STYLE,
                "selectionArc: 10;" +
                "selectionBackground: #454552;" +
                "selectionInactiveBackground: #454552;" +
                "border: 10, 10, 10, 10;" +
                "rowHeight:25");

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            String name = node.toString();

            if(node instanceof PropertyNode) {
                remove(1);
                add(editPanelProperty((PropertyNode) node), BorderLayout.EAST);
                revalidate();
            }else if(node instanceof ContainerNode) {
                remove(1);
                add(editPanelContainer((ContainerNode) node), BorderLayout.EAST);
                revalidate();
            }else {
                remove(1);
                add(new JPanel(), BorderLayout.EAST);
                revalidate();
            }
            revalidate();
        });
        expandAllNodes(tree, 0, tree.getRowCount());

        add(tree,                    BorderLayout.CENTER);
        add(new JPanel(),            BorderLayout.EAST);
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
        p.setLayout(new MigLayout("fillx, gap 2, insets 10"));
        p.setPreferredSize(new Dimension(400, 0));
        p.putClientProperty(FlatClientProperties.STYLE,
                "background:darken(@background, 5%);" +
                "arc:10");

        JLabel paneLabel = new JLabel("────────────────── CONTAINER ──────────────────");

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField();
        nameField.setText(node.getName());

        p.add(paneLabel, "span, grow, wrap 30");
        p.add(nameLabel, "split, width 60:60:60");
        p.add(nameField, "grow, wrap");
        return p;
    }

    public JPanel editPanelProperty(PropertyNode node) {
        JPanel p = new JPanel();
        p.setLayout(new MigLayout("fillx, gap 6 2, insets 10"));
        p.setPreferredSize(new Dimension(400, 0));
        p.putClientProperty(FlatClientProperties.STYLE,
                "background:darken(@background, 5%);" +
                        "arc:10");

        JLabel paneLabel = new JLabel("─────────────────── PROPERTY ──────────────────");

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField();
        nameField.setText(node.getName());


        JLabel valueLabel = new JLabel("Value");
        JTextField valueField = new JTextField();
        valueField.setText(node.getValue());

        p.add(paneLabel, "span, grow, wrap 30");
        p.add(nameLabel, "split, width 60:60:60");
        p.add(nameField, "grow, wrap");
        p.add(valueLabel, "split, width 60:60:60");
        p.add(valueField, "grow, wrap");
        return p;
    }

}
