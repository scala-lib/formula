package Formula.Extension

import Formula.Lexer.*
import Formula.Parser.*
import Formula.Formula.*

implicit class FormulaHelper(private val sc: StringContext) extends AnyVal {
	def formula(args: Any*): Formula = 
		val lexed = Lexer(sc.s()).lex()
		val parsed = Parser(lexed).parse()
		parsed
}