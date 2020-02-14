package enigma.machine

/**
 * The enigma class that houses the rotors and encodes/decodes input.
 *
 * @param slowRotor The left most rotor that moves the least
 * @param mediumRotor The middle rotor
 * @param fastRotor The right most rotor that moves on each character
 * @param reflector The reflector, can be either B or C
 * @param plugBoard A sequence of plug board substitutions
 */
case class Enigma(
    var slowRotor: Rotor,
    var mediumRotor: Rotor,
    var fastRotor: Rotor,
    var reflector: Reflector,
    var plugBoard: PlugBoard
) {
    def this(
        slowRotor: Rotor,
        mediumRotor: Rotor,
        fastRotor: Rotor,
        reflector: Reflector,
        connections: Seq[(Char, Char)]
    ) {
        this(
            slowRotor,
            mediumRotor,
            fastRotor,
            reflector,
            new PlugBoard(connections)
        )
    }

    /**
     * Ensure all the rotors are different.
     */
    private def validateRotors(): Unit = {
        if (
            slowRotor.getClass == mediumRotor.getClass ||
            mediumRotor.getClass == fastRotor.getClass ||
            slowRotor.getClass == fastRotor.getClass
        ) {
            throw new DuplicateRotorsException
        }
    }

    validateRotors()

    /**
     * Change the positions of the three rotors using letters.
     */
    def setPositions(slowPosition: Char, mediumPosition: Char, fastPosition: Char): Unit = {
        slowRotor.setPosition(slowPosition)
        mediumRotor.setPosition(mediumPosition)
        fastRotor.setPosition(fastPosition)
    }

    /**
     * Change the positions of the three rotors using numbers.
     */
    def setPositions(slowPosition: Int, mediumPosition: Int, fastPosition: Int): Unit = {
        slowRotor.setPosition(slowPosition)
        mediumRotor.setPosition(mediumPosition)
        fastRotor.setPosition(fastPosition)
    }

    /**
     * Encode/decode a letter through the enigma machine.
     */
    def encode(c: Char): Char = {
        // First off we step up the fast rotor.
        fastRotor.step()

        // Second check if the medium rotor should step and then step it up.
        var carriedFast = false

        if (fastRotor.shouldCarry(mediumRotor)) {
            mediumRotor.step()
            carriedFast = true
        }

        // If we stepped up the medium rotor and the slow rotor should step we
        // step it up.
        if (carriedFast && mediumRotor.shouldCarry(slowRotor)) {
            slowRotor.step()
        }

        // We start encoding the letter by sending it through the plug board.
        var result: Char = c.toUpper
        result = plugBoard.encode(result)

        // Then we send it through each of the rotors.
        result = fastRotor.encode(result)
        result = mediumRotor.encode(result)
        result = slowRotor.encode(result)

        // Pass it through the reflector.
        result = reflector.encode(result)

        // Now back through the rotors in reverse.
        result = slowRotor.decode(result)
        result = mediumRotor.decode(result)
        result = fastRotor.decode(result)

        // Finally through the plug board one more time before returning the
        // result.
        result = plugBoard.encode(result)

        result
    }
}
