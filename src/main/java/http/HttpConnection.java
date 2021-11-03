package http;

import com.google.gson.Gson;
import dto.Music;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HttpConnection {
    private static HttpConnection instance;
    URL add_url;
    URL search_url;
    URL delete_url;

    private HttpConnection(String add_url, String search_url, String delete_url) throws MalformedURLException {
        this.add_url = new URL(add_url);
        this.search_url = new URL(search_url);
        this.delete_url = new URL(delete_url);
    }

    public static HttpConnection getInstance(String add_url, String search_url, String delete_url) throws MalformedURLException {
        if (instance == null) {
            instance = new HttpConnection(add_url, search_url, delete_url);
        }
        return instance;
    }

    public void sendAddCommand(Music music) {
        try {
            HttpURLConnection con = (HttpURLConnection) add_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(music);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            if (con.getResponseCode() == 201) {
                System.out.println("Песня была добавлена");
            }
        } catch (IOException e) {
            System.out.println("Ошибка доступа к серверу");
            System.exit(0);
        }
    }

    public void sendListCommand(String value) throws MalformedURLException {
        URL url;
        if (value != null) {
            url = new URL(search_url.toString() + "?authorName="+value);
        } else {
            url = new URL(search_url.toString());
        }
        HttpURLConnection con = null;
        ArrayList<Music> musics = new ArrayList<>();
        try {
            con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.connect();
            int status = con.getResponseCode();
            if (status == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                JSONArray books_json = (JSONArray) JSONValue.parse(sb.toString());
                for (Object jsonObject : books_json){
                    Music music = new Music();
                    music.setName((String) ((JSONObject) jsonObject).get("name"));
                    music.setAuthorName((String) ((JSONObject) jsonObject).get("authorName"));
                    music.setId((long)  ((JSONObject) jsonObject).get("id"));
                    musics.add(music);
                }
                musics.forEach(System.out::println);

            }else{
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDelCommand(String name) {
        try {
            HttpURLConnection con = (HttpURLConnection) delete_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = name.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            if (con.getResponseCode() == 201) {
                System.out.println("Песня была удалена");
            }
        } catch (IOException e) {
            System.out.println("Ошибка доступа к серверу");
            System.exit(0);
        }
    }
}
