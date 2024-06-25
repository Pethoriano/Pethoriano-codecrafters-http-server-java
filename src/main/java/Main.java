import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader bufferedReader = null;

        try {

            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientRequest = bufferedReader.readLine();
            String[] requestParts = clientRequest.split(" ");
            String path = requestParts[1];

            if ("/".equals(path)) {
                clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            } else if (path.startsWith("/echo/")) {
                String echoString = path.substring(6);
                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + echoString.length() + "\r\n\r\n" + echoString;
                clientSocket.getOutputStream().write(response.getBytes());
            } else {
                clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }


        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

    }
}
