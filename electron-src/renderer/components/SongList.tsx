interface SongListProps {
  songs: string[];
  selectedFile: string | null;
  onSelect: (fileName: string) => void;
}

function displayName(fileName: string): string {
  return fileName.replace(/\.(json|sng|txt)$/, "");
}

export function SongList({ songs, selectedFile, onSelect }: SongListProps) {
  return (
    <aside id="song-list">
      <h2>Piesne</h2>
      <ul id="songs">
        {songs.map((file) => (
          <li
            key={file}
            className={file === selectedFile ? "active" : ""}
            onClick={() => onSelect(file)}
          >
            {displayName(file)}
          </li>
        ))}
      </ul>
    </aside>
  );
}
