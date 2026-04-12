# AGENTS.md

## Project overview

jWorship is a church worship presentation desktop application originally built in Java 8 with JavaFX and Swing. The project goal is to **migrate from JavaFX/Java to TypeScript and Electron**.

### Legacy Java codebase (reference only)
- Build: Maven (`pom.xml`), source in `src/` (flat layout, not `src/main/java/`)
- Main entry: `sk.calvary.worship_fx.App` (JavaFX app)
- Dependencies: `javax.json:1.0.4`, `vlcj:3.10.1` (VLC media player bindings)
- The Java source is kept for reference during migration; you do not need to compile or run it.

### Key features to migrate
- Song library management (JSON-based storage in `songs/` directory)
- Lyrics display with verse selection
- Background image/video support (from `pictures/` directory)
- Prepared/Live screen workflow with projector output
- Playlists
- Screen transitions (fade, fade-out-fade-in)
- Settings persistence (JSON in `settings/` directory)
- UI is in Slovak language

### Electron/TypeScript codebase
- Source: `electron-src/` (main process in `main/`, renderer in `renderer/`)
- Build output: `dist/`
- Entry point: `dist/main/main.js` (Electron main), `dist/dev-server.js` (web dev server)
- See `package.json` scripts for build/lint/dev/test commands.

## Cursor Cloud specific instructions

### Commands
- **Build:** `npm run build`
- **Lint:** `npm run lint`
- **Dev (Electron):** `npm run dev` — builds then launches the native Electron desktop app
- **Dev (web):** `npm run dev:web` — builds then starts a local HTTP server at http://localhost:3000
- **Test:** `npm test`

### Running Electron in this environment
- Always pass `--no-sandbox` when running Electron: `DISPLAY=:1 npx electron . --no-sandbox`
- DBus errors in the log (`Failed to connect to the bus`) are cosmetic and harmless in a container/VM.
- SSL handshake errors for background Chromium network calls are caused by egress restrictions and are harmless.
- The `npm run dev` script handles the `--no-sandbox` flag is not included in the package.json script — run `DISPLAY=:1 npx electron . --no-sandbox` directly, or use `npm run dev:web` for browser-based dev.

### Renderer code gotcha
- The renderer runs in Electron's sandboxed browser context with `contextIsolation: true` and `nodeIntegration: false`.
- Do NOT use `export` statements or ES module syntax in renderer `.ts` files — TypeScript compiles to CommonJS which uses `exports` (undefined in browser context).
- Do NOT declare global variables that collide with names exposed by the preload script (e.g., `api` is exposed via `contextBridge`). Use unique names like `songApi`.
- Type declarations for `window.api` live in `electron-src/renderer/global.d.ts`.

### Node.js / npm
- Node.js v22 via nvm. Project uses npm (`package-lock.json`).
- Electron binary (~110 MB) downloads from `release-assets.githubusercontent.com` during `npm install`. If that domain is blocked, use `npm install --ignore-scripts` and then `node node_modules/electron/install.js` separately.

### Display / GUI testing
- Virtual display (Xtigervnc) is on DISPLAY=:1.
- For web-mode dev (`npm run dev:web`), open http://localhost:3000 in Chrome.

### Project structure
```
electron-src/
  main/          # Electron main process (Node.js)
    main.ts      # App entry, window creation, IPC handlers
    songStore.ts # Song file loading (JSON and legacy .sng formats)
  renderer/      # Electron renderer process (DOM/browser)
    global.d.ts  # Type declarations for window.api
    index.html   # App shell
    style.css    # Styles
    renderer.ts  # UI logic, song list, verse selection, projector preview
    preload.ts   # Context bridge (Electron preload script)
  dev-server.ts  # Lightweight HTTP server for browser-based development
src/             # Legacy Java source (reference only)
songs/           # Song data directory (JSON files)
```
