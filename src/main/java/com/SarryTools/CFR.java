package com.SarryTools;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

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
    private static boolean isCFRParsed = false;
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

                if(line.startsWith("##")) {
                    commentLines.put(lineNum, line);
                    lineNum++;
                    continue;
                }

                if(line.contains("{}")) {
                    validateNoInlineComment(line, "Container");
                    containers.put(line.substring(0, line.length()-2).trim(), new HashMap<>());
                    currentContainer = null;
                }
                else if(line.contains("{")) {
                    validateNoInlineComment(line, "Container");
                    if(line.endsWith("{")) {
                        currentContainer = line.substring(0, line.length()-1).trim();
                        containers.put(currentContainer, new HashMap<>());
                    }
                }
                else if(line.contains("}")) {
                    validateNoInlineComment(line, "closing container symbol");
                    if(line.endsWith("}"))
                        currentContainer = null;
                }
                else if(currentContainer != null && !line.isEmpty()) {
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

        isCFRParsed = true;
        return RESPONSE_STATUS.SUCCESS;
    }

    private static void validateNoInlineComment(@NotNull String line, String context) throws CFRInvalidSyntaxException {
        if (line.contains("##")) {
            throw new CFRInvalidSyntaxException("Comment is not allowed on the same line as a " + context);
        }
    }

    /**
     * Loops through all the containers and searches for the target property,
     * if the container does own that property the container will be added to the list
     *
     * @param property the name of the property
     * @return a <code style="color:#ebc240;">List&lt;String&gt;</code> representing all the containers that owns the target property
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static List<String> searchProperty(String property) {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to search for properties");

        List<String> arr = new ArrayList<>();
        for(String container : getContainers().keySet()) {
            if(getContainers().get(container).containsKey(property)) {
                arr.add(container);
            }
        }
        return arr;
    }

    /**
     * Loops through all the containers and searches for the target property ignoring case sensitivity,
     * if the container does own that property the container will be added to the list
     *
     * @param property the name of the property
     * @return a <code style="color:#ebc240;">List&lt;String&gt;</code> representing all the containers that owns the target property
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static List<String> searchPropertyIgnoreCase(String property) {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to search for properties");

        List<String> arr = new ArrayList<>();
        for(String container : getContainers().keySet()) {
            for(String prop : getPropertiesAsList(container)) {
                if(prop.equalsIgnoreCase(property)) {
                    arr.add(container);
                    break;
                }
            }
        }
        return arr;
    }

    /**
     * @return all the containers found in the parsed CFR file, as an array of String
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static String[] getContainersAsList() {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to retrieve Containers");

        if(getContainers().keySet().isEmpty()) {
            return new String[0];
        }
        return getContainers().keySet().toArray(new String[0]);
    }

    /**
     *
     * @param container the name of the container
     * @return all the properties found in the specified Container but as an array of String
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static String[] getPropertiesAsList(String container) {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to retrieve Properties");

        if(getContainers().keySet().isEmpty()) {
            return new String[0];
        }
        return getProperties(container).keySet().toArray(new String[0]);
    }

    /**
     * Retrieves a property from a specified container.
     *
     * @param container the name of the container
     * @param property the property to retrieve
     * @return the property and its value as a Map.Entry
     * @throws CFRPropertyNotFoundException if the property is not found in the specified container
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static Map.Entry<String, String> getProperty(@NotNull String container, @NotNull String property) {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to retrieve properties");

        for(Map.Entry<String, String> entry : getProperties(container).entrySet()) {
            if(property.equals(entry.getKey()))
                return entry;
        }
        throw new CFRPropertyNotFoundException("Property not found!");
    }

    /**
     * Retrieves all properties of a specified container.
     *
     * @param container the name of the container
     * @return a Map of properties and their values for the specified container
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static Map<String, String> getProperties(@NotNull String container) {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to retrieve properties");
        return containers.get(container);
    }

    /**
     * Retrieves all containers and their properties.
     *
     * @return a Map of container names to their respective properties and values
     * @throws CFRNotFoundException if no CFR has been parsed yet
     */
    public static Map<String, Map<String, String>> getContainers() {
        if(!isCFRParsed) throw new CFRNotFoundException("CFR is missing, unable to retrieve Containers");
        return containers;
    }

    protected static HashMap<Integer, String> getCommentLines() {
        return commentLines;
    }

    // Completed Task (7 / 26 / 2024)
    //    --> Runs code everytime a commit is done or rollback
    //    > addStateUpdateListener(StateUpdateListener)
    //    > StateUpdateListener --> Interface class
    //    > onStateUpdate()     --> Interface method

    // TODO : (8 / 7 / 2024)
    //      --> Implement Functions:
    //          [ ]> validateCFR()                 // Syntax Checking
    //          [/]> searchProperty()              // All Containers
    //          [/]> searchPropertyIgnoreCase()    // Ignore Capitalization
    //          [/]> getContainersAsList()         // Returns a String[]
    //          [/]> getPropertiesAsList()         // Returns a String[]
    //      --> Implement Function Editor
    //          [ ]> mergeContainers(target, ... toMerge)
    //          [ ]> renameContainer()
    //          [ ]> renameProperty()              // Based on SelectedContainer

    public static void main(String[] args) {
        CFR.parseCFR(new File("Sample.cfr"));
        for(String s : CFR.getPropertiesAsList("GameObject")) {
            System.out.println(s);
        }
        System.out.println(CFR.getPropertiesAsList("GameObject").length);
        System.out.println(searchPropertyIgnoreCase("PoSitIon"));
    }
}