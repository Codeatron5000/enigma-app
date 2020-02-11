package enigma.machine

/**
 * Defines the behaviour of the rotor tumblers.
 *
 * @param setting The orientation of the numbers in the tumbler.
 * @param position The position of the tumbler in the machine.
 */
sealed abstract class Rotor(
    private var setting: Int = 1,
    private var position: Int = 1
) {
    /**
     * The internal wiring of the rotor as a string of letters mapped to the
     * alphabet when the rotor is at setting 1.
     */
    val wiring: String
    /**
     * The point at which the next rotor along should step up.
     */
    val turnover: Int

    validateIndex(setting)
    validateIndex(position)

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
        (sequence.slice(26 - setting + 1, 26) ++
            sequence.slice(0, 26 - setting + 1)).map(Alphabet.indexOf)
    }

    /**
     * Should the tumbler trigger the next tumbler over to turn?
     */
    def shouldCarry: Boolean = position == turnover

    /**
     * Should the tumbler trigger the next tumbler over to turn based on the
     * position of the next rotor. Due to a weird quirk with the enigma
     * machine, if the next rotor is close to its turnover, it should turnover
     * again.
     */
    def shouldCarry(nextRotor: Rotor): Boolean = {
        shouldCarry ||
            (position == turnover + 1 &&
                nextRotor.getPosition == nextRotor.turnover - 1)
    }

    def setPosition(newPosition: Char): Unit = {
        setPosition(Alphabet.indexOf(newPosition) + 1)
    }

    def setPosition(newPosition: Int): Unit = {
        validateIndex(newPosition)
        position = newPosition
    }

    def getPosition: Int = position

    def setSetting(newPosition: Char): Unit = {
        setSetting(Alphabet.indexOf(newPosition) + 1)
    }

    def setSetting(newSetting: Int): Unit = {
        validateIndex(newSetting)
        setting = newSetting
    }

    def getSetting: Int = setting

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
        position += 1
        if (position > 26) position -= 26
    }
}

object Rotor {

    case class I(setting: Int) extends Rotor(setting) {
        val wiring: String = "EKMFLGDQVZNTOWYHXUSPAIBRCJ"
        val turnover: Int = 19

        def this(setting: Char) = this(Alphabet.indexOf(setting) + 1)
    }

    object I {
        def apply(setting: Char) = new I(setting)
    }

    case class II(setting: Int) extends Rotor(setting) {
        val wiring: String = "AJDKSIRUXBLHWTMCQGZNPYFVOE"
        val turnover: Int = 6

        def this(setting: Char) = this(Alphabet.indexOf(setting) + 1)
    }

    object II {
        def apply(setting: Char) = new II(setting)
    }

    case class III(setting: Int) extends Rotor(setting) {
        val wiring: String = "BDFHJLCPRTXVZNYEIWGAKMUSQO"
        val turnover: Int = 23

        def this(setting: Char) = this(Alphabet.indexOf(setting) + 1)
    }

    object III {
        def apply(setting: Char) = new III(setting)
    }

    case class IV(setting: Int) extends Rotor(setting) {
        val wiring: String = "ESOVPZJAYQUIRHXLNFTGKDCMWB"
        val turnover: Int = 11

        def this(setting: Char) = this(Alphabet.indexOf(setting) + 1)
    }

    object IV {
        def apply(setting: Char) = new IV(setting)
    }

    case class V(setting: Int) extends Rotor(setting) {
        val wiring: String = "VZBRGITYUPSDNHLXAWMJQOFECK"
        val turnover: Int = 0

        def this(setting: Char) = this(Alphabet.indexOf(setting) + 1)
    }

    object V {
        def apply(setting: Char) = new V(setting)
    }

}

