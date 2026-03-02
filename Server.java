

import java.io.*;
import java.net.*;
import java.time.*;
import java.util.*;

public class Server {
    private static final int PORT = 1159; //port to listen on
    private static final LocalDateTime startTime = LocalDateTime.now(); //track server uptime

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT); // creates the server listener // where it listens to
        System.out.println("Server started. Listening on port " + PORT);

        while (true) { //keep server running 
        	Socket clientSocket = serverSocket.accept();
        	new Thread(() -> { // new thread for each request
        	    try {
        	        handleClient(clientSocket); //perform the operation requested & return the data
        	    } catch (IOException e) {
        	        e.printStackTrace();
        	    }
        	}).start();

        }//end while loop
    }//end main method
    //===============================

    private static void handleClient(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  //to read from the socket/ client
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //to write to the client
      
               
        String request = in.readLine().trim(); // read client request, ***had issues converting it to an int, so i kept it as a string for the switch menu
        String response;

        //all the requests the client can make
        switch (request) {
            case "1": 
                response = LocalDateTime.now().toString();
                break;
            case "2":
                Duration uptime = Duration.between(startTime, LocalDateTime.now());
                response = uptime.toString();
                break;
            case "3":
                long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                response = "Memory used: " + usedMemory + " bytes";
                break;
            case "4":
                response = runCommand("netstat"); //uses linux commands b/c thats what works on the course server
                break;
            case "5":
                response = runCommand("who");
                break;
            case "6":
                response = runCommand("ps -e");
                break;
            default:
                response = "Invalid request";
        }//end switch-case 

        out.println(response); //send the output
        socket.close(); //close the connection
    }//end handleClient method
    //================================

    // execute system commands and capture output
    private static String runCommand(String command) {
        StringBuilder output = new StringBuilder(); 
        try {
            Process process = Runtime.getRuntime().exec(command); //executes terminal command , process class used to execute tasks (code, input, output) 
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //bufferedReader reading from the process inputStream
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }//end while loop
        } catch (IOException e) {
            return "Error running command: " + e.getMessage();
        }
        return output.toString();
    }//end run command method
    //============================
    
}//end Server class