package enigma

sealed abstract class Rotor(setting: Int) {
  val wiring: String
  val turnover: Int

  if (setting > 26 || setting < 1) {
    throw new AlphabetOutOBoundsException
  }

  var position: Int = 1

  private def normalize(index: Int): Int = {
    if (index > 25) index - 26
    else if (index < 0) index + 26
    else index
  }

  lazy private val sequence: Seq[Int] = {
    val sequence = wiring.toCharArray
    sequence.slice(26 - setting + 1, 26) ++
      sequence.slice(0, 26 - setting + 1)
  }.map(Alphabet.indexOf)

  def shouldCarry(): Boolean = position == turnover
  def shouldCarry(nextRotor: Rotor): Boolean = {
    shouldCarry() ||
      (position == turnover + 1 && nextRotor.position == nextRotor.turnover - 1)
  }

  def setPosition(newPosition: Char): Unit = {
    setPosition(Alphabet.indexOf(newPosition) + 1)
  }

  def setPosition(newPosition: Int): Unit = {
    if (newPosition > 26 || newPosition < 0) {
      throw new AlphabetOutOBoundsException
    }
    position = newPosition
  }

  def encode(letter: Char): Char = {
    var index = Alphabet.indexOf(letter)
    index = normalize(index + position - 1)
    index = sequence(index)
    index = normalize(index - position + setting)
    Alphabet(index)
  }

  def decode(letter: Char): Char = {
    var index = Alphabet.indexOf(letter)
    index = normalize(index + position - setting)
    index = sequence.indexOf(index)
    index = normalize(index - position + 1)
    Alphabet(index)
  }

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

