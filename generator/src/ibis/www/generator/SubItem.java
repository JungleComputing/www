/**
 * 
 */
package ibis.www.generator;

import java.io.PrintStream;
import java.util.Arrays;

class SubItem {
    private final String name;

    private final String pageFilename;

    private final SubItem[] children;

    SubItem(String name, String prefix, WebsiteProperties properties)
            throws Exception {
        this.name = name;

        pageFilename = properties.getProperty(prefix + "." + name + ".page");

        if (pageFilename == null) {
            throw new Exception("no page name specified for " + name);
        }

        String[] childNames = properties.getStringList(prefix + "." + name
                + ".items");

        children = new SubItem[childNames.length];

        for (int i = 0; i < children.length; i++) {
            // recursive
            children[i] = new SubItem(childNames[i], prefix + "." + name,
                    properties);
        }
    }

    /**
     * @return name of this item
     */
    public String name() {
        return name;
    }

    /**
     * @return target of this item
     */
    public String getPageFilename() {
        return pageFilename;
    }

    // first create own page, then children
    void generatePages(Generator generator, TopItem topItem, int topItemIndex, int[] indexes)
            throws Exception {
        System.err.println("Creating page: " + pageFilename);

        PrintStream out = new PrintStream(pageFilename);

        generator.writeHeader(out);

        generator.writeTopMenu(out, topItemIndex);

        out.println("<tr>");

        topItem.writeSubMenu(out, indexes);

        generator.writeContent(out, pageFilename);

        out.println("</tr>");

        generator.writeFooter(out);

        out.flush();
        out.close();

        for (int child = 0; child < children.length; child++) {
            // add additional index to list
            int[] childIndexes = new int[indexes.length + 1];
            for (int i = 0; i < indexes.length; i++) {
                childIndexes[i] = indexes[i];
            }
            childIndexes[indexes.length] = child;

            children[child].generatePages(generator, topItem, topItemIndex, childIndexes);
        }
    }

    //write this non-active item and its children (not active too)
    void writeSubMenuPassiveItems(PrintStream out, int level) {
        out.println("            <tr>");
        out.println("                <td class=menu>");
        out.println("                    <a href=\"" + pageFilename + "\" class=\"menu level" + level + "\">" + name + "</a>");
        out.println("                </td>");
        out.println("            </tr>");
        
        for(SubItem child: children) {
            child.writeSubMenuPassiveItems(out, level + 1);
        }
    }
    
    //write this active item and its children (some of which may be active too)
    void writeSubMenuActiveItems(PrintStream out, int[] activeItems, int level) {
        out.println("            <tr>");
        out.println("                <td class=menu>");
        out.println("                    <a href=\"" + pageFilename + "\" class=\"menu menu_active level" + level + "\">" + name + "</a>");
        out.println("                </td>");
        out.println("            </tr>");
        
        for(int i = 0; i < children.length; i++) {
            if (activeItems.length > 0 && i == activeItems[0]) {
                //cut of first element of active items
                int[] childActiveItems = new int[activeItems.length - 1];
                for (int j = 0; j < childActiveItems.length; j++) {
                    childActiveItems[j] = activeItems[j + 1];
                }
                
                children[i].writeSubMenuActiveItems(out, childActiveItems, level + 1);
                
            }
            
            children[i].writeSubMenuPassiveItems(out, level + 1);
        }
        
        
    }


}