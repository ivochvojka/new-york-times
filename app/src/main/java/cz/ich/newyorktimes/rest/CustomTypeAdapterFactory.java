package cz.ich.newyorktimes.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Adapter factory for JSON conversions.
 *
 * @author Ivo Chvojka
 */
public abstract class CustomTypeAdapterFactory<C> implements TypeAdapterFactory {

    private final Class<C> clazz;

    /**
     * Constructor.
     *
     * @param clazz - Generated class by jsonSchema2Pojo
     */
    public CustomTypeAdapterFactory(Class<C> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return type.getRawType() == clazz
                ? (TypeAdapter<T>) createAdapter(gson, (TypeToken<C>) type)
                : null;
    }

    /**
     * Create custom adapter for generated pojo class.
     *
     * @param gson GSON instance
     * @param type Concrete type to work with
     * @return Concrete type adapter
     */
    private TypeAdapter<C> createAdapter(Gson gson, TypeToken<C> type) {

        // GSON adapter for JsonElement
        final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);

        // Concrete adapter
        final TypeAdapter<C> concreteAdapter = gson.getDelegateAdapter(this, type);

        final TypeAdapter<C> typeAdapter = new TypeAdapter<C>() {

            @Override
            public void write(JsonWriter out, C value) throws IOException {
                JsonElement jsonElement = concreteAdapter.toJsonTree(value);
                CustomTypeAdapterFactory.this.write(value, jsonElement);
                elementAdapter.write(out, jsonElement);
            }

            @Override
            public C read(JsonReader in) throws IOException {
                JsonElement jsonElement = elementAdapter.read(in);
                CustomTypeAdapterFactory.this.read(jsonElement);
                return concreteAdapter.fromJsonTree(jsonElement);
            }
        };

        return typeAdapter;
    }

    /**
     * Override this with concrete adapter.
     *
     * @param source Concrete source object
     * @param jsonElement JSON element to serialize
     */
    protected void write(C source, JsonElement jsonElement) {
    }

    /**
     * Override this with concrete adapter.
     *
     * @param jsonElement - JSON element do deserialize
     */
    protected void read(JsonElement jsonElement) {
    }
}