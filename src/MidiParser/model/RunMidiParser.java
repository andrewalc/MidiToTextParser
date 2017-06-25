package MidiParser.model;

import java.net.URISyntaxException;

import MidiParser.view.MidiParserView;


/**
 * Main method for running a MidiParser.
 */
public class RunMidiParser {
  public static void main(String[] args) {
    MidiParserView view = null;
    try {
      view = new MidiParserView();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    view.initialize();
  }
}
