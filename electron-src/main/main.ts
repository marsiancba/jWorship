import { app, BrowserWindow, ipcMain } from "electron";
import * as path from "path";
import { SongStore } from "./songStore";

let mainWindow: BrowserWindow | null = null;
const songStore = new SongStore(path.join(process.cwd(), "songs"));

function createWindow(): void {
  mainWindow = new BrowserWindow({
    width: 900,
    height: 600,
    title: `jWorship 4.2`,
    webPreferences: {
      preload: path.join(__dirname, "..", "renderer", "preload.js"),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });

  mainWindow.loadFile(
    path.join(__dirname, "..", "renderer", "index.html")
  );

  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

app.whenReady().then(() => {
  createWindow();

  ipcMain.handle("songs:list", () => songStore.listSongs());
  ipcMain.handle("songs:load", (_event, fileName: string) =>
    songStore.loadSong(fileName)
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
