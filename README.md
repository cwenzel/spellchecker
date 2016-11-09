# spellchecker
An exercise in scala

## running the tests
Within sbt run `test`

## generating a jar
Within sbt run `assembly` and the jar will be in target/scala-2.11/SpellChecker-assembly-1.0.jar

## running the jar
`java -jar SpellChecker-assembly-1.0.jar --file $FILE_WITH_WORDS`

## command line options
### file option
-f, --file <value>   File name to spell check. Full path or a file in the current working directory.
### debug option
-d, --debug <value>  Debug mode. Prints back word name and timing info. Accepts Boolean values.
