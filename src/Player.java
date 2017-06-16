import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

/**
 * Created by Andrew Alcala on 6/15/2017.
 */
public class Player {
  public static void main(String[] args) {


    try {
      String fileName = "MrBlueSky.mid";
      MidiParser parser = new MidiParser(fileName);
      parser.writeMidiTextFile();

    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
  }
}
