package Formula.Parser.Lexer

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex
import Formula.Parser.Token.*

case class FormulaLexException(message: String) extends Exception(message)

class Lexer(val code: String):
	private var cursor: Int = 0
	private val length: Int = code.length()
	private var loc = TokenLocation()
	private var tokens: ListBuffer[Token] = ListBuffer()

	/* Lex code and get tokens */
	def lex(): ListBuffer[Token] =
		nextCharacter()
		tokens

	private def nextCharacter(): Unit = 
		if (cursor >= length) return

		// Get current character
		val char = code(cursor)
		// Is new line? -> skip && change location line
		if (char == '\n') {
			loc.+++
			cursor = cursor + 1
			nextCharacter()
			return
		}
		// Is space? -> skip
		else if (isSpace(char)) {
			moveCursor()
			nextCharacter()
			return
		}
		// Is identifier -> lex it
		else if (isLetter(char)) {
			val identifier = lexIdentifier(loc.copy())

			if (identifier != null) {
				val tok = lexString(identifier.value, identifier.location)

				if(tok == null) tokens.addOne(identifier)
				else tokens.addOne(tok)
			}
		}
		// Is string literal -> lex it
		else if (char == '\'' || char == '"') {
			val literal = lexLiteral(char, loc.copy())
			if (literal != null) tokens.addOne(literal)
		}
		// Other character
		else {
			val token = lexCharacter(char, loc.copy())

			if (token != null) tokens.addOne(token)
			else throw new FormulaLexException(s"Unknown formula token: '${char}'")
		}
		
		// To the next
		moveCursor()
		nextCharacter()

	private def moveCursor(): Unit = 
		cursor = cursor + 1
		loc.++

	private def isSpace(char: Char): Boolean =
		"""\s""".r.findFirstMatchIn(char.toString()) match
			case Some(_) => true
			case None => false

	private def isLetter(char: Char): Boolean =
		"[a-zA-Z]".r.findFirstMatchIn(char.toString()) match
			case Some(_) => true
			case None => false

	private def lexLiteral(until: Char, startLocation: TokenLocation): Token.Literal | Null =
		// Skip first character
		moveCursor()

		// Until end of string
		var literal = ""
		while(cursor < length && code(cursor) != until) {
			literal = literal + code(cursor)
			moveCursor()
		}

		if(cursor >= length) {
			throw new FormulaLexException("Unexpected end of file, string literal was disclosed")
		}
		else {
			Token.Literal(literal, startLocation)
		}

	private def lexIdentifier(startLocation: TokenLocation): Token.Identifier | Null =
		var identifier = code(cursor).toString()

		// Skip first character
		moveCursor()

		// Until end of identifier
		while(cursor < length && isLetter(code(cursor))) {
			identifier = identifier + code(cursor)
			moveCursor()
		}

		Token.Identifier(identifier, startLocation)
	
	private def lexCharacter(char: Char, tokenLocation: TokenLocation): Token | Null = char match
		case '(' => Token.ArgStart(tokenLocation)
		case ')' => Token.ArgEnd(tokenLocation)
		case '{' => Token.GroupStart(tokenLocation)
		case '}' => Token.GroupEnd(tokenLocation)
		case ',' => Token.Comma(tokenLocation)
		case '!' => Token.Anonymous(tokenLocation)
		case '|' => Token.Or(tokenLocation)
		case '&' => Token.And(tokenLocation)
		case ';' => Token.StatementEnd(tokenLocation)
		// Operators
		case '=' => Token.Operator("=", tokenLocation)
		case '-' => Token.Operator("-", tokenLocation)
		case '+' => Token.Operator("+", tokenLocation)
		case '*' => Token.Operator("*", tokenLocation)
		case '/' => Token.Operator("/", tokenLocation)
		case '%' => Token.Operator("%", tokenLocation)
		// None
		case _ => null
	
	private def lexString(identifier: String, tokenLocation: TokenLocation): Token | Null = identifier match
		case "match" => Token.Match(tokenLocation)
		case "group" => Token.Group(tokenLocation)
		case "define" => Token.Define(tokenLocation)
		case "if" => Token.If(tokenLocation)
		case "else" => Token.Else(tokenLocation)
		// None
		case _ => null