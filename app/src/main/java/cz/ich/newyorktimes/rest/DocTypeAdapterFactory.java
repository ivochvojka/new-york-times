package cz.ich.newyorktimes.rest;

import com.google.gson.JsonElement;

import cz.ich.newyorktimes.pojo.Doc;

/**
 * Adapter factory for {@link Doc}.
 *
 * @author Ivo Chvojka
 */
public class DocTypeAdapterFactory extends CustomTypeAdapterFactory<Doc> {
    public DocTypeAdapterFactory() {
        super(Doc.class);
    }

    @Override protected void write(Doc source, JsonElement jsonElement) {
    }

    @Override protected void read(JsonElement jsonElement) {
        // byline is sometimes array and sometimes object, but useless for us
        jsonElement.getAsJsonObject().remove("byline");
    }
}
