import * as fs from "fs";
import * as path from "path";
import { parseSngFile, parseTxtSongFile } from "./sngParser";

export interface Song {
  fileName: string;
  title: string;
  title2: string;
  author: string;
  verses: string[];
  format: "json" | "sng" | "txt";
}

const SONG_EXTENSIONS = /\.(json|sng|txt)$/i;

export class SongStore {
  private readonly songsDir: string;

  constructor(songsDir: string) {
    this.songsDir = songsDir;
  }

  listSongs(): string[] {
    if (!fs.existsSync(this.songsDir)) {
      return [];
    }
    return fs
      .readdirSync(this.songsDir)
      .filter((f) => SONG_EXTENSIONS.test(f));
  }

  loadSong(fileName: string): Song | null {
    const filePath = path.join(this.songsDir, fileName);
    if (!fs.existsSync(filePath)) {
      return null;
    }

    const ext = path.extname(fileName).toLowerCase();
    try {
      switch (ext) {
        case ".json":
          return this.loadJsonSong(filePath, fileName);
        case ".sng":
          return this.loadSngSong(filePath, fileName);
        case ".txt":
          return this.loadTxtSong(filePath, fileName);
        default:
          return null;
      }
    } catch (err) {
      console.error(`Failed to load song: ${fileName}`, err);
      return null;
    }
  }

  private loadJsonSong(filePath: string, fileName: string): Song {
    const raw = JSON.parse(fs.readFileSync(filePath, "utf-8"));
    return {
      fileName,
      title: raw.title ?? fileName.replace(/\.json$/i, ""),
      title2: raw.title2 ?? "",
      author: raw.author ?? "",
      verses: raw.verses ?? [],
      format: "json",
    };
  }

  private loadSngSong(filePath: string, fileName: string): Song {
    const parsed = parseSngFile(filePath);
    return {
      fileName,
      title: parsed.title || fileName.replace(/\.sng$/i, ""),
      title2: parsed.title2,
      author: parsed.author,
      verses: parsed.verses,
      format: "sng",
    };
  }

  private loadTxtSong(filePath: string, fileName: string): Song {
    const parsed = parseTxtSongFile(filePath, fileName);
    return {
      fileName,
      title: parsed.title || fileName.replace(/\.txt$/i, ""),
      title2: parsed.title2,
      author: parsed.author,
      verses: parsed.verses,
      format: "txt",
    };
  }
}
