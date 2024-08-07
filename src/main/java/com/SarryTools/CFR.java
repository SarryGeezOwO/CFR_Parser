package com.SarryTools;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author SarryGeezOwO
 * <hr>
 * <p>CFR is a class with only static Methods</p>
 * <br>
 * <p>This class is used only for CFR file parsing and CFR data Reading</p>
 *
 */
public class CFR {

    private static final Map<String, Map<String, String>> containers = new HashMap<>();
    private static final HashMap<Integer, String> commentLines = new HashMap<>();
    public static enum RESPONSE_STATUS {
        FAILED, SUCCESS, ERROR
    }

    /**
     * Parses a given CFR file, and transforms the file contents into a <code style="color:#ebc240;">Map</code> object :
     * <pre>
     * Map&lt;String, Map&lt;String, String&gt;&gt;
     * <span style="color:GRAY;">--> ContainerName, Map&lt;Property, Value&gt; </span>
     * </pre>
     * @param f a <b style="color:#4d7ae2;">CFR (Configuration For Rascals)</b> file format
     * @return <p>a Response code depending on the methods result:</p>
     *              <code style="color:#ebc240;">FAILED, SUCCESS, ERROR</code>
     */
    public static RESPONSE_STATUS parseCFR(@NotNull File f) {
        if(!f.getName().endsWith(".cfr"))
            return RESPONSE_STATUS.FAILED;

        commentLines.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
            String line;
            int lineNum = 1;

            String currentContainer = null;
            while((line = reader.readLine()) != null) {
                line = line.trim();

                if(line.trim().startsWith("##")) {
                    commentLines.put(lineNum, line);
                    lineNum++;
                    continue;
                }

                if(line.contains("{}")) {
                    validateNoInlineComment(line, "Container");
                    containers.put(line.substring(0, line.length()-2).trim(), new HashMap<>());
                    currentContainer = null;
                }
                else if(line.trim().contains("{")) {
                    validateNoInlineComment(line, "Container");
                    if(line.trim().endsWith("{")) {
                        currentContainer = line.substring(0, line.length()-1).trim();
                        containers.put(currentContainer, new HashMap<>());
                    }
                }
                else if(line.trim().contains("}")) {
                    validateNoInlineComment(line, "closing container symbol");
                    if(line.trim().endsWith("}"))
                        currentContainer = null;
                }
                else if(currentContainer != null && !line.trim().isEmpty()) {
                    validateNoInlineComment(line, "property");
                    String[] split  = line.split(":");
                    String property = split[0].trim();
                    String value    = split[1].replace(";", "").trim();
                    containers.get(currentContainer).put(property, value);
                }
                lineNum++;
            }
            reader.close();
        } catch (IOException e) {
            return RESPONSE_STATUS.ERROR;
        }

        return RESPONSE_STATUS.SUCCESS;
    }

    private static void validateNoInlineComment(String line, String context) throws InvalidSyntaxException {
        if (line.contains("##")) {
            throw new InvalidSyntaxException("Comment is not allowed on the same line as a " + context);
        }
    }

    /**
     * Retrieves a property from a specified container.
     *
     * @param container the name of the container
     * @param property the property to retrieve
     * @return the property and its value as a Map.Entry
     * @throws PropertyNotFoundException if the property is not found in the specified container
     */
    public static Map.Entry<String, String> getProperty(@NotNull String container, @NotNull String property) {
        for(Map.Entry<String, String> entry : getProperties(container).entrySet()) {
            if(property.equals(entry.getKey()))
                return entry;
        }
        throw new PropertyNotFoundException("Property not found!");
    }

    /**
     * Retrieves all properties of a specified container.
     *
     * @param container the name of the container
     * @return a Map of properties and their values for the specified container
     */
    public static Map<String, String> getProperties(@NotNull String container) {
        return containers.get(container);
    }

    /**
     * Retrieves all containers and their properties.
     *
     * @return a Map of container names to their respective properties and values
     */
    public static Map<String, Map<String, String>> getContainers() {
        return containers;
    }

    protected static HashMap<Integer, String> getCommentLines() {
        return commentLines;
    }

    // Completed Task (7 / 26 / 2024)
    //    --> Runs code everytime a commit is done or rollback
    //    > addStateUpdateListener(main.java.com.SarryTools.StateUpdateListener)
    //    > main.java.com.SarryTools.StateUpdateListener --> Interface class
    //    > onStateUpdate()     --> Interface method

    // TODO : (8 / 7 / 2024)
    //      --> Implement Functions:
    //          > validateCFR()                 // Returns a boolean if CFR file is syntax correct
    //          > searchProperty()              // All Containers
    //          > searchPropertyIgnoreCase()    // Ignore Capitalization
    //          > getContainersAsList()         // Returns a String[]
    //          > getPropertiesAsList()         // Returns a String[]
    //      --> Implement Function Editor
    //          > mergeContainers(target, ... toMerge)
    //          > renameContainer()
    //          > renameProperty()              // Based on SelectedContainer

    public static void main(String[] args) {
        CFR_Editor editor = new CFR_Editor(new File("Sample.cfr"));
        editor.setSelectedContainer("Settings")
                .addProperty("Font", "Roboto")
                .commit("Font");
    }
}