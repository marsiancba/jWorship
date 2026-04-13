import type { ScreenState } from "../models/screen";
import { TextAlign, TextAreaPart } from "../models/screen";

interface ScreenPreviewProps {
  label: string;
  screen: ScreenState;
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
      return { justifyContent: "flex-start", paddingBottom: "45%" };
    case TextAreaPart.BOTTOM:
      return { justifyContent: "flex-end", paddingTop: "45%" };
    case TextAreaPart.TOP_2THIRDS:
      return { justifyContent: "flex-start", paddingBottom: "28%" };
    default:
      return {};
  }
}

export function ScreenPreview({ label, screen }: ScreenPreviewProps) {
  const aspectRatio = `1 / ${screen.height}`;

  const textStyle: React.CSSProperties = {
    color: screen.textColor,
    textAlign: textAlignCSS(screen.textAlign),
    textShadow: screen.textShadow
      ? "2px 2px 4px rgba(0,0,0,0.8)"
      : "none",
    textTransform: screen.textCapsLock ? "uppercase" : "none",
    overflowWrap: screen.textWordWrap ? "break-word" : "normal",
    whiteSpace: screen.textWordWrap ? "pre-wrap" : "pre",
  };

  return (
    <div className="screen-preview">
      <div className="screen-preview-label">{label}</div>
      <div
        className="screen-preview-display"
        style={{ aspectRatio, ...textAreaStyle(screen.textAreaPart) }}
      >
        <div className="screen-preview-text" style={textStyle}>
          {screen.text}
        </div>
      </div>
    </div>
  );
}
