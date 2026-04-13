import type { Song } from "../api";

interface SongViewProps {
  song: Song | null;
  activeVerse: string | null;
  onSelectVerse: (verse: string) => void;
}

export function SongView({ song, activeVerse, onSelectVerse }: SongViewProps) {
  if (!song) {
    return (
      <section id="song-view">
        <div id="song-title">Vyber pieseň zo zoznamu</div>
        <div id="verses" />
      </section>
    );
  }

  const title = song.title + (song.author ? ` — ${song.author}` : "");

  return (
    <section id="song-view">
      <div id="song-title">{title}</div>
      <div id="verses">
        {song.verses.map((verse, i) => (
          <div
            key={i}
            className={`verse${verse === activeVerse ? " active" : ""}`}
            onClick={() => onSelectVerse(verse)}
          >
            <div className="verse-label">Verš {i + 1}</div>
            <div>{verse}</div>
          </div>
        ))}
      </div>
    </section>
  );
}
