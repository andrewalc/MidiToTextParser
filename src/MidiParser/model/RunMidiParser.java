package MidiParser.model;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;


/**
 * Main method for running a MidiParser.
 */
public class RunMidiParser {
  public static void main(String[] args){
    if (args.length != 1) {
      throw new IllegalArgumentException("Must give the midi file name:\n"
          + "(ex.: \"smoke.mid\")\n");
    }
    String midiFile = args[0];
    try {
      MidiParser parser = new MidiParser(midiFile);
      parser.writeMidiTextFile();
      System.exit(0);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
  }
}
