\version "2.24.0"
\header {
  title = "<TITLE>"
}

\markup \vspace #1

\layout {
  \context {
    \Score
    \override Dots.X-extent = ##f
  }
  \context {
    \DrumStaff
    \omit Clef
  }
}

\score {
  \new DrumStaff {
    <<
      \new DrumVoice = "main" {
        \numericTimeSignature
        \time 4/4
        \stemDown
        \override NoteHead.extra-offset = #'(0 . -.5)
        \override Stem.extra-offset = #'(0 . -.5)
        \override Beam.extra-offset = #'(0 . -.5)
        \drummode {
          <NOTES>
        }
      }
      \new DrumVoice = "hihat" {
        \stemUp
        \override NoteHead.extra-offset = #'(0 . 1)
        \override Stem.extra-offset = #'(0 . 1)
        \override Beam.extra-offset = #'(0 . 1)
        \drummode {
		  \repeat unfold <BARS> { \repeat unfold 16 { hh16 } }
        }
      }
    >>
  }
}