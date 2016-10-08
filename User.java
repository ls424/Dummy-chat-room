import java.io.*;
import java.net.*;
import java.util.*;


public class User extends Thread {
    
    // The user socket
    private static Socket userSocket = null;
    // The output stream
    private static PrintStream output_stream = null;
    // The input stream
    private static BufferedReader input_stream = null;
    
    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    
    public static void main(String[] args) {
        // The default port.
        int portNumber = 8000;
        // int portNumber = 58999;

        // The default host.
        String host = "localhost";
        // String host = "csa2.bu.edu";
        
    /*
     * Open a socket on a given host and port. Open input and output streams.
     */
        try {
            input_stream = new BufferedReader(new InputStreamReader(System.in));
            userSocket = new Socket(host, portNumber);
            output_stream = new PrintStream(userSocket.getOutputStream()); //msg send to server
            inputLine = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
    /*
     * If everything has been initialized then create a listening thread to 
     * read from the server. 
     * Also send any user’s message to server until user logs out.
     */                        


            new Thread(new User()).start();

            while (closed == false) {
                output_stream.println(input_stream.readLine()); //write data to server
                // output_stream.println("test info");
            }
        }catch(Exception e) {
            System.out.println("Error:" + e);
        }
    }

    public void run() {
        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break and close the connection.
         */
        try{
            // System.out.println("test start");
            while (true) {              
                 //msg from server     
                String received = inputLine.readLine();
                if (received.indexOf("Bye") >= 0) {
                    closed = true;
                    input_stream.close();
                    output_stream.close();
                    inputLine.close();
                    userSocket.close();
                    break;
                }
                System.out.println(received);  
            }
                       
        } catch (Exception ex) {
            System.out.println("UserError:" + ex);
        }
    }
}


