package Formula.Lib.Syntax

case class Match(val regex: String):
  // Methods for concatenation of [[Match]]
  infix def and(that: Match): Match = this + that
  infix def +(that: Match): Match = Match(regex + that.regex)

  // Methods for chaining [[Match]] using OR operator
  infix def or(that: Match): Match = this | that
  infix def |(that: Match): Match = Match(regex + "|" + that.regex)

  override def toString = regex

  def r = regex.r
  def matches(value: String) = value.matches(regex)
  
object expect:
  /**
    * Expect any possible character
    */
  def any = Match(".")
  /**
    * Expect space character
    */
  def space = Match("\\s")
  /**
    * Expect any digit character (0-9)
    */
  def digit = Match("[0-9]")
  /**
    * Expect any NOT digit character (everything except 0-9)
    */
  def notDigit = Match("[^0-9]")
  /**
    * Expect any letter character (a-zA-Z)
    */
  def letter = Match("[a-zA-Z]")
  /**
    * Expect any NOT letter character (everything except a-zA-Z)
    */
  def notLetter = Match("[^a-zA-Z]")
  
  /**
    * Expect character in range (0-9 will expect digits, a-zA-Z will expect letters)
    */
  def charRange(range: String) = Match(s"[${range}]")

  /**
    * Expect character in NOT range (0-9 will expect anything but NOT digits)
    */
  def notCharRange(range: String) = Match(s"^[${range}]")

  /**
    * Expect exact amount of characters
    *
    * @param amount amount of characters to expect
    */
  def exactly(amount: Int) = Match(s"{${amount}}")
  def exactly(amount: Int)(value: Match | String): Match = modify(_ + s"{${amount}}")(value)

  /**
    * Expect minimal and maximum amount of characters
    *
    * @param min minimal amount of characters to expect
    * @param max maximum amount of characters to expect
    */
  def amount(min: Int, max: Int) = Match(s"{${min},${max}}")
  def amount(min: Int, max: Int)(value: Match | String): Match = modify(_ + s"{${min},${max}}")(value)
  
  /**
    * Expect from 1 to unlimited amount of characters
    */
  def some = Match("+")
  def some(value: Match | String): Match = modify(_ + "+")(value)

  /**
    * Expect from 0 to unlimited amount of characters
    */
  def maybeSome = Match("*")
  def maybeSome(value: Match | String): Match = modify(_ + "*")(value)

  /**
    * Expect character, but optionally
    */
  def maybe = Match("?")
  def maybe(value: Match | String): Match = modify(_ + "?")(value)

  /**
    * Expect value to be from start to end of string
    */
  def fromStartToEnd(value: Match | String): Match = modify("^" + _ + "$")(value)

  /**
    * Group multiple expectations, can be used to add maybe/amount to a group of expectations.
    */
  def grouped(value: Match | String): Match = modify("(?:" + _ + ")")(value)

  /**
   * Modifies string or [[Match]] using lambda function
   */
  def modify(modifier: (String) => String)(value: Match | String): Match = value match {
    case m: Match => Match(modifier(m.regex))
    case s: String => Match(modifier(s))
  }
  
  /**
    * Expect character. Safe: dangerous characters will be escaped
    */
  def safe(value: Char): Match = value match {
    case '(' | ')' | '[' | ']' | '{' | '}' | '^' | '$' | '.' | '|' | '?' | '*' | '+' => Match("\\" + value)
    case '\\' => Match("\\\\")
    case _ => Match(value.toString)
  }
  /**
    * Expect text. Safe: dangerous characters will be escaped
    */
  def safe(value: String): Match = Match(value.replaceAll("([()\\[\\]{}\\^$.|?*+])", "\\\\$1"))

  /**
    * Expect character. Unsafe: dangerous characters will be NOT escaped
    */
  def unsafe(value: Char): Match = Match(value.toString)
  /**
    * Expect text. Unsafe: dangerous characters will be NOT escaped
    */
  def unsafe(value: String): Match = Match(value)
  
  /**
    * Expect characters. Better to use [[expect.safe]] for escaping dangerous characters
    */
  def apply(value: Match | String): Match = value match {
    case m: Match => m
    case s: String => unsafe(s)
  }
end expect

object capture:
  /**
    * Capture characters in a group
    */
  def apply(value: Match | String): Match = expect.modify("(" + _ + ")")(value)

  /**
    * Capture characters in a group, but optionally
    */
  def maybe(value: Match | String): Match = expect.modify("(" + _ + ")?")(value)

  /**
    * Capture characters in a named group: (<name>some)
    */
  def named(name: String)(value: Match | String) = expect.modify(s"(<${name}>" + _ + ")")(value)
  /**
    * Capture characters in a anonymous group: (?:some)
    */
  def anonymous(value: Match | String): Match = expect.modify("(?:" + _ + ")")(value)
end capture