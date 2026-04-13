import { useState, useEffect, useCallback } from "react";
import { SongList } from "./components/SongList";
import { SongView } from "./components/SongView";
import { ProjectorPreview } from "./components/ProjectorPreview";
import { getSongApi, type Song } from "./api";

const api = getSongApi();

export function App() {
  const [songs, setSongs] = useState<string[]>([]);
  const [search, setSearch] = useState("");
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [song, setSong] = useState<Song | null>(null);
  const [activeVerse, setActiveVerse] = useState<string | null>(null);

  useEffect(() => {
    api.listSongs().then(setSongs).catch(console.error);
  }, []);

  const handleSelectSong = useCallback(async (fileName: string) => {
    setSelectedFile(fileName);
    setActiveVerse(null);
    const loaded = await api.loadSong(fileName);
    setSong(loaded);
  }, []);

  const filtered = songs.filter((s) =>
    s.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div id="app">
      <header>
        <h1>
          jWorship <span className="version">4.2</span>
        </h1>
        <input
          type="text"
          id="search"
          placeholder="Hľadať pieseň..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </header>
      <main>
        <SongList
          songs={filtered}
          selectedFile={selectedFile}
          onSelect={handleSelectSong}
        />
        <SongView
          song={song}
          activeVerse={activeVerse}
          onSelectVerse={setActiveVerse}
        />
        <ProjectorPreview text={activeVerse} />
      </main>
    </div>
  );
}
