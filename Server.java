import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;



/*
 * A chat server that delivers public and private messages.
 * Author: shuo
 */
public class Server {
    
    // Create a socket for the server 
    private static ServerSocket serverSocket = null;
    // Create a socket for the user 
    private static Socket userSocket = null;
    // Maximum number of users 
    private static int maxUsersCount = 5;
    // An array of threads for users
    private static userThread[] threads = null;

    public static void main(String args[]) {
        
        // The default port number.
        // int portNumber = 58424;
        int portNumber = 8000;
        
        /*
         * Create a user socket for each connection and pass it to a new user
         * thread.
         */

                

        try {
            serverSocket = new ServerSocket(portNumber);//wait for request
            threads = new userThread[maxUsersCount];
            while (true) {
                userSocket = serverSocket.accept(); 
                int i = 0;
                while (i < maxUsersCount) {
                    if (threads[i] == null) {
                        threads[i] = new userThread(userSocket, threads);
                        threads[i].start();
                        break;
                    }
                    i++;
                }
                if (i == maxUsersCount) { // no open seats for new user
                    // PrintStream output = new PrintStream()
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

/*
 * Threads
 */
class userThread extends Thread {
    
    private String userName = null;
    private Socket userSocket = null;
    private int maxUsersCount;

    private BufferedReader input_stream = null;
    private PrintStream output_stream = null;
    private final userThread[] threads;
    private Set<String> set = new HashSet<String>();

    // only relevant for Part IV: adding friendship
    private ArrayList<String> friends = new ArrayList<String>();
    private ArrayList<String> friendrequests = new ArrayList<String>();  //keep track of sent friend requests 
    //

    
    public userThread(Socket userSocket, userThread[] threads) {
        this.userSocket = userSocket;
        this.threads = threads;
        maxUsersCount = threads.length;
    }
    
    public void run() {
    /*
     * Create input and output streams for this client, and start conversation.
     */
        try {

            //initiate
            System.out.println("new client started!");
            this.input_stream = 
                        new BufferedReader(
                            new InputStreamReader(this.userSocket.getInputStream())); //open inputstream
            this.output_stream = new PrintStream(this.userSocket.getOutputStream());
            this.output_stream.println("Enter your name.");
            
            
            //keep reading and sending data
            while (true) {
                String recieved = input_stream.readLine();

                //check if user has given his/her name
                if (this.userName == null) {
                    if (recieved.indexOf("@") >= 0 || set.contains(recieved)) {
                        this.output_stream.println("The name should not contain '@' character.");
                    }else {
                        this.userName = recieved;
                        set.add(this.userName);

                        //send welcome message to every user
                        synchronized(this) {
                            for (int i = 0; i < threads.length; i++) {
                                if (threads[i] != null) {
                                    PrintStream tempOut = new PrintStream(threads[i].userSocket.getOutputStream());
                                    tempOut.println( "Welcome " + "<" + this.userName + ">");
                                        // tempOutput1.close();
                                }
                            }
                        }
                    }
                } else {

                    //if the message starts with #friendme @
                    if (recieved.indexOf("#friendme @") == 0) { 
                        String tempStr = recieved.replace("#friendme @", "");
                        String[] array = tempStr.split(" ");
                        int count = 0; //used for checking if the user you want to make friend with exists

                        synchronized(this) {
                            for (int i = 0; i < maxUsersCount; i++) {
                            // System.out.println("");
                                if (threads[i] != null && threads[i].userName.equals(array[0].replace("@", ""))) {

                                    threads[i].output_stream.println("<"+ this.userName + ">"
                                                        + " Would you like to be friends?"); //friend request
                                    threads[i].friendrequests.add(this.userName); //add friend request

                                    break;
                                }
                                count++; 
                            }
                        }

                        if (count == maxUsersCount){
                            output_stream.println("Error: The user you want to be friends with doesn't exist!");
                        } 

                    //unicast                 
                    } else if (recieved.indexOf("@") == 0) { 
                        String[] arr = recieved.split(" ");
                        int num = 0;
                        String toUser = arr[0].replace("@", ""); //the user's name you send message to


                        synchronized(this) {
                            //check the existence of user
                            for (int i = 0; i < maxUsersCount; i++) {
                                if (threads[i] != null && threads[i].userName.equals(toUser)) {
                                    break;
                                }
                                num++;
                            }

                            //the username you entered doesn't exist
                            if (num == maxUsersCount) {
                                this.output_stream.println("Error: This user does not exist!");

                            //the username you entered exist
                            } else {
                                //if you two are not friends
                                if (!this.friends.contains(toUser)) {
                                    output_stream.println("You are not friends with " + toUser);
                                    
                                    //if you two are not friends, and he is on your friendrequests list
                                    if (this.friendrequests.contains(toUser)) {
                                        if (arr[1].equals("#friends")) {
                                            System.out.println("num: " + num);
                                            threads[num].friends.add(this.userName);
                                            this.friends.add(threads[num].userName);
                                            this.output_stream.println(this.userName + " and " 
                                                                    + threads[num].userName 
                                                                    + " are now friends!");
                                            threads[num].output_stream.println(this.userName + " and " 
                                                                            + threads[num].userName 
                                                                            + " are now friends!");
                                            this.friendrequests.remove(threads[num].userName);
                                        }else {
                                            this.output_stream.println("Wrong format to confirm the friend request");
                                        }
                                    }
                                //you two are friends
                                } else {

                                    //if it's a unfriend message 
                                    if (recieved.indexOf("#unfriend") > 0) {
                                        if (arr[1].equals("#unfriend")) {
                                            threads[num].friends.remove(this.userName);
                                            this.friends.remove(threads[num].userName);
                                            this.output_stream.println(this.userName + " and " 
                                                                    + threads[num].userName 
                                                                    + " are not friends anymore!");
                                            threads[num].output_stream.println(this.userName + " and " 
                                                                            + threads[num].userName 
                                                                            + " are not friends anymore!");
                                        } else {
                                            this.output_stream.println("Please follow right unfriend format: "
                                                                        + "@username #unfriend");
                                        }
                                    
                                    //normal unicast
                                    } else {
                                        for (int i = 0; i < maxUsersCount; i++) {
                                            if (threads[i] != null && 
                                                                threads[i].userName.equals(toUser)) {
                                                threads[i].output_stream.println("New message from " 
                                                                             + this.userName + ": "+ recieved);
                                                this.output_stream.println("Your message has been sent to " 
                                                                                        + threads[i].userName);
                                                break;
                                            }
                                        }
                                    }    
                                }
                            }
                        }

                    //broadcast
                    } else {  
                        if (recieved.indexOf("LogOut") >= 0) {

                            //broadcast message to every user that one user is leaving
                            synchronized(this) {
                                for (int i = 0; i < maxUsersCount; i++) {
                                    if (threads[i] != null) {
                                        if (threads[i].userSocket == this.userSocket) {
                                            this.output_stream.println("<" + this.userName + ">" + "is leaving.");
                                            threads[i] = null;
                                            this.userSocket.close();
                                        }else{
                                            PrintStream broadOut1 = new PrintStream(threads[i].userSocket.getOutputStream());
                                            broadOut1.println( "<" + this.userName + ">" + "is leaving.");
                                            // output2.close();
                                        }
                                    }
                                }
                            }

                            //user logout, flush the message and then close all the io and userSocket
                            this.output_stream.println("### Bye" + "<" +this.userName + ">" + "###" 
                                                        + "Please close the connection on your end.");
                            this.output_stream.flush();
                            this.input_stream.close();
                            this.output_stream.close();
                            this.userSocket.close();

                        } else { // broadcast normal message to each user
                            synchronized(this) {
                                for (int i = 0; i < maxUsersCount; i++) {
                                    if (threads[i] != null) {
                                        this.output_stream = new PrintStream(threads[i].userSocket.getOutputStream());
                                        this.output_stream.println( "<" + this.userName + ">" + recieved);
                                    }
                                }
                            }
                        }
                    }     
                }
            }     
        }catch (Exception ex) {
            System.out.println(ex);
        }
    }
}



