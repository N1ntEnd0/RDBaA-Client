package console;

import com.google.gson.Gson;
import dto.User;
import exception.CommandException;
import http.HttpConnection;
import parse.CommandParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class ConsoleReader {

    private static ConsoleReader instance;
    private Properties properties;
    private CommandParser parser;
    private Scanner in = new Scanner(System.in);
    private User user = new User();
    private static HttpConnection httpConnection;


    private ConsoleReader() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties = new Properties();
            properties.load(input);
            parser = CommandParser.getInstance(properties);
            httpConnection = HttpConnection.getInstance((String) properties.get("server-add-url"),
                    (String) properties.get("server-search-url"),
                    (String) properties.get("server-delete-url"),
                    (String) properties.get("server-sign-in-url"),
                    (String) properties.get("server-sign-up-url")
            );
        } catch (IOException e) {
            System.out.println("Ошибка старта программы. Отсутствует файл конфигурации");
            System.exit(0);
        }
    }


    private void authorization() {
        System.out.println("Enter the number of action and press [Enter]. Then follow instructions.");
        while (true) {
            System.out.println(this.authInfo());
            String cmd = in.nextLine();
            switch (cmd) {
                case "1":
                case "2":
                    break;
                default:
                    System.out.println("Unknown param try again");
                    continue;
            }
            String password;
            System.out.println("login");
            String login = in.nextLine();
            if (login != null && !login.equals("")) {
                System.out.println("password");
                password = in.nextLine();
                if (password != null && !password.equals("")) {
                    user.setLogin(login);
                    user.setPassword(password);
                        if (Objects.equals(cmd, "1") && httpConnection.signIn(user)){
                            System.out.println("Successfully logged in");
                            return;
                        }
                        if (Objects.equals(cmd, "2") && httpConnection.signUp(user)) {
                            System.out.println("Successfully registered");
                            return;
                        }
                        else {
                            System.out.println("Invalid data of user");
                        }
//                    if (Objects.equals(cmd, "1") && this.signIn(user)) return;
//                    if (Objects.equals(cmd, "2") && this.signUp(user)) return;
                }
            }
        }
    }

    public void read(){

        System.out.println("Welcome to Music catalog");
        authorization();

        while (true) {
            System.out.println(this.info());
            System.out.println("Enter command:");
            try {
                this.parser.parseCommand(in.nextLine(), user);
            } catch (CommandException e) {
                System.err.println(e.getMessage());
                continue;
            }
        }
    }

    private String authInfo() {
        return properties.getProperty("auth-message");
    }

    private String info(){
        return properties.getProperty("help-message");
    }


    public static ConsoleReader getInstance(){
        if (instance == null) {
            instance = new ConsoleReader();
        }
        return instance;
    }
}
