import Formula.Extension.*

@main def main(): Unit =
  val frml = formula"""
    define SOME group ! {
      match LETTER;
      match DIGIT;
    }

    match SOME
  """

  println(frml.value)