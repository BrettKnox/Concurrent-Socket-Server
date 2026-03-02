import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);  // in for reading user input

        //get server info 
        System.out.print("Enter server IP address: ");
        String serverAddress = in.nextLine();

        System.out.print("Enter server port: ");
        int port = in.nextInt();
        in.nextLine();


        while (true) {

            //show options to the user
            System.out.println("Select command:");
            System.out.println("1 : Date and Time");
            System.out.println("2 : Uptime");
            System.out.println("3 : Memory Used");
            System.out.println("4 : Netstat");
            System.out.println("5 : Current Users");
            System.out.println("6 : Running Processes");
            System.out.println("7 : Exit Program");

            String command = in.nextLine();

            if(command.equals("7")) {//added an exit option to match the example in the video
                break;
            }

            //ask how many threads (clients) to spawn
            System.out.print("Enter number of requests (1, 5, 10, 15, 20, 25, 100): ");
            int count = in.nextInt();
            in.nextLine();

            //shared list to store times for each thread 
            List<Double> times = Collections.synchronizedList(new ArrayList<>());

            //array to hold each thread object
            Thread[] threads = new Thread[count];

            //create and start threads
            for (int i = 0; i < count; i++) { //loop through as many times as user specified (number of threads)
                final int index = i; //for user id

                //create a thread 
                threads[i] = new Thread(new Runnable() {
                    public void run() { //code block that will be run in new thread
                        try {
                            //connect to the server
                            Socket socket = new Socket(serverAddress, port);

                            //i/o objects
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //writer
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //reader

                            //start timer
                            double start = System.nanoTime();


                            out.println(command);//send the selected command
                            StringBuilder response = new StringBuilder(); 
                            String st; 
                            while((st = in.readLine()) != null) //read server's reply
                            {
                            	response.append(st).append("\n");
                            }
                            

                            //stop timer + log time
                            double end = System.nanoTime();
                            double turnaround = (end - start) / 1_000_000.0; //convert to milliseconds

                            // save times
                            synchronized (times) {// only one thread implements the times method at a time 
                                times.add(turnaround);
                            }

                            // print response
                            System.out.println("[Client " + index + "] Response: " + response);

                            // close everything
                            in.close();
                            out.close();
                            socket.close();
                        } catch (IOException e) {
                            System.out.println("[Client " + index + "] Error: " + e.getMessage());
                        }
                    }//end run
                });
                //start the threads
                threads[i].start();
            }//end for loop


            for (Thread t : threads) {
                t.join(); //ensures all threads complete before moving on
            }//end for loop

            // calculate total time
            double total = 0.0;
            for (double t : times)
            {
                total += t;
            }//end for loop

            double average =  total / times.size();

            System.out.println("Total Time: " + total + " ms");
            System.out.println("Average Time: " + average + " ms");
        }//end while loop
    }// end main method
}
