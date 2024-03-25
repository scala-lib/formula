package Formula.Parser.Formula

import scala.util.matching.Regex

class Formula(val value: String):
	def r: Regex = value.r

	override def toString = {
		s"Formula(${value})"
	}
	
	def test(value: String): Boolean = 
		r.findFirstMatchIn(value) match
			case Some(_) => true
			case None => false