package enigma.app

import enigma.machine.Rotor
import scalafx.beans.property.{ IntegerProperty, ObjectProperty }

class RotorProperty(private var _value: Rotor) {
    private var onChangeCallbacks: Seq[(RotorProperty, Rotor, Rotor) => Unit] = Seq.empty

    def onChange(cb: (RotorProperty, Rotor, Rotor) => Unit): Unit = {
        onChangeCallbacks = onChangeCallbacks :+ cb
    }

    def value: Rotor = _value

    def value_=(rotor: Rotor): Unit = {
        val original = _value
        _value = rotor
        onChangeCallbacks.foreach(cb => cb(this, original, rotor))
    }

    def apply(): Rotor = value

    def sync(): Unit = {
        if (value.position != _position()) {
            _position() = value.position
        }
    }

    // The positions of each rotor have a property so that they can change the
    // enigma machine when they change.
    val _position: IntegerProperty = {
        IntegerProperty(_value.position)
    }
    def position: IntegerProperty = _position
    def position_=(newPosition: Int): Unit = position() = newPosition
    position.onChange((newPosition, _, _) => {
        _value.position = newPosition()
    })

    // The settings of each rotor have a property so that they can change the
    // enigma machine when they change.
    val _setting: IntegerProperty = {
        IntegerProperty(_value.setting)
    }
    def setting: IntegerProperty = _setting
    def setting_=(newSetting: Int): Unit = setting() = newSetting
    setting.onChange((setting, _, _) => {
        _value.setting = setting()
    })
}
