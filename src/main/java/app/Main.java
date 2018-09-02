package app;

import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create()
            .sessionHandler(Sessions::fileSessionHandler)
            .start(7000);
    }
}
