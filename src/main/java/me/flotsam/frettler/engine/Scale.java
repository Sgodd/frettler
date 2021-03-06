/*
 * Copyright (C) 2020 Philip Whiles
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package me.flotsam.frettler.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import me.flotsam.frettler.engine.Chord.ChordType;
import me.flotsam.frettler.engine.IntervalPattern.PatternType;
import me.flotsam.frettler.view.Colour;
import me.flotsam.frettler.view.ColourMap;

enum Position {
  HEAD, MIDDLE, TAIL;
}


/**
 * Is a type of cyclic linked list. Represents a scale, and created from a root note and a scale
 * pattern containing intervals.
 * 
 * @author philwhiles
 *
 */
public class Scale {

  public static final Scale CHROMATIC_SCALE = new Scale(Arrays.asList(Note.values()));

  @Getter
  private ScaleNote head = null;
  @Getter
  private ScaleNote tail = null;
  @Getter
  private IntervalPattern scalePattern;
  @Getter
  private Note rootNote;


  public Scale(Note rootNote, IntervalPattern scalePattern) {
    if (scalePattern.getPatternType() == PatternType.CHORD) {
      System.err.println(
          "Interval pattern '" + scalePattern.getLabel() + "' is not a scale/mode pattern");
      System.exit(-1);
    }
    this.scalePattern = scalePattern;
    this.rootNote = rootNote;

    Optional<ScaleNote> rootScaleNote = CHROMATIC_SCALE.findScaleNote(rootNote);
    for (ScaleInterval interval : scalePattern.getIntervals()) {
      ScaleNote scaleNote = Scale.getScaleNote(rootScaleNote.get(), interval);
      addScaleNote(scaleNote.getNote(), Optional.of(interval));
    }
  }


  public Scale(List<Note> notes) {
    this.scalePattern = IntervalPattern.SCALE_CHROMATIC;
    this.rootNote = notes.get(0);
    ScaleInterval [] intervals = ScaleInterval.values(); 
    int interval = 0;
    for (Note note : notes) {
      addScaleNote(note, Optional.of(intervals[interval++]));
    }
  }


  private void addScaleNote(Note note, Optional<ScaleInterval> interval) {
    ScaleNote newNoteNode = new ScaleNote(note, interval, this);

    if (head == null) {
      head = newNoteNode;
      head.setPosition(Position.HEAD);
    } else {
      tail.setNextScaleNote(newNoteNode);
      tail.setPosition(Position.MIDDLE);
    }

    tail = newNoteNode;
    tail.setPosition(Position.TAIL);
    tail.setNextScaleNote(head);
  }



  public boolean containsNote(Note note) {
    ScaleNote currentNoteNode = head;

    if (head == null) {
      return false;
    } else {
      do {
        if (currentNoteNode.getNote() == note) {
          return true;
        }
        currentNoteNode = currentNoteNode.getNextScaleNote();
      } while (currentNoteNode != head);
      return false;
    }
  }

  public Optional<ScaleNote> findScaleNote(Note note) {
    ScaleNote currentNoteNode = head;

    if (head == null) {
      return Optional.empty();
    } else {
      do {
        if (currentNoteNode.getNote() == note) {
          return Optional.of(currentNoteNode);
        }
        currentNoteNode = currentNoteNode.getNextScaleNote();
      } while (currentNoteNode != head);
      return Optional.empty();
    }
  }

  public Optional<ScaleNote> findScaleNote(ScaleInterval scaleInterval) {
    ScaleNote currentNoteNode = head;

    if (head == null) {
      return Optional.empty();
    } else {
      do {
        if (currentNoteNode.getInterval().get() == scaleInterval) {
          return Optional.of(currentNoteNode);
        }
        currentNoteNode = currentNoteNode.getNextScaleNote();
      } while (currentNoteNode != head);
      return Optional.empty();
    }
  }

  public List<ScaleNote> getScaleNotes() {
    List<ScaleNote> scaleNotes = new ArrayList<>();
    ScaleNote scaleNote = head;

    if (scaleNote != null) {
      do {
        scaleNotes.add(scaleNote);
        scaleNote = scaleNote.getNextScaleNote();
      } while (scaleNote != head && scaleNote.getNote() != head.getNote());
    }
    return scaleNotes;
  }

  public String getTitle() {
    String title = null;
    if (scalePattern == IntervalPattern.SCALE_CHROMATIC) {
      title = scalePattern.getLabel() + " Scale";
    } else {
      title = rootNote.getLabel() + " " + scalePattern.getLabel();
    }
    return title;
  }

  public static ScaleNote getScaleNote(ScaleNote rootScaleNote, ScaleInterval interval) {
    return getScaleNote(rootScaleNote, interval.getSemiTones());
  }

  public static ScaleNote getScaleNote(ScaleNote rootScaleNote, int semiTones) {
    ScaleNote scaleNote = rootScaleNote;
    for (int i = 0; i < semiTones; i++) {
      scaleNote = scaleNote.getNextScaleNote();
    }
    return scaleNote;
  }

  public List<Chord> createScaleChords() {
    List<Chord> chords = new ArrayList<>();
    List<ScaleNote> scaleNotesToUse = null;
    List<ScaleNote> scaleNotes = null;

    if (!scalePattern.isScaleChordGenerationSupported()) {
      System.err.println("Chord generation from this scale is currently not supported");
    } else {
      if (scalePattern.getParentPattern() == null) {
        scaleNotesToUse = getScaleNotes();
      } else {
        scaleNotes = getScaleNotes();
        scaleNotesToUse = new Scale(rootNote, scalePattern.getParentPattern()).getScaleNotes();
      }
      for (ScaleNote scaleNote : scaleNotesToUse) {
        if (scaleNotes == null || scaleNotes.stream().anyMatch(sn -> sn.equalsTonally(scaleNote))) {
          chords.add(new Chord(scaleNote, ChordType.STANDARD));
        }
      }
    }
    return chords;
  }

  public String toString() {
    return describe(false);
  }


  public String describe(boolean mono) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n          ");
    Optional<ScaleNote> chromaticScaleNote = CHROMATIC_SCALE.findScaleNote(rootNote);
    ScaleNote scaleNote = head;
    List<Note> notes = new ArrayList<>();
    List<ScaleInterval> intervals = new ArrayList<>();
    List<Note> chromaticScaleNotes = new ArrayList<>();

    do {
      Note note = chromaticScaleNote.get().getNote();
      chromaticScaleNotes.add(note);
      if (note == scaleNote.getNote()) {
        notes.add(note);
        intervals.add(scaleNote.getInterval().get());
        scaleNote = scaleNote.getNextScaleNote();
      } else {
        notes.add(null);
        intervals.add(null);
      }
      chromaticScaleNote = Optional.of(chromaticScaleNote.get().getNextScaleNote());
    } while (chromaticScaleNote.get().getNote() != head.getNote());

    for (Note note : chromaticScaleNotes) {
      sb.append(String.format("%s%-2s    %s",
          notes.contains(note) ? (mono ? "" : ColourMap.get(note)) : "", note.getLabel(),
          (mono ? "" : Colour.RESET)));
    }
    sb.append("\n          ");
    for (int n = 0; n < intervals.size(); n++) {
      ScaleInterval interval = intervals.get(n);
      if (interval == null) {
        sb.append(String.format("      "));
      } else
        sb.append(String.format("%s%-2s    %s",
            notes.contains(notes.get(n)) ? (mono ? "" : ColourMap.get(notes.get(n))) : "", interval,
            (mono ? "" : Colour.RESET)));
    }
    sb.append("\n\n");
    return sb.toString();
  }
}


