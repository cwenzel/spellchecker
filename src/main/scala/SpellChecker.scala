import scala.io.Source
import java.io.InputStream
import scala.collection.mutable.ListBuffer

object SpellChecker extends App {
  private case class Config(file: String = "")
  private var frequencyMap : Map[String, Int] = Map()
  private var numberOfWords : Int = 0

  (new scopt.OptionParser[Config]("SpellChecker") {
   opt[String]('f', "file").required() action((value, c) => c.copy(file = value)) text("File name to spell check")
  }).parse(args, Config()).map({ config =>

    val stream : InputStream = getClass.getResourceAsStream("/words.txt")
    val dictionary = scala.io.Source.fromInputStream( stream ).getLines.toList
    setWordFrequency
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
    chooseBestEdit(editsOfWord(word)) match {
      case Some(w) => println("YOU SAID: " + word + ". DID YOU MEAN? " + w)
      case None => println(word + ": INCORRECT")
    }
  }

  def chooseBestEdit(edits : List[String]) : Option[String]= {
    var bestMatch = ("", 0)
    edits.foreach(candidate => {
      val occurrences = frequencyMap.getOrElse(candidate, 0)
      if (occurrences > bestMatch._2)
        bestMatch = (candidate, occurrences)
    })

    if (bestMatch._2 > 0) Some(bestMatch._1) else None
  }

  //All edits that are one edit away from the word
  def editsOfWord(word : String) : List[String] = {
    var edits : ListBuffer[String] = ListBuffer()
    val letters = "abcdefghijklmnopqrstuvwxyz"

    //deletes
    for (i <- word.indices) { edits += (word.toList.take(i) ++ word.toList.drop(i + 1)).mkString }

    //additions
    for (i <- word.indices) { letters.foreach(letter => {
      edits += (word.toList.take(i) ++ List(letter) ++ word.toList.drop(i)).mkString
    })}

    //replaces
    for (i <- word.indices) { letters.foreach(letter => {
      edits += (word.toList.take(i) ++ List(letter) ++ word.toList.drop(i + 1)).mkString
    })}

    //switching of neighbor chars
    for (i <- word.indices) {
      val firstHalf = word.toList.take(i)
      val secondHalf = word.toList.drop(i)
      edits += (firstHalf.take(i - 1) ++ List(secondHalf(0)) ++ firstHalf.drop(firstHalf.length - 1) ++ secondHalf.drop(1)).mkString
    }

    edits.toList
  }

  def setWordFrequency = {
    val stream : InputStream = getClass.getResourceAsStream("/sherlock_holmes.txt")
    val lineIterator = scala.io.Source.fromInputStream( stream ).getLines
    for (line <- lineIterator) {
      line.split("(\\s)+").toList.foreach(x => {
        numberOfWords += 1
        frequencyMap += (x.toLowerCase -> (frequencyMap.getOrElse(x.toLowerCase, 0) + 1))
      })
    }
  }
}
