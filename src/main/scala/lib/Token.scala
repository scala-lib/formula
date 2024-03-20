package Formula.Token

/* Used to store location of an token */
case class TokenLocation(var line: Int = 1, var column: Int = 0):
	/* Increase column value */
	def ++ = (column = column + 1)
	/* Decrease column value */
	def -- = (column = column - 1)

	/* Increase line value */
	def +++ = (line = line + 1, column = 0)
	/* Decrease line value */
	def --- = (line = line - 1, column = 0)

	override def toString = {
		s"Line ${line}, Column ${column}"
	}

/* Used to store invidual token information */
enum Token(var tokenType: String, val location: TokenLocation):
	case Match(_location: TokenLocation) extends Token("match", _location)
	case Group(_location: TokenLocation) extends Token("group", _location)
	case Define(_location: TokenLocation) extends Token("define", _location)
	case If(_location: TokenLocation) extends Token("if", _location)
	case Else(_location: TokenLocation) extends Token("else", _location)

	// (
	case ArgStart(_location: TokenLocation) extends Token("arg_start", _location)
	// )
	case ArgEnd(_location: TokenLocation) extends Token("arg_end", _location)
	// {
	case GroupStart(_location: TokenLocation) extends Token("group_start", _location)
	// }
	case GroupEnd(_location: TokenLocation) extends Token("group_end", _location)
	// ,
	case Comma(_location: TokenLocation) extends Token("comma", _location)
	// !
	case Anonymous(_location: TokenLocation) extends Token("anonymous", _location)
	// |
	case Or(_location: TokenLocation) extends Token("or", _location)
	// &
	case And(_location: TokenLocation) extends Token("and", _location)
	// ;
	case StatementEnd(_location: TokenLocation) extends Token("statement_end", _location)

	case Identifier(val value: String, _location: TokenLocation) extends Token("id", _location)
	case Operator(val value: String, _location: TokenLocation) extends Token("operator", _location)
	case Literal(val value: String, _location: TokenLocation) extends Token("literal", _location)
end Token