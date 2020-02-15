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
    private val _slowRotor: ObjectProperty[Rotor] = ObjectProperty(initialSlowRotor)
    def slowRotor: ObjectProperty[Rotor] = _slowRotor
    def slowRotor_=(rotor: Rotor): Unit = slowRotor() = rotor
    slowRotor.onChange((_, _, rotor) => {
        enigma.slowRotor = rotor
        slowRotorPosition() = rotor.position
    })
    val _mediumRotor: ObjectProperty[Rotor] = ObjectProperty(initialMediumRotor)
    def mediumRotor: ObjectProperty[Rotor] = _mediumRotor
    def mediumRotor_=(rotor: Rotor): Unit = mediumRotor() = rotor
    mediumRotor.onChange((_, _, rotor) => {
        enigma.mediumRotor = rotor
        mediumRotorPosition() = rotor.position
    })
    val _fastRotor: ObjectProperty[Rotor] = ObjectProperty(initialFastRotor)
    def fastRotor: ObjectProperty[Rotor] = _fastRotor
    def fastRotor_=(rotor: Rotor): Unit = fastRotor() = rotor
    fastRotor.onChange((_, _, rotor) => {
        enigma.fastRotor = rotor
        fastRotorPosition() = rotor.position
    })

    // The positions of each rotor have a property so that they can change the
    // enigma machine when they change.
    val _slowRotorPosition: IntegerProperty = {
        IntegerProperty(initialSlowRotor.position)
    }
    def slowRotorPosition: IntegerProperty = _slowRotorPosition
    def slowRotorPosition_=(position: Int): Unit = slowRotorPosition() = position
    slowRotorPosition.onChange((position, _, _) => {
        enigma.slowRotor.position = position()
    })
    val _mediumRotorPosition: IntegerProperty = {
        IntegerProperty(initialMediumRotor.position)
    }
    def mediumRotorPosition: IntegerProperty = _mediumRotorPosition
    def mediumRotorPosition_=(position: Int): Unit = mediumRotorPosition() = position
    mediumRotorPosition.onChange((position, _, _) => {
        enigma.mediumRotor.position = position()
    })
    val _fastRotorPosition: IntegerProperty = {
        IntegerProperty(initialFastRotor.position)
    }
    def fastRotorPosition: IntegerProperty = _fastRotorPosition
    def fastRotorPosition_=(position: Int): Unit = fastRotorPosition() = position
    fastRotorPosition.onChange((position, _, _) => {
        enigma.fastRotor.position = position()
    })

    // The settings of each rotor have a property so that they can change the
    // enigma machine when they change.
    val _slowRotorSetting: IntegerProperty = {
        IntegerProperty(initialSlowRotor.setting)
    }
    def slowRotorSetting: IntegerProperty = _slowRotorSetting
    def slowRotorSetting_=(setting: Int): Unit = slowRotorSetting() = setting
    slowRotorSetting.onChange((setting, _, _) => {
        enigma.slowRotor.setting = setting()
    })
    val _mediumRotorSetting: IntegerProperty = {
        IntegerProperty(initialMediumRotor.setting)
    }
    def mediumRotorSetting: IntegerProperty = _mediumRotorSetting
    def mediumRotorSetting_=(setting: Int): Unit = mediumRotorSetting() = setting
    mediumRotorSetting.onChange((setting, _, _) => {
        enigma.mediumRotor.setting = setting()
    })
    val _fastRotorSetting: IntegerProperty = {
        IntegerProperty(initialFastRotor.setting)
    }
    def fastRotorSetting: IntegerProperty = _fastRotorSetting
    def fastRotorSetting_=(setting: Int): Unit = fastRotorSetting() = setting
    fastRotorSetting.onChange((setting, _, _) => {
        enigma.fastRotor.setting = setting()
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
        if (fastRotor().position != fastRotorPosition()) {
            fastRotorPosition = fastRotor().position
        }
        if (mediumRotor().position != mediumRotorPosition()) {
            mediumRotorPosition = mediumRotor().position
        }
        if (slowRotor().position != slowRotorPosition()) {
            slowRotorPosition = slowRotor().position
        }
        result
    }
}
