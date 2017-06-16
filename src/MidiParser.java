

import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.sound.midi.*;


/**
 * A skeleton for MIDI playback
 */
public class MidiParser {
  private final Sequence midi;
  private String fileName;
  private int tempo;

  public MidiParser(String fileName) throws MidiUnavailableException, IOException,
          InvalidMidiDataException {
    String[] fileNameExt = fileName.split("\\.");
    this.fileName = fileNameExt[0];
    this.midi = MidiSystem.getSequence(new File(fileName));
    Sequencer seq = MidiSystem.getSequencer();
    seq.open();
    seq.setSequence(midi);
    this.tempo = (int) (seq.getTempoInMPQ() / midi.getResolution());

  }

  public void writeMidiTextFile() {
    try {
      ArrayList<Note> notes = this.translateMidiToNotes();
      FileWriter writer = new FileWriter(fileName + ".txt");
      writer.append("tempo " + tempo + "\n");
      int noteCount = 0;
      for (Note note : notes) {
        writer.append(note.toString());
        noteCount++;
      }
      writer.close();
      System.out.println(noteCount + " notes at a tempo of " + tempo + " were exported as "
      + fileName +".txt");
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public ArrayList<Note> translateMidiToNotes() throws InvalidMidiDataException {

    // We will return a list of notes
    ArrayList<Note> notes = new ArrayList<>();

    int trackCount = 0;
    // We will store pitches corresponding to their play ticks in this arraylist
    ArrayList<ArrayList<Integer[]>> trackByPitch = new ArrayList<>();


    for (Track track : midi.getTracks()) {
      // instruments default to 0 if track has no Program Change
      int instrument = 0;
      for (int i = 0; i < track.size(); i++) {
        // set this track's instrument, we only need to look at the first instance of a note, all
        // tracks share the same instrument.
        MidiEvent instrumentEvent = track.get(i);
        if (instrumentEvent.getMessage() instanceof ShortMessage) {
          ShortMessage instrumentMessage = (ShortMessage) instrumentEvent.getMessage();
          int instrumentCommand = instrumentMessage.getCommand();
          // If the ShortMessage is a program change, get the instrument.
          if (instrumentCommand == (int) (ShortMessage.PROGRAM_CHANGE & 0xFF)) {
            instrument = instrumentMessage.getData1();
            break;
          }
        }
      }

      // The current track we are parsing needs a place in our Pitch, Tick arraylist
      trackByPitch.add(new ArrayList<Integer[]>());

      // parse through all messages in this track
      for (int i = 0; i < track.size(); i++) {
        int channel = 0;
        MidiEvent event = track.get(i);
        // 144 = NOTE ON , 128 == NOTE OFF
        // @TODO Figure out why some MIDIs do not have note off.
        //System.out.println(event.getMessage().getStatus());
        if (event.getMessage() instanceof ShortMessage) {
          ShortMessage message = (ShortMessage) event.getMessage();
          // The tick that this message is being fired.
          int timeOccured = (int) event.getTick();
          int command = message.getCommand();


          // If the message we have is a NOTE ON message add the pitch, tick pair to our arraylist.
          if (command == (int) (ShortMessage.NOTE_ON & 0xFF)) {
            int pitch = message.getData1();
            trackByPitch.get(trackCount).add(new Integer[]{pitch, timeOccured});

          } else if (command == (int) (ShortMessage.NOTE_OFF & 0xFF)) {
            int pitch = message.getData1();
            int volume = message.getData2();
            // Some MIDIs have message volumes at 0, 64 is default for those.
            if (volume == 0) {
              volume = 64;
            }
            //In our developing arraylist, we want to get the arraylist that represents this track.
            ArrayList<Integer[]> currentTrack = trackByPitch.get(trackCount);
            // Parse our arraylist and find this NOTE OFF msg's corresponding NOTE ON by looking
            // first NOTE ON that matches this NOTE OFF's pitch.
            for (int j = 0; j < currentTrack.size(); j++) {
              Integer[] messageInTrack = currentTrack.get(j);
              if (messageInTrack[0] == pitch) {
                int startingTimeOccured = messageInTrack[1];
                int startingBeat = startingTimeOccured;
                int endBeat = timeOccured;
                // Percussion channel is 9.
                // If the message is in the percussion channel, we get keep that channel.
                // Current implementation of text Note does not account for channels unfortunately.
//                if(message.getChannel() == 9){
//                  channel = 9;
//                }

                // convert information into a note and store it.
                notes.add(new Note(startingBeat, endBeat, instrument, pitch, volume));
                // remove the NOTE ON information from the arraylist of pitch, tick.
                currentTrack.remove(messageInTrack);
                // We found our NOTE OFF's corresponding NOTE ON. We don't need to look or remove
                // anything else further.
                break;
              }
            }
          }
        }
      }
      // Time to check the next track.
      trackCount += 1;
    }

    // We must filter out invalid notes with pitches that MIDI editor can't play.
    ArrayList<Note> badNotes = new ArrayList<>();
    for (Note note : notes) {
      if (note.pitch > 131 || note.pitch < 24) {
        badNotes.add(note);
      }
    }

    // remove all badnotes from our list of notes.
    notes.removeAll(badNotes);
    return notes;
  }

}
