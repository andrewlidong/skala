package presentation

import scalatags.Text.all.*

object SlideHelpers:

  val section = tag("section")

  /** A single slide with a title and arbitrary content fragments. */
  def slide(titleText: String, content: Frag*): Frag =
    section(
      h2(titleText),
      content
    )

  /** A title slide (used for section openers). */
  def titleSlide(titleText: String, subtitleText: String): Frag =
    section(
      h1(titleText),
      h3(subtitleText)
    )

  /** A slide with a Scala code block with syntax highlighting. */
  def codeSlide(titleText: String, codeText: String, notes: Frag*): Frag =
    section(
      h2(titleText),
      pre(
        tag("code")(
          cls := "language-scala",
          attr("data-trim") := "",
          attr("data-noescape") := "",
          codeText
        )
      ),
      notes
    )

  /** A slide with a code block and explanatory text below it. */
  def codeWithExplanation(titleText: String, codeText: String, explanation: Frag*): Frag =
    section(
      h2(titleText),
      pre(
        tag("code")(
          cls := "language-scala",
          attr("data-trim") := "",
          attr("data-noescape") := "",
          codeText
        )
      ),
      div(cls := "fragment", explanation)
    )

  /** A slide with a bulleted list. Items appear one by one. */
  def bulletSlide(titleText: String, items: Frag*): Frag =
    section(
      h2(titleText),
      ul(
        items.map(item => li(cls := "fragment", item))
      )
    )

  /** A slide with two columns using flexbox. */
  def twoColumnSlide(titleText: String, leftContent: Frag, rightContent: Frag): Frag =
    section(
      h2(titleText),
      div(
        style := "display: flex; gap: 2em;",
        div(style := "flex: 1;", leftContent),
        div(style := "flex: 1;", rightContent)
      )
    )

  /** Wraps slides in an outer section for vertical navigation grouping. */
  def verticalStack(slides: Frag*): Frag =
    section(slides)

  /** A blockquote slide for quotations. */
  def quoteSlide(titleText: String, quoteText: String, attribution: String): Frag =
    section(
      h2(titleText),
      tag("blockquote")(quoteText),
      p(em(s"-- $attribution"))
    )

  /** Inline code helper for prose. */
  def ic(text: String): Frag = code(text)
