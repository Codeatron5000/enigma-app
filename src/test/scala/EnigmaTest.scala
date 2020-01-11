import enigma.machine.{AlphabetOutOBoundsException, DuplicateRotorsException, Enigma, PlugBoardConflictException, Reflector, Rotor}
import org.scalatest.FunSuite

class EnigmaTest extends FunSuite {
  test("The enigma package can encode a value") {
    val enigma = Enigma(
      Rotor.I(3),
      Rotor.II(4),
      Rotor.III(5),
      Reflector.B,
      "EJ OY IV AQ KW FX MT PS LU BD".split(" ").toSeq.map(subs => Tuple2(subs.charAt(0), subs.charAt(1)))
    )

    enigma.setPositions(5, 4, 3)

    assert(enigma.encode('A') == 'K')

    assert(enigma.fastRotor.position == 4)
    assert(enigma.mediumRotor.position == 4)
    assert(enigma.slowRotor.position == 5)

    enigma.setPositions(5, 4, 3)

    assert(enigma.encode('K') == 'A')
  }

  test("The enigma rotors rotate") {
    val enigma = Enigma(
      Rotor.I(3),
      Rotor.II(4),
      Rotor.III(5),
      Reflector.B,
      "EJ OY IV AQ KW FX MT PS LU BD".split(" ").toSeq.map(subs => Tuple2(subs.charAt(0), subs.charAt(1)))
    )

    enigma.setPositions(5, 5, 23)

    assert(enigma.encode('A') == 'R')

    assert(enigma.fastRotor.position == 24)
    assert(enigma.mediumRotor.position == 6)
    assert(enigma.slowRotor.position == 6)
  }

  test("The rotors passed to the enigma machine must be unique") {
    assertThrows[DuplicateRotorsException] {
      Enigma(
        Rotor.I(3),
        Rotor.I(4),
        Rotor.III(5),
        Reflector.B,
        "EJ OY IV AQ KW FX MT PS LU BD".split(" ").toSeq.map(subs => Tuple2(subs.charAt(0), subs.charAt(1)))
      )
    }
  }

  test("The rotor setting must be between 1 and 26") {
    assertThrows[AlphabetOutOBoundsException] {
      Rotor.I(27)
    }
  }

  test("The rotor positions must be between 1 and 26") {
    val enigma = Enigma(
      Rotor.I(3),
      Rotor.II(4),
      Rotor.III(5),
      Reflector.B,
      "EJ OY IV AQ KW FX MT PS LU BD".split(" ").toSeq.map(subs => Tuple2(subs.charAt(0), subs.charAt(1)))
    )

    assertThrows[AlphabetOutOBoundsException] {
      enigma.setPositions(28, 4, 3)
    }
  }

  test("Plug board connections cannot clash") {
    assertThrows[PlugBoardConflictException] {
      Enigma(
        Rotor.I(3),
        Rotor.II(4),
        Rotor.III(5),
        Reflector.B,
        "EJ EK".split(" ").toSeq.map(subs => Tuple2(subs.charAt(0), subs.charAt(1)))
      )
    }
  }

  test("The enigma package can encode and decode a string") {
    val enigma = Enigma(
      Rotor.IV('M'),
      Rotor.III('R'),
      Rotor.V('D'),
      Reflector.C,
      "BY CP DF EZ GU HL JO KN QR SW".split(" ").toSeq.map(subs => Tuple2(subs.charAt(0), subs.charAt(1)))
    )

    enigma.setPositions('U', 'X', 'I')

    val cipher = "TOPSECRETMESSAGE".toCharArray.toSeq.map(enigma.encode)

    assert(cipher.mkString == "ZCHTFLESVDLVPREL")

    enigma.setPositions('U', 'X', 'I')

    val message = "ZCHTFLESVDLVPREL".toCharArray.toSeq.map(enigma.encode)

    assert(message.mkString == "TOPSECRETMESSAGE")
  }
}
