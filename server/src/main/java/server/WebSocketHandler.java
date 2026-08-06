package server;

public class WebSocketHandler {
    public void handleMessage(Object ctx, String message) {
        System.out.println("Message: " + message);
    }
}