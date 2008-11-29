package ibis.www.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Generator for the ibis website.
 * @author Niels Drost
 *
 */
public class Generator {
    
    private final TypedProperties properties;
    
    
    
    private Generator(TypedProperties properties) {
        this.properties = properties;
        
        List<String> topPages = getElementList("");
        
        for(String page: topPages) {
            
            
            
            
            
        }
        
    
        
        
    }
    
    private List<String> getElementList(String prefix) {
        List<String> result = new ArrayList<String>();

        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
                // add part of key before the first period to the result
                result.add(key.split("\\.")[0]);
            }
        }

        return result;
    }


    /**
     * Main function. Expects a property file with sub pages.
     * 
     * @param arguments arguments of the application.
     */
    public static void main(String[] arguments) {
        if (arguments.length != 1) {
            System.err.println("Generator expects a property file with subpages as the single argument");
            System.exit(1);
        }
        
        TypedProperties properties = new TypedProperties();
        properties.loadFromFile(arguments[0]);
        
        if (properties.size() == 0) {
            System.err.println("Error on loading subpage description file: " + arguments[0]);
            System.exit(1);
        }

        new Generator(properties);
        
        
        
    }

}
