import type { Song } from "../api";

interface SongViewProps {
  song: Song | null;
  selectedVerses: number[];
  onToggleVerse: (index: number, ctrlKey: boolean) => void;
}

export function SongView({
  song,
  selectedVerses,
  onToggleVerse,
}: SongViewProps) {
  if (!song) {
    return (
      <section id="song-view">
        <div id="song-title">Vyber pieseň zo zoznamu</div>
        <div id="verses" />
      </section>
    );
  }

  let title = song.title;
  if (song.title2) title += ` (${song.title2})`;
  if (song.author) title += ` — ${song.author}`;

  return (
    <section id="song-view">
      <div id="song-title">{title}</div>
      <div id="verses">
        {song.verses.map((verse, i) => (
          <div
            key={i}
            className={`verse${selectedVerses.includes(i) ? " active" : ""}`}
            onClick={(e) => onToggleVerse(i, e.ctrlKey || e.metaKey)}
          >
            <div className="verse-label">Verš {i + 1}</div>
            <div>{verse}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
