export interface Song {
  fileName: string;
  title: string;
  author: string;
  verses: string[];
}

export interface AppAPI {
  listSongs(): Promise<string[]>;
  loadSong(fileName: string): Promise<Song | null>;
  listPictures(): Promise<string[]>;
}

declare global {
  interface Window {
    api?: AppAPI;
  }
}

export function getApi(): AppAPI {
  if (window.api) {
    return window.api;
  }
  return {
    listSongs: () => fetch("/api/songs").then((r) => r.json()),
    loadSong: (fileName: string) =>
      fetch(`/api/songs/${encodeURIComponent(fileName)}`).then((r) =>
        r.json()
      ),
    listPictures: () => fetch("/api/pictures").then((r) => r.json()),
  };
}
