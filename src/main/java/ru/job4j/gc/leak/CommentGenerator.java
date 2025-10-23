package ru.job4j.gc.leak;

import ru.job4j.gc.leak.models.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentGenerator implements Generate {

    public final String pathPhrases = "files/phrases.txt";

    public final String separator = System.lineSeparator();
    private final List<Comment> comments = new ArrayList<>();
    public final Integer count = 50;
    private List<String> phrases;
    private final UserGenerator userGenerator;
    private final Random random;

    public CommentGenerator(Random random, UserGenerator userGenerator) {
        this.userGenerator = userGenerator;
        this.random = random;
        read();
    }

    private void read() {
        try {
            if (phrases == null) {
                phrases = read(pathPhrases);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Comment> getComments() {
        return comments;
    }

    @Override
    public void generate() {
        comments.clear();
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(phrases.get(random.nextInt(phrases.size())))
                    .append(separator)
                    .append(phrases.get(random.nextInt(phrases.size())))
                    .append(separator)
                    .append(phrases.get(random.nextInt(phrases.size())));
            var comment = new Comment();
            comment.setText(sb.toString());
            comment.setUser(userGenerator.randomUser());
            comments.add(comment);
        }
    }
}