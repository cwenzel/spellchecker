import scala.io.Source
import java.io.InputStream

object SpellChecker extends App {
  private case class Config(file: String = "")

  (new scopt.OptionParser[Config]("SpellChecker") {
   opt[String]('f', "file").required() action((value, c) => c.copy(file = value)) text("File name to spell check")
  }).parse(args, Config()).map({ config =>

    val stream : InputStream = getClass.getResourceAsStream("/words.txt")
    val dictionary = scala.io.Source.fromInputStream( stream ).getLines.toList
    readFile(config.file, dictionary)

  }).getOrElse(sys.exit(-1))

  def readFile(fileName : String, dictionary : List[String]) : Boolean = {
   try {
     for (line <- Source.fromFile(fileName).getLines()) {
       spellCheckWord(line, dictionary)
     }
   }
   catch {
    case ex: Exception => println("Error, unable to read file")
    return false
   }
   return true
  }

  def spellCheckWord(word : String, dictionary : List[String]) = {
    dictionary.find((a) => a == word) match {
      case Some(_) => println(word + ": CORRECT")
      case None => autoCorrectWord(word, dictionary)
    }
  }

  def autoCorrectWord(word : String, dictionary : List[String]) = {
    println(word + ": INCORRECT")
  }
}
