package enigma.app

import enigma.machine.{ Enigma, PlugBoard, Reflector }

import scala.collection.mutable

/**
 * An observable wrapper around the enigma machine that provides properties for
 * each of the settings that can be watched for changes.
 */
object EnigmaProperty {
    private val rotors: mutable.Seq[Option[RotorProperty]] = mutable.Seq(None, None, None)
    // The plug board connections have a property which is updated when the
    // addConnection and removeConnection methods are called. That then
    // updates the enigma object.
    private val _plugBoard: PlugBoard = new PlugBoard(Seq.empty)
    private var _reflector: Option[Reflector] = None

    // Each rotor is given a property and updates the enigma machine and the
    // settings property when it is updated.
    def slowRotor: Option[RotorProperty] = rotors.head

    def slowRotor_=(rotor: RotorProperty): Unit = {
        rotors(0) = Some(rotor)
    }
    def slowRotor_=(option: Option[RotorProperty]): Unit = {
        rotors(0) = option
    }

    def mediumRotor: Option[RotorProperty] = rotors.head

    def mediumRotor_=(rotor: RotorProperty): Unit = {
        rotors(1) = Some(rotor)
    }
    def mediumRotor_=(option: Option[RotorProperty]): Unit = {
        rotors(1) = option
    }

    def fastRotor: Option[RotorProperty] = rotors.head

    def fastRotor_=(rotor: RotorProperty): Unit = {
        rotors(2) = Some(rotor)
    }
    def fastRotor_=(option: Option[RotorProperty]): Unit = {
        rotors(2) = option
    }

    def addConnection(connection: (Char, Char)): Unit = {
        _plugBoard.connections = connections :+ connection
    }

    def connections: Seq[(Char, Char)] = _plugBoard.connections

    def removeConnection(connection: (Char, Char)): Unit = {
        val newConnections = connections.filterNot(_ == connection)
        if (newConnections.size < connections.size) {
            _plugBoard.connections = newConnections
        }
    }

    // A wrapper around the enigma's encode method.
    def encode(c: Char): Option[Char] = {
        enigma.map(v => {
            val result = v.encode(c)
            rotors.foreach(_.foreach(_.sync()))
            result
        })
    }

    /**
     * Dynamically build the enigma instance each time you want it. As any of
     * the components could be missing in which case the enigma machine
     * wouldn't work.
     * @return
     */
    def enigma: Option[Enigma] = {
        if (isDisabled) {
            None
        } else {
            Some(Enigma(
                rotors.head.get(),
                rotors(1).get(),
                rotors(2).get(),
                reflector.get,
                _plugBoard
            ))
        }
    }

    def reflector: Option[Reflector] = _reflector

    def reflector_=(reflector: Reflector): Unit = _reflector = Some(reflector)
    def reflector_=(option: Option[Reflector]): Unit = _reflector = option

    /**
     * Indicate if the enigma machine is disabled or not.
     * @return
     */
    def isDisabled: Boolean = {
        rotors.exists(_.isEmpty) || reflector.isEmpty
    }
}
