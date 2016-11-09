import org.scalatest._

class SpellCheckerSpec extends FlatSpec {
  "deleteEdits" should "produce all the edits" in {
    val res = SpellChecker.deleteEdits("abcd")
    assert(res == List("bcd", "acd", "abd", "abc"))
  }

  "insertEdits" should "produce all the edits" in {
    val res = SpellChecker.insertEdits("a")
    assert(res == List("aa", "ba", "ca", "da", "ea",
      "fa", "ga", "ha", "ia", "ja", "ka", "la", "ma",
      "na", "oa", "pa", "qa", "ra", "sa", "ta", "ua",
      "va", "wa", "xa", "ya", "za", "ab", "ac", "ad",
      "ae", "af", "ag", "ah", "ai", "aj", "ak", "al",
      "am", "an", "ao", "ap", "aq", "ar", "as", "at",
      "au", "av", "aw", "ax", "ay", "az"))
  }

  "replaceEdits" should "produce all the edits" in {
    val res = SpellChecker.replaceEdits("aa")
    assert(res == List("aa", "ba", "ca", "da", "ea",
      "fa", "ga", "ha", "ia", "ja", "ka", "la", "ma",
      "na", "oa", "pa", "qa", "ra", "sa", "ta", "ua",
      "va", "wa", "xa", "ya", "za", "ab", "ac", "ad",
      "ae", "af", "ag", "ah", "ai", "aj", "ak", "al",
      "am", "an", "ao", "ap", "aq", "ar", "as", "at",
      "au", "av", "aw", "ax", "ay", "az"))
  }

  "switchEdits" should "produce all the edits" in {
    val res = SpellChecker.switchEdits("abcd")
    assert(res == List("bacd", "acbd", "abdc"))
  }

  "chooseBestEdit" should "choose the best edit" in {
    val res = SpellChecker.chooseBestEdit(List("garbage", "word", "words"), Map(("word" -> 10), ("words" -> 2)))
    assert(res == Some("word"))
  }

  "chooseBestEdit" should "return None for garbage" in {
    val res = SpellChecker.chooseBestEdit(List("garbage"), Map(("word" -> 10), ("words" -> 2)))
    assert(res == None)
  }
}

