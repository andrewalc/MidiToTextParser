package MidiParser.model;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import MidiParser.view.IMidiParserView;
import MidiParser.view.MidiParserView;


/**
 * Main method for running a MidiParser.
 */
public class RunMidiParser {
  public static void main(String[] args) {
//    if (args.length != 1) {
//      throw new IllegalArgumentException("Must give the midi file name:\n"
//              + "(ex.: \"smoke.mid\")\n");
//    }
//    String midiFile = args[0];


    IMidiParserView view = new MidiParserView();
    view.initialize();
    File midiFile = view.getSelectedFile();
    try {
      MidiParser parser = new MidiParser(midiFile);
      parser.writeMidiTextFile();
      System.exit(0);
    } catch (InvalidMidiDataException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (MidiUnavailableException e) {
      System.out.println(e.getMessage());
    }
  }
}
