import * as http from "http";
import * as fs from "fs";
import * as path from "path";
import { SongStore } from "./main/songStore";

const PORT = 3000;
const DIST_DIR = path.join(__dirname, "renderer");
const songStore = new SongStore(path.join(process.cwd(), "songs"));

const MIME: Record<string, string> = {
  ".html": "text/html",
  ".css": "text/css",
  ".js": "application/javascript",
  ".json": "application/json",
};

const server = http.createServer((req, res) => {
  if (req.url?.startsWith("/api/")) {
    handleApi(req, res);
    return;
  }

  const filePath = path.join(
    DIST_DIR,
    req.url === "/" ? "index.html" : req.url ?? "index.html"
  );

  const ext = path.extname(filePath);
  const contentType = MIME[ext] || "application/octet-stream";

  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(404);
      res.end("Not found");
      return;
    }
    res.writeHead(200, { "Content-Type": contentType });
    res.end(data);
  });
});

function handleApi(req: http.IncomingMessage, res: http.ServerResponse): void {
  res.setHeader("Content-Type", "application/json");

  if (req.url === "/api/songs") {
    res.end(JSON.stringify(songStore.listSongs()));
    return;
  }

  const loadMatch = req.url?.match(/^\/api\/songs\/(.+)$/);
  if (loadMatch) {
    const song = songStore.loadSong(decodeURIComponent(loadMatch[1]));
    res.end(JSON.stringify(song));
    return;
  }

  res.writeHead(404);
  res.end(JSON.stringify({ error: "not found" }));
}

server.listen(PORT, () => {
  console.log(`jWorship dev server running at http://localhost:${PORT}`);
});
