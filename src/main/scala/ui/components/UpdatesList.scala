package ui.components

import scalatags.Text.all.*
import ui.model.SiteUpdate

object UpdatesList {
  def view(updates: List[SiteUpdate]) =
    div(cls := "updates")(
      updates.map(update)*
    )

  private def update(u: SiteUpdate) =
    div(cls := "card")(
      h2(u.title),
      p(cls := "update-date")(
        s"${u.date} (${u.`type`})"
      ),
      ul(
        u.changes.map(li(_))*
      )
    )
}
