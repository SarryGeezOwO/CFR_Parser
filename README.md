# CFR & CFR_Editor

## Overview

**CFR** (Configuration For Rascals) and **CFR_Editor** are Java libraries designed for parsing, reading, and modifying custom CFR configuration files. These tools provide an easy-to-use API for managing configuration data, including functionality for committing and rolling back changes.

## Features

- **CFR**
  - Parse CFR files into a structured format.
  - Retrieve properties and containers from parsed data.
  - Static methods for ease of use.

- **CFR_Editor**
  - Modify CFR files directly.
  - Add, update, and remove containers and properties.
  - Commit and rollback changes with state update listeners.
  - Maintain commit history for changes.

### Prerequisites
- Java 8 or higher
- Maven

## Usage

#### Parsing CFR Files
<pre>
<code>
import com.SarryTools.CFR;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("path/to/config.cfr");
        CFR.RESPONSE_STATUS status = CFR.parseCFR(file);
        
        if (status == CFR.RESPONSE_STATUS.SUCCESS) {
            System.out.println("Parsing successful!");
            System.out.println(CFR.getContainers());
        } else {
            System.out.println("Parsing failed!");
        }
    }
}
</code>
</pre>

#### Modifying CFR files
<pre>
<code>
import com.SarryTools.CFR_Editor;
import com.SarryTools.CFR;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("path/to/config.cfr");
        CFR_Editor editor = new CFR_Editor(file);
        
        editor.addContainer("NewContainer")
              .setSelectedContainer("NewContainer")
              .addProperty("NewProperty", "NewValue")
              .commit("InitialCommit");
        
        System.out.println(CFR.getContainers());
    }
}

</code>
</pre>

## Contributions 
Contributions are welcome! Please open an issue or submit a pull request for any changes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


