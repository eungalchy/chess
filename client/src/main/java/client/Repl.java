package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade server;
    private PreloginClient preloginClient;
    private PostloginClient postloginClient;
    private State state = State.PRELOGIN;

    private enum State {
        PRELOGIN,
        POSTLOGIN
    }

    public Repl(int port) {
        server = new ServerFacade(port);
        preloginClient = new PreloginClient(server);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Type help to get started. ♕");
        System.out.print(preloginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                if (state == State.PRELOGIN) {
                    result = preloginClient.eval(line);
                    System.out.print(result);

                    if (preloginClient.isLoggedIn()) {
                        var authData = preloginClient.getAuthData();
                        postloginClient = new PostloginClient(server, authData.authToken());
                        state = State.POSTLOGIN;
                        System.out.print("\n" + postloginClient.help());
                    }
                } else {
                    result = postloginClient.eval(line);
                    System.out.print(result);

                    if (postloginClient.isLoggedOut()) {
                        preloginClient = new PreloginClient(server);
                        state = State.PRELOGIN;
                        result = "";
                        System.out.print("\n" + preloginClient.help());
                    }
                }
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}