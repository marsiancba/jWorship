import * as fs from "fs";
import * as path from "path";

export interface Song {
  fileName: string;
  title: string;
  author: string;
  verses: string[];
}

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
      .filter((f) => f.endsWith(".json") || f.endsWith(".sng"));
  }

  loadSong(fileName: string): Song | null {
    const filePath = path.join(this.songsDir, fileName);
    if (!fs.existsSync(filePath)) {
      return null;
    }

    try {
      if (fileName.endsWith(".json")) {
        return this.loadJsonSong(filePath, fileName);
      }
      return this.loadLegacySong(filePath, fileName);
    } catch {
      console.error(`Failed to load song: ${fileName}`);
      return null;
    }
  }

  private loadJsonSong(filePath: string, fileName: string): Song {
    const raw = JSON.parse(fs.readFileSync(filePath, "utf-8"));
    return {
      fileName,
      title: raw.title ?? fileName.replace(/\.json$/, ""),
      author: raw.author ?? "",
      verses: raw.verses ?? [],
    };
  }

  private loadLegacySong(filePath: string, fileName: string): Song {
    const content = fs.readFileSync(filePath, "utf-8");
    const lines = content.split(/\r?\n/);
    const title = lines[0] ?? fileName;
    const verses = content.split(/\n\n+/).filter(Boolean);
    return {
      fileName,
      title,
      author: "",
      verses,
    };
  }
}
