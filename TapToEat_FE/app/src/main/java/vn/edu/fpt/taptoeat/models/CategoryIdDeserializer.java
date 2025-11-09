package vn.edu.fpt.taptoeat.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Custom deserializer to handle categoryId which can be:
 * - STRING: "69101ef952f23221431cc1d6" (from /api/categories/:id/items)
 * - OBJECT: {"_id": "...", "name": "...", "displayOrder": 1} (from /api/menu-items)
 */
public class CategoryIdDeserializer implements JsonDeserializer<MenuItem.CategoryIdWrapper> {

    @Override
    public MenuItem.CategoryIdWrapper deserialize(JsonElement json, Type typeOfT, 
                                                   JsonDeserializationContext context) 
            throws JsonParseException {
        
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            // Case 1: categoryId is a String
            String id = json.getAsString();
            return new MenuItem.CategoryIdWrapper(id, null, 0);
            
        } else if (json.isJsonObject()) {
            // Case 2: categoryId is an Object with _id, name, displayOrder
            JsonObject obj = json.getAsJsonObject();
            String id = obj.has("_id") ? obj.get("_id").getAsString() : null;
            String name = obj.has("name") ? obj.get("name").getAsString() : null;
            int displayOrder = obj.has("displayOrder") ? obj.get("displayOrder").getAsInt() : 0;
            return new MenuItem.CategoryIdWrapper(id, name, displayOrder);
            
        } else {
            // Unexpected format, return null
            return null;
        }
    }
}
