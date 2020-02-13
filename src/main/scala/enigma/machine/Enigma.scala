package enigma.machine

case class Enigma(slowRotor: Rotor, mediumRotor: Rotor, fastRotor: Rotor, reflector: Reflector, substitutions: Seq[(Char, Char)]) {
  private val slowClass = slowRotor.getClass
  private val mediumClass = mediumRotor.getClass
  private val fastClass = fastRotor.getClass

  if (Set(slowClass, mediumClass, fastClass).size != 3) {
    throw new DuplicateRotorsException
  }

  var plugBoard: PlugBoard = new PlugBoard(substitutions)

  def setPositions(slowPosition: Char, mediumPosition: Char, fastPosition: Char): Unit = {
    slowRotor.setPosition(slowPosition)
    mediumRotor.setPosition(mediumPosition)
    fastRotor.setPosition(fastPosition)
  }

  def setPositions(slowPosition: Int, mediumPosition: Int, fastPosition: Int): Unit = {
    slowRotor.setPosition(slowPosition)
    mediumRotor.setPosition(mediumPosition)
    fastRotor.setPosition(fastPosition)
  }

  def encode(c: Char): Char = {
    fastRotor.step()

    var carriedFast = false

    if (fastRotor.shouldCarry(mediumRotor)) {
      mediumRotor.step()
      carriedFast = true
    }

    if (carriedFast && mediumRotor.shouldCarry(slowRotor)) {
      slowRotor.step()
    }

    var result: Char = c.toUpper
    result = plugBoard.encode(result)

    result = fastRotor.encode(result)
    result = mediumRotor.encode(result)
    result = slowRotor.encode(result)

    result = reflector.encode(result)

    result = slowRotor.decode(result)
    result = mediumRotor.decode(result)
    result = fastRotor.decode(result)

    result = plugBoard.encode(result)
    result
  }
}
