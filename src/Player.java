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
  public static void main(String[] args){
    if (args.length != 2) {
      throw new IllegalArgumentException("Must give two arguments:\n"
          + "1) the name of the midi file (ex.: \"smoke.mid\")\n"
          + "2) the name of the new text file (ex.: \"smoke.txt\")");
    }
    String midiFile = args[0];
    String textFile = args[1];
    try {
      Sequence midi = MidiSystem.getSequence(new File(midiFile));
      MidiParser reader = new MidiParser(midi);
      Sequencer seq = MidiSystem.getSequencer();
      seq.open();
      seq.setSequence(midi);
      int tempo = (int) (seq.getTempoInMPQ() / midi.getResolution());
      ArrayList<Note> notes = reader.translateMidiToNotes();
      FileWriter writer = new FileWriter(textFile);
      writer.append("tempo " + tempo + "\n");
      for(Note note : notes){
        writer.append(note.toString() + "\n");
      }
      writer.close();
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
