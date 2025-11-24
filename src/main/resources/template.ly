\version "2.24.0"
\header {
  title = "<TITLE>"
    tagline = ##f
    copyright = "Have fun :)"
}

\markup \vspace #1

#(define my-custom-drums-style
  '(
    (bassdrum        default   #f    -5)
    (snare           default   #f     0)
    (hihat           cross     #f     5)
    (closedhihat     cross     "stopped" 7)
    (openhihat       cross     "open"    7)
  ))

\layout {
  \context {
    \Score
    \omit SystemStartBar
    proportionalNotationDuration = #(ly:make-moment 1/16)
    \override SpacingSpanner.strict-note-spacing = ##t
    \override SpacingSpanner.uniform-stretching = ##t
    \override Dots.X-extent = ##f
  }
  \context {
    \DrumStaff
    \omit Clef
    \numericTimeSignature
    \override BarLine.break-visibility = ##(#t #t #f)
    drumStyleTable = #(alist->hash-table my-custom-drums-style)
  }
}

\paper {
  system-system-spacing = #'((basic-distance . 15) (minimum-distance . 10))
  print-footer = ##f
}

% Samples
<SAMPLES>

\score {
  \new DrumStaff
  <<
    % --- TOP VOICE (Hats + Snares) ---
    \new DrumVoice = "top" {
      \voiceOne
      \numericTimeSignature
      \override Beam.positions = #'(5.5 . 5.5)
      \mergeDifferentlyHeadedOn
      \mergeDifferentlyDottedOn
      \drummode {
        <NOTES_TOP>
      }
    }

    % --- BOTTOM VOICE (Kicks + Snares) ---
    \new DrumVoice = "bottom" {
      \voiceTwo
      \override Stem.length-fraction = #1.2
      \mergeDifferentlyHeadedOn
      \mergeDifferentlyDottedOn
      \drummode {
        <NOTES_BOTTOM>
      }
    }
  >>
}