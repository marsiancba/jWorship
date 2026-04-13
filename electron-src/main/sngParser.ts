import * as fs from "fs";

/**
 * Minimal parser for Java ObjectOutputStream .sng files containing
 * sk.calvary.worship.Song (or sk.asc.worship.Song) serialized objects.
 *
 * Format: standard Java Object Serialization Protocol.
 * We only need to extract: title, title2, author, and verses (Vector<String>).
 */

interface SngSong {
  title: string;
  title2: string;
  author: string;
  verses: string[];
}

const TC_NULL = 0x70;
const TC_REFERENCE = 0x71;
const TC_CLASSDESC = 0x72;
const TC_OBJECT = 0x73;
const TC_STRING = 0x74;
const TC_ARRAY = 0x75;
const TC_ENDBLOCKDATA = 0x78;

class SngReader {
  private buf: Buffer;
  private pos: number;
  private handles: unknown[] = [];

  constructor(buf: Buffer) {
    this.buf = buf;
    this.pos = 0;
  }

  private readByte(): number {
    return this.buf[this.pos++];
  }

  private readShort(): number {
    const v = this.buf.readInt16BE(this.pos);
    this.pos += 2;
    return v;
  }

  private readUShort(): number {
    const v = this.buf.readUInt16BE(this.pos);
    this.pos += 2;
    return v;
  }

  private readInt(): number {
    const v = this.buf.readInt32BE(this.pos);
    this.pos += 4;
    return v;
  }

  private readLong(): void {
    this.pos += 8;
  }

  private readUtf(): string {
    const len = this.readUShort();
    const s = this.buf.toString("utf8", this.pos, this.pos + len);
    this.pos += len;
    return s;
  }

  private newHandle(obj: unknown): number {
    const h = this.handles.length;
    this.handles.push(obj);
    return h;
  }

  private readString(): string {
    const s = this.readUtf();
    this.newHandle(s);
    return s;
  }

  private readClassDesc(): { name: string; fieldCount: number; fields: { type: string; name: string; className?: string }[] } | null {
    const tc = this.readByte();
    if (tc === TC_NULL) return null;
    if (tc === TC_REFERENCE) {
      this.readInt(); // handle
      return null;
    }
    if (tc !== TC_CLASSDESC) return null;

    const name = this.readUtf();
    this.readLong(); // serialVersionUID
    const handle = this.newHandle({ name });
    const flags = this.readByte();
    const fieldCount = this.readShort();

    const fields: { type: string; name: string; className?: string }[] = [];
    for (let i = 0; i < fieldCount; i++) {
      const typeCode = String.fromCharCode(this.readByte());
      const fieldName = this.readUtf();
      let className: string | undefined;
      if (typeCode === "L" || typeCode === "[") {
        const stc = this.readByte();
        if (stc === TC_STRING) {
          className = this.readString();
        } else if (stc === TC_REFERENCE) {
          this.readInt();
        }
      }
      fields.push({ type: typeCode, name: fieldName, className });
    }

    // classAnnotation
    while (this.readByte() !== TC_ENDBLOCKDATA) {
      this.pos--;
      this.readContent();
    }

    // superClassDesc
    this.readClassDesc();

    void flags;
    void handle;
    return { name, fieldCount, fields };
  }

  private readContent(): unknown {
    const tc = this.buf[this.pos];
    switch (tc) {
      case TC_OBJECT:
        return this.readObject();
      case TC_STRING:
        this.pos++;
        return this.readString();
      case TC_ARRAY:
        return this.readArray();
      case TC_NULL:
        this.pos++;
        return null;
      case TC_REFERENCE: {
        this.pos++;
        const handle = this.readInt() - 0x7e0000;
        return this.handles[handle] ?? null;
      }
      default:
        return null;
    }
  }

  private readArray(): unknown[] {
    this.pos++; // TC_ARRAY
    const desc = this.readClassDesc();
    const handle = this.newHandle([]);
    const size = this.readInt();
    const arr: unknown[] = [];
    for (let i = 0; i < size; i++) {
      arr.push(this.readContent());
    }
    (this.handles[handle] as unknown[]).push(...arr);
    void desc;
    return arr;
  }

  private readObject(): Record<string, unknown> {
    this.pos++; // TC_OBJECT
    const desc = this.readClassDesc();
    const obj: Record<string, unknown> = {};
    const handle = this.newHandle(obj);

    if (desc) {
      for (const field of desc.fields) {
        switch (field.type) {
          case "I":
            obj[field.name] = this.readInt();
            break;
          case "Z":
            obj[field.name] = this.readByte() !== 0;
            break;
          case "L":
          case "[":
            obj[field.name] = this.readContent();
            break;
          default:
            break;
        }
      }
    }

    // classAnnotation / objectAnnotation
    if (this.pos < this.buf.length && this.buf[this.pos] === TC_ENDBLOCKDATA) {
      this.pos++;
    }

    void handle;
    return obj;
  }

  parse(): SngSong {
    // Magic + version
    const magic = this.readUShort();
    const version = this.readUShort();
    if (magic !== 0xaced || version !== 5) {
      throw new Error("Not a Java serialized object");
    }

    const obj = this.readContent() as Record<string, unknown>;

    const title = (obj.title as string) ?? "";
    const title2 = (obj.title2 as string) ?? "";
    const author = (obj.author as string) ?? "";

    let verses: string[] = [];
    const versesObj = obj.verses as Record<string, unknown> | undefined;
    if (versesObj && versesObj.elementData) {
      const elements = versesObj.elementData as unknown[];
      const count =
        typeof versesObj.elementCount === "number"
          ? versesObj.elementCount
          : elements.length;
      verses = elements.slice(0, count).filter((v): v is string => typeof v === "string");
    }

    return { title, title2, author, verses };
  }
}

export function parseSngFile(filePath: string): SngSong {
  const buf = fs.readFileSync(filePath);
  const reader = new SngReader(buf);
  return reader.parse();
}

/**
 * Parse a .txt song file: `@` separates verses, filename becomes title.
 */
export function parseTxtSongFile(
  filePath: string,
  fileName: string
): SngSong {
  const content = fs.readFileSync(filePath, "utf-8").replace(/\r/g, "");
  const title = fileName.replace(/\.txt$/i, "");
  const verses = content
    .split("@")
    .map((v) => v.trim())
    .filter(Boolean);
  return { title, title2: "", author: "", verses };
}
