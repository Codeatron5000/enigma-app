package enigma.machine

import enigma.machine.Rotor.I.setting

sealed trait Rotor {
    /**
     * The internal wiring of the rotor as a string of letters mapped to the
     * alphabet when the rotor is at setting 1.
     */
    val wiring: String
    /**
     * The point at which the next rotor along should step up.
     */
    val turnover: Int

    private var _setting = 1
    private var _position = 1

    validateIndex(_setting)
    validateIndex(_position)

    /**
     * Ensure the index is between 1 and 26.
     */
    private def normalize(index: Int): Int = {
        if (index > 25) {
            index - 26
        } else if (index < 0) {
            index + 26
        } else {
            index
        }
    }

    /**
     * Prevent any settings outside the alphabet bounds.
     */
    private def validateIndex(index: Int) {
        if (index > 26 || index < 1) {
            throw new AlphabetOutOBoundsException
        }
    }

    /**
     * Update the scrambling sequence to the current setting.
     */
    def sequence: Seq[Int] = {
        val sequence = wiring.toCharArray.toSeq
        (sequence.slice(26 - _setting + 1, 26) ++
            sequence.slice(0, 26 - _setting + 1)).map(Alphabet.indexOf)
    }

    /**
     * Should the tumbler trigger the next tumbler over to turn?
     */
    def shouldCarry: Boolean = _position == turnover

    /**
     * Should the tumbler trigger the next tumbler over to turn based on the
     * position of the next rotor. Due to a weird quirk with the enigma
     * machine, if the next rotor is close to its turnover, it should turnover
     * again.
     */
    def shouldCarry(nextRotor: Rotor): Boolean = {
        shouldCarry ||
            (_position == turnover + 1 &&
                nextRotor.position == nextRotor.turnover - 1)
    }

    def position_=(newPosition: Char): Unit = {
        position = Alphabet.indexOf(newPosition) + 1
    }

    def position_=(newPosition: Int): Unit = {
        validateIndex(newPosition)
        _position = newPosition
    }

    def position: Int = _position

    def setting_=(newPosition: Char): Unit = {
        setting = Alphabet.indexOf(newPosition) + 1
    }

    def setting_=(newSetting: Int): Unit = {
        validateIndex(newSetting)
        _setting = newSetting
    }

    def setting: Int = _setting

    /**
     * Scramble a char through the tumbler.
     */
    def encode(letter: Char): Char = {
        var index = Alphabet.indexOf(letter)
        index = normalize(index + position - 1)
        index = sequence(index)
        index = normalize(index - position + setting)
        Alphabet(index)
    }

    /**
     * Unscramble a char through the tumbler.
     */
    def decode(letter: Char): Char = {
        var index = Alphabet.indexOf(letter)
        index = normalize(index + position - setting)
        index = sequence.indexOf(index)
        index = normalize(index - position + 1)
        Alphabet(index)
    }

    /**
     * Turn the the rotor round round one step
     */
    def step(): Unit = {
        _position += 1
        if (_position > 26) _position -= 26
    }


    def apply(_setting: Char): Rotor = {
        setting = Alphabet.indexOf(_setting) + 1
        this
    }

    def apply(_setting: Int): Rotor = {
        setting = _setting
        this
    }
}

object Rotor {

    object I extends Rotor {
        val wiring: String = "EKMFLGDQVZNTOWYHXUSPAIBRCJ"
        val turnover: Int = 19
    }

    object II extends Rotor {
        val wiring: String = "AJDKSIRUXBLHWTMCQGZNPYFVOE"
        val turnover: Int = 6
    }

    object III extends Rotor {
        val wiring: String = "BDFHJLCPRTXVZNYEIWGAKMUSQO"
        val turnover: Int = 23
    }

    object IV extends Rotor {
        val wiring: String = "ESOVPZJAYQUIRHXLNFTGKDCMWB"
        val turnover: Int = 11
    }

    object V extends Rotor {
        val wiring: String = "VZBRGITYUPSDNHLXAWMJQOFECK"
        val turnover: Int = 0
    }
}

