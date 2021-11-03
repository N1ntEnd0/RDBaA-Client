package parse;

import dto.Music;
import exception.CommandException;
import http.HttpConnection;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class CommandParser {

    private static CommandParser instance;
    public static ArrayList<ArrayList<String>> commands = new ArrayList<>();
    private static HttpConnection httpConnection;
    private Scanner scanner = new Scanner(System.in);;

    private CommandParser(Properties properties) throws MalformedURLException {
        String[] string_commands = properties.get("allowed-commands").toString().split("&");
        httpConnection = HttpConnection.getInstance((String) properties.get("server-add-url"),
                (String) properties.get("server-search-url"),
                (String) properties.get("server-delete-url"));
        for (String command: string_commands) {
            commands.add(new ArrayList(Arrays.asList(command.split(" "))));
        }
        commands.forEach(System.out::println);
    }



    public void parseCommand(String command) throws CommandException {
        String[] partsOfCommand = command.split(" ");
        String name;
        String authorName;
        switch (partsOfCommand[0]) {
            case "add":
                System.out.println("Введите имя автора");
                authorName = scanner.nextLine();
                System.out.println("Введите название");
                name = scanner.nextLine();
                if (!authorName.equals(" ") && !name.equals(" ")) {
                    Music music = new Music(authorName, name);
                    httpConnection.sendAddCommand(music);
                } else {
                    throw new CommandException("неверные параметры комманды");
                }
                break;
            case "list":
                try {
                    httpConnection.sendListCommand(null);
                } catch (MalformedURLException e) {
                    throw new CommandException("Ошибка подключения к серверу");
                }
                break;
            case "search":
                try {
                    System.out.println("Введите имя автора");
                    authorName = scanner.nextLine();
                    if (!authorName.equals(" ")) {
                        httpConnection.sendListCommand(authorName);
                    } else {
                        throw new CommandException("Команда search должна содержать имя автора");
                    }
                } catch (MalformedURLException e) {
                    throw new CommandException("Ошибка подключения к серверу");
                }
                break;
            case "delete":
                System.out.println("Введите название");
                name = scanner.nextLine();
                if (!name.equals(" ")) {
                    httpConnection.sendDelCommand(name);
                } else {
                    throw new CommandException("Команда delete должна содержать название");
                }
                break;
            case "exit":
                System.out.println("Сеанс завершен");
                System.exit(0);
            default:
                throw new CommandException("Неизвестная командой");
        }
    }

    public static CommandParser getInstance(Properties properties) throws MalformedURLException {
        if (instance == null) {
            instance = new CommandParser(properties);
        }
        return instance;
    }
}



//add добавление составных имен
//search
//delete
//exit