import { useRef, useState, useEffect } from "react";
import type { ScreenState } from "../models/screen";
import { SvgScreenRenderer } from "./SvgScreenRenderer";

interface ScreenPreviewProps {
  label: string;
  screen: ScreenState;
}

export function ScreenPreview({ label, screen }: ScreenPreviewProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [size, setSize] = useState({ width: 200, height: 150 });

  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;

    const observer = new ResizeObserver((entries) => {
      for (const entry of entries) {
        const { width, height } = entry.contentRect;
        if (width > 0 && height > 0) {
          setSize({ width, height });
        }
      }
    });
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return (
    <div className="screen-preview">
      <div className="screen-preview-label">{label}</div>
      <div className="screen-preview-container" ref={containerRef}>
        <SvgScreenRenderer
          screen={screen}
          width={size.width}
          height={size.height}
        />
      </div>
    </div>
  );
}
