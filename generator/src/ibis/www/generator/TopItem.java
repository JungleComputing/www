/**
 * 
 */
package ibis.www.generator;

import java.io.PrintStream;
import java.util.Arrays;

class TopItem {
    private final String name;

    // for items without a sub menu
    private final String pageFilename;

    private final SubItem[] children;

    TopItem(String name, WebsiteProperties properties) throws Exception {
        this.name = name;

        if (properties.getProperty(name + ".items") == null) {
            // top item without children
            children = null;
            pageFilename = properties.getProperty(name + ".page");

            if (pageFilename == null) {
                throw new Exception("no page or items specified for " + name);
            }
        } else {
            pageFilename = null;

            String[] childNames = properties.getStringList(name + ".items");

            if (childNames == null || childNames.length == 0) {
                throw new Exception(
                        "no children in top menu, but items property specified");
            }

            children = new SubItem[childNames.length];

            for (int i = 0; i < children.length; i++) {
                children[i] = new SubItem(childNames[i], name, properties);
            }
        }
    }

    /**
     * @return name of this item. Also converts name to include spaces instead
     *         of underscores.
     */
    String getName() {
        return name.replace('_', ' ');
    }

    // target of link in top menu
    String getTarget() {
        if (children == null) {
            return pageFilename;
        } else {
            // first item in menu
            return children[0].getTarget();
        }
    }

    void writeSubMenu(PrintStream out, int[] activeItems) {
        // System.err.println("Active items = " + Arrays.toString(activeItems));

        out.println("<!-- SUB MENU -->");
        out.println();
        out.println("    <td class=main>");
        out.println("        <table class=menu>");

        for (int i = 0; i < children.length; i++) {
            if (activeItems.length > 0 && i == activeItems[0]) {
                // cut of first element of active items
                int[] childActiveItems = new int[activeItems.length - 1];
                for (int j = 0; j < childActiveItems.length; j++) {
                    childActiveItems[j] = activeItems[j + 1];
                }
                children[i].writeSubMenuActiveItems(out, childActiveItems, 0);
            } else {
                children[i].writeSubMenuPassiveItems(out, 0);
            }
        }

        out.println("        </table>");
        out.println("    </td>");
        out.println();
    }

    void generatePages(int index, Generator generator) throws Exception {
        if (children == null) {
            System.err.println("Creating page: " + pageFilename);

            PrintStream out = new PrintStream(pageFilename);

            generator.writeHeader(out);

            generator.writeTopMenu(out, index);

            out.println("<tr>");

            if (pageFilename.equals("index.html")) {
                generator.writeContent(out, pageFilename);
                // add what's new box to index page
                generator.writeWhatsNew(out);
            } else if ((pageFilename.equals("downloads.html"))) {
                //downloads page has one big cell
                generator.writeContent(out, pageFilename);
            } else {

                out.println("<!-- EMPTY SUB MENU -->");
                out.println();
                out.println("    <td class=main>");
                out.println("        <table class=menu>");
                out.println("        </table>");
                out.println("    </td>");

                generator.writeContent(out, pageFilename);
            }

            out.println("</tr>");
            generator.writeFooter(out);

            out.flush();
            out.close();

        } else {
            // create all sub pages
            for (int i = 0; i < children.length; i++) {
                int[] childIndexes = new int[1];
                childIndexes[0] = i;

                children[i].generatePages(generator, this, index, childIndexes);
            }
        }

    }

}