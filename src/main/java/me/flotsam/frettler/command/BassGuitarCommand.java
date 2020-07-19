package me.flotsam.frettler.command;

import me.flotsam.frettler.instrument.stringed.BassGuitar;
import picocli.CommandLine.Command;

/**
 * Handles the initial BASSGUITAR command/param
 * @author philwhiles
 *
 */
@Command(name = "BASSGUITAR", description = "Generates bass guitar scales (and chords?!)")
public class BassGuitarCommand extends StringedInstrumentCommand implements Runnable {

  
  @Override
  public void run() {
   exec(new BassGuitar(strings));
  }
}