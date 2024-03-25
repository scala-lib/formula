# Formula

Regex runtime alternative for Scala.

## Usage (Syntax)

You can use special simple & readable syntax to construct Regexes:

```scala
import Formula.Lib.Syntax.*

val frml = expect.fromStartToEnd {
	expect.safe('{')
		+ capture(
			expect.some { expect.letter }
		)
	+ expect.safe('}')
}

println(frml.r) // ^\{([a-zA-Z]+)\}$
```

## Usage (Parser)

You can write formula language directly in your code:

```scala
import Formula.Parser.Extension.*

val frml = formula"""
define HEX_3 group {
	match(3) LETTER
}
define HEX_6 group {
	match(6) LETTER
}

match '#'
match HEX_3 | HEX_6
"""

println(frml.value) // #([a-zA-Z]{3})|([a-zA-Z]{6})
```

Formula language code will be lexed, parsed and serialized into Regex.

## Changelog

-   v1.0.0 – added `match`, `group`, `define` statements

## Plans

-   [ ] Add comments via `#`
-   [ ] Add amount arguments for `match`
-   [ ] Add ability to pass custom variables from scala
-   [ ] Add `|` and `&` operators

## Local setup

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).
