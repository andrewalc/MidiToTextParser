import java.lang.annotation.Documented;

/**
 * Created by Andrew Alcala on 6/15/2017.
 */
public class Note {
  int startingBeat;
  int endBeat;
  int pitch;
  int instrument;
  int volume;

  public Note(int startingBeat, int endBeat, int instrument, int pitch, int volume){
    this.startingBeat =startingBeat;
    this.endBeat = endBeat;
    this.pitch = pitch;
    this.instrument = instrument;
    this.volume = volume;
  }


  @Override
  public String toString(){
    return "note " + startingBeat + " " + endBeat + " " + instrument + " " + pitch + " " + volume
            + "\n";
  }
}
