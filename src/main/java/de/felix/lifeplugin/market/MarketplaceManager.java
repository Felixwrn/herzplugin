package de.felix.lifeplugin.market;

import com.google.gson.*;
import java.net.URL;
import java.util.*;

public class MarketplaceManager {

    private static final String URL_STRING =
            "https://raw.githubusercontent.com/YOUR/REPO/main/marketplace.json";

    public static Map<String, JsonObject> load() {

        Map<String, JsonObject> map = new HashMap<>();

        try {
            Scanner sc = new Scanner(new URL(URL_STRING).openStream()).useDelimiter("\\A");
            String jsonText = sc.next();

            JsonObject json = JsonParser.parseString(jsonText).getAsJsonObject();

            for (String key : json.keySet()) {
                map.put(key, json.getAsJsonObject(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
