package ru.job4j.gc.leak;

import ru.job4j.gc.leak.models.Post;

import java.util.Random;
import java.util.Scanner;

public class Menu {

    public static final int ADD_POST = 1;
    public static final int ADD_MANY_POST = 2;
    public static final int SHOW_ALL_POSTS = 3;
    public static final int DELETE_POST = 4;
    public static final int DELETE_POST_WITH_ID = 5;

    public static final String SELECT = "Выберите меню";
    public static final String COUNT = "Выберите количество создаваемых постов";
    public static final String TEXT_OF_POST = "Введите текст";
    public static final String EXIT = "Конец работы";
    public static final String ID_REQUEST = "Введите id поста";

    public static final String MENU = """
                Введите 1 для создание поста.
                Введите 2, чтобы создать определенное количество постов.
                Введите 3, чтобы показать все посты.
                Введите 4, чтобы удалить все посты.
                Введите 5, чтобы удалить пост по id.
                Введите любое другое число для выхода.
            """;

    public static void main(String[] args) {
        Random random = new Random();
        UserGenerator userGenerator = new UserGenerator(random);
        CommentGenerator commentGenerator = new CommentGenerator(random, userGenerator);
        Scanner scanner = new Scanner(System.in);
        PostStore postStore = new PostStore();
        start(commentGenerator, scanner, userGenerator, postStore);
    }

    private static void start(CommentGenerator commentGenerator, Scanner scanner, UserGenerator userGenerator, PostStore postStore) {
        boolean run = true;
        while (run) {
            System.out.println(MENU);
            System.out.println(SELECT);
            int userChoice = Integer.parseInt(scanner.nextLine());
            System.out.println(userChoice);
            if (ADD_POST == userChoice) {
                System.out.println(TEXT_OF_POST);
                String text = scanner.nextLine();
                userGenerator.generate();
                commentGenerator.generate();
                var post = new Post();
                post.setText(text);
                post.setComments(commentGenerator.getComments());
                var saved = postStore.add(post);
                System.out.println("Generate: " + saved.getId());
            } else if (ADD_MANY_POST == userChoice) {
                System.out.println(TEXT_OF_POST);
                String text = scanner.nextLine();
                System.out.println(COUNT);
                String count = scanner.nextLine();
                memUsage();
                for (int i = 0; i < Integer.parseInt(count); i++) {
                    System.out.printf("\rGenerate %.2f%% %.2fMb",
                            ((double) i / Integer.parseInt(count)) * 100,
                            memUsage());
                    createPost(commentGenerator, userGenerator, postStore, text);
                }
                System.out.println();
                memUsage();
            } else if (SHOW_ALL_POSTS == userChoice) {
                System.out.println(postStore.getPosts());
            } else if (DELETE_POST == userChoice) {
                System.out.println("Удаление всех постов ...");
                System.out.printf("До удаления: %.2f MB%n", memUsage());
                postStore.removeAll();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("После удаления: %.2f MB%n", memUsage());
            } else if (DELETE_POST_WITH_ID == userChoice) {
                System.out.println(ID_REQUEST);
                String id = scanner.nextLine();
                if (postStore.removeWithId(Integer.parseInt(id))) {
                    System.out.printf("Пост с id %s удален%n", id);
                } else {
                    System.out.printf("Поста с id %s не существует%n", id);
                }
            } else {
                run = false;
                System.out.println(EXIT);
            }
        }
    }

    private static double memUsage() {
        var rt = Runtime.getRuntime();
        var totalMem = rt.totalMemory();
        var freeMem = rt.freeMemory();
        var usedMem = totalMem - freeMem;
        return (double) usedMem / 1024 / 1024;
    }

    private static void createPost(CommentGenerator commentGenerator,
                                   UserGenerator userGenerator,
                                   PostStore postStore, String text) {
        userGenerator.generate();
        commentGenerator.generate();
        var post = new Post();
        post.setText(text);
        post.setComments(commentGenerator.getComments());
        postStore.add(post);
    }
}