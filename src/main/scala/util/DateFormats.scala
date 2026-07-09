package util

import java.time.format.DateTimeFormatter

object DateFormats {
  val format: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
}