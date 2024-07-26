import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CFR {

    private static final Map<String, Map<String, String>> containers = new HashMap<>();
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

        try {
            BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
            String line;

            String currentContainer = null;
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if(line.endsWith("{")) {
                    currentContainer = line.substring(0, line.length()-1).trim();
                    containers.put(currentContainer, new HashMap<>());
                }
                else if(line.endsWith("}")) {
                    currentContainer = null;
                }
                else if(currentContainer != null) {
                    String[] split  = line.split(":");
                    String property = split[0].trim();
                    String value    = split[1].replace(";", "").trim();
                    containers.get(currentContainer).put(property, value);
                }
            }
            reader.close();
        } catch (IOException e) {
            return RESPONSE_STATUS.ERROR;
        }

        return RESPONSE_STATUS.SUCCESS;
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

    public static void main(String[] args) {
        // Completed Task (7 / 26 / 2024)
        //    --> Runs code everytime a commit is done or rollback
        //    > addStateUpdateListener(StateUpdateListener)
        //    > StateUpdateListener --> Interface class
        //    > onStateUpdate()     --> Interface method
    }
}