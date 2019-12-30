package enigma

case class PlugBoard(substitutions: Seq[(Char, Char)]) {
  private val forwardSubstitutions = substitutions.toMap
  private val reverseSubstitutions = for ((k, v) <- forwardSubstitutions) yield (v, k)
  val subMap: Map[Char, Char] = forwardSubstitutions ++ reverseSubstitutions

  if (subMap.size != substitutions.size * 2) {
    throw new PlugBoardConflictException
  }

  def encode(letter: Char): Char = subMap.getOrElse(letter, letter)
}
