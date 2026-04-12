interface JWorshipAPI {
  listSongs(): Promise<string[]>;
  loadSong(fileName: string): Promise<{
    fileName: string;
    title: string;
    author: string;
    verses: string[];
  } | null>;
}

export {};

declare global {
  interface Window {
    api: JWorshipAPI;
  }
}

const songList = document.getElementById("songs") as HTMLUListElement;
const songTitle = document.getElementById("song-title") as HTMLDivElement;
const versesContainer = document.getElementById("verses") as HTMLDivElement;
const projectorText = document.getElementById("projector-text") as HTMLDivElement;
const searchInput = document.getElementById("search") as HTMLInputElement;

let allSongs: string[] = [];

function displayNameFromFile(fileName: string): string {
  return fileName.replace(/\.(json|sng|txt)$/, "");
}

function renderSongList(filter: string): void {
  songList.innerHTML = "";
  const lowerFilter = filter.toLowerCase();
  const filtered = allSongs.filter((s) =>
    displayNameFromFile(s).toLowerCase().includes(lowerFilter)
  );

  for (const song of filtered) {
    const li = document.createElement("li");
    li.textContent = displayNameFromFile(song);
    li.dataset.file = song;
    li.addEventListener("click", () => selectSong(song, li));
    songList.appendChild(li);
  }
}

async function selectSong(fileName: string, element: HTMLLIElement): Promise<void> {
  document.querySelectorAll("#songs li.active").forEach((el) => el.classList.remove("active"));
  element.classList.add("active");

  const song = await window.api.loadSong(fileName);
  if (!song) {
    songTitle.textContent = "Chyba pri načítaní piesne";
    versesContainer.innerHTML = "";
    return;
  }

  songTitle.textContent = song.title + (song.author ? ` — ${song.author}` : "");
  versesContainer.innerHTML = "";

  song.verses.forEach((verse, index) => {
    const div = document.createElement("div");
    div.className = "verse";

    const label = document.createElement("div");
    label.className = "verse-label";
    label.textContent = `Verš ${index + 1}`;
    div.appendChild(label);

    const text = document.createElement("div");
    text.textContent = verse;
    div.appendChild(text);

    div.addEventListener("click", () => {
      document.querySelectorAll(".verse.active").forEach((el) => el.classList.remove("active"));
      div.classList.add("active");
      projectorText.textContent = verse;
    });

    versesContainer.appendChild(div);
  });
}

searchInput.addEventListener("input", () => {
  renderSongList(searchInput.value);
});

function getApi(): JWorshipAPI {
  if (window.api) {
    return window.api;
  }
  return {
    listSongs: () => fetch("/api/songs").then((r) => r.json()),
    loadSong: (fileName: string) =>
      fetch(`/api/songs/${encodeURIComponent(fileName)}`).then((r) => r.json()),
  };
}

async function init(): Promise<void> {
  const api = getApi();
  window.api = api;
  try {
    allSongs = await api.listSongs();
    renderSongList("");
  } catch {
    songTitle.textContent = "jWorship – Vitajte";
    versesContainer.innerHTML = `<p style="color:#888">Žiadne piesne nenájdené. Pridajte súbory .json do priečinka songs/.</p>`;
  }
}

init();
