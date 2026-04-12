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

## Cursor Cloud specific instructions

### Network restrictions
- Only `github.com` (git) and `api.github.com` (API) are accessible.
- `registry.npmjs.org` is **blocked** by the egress proxy. You must request it be added to the allowlist before `npm install` / `pnpm install` will work.
- `archive.ubuntu.com` (apt), `pypi.org` (pip), `repo.maven.apache.org` (Maven Central) are also blocked.
- Maven is installed at `/opt/maven` (from GitHub clone); not needed for the Electron migration.

### Node.js / npm
- Node.js v22 is available via nvm (`~/.nvm/versions/node/v22.22.1/`).
- `npm`, `pnpm`, and `yarn` are installed but **cannot reach their registries** due to egress restrictions.
- Once `registry.npmjs.org` is added to the allowlist, use `npm install` (or the lockfile-matching package manager) to install dependencies.

### Display / GUI testing
- A virtual display (Xvfb / VNC) is available for GUI testing via the `computerUse` subagent.
- Electron apps should work with `--no-sandbox` flag in this environment.

### Java environment (legacy, reference only)
- OpenJDK 21 is installed (`/usr/lib/jvm/java-21-openjdk-amd64/`).
- JavaFX is **not** included with OpenJDK 21; building the legacy Java app in this environment is not straightforward and not needed for the migration.
