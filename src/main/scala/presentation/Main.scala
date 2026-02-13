package presentation

import scalatags.Text.all.*
import scalatags.Text.tags2
import java.nio.file.{Files, Paths}

@main def generate(): Unit =
  val outputDir = Paths.get("output")
  if !Files.exists(outputDir) then Files.createDirectories(outputDir)

  val htmlContent = buildPresentation()
  val outputPath = outputDir.resolve("presentation.html")
  Files.writeString(outputPath, htmlContent)
  println(s"Presentation generated at $outputPath")

def buildPresentation(): String =
  val doc = doctype("html")(
    html(lang := "en",
      head(
        meta(charset := "utf-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        tags2.title("Scala: A Deep Dive"),
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.1.0/reveal.min.css"
        ),
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.1.0/theme/dracula.min.css"
        ),
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.1.0/plugin/highlight/monokai.min.css"
        ),
        tag("style")(raw(customCss))
      ),
      body(
        div(cls := "reveal",
          div(cls := "slides",
            Slides.all
          )
        ),
        script(src := "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.1.0/reveal.min.js"),
        script(src := "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.1.0/plugin/highlight/highlight.min.js"),
        script(src := "https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.1.0/plugin/notes/notes.min.js"),
        script(raw("""
          Reveal.initialize({
            hash: true,
            slideNumber: true,
            transition: 'slide',
            plugins: [ RevealHighlight, RevealNotes ],
            highlight: {
              highlightOnLoad: true
            }
          });
        """))
      )
    )
  )
  doc.render

val customCss: String =
  """
  .reveal pre code {
    max-height: 520px;
    font-size: 0.52em;
    line-height: 1.35;
    padding: 1em;
  }
  .reveal h1 { font-size: 2.0em; }
  .reveal h2 { font-size: 1.3em; margin-bottom: 0.5em; }
  .reveal ul { font-size: 0.8em; }
  .reveal li { margin-bottom: 0.3em; }
  .reveal .slides section {
    text-align: left;
  }
  .reveal .slides section h1,
  .reveal .slides section h2,
  .reveal .slides section h3 {
    text-align: center;
  }
  .reveal table {
    font-size: 0.7em;
    margin: 0 auto;
  }
  .reveal table th,
  .reveal table td {
    padding: 0.3em 0.8em;
    border: 1px solid #666;
  }
  .reveal blockquote {
    font-size: 0.9em;
    padding: 1em;
    width: 90%;
  }
  """
