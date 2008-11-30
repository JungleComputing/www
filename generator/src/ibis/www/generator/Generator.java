package ibis.www.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

/**
 * Generator for the ibis website.
 * 
 * @author Niels Drost
 * 
 */
public class Generator {

    // copy file into given stream
    private static void copy(File file, PrintStream out) throws Exception {
        FileInputStream in = new FileInputStream(file);

        byte[] buffer = new byte[100];

        while (true) {
            int read = in.read(buffer);

            if (read == -1) {
                in.close();
                return;
            }

            out.write(buffer, 0, read);
        }
    }

    // items in top menu
    private final TopItem[] items;

    // file used as header
    private final File header;

    // file used as footer
    private final File footer;

    Generator(WebsiteProperties properties, File header, File footer)
            throws Exception {
        this.header = header;
        this.footer = footer;
        String[] itemNames = properties.getStringList("items");

        items = new TopItem[itemNames.length];

        for (int i = 0; i < items.length; i++) {
            items[i] = new TopItem(itemNames[i], properties);
        }
    }

    // Content generator functions

    void writeHeader(PrintStream out) throws Exception {
        copy(header, out);
    }

    void writeContent(PrintStream out, String pageFileName) {
        out.println("    <!-- CONTENT -->");
        out.println("    <td class=main id=content_cell>");
        out.println("        <?php include (\"content/" + pageFileName
                + "\"); ?>");
        out.println("    </td>");
    }

    void writeWhatsNew(PrintStream out) {
        out.println("    <!-- NEWS BOX -->");
        out.println("    <td class=main>");
        out.println("        <table class=news>");
        out
                .println("            <tr class=news><th class=news>What's new?</th></tr>");
        out
                .println("            <?php include (\"content/news-summary.html\"); ?>");
        out.println("        </table>");
        out.println("    </td>");
    }

    void writeFooter(PrintStream out) throws Exception {
        copy(footer, out);
    }

    void writeTopMenu(PrintStream out, int activeItem) {
        out.println("<!-- TOP MENU -->");
        out.println();
        out.println("<tr>");
        out.println("    <td class=main colspan=2>");
        out.println("    <div class=topmenu>");
        out.println("    <table class=topmenu>");
        out.println("    <tr>");

        for (int i = 0; i < items.length; i++) {
            if (i == activeItem) {
                out.println("        <td class=\"topmenu topmenu_active\">");
                out.println("            <a class=\"topmenu topmenu_active\" href=\""
                                + items[i].getTarget()
                                + "\">"
                                + items[i].getName() + "</a>");
                out.println("        </td>");
            } else {
                out.println("        <td class=topmenu>");
                out.println("            <a class=topmenu href=\""
                        + items[i].getTarget() + "\">" + items[i].getName()
                        + "</a>");
                out.println("        </td>");
            }
        }
        out.println("    </tr>");
        out.println("    </table>");
        out.println("    </div>");
        out.println("    </td>");
        out.println("</tr>");
        out.println();
    }

    // generator all pages recursively
    private void generatePages() throws Exception {
        for (int i = 0; i < items.length; i++) {
            items[i].generatePages(i, this);
        }
    }

    /**
     * Main function. Expects a property file with sub pages.
     * 
     * @param arguments
     *            arguments of the application.
     */
    public static void main(String[] arguments) {
        if (arguments.length != 3) {
            System.err.println("Generator SUBPAGE.PROPERTIES HEADER FOOTER");
            System.exit(1);
        }

        WebsiteProperties properties = new WebsiteProperties();
        properties.loadFromFile(arguments[0]);

        if (properties.size() == 0) {
            System.err.println("Error on loading subpage description file: "
                    + arguments[0]);
            System.exit(1);
        }

        File header = new File(arguments[1]);
        File footer = new File(arguments[2]);

        try {

            Generator generator = new Generator(properties, header, footer);
            generator.generatePages();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

    }

}
