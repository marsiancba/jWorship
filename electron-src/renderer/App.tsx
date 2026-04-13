import { useState, useEffect, useCallback, useRef } from "react";
import { SongList } from "./components/SongList";
import { SongView } from "./components/SongView";
import { SongToolbar } from "./components/SongToolbar";
import { SettingsPanel } from "./components/SettingsPanel";
import { BackgroundPanel } from "./components/BackgroundPanel";
import { ScreenPreview } from "./components/ScreenPreview";
import { getApi, type Song } from "./api";
import {
  type ScreenSettings,
  type ScreenState,
  DEFAULT_SCREEN_STATE,
  DEFAULT_SCREEN_SETTINGS,
} from "./models/screen";

const api = getApi();

type Tab = "songs+bg" | "songs" | "background" | "settings";

export function App() {
  const [songs, setSongs] = useState<string[]>([]);
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [song, setSong] = useState<Song | null>(null);
  const [selectedVerses, setSelectedVerses] = useState<number[]>([]);
  const [screenSettings, setScreenSettings] = useState<ScreenSettings>(
    DEFAULT_SCREEN_SETTINGS
  );
  const [backgroundMedia, setBackgroundMedia] = useState("");
  const [activeTab, setActiveTab] = useState<Tab>("songs+bg");

  const [prepared, setPrepared] = useState<ScreenState>(DEFAULT_SCREEN_STATE);
  const [live, setLive] = useState<ScreenState>(DEFAULT_SCREEN_STATE);

  const [rightWidth, setRightWidth] = useState(300);
  const resizing = useRef(false);

  useEffect(() => {
    api.listSongs().then(setSongs).catch(console.error);
  }, []);

  const verseText = song
    ? selectedVerses.map((i) => song.verses[i]).join("\n\n")
    : "";

  useEffect(() => {
    setPrepared((prev) => ({
      ...prev,
      ...screenSettings,
      backgroundMedia,
      text: verseText,
    }));
  }, [screenSettings, verseText, backgroundMedia]);

  const handleSelectSong = useCallback(async (fileName: string) => {
    setSelectedFile(fileName);
    setSelectedVerses([]);
    const loaded = await api.loadSong(fileName);
    setSong(loaded);
  }, []);

  const handleToggleVerse = useCallback(
    (index: number, ctrlKey: boolean) => {
      setSelectedVerses((prev) => {
        if (ctrlKey) {
          return prev.includes(index)
            ? prev.filter((i) => i !== index)
            : [...prev, index].sort((a, b) => a - b);
        }
        return prev.length === 1 && prev[0] === index ? [] : [index];
      });
    },
    []
  );

  const handleSettingsChange = useCallback(
    (patch: Partial<ScreenSettings>) => {
      setScreenSettings((prev) => ({ ...prev, ...patch }));
    },
    []
  );

  const handleGo = useCallback(() => {
    setLive({ ...prepared });
  }, [prepared]);

  const handleResizeStart = useCallback(
    (e: React.MouseEvent) => {
      e.preventDefault();
      resizing.current = true;
      const startX = e.clientX;
      const startW = rightWidth;

      const onMove = (ev: MouseEvent) => {
        if (!resizing.current) return;
        const delta = startX - ev.clientX;
        setRightWidth(Math.max(200, Math.min(600, startW + delta)));
      };
      const onUp = () => {
        resizing.current = false;
        document.removeEventListener("mousemove", onMove);
        document.removeEventListener("mouseup", onUp);
      };
      document.addEventListener("mousemove", onMove);
      document.addEventListener("mouseup", onUp);
    },
    [rightWidth]
  );

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
              <div className="split-vertical">
                <SongsWithSearch
                  songs={songs}
                  selectedFile={selectedFile}
                  song={song}
                  selectedVerses={selectedVerses}
                  onSelectSong={handleSelectSong}
                  onToggleVerse={handleToggleVerse}
                  settings={screenSettings}
                  onSettingsChange={handleSettingsChange}
                />
                <BackgroundPanel
                  selected={backgroundMedia}
                  onSelect={setBackgroundMedia}
                />
              </div>
            ) : activeTab === "songs" ? (
              <SongsWithSearch
                songs={songs}
                selectedFile={selectedFile}
                song={song}
                selectedVerses={selectedVerses}
                onSelectSong={handleSelectSong}
                onToggleVerse={handleToggleVerse}
                settings={screenSettings}
                onSettingsChange={handleSettingsChange}
              />
            ) : activeTab === "background" ? (
              <BackgroundPanel
                selected={backgroundMedia}
                onSelect={setBackgroundMedia}
              />
            ) : (
              <SettingsPanel
                settings={screenSettings}
                onChange={handleSettingsChange}
              />
            )}
          </div>
        </div>

        <div className="resize-handle" onMouseDown={handleResizeStart} />

        <div className="right-panel" style={{ width: rightWidth }}>
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
  selectedVerses,
  onSelectSong,
  onToggleVerse,
  settings,
  onSettingsChange,
}: {
  songs: string[];
  selectedFile: string | null;
  song: Song | null;
  selectedVerses: number[];
  onSelectSong: (f: string) => void;
  onToggleVerse: (index: number, ctrlKey: boolean) => void;
  settings: ScreenSettings;
  onSettingsChange: (patch: Partial<ScreenSettings>) => void;
}) {
  const [search, setSearch] = useState("");
  const filtered = songs.filter((s) =>
    s.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="songs-with-search">
      <SongToolbar settings={settings} onChange={onSettingsChange} />
      <div className="songs-columns">
        <SongList
          songs={filtered}
          selectedFile={selectedFile}
          onSelect={onSelectSong}
        />
        <SongView
          song={song}
          selectedVerses={selectedVerses}
          onToggleVerse={onToggleVerse}
        />
      </div>
      <div className="search-bar">
        <input
          type="text"
          placeholder="Hľadať pieseň..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>
    </div>
  );
}
