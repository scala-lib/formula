package Formula.Parser.Extension

import Formula.Parser.Lexer.*
import Formula.Parser.Parser.*
import Formula.Parser.Formula.*

implicit class FormulaHelper(private val sc: StringContext) extends AnyVal {
	def formula(args: Any*): Formula = 
		val lexed = Lexer(sc.s()).lex()
		val parsed = Parser(lexed).parse()
		parsed
}