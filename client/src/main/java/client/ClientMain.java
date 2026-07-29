package client;

public class ClientMain {
    public static void main(String[] args) {
        var port = 8080;
        new Repl(port).run();
    }
}
