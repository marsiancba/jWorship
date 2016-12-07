/*
 * Created on Nov 13, 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public interface JSONSerializer {
	boolean serialize(DoubleProperty p);

	boolean serialize(BooleanProperty p);

	boolean serialize(StringProperty p);

	boolean serializeSubObject(String name, Consumer<JSONSerializer> sub);

	<E extends Enum<E>> boolean serializeEnum(ObjectProperty<E> p,
			Function<String, E> valueOf);

	<T> boolean serializeObjectList(String name, List<T> list,
			Supplier<T> new_item, BiConsumer<T, JSONSerializer> serialize_item);

	<T> boolean serializeStringList(String name, List<String> list);

	<T> boolean serializeObjectListAsStrings(String name, List<T> list,
			Function<T, String> encoder, Function<String, T> decoder);

	void write(File f) throws IOException;

	static JSONSerializer writer(JsonObjectBuilder b) {
		return new Writer(b);
	}

	static JSONSerializer writer() {
		return new Writer(Json.createObjectBuilder());
	}

	JsonObject get();

	class Writer implements JSONSerializer {
		private JsonObjectBuilder j;

		Writer() {
			this(Json.createObjectBuilder());
		}

		public Writer(JsonObjectBuilder j) {
			this.j = j;
		}

		@Override
		public boolean serialize(DoubleProperty p) {
			j.add(p.getName(), p.get());
			return true;
		}

		@Override
		public boolean serialize(BooleanProperty p) {
			j.add(p.getName(), p.get());
			return true;
		}

		@Override
		public boolean serialize(StringProperty p) {
			j.add(p.getName(), p.get());
			return true;
		}

		@Override
		public boolean serializeSubObject(String name,
				Consumer<JSONSerializer> sub) {
			Writer w = new Writer(Json.createObjectBuilder());
			sub.accept(w);
			j.add(name, w.j);
			return true;
		}

		@Override
		public JsonObject get() {
			return j.build();
		}

		@Override
		public <E extends Enum<E>> boolean serializeEnum(ObjectProperty<E> p,
				Function<String, E> valueOf) {
			j.add(p.getName(), p.get().name());
			return true;
		}

		@Override
		public void write(File f) throws IOException {
			try (JsonWriter w = Json.createWriter(new FileOutputStream(f))) {
				w.write(get());
				w.close();
			}
		}

		@Override
		public <T> boolean serializeObjectList(String name, List<T> list,
				Supplier<T> new_item,
				BiConsumer<T, JSONSerializer> serialize_item) {
			JsonArrayBuilder ja = Json.createArrayBuilder();
			for (T item : list) {
				Writer ss = new Writer();
				serialize_item.accept(item, ss);
				ja.add(ss.j);
			}
			j.add(name, ja);
			return true;
		}

		@Override
		public <T> boolean serializeStringList(String name, List<String> list) {
			JsonArrayBuilder ja = Json.createArrayBuilder();
			for (String s : list) {
				ja.add(s);
			}
			j.add(name, ja);
			return true;
		}

		@Override
		public <T> boolean serializeObjectListAsStrings(String name,
				List<T> list, Function<T, String> encoder,
				Function<String, T> decoder) {
			JsonArrayBuilder ja = Json.createArrayBuilder();
			for (T item : list) {
				ja.add(encoder.apply(item));
			}
			j.add(name, ja);
			return true;
		}

	}

	class Reader implements JSONSerializer {
		private final JsonObject j;

		public Reader(JsonObject j) {
			this.j = j;
		}

		@Override
		public boolean serialize(DoubleProperty p) {
			JsonValue jv = j.get(p.getName());
			if (jv instanceof JsonNumber) {
				JsonNumber jn = (JsonNumber) jv;
				p.set(jn.doubleValue());
				return true;
			}

			return false;
		}

		@Override
		public boolean serialize(BooleanProperty p) {
			JsonValue jv = j.get(p.getName());
			if (JsonValue.TRUE.equals(jv))
				p.set(true);
			else if (JsonValue.FALSE.equals(jv))
				p.set(false);
			else
				return false;

			return true;
		}

		@Override
		public boolean serialize(StringProperty p) {
			JsonValue jv = j.get(p.getName());
			if (jv instanceof JsonString) {
				JsonString js = (JsonString) jv;
				p.set(js.getString());
				return true;
			}
			return false;
		}

		@Override
		public boolean serializeSubObject(String name,
				Consumer<JSONSerializer> sub) {
			JsonValue jv = j.get(name);
			if (jv instanceof JsonObject) {
				sub.accept(new Reader((JsonObject) jv));
				return true;
			}
			return false;
		}

		@Override
		public JsonObject get() {
			return j;
		}

		@Override
		public <E extends Enum<E>> boolean serializeEnum(ObjectProperty<E> p,
				Function<String, E> valueOf) {
			JsonValue jv = j.get(p.getName());
			if (jv instanceof JsonString) {
				E e = valueOf.apply(((JsonString) jv).getString());
				if (e != null) {
					p.set(e);
					return true;
				}
			}
			return false;
		}

		@Override
		public void write(File f) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> boolean serializeObjectList(String name, List<T> list,
				Supplier<T> new_item,
				BiConsumer<T, JSONSerializer> serialize_item) {
			JsonValue jv = j.get(name);
			if (jv instanceof JsonArray) {
				JsonArray ja = (JsonArray) jv;
				for (JsonValue jv2 : ja) {
					if (jv2 instanceof JsonObject) {
						JsonObject jo2 = (JsonObject) jv2;
						T item = new_item.get();
						serialize_item.accept(item, new Reader(jo2));
						list.add(item);
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public <T> boolean serializeStringList(String name, List<String> list) {
			JsonValue jv = j.get(name);
			if (jv instanceof JsonArray) {
				JsonArray ja = (JsonArray) jv;
				for (JsonValue jv2 : ja) {
					if (jv2 instanceof JsonString) {
						JsonString js = (JsonString) jv2;
						list.add(js.getString());
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public <T> boolean serializeObjectListAsStrings(String name,
				List<T> list, Function<T, String> encoder,
				Function<String, T> decoder) {
			JsonValue jv = j.get(name);
			if (jv instanceof JsonArray) {
				JsonArray ja = (JsonArray) jv;
				for (JsonValue jv2 : ja) {
					if (jv2 instanceof JsonString) {
						JsonString js = (JsonString) jv2;
						T item = decoder.apply(js.getString());
						if (item != null)
							list.add(item);
					}
				}
				return true;
			}
			return false;
		}

	}

	static JSONSerializer reader(JsonObject r) {
		return new Reader(r);
	}

	static JSONSerializer reader(File f) throws IOException {
		if (f.exists()) {
			try (JsonReader r = Json.createReader(new FileInputStream(f))) {
				JSONSerializer s = reader(r.readObject());
				r.close();
				return s;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reader(Json.createObjectBuilder().build());
	}
}
