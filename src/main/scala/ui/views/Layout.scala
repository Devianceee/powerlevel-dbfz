package ui.views

import scalatags.Text.all._
import scalatags.Text.tags2

object Layout {

  def basePage(pageTitle: String)(content: Modifier*) =
    html(
      head(
        tags2.title(pageTitle),

        meta(charset := "utf-8"),
        meta(
          scalatags.Text.attrs.name := "viewport",
          scalatags.Text.attrs.content := "width=device-width, initial-scale=1"
        ),

        link(rel := "stylesheet", href := "/assets/powerlevel.css"),

        script(src := "https://unpkg.com/htmx.org@1.9.11")
      ),

      body(
        header(
          div(cls := "container")(
            div(cls := "navbar")(

              h1(cls := "logo")(
                a(href := "/")("PowerLevel")
              ),

              tags2.nav(
                ul(
                  li(a(href := "/about")("About")),
                  li(a(href := "/updates")("Updates")),
                  li(a(href := "/api")("API"))
                )
              )
            )
          )
        ),

        tags2.main(cls := "dual")(
          content
        )
      )
    )
}