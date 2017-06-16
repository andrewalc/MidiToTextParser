

import java.awt.image.ShortLookupTable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.sound.midi.*;


/**
 * A skeleton for MIDI playback
 */
public class MidiParser {
  private final Synthesizer synth;
  private final Receiver receiver;
  private final Sequence midi;

  public MidiParser(Sequence midi) throws MidiUnavailableException {
    this.synth = MidiSystem.getSynthesizer();
    this.receiver = synth.getReceiver();
    this.synth.open();
    this.midi = midi;

  }

  /**
   * Relevant classes and methods from the javax.sound.midi library:
   * <ul>
   * <li>{@link MidiSystem#getSynthesizer()}</li>
   * <li>{@link Synthesizer}
   * <ul>
   * <li>{@link Synthesizer#open()}</li>
   * <li>{@link Synthesizer#getReceiver()}</li>
   * <li>{@link Synthesizer#getChannels()}</li>
   * </ul>
   * </li>
   * <li>{@link Receiver}
   * <ul>
   * <li>{@link Receiver#send(MidiMessage, long)}</li>
   * <li>{@link Receiver#close()}</li>
   * </ul>
   * </li>
   * <li>{@link MidiMessage}</li>
   * <li>{@link ShortMessage}</li>
   * <li>{@link MidiChannel}
   * <ul>
   * <li>{@link MidiChannel#getProgram()}</li>
   * <li>{@link MidiChannel#programChange(int)}</li>
   * </ul>
   * </li>
   * </ul>
   *
   * @see <a href="https://en.wikipedia.org/wiki/General_MIDI"> https://en.wikipedia
   * .org/wiki/General_MIDI
   * </a>
   */

//  int startingBeat = note.get(0);
//  int endBeat = note.get(1);
//  int instrument = note.get(2);
//  int pitch = note.get(3);
//  int volume = note.get(4);
  public ArrayList<Note> translateMidiToNotes() throws InvalidMidiDataException {
/*    Sequencer seq = null;
    try {
      seq = MidiSystem.getSequencer();
      seq.open();
     // seq.setTempoInMPQ(10000000);
      //seq.getMicrosecondPosition();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
    seq.setSequence(midi);

    seq.start();
    */

    Sequencer seq = null;
    try {
      seq = MidiSystem.getSequencer();
      seq.open();
      seq.setSequence(midi);


    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }

    ArrayList<Note> notes = new ArrayList<>();
    float tempo = seq.getTempoInMPQ();
    int timeResolution = midi.getResolution();
    int trackCount = 0;
    ArrayList<ArrayList<Integer[]>> trackByPitch = new ArrayList<>();
    for(Track track : midi.getTracks()){
      int instrument = 0;
      for(int i = 0; i < track.size(); i++){
        // set this track's instrument
        MidiEvent instrumentEvent = track.get(i);
        if(instrumentEvent.getMessage() instanceof ShortMessage){
          ShortMessage instrumentMessage = (ShortMessage) instrumentEvent.getMessage();
          int instrumentCommand = instrumentMessage.getCommand();
          if(instrumentCommand == (int) (ShortMessage.PROGRAM_CHANGE & 0xFF)){
             instrument = instrumentMessage.getData1();
             break;
          }
        }
      }


      System.out.println("Tracks " + midi.getTracks().length);
      trackByPitch.add(new ArrayList<Integer[]>());

      for(int i = 0; i < track.size(); i++){
        int channel = 0;

        MidiEvent event = track.get(i);
        if(event.getMessage() instanceof ShortMessage) {
          ShortMessage message = (ShortMessage) event.getMessage();

          int command = message.getCommand();
          int pitch = -1;
          int volume = -1;
          int timeOccured = (int) event.getTick();
          System.out.println(command);


          int ticksPerBeat = midi.getResolution();
          float divisionType = midi.getDivisionType();
          //System.out.println(ticksPerBeat + " and " + divisionType);

          if (command == (int) (ShortMessage.NOTE_OFF & 0xFF)) {
            pitch = message.getData1();
            volume = message.getData2();
            ArrayList<Integer[]> currentTrack = trackByPitch.get(trackCount);
            for(int j = 0; j < currentTrack.size() ; j++){
              Integer[] messageInTrack = currentTrack.get(j);
              if(messageInTrack[0] == pitch){
                int startingTimeOccured = messageInTrack[1];
                int timeElapsed = timeOccured - startingTimeOccured;
                int startingBeat = (int) ((startingTimeOccured));
                int endBeat = (int) ((timeOccured));
                System.out.println("Adding");
                if(message.getChannel() == 9){
                  channel = 10;
                }
                notes.add(new Note(startingBeat, endBeat, instrument, pitch, volume));
                currentTrack.remove(messageInTrack);
                break;
              }
            }
          } else if (command == (int) (ShortMessage.NOTE_ON & 0xFF)) {

            pitch = message.getData1();
            volume = message.getData2();
            trackByPitch.get(trackCount).add(new Integer[]{pitch, timeOccured});

          }
        }
      }
      trackCount += 1;
    }





//        this.receiver.send(event, 1);
//        currentTime += tempo;
//        try {
//
//          Thread.sleep(100);
//
//        } catch (InterruptedException e) {
//          System.out.println(e.getMessage());
//        }




    /*
    The receiver does not "block", i.e. this method
    immediately moves to the next line and closes the
    receiver without waiting for the synthesizer to
    finish playing.

    You can make the program artificially "wait" using
    Thread.sleep. A better solution will be forthcoming
    in the subsequent assignments.
    */
    // Only call this once you're done playing *all* notes

    ArrayList<Note> badNotes = new ArrayList<>();
    for(Note note : notes){
      if(note.pitch > 131 || note.pitch < 24){
        badNotes.add(note);
      }
    }

    for(Note note : badNotes){
      notes.remove(note);
    }

    return notes;
  }

  public void initialize() {
    try {
      System.out.println("init");
      translateMidiToNotes();
    } catch (InvalidMidiDataException e) {
      System.out.println(e.getMessage());
    }
  }
}
