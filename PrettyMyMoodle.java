import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Converts a rich formatted text to raw html text and writes it into the clipboard.
 */
@SuppressWarnings("serial")
public class PrettyMyMoodle extends JPanel {

    private static final String OUTPUT_FILE = "./output.txt";
    private static JFrame frame = new JFrame("Pretty my Moodle");
    private JButton sendButton = new JButton("Do your thing!");
    private JLabel label = new JLabel("Copy your code from IntelliJ and click the button:");
    private JPanel mainPanel = new JPanel();

    public PrettyMyMoodle() {
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
        this.mainPanel.add(this.label);
        this.mainPanel.add(this.sendButton);
        this.sendButton.addActionListener(e -> this.buttonAction());
        setLayout(new BorderLayout());
        add(this.mainPanel, BorderLayout.PAGE_END);
    }

    private void buttonAction() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

        Transferable t = cb.getContents(null);
        ArrayList<DataFlavor> dataFlavors = new ArrayList<>(List.of(t.getTransferDataFlavors()));

        AtomicBoolean outputWriteFile = new AtomicBoolean(false);

        do {
            DataFlavor df = getHTMLDataFlavor(dataFlavors);
            if (df != null) {
                outputWriteFile.set(writeClipboardToFile(cb, df));
            }
            else {
                System.out.println("The clipboard content is invalid.");
                label.setText("The clipboard content is invalid. Try a different source: ");
                updateGUI();
                return;
            }
        } while (!outputWriteFile.get()); // TODO: Infinite loop

        // read file
        String htmlContent = readFileToString();

        // fix html
        String htmlFiltered = filterHMTL(htmlContent);

        // write content to clipboard
        writeContentToClipboard(cb, htmlContent);

        // TODO: Test if copy was succeed
        label.setText("Copy was succeed!");
        updateGUI();
    }

    /**
     * Creates and show frame
     */
    public static void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new PrettyMyMoodle());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Update GUI
     */
    private static void updateGUI() {
        frame.validate();
        frame.repaint();
        frame.pack();
    }

    /**
     * Get Data Flavor related to text/html
     * @param dataFlavors
     * @return
     */
    private static DataFlavor getHTMLDataFlavor(ArrayList<DataFlavor> dataFlavors) {

        Iterator<DataFlavor> iterator = dataFlavors.iterator();
        while (iterator.hasNext()) {
            DataFlavor df = iterator.next();
            if (df.getMimeType().contains("text/html") && df.getMimeType().contains("UTF-8")) {
                //System.out.println("Found DataFlavor HTML: " + df.getMimeType());
                iterator.remove();
                return df;
            }
        }
        return null;
    }

    /**
     * Write the clipboard to a file to be read later.
     * @param cb
     * @param df
     * @return
     */
    private static boolean writeClipboardToFile(Clipboard cb, DataFlavor df) {
        try {
            Path file = Paths.get(OUTPUT_FILE);
            Files.write(file, ((byte[]) cb.getData(df)));
        } catch (IOException | UnsupportedFlavorException | ClassCastException err) {
            //System.out.println("Error writing the file!");
            return false;
        }
        return true;
    }

    /**
     * Reads a text file and returns a string.
     * @return
     */
    private static String readFileToString() {
        try{
            Path path = Path.of(OUTPUT_FILE);
            String fileContents = Files.readString(path);
            System.out.println(fileContents);
            return fileContents;
        }
        catch(IOException e){
            System.out.println("The geeks file could not be found or loaded.");
        }
        return "";
    }

    /**
     * TODO
     * @param content
     * @return
     */
    private static String filterHMTL(String content) {
        return content;
    }

    /**
     * Writes a string to the clipboard
     * @param cb
     * @param content
     */
    private static void writeContentToClipboard(Clipboard cb, String content) {
        StringSelection stringSelection = new StringSelection(content);
        cb.setContents(stringSelection, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrettyMyMoodle::createAndShowGUI);
    }

}
