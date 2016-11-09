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

## the auto-correct of "irregardless"
This solution uses a word frequency based approach to find the best word. For a misspelling of a word, several candidates may be found. For each of these candidates I try to determine the worth of the candidates by comparing their frequency in the book "Sherlock Holmes" (I also added the 10,000 most commonly used words to the end of the book, to protect against Sir Arthur Conan Doyle boycotting a popular word). The word "irregardless" appears in "Sherlock Holmes" only once, but the word "regardless" appears several times, so I auto-correct to "regardless". I defend this behavior, because I would always prefer to have the most likely candidate. I would also defend this approach because it's how the brilliant AI professor Peter Norvig (who just so happened to write the AI book I used in college) did http://norvig.com/spell-correct.html.
