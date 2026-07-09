package domain.enums

import enumeratum.*

sealed abstract class DbfzCharacter(
  val id: Int,
  val displayName: String
) extends EnumEntry {
  lazy val imagePath: String = s"/assets/images/character_icons/${entryNameToFileName}.png"

  private def entryNameToFileName: String =
    entryName
      .replaceAll("([a-z])([A-Z0-9])", "$1_$2")
      .replaceAll("([0-9])([A-Z])", "$1_$2")
      .toLowerCase
}

object DbfzCharacter extends Enum[DbfzCharacter] {
  case object GokuSSJ           extends DbfzCharacter(0, "Goku (SSJ)")
  case object VegetaSSJ         extends DbfzCharacter(1, "Vegeta (SSJ)")
  case object Piccolo           extends DbfzCharacter(2, "Piccolo")
  case object TeenGohan         extends DbfzCharacter(3, "Teen Gohan")
  case object Frieza            extends DbfzCharacter(4, "Frieza")
  case object Ginyu             extends DbfzCharacter(5, "Ginyu")
  case object Trunks            extends DbfzCharacter(6, "Trunks")
  case object Cell              extends DbfzCharacter(7, "Cell")
  case object Android18         extends DbfzCharacter(8, "Android 18")
  case object Gotenks           extends DbfzCharacter(9, "Gotenks")
  case object Krillin           extends DbfzCharacter(10, "Krillin")
  case object KidBuu            extends DbfzCharacter(11, "Kid Buu")
  case object MajinBoo          extends DbfzCharacter(12, "Majin Buu")
  case object Nappa             extends DbfzCharacter(13, "Nappa")
  case object Android16         extends DbfzCharacter(14, "Android 16")
  case object Yamcha            extends DbfzCharacter(15, "Yamcha")
  case object Tien              extends DbfzCharacter(16, "Tien")
  case object AdultGohan        extends DbfzCharacter(17, "Adult Gohan")
  case object Hit               extends DbfzCharacter(18, "Hit")
  case object GokuSSGSS         extends DbfzCharacter(19, "Goku (SSGSS)")
  case object VegetaSSGSS       extends DbfzCharacter(20, "Vegeta (SSGSS)")
  case object Beerus            extends DbfzCharacter(21, "Beerus")
  case object GokuBlack         extends DbfzCharacter(22, "Goku Black")
  case object Android21         extends DbfzCharacter(23, "Android 21")
  case object Android21Evil     extends DbfzCharacter(24, "Android 21 (Evil)")
  case object Android21Good     extends DbfzCharacter(25, "Android 21 (Good)")
  case object Goku              extends DbfzCharacter(26, "Goku")
  case object Vegeta            extends DbfzCharacter(27, "Vegeta")
  case object Broly             extends DbfzCharacter(28, "Broly")
  case object Zamasu            extends DbfzCharacter(29, "Zamasu")
  case object Bardock           extends DbfzCharacter(30, "Bardock")
  case object VegitoSSGSS       extends DbfzCharacter(31, "Vegito (SSGSS)")
  case object Android17         extends DbfzCharacter(32, "Android 17")
  case object Cooler            extends DbfzCharacter(33, "Cooler")
  case object Jiren             extends DbfzCharacter(34, "Jiren")
  case object Videl             extends DbfzCharacter(35, "Videl")
  case object GokuGT            extends DbfzCharacter(36, "Goku GT")
  case object Janemba           extends DbfzCharacter(37, "Janemba")
  case object GogetaSSGSS       extends DbfzCharacter(38, "Gogeta (SSGSS)")
  case object BrolyDBS          extends DbfzCharacter(39, "Broly (DBS)")
  case object Kefla             extends DbfzCharacter(40, "Kefla")
  case object GokuUltraInstinct extends DbfzCharacter(41, "Goku (Ultra Instinct)")
  case object MasterRoshi       extends DbfzCharacter(42, "Master Roshi")
  case object SuperBaby2        extends DbfzCharacter(43, "Super Baby 2")
  case object GogetaSS4         extends DbfzCharacter(44, "Gogeta (SS4)")
  case object Android21LabCoat  extends DbfzCharacter(45, "Android 21 (Lab Coat)")
  case object GokuSS4           extends DbfzCharacter(46, "Goku (SS4)")

  override val values: IndexedSeq[DbfzCharacter] = findValues

  def fromId(id: Int): Option[DbfzCharacter] = values.find(_.id == id)
}
