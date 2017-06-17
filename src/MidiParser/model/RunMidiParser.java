package MidiParser.model;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import model.MidiParser;

/**
 * Created by Andrew Alcala on 6/15/2017.
 */
public class Player {
  public static void main(String[] args){
//    if (args.length != 2) {
//      throw new IllegalArgumentException("Must give two arguments:\n"
//          + "1) the name of the midi file (ex.: \"smoke.mid\")\n"
//          + "2) the name of the new text file (ex.: \"smoke.txt\")");
//    }
//    String midiFile = args[0];
//    String textFile = args[1];
    try {
      MidiParser parser = new MidiParser("luigismansion.mid");
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
