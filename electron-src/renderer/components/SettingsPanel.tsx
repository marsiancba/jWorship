import {
  type ScreenSettings,
  TextAlign,
  TextAreaPart,
} from "../models/screen";

interface SettingsPanelProps {
  settings: ScreenSettings;
  onChange: (patch: Partial<ScreenSettings>) => void;
}

const ASPECT_PRESETS: { label: string; value: number }[] = [
  { label: "4:3", value: 0.75 },
  { label: "16:9", value: 0.5625 },
  { label: "8:7", value: 0.875 },
];

export function SettingsPanel({ settings, onChange }: SettingsPanelProps) {
  return (
    <div className="settings-panel">
      <fieldset>
        <legend>Text</legend>

        <label className="setting-row">
          <input
            type="checkbox"
            checked={settings.textShadow}
            onChange={(e) => onChange({ textShadow: e.target.checked })}
          />
          Tieň
        </label>

        <label className="setting-row">
          <input
            type="checkbox"
            checked={settings.textCapsLock}
            onChange={(e) => onChange({ textCapsLock: e.target.checked })}
          />
          CapsLock
        </label>

        <label className="setting-row">
          <input
            type="checkbox"
            checked={settings.textWordWrap}
            onChange={(e) => onChange({ textWordWrap: e.target.checked })}
          />
          Zalamovať
        </label>

        <div className="setting-row">
          <span className="setting-label">Veľkosť písma:</span>
          <input
            type="range"
            min={0.02}
            max={0.3}
            step={0.01}
            value={settings.textFontHeight}
            onChange={(e) =>
              onChange({ textFontHeight: parseFloat(e.target.value) })
            }
          />
          <span className="setting-value">
            {(settings.textFontHeight * 100).toFixed(0)}%
          </span>
        </div>

        <div className="setting-row">
          <span className="setting-label">Farba písma:</span>
          <input
            type="color"
            value={settings.textColor}
            onChange={(e) => onChange({ textColor: e.target.value })}
          />
        </div>

        <div className="setting-row">
          <span className="setting-label">Zarovnanie:</span>
          <div className="button-group">
            {Object.values(TextAlign).map((a) => (
              <button
                key={a}
                className={settings.textAlign === a ? "active" : ""}
                onClick={() => onChange({ textAlign: a })}
              >
                {a === TextAlign.LEFT
                  ? "⬅"
                  : a === TextAlign.CENTER
                    ? "⬌"
                    : "➡"}
              </button>
            ))}
          </div>
        </div>

        <div className="setting-row">
          <span className="setting-label">Oblasť textu:</span>
          <select
            value={settings.textAreaPart}
            onChange={(e) =>
              onChange({ textAreaPart: e.target.value as TextAreaPart })
            }
          >
            <option value={TextAreaPart.ALL}>Celá obrazovka</option>
            <option value={TextAreaPart.TOP}>Horná polovica</option>
            <option value={TextAreaPart.BOTTOM}>Dolná polovica</option>
            <option value={TextAreaPart.TOP_2THIRDS}>Horné 2/3</option>
          </select>
        </div>
      </fieldset>

      <fieldset>
        <legend>Pozadie</legend>

        <label className="setting-row">
          <input
            type="checkbox"
            checked={settings.backgroundFillScreen}
            onChange={(e) =>
              onChange({ backgroundFillScreen: e.target.checked })
            }
          />
          Pozadie vypĺňa celú obrazovku
        </label>
      </fieldset>

      <fieldset>
        <legend>Obrazovka</legend>

        <div className="setting-row">
          <span className="setting-label">Pomer strán:</span>
          <div className="button-group">
            {ASPECT_PRESETS.map((p) => (
              <button
                key={p.label}
                className={settings.height === p.value ? "active" : ""}
                onClick={() => onChange({ height: p.value })}
              >
                {p.label}
              </button>
            ))}
          </div>
        </div>

        <div className="setting-row">
          <input
            type="range"
            min={0.5}
            max={2.0}
            step={0.01}
            value={settings.height}
            onChange={(e) => onChange({ height: parseFloat(e.target.value) })}
          />
          <span className="setting-value">{settings.height.toFixed(3)}</span>
        </div>
      </fieldset>
    </div>
  );
}
