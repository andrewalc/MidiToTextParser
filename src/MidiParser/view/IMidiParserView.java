package MidiParser.view;

import java.io.File;

/**
 * Interface for a MidiParserView
 */
public interface IMidiParserView {
  /**
   * Initializes the view for the MidiParser.
   */
  void initialize();

  File getSelectedFile();
}
