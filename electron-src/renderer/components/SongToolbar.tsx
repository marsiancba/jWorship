import type { ScreenSettings } from "../models/screen";
import { TextAlign, TextAreaPart } from "../models/screen";

interface SongToolbarProps {
  settings: ScreenSettings;
  onChange: (patch: Partial<ScreenSettings>) => void;
}

export function SongToolbar({ settings, onChange }: SongToolbarProps) {
  return (
    <div className="song-toolbar">
      <div className="toolbar-group" title="Oblasť textu">
        {([
          [TextAreaPart.ALL, "▣", "Celá"],
          [TextAreaPart.TOP, "⬒", "Hore"],
          [TextAreaPart.BOTTOM, "⬓", "Dole"],
          [TextAreaPart.TOP_2THIRDS, "◧", "2/3"],
        ] as [TextAreaPart, string, string][]).map(([part, icon, tip]) => (
          <button
            key={part}
            className={`toolbar-btn${settings.textAreaPart === part ? " active" : ""}`}
            title={tip}
            onClick={() => onChange({ textAreaPart: part })}
          >
            {icon}
          </button>
        ))}
      </div>

      <div className="toolbar-sep" />

      <div className="toolbar-group" title="Zarovnanie">
        {([
          [TextAlign.LEFT, "⫷", "Vľavo"],
          [TextAlign.CENTER, "☰", "Na stred"],
          [TextAlign.RIGHT, "⫸", "Vpravo"],
        ] as [TextAlign, string, string][]).map(([align, icon, tip]) => (
          <button
            key={align}
            className={`toolbar-btn${settings.textAlign === align ? " active" : ""}`}
            title={tip}
            onClick={() => onChange({ textAlign: align })}
          >
            {icon}
          </button>
        ))}
      </div>

      <div className="toolbar-sep" />

      <button
        className={`toolbar-btn${settings.textShadow ? " active" : ""}`}
        title="Tieň"
        onClick={() => onChange({ textShadow: !settings.textShadow })}
      >
        S
      </button>

      <button
        className={`toolbar-btn${settings.textCapsLock ? " active" : ""}`}
        title="CapsLock"
        onClick={() => onChange({ textCapsLock: !settings.textCapsLock })}
      >
        AA
      </button>

      <button
        className={`toolbar-btn${settings.textWordWrap ? " active" : ""}`}
        title="Zalamovať"
        onClick={() => onChange({ textWordWrap: !settings.textWordWrap })}
      >
        ↩
      </button>

      <div className="toolbar-sep" />

      <input
        type="color"
        className="toolbar-color"
        value={settings.textColor}
        title="Farba písma"
        onChange={(e) => onChange({ textColor: e.target.value })}
      />
    </div>
  );
}
