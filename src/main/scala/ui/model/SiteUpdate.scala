package ui.model

import java.time.LocalDate

final case class SiteUpdate(
  date: LocalDate,
  `type`: String,
  title: String,
  changes: List[String]
)
