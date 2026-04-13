# AGENTS.md

## Project overview

jWorship is a church worship presentation desktop application originally built in Java 8 with JavaFX and Swing. The project goal is to **migrate from JavaFX/Java to TypeScript, React, and Electron**.

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

### Electron/React/TypeScript codebase
- **Renderer:** React + Vite in `electron-src/renderer/` (TSX components)
- **Main process:** plain TypeScript in `electron-src/main/`
- **Build output:** `dist/`
- See `package.json` scripts for all commands.

## Cursor Cloud specific instructions

### Commands
- **Build:** `npm run build` — compiles main process (tsc), bundles renderer (Vite), compiles preload (tsc)
- **Lint:** `npm run lint` — ESLint on all `electron-src/**/*.{ts,tsx}`
- **Dev (Vite HMR):** run `npm run dev:vite` in one terminal, then `npm run dev:electron` in another — gives hot-reload for the renderer
- **Dev (production build):** `npm run dev` — full build then launch Electron
- **Test:** `npm test`

### Running Electron in this environment
- Pass `--no-sandbox`: `DISPLAY=:1 npx electron . --no-sandbox`
- DBus errors in the log are cosmetic and harmless in containers.
- The `dev:electron` script sets `NODE_ENV=development` so the main process loads `http://localhost:3000` (Vite dev server) instead of the built files.

### Architecture notes
- **Two tsconfigs for the renderer:** `electron-src/renderer/tsconfig.json` (React/Vite, `noEmit`) and `tsconfig.preload.json` (preload script, CommonJS output). The root `tsconfig.json` is for the main process only.
- **Preload script** must be CommonJS (Electron requirement). It is compiled separately via `tsconfig.preload.json`.
- **React components** use ESM and are bundled by Vite. They go through `contextBridge` for IPC.
- The `api.ts` module provides `getSongApi()` which returns the Electron IPC bridge (`window.api` from preload) when available, or falls back to fetch-based API for browser-only dev.

### Node.js / npm
- Node.js v22 via nvm. Project uses npm (`package-lock.json`).
- Electron binary (~110 MB) downloads from `release-assets.githubusercontent.com` during `npm install`.

### Display / GUI testing
- Virtual display (Xtigervnc) is on DISPLAY=:1.
- For Vite dev server, open http://localhost:3000 in Chrome.

### Project structure
```
electron-src/
  main/                   # Electron main process (Node.js, CommonJS)
    main.ts               # App entry, window creation, IPC handlers
    songStore.ts          # Song file loading (JSON and legacy .sng)
  renderer/               # Electron renderer (React + Vite, ESM)
    vite.config.ts        # Vite build config
    tsconfig.json         # React/Vite tsconfig (noEmit, bundler resolution)
    tsconfig.preload.json # Preload-only tsconfig (CommonJS output)
    index.html            # Vite entry HTML
    main.tsx              # React root mount
    App.tsx               # Top-level App component
    api.ts                # Song API abstraction (IPC or fetch)
    style.css             # Global styles
    preload.ts            # Electron context bridge
    components/
      SongList.tsx        # Song list sidebar
      SongView.tsx        # Verse display panel
      ProjectorPreview.tsx # Live projector preview
src/                      # Legacy Java source (reference only)
songs/                    # Song data directory (JSON files)
```
