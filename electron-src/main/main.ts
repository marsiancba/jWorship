import { app, BrowserWindow, ipcMain, protocol } from "electron";
import * as path from "path";
import * as fs from "fs";
import { SongStore } from "./songStore";

const isDev = process.env.NODE_ENV === "development";
let mainWindow: BrowserWindow | null = null;
const songStore = new SongStore(path.join(process.cwd(), "songs"));
const picturesDir = path.join(process.cwd(), "pictures");

const IMAGE_RE = /\.(png|jpe?g|webp|gif|bmp)$/i;

interface DirEntry {
  name: string;
  isDir: boolean;
}

function listDir(subPath: string): DirEntry[] {
  const full = path.join(picturesDir, subPath);
  if (!fs.existsSync(full)) return [];
  return fs
    .readdirSync(full, { withFileTypes: true })
    .filter((d) => d.isDirectory() || IMAGE_RE.test(d.name))
    .map((d) => ({ name: d.name, isDir: d.isDirectory() }))
    .sort((a, b) => {
      if (a.isDir !== b.isDir) return a.isDir ? -1 : 1;
      return a.name.localeCompare(b.name);
    });
}

function listPictures(subPath: string): string[] {
  const full = path.join(picturesDir, subPath);
  if (!fs.existsSync(full)) return [];
  return fs
    .readdirSync(full)
    .filter((f) => IMAGE_RE.test(f));
}

function createWindow(): void {
  mainWindow = new BrowserWindow({
    width: 1100,
    height: 700,
    title: `jWorship 4.2`,
    webPreferences: {
      preload: path.join(__dirname, "..", "renderer", "preload.js"),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });

  if (isDev) {
    mainWindow.loadURL("http://localhost:3000");
  } else {
    mainWindow.loadFile(
      path.join(__dirname, "..", "renderer", "index.html")
    );
  }

  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

function getMimeType(filePath: string): string {
  const ext = path.extname(filePath).toLowerCase();
  const mimes: Record<string, string> = {
    ".png": "image/png",
    ".jpg": "image/jpeg",
    ".jpeg": "image/jpeg",
    ".webp": "image/webp",
    ".gif": "image/gif",
    ".bmp": "image/bmp",
  };
  return mimes[ext] || "application/octet-stream";
}

app.whenReady().then(() => {
  protocol.handle("pictures", (req) => {
    const urlPath = decodeURIComponent(new URL(req.url).pathname);
    const filePath = path.join(picturesDir, urlPath.startsWith("/") ? urlPath.slice(1) : urlPath);
    if (!fs.existsSync(filePath)) {
      return new Response("Not found", { status: 404 });
    }
    return new Response(fs.readFileSync(filePath), {
      headers: { "Content-Type": getMimeType(filePath) },
    });
  });

  createWindow();

  ipcMain.handle("songs:list", () => songStore.listSongs());
  ipcMain.handle("songs:load", (_event, fileName: string) =>
    songStore.loadSong(fileName)
  );
  ipcMain.handle("pictures:list", (_event, subPath: string) =>
    listPictures(subPath || "")
  );
  ipcMain.handle("pictures:listDir", (_event, subPath: string) =>
    listDir(subPath || "")
  );

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});
