package enigma

object Alphabet {
  val alphabet: Array[Char] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray
  def indexOf(c: Char): Int = alphabet.indexOf(c)
  def apply(i: Int): Char = alphabet(i)
}
