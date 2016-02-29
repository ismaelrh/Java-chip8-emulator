[![Build Status](https://travis-ci.org/ismaro3/Java-chip8-emulator.svg?branch=master)](https://travis-ci.org/ismaro3/Java-chip8-emulator)
# Java Chip-8 Emulator

[Chip-8](https://en.wikipedia.org/wiki/CHIP-8) emulator written in Java, with the target of learning how emulators work.
It has a comprehensive set of tests (every CPU instruction) as well as a lot of comments so it is easily understandable.

Currently it has these features:
* Runs all Chip-8 games (Not Chip-48) flawlessly.
* Frequency can be changed before launching the emulator.
* Sound implemented.
* Displays by stdout time to emulate 1 second of emulated-system.

## How to run
Just run the Main class, or execute `gradle run` in the root directory of project.
To change loaded rom, edit Main.java.

## Roms
Roms are located in "roms" directory at root directory. Note that, although frequency by default is set to 500Hz,
different games require different frequencies.

##Keyboard

| Original | Emulator |
|----------|----------|
| 1 2 3 C  | 1 2 3 4  |
| 4 5 6 D  | Q W E R  |
| 7 8 9 E  | A S D F  |
| A 0 B F  | Z X C V  |


## Screenshot
![Emulator running "Invaders"](screenshot.png)
