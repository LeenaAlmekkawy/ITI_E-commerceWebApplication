/*package jets.projects.utils;

import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class JsonResponseConverter {

    private static final Gson PRETTY_GSON;

    static {
        PRETTY_GSON = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .setPrettyPrinting()
                .create();
    }

    private JsonResponseConverter() {
        // Prevent instantiation
    }

    public static String toJsonResponse(Object data, boolean success) {
        JsonElement dataJson = PRETTY_GSON.toJsonTree(data);

        JsonObject result = new JsonObject();
        result.addProperty("success", success); // Dynamic success value
        result.add("data", dataJson);

        return PRETTY_GSON.toJson(result);
    }
}
 */
package jets.projects.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

public class JsonResponseConverter {

    private static final Gson PRETTY_GSON;

    static {
        PRETTY_GSON = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                        (JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                        -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }

    private JsonResponseConverter() {
        // Prevent instantiation
    }

    /**
     * Converts data to pretty-printed JSON with success flag
     *
     * @param data Can be a single object or a Collection
     * @param success Boolean to indicate operation success
     * @return Formatted JSON string: {success: boolean, data: ...}
     */
    public static String toJsonResponse(Object data, boolean success) {
        JsonElement dataJson = PRETTY_GSON.toJsonTree(data);

        JsonObject result = new JsonObject();
        result.addProperty("success", success);
        result.add("data", dataJson);

        return PRETTY_GSON.toJson(result);
    }

    // Custom adapter for LocalDateTime
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc,
                com.google.gson.JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                com.google.gson.JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), FORMATTER);
        }
    }
}
