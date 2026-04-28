
package de.felix.lifeplugin.ai;

import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenAIService {

    private final String apiKey;
    private final String model;

    public OpenAIService(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    public String ask(String prompt) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JsonObject json = new JsonObject();
            json.addProperty("model", model);

            JsonArray messages = new JsonArray();

            JsonObject system = new JsonObject();
            system.addProperty("role", "system");
            system.addProperty("content", "Only respond in JSON.");

            JsonObject user = new JsonObject();
            user.addProperty("role", "user");
            user.addProperty("content", prompt);

            messages.add(system);
            messages.add(user);

            json.add("messages", messages);

            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JsonObject res = JsonParser.parseString(response.toString()).getAsJsonObject();

            return res.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
