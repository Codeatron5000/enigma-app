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
    private var _slowRotor = new RotorProperty(initialSlowRotor)
    def slowRotor: RotorProperty = _slowRotor
    def slowRotor_=(rotor: Rotor): Unit = _slowRotor.value = rotor
    def slowRotor_=(rotor: RotorProperty): Unit = {
        _slowRotor = rotor
        enigma.slowRotor = rotor()
        rotor.onChange((_, _, rotor) => {
            enigma.slowRotor = rotor
        })
    }
    slowRotor.onChange((_, _, rotor) => {
        enigma.slowRotor = rotor
    })
    private var _mediumRotor = new RotorProperty(initialMediumRotor)
    def mediumRotor: RotorProperty = _mediumRotor
    def mediumRotor_=(rotor: Rotor): Unit = _mediumRotor.value = rotor
    def mediumRotor_=(rotor: RotorProperty): Unit = {
        _mediumRotor = rotor
        enigma.mediumRotor = rotor()
        rotor.onChange((_, _, rotor) => {
            enigma.mediumRotor = rotor
        })
    }
    mediumRotor.onChange((_, _, rotor) => {
        enigma.mediumRotor = rotor
    })
    private var _fastRotor = new RotorProperty(initialFastRotor)
    def fastRotor: RotorProperty = _fastRotor
    def fastRotor_=(rotor: Rotor): Unit = _fastRotor.value = rotor
    def fastRotor_=(rotor: RotorProperty): Unit = {
        _fastRotor = rotor
        enigma.fastRotor = rotor()
        rotor.onChange((_, _, rotor) => {
            enigma.fastRotor = rotor
        })
    }
    fastRotor.onChange((_, _, rotor) => {
        enigma.fastRotor = rotor
    })
    private val _reflector = ObjectProperty[Reflector](initialReflector)
    def reflector: ObjectProperty[Reflector] = _reflector
    def reflector_=(reflector: Reflector): Unit = _reflector.value = reflector
    reflector.onChange((_, _, reflector) => {
        enigma.reflector = reflector
    })

    // The plug board connections have a property which is updated when the
    // addConnection and removeConnection methods are called. That then
    // updates the enigma object.
    val _connections: ObjectProperty[Seq[(Char, Char)]] = {
        ObjectProperty(initialConnections)
    }
    def connections: ObjectProperty[Seq[(Char, Char)]] = _connections
    def connections_=(newConnections: Seq[(Char, Char)]): Unit = {
        connections() = newConnections
    }
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
    def encode(c: Char): Char = {
        val result = enigma.encode(c)
        fastRotor.sync()
        mediumRotor.sync()
        slowRotor.sync()
        result
    }
}
