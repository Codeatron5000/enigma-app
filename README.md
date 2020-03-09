# Enigma App
An accurate simulation of the German enigma machine used in World War II.

![Enigma demo gif](/resources/enigma-demo.gif)

There were many Enigma machines built leading to and during the second world war of varying complexity.
This simulation is based on the earlier Enigma M3 model.

For reference on how the Enigma machine works check out the
[Technical Details of the Enigma Machine](http://users.telenet.be/d.rijmenants/en/enigmatech.htm#top)
link which has in depth explanations about all the mechanics of the rotors and the plug board.

This simulation is designed to be a hands on approach that is meant to give an idea of how setting up
an Enigma machine was done and the process writing out messages and decoding.

## Installation
To get the application up and running you can either download the `jar` file directly and run it using java,
or you can clone this repository and run the program through sbt.

### Direct download
You will need to have a recent version of the JVM installed in your machine for it to work.
You likely already have it installed, but if not you can follow [this link](https://www.java.com/en/download) to install it.

Download [this link](https://github.com/Codeatron5000/enigma-app/raw/master/target/scala-2.13/EnigmaMachine-assembly-0.1.jar).

Navigate to the folder in your terminal or powershell and run `java -jar ./EnigmaMachine-assembly-0.1.jar`.

### SBT build
To build the project with SBT you need SBT and scala 2.13 or newer.

Simply clone the repository and run `sbt run` in the project directory.

You can run `sbt assembly` to generate the self contained java file mentioned above.

Or you can run `sbt compile` to generate a scala file that you can run with the scala executable.

## Usage
The Enigma machine had 4 levels of encryption.

First was the order of the rotors and the reflector,
there were 5 rotors to choose from and the machine needed 3 of them to work
and there were two reflectors of which the machine needed one.
(later versions of the Enigma machine had more rotors and reflectors but they are not featured in this application).

Second was the internal wiring of the rotors. Each rotor had 26 wires inside that mapped a connection from one side
to the other. These could be twisted so the connection mapping was relative to the setting.
So for example if a rotor on setting one had a wire that mapped the letter A to the letter P,
then on setting 2 the letter B would be mapped to the letter P and so on.

Third was the position of the rotor inside the machine. The rotors could be in any one of 26 positions each with the
name of the position being the number that appeared through the holes of the rotor case.
The position of the rotors changed with each key stroke.

Finally there was the plug board which was a set of up to 10 connections between any two letters.
All the plug board did was map one letter to another, much like the rotors did but without ever changing.

### Setting up the Enigma machine
When the application starts all the rotors and reflectors appear on the screen and the case is open.

To get the machine to work you need to choose 1 relfector and 3 rotors.
Drag the reflector to the long placeholder on the left and the 3 rotors to any of the remaining rotor placeholders.
To remove a rotor/reflector just click on it and the remaining rotors will appear.

To adjust the rotor settings you need to click on the red pin to the right of the numbers.
This will unlock the number wheel and allow you to turn it independently of the rotor.
The number that appears next to the red pin is the setting of the rotor.
Once you have the correct setting you can click the pin again to lock the rotor.

Once the pin is locked you can click and drag the rotor to change the position of the rotor in the machine.
The number that appears through the hole in the cover is the rotor position.

To set the connections simply click and drag from one plug in the plug board to another.
The white lines show the existing connections.
The Enigma machine had a maximum of 10 connections.
You cannot have two different letters connecting to the same letter.
Click on a connection to remove it.

### Encoding / decoding
The lower keyboard is where you punch in the letters of the code.
The upper keyboard is the lightbox that displays the encoded letter.
You can click the letters on the keyboard and see the encoded letter light up.
Or you can type a letter on your physical keyboard and the same encoded letter will light up.
Notice as you type that the rotors change position.
If you want to decode the cipher you will need to change the rotor positions
back to how they were when you started the cipher.

## Resources
[Technical Details of the Enigma Machine](http://users.telenet.be/d.rijmenants/en/enigmatech.htm#top)

[A more comprehensive Enigma simulator](https://summersidemakerspace.ca/projects/enigma-machine/)

[Wikipedia](https://en.wikipedia.org/wiki/Enigma_machine)

[An interesting video on the Enigma machine](https://www.youtube.com/watch?v=G2_Q9FoD-oQ)

## Future work
- Add a marker to show what the position is when the case is open.
- Angle the rotors when they are out of the machine so the rotor number can be seen on the side instead of on top.
- Allow the user to change the type of Enigma machine
