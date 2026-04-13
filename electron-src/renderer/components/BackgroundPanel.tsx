import { useState, useEffect } from "react";
import { getApi } from "../api";

const api = getApi();

function picUrl(file: string): string {
  const isElectron = Boolean(window.api);
  return isElectron
    ? `pictures:///${file}`
    : `/api/pictures/file/${file}`;
}

interface BackgroundPanelProps {
  selected: string;
  onSelect: (media: string) => void;
}

export function BackgroundPanel({ selected, onSelect }: BackgroundPanelProps) {
  const [pictures, setPictures] = useState<string[]>([]);

  useEffect(() => {
    api.listPictures().then(setPictures).catch(console.error);
  }, []);

  return (
    <div className="background-panel">
      <div className="bg-grid">
        <div
          className={`bg-thumb${selected === "" ? " active" : ""}`}
          onClick={() => onSelect("")}
        >
          <div className="bg-thumb-none">Žiadne</div>
        </div>
        {pictures.map((file) => (
          <div
            key={file}
            className={`bg-thumb${selected === file ? " active" : ""}`}
            onClick={() => onSelect(file)}
          >
            <img src={picUrl(file)} alt={file} />
            <div className="bg-thumb-name">{file}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
