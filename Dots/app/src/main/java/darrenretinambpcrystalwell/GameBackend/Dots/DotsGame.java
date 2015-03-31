package darrenretinambpcrystalwell.GameBackend.Dots;

import darrenretinambpcrystalwell.GameBackend.Constants.DotsConstants;
import Model.DotsInteraction;
import Model.DotsInteractionStates;
import Model.DotsLocks;

import java.util.ArrayList;

/**
 * The general dots game, that wraps the board, logic and locks together
 *
 * Stores information about the points that are held by each player
 *
 * Created by JiaHao on 25/2/15.
 */
public class DotsGame {


    private final DotsBoard dotsBoard;
    private final DotsLogic dotsLogic;
    private final DotsLocks dotsLocks;

    private ArrayList<Point>[] playerMoves;


    public DotsGame() {
        this.dotsBoard = new DotsBoard(DotsConstants.BOARD_SIZE);
        this.dotsLogic = new DotsLogic(this.dotsBoard);


        this.dotsLocks = new DotsLocks();


        this.playerMoves = new ArrayList[DotsConstants.NO_OF_PLAYERS];

        for (int i = 0; i < DotsConstants.NO_OF_PLAYERS; i++) {
            this.playerMoves[i] = new ArrayList<Point>();
        }

    }

    public boolean isGameRunning() {
        return this.dotsLocks.isGameRunning();
    }

    public synchronized DotsBoard getDotsBoard() {
        return dotsBoard;
    }

    /**
     * Returns the lock that is used for tracking the game state
     * @return
     */
    public DotsLocks getGameLocks() {
        return dotsLocks;
    }

    /**
     * The primary method that we will call on our game, to update the game based on
     * an input DotsInteraction
     *
     * In this method, we will also check for a change for the board, when the interaction is touchUp
     * and certain dots on the screen need to be cleared.
     *
     * If so, this method will notify the dotsLock, which will in turn awaken the main thread to update
     * the screen
     *
     * @param interaction touch interaction
     * @return true if valid, false if invalid
     */
    public synchronized boolean doMove(DotsInteraction interaction) {

        // TODO Check for conflicts between two player points

        // if conflict detected
        if (conflictIsDetected(interaction)) {
            // invalid move
            return false;

        }

        // gets details from the interaction
        int player = interaction.getPlayerId();
        Point point = interaction.getPoint();

        // if its a new touch down, recreate the array list
        if (interaction.getState() == DotsInteractionStates.TOUCH_DOWN) {

            this.playerMoves[player] = new ArrayList<Point>();
        }


        // we temporarily add the new point to the current list of points and perform a check for adjacency

        boolean pointAlreadyRecorded = false;
        for (Point storedPoints : this.playerMoves[player]) {

            if (storedPoints.compareWith(point)) {
                pointAlreadyRecorded = true;
            }
        }

        if (!pointAlreadyRecorded) {

            this.playerMoves[player].add(point);
            boolean moveResult = this.dotsLogic.checkMove(this.playerMoves[player]);


            // remove the point if it is invalid
            if (!moveResult) {

                this.playerMoves[player].remove(this.playerMoves[player].size()-1);
                return false;
            }

        }

        if (interaction.getState() == DotsInteractionStates.TOUCH_UP) {

            boolean needToUpdateBoard = this.dotsLogic.moveCompleted(this.playerMoves[player]);

            // if the board is changed, notify lock
            if (needToUpdateBoard) {

                synchronized (this.dotsLocks) {

                    this.dotsLocks.setBoardChanged(true);
                    this.dotsLocks.notifyAll();
                }

            }

        }

        return true;
    }

    /**
     * General method to check for conflicts in the same point selected
     * and when points are selected above points that can be cleared
     * @param dotsInteraction
     * @return
     */
    private boolean conflictIsDetected(DotsInteraction dotsInteraction) {

        boolean samePointConflict = this.samePointConflict(dotsInteraction);
        boolean cascadingConflict = this.cascadingConflict(dotsInteraction);

        return samePointConflict || cascadingConflict;

    }

    /**
     * Helper method to check for an interaction that selects points
     * which are above the points another player has selected
     * @param dotsInteraction interaction of a particular player
     * @return false if no conflict, true if there is a conflict
     */
    private boolean cascadingConflict(DotsInteraction dotsInteraction) {
        // TODO cascading conflict

        Point interactionPoint = dotsInteraction.getPoint();

        ArrayList<Point> otherPlayerPoints = this.getOtherPlayerPoints(dotsInteraction.getPlayerId());

        for (Point otherPlayerPoint : otherPlayerPoints) {

            // same column
            if (otherPlayerPoint.x == interactionPoint.x) {

                // interaction point is above the other player point
                if (otherPlayerPoint.y > interactionPoint.y) {
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * This method will check for any conflicts in touches between players.
     * I.e. if player0 is holding on to a point, player1 will not be allowed to select that point
     * @param dotsInteraction interaction of a particular player
     * @return false if no conflict, true if there is a conflict
     */
    private boolean samePointConflict(DotsInteraction dotsInteraction) {

        ArrayList<Point> otherPlayerPoints = this.getOtherPlayerPoints(dotsInteraction.getPlayerId());

        // null if no such player
        if (!(otherPlayerPoints == null)) {

            for (Point otherPlayerPoint : otherPlayerPoints) {

                if (otherPlayerPoint.compareWith(dotsInteraction.getPoint())) {
                    return true;
                }

            }

        }


        return false;
    }

    /**
     * Returns an arraylist of points held by the other player
     * @param player current player
     * @return
     */
    private ArrayList<Point> getOtherPlayerPoints(int player) {
        ArrayList<Point> otherPlayerPoints;

        if (player == 0) {

            otherPlayerPoints = this.playerMoves[1];
        } else if (player == 1) {
            otherPlayerPoints = this.playerMoves[0];
        } else {
            System.err.println("No such player");
            return null;
        }
        return otherPlayerPoints;

    }


}
