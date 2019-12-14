package client;

import java.io.*; 
import java.net.*;
import java.util.Scanner; 
  
/**
 * @author Morgan Valentin
 * @Source code used to understand => https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
*/

/**
 * //TODO
 */
public class ClientCmd extends Client { 

    private Scanner scn;

    private CmdGridDisplay gridDisplay = new CmdGridDisplay();

    //Escape characters tho control the cmdline display. => ! only works on unix systems !
    public static final String RED_FG       = "\u001B[31m";
    public static final String GREEN_FG     = "\u001B[32m";
    public static final String BLUE_FG      = "\u001B[34m";
    public static final String PURPLE_FG    = "\u001B[35m";
    public static final String YELLOW_FG    = "\u001B[33m";
    public static final String RESET_COLOR  = "\u001B[0m";
    public static final String CLEAR_SCREEN = "\u001B[2J";
    public static final String HOME_CURSOR  = "\u001B[H";

    // !---------------------------------------------------------------------------------
    // !                                  Constructor
    // !---------------------------------------------------------------------------------
    
     /**
     * Constructor
     */
    public ClientCmd(){
        scn = new Scanner(System.in); 
    }
    
    
    //!---------------------------------------------------------------------------------
    //!                          Initialize Cleint <-> Server Connection 
    //!---------------------------------------------------------------------------------

    /**
     * Method that checks if name (input from user) is between 4 and 12 chracters in length
     * @param name {String} - //TODO
     * @param scanner {Scanner} - //TODO
     */
	private void checkNameClient(String name,Scanner scanner) {
        while (name.length()<4 || name.length() >13) {
            System.out.println("Invalid input. Please re-enter a name (min 4 - max 12 :");
            name = scanner.nextLine();
        }
    }
    
    /**
     * Method thet initialises a connection with the server
     * 
     */
    private void InitConnection(){

        System.out.print(CLEAR_SCREEN);
        System.out.print(HOME_CURSOR);

        // Retrieving client's name from user input :
        System.out.println("What's your name ?");
        name = scn.nextLine();
        checkNameClient(name,scn);

        // Retrieving port from user input :
        System.out.println(PURPLE_FG+"Port number ? (press enter for default port: 5555)"+RESET_COLOR);
        String userStr = scn.nextLine();
            if(userStr.equals("")){
                port = 5555;
            }
            else{
                port = Integer.valueOf(userStr);
            }
            
        System.out.println(BLUE_FG+name+"> "+PURPLE_FG+port+RESET_COLOR);
        

        //Retrieving ip adress from user input :
        System.out.println(RED_FG+"IP address ? (nothing + enter = localhost)"+RESET_COLOR);
        super.ip = scn.nextLine();
        if (super.ip.length()==0) {ip = "localhost";}
        System.out.println(BLUE_FG+name+"> "+RED_FG+ip+RESET_COLOR);

        try{
        //Establish the connection with the server with IP and Port from user input
        System.out.println(BLUE_FG+"Waiting to connect to server..."+RESET_COLOR);
        sock = new Socket(ip, port);
        System.out.println(RED_FG+"Connected !"+RESET_COLOR);
        System.out.println("__________________________");

        // In and out streams => Information received (inputStream) and sent (outputStream) :
        in = new DataInputStream(sock.getInputStream()); 
        out = new DataOutputStream(sock.getOutputStream()); 

        out.writeUTF(name);//Sends client name to server

        }
        catch(Exception e){}
    }
    
    //!---------------------------------------------------------------------------------
    //!                          Cleint <-> Server Communication 
    //!---------------------------------------------------------------------------------

