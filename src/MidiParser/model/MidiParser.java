package MidiParser.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.*;


/**
 * Class for creating a MIDI parser to convert a (.mid / .midi) file to a txt file with the
 * appropriate tempo and notes needed to play in a CS3500 Music Editor.
 */
public class MidiParser {
  private final Sequence midi;
  private String fileName;
  private int tempo;

  /**
   * Constructor for a MidiParser. Requires a pathname string to the MIDI file.
   *
   * @param filePathName A pathname string to the MIDI file.
   */
  public MidiParser(String filePathName) throws MidiUnavailableException, IOException,
          InvalidMidiDataException {
    String[] fileNameSplit = filePathName.split("\\.");
    // Get the file name without the extension.
    this.fileName = fileNameSplit[0];
    // Store the midi as a sequence.
    this.midi = MidiSystem.getSequence(new File(filePathName));
    //  Calculate the midi's tempo by setting it as the sequence for a new MIDISystem Sequencer.
    Sequencer seq = MidiSystem.getSequencer();
    seq.open();
    seq.setSequence(midi);
    this.tempo = (int) (seq.getTempoInMPQ() / midi.getResolution());

  }

  /**
   * Writes a txt file, translating the midi file's notes to text in the format of the
   * MusicCreator cs3500 assignment.
   */
  public void writeMidiTextFile() {
    try {
      ArrayList<Note> notes = this.translateMidiToNotes();
      FileWriter writer = new FileWriter(fileName + ".txt");
      writer.append("tempo " + tempo + "\n");
      int noteCount = 0;
      for (Note note : notes) {
        writer.append(note.toString() + "\n");
        noteCount++;
      }
      writer.close();
      System.out.println(noteCount + " notes at a tempo of " + tempo + " were exported as "
              + fileName + ".txt");
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Translates MIDI messages in a midi file to an ArrayList of Notes, object which hold the
   * proper note data necessary in a CS3500 midi txt file.
   *
   * @return All the notes that are played in MidiParser's midi.
   */
  private ArrayList<Note> translateMidiToNotes() throws InvalidMidiDataException {

    // We will return a list of all notes played in the midi.
    ArrayList<Note> notes = new ArrayList<>();

    // Integer[] will store NOTE ON message information with pitch, tick fired, and volume.
    // {pitch, tick, volume}

    // ArrayList<Integer[]> represents the list of all messages contained in an arraylist of a
    // single track's messages.

    // ArrayList<ArrayList<Integer[]>> represents the culmination of all tracks containing these
    // NOTE ON messages. We store them here for now, then once we find a NOTE OFF message or a
    // NOTE ON with a volume of 0, we look for the original message here and pair them, forming a
    // note. When a note is formed, remove the NOTE ON message from this list.
    ArrayList<ArrayList<Integer[]>> unpairedNoteOnMsgs = new ArrayList<>();

    // The track we are currently parsing.
    int trackCount = 0;
    for (Track track : midi.getTracks()) {

      // The current track we are parsing needs a place in our {Pitch, Tick, volume} arraylist.
      unpairedNoteOnMsgs.add(new ArrayList<Integer[]>());

      // Get the MIDI instrument for this track. Notes in the same track should share the same
      // instrument.
      int instrument = getInstrumentOfTrack(track);


      // parse through all messages (events) in this track
      for (int i = 0; i < track.size(); i++) {
        MidiEvent event = track.get(i);

        int channel = 0; // if we ever decide to implement proper percussion, tied to channel 10.

        // 144 = NOTE ON , 128 == NOTE OFF, if NOTE ON has volume (velocity) of 0 treat as NOTE OFF.
        if (event.getMessage() instanceof ShortMessage) {
          ShortMessage message = (ShortMessage) event.getMessage();
          // The tick that this message is being fired.
          int tickMsgFired = (int) event.getTick();
          int msgCommand = message.getCommand();


          // We found a NOTE ON message. {data1 is pitch, data2 is volume}
          if (msgCommand == 144) {
            int pitch = message.getData1();
            int volume = message.getData2();
            //In our lost and found arraylist, we want to get the arraylist that represents this
            // track's unpaired NOTE ON messages.
            ArrayList<Integer[]> currTrackLostNoteOns = unpairedNoteOnMsgs.get(trackCount);

            // If NOTE ON volume == 0, treat as a NOTE OFF and find its matching NOTE ON starting
            // message. Once found, create a note and add it to the list.
            if (volume == 0) {
              for (int k = 0; k < currTrackLostNoteOns.size(); k++) {
                Integer[] noteOnMsg = currTrackLostNoteOns.get(k);
                if (noteOnMsg[0] == pitch) {
                  int startingTickMsgFired = noteOnMsg[1];
                  int startingBeat = startingTickMsgFired;
                  int endBeat = tickMsgFired;

                  // Volume was 0, now make sure its the original's.
                  volume = noteOnMsg[2];

                  // Percussion channel is 9.
                  // If the message is in the percussion channel, we get keep that channel.
                  // Current implementation of text Note does not account for channels
                  // unfortunately.
                  //if(message.getChannel() == 9){
                  //  channel = 9;
                  //}

                  // Create a note and store it.
                  notes.add(new Note(startingBeat, endBeat, instrument, pitch, volume));
                  // remove the NOTE ON information from the arraylist of pitch, tick.
                  currTrackLostNoteOns.remove(noteOnMsg);
                  // We found our 'fake' NOTE ON's corresponding NOTE ON. We don't need to look or
                  // remove anything else further.
                  break;
                }
              }

            }
            // If the NOTE ON isn't volume 0, then a real NOTE ON message, add the {pitch tick
            // volume} to NOTE ON arraylist.
            else {
              currTrackLostNoteOns.add(new Integer[]{pitch, tickMsgFired, volume});
            }

          }
          // NOTE OFF message found. Find the matching NOTE ON message and create a note.
          else if (msgCommand == 128) {
            int pitch = message.getData1();
            int volume = message.getData2();
            // Some MIDIs have message volumes at 0, 64 is default for those.
            // @TODO see if this might be a problem with note on volume 0
            if (volume == 0) {
              volume = 64;
            }
            //In our lost and found arraylist, we want to get the arraylist that represents this
            // track's unpaired NOTE ON messages.
            ArrayList<Integer[]> currTrackLostNoteOns = unpairedNoteOnMsgs.get(trackCount);
            // Parse our arraylist and find this NOTE OFF msg's corresponding NOTE ON by looking
            // first NOTE ON that matches this NOTE OFF's pitch.
            for (int j = 0; j < currTrackLostNoteOns.size(); j++) {
              Integer[] noteOnMsg = currTrackLostNoteOns.get(j);
              if (noteOnMsg[0] == pitch) {
                int startingTickFired = noteOnMsg[1];
                int startingBeat = startingTickFired;
                int endBeat = tickMsgFired;

                // Percussion channel is 9.
                // If the message is in the percussion channel, we get keep that channel.
                // Current implementation of text Note does not account for channels unfortunately.
                //if(message.getChannel() == 9){
                //  channel = 9;
                //}

                // convert information into a note and store it.
                notes.add(new Note(startingBeat, endBeat, instrument, pitch, volume));
                // remove the NOTE ON information from the arraylist of NOTE ONS
                currTrackLostNoteOns.remove(noteOnMsg);
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
      if (note.getPitch() > 131 || note.getPitch() < 24) {
        badNotes.add(note);
      }
    }

    // remove all badnotes from our list of notes.
    notes.removeAll(badNotes);
    return notes;
  }

  /**
   * Returns the MIDI instrument of a given track, assuming a track's notes all share the same
   * instrument.
   *
   * @param track The track to get an instrument value from.
   * @return The MIDI instrument value for the given track.
   */
  private int getInstrumentOfTrack(Track track) {
    // instruments default to 0 if track has no Program Change message.
    int instrument = 0;
    // Cycle through all messages in current track.
    for (int i = 0; i < track.size(); i++) {
      // set this track's instrument, we only need to look at the first instance of a PROGRAM
      // CHANGE message, all tracks should share the same instrument.
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
