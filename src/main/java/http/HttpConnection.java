package http;

import com.google.gson.Gson;
import dto.MessageDTO;
import dto.Music;
import dto.User;
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
    URL sign_in_url;
    URL sign_up_url;


    private HttpConnection(String add_url, String search_url, String delete_url, String sign_in_url, String sign_up_url) throws MalformedURLException {
        this.add_url = new URL(add_url);
        this.search_url = new URL(search_url);
        this.delete_url = new URL(delete_url);
        this.sign_in_url = new URL(sign_in_url);
        this.sign_up_url = new URL(sign_up_url);
    }

    public static HttpConnection getInstance(String add_url, String search_url, String delete_url, String sign_in_url, String sign_up_url) throws MalformedURLException {
        if (instance == null) {
            instance = new HttpConnection(add_url, search_url, delete_url, sign_in_url, sign_up_url);
        }
        return instance;
    }

    public boolean signIn(User user) {
        try {
            HttpURLConnection con = (HttpURLConnection)sign_in_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(user);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            return con.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка доступа к серверу");
            System.exit(0);
            return false;
        }
    }

    public boolean signUp(User user) {
        try {
            HttpURLConnection con = (HttpURLConnection)sign_up_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(user);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            return con.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка доступа к серверу");
            System.exit(0);
            return false;
        }
    }

    public void sendAddCommand(MessageDTO messageDTO) {
        try {
            HttpURLConnection con = (HttpURLConnection) add_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(messageDTO);
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

    public void sendListCommand(MessageDTO messageDTO) throws MalformedURLException {
//        URL url;
//        if (messageDTO.getAuthorName() != null) {
//            url = new URL(search_url.toString() + "?authorName="+messageDTO.getAuthorName());
//        } else {
//            url = new URL(search_url.toString());
//        }
//        HttpURLConnection con = null;
        ArrayList<Music> musics = new ArrayList<>();
        try {
            HttpURLConnection con = (HttpURLConnection) search_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(messageDTO);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
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
//                    music.setId((long)  ((JSONObject) jsonObject).get("id"));
                    musics.add(music);
                }
                musics.forEach(System.out::println);

            }else{
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }


            /*con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
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
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDelCommand(MessageDTO messageDTO) {
        try {
            HttpURLConnection con = (HttpURLConnection) delete_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(messageDTO);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
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
