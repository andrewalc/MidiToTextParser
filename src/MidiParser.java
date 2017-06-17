

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
    // We will store messages as int arrays with pitch tick fired and volume in this arraylist.
    // {pitch, tick, volume}

    // ArrayList<Integer[]> represents the list of all messages contained in an arraylist of a
    // single track's messages.

    ArrayList<ArrayList<Integer[]>> allNoteOnMsgs = new ArrayList<>();


    for (Track track : midi.getTracks()) {

      // The current track we are parsing needs a place in our {Pitch, Tick, volume} arraylist
      allNoteOnMsgs.add(new ArrayList<Integer[]>());

      int instrument = getInstrumentOfTrack(track);


      // parse through all messages (events) in this track
      for (int i = 0; i < track.size(); i++) {
        MidiEvent event = track.get(i);

        int channel = 0;
        // 144 = NOTE ON , 128 == NOTE OFF, if NOTE ON has volume (velocity) of 0 treat as NOTE OFF.
        if (event.getMessage() instanceof ShortMessage) {
          ShortMessage message = (ShortMessage) event.getMessage();
          // The tick that this message is being fired.
          int tickMsgFired = (int) event.getTick();
          int msgCommand = message.getCommand();


          // If the message we have is a NOTE ON message add the pitch, tick pair to our arraylist.
          if (msgCommand == 144) {

            int pitch = message.getData1();
            int volume = message.getData2();
            // All messages of this track in our NOTE ON arraylist
            ArrayList<Integer[]> currentTrackNoteOns = allNoteOnMsgs.get(trackCount);

            // If NOTE ON volume == 0, treat as a NOTE OFF and find its matching NOTE ON starting
            // message. Once found create a note and add it to the list.
            if(volume == 0){
              for(int k = 0; k < currentTrackNoteOns.size(); k++){
                Integer[] noteOnMsg = currentTrackNoteOns.get(k);
                if(noteOnMsg[0] == pitch){
                  int startingTickMsgFired = noteOnMsg[1];
                  int startingBeat = startingTickMsgFired;
                  int endBeat = tickMsgFired;

                  // Volume was 0, now make sure its the originals.
                  volume = noteOnMsg[2];

                  // Percussion channel is 9.
                  // If the message is in the percussion channel, we get keep that channel.
                  // Current implementation of text Note does not account for channels unfortunately.
//                if(message.getChannel() == 9){
//                  channel = 9;
//                }


                  // Create a note and store it.
                  notes.add(new Note(startingBeat, endBeat, instrument, pitch, volume));
                  // remove the NOTE ON information from the arraylist of pitch, tick.
                  currentTrackNoteOns.remove(noteOnMsg);
                  // We found our 'fake' NOTE ON's corresponding NOTE ON. We don't need to look or
                  // remove anything else further.
                  break;
                }
              }
            }else{
              // This is a real NOTE ON message, add the pitch tick volume to NOTE ON arraylist.
              currentTrackNoteOns.add(new Integer[]{pitch, tickMsgFired, volume});
            }

          }
          // NOTE OFF message found. Find the matching NOTE ON and create a note.
          else if (msgCommand == 128) {
            int pitch = message.getData1();
            int volume = message.getData2();
            // Some MIDIs have message volumes at 0, 64 is default for those.
            // @TODO see if this might be a problem with note on volume 0
            if (volume == 0) {
              volume = 64;
            }
            //In our developing arraylist, we want to get the arraylist that represents this track.
            ArrayList<Integer[]> currentTrackNoteOns = allNoteOnMsgs.get(trackCount);
            // Parse our arraylist and find this NOTE OFF msg's corresponding NOTE ON by looking
            // first NOTE ON that matches this NOTE OFF's pitch.
            for (int j = 0; j < currentTrackNoteOns.size(); j++) {
              Integer[] noteOnMsg = currentTrackNoteOns.get(j);
              if (noteOnMsg[0] == pitch) {
                int startingTickFired = noteOnMsg[1];
                int startingBeat = startingTickFired;
                int endBeat = tickMsgFired;


                // Percussion channel is 9.
                // If the message is in the percussion channel, we get keep that channel.
                // Current implementation of text Note does not account for channels unfortunately.
//                if(message.getChannel() == 9){
//                  channel = 9;
//                }


                // convert information into a note and store it.
                notes.add(new Note(startingBeat, endBeat, instrument, pitch, volume));
                // remove the NOTE ON information from the arraylist of NOTE ONS
                currentTrackNoteOns.remove(noteOnMsg);
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

  private int getInstrumentOfTrack(Track track) {
    // instruments default to 0 if track has no Program Change
    int instrument = 0;
    // Cycle through all messages in current track.
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
    return instrument;
  }

}
