package MidiParser.view;

import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * JPanel with a File Selector. Used for selecting the .mid or .midi file to convert to at txt.
 */
public class FileSelectorPanel extends JPanel{

  File selectedFile;

  public FileSelectorPanel(){
    JFileChooser jFileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Midi Files", "mid", "midi");
    jFileChooser.setFileFilter(filter);

    int returnVal = jFileChooser.showOpenDialog(this);
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      System.out.println("You chose to open this file: " +
              jFileChooser.getSelectedFile());
    }
    selectedFile =jFileChooser.getSelectedFile();
  }

  public File getSelectedFile() {
    return selectedFile;
  }
}
