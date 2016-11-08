import scala.io.Source
import java.io.InputStream
import scala.collection.mutable.ListBuffer

object SpellChecker extends App {
  case class Config(file: String = "")
  
  val frequencyMap = getFrequencyMap
  val dictionary = scala.io.Source.fromInputStream( getClass.getResourceAsStream("/words.txt") ).getLines.toList
  
  (new scopt.OptionParser[Config]("SpellChecker") {
   opt[String]('f', "file").required() action((value, c) => c.copy(file = value)) text("File name to spell check")
  }).parse(args, Config()).map({ config =>
    readFile(config.file)
  }).getOrElse(sys.exit(-1))

  def readFile(fileName : String) : Boolean = {
   try {
     for (line <- Source.fromFile(fileName).getLines()) {
       timeProfile({ spellCheckWord(line) })
     }
   }
   catch {
    case ex: Exception => println("Error, unable to read file")
    return false
   }
   return true
  }

  def spellCheckWord(word : String) = {
    dictionary.find((a) => a == word) match {
      case Some(_) => println(word + ": CORRECT")
      case None =>  {
        val edits = editsOfWord(word)
        // it takes too long to calculate moreEditsOfWord() on a super long word
        val edits2 = if (word.length < 20) moreEditsOfWord(word) else List()

        chooseBestEdit(edits ::: edits2) match {
          case Some(w) => println("YOU SAID: " + word + ". DID YOU MEAN? " + w)
          case None => println(word + ": INCORRECT")
        }
      }
    }
  }
  
  def moreEditsOfWord(word : String) = for(e1 <- editsOfWord(word); e2 <-editsOfWord(e1)) yield e2

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

  def getFrequencyMap : Map[String, Int] = {
    val lineIterator = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/sherlock_holmes.txt")).getLines
    var fMap : Map[String, Int] = Map()
    for (line <- lineIterator; x <- line.split("(\\s)+").toList) {
      fMap += (x.toLowerCase -> (fMap.getOrElse(x.toLowerCase, 0) + 1))
    }
    fMap
  }

  def timeProfile[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) / 1000000000.0  + "s")
    result
  }
}
