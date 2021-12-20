package parse;

import dto.MessageDTO;
import dto.Music;
import dto.User;
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
                (String) properties.get("server-delete-url"),
                (String) properties.get("server-sign-in-url"),
                (String) properties.get("server-sign-up-url")
                );
        for (String command: string_commands) {
            commands.add(new ArrayList(Arrays.asList(command.split(" "))));
        }
        commands.forEach(System.out::println);
    }

    public void parseCommand(String command, User user) throws CommandException {
        String[] partsOfCommand = command.split(" ");
        String name;
        String authorName;
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setLogin(user.getLogin());
        messageDTO.setPassword(user.getPassword());
        switch (partsOfCommand[0]) {
            case "1":
                System.out.println("Введите имя автора");
                authorName = scanner.nextLine();
                System.out.println("Введите название");
                name = scanner.nextLine();
                if (!authorName.equals(" ") && !name.equals(" ")) {
//                    Music music = new Music(authorName, name);
                    messageDTO.setName(name);
                    messageDTO.setAuthorName(authorName);
                    httpConnection.sendAddCommand(messageDTO);
                } else {
                    throw new CommandException("неверные параметры комманды");
                }
                break;
            case "2":
                try {
                    httpConnection.sendListCommand(messageDTO);
                } catch (MalformedURLException e) {
                    throw new CommandException("Ошибка подключения к серверу");
                }
                break;
            case "3":
                try {
                    System.out.println("Введите имя автора");
                    messageDTO.setAuthorName(scanner.nextLine());
//                    authorName = scanner.nextLine();
                    if (!messageDTO.getAuthorName().equals(" ")) {
                        httpConnection.sendListCommand(messageDTO);
                    } else {
                        throw new CommandException("Команда search должна содержать имя автора");
                    }
                } catch (MalformedURLException e) {
                    throw new CommandException("Ошибка подключения к серверу");
                }
                break;
            case "4":
                System.out.println("Введите название");
//                name = scanner.nextLine();
                messageDTO.setName(scanner.nextLine());
                if (!messageDTO.getName().equals(" ")) {
                    httpConnection.sendDelCommand(messageDTO);
                } else {
                    throw new CommandException("Команда delete должна содержать название");
                }
                break;
            case "5":
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
