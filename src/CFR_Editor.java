import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

public class CFR_Editor {

    private final Map<String, Map<String, Map<String, String>>> commitHistory = new HashMap<>();
    private final File f;
    private Map<String, Map<String, String>> temporary_containers = null;
    private String selectedContainer = null;

    /**
     * Constructor for CFR_Editor.
     * A CFR follows a certain format:
     * <pre>
     *     Container1 {
     *         Property1 : Value1;
     *         Property2 : Value1;
     *     }
     *
     *     Container2 {
     *         Property1 : Value8;
     *     }
     * </pre>
     *
     * @param f the CFR file to be edited
     */
    public CFR_Editor(@NotNull File f) {
        this.f = f;

        if(CFR.parseCFR(f) == CFR.RESPONSE_STATUS.SUCCESS)
            this.temporary_containers = CFR.getContainers();
    }

    /**
     * Retrieves the name of the currently selected container.
     *
     * @return the name of the selected container
     */
    public String getSelectedContainer() {
        return selectedContainer;
    }

    /**
     * Sets the selected container for editing.
     *
     * @param container the name of the container to select
     * @return the CFR_Editor instance for method chaining
     */
    public CFR_Editor setSelectedContainer(String container) {
        selectedContainer = container;
        return this;
    }


    // ============================ CFR FILE OPERATIONS ==================================//


    /**
     * Adds new containers to the CFR file.
     *
     * @param containers the names of the containers to add
     * @return the CFR_Editor instance for method chaining
     */
    public CFR_Editor addContainer        (String ... containers) {
        for(String s : containers) {
            temporary_containers.put(s, new HashMap<>());
        }
        return this;
    }

    /**
     * Adds empty properties to the selected container.
     *
     * @param properties the names of the properties to add
     * @return the CFR_Editor instance for method chaining
     * @throws NullPointerException if no container is selected
     */
    public CFR_Editor addEmptyProperty    (String ... properties) {
        if(selectedContainer == null)
            throw new NullPointerException("No Container selected...");

        for(String p : properties) {
            temporary_containers.get(selectedContainer).put(p, "");
        }
        return this;
    }

    /**
     * Adds a property with a specified value to the selected container.
     *
     * @param property the name of the property to add
     * @param value the value of the property
     * @return the CFR_Editor instance for method chaining
     * @throws NullPointerException if no container is selected
     */
    public CFR_Editor addProperty         (String property, String value) {
        if(selectedContainer == null)
            throw new NullPointerException("No Container selected...");

        temporary_containers.get(selectedContainer).put(property, value);
        return this;
    }

    /**
     * Sets a new value for an existing property in the selected container.
     *
     * @param property the name of the property to update
     * @param newValue the new value for the property
     * @return the CFR_Editor instance for method chaining
     * @throws NullPointerException if no container is selected
     * @throws PropertyNotFoundException if the property is not found in the selected container
     */
    public CFR_Editor setPropertyValue    (String property, String newValue) {
        if(selectedContainer == null)
            throw new NullPointerException("No Container selected...");
        if(!temporary_containers.get(selectedContainer).containsKey(property))
            throw new PropertyNotFoundException("Property not found in container: " + selectedContainer);

        for(Map.Entry<String, String> entry : temporary_containers.get(selectedContainer).entrySet()) {
            if(entry.getKey().equals(property)) {
                entry.setValue(newValue);
                break;
            }
        }
        return this;
    }

    /**
     * Removes a property from the selected container.
     *
     * @param property the name of the property to remove
     * @return the CFR_Editor instance for method chaining
     * @throws NullPointerException if no container is selected
     * @throws PropertyNotFoundException if the property is not found in the selected container
     */
    public CFR_Editor removeProperty      (String property) {
        if(selectedContainer == null)
            throw new NullPointerException("No Container selected...");
        if(!temporary_containers.get(selectedContainer).containsKey(property))
            throw new PropertyNotFoundException("Property not found in container: " + selectedContainer);

        temporary_containers.get(selectedContainer).remove(property);
        return this;
    }

    /**
     * Removes a container from the CFR file.
     *
     * @param container the name of the container to remove
     * @return the CFR_Editor instance for method chaining
     */
    public CFR_Editor removeContainer     (String container) {
        return this;
    }

    /**
     * Commits the changes made to the CFR file, writing them to disk.
     * Commits are saved in the commit history, this can be access through <code style="color:#ebc240;">rollback()</code>
     *
     * @param commitName the name that will be labeled in the commit history.
     * @return <p>a Response code depending on the methods result:</p>
     *              <code style="color:#ebc240;">FAILED, SUCCESS, ERROR</code>
     * @throws InvalidKeyException if the given commit name exists in the commit history. When the commit name given already exists, the changes will not be saved.
     */
    public CFR.RESPONSE_STATUS commit(String commitName)  {
        if(temporary_containers == null)
            return CFR.RESPONSE_STATUS.FAILED;

        if(!commitHistory.containsKey(commitName)) {
            commitHistory.put(commitName, new HashMap<>(temporary_containers));
            try {
                updateFileContents();
            } catch (IOException e) {
                return CFR.RESPONSE_STATUS.ERROR;
            }

            // Update the variable 'containers' in CFR static class
            CFR.parseCFR(f);
            return CFR.RESPONSE_STATUS.SUCCESS;
        }
        else try {
            throw new InvalidKeyException("Commit: " + commitName + " Already  exists.");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Rolls back to a previous commit based on the commit name and updates the file.
     * @param commitTarget the name of the commit to roll back to
     * @return <p>a Response code depending on the methods result:</p>
     *              <code style="color:#ebc240;">FAILED, SUCCESS, ERROR</code>
     * @throws InvalidKeyException when the given commit name is not found in the commit history.
     */
    public CFR.RESPONSE_STATUS rollback(String commitTarget) {
        if(temporary_containers == null)
            return CFR.RESPONSE_STATUS.FAILED;
        if(commitHistory.isEmpty())
            return CFR.RESPONSE_STATUS.FAILED;

        if(commitHistory.containsKey(commitTarget)){
            temporary_containers = new HashMap<>(commitHistory.get(commitTarget));

            try {
                updateFileContents();
            }catch (IOException e) {
                return CFR.RESPONSE_STATUS.ERROR;
            }

            CFR.parseCFR(f);
            return CFR.RESPONSE_STATUS.SUCCESS;
        }
        else try {
            throw new InvalidKeyException("Commit: " + commitTarget + " doesn't  exists.");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFileContents() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));

        for(String container : temporary_containers.keySet()) {
            writer.write(container + " {");
            writer.newLine();
            for(Map.Entry<String, String> property : temporary_containers.get(container).entrySet()) {
                writer.write("    " + property.getKey() + " : " + property.getValue() + ";");
                writer.newLine();
            }
            writer.write("}");
            writer.newLine();
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
}
