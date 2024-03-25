package Formula.Parser.Parser

import scala.collection.mutable.ListBuffer
import scala.language.dynamics
import Formula.Parser.Token.*
import Formula.Parser.Formula.*

case class FormulaParseException(message: String) extends Exception(message)
class ParserProvided extends Dynamic

class Parser(val tokens: ListBuffer[Token], var data: Map[String, String] = Map()):
	private var value: StringBuilder = StringBuilder("")
	private var cursor: Int = 0
	private val length = tokens.length

	def parse(): Formula =
		nextToken()
		Formula(value.toString())

	private def nextToken(): Unit =
		if(cursor >= length) return
		
		value ++= parseStatement()

		moveCursor()
		nextToken()

	private def parseStatement(): String =
		val token = tokens(cursor)

		// match 'literal'
		if(token.tokenType == "match") parseMatch()
		// match 'group'
		else if(token.tokenType == "group") parseGroup()
		// match 'define'
		else if(token.tokenType == "define") parseDefine()
		// Unknown
		else {
			throw new FormulaParseException(s"Unknown token: '${token.tokenType}' at ${token.location.toString()}")
		}

	private def parseMatch(): String =
		// Skip 'match' keyword
		moveCursor()

		var matchValue = ""

		// It's literal value
		if (checkCurrent("literal")) {
			val literal = getCurrent[Token.Literal]("literal")

			matchValue = literal.value
		}
		// It's identifier
		else if (checkCurrent("id")) {
			val identifier = getCurrent[Token.Identifier]("id")
			val idValue = getConstant(identifier.value)

			// Constant not found
			if(idValue == null) {
				throw new FormulaParseException(s"Unknown constant '${identifier.value}'")
			}

			matchValue = idValue
		}
		else {
			throw new FormulaParseException(s"Expected literal string or constant after 'match', but got '${tokens(cursor).tokenType}' at ${tokens(cursor).location}")
		}

		// ;
		skipOptional("statement_end")

		matchValue
	
	private def parseGroup(): String =
		// Skip 'group' keyword
		moveCursor()

		var groupValue = StringBuilder()

		// It's anonymous group
		if (checkCurrent("anonymous")) {
			groupValue ++= "(?:"
			moveCursor()
		}
		// It's named group
		else if (checkCurrent("id")) {
			val identifier = getCurrent[Token.Identifier]("id")

			groupValue ++= s"(<${identifier.value}>"
			moveCursor()
		}
		else {
			groupValue ++= "("
		}

		// Expect {
		expectCurrent("group_start")
		moveCursor()

		// Inner statements
		while (cursor < length && (tokens(cursor).tokenType != "group_end")) {
			groupValue ++= parseStatement()

			moveCursor()
		}

		// Added end of group
		groupValue ++= ")"

		// Return group
		groupValue.toString()
	
	private def parseDefine(): String =
		moveCursor()

		// Identifier
		val id = expectCurrent[Token.Identifier]("id")
		moveCursor()
		
		// Statement
		val stmt = parseStatement()
		data = data.updated(id.value.toUpperCase(), stmt)

		// Define statement doesn't returns anything
		""

	private def moveCursor(): Unit =
		cursor = cursor + 1

	private def skipOptional(tokenType: String): Unit =
		if (checkCurrent(tokenType)) moveCursor()

	private def getCurrent[T <: Token](tokenType: String): T | Null =
		if(cursor >= length) null
		else if(tokens(cursor).tokenType != tokenType) null
		else tokens(cursor).asInstanceOf[T]
	
	private def checkCurrent(tokenType: String): Boolean =
		if(cursor >= length) false
		else tokens(cursor).tokenType == tokenType

	private def expectCurrent[T <: Token](tokenType: String): T =
		if (cursor >= length) throw new FormulaParseException(s"Expected '${tokenType}' but got 'end of file'")
		else if (tokens(cursor).tokenType != tokenType) throw new FormulaParseException(s"Expected '${tokenType}' but got '${tokens(cursor).tokenType}'")
		else tokens(cursor).asInstanceOf[T]

	private def getConstant(id: String): String | Null = id.toUpperCase() match
		case "LETTER" => "[a-zA-Z]"
		case "DIGIT" => "[0-9]"
		case "ANY" => "."
		case _ => {
			// Check for the value in data
			data.get(id.toUpperCase()) match
				case None => null
				case Some(value) => value
		}