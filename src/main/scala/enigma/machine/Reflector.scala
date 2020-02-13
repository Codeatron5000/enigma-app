package enigma.machine

sealed abstract class ReflectorWiring(val wiring: String)

/**
 * The reflector doesn't have any complex mechanisms it just maps one letter to
 * another.
 */
sealed trait Reflector {
    /**
     * The string of letters that are mapped to the alphabet.
     */
    val wiring: String

    /**
     * Build the char map from the alphabet to the scrambled char.
     */
    val sequence: Map[Char, Char] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray
        .zip(wiring.toCharArray).toMap

    /**
     * Encode a char through the reflector (there is not need for a decode method
     * as the reflector encodes the same char both ways.
     */
    def encode(letter: Char): Char = sequence(letter)
}

object Reflector {

    case object B extends ReflectorWiring("YRUHQSLDPXNGOKMIEBFZCWVJAT") with Reflector

    case object C extends ReflectorWiring("FVPJIAOYEDRZXWGCTKUQSBNMHL") with Reflector

}
