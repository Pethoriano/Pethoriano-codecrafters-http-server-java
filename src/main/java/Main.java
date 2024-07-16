import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {


        try {

            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);

            while(true){

                Socket clientSocket = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
                connectionHandler.start();

            }
        } catch (IOException e) {

            System.out.println("IOException: " + e.getMessage());

        }
    }
}

class ConnectionHandler extends Thread {

    private Socket clientSocket;


    public ConnectionHandler (Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run (){

        try{

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientRequest = bufferedReader.readLine();
            String[] requestParts = clientRequest.split(" ");
            String path = requestParts[1];

            if ("/".equals(path)) {

                clientSocket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());

            } else if (path.startsWith("/echo/")) {

                String echoString = path.substring(6);
                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "
                        + echoString.length() + "\r\n\r\n" + echoString;
                clientSocket.getOutputStream().write(response.getBytes());

            } else if (path.startsWith("/user-agent")) {

                String headerLine;
                String userAgentValue = "";

                while ((headerLine = bufferedReader.readLine()) != null) {

                    if (headerLine.isEmpty()) {

                        break;

                    }

                    if (headerLine.startsWith("User-Agent:")) {
                        userAgentValue = headerLine.substring("User-Agent: ".length()).trim();
                    }
                }

                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "
                        + userAgentValue.length() + "\r\n\r\n" + userAgentValue;

                clientSocket.getOutputStream().write(response.getBytes());

            } else {
                clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
            }

            clientSocket.close();

        }catch(IOException e){

            System.out.println("IO Exception: " + e.getMessage());

        }

    }

}