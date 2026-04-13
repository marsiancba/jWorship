import { useRef, useEffect, useState, useCallback } from "react";
import type { ScreenState } from "../models/screen";
import { TextAlign, TextAreaPart } from "../models/screen";
import { picUrl } from "../api";

interface SvgScreenRendererProps {
  screen: ScreenState;
  width: number;
  height: number;
}

interface WrappedLine {
  text: string;
  y: number;
}

export function SvgScreenRenderer({
  screen,
  width,
  height,
}: SvgScreenRendererProps) {
  const svgRef = useRef<SVGSVGElement>(null);
  const measureRef = useRef<SVGTextElement>(null);
  const [lines, setLines] = useState<WrappedLine[]>([]);
  const [fontSize, setFontSize] = useState(16);
  const [bgUrl, setBgUrl] = useState<string | null>(null);

  const displayHeight = width * screen.height;
  const svgH = Math.min(height, displayHeight);
  const svgW = svgH / screen.height;

  useEffect(() => {
    if (screen.backgroundMedia) {
      setBgUrl(picUrl(screen.backgroundMedia));
    } else {
      setBgUrl(null);
    }
  }, [screen.backgroundMedia]);

  const computeTextArea = useCallback((): {
    x: number;
    y: number;
    w: number;
    h: number;
  } => {
    const pad = svgW * 0.05;
    let areaX = pad;
    let areaY = pad;
    let areaW = svgW - pad * 2;
    let areaH = svgH - pad * 2;

    switch (screen.textAreaPart) {
      case TextAreaPart.TOP:
        areaH = areaH * 0.5;
        break;
      case TextAreaPart.BOTTOM:
        areaY = areaY + areaH * 0.5;
        areaH = areaH * 0.5;
        break;
      case TextAreaPart.TOP_2THIRDS:
        areaH = areaH * 0.667;
        break;
    }
    return { x: areaX, y: areaY, w: areaW, h: areaH };
  }, [svgW, svgH, screen.textAreaPart]);

  useEffect(() => {
    const svg = svgRef.current;
    const measure = measureRef.current;
    if (!svg || !measure || svgW <= 0 || svgH <= 0) return;

    const rawText = screen.textCapsLock
      ? screen.text.toUpperCase()
      : screen.text;

    if (!rawText.trim()) {
      setLines([]);
      setFontSize(16);
      return;
    }

    const area = computeTextArea();
    let fs = svgH * screen.textFontHeight;
    if (fs < 2) fs = 2;

    const computeLines = (
      text: string,
      fSize: number,
      maxW: number
    ): string[] => {
      measure.style.fontSize = `${fSize}px`;
      const paragraphs = text.split("\n");
      const result: string[] = [];

      for (const para of paragraphs) {
        if (!screen.textWordWrap) {
          result.push(para);
          continue;
        }
        const words = para.split(/\s+/);
        if (words.length === 0 || (words.length === 1 && words[0] === "")) {
          result.push("");
          continue;
        }
        let currentLine = "";
        for (const word of words) {
          const testLine = currentLine ? `${currentLine} ${word}` : word;
          measure.textContent = testLine;
          const tw = measure.getComputedTextLength();
          if (tw > maxW && currentLine) {
            result.push(currentLine);
            currentLine = word;
          } else {
            currentLine = testLine;
          }
        }
        if (currentLine) result.push(currentLine);
      }
      return result;
    };

    for (let iter = 0; iter < 200; iter++) {
      const wrapped = computeLines(rawText, fs, area.w);
      const lineH = fs * 1.3;
      const totalH = wrapped.length * lineH;

      let ok = true;
      if (totalH > area.h * 1.001) ok = false;
      if (!screen.textWordWrap) {
        measure.style.fontSize = `${fs}px`;
        for (const l of wrapped) {
          measure.textContent = l;
          if (measure.getComputedTextLength() > area.w * 1.001) {
            ok = false;
            break;
          }
        }
      }

      if (ok) {
        const lineHeight = fs * 1.3;
        const yStart = area.y + (area.h - wrapped.length * lineHeight) / 2;
        setLines(
          wrapped.map((text, i) => ({
            text,
            y: yStart + i * lineHeight + fs,
          }))
        );
        setFontSize(fs);
        return;
      }

      let shrink = 0.97;
      if (totalH > 2 * area.h) {
        shrink = Math.pow(area.h / totalH, 0.33);
      }
      fs *= shrink;
      if (fs < 1) {
        fs = 1;
        break;
      }
    }

    const wrapped = computeLines(rawText, fs, area.w);
    const lineHeight = fs * 1.3;
    const yStart = area.y + (area.h - wrapped.length * lineHeight) / 2;
    setLines(
      wrapped.map((text, i) => ({
        text,
        y: yStart + i * lineHeight + fs,
      }))
    );
    setFontSize(fs);
  }, [screen, svgW, svgH, computeTextArea]);

  const textAnchor =
    screen.textAlign === TextAlign.LEFT
      ? "start"
      : screen.textAlign === TextAlign.RIGHT
        ? "end"
        : "middle";

  const area = computeTextArea();
  const textX =
    screen.textAlign === TextAlign.LEFT
      ? area.x
      : screen.textAlign === TextAlign.RIGHT
        ? area.x + area.w
        : area.x + area.w / 2;

  const shadowFilter = screen.textShadow
    ? "url(#text-shadow)"
    : undefined;

  return (
    <svg
      ref={svgRef}
      width={svgW}
      height={svgH}
      viewBox={`0 0 ${svgW} ${svgH}`}
      style={{ background: "#000", borderRadius: 4, display: "block" }}
    >
      <defs>
        <filter id="text-shadow" x="-10%" y="-10%" width="120%" height="120%">
          <feDropShadow
            dx={fontSize * 0.05}
            dy={fontSize * 0.05}
            stdDeviation={fontSize * 0.06}
            floodColor="black"
            floodOpacity="0.8"
          />
        </filter>
      </defs>

      {bgUrl && (
        <image
          href={bgUrl}
          x={0}
          y={0}
          width={svgW}
          height={svgH}
          preserveAspectRatio={
            screen.backgroundFillScreen ? "xMidYMid slice" : "xMidYMid meet"
          }
        />
      )}

      <text
        ref={measureRef}
        style={{
          fontSize: `${fontSize}px`,
          visibility: "hidden",
          position: "absolute",
        }}
      />

      {lines.map((line, i) => (
        <text
          key={i}
          x={textX}
          y={line.y}
          textAnchor={textAnchor}
          fill={screen.textColor}
          fontSize={fontSize}
          stroke={screen.textColor}
          strokeWidth={fontSize * 0.01}
          filter={shadowFilter}
        >
          {line.text}
        </text>
      ))}
    </svg>
  );
}
