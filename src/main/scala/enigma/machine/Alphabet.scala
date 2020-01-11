package enigma.machine

object Alphabet {
  val alphabet: Seq[Char] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray.toSeq
  def indexOf(c: Char): Int = alphabet.indexOf(c)
  def apply(i: Int): Char = alphabet(i)
}
