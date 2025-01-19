package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WebScraperClient {

    public static void main(String[] args) {

        String serverAddress = "localhost";
        int port = 8080;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            Scanner scanner = new Scanner(System.in);
            String input;

            while (true) {

                System.out.print("Enter the number of rows (n) to fetch, or 'exit' to quit: ");
                input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Exiting client...");
                    break;
                }

                try {

                    int numLines = Integer.parseInt(input);
                    if (numLines <= 0) {

                        throw new IllegalArgumentException("The number of rows cannot be negative or zero.");

                    }

                    out.println(numLines);

                    String responseLine;
                    StringBuilder content = new StringBuilder();
                    while ((responseLine = in.readLine()) != null && !responseLine.isEmpty()) {
                        content.append(responseLine).append("\n");
                    }

                    System.out.println("\nResponse from server:\n" + content);

                } catch (NumberFormatException e) {

                    System.out.println("Error: Invalid input. Please enter a valid number or 'exit'.");

                } catch (IllegalArgumentException e) {

                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch(IOException e) {

            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}
