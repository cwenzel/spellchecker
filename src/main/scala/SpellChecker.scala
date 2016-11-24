import scala.io.Source
import java.io.InputStream
import scala.collection.mutable.ListBuffer

object SpellChecker extends App {
  case class Config(file: String = "", debug: Boolean = false)
  
  val frequencyMap = getFrequencyMap
  val dictionary = scala.io.Source.fromInputStream( getClass.getResourceAsStream("/words.txt") ).getLines.toList
  var debugMode = false
  
  (new scopt.OptionParser[Config]("SpellChecker") {
   opt[String]('f', "file").required() action((value, c) => c.copy(file = value)) text("Required. File name to spell check")
   opt[Boolean]('d', "debug") action((value, c) => c.copy(debug = value)) text("Optional. Default is false. Debug mode that prints back word name and timing info. Accepts a boolean value.")
  }).parse(args, Config()).map({ config => {
      debugMode = config.debug
      readFile(config.file)
    }
  }).getOrElse(sys.exit(-1))

  def readFile(fileName : String) {
   try {
     for (line <- Source.fromFile(fileName).getLines()) {
       if (debugMode)
         timeProfile({ spellCheckWord(line.toLowerCase) })
       else
         spellCheckWord(line.toLowerCase)
     }
   }
   catch {
    case ex: Exception => println("Error, unable to read file")
   }
  }

  def spellCheckWord(word : String) = {
    dictionary.find((a) => a == word) match {
      case Some(_) => printOutput(word, "CORRECT")
      case None =>  {
        makeEditsAndChoose(word) match {
          case Some(w) => if (debugMode) println(word + ". DID YOU MEAN? " + w) else println(w)
          case None => printOutput(word, "INCORRECT")
        }
      }
    }
  }

  def printOutput(word : String, result : String) {
     if (debugMode)
       println(word + ": " + result)
     else
       println(result)
  }

  def makeEditsAndChoose(word : String) : Option[String] = {
    val edits = editsOfWord(word)
    chooseBestEdit(edits, frequencyMap).orElse(if (word.length < 20) chooseBestEdit(editsOfEdits(edits), frequencyMap) else None)
  }

  def chooseBestEdit(edits : List[String], frequencyMap : Map[String, Int]) : Option[String]= {
    val noneSeed: Option[String] = None
    edits.map(Some(_)).foldLeft(noneSeed)((a, b) => {
      val aFreq = a.map(frequencyMap.getOrElse(_, 0)).getOrElse(0)
      val bFreq = b.map(frequencyMap.getOrElse(_, 0)).getOrElse(0)
      if (aFreq >= bFreq) a else b
    })
  }

  def editsOfEdits(edits : List[String]) = for(e1 <- edits; e2 <-editsOfWord(e1)) yield e2

  //All edits that are one edit away from the word
  def editsOfWord(word : String) : List[String] = {
    (deleteEdits(word) ++ insertEdits(word) ++ replaceEdits(word) ++ switchEdits(word)).distinct
  }

  def deleteEdits(word : String) : List[String] = word.indices.map({i => word.take(i) ++ word.drop(i + 1)}).toList

  def insertEdits(word : String) : List[String] = {
    val preWordEdits = for {
      i <- word.indices.toList
      letter <- ('a' to 'z')
    } yield (word.take(i) ++ List(letter) ++ word.drop(i))

    (preWordEdits ++ ('a' to 'z').map({letter => word ++ List(letter)}))
  }

  def replaceEdits(word : String) : List[String] = {
    for {
      i <- word.indices.toList
      letter <- ('a' to 'z')
    } yield (word.take(i) ++ List(letter) ++ word.drop(i + 1))
  }

  def switchEdits(word : String) : List[String] = {
    for {
       i <- word.indices.toList
       if (i > 0)
    } yield {
        val firstHalf = word.take(i)
        val secondHalf = word.drop(i)
        firstHalf.take(i - 1) ++ List(secondHalf(0)) ++ firstHalf.drop(firstHalf.length - 1) ++ secondHalf.drop(1)
    }
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
    println(" ")
    result
  }
}
