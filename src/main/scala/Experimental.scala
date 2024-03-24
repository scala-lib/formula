case class Match(val regex: String):
  infix def +(that: Match): Match = Match(regex + that.regex)
  infix def |(that: Match): Match = Match(regex + "|" + that.regex)

  override def toString = regex
  def r = regex.r
  
object expect:
  def any = Match(".")
  def space = Match("\s")
  def digit = Match("[0-9]")
  def notDigit = Match("[^0-9]")
  def letter = Match("[a-zA-Z]")
  def notLetter = Match("[^a-zA-Z]")
  
  def exactly(amount: Int) = Match(s"{${amount}}")
  def exactly(amount: Int)(value: Match | String): Match = modify(_ + s"{${amount}}")(value)

  def amount(from: Int, to: Int) = Match(s"{${from},${to}}")
  def amount(from: Int, to: Int)(value: Match | String): Match = modify(_ + s"{${from},${to}}")(value)
  
  def anyAmount = Match("+")
  def anyAmount(value: Match | String): Match = modify(_ + "+")(value)

  def optionalAnyAmount = Match("*")
  def optionalAnyAmount(value: Match | String): Match = modify(_ + "*")(value)

  def optional = Match("?")
  def optional(value: Match | String): Match = modify(_ + "?")(value)

  def fromStartToEnd(value: Match | String): Match = modify("^" + _ + "$")(value)

  def modify(modifier: (String) => String)(value: Match | String): Match = value match {
    case m: Match => Match(modifier(m.regex))
    case s: String => Match(modifier(s))
  }
  
  def safe(value: Char): Match = value match {
    case '(' | ')' | '[' | ']' | '{' | '}' | '^' | '$' | '.' | '|' | '?' | '*' | '+' => Match("\\" + value)
    case '\\' => Match("\\\\")
    case _ => Match(value.toString)
  }
  def safe(value: String): Match = Match(value.replaceAll("([()\\[\\]{}\\^$.|?*+])", "\\\\$1"))
  def unsafe(value: String): Match = Match(value)
  
  def apply(value: Match | String): Match = value match {
    case m: Match => m
    case s: String => unsafe(s)
  }
end expect

object capture:
  def apply(value: Match | String): Match = expect.modify("(" + _ + ")")(value)
  def named(name: String)(value: Match | String) = expect.modify(s"(<${name}>" + _ + ")")(value)
  def anonymous(value: Match | String): Match = expect.modify("(?:" + _ + ")")(value)
end capture

// Example: {JdXgi}, {NpxT-825}
val formula = expect.fromStartToEnd {
    expect.safe('{')
        + capture(
            expect("[a-zA-Z]") + expect.anyAmount
            + expect.optional(
                expect.safe('-')
                + expect("[0-9]") + expect.anyAmount
            )
        )
    + expect.safe('}')
}

println(formula.r)