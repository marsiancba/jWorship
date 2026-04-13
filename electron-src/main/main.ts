import { app, BrowserWindow, ipcMain, protocol } from "electron";
import * as path from "path";
import * as fs from "fs";
import { SongStore } from "./songStore";

const isDev = process.env.NODE_ENV === "development";
let mainWindow: BrowserWindow | null = null;
const songStore = new SongStore(path.join(process.cwd(), "songs"));
const picturesDir = path.join(process.cwd(), "pictures");

function listPictures(): string[] {
  if (!fs.existsSync(picturesDir)) return [];
  return fs
    .readdirSync(picturesDir)
    .filter((f) => /\.(png|jpe?g|webp|gif|bmp)$/i.test(f));
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

app.whenReady().then(() => {
  protocol.handle("pictures", (req) => {
    const filePath = path.join(
      picturesDir,
      decodeURIComponent(new URL(req.url).pathname.slice(1))
    );
    return new Response(fs.readFileSync(filePath), {
      headers: { "Content-Type": "image/png" },
    });
  });

  createWindow();

  ipcMain.handle("songs:list", () => songStore.listSongs());
  ipcMain.handle("songs:load", (_event, fileName: string) =>
    songStore.loadSong(fileName)
  );
  ipcMain.handle("pictures:list", () => listPictures());

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
