# Frettler - A CLI for generating musical scales and chords with fretboard and chord rendering for a variety of fretted instruments

## Synopsis
This is a CLI program, written in Java 11, that exposes the results of it's own music theory API through console rendering of a fretboard.
The rendering is just a bunch of System.out.prinln statements, but uses Unicode boxing characters and ANSI colour coding.
Example output can be seen at the bottom of this page. The rendering turned out a lot better than I had hoped tbh!

## Building
The application is built using maven, but you don't need to have maven pre-installed - you can build an executable jar that will be
used by the included bash wrapper, with the following

```
./build

```

## Execution

### Command Line
Run it using the provided shell wrapper, frettler, ie :


```
./frettler GUITAR HORIZONTAL SCALE C MAJOR_SCALE

```

Need to use drop D tuning? just use the optional strings argument '-s D,A,D,G,B,E'

Add the optional chords argument '-c' and frettler will calculate the chords in that key and display each using the VERTICAL chord view.

You can use the strings argument to use any number of strings and using any tuning. 
To make that easier for say ukelele players, frettler has several alternative instruments, which are
simply conveniences to avoid having to keep on using the strings argument. ie in the examples shown above you could use one of the following instruments :

- GUITAR
- BASSGUITAR
- UKELELE
- MANDOLIN
- BANJO

Yeah, about that last one - frettler assumes every banjo has equal length strings. The shortened fifth string on most standard banjos is somethng I need to work
out how to handle. Watch this space.

The chord calculation used in the VERTICAL view is definately a work in progress. It appears to work for standard six string guitar, open string chords, but for anything else,
take the chord fingerings calculated with a pinch of salt. I already know its calculation for C Major with a seven string guitar is a bit out of whack, 
and that is probably an indication that it will fall short elsewhere.

Here are some examples :

<img src="https://github.com/philwhiles/frettler/blob/master/frettler.png"/>


NOTE: the VERTICAL display of SCALE is TBD at this time.

NOTE: the CLI arguments are handled by the Java framework, picocli, added to the codebase in the space of one evening (after actually practising guitar for 2 hours!),
as well as the maven fat jar executable generation and the maven wrapper for building, and the bash wrappers. 
This will all no doubt go through some further refinements soon to make it easier to use!

### Programmatically
If you want to you can write your own Main class and create a Guitar object, create a Scale or Chord object, create a Guitar and a view for that Guitar
and then instruct the View to display your chord or object. The API is pretty straightfoward I think, and defaults safely to a standard tuning EADGBE six 
string guitar etc
Have a look at the GuitarCommand for some examples of usage, look at the constructors of the various classes such as Guitar, Scale, Chord and the ChordView and 
GuitarView classes, and their public methods.

## Engine
The engine can generate Lists of notes that represent given scales, and can calculate the chords within that scale.
The engine knows nothing about a guitar, it simply applies music theory to generate Java lists of the notes in scales and chords.


## View
Currently only console views, each constructed with a Guitar, which take the scale and chord constructs from the engine, and render them
on that guitars fretboard, up to the 12th fret.

Both the GuitarView and the ChordView can display the notes or intervals with unique ANSI colours, if you are
either running from the command line and using an ANSI colour friendly terminal, or in Eclipse using an ANSI Console
plugin (goto to Eclipse Marketplace and search for 'ANSI console').

The colours look good in my Eclipse with Dark mode (what programmer doesn't use Dark Mode?!), and they help to scan the notes and intervals
and easily see the patterns. 

The ChordView now calculates the open string fingering for a chord, but can still display all occurences using an alternate method.

## Caveats
This is a work in progress.

### Music theory
Prior to writing this app, my knowledge of music theory was pretty rudimentary. Still is to be honest.
I have been learning to play guitar for the last six months, and have been deliberately not rushing into it as I want to build up my knowledge of music
theory at the same time. I don't want to blindly learn the fingering for various chords without understanding how the scales are constructed, how the chords 
in that scale can be derived, and how to name those chords, and also how the chord fingering is arrived at.
A lot of the theory behind this code is formed from my reading random resources and trying to fit it all together, so forgive me if some of the music domain
names used are suspect, or the rules in my music theory code has some gaps or holes. I am finding this to be a great learning exercise, and I shall get there.

### Chord fingerings
The ChordView still requires some work - it can calculate the chord fingering for fairly standard, open string, major, minor and diminished chords
but I am still working on it - I need to check it's handiwork for a wider variety of chords and confirm that my algorithm for selecting the correct 
fingering works extensively. I havent found any resources online which explain how chord fingerings are derived. Yes, I know it's all about the tonic,
a third and fifth etc, but each string has multiple candidates for each note in a chord. My algorithm favours the higher frets, can exclude strings lower 
than the tonic string, can avoid duplicating the same note in the same octave as it works from the sixth string to the first etc. But I need to put that
theory to the test with a wider set of chords and confirm my assumptions hold water.

## TODO
- Add some Javadoc, for my own sanity if no one elses
- Extend the types of scales it understands
- Write the VERTICAL SCALE view and expose it through the CLI 
- Verify the VERTICAL CHORD fingering output for the more esoteric chords!
- Actually learn guitar!


