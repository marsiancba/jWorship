import { useState, useEffect, useCallback } from "react";
import { SongList } from "./components/SongList";
import { SongView } from "./components/SongView";
import { SettingsPanel } from "./components/SettingsPanel";
import { ScreenPreview } from "./components/ScreenPreview";
import { getSongApi, type Song } from "./api";
import {
  type ScreenSettings,
  type ScreenState,
  DEFAULT_SCREEN_STATE,
  DEFAULT_SCREEN_SETTINGS,
} from "./models/screen";

const api = getSongApi();

type Tab = "songs+bg" | "songs" | "background" | "settings";

export function App() {
  const [songs, setSongs] = useState<string[]>([]);
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [song, setSong] = useState<Song | null>(null);
  const [activeVerse, setActiveVerse] = useState<string | null>(null);
  const [screenSettings, setScreenSettings] = useState<ScreenSettings>(
    DEFAULT_SCREEN_SETTINGS
  );
  const [activeTab, setActiveTab] = useState<Tab>("songs+bg");

  const [prepared, setPrepared] = useState<ScreenState>(DEFAULT_SCREEN_STATE);
  const [live, setLive] = useState<ScreenState>(DEFAULT_SCREEN_STATE);

  useEffect(() => {
    api.listSongs().then(setSongs).catch(console.error);
  }, []);

  useEffect(() => {
    setPrepared((prev) => ({
      ...prev,
      ...screenSettings,
      text: activeVerse ?? "",
    }));
  }, [screenSettings, activeVerse]);

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

  const handleGo = useCallback(() => {
    setLive({ ...prepared });
  }, [prepared]);

  return (
    <div id="app">
      <main>
        <div className="left-panel">
          <nav className="tabs">
            {(
              [
                ["songs+bg", "Piesne + Pozadie"],
                ["songs", "Piesne"],
                ["background", "Pozadie"],
                ["settings", "Nastavenia"],
              ] as [Tab, string][]
            ).map(([id, label]) => (
              <button
                key={id}
                className={activeTab === id ? "active" : ""}
                onClick={() => setActiveTab(id)}
              >
                {label}
              </button>
            ))}
          </nav>

          <div className="left-content">
            {activeTab === "songs+bg" ? (
              <SongsWithSearch
                songs={songs}
                selectedFile={selectedFile}
                song={song}
                activeVerse={activeVerse}
                onSelectSong={handleSelectSong}
                onSelectVerse={setActiveVerse}
              />
            ) : activeTab === "songs" ? (
              <SongsWithSearch
                songs={songs}
                selectedFile={selectedFile}
                song={song}
                activeVerse={activeVerse}
                onSelectSong={handleSelectSong}
                onSelectVerse={setActiveVerse}
              />
            ) : activeTab === "background" ? (
              <div className="placeholder-panel">
                <p>Pozadie — bude implementované</p>
              </div>
            ) : (
              <SettingsPanel
                settings={screenSettings}
                onChange={handleSettingsChange}
              />
            )}
          </div>
        </div>

        <div className="right-panel">
          <ScreenPreview label="Pripravené" screen={prepared} />
          <div className="go-bar">
            <button className="go-button" onClick={handleGo}>
              Na projektor!
            </button>
          </div>
          <ScreenPreview label="Projekcia" screen={live} />
        </div>
      </main>
    </div>
  );
}

function SongsWithSearch({
  songs,
  selectedFile,
  song,
  activeVerse,
  onSelectSong,
  onSelectVerse,
}: {
  songs: string[];
  selectedFile: string | null;
  song: Song | null;
  activeVerse: string | null;
  onSelectSong: (f: string) => void;
  onSelectVerse: (v: string) => void;
}) {
  const [search, setSearch] = useState("");
  const filtered = songs.filter((s) =>
    s.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="songs-with-search">
      <div className="songs-columns">
        <SongList
          songs={filtered}
          selectedFile={selectedFile}
          onSelect={onSelectSong}
        />
        <SongView
          song={song}
          activeVerse={activeVerse}
          onSelectVerse={onSelectVerse}
        />
      </div>
      <div className="search-bar">
        <input
          type="text"
          id="search"
          placeholder="Hľadať pieseň..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>
    </div>
  );
}
