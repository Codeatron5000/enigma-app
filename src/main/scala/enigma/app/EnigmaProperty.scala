package enigma.app

import enigma.machine.{ PlugBoard, Enigma, Reflector, Rotor }
import scalafx.beans.property.{ IntegerProperty, ObjectProperty }

/**
 * An observable wrapper around the enigma machine that provides properties for
 * each of the settings that can be watched for changes.
 */
class EnigmaProperty(
    private val initialSlowRotor: Rotor,
    private val initialMediumRotor: Rotor,
    private val initialFastRotor: Rotor,
    private val initialReflector: Reflector,
    private val initialConnections: Seq[(Char, Char)]
) {
    // The underlying enigma instance.
    private val enigma = new Enigma(
        initialSlowRotor: Rotor,
        initialMediumRotor: Rotor,
        initialFastRotor: Rotor,
        initialReflector: Reflector,
        initialConnections: Seq[(Char, Char)]
    )

    // Each rotor is given a property and updates the enigma machine and the
    // settings property when it is updated.
    val slowRotor: ObjectProperty[Rotor] = ObjectProperty(initialSlowRotor)
    slowRotor.onChange((_, _, rotor) => {
        enigma.slowRotor = rotor
        slowRotorPosition() = rotor.getPosition
    })
    val mediumRotor: ObjectProperty[Rotor] = ObjectProperty(initialMediumRotor)
    mediumRotor.onChange((_, _, rotor) => {
        enigma.mediumRotor = rotor
        mediumRotorPosition() = rotor.getPosition
    })
    val fastRotor: ObjectProperty[Rotor] = ObjectProperty(initialFastRotor)
    fastRotor.onChange((_, _, rotor) => {
        enigma.fastRotor = rotor
        fastRotorPosition() = rotor.getPosition
    })

    // The settings of each rotor have a property so that they can change the
    // enigma machine when they change.
    val slowRotorPosition: IntegerProperty = IntegerProperty(initialSlowRotor.getPosition)
    slowRotorPosition.onChange((position, _, _) => {
        enigma.slowRotor.setPosition(position())
    })
    val mediumRotorPosition: IntegerProperty = IntegerProperty(initialMediumRotor.getPosition)
    mediumRotorPosition.onChange((position, _, _) => {
        enigma.mediumRotor.setPosition(position())
    })
    val fastRotorPosition: IntegerProperty = IntegerProperty(initialFastRotor.getPosition)
    fastRotorPosition.onChange((position, _, _) => {
        enigma.fastRotor.setPosition(position())
    })

    // The plug board connections have a property which is updated when the
    // addConnection and removeConnection methods are called. That then
    // updates the enigma object.
    val connections: ObjectProperty[Seq[(Char, Char)]] = ObjectProperty(initialConnections)
    connections.onChange((connections, _, _) => {
        enigma.plugBoard = new PlugBoard(connections())
    })

    def addConnection(connection: (Char, Char)): Unit = {
        connections.value = connections() :+ connection
    }

    def removeConnection(connection: (Char, Char)): Unit = {
        val newConnections = connections().filterNot(_ == connection)
        if (newConnections.size < connections().size) {
            connections.value = newConnections
        }
    }

    // A wrapper around the enigma's encode method.
    def encode(c: Char): Char = enigma.encode(c)
}
