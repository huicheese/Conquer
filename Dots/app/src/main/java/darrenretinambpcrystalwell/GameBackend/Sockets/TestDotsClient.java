package darrenretinambpcrystalwell.GameBackend.Sockets;

import darrenretinambpcrystalwell.GameBackend.AwesomeSockets.AwesomeClientSocket;
import darrenretinambpcrystalwell.GameBackend.Dots.DotsBoard;
import darrenretinambpcrystalwell.GameBackend.Dots.DotsGame;
import darrenretinambpcrystalwell.GameBackend.Model.DotsMessage;
import darrenretinambpcrystalwell.GameBackend.Model.DotsMessageBoard;
import darrenretinambpcrystalwell.GameBackend.Model.DotsMessageResponse;

import java.io.IOException;

/**
 * Created by JiaHao on 25/2/15.
 */
public class TestDotsClient {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        final int PORT = 4321;
        final String SERVER_ADDRESS = "localhost";

        AwesomeClientSocket clientSocket = new AwesomeClientSocket(SERVER_ADDRESS, PORT);

        // read from server
        for (int i = 0; i < 2; i++) {

            DotsMessage message = DotsSocketHelper.readMessageFromServer(clientSocket);

            if (message instanceof DotsMessageBoard) {
                DotsBoard receivedBoard = ((DotsMessageBoard) message).getDotsBoard();
                receivedBoard.printWithIndex();

            } else if (message instanceof DotsMessageResponse) {
                boolean response = ((DotsMessageResponse) message).getResponse();
                System.out.println("Received Response: ");
                System.out.println(response);

            } else {

                System.err.println("Unknown message type");
            }
        }

        // send to server
        DotsGame dotsGame = new DotsGame();
        DotsBoard board = dotsGame.getDotsBoard();

        DotsMessageBoard messageBoard = new DotsMessageBoard(board);
        DotsSocketHelper.sendMessageToServer(clientSocket, messageBoard);

        DotsMessageResponse messageResponse = new DotsMessageResponse(true);
        DotsSocketHelper.sendMessageToServer(clientSocket, messageResponse);


        Thread.sleep(10000);
//
//
////            System.out.println(Arrays.deepToString(receivedBoard));
//
//        DotsBoard.printBoardWithIndex(receivedBoard);
//
//        Point testPoint = new Point(1,2, 1);
//        DotsSocketHelper.sendMoveToServer(clientSocket, testPoint);
//
//
//        clientSocket.closeClient();
//
//


    }


}
