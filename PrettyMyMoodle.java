import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrettyMyMoodle {

    private static final String OUTPUT_FILE = "./output.txt";

    public static void main(String[] args) {

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
                System.exit(1);
            }
        } while (!outputWriteFile.get());

    }

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

}
