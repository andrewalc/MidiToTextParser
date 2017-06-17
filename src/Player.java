
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;



public class Player {
  public static void main(String[] args) {


    try {
      String fileName = "AllStar.mid";
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
