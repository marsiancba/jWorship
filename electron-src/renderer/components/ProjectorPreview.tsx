interface ProjectorPreviewProps {
  text: string | null;
}

export function ProjectorPreview({ text }: ProjectorPreviewProps) {
  return (
    <section id="live-preview">
      <h2>Projekcia</h2>
      <div id="projector-preview">
        <div id="projector-text">{text}</div>
      </div>
    </section>
  );
}
