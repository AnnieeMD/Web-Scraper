package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WebScraperServer {

    public static void main(String[] args) {

        int port = 8080;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server started on port " + port);

            while(true) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected...");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }


        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {

        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {


                while (true) {

                    String input = in.readLine();
                    if (input == null) {
                        break;
                    }

                    try {

                        int numLines = Integer.parseInt(input);
                        String response = getFirstNRows(numLines);
                        out.println(response);

                    } catch (NumberFormatException e) {

                        out.println("Error: Invalid number. Please send a valid integer.");
                    }
                }

                System.out.println("Client has successfully completed and terminated.");

            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    private static String getFirstNRows(int n) throws IOException {

        String url = "example.html";

        Document document = Jsoup.parse(new File(url), "UTF-8");
        Elements bodyElements = document.body().getAllElements();

        StringBuilder result = new StringBuilder();
        int lineCount = 0;

        for (Element element : bodyElements) {

            String text = element.ownText().trim();

            if (!text.isEmpty()) {

                result.append(text).append("\n");
                lineCount++;

                if (lineCount >= n) break;
            }
        }

        return result.toString();
    }
}
