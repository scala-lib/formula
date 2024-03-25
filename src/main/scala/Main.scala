import Formula.Lib.Syntax.*

@main def main(): Unit =
	// Example: {JdXgi}, {NpxTNxhueDy}
	val formula = expect.fromStartToEnd {
		expect.safe('{')
			+ capture(
				expect.some { expect.letter }
			)
		+ expect.safe('}')
	}

	println((formula.r, formula.matches("{JdXgi}")))