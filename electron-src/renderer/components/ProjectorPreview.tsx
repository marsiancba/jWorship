import type { ScreenSettings } from "../models/screen";
import { TextAlign, TextAreaPart } from "../models/screen";

interface ProjectorPreviewProps {
  text: string | null;
  settings: ScreenSettings;
}

function textAlignCSS(align: TextAlign): string {
  switch (align) {
    case TextAlign.LEFT:
      return "left";
    case TextAlign.RIGHT:
      return "right";
    default:
      return "center";
  }
}

function textAreaStyle(part: TextAreaPart): React.CSSProperties {
  switch (part) {
    case TextAreaPart.TOP:
      return { alignItems: "flex-start", paddingBottom: "50%" };
    case TextAreaPart.BOTTOM:
      return { alignItems: "flex-end", paddingTop: "50%" };
    case TextAreaPart.TOP_2THIRDS:
      return { alignItems: "flex-start", paddingBottom: "33%" };
    default:
      return {};
  }
}

export function ProjectorPreview({ text, settings }: ProjectorPreviewProps) {
  const aspectRatio = `1 / ${settings.height}`;

  const textStyle: React.CSSProperties = {
    color: settings.textColor,
    textAlign: textAlignCSS(settings.textAlign),
    textShadow: settings.textShadow
      ? "2px 2px 4px rgba(0,0,0,0.8)"
      : "none",
    textTransform: settings.textCapsLock ? "uppercase" : "none",
    overflowWrap: settings.textWordWrap ? "break-word" : "normal",
    whiteSpace: settings.textWordWrap ? "pre-wrap" : "pre",
    fontSize: `${settings.textFontHeight * 100}%`,
  };

  return (
    <section id="live-preview">
      <h2>Projekcia</h2>
      <div
        id="projector-preview"
        style={{ aspectRatio, ...textAreaStyle(settings.textAreaPart) }}
      >
        <div id="projector-text" style={textStyle}>
          {text}
        </div>
      </div>
    </section>
  );
}
