import { contextBridge, ipcRenderer } from "electron";

contextBridge.exposeInMainWorld("api", {
  listSongs: (): Promise<string[]> => ipcRenderer.invoke("songs:list"),
  loadSong: (fileName: string) => ipcRenderer.invoke("songs:load", fileName),
  listPictures: (subPath: string): Promise<string[]> =>
    ipcRenderer.invoke("pictures:list", subPath),
  listDir: (subPath: string) =>
    ipcRenderer.invoke("pictures:listDir", subPath),
});
