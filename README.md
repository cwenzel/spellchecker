# spellchecker
An exercise in scala

## running the tests
Within sbt run `test`

## generating a jar
Within sbt run `assembly` and the jar will be in target/scala-2.11/SpellChecker-assembly-1.0.jar

## running the jar
`java -jar SpellChecker-assembly-1.0.jar --file $FILE_WITH_WORDS`

## command line arguments
### file option
-f, --file <value> Required. File name to spell check. Full path or a file in the current working directory.
### debug option
-d, --debug <value> Optional. Default is false. Debug mode that prints back word name and timing info. Accepts a boolean value.
