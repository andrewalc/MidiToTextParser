package MidiParser.model;

/**
 * A Class representing a note. Used to store note note to be printed out to the finished text file.
 */
public class Note {

  private int startingBeat;
  private int endBeat;
  private int pitch;
  private int instrument;
  private int volume;

  /**
   * Constructor for a Note.
   *
   * @param startingBeat The beat the note starts at.
   * @param endBeat      The beat the note ends at.
   * @param instrument   The MIDI instrument value the note will play as.
   * @param pitch        The MIDI pitch value the note will play as.
   * @param volume       The volume this note will play at.
   */
  public Note(int startingBeat, int endBeat, int instrument, int pitch, int volume) {
    this.startingBeat = startingBeat;
    this.endBeat = endBeat;
    this.pitch = pitch;
    this.instrument = instrument;
    this.volume = volume;
  }

  public int getStartingBeat() {
    return startingBeat;
  }

  public int getEndBeat() {
    return endBeat;
  }

  public int getPitch() {
    return pitch;
  }

  public int getInstrument() {
    return instrument;
  }

  public int getVolume() {
    return volume;
  }


  @Override
  public String toString() {
    return "note " + startingBeat + " " + endBeat + " " + instrument + " " + pitch + " " + volume;
  }
}