    /**
     * Method that gets and processes the queries from the server.
     */
    private void listenToServer(){
        String[] queryFromServer;
        String command = "";
        String comment = "";
        String strToServer ="";

        while (true)  { 

            queryFromServer = getFromServer().split("-"); 
            command = queryFromServer[0];
            switch(command){
                case "U":   //Placing of units -> queryFromServer format : U-<unitName>-<unitSize>-<Comment>
                    String unitName = queryFromServer[1];
                    comment = queryFromServer[3];
                    System.out.print("\nWhere do you want to place the " + unitName + "? Enter top-left and bottom-right coordinates separated by a whitespace.\n");
                    if(!comment.equals("NC")){
                        System.out.print(comment);
                    }
                    sendToServer("U-"+unitName+"-"+queryFromServer[2]);
                    strToServer = scn.nextLine();
                    sendToServer(strToServer);
                    break;
                
                case "S": //Shooting -> queryFromServer format : S-<commandType>-<data>-<Comment>
                    String commandType = queryFromServer[1];
                    comment = queryFromServer[3];
                    switch(commandType){
                        case "T":   //shot Type
                            String availableShotTypes = queryFromServer[2];
                            System.out.print("What type of shot do you want to use?     Available: "+ availableShotTypes +"\n"+
                            "S : Singleshot; A : Airstrike; D : Radar discovery; B : Bigshot; R : Rocketstrike\n");
                            if(!comment.equals("NC")){
                                System.out.print(comment);
                            }
                            strToServer = scn.nextLine();
                            sendToServer(strToServer);
                            break;
                        
                        case "C":   //shot center Coord
                            System.out.print("Enter the coordinate of the center of the shot. Ex: H4 \n");
                            if(!comment.equals("NC")){
                                System.out.print(comment);
                            }
                            strToServer = scn.nextLine();
                            sendToServer(strToServer);
                            break;

                        case "D":   //direction
                            System.out.print("Enter the direction of the airstrike. H : Horizontal;  any other key : Vertical \n");
                            if(!comment.equals("NC")){
                                System.out.print(comment);
                            }
                            strToServer = scn.nextLine();
                            sendToServer(strToServer);
                            break;
                    }
                    break;

                case "I": //Comment with input from client
                    comment = queryFromServer[1];
                    System.out.print(comment);
                    strToServer = scn.nextLine();
                    sendToServer(strToServer);
                    break;

                case "C": //Comment without input from client
                    comment = queryFromServer[1];
                    System.out.print(comment);
                    break;


                case "displayGrid":
                    System.out.print("disp"); 
                    gridDisplay.displayGrid();
                    break;

                case "insertUnit":
                command = getFromServer();
                    gridDisplay.insertInGrid("Unit", command, false);
                    break;
                
                case "Hit": 
                    command = getFromServer();
                    gridDisplay.insertInGrid("Hit", command, true);
                    break;

                case "noHit":
                    command = getFromServer();
                    gridDisplay.insertInGrid("noHit", command, true);
                    break;
                
                case "Destroyed":
                    command = getFromServer();
                    gridDisplay.insertInGrid("Destroyed", command, true);
                    break;
                
                case "myDestroyed":
                    command = getFromServer();
                    gridDisplay.insertInGrid("Destroyed", command, false);
                    break;
                
                case "myHit": 
                    command = getFromServer();
                    gridDisplay.insertInGrid("Hit", command, false);
                    break;

                case "myNoHit":
                    command = getFromServer();
                    gridDisplay.insertInGrid("noHit", command, false);
                    break;

                case "Rem": //remove lines
                    command = getFromServer();
                    gridDisplay.removeLines(Integer.parseInt(command));
                    break;
                    
                case "WON":
                    System.out.print(GREEN_FG + "\n    YOU WON!    \n\n"+ RESET_COLOR);
                    System.exit(0);
                    break;
                
                case "LOST":
                    System.out.print(RED_FG + "\n    YOU LOST!    \n\n"+ RESET_COLOR);
                    System.exit(0);
                    break;

                case "CLOSE":
                    System.exit(0);
                    break;

                default:
                    System.out.print(command); //TODO
            }
        }

    }


    //!---------------------------------------------------------------------------------
    //!                                       MAIN
    //!---------------------------------------------------------------------------------
    public static void main(String[] args){
    	try {
    		ClientCmd client = new ClientCmd();
            client.InitConnection();
            client.listenToServer();
    	}
        catch(NumberFormatException a) {
        	System.out.println(RED_FG+"FATAL ERROR :"+PURPLE_FG+" IP and Port must be Integer"+RESET_COLOR);
        }
    	catch(NullPointerException a) {
            System.out.println(RED_FG+"Can't reach server...Check Ip/port and connectivity"+RESET_COLOR);
    	}
    } 

}