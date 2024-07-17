package client;

import remote.IUserList;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

// manager (first craete): java create <serverIP> <server PORT> username
// other users:   java join <serverIP> <server PORT> username
public class Client {

    private static String username;
    private static String whiteBoardID;
    private static boolean isManager = false;
    private static IUserList list;
    private static Thread checkApprovalThread = null;

    public static void main(String[] args) {

        // checkValidCommands(args);

        // everything is valid now
        // check role

        // todo: wait for manager approval
        /*if (!isManager)
            System.out.println(checkApprovalThread.isInterrupted());
            System.out.println(checkApprovalThread == null); */

        checkValidCommands(args);
        new WhiteBoard(username, isManager, whiteBoardID, list);

    }

    private static void checkValidCommands(String[] args){

        System.out.println("start check valid commands");

        if (args.length != 3){
            System.out.println("The format of command should be: java <create/join> <whiteboardName> <username>");
            System.exit(0);
        }

        String action = args[0];

        if (action.equals("create")){ // manager
            isManager = true;
        } else if (action.equals("join")){ // normal user
            isManager = false;
        } else {
            System.out.println("Unknown action. Action needs to be either create or join");
            System.exit(0);
        }


        whiteBoardID = args[1];
        checkNameAndBoard(args[2], whiteBoardID);

        System.out.println("end check valid commands");

    }

    private static void checkNameAndBoard(String name, String boardID){
        // username should not exist 25 characters long and not empty
        if (name.length() > 25 || name.length() == 0){
            System.out.println("Username should be non-empty and less than 25 characters long");
            System.exit(0);
        }

        try {
            Registry registry = LocateRegistry.getRegistry();
            list = (IUserList) registry.lookup("userlist");
            System.out.println("successfully look up userlist");

            // if create the board, boardID should not exist
            if (isManager){
                if (list.containsBoard(boardID)){
                    System.out.println("The board is already in use. Try to join or create a new one with different whiteboardID");
                    System.exit(0);
                }
                // the board hasn't existed yet, create a new one and add the manager name
                list.addBoard(boardID);
                list.addUser(name, boardID, true);

            }

            // if join the board, boardID should already exist
            else {
                if (!list.containsBoard(boardID)){
                    System.out.println("The board hasn't existed yet. Try to join another existing board or create a new one");
                    System.exit(0);
                }

                // check if the username is duplicate
                if (list.getUserList(boardID).contains(name)){
                    System.out.println("Username has existed, please change to another");
                    System.exit(0);
                }



                // checkManagerApproval(name);
                System.out.println("manager approved");
                // add the username to the list
                list.addUser(name, boardID, false);
            }

            username = name;


        } catch (RemoteException re) {
            System.out.println("Exception when look up userlist");
            System.exit(0);
        } catch (NotBoundException nbe) {
            System.out.println("Remote Object not bound");
            System.exit(0);
        }

    }

    private static void checkManagerApproval(String name){

        try {
            list.wanna_join(name, whiteBoardID);
        } catch (RemoteException e) {
            System.out.println("Error: Cannot join the whiteboard");
            System.exit(0);
        }

        JOptionPane.showMessageDialog(null, "wait for manager approval");

        checkApprovalThread = new Thread(() -> {

            int decision = -1;

            for(int i = 0; i < 10; i++){
                try {
                    decision = list.getJoinDecision(name, whiteBoardID);
                    System.out.println(decision);
                    if (decision == 1) { // manager approves
                        System.out.println("approved");
                        return;
                    }
                    if (decision == 2) { // manager rejects
                        System.out.println("rejected");
                        JOptionPane.showMessageDialog(null, "Join is rejected");
                        System.exit(0);
                    }
                } catch (RemoteException e) {
                    System.out.println("Error when getting manager's decision");
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                }
            }

            JOptionPane.showMessageDialog(null, "Manager approval timeout, please try again");
            System.exit(0);

        });

        checkApprovalThread.start();

    }


}
