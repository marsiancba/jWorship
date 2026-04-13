export enum TextAlign {
  LEFT = "LEFT",
  CENTER = "CENTER",
  RIGHT = "RIGHT",
}

export enum TextAreaPart {
  ALL = "ALL",
  TOP = "TOP",
  BOTTOM = "BOTTOM",
  TOP_2THIRDS = "TOP_2THIRDS",
}

export interface ScreenSettings {
  height: number;
  textFontHeight: number;
  textShadow: boolean;
  textCapsLock: boolean;
  textColor: string;
  textWordWrap: boolean;
  textFit: boolean;
  backgroundFillScreen: boolean;
  textAlign: TextAlign;
  textAreaPart: TextAreaPart;
}

export const DEFAULT_SCREEN_SETTINGS: ScreenSettings = {
  height: 0.75,
  textFontHeight: 0.1,
  textShadow: true,
  textCapsLock: false,
  textColor: "#ffffff",
  textWordWrap: true,
  textFit: true,
  backgroundFillScreen: true,
  textAlign: TextAlign.CENTER,
  textAreaPart: TextAreaPart.ALL,
};

export interface ScreenState extends ScreenSettings {
  backgroundMedia: string;
  text: string;
}

export const DEFAULT_SCREEN_STATE: ScreenState = {
  ...DEFAULT_SCREEN_SETTINGS,
  backgroundMedia: "",
  text: "",
};

export function serializeScreenSettings(s: ScreenSettings): string {
  return JSON.stringify({
    textWordWrap: s.textWordWrap,
    textShadow: s.textShadow,
    textCapsLock: s.textCapsLock,
    textFit: s.textFit,
    height: s.height,
    textFontHeight: s.textFontHeight,
    backgroundFillScreen: s.backgroundFillScreen,
    textAlign: s.textAlign,
    textAreaPart: s.textAreaPart,
  });
}

export function deserializeScreenSettings(
  json: string
): Partial<ScreenSettings> {
  try {
    const raw = JSON.parse(json);
    const result: Partial<ScreenSettings> = {};
    if (typeof raw.textWordWrap === "boolean")
      result.textWordWrap = raw.textWordWrap;
    if (typeof raw.textShadow === "boolean")
      result.textShadow = raw.textShadow;
    if (typeof raw.textCapsLock === "boolean")
      result.textCapsLock = raw.textCapsLock;
    if (typeof raw.textFit === "boolean") result.textFit = raw.textFit;
    if (typeof raw.height === "number") result.height = raw.height;
    if (typeof raw.textFontHeight === "number")
      result.textFontHeight = raw.textFontHeight;
    if (typeof raw.backgroundFillScreen === "boolean")
      result.backgroundFillScreen = raw.backgroundFillScreen;
    if (raw.textAlign in TextAlign) result.textAlign = raw.textAlign;
    if (raw.textAreaPart in TextAreaPart)
      result.textAreaPart = raw.textAreaPart;
    return result;
  } catch {
    return {};
  }
}
