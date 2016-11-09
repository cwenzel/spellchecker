import scala.io.Source
import java.io.InputStream
import scala.collection.mutable.ListBuffer

object SpellChecker extends App {
  case class Config(file: String = "", debug: Boolean = false)
  
  val frequencyMap = getFrequencyMap
  val dictionary = scala.io.Source.fromInputStream( getClass.getResourceAsStream("/words.txt") ).getLines.toList
  var debugMode = false
  
  (new scopt.OptionParser[Config]("SpellChecker") {
   opt[String]('f', "file").required() action((value, c) => c.copy(file = value)) text("File name to spell check")
   opt[Boolean]('d', "debug") action((value, c) => c.copy(debug = value)) text("Debug mode. Prints back word name and timing info. Accepts Boolean values.")
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
    val firstEdit = chooseBestEdit(edits, frequencyMap)
    if (firstEdit != None)
      return firstEdit
    
    if (word.length < 20) // 20 is the cutoff where moreEditsOfWord gets too slow
      return chooseBestEdit(editsOfEdits(edits), frequencyMap)
    None
  }

  def chooseBestEdit(edits : List[String], frequencyMap : Map[String, Int]) : Option[String]= {
    val res = edits.foldLeft("NO_VALUE_FOUND")((a, b) => {
      if (frequencyMap.getOrElse(a, 0) >= frequencyMap.getOrElse(b, 0)) a else b
    })
    if (res == "NO_VALUE_FOUND") None else Some(res)
  }

  def editsOfEdits(edits : List[String]) = for(e1 <- edits; e2 <-editsOfWord(e1)) yield e2
  
  //All edits that are one edit away from the word
  def editsOfWord(word : String) : List[String] = {
    (deleteEdits(word) ++ insertEdits(word) ++ replaceEdits(word) ++ switchEdits(word)).distinct
  }

  def deleteEdits(word : String) : List[String] = {
    var edits : ListBuffer[String] = ListBuffer()
    for (i <- word.indices) { edits += (word.toList.take(i) ++ word.toList.drop(i + 1)).mkString }
    edits.toList.distinct
  }

  def insertEdits(word : String) : List[String] = {
    var edits : ListBuffer[String] = ListBuffer()
     for (i <- word.indices) { ('a' to 'z').foreach(letter => {
      edits += (word.toList.take(i) ++ List(letter) ++ word.toList.drop(i)).mkString
    })}
    //there are still the "post word" inserts to make
    ('a' to 'z').foreach(letter => { edits += (word + letter) })
    edits.toList.distinct
  }

  def replaceEdits(word : String) : List[String] = {
    var edits : ListBuffer[String] = ListBuffer()
    for (i <- word.indices) { ('a' to 'z').foreach(letter => {
      edits += (word.toList.take(i) ++ List(letter) ++ word.toList.drop(i + 1)).mkString
    })}
    edits.toList.distinct
  }

  def switchEdits(word : String) : List[String] = {
    var edits : ListBuffer[String] = ListBuffer()
    for (i <- word.indices) {
      if (i > 0) {
        val firstHalf = word.toList.take(i)
        val secondHalf = word.toList.drop(i)
        edits += (firstHalf.take(i - 1) ++ List(secondHalf(0)) ++ firstHalf.drop(firstHalf.length - 1) ++ secondHalf.drop(1)).mkString
      }
    }
    edits.toList.distinct
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
