package sh.ball.patchable.audio.midi;

import javax.sound.midi.ShortMessage;

public interface MidiListener {

  void sendMidiMessage(ShortMessage message);
}
