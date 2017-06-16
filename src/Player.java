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



    try {

      Sequence midi = MidiSystem.getSequence(new File("roundabout.mid"));
      MidiParser reader = new MidiParser(midi);


        Sequencer seq = null;
        seq = MidiSystem.getSequencer();
        seq.open();
        seq.setSequence(midi);
        int tempo = (int) (seq.getTempoInMPQ()/midi.getResolution());

      ArrayList<Note> notes = reader.translateMidiToNotes();
      FileWriter writer = new FileWriter("roundabout.txt");
        writer.append("tempo " + tempo+ "\n");
        for(Note note : notes){
          writer.append(note.toString());

        }
        writer.close();
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
  }
}
