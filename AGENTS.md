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
- **Build:** `npm run build` — compiles TypeScript and copies static assets to `dist/`
- **Lint:** `npm run lint` — runs ESLint on `electron-src/**/*.ts`
- **Dev (web):** `npm run dev:web` — builds then starts a local HTTP server at http://localhost:3000
- **Dev (Electron):** `npm run dev` — builds then launches Electron (requires Electron binary; see caveat below)
- **Test:** `npm test`

### Electron binary download caveat
Electron's postinstall script downloads a ~110 MB binary from `release-assets.githubusercontent.com`. If that domain is blocked by egress restrictions, the binary won't download and `electron .` / `npm run dev` will fail. Workarounds:
1. Add `release-assets.githubusercontent.com` to the network egress allowlist.
2. Use `npm run dev:web` which serves the renderer in any browser without the Electron binary.
3. Set `ELECTRON_SKIP_BINARY_DOWNLOAD=1` when installing to skip the download, then manually place the binary in `node_modules/electron/dist/`.

After running `npm install --ignore-scripts`, you can attempt `node node_modules/electron/install.js` to download the binary separately.

### Node.js / npm
- Node.js v22 is available via nvm.
- `npm`, `pnpm`, and `yarn` are installed. The project uses npm (see `package-lock.json`).

### Display / GUI testing
- A virtual display is available for GUI testing via the `computerUse` subagent.
- Electron apps require the `--no-sandbox` flag in this environment.
- For web-mode dev (`npm run dev:web`), open http://localhost:3000 in Chrome.

### Project structure
```
electron-src/
  main/          # Electron main process (Node.js)
    main.ts      # App entry, window creation, IPC handlers
    songStore.ts # Song file loading (JSON and legacy .sng formats)
  renderer/      # Electron renderer process (DOM/browser)
    index.html   # App shell
    style.css    # Styles
    renderer.ts  # UI logic, song list, verse selection, projector preview
    preload.ts   # Context bridge (Electron preload script)
  dev-server.ts  # Lightweight HTTP server for browser-based development
src/             # Legacy Java source (reference only)
songs/           # Song data directory (JSON files)
```
