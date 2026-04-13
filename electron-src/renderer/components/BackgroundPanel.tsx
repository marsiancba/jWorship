import { useState, useEffect, useCallback } from "react";
import { getApi, picUrl, type DirEntry } from "../api";

const api = getApi();

interface BackgroundPanelProps {
  selected: string;
  onSelect: (media: string) => void;
}

export function BackgroundPanel({ selected, onSelect }: BackgroundPanelProps) {
  const [currentPath, setCurrentPath] = useState("");
  const [entries, setEntries] = useState<DirEntry[]>([]);
  const [pictures, setPictures] = useState<string[]>([]);

  const loadDir = useCallback((dirPath: string) => {
    setCurrentPath(dirPath);
    api.listDir(dirPath).then(setEntries).catch(console.error);
    api.listPictures(dirPath).then(setPictures).catch(console.error);
  }, []);

  useEffect(() => {
    loadDir("");
  }, [loadDir]);

  const navigateUp = () => {
    const parent = currentPath.includes("/")
      ? currentPath.substring(0, currentPath.lastIndexOf("/"))
      : "";
    loadDir(parent);
  };

  const navigateInto = (name: string) => {
    const next = currentPath ? `${currentPath}/${name}` : name;
    loadDir(next);
  };

  const folders = entries.filter((e) => e.isDir);

  return (
    <div className="background-panel">
      <div className="bg-toolbar">
        {currentPath && (
          <button className="bg-nav-btn" onClick={navigateUp} title="Späť">
            ⬆
          </button>
        )}
        <span className="bg-path">
          /{currentPath || "pictures"}
        </span>
      </div>
      <div className="bg-content">
        <div className="bg-folders">
          {folders.map((f) => (
            <div
              key={f.name}
              className="bg-folder"
              onClick={() => navigateInto(f.name)}
            >
              📁 {f.name}
            </div>
          ))}
        </div>
        <div className="bg-grid">
          <div
            className={`bg-thumb${selected === "" ? " active" : ""}`}
            onClick={() => onSelect("")}
          >
            <div className="bg-thumb-none">Žiadne</div>
          </div>
          {pictures.map((file) => {
            const fullPath = currentPath ? `${currentPath}/${file}` : file;
            return (
              <div
                key={file}
                className={`bg-thumb${selected === fullPath ? " active" : ""}`}
                onClick={() => onSelect(fullPath)}
              >
                <img src={picUrl(fullPath)} alt={file} />
                <div className="bg-thumb-name">{file}</div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
