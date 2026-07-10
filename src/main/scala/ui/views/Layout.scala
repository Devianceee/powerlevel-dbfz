package ui.views

import scalatags.Text.all._
import scalatags.Text.tags2

object Layout {
  def basePage(pageTitle: String)(content: Modifier*) =
    html(
      head(
        tags2.title(pageTitle),
        meta(charset                   := "utf-8"),
        meta(scalatags.Text.attrs.name := "viewport", scalatags.Text.attrs.content := "width=device-width, initial-scale=1"),
        link(rel                       := "preconnect", href                       := "https://fonts.googleapis.com"),
        link(rel := "preconnect", href := "https://fonts.gstatic.com", crossorigin := "anonymous"),
        link(rel                       := "stylesheet", href                       := "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"),
        link(rel                       := "stylesheet", href                       := "/assets/powerlevel.css"),
        script(src                     := "https://unpkg.com/htmx.org@1.9.11")
      ),
      body(
        header(
          div(cls := "header-container")(
            h1(cls := "logo")(
              a(href := "/")("PowerLevel")
            ),
            tags2.nav(
              ul(
                li(a(href := "/about")("About")),
                li(a(href := "/updates")("Updates")),
                li(a(href := "/api")("API")),
                li(a(href := "/latest")("Latest matches"))
              )
            )
          )
        ),
        tags2.main(cls := "dual")(content),
        footer(
          div(cls := "container footer-content")(
            p(s"© ${java.time.Year.now().getValue} PowerLevel. All rights reserved."),
            tags2.nav(cls := "footer-nav")(
              ul(
                li(a(href := "https://github.com/Devianceee/powerlevel-dbfz", target := "_blank", rel := "noopener noreferrer")("GitHub")),
                li(a(href := "https://x.com/Deviance___", target := "_blank", rel := "noopener noreferrer")("Twitter (X)")),
                li(a(href := "/about")("About"))
              )
            )
          )
        )
      )
    )
}
