interface JWorshipAPI {
  listSongs(): Promise<string[]>;
  loadSong(fileName: string): Promise<{
    fileName: string;
    title: string;
    author: string;
    verses: string[];
  } | null>;
}

interface Window {
  api: JWorshipAPI;
}
