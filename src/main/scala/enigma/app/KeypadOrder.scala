package enigma.app

object KeypadOrder {
  val order = Seq(
    Seq('Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I', 'O'),
    Seq('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K'),
    Seq('P', 'Y', 'X', 'C', 'V', 'B', 'N', 'M', 'L'),
  )

  def apply(): Seq[Seq[Char]] = order
}
