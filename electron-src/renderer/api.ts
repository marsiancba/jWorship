export interface Song {
  fileName: string;
  title: string;
  author: string;
  verses: string[];
}

export interface SongAPI {
  listSongs(): Promise<string[]>;
  loadSong(fileName: string): Promise<Song | null>;
}

declare global {
  interface Window {
    api?: SongAPI;
  }
}

export function getSongApi(): SongAPI {
  if (window.api) {
    return window.api;
  }
  return {
    listSongs: () => fetch("/api/songs").then((r) => r.json()),
    loadSong: (fileName: string) =>
      fetch(`/api/songs/${encodeURIComponent(fileName)}`).then((r) =>
        r.json()
      ),
  };
}
