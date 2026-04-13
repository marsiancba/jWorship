import { useState, useEffect, useCallback } from "react";
import { SongList } from "./components/SongList";
import { SongView } from "./components/SongView";
import { ProjectorPreview } from "./components/ProjectorPreview";
import { SettingsPanel } from "./components/SettingsPanel";
import { getSongApi, type Song } from "./api";
import {
  type ScreenSettings,
  DEFAULT_SCREEN_SETTINGS,
} from "./models/screen";

const api = getSongApi();

type Tab = "songs" | "settings";

export function App() {
  const [songs, setSongs] = useState<string[]>([]);
  const [search, setSearch] = useState("");
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [song, setSong] = useState<Song | null>(null);
  const [activeVerse, setActiveVerse] = useState<string | null>(null);
  const [screenSettings, setScreenSettings] = useState<ScreenSettings>(
    DEFAULT_SCREEN_SETTINGS
  );
  const [activeTab, setActiveTab] = useState<Tab>("songs");

  useEffect(() => {
    api.listSongs().then(setSongs).catch(console.error);
  }, []);

  const handleSelectSong = useCallback(async (fileName: string) => {
    setSelectedFile(fileName);
    setActiveVerse(null);
    const loaded = await api.loadSong(fileName);
    setSong(loaded);
  }, []);

  const handleSettingsChange = useCallback(
    (patch: Partial<ScreenSettings>) => {
      setScreenSettings((prev) => ({ ...prev, ...patch }));
    },
    []
  );

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
        <nav className="tabs">
          <button
            className={activeTab === "songs" ? "active" : ""}
            onClick={() => setActiveTab("songs")}
          >
            Piesne
          </button>
          <button
            className={activeTab === "settings" ? "active" : ""}
            onClick={() => setActiveTab("settings")}
          >
            Nastavenia
          </button>
        </nav>
      </header>
      <main>
        {activeTab === "songs" ? (
          <>
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
          </>
        ) : (
          <SettingsPanel
            settings={screenSettings}
            onChange={handleSettingsChange}
          />
        )}
        <ProjectorPreview text={activeVerse} settings={screenSettings} />
      </main>
    </div>
  );
}
