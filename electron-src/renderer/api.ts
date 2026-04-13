export interface Song {
  fileName: string;
  title: string;
  title2: string;
  author: string;
  verses: string[];
  format: "json" | "sng" | "txt";
}

export interface DirEntry {
  name: string;
  isDir: boolean;
}

export interface AppAPI {
  listSongs(): Promise<string[]>;
  loadSong(fileName: string): Promise<Song | null>;
  listPictures(subPath: string): Promise<string[]>;
  listDir(subPath: string): Promise<DirEntry[]>;
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
    listPictures: (subPath: string) =>
      fetch(`/api/pictures?path=${encodeURIComponent(subPath)}`).then((r) =>
        r.json()
      ),
    listDir: (subPath: string) =>
      fetch(`/api/pictures/dir?path=${encodeURIComponent(subPath)}`).then(
        (r) => r.json()
      ),
  };
}

export function picUrl(file: string): string {
  return window.api ? `pictures:///${file}` : `/api/pictures/file/${file}`;
}
