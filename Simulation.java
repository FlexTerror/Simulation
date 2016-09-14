package assignments.product.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.out;

/*
 * A Simulation
 *
 * See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 */
// Extends JPanel because we will will draw graphics
public class Simulation extends JPanel {

    public static void main(String[] args) {
        new Simulation().program();
    }

    // Enumeration (reference) type for the Actors
    enum Actor {
        BLUE, NONE, RED
    }

    // Enumeration (reference) type for the state of an Actor
    enum State {
        UNSATISFIED,
        NA,        // Not applicable (NA), used for NONEs
        SATISFIED
    }

    // Below are the only accepted instance variables
    final Random rand = new Random();
    // %-distribution of RED, BLUE and NONE
    // Result (terminating?) is very depending on % empty and threshold, experiment!
    final double[] distribution = {0.25, 0.25, 0.50};
    // % of surrounding neighbours that are like me
    final double threshold = 0.5; // 0.5 easier for testing;
    //  This is hard coded for testing purposes (later you should generate the world)
    Actor[][] world = {
            {Actor.RED, Actor.RED, Actor.NONE},
            {Actor.BLUE, Actor.RED, Actor.NONE},  // Middle BLUE is dissatisfied (threshold = 0.5)
            {Actor.RED, Actor.NONE, Actor.BLUE}    // Left RED are dissatisfied (threshold = 0.5)
    };
    boolean toggle = true;  // Used in updateWorld
    State[][] state = new State[world.length][world.length]; // Matrix for the state of all Actors

    void program() {
        // Testing with *** threshold = 0.5;****
        plot(world);


        // Initialize the world here


        initGraphics();
        initEvents();
    }

    // Method called by timer
    void updateWorld() {
        if (toggle) {// TODO Get all unsatisfied
            for (int i = 0; i < world.length; i++) {
                for (int j = 0; j < world.length; j++) {
                    if (world[i][j] == Actor.NONE){
                        state[i][j] = State.NA;
                    }
                    else if (IsUn(i, j)) {
                        state[i][j] = State.UNSATISFIED;
                    } else {
                        state[i][j] = State.SATISFIED;
                    }
                }
            }
        } else {// TODO Move all unsatisfied
            //Turn world and state into array
            int matrixWidth = world.length;
            Actor[] worldArr = new Actor[matrixWidth * matrixWidth];
            toArray(world, worldArr);
            matrixWidth = state.length;
            State[] stateArr = new State[matrixWidth * matrixWidth];
            toArray(state, stateArr);

            //Make an array of the right size, Arraylist?
            int emptyAmn = 0;
            for (int i = 0; i < worldArr.length; i++) {
                if (stateArr[i] == State.NA) {
                    emptyAmn++;
                }
            }
            //Put all empty in a list
            Integer[] emptyIndex = new Integer[emptyAmn];
            int j = 0;
            for (int i = 0; i < worldArr.length; i++) {
                if (stateArr[i] == State.NA) {
                    emptyIndex[j] = i;
                    j++;
                }
            }

            //Scramble empty
            shuffle(emptyIndex);
            out.println("Empty tiles: " + Arrays.toString(emptyIndex));

            //Move unsatisifed to empty tile
            int i = 0;
            for (int z = 0; z < stateArr.length; z++){
                if (stateArr[z] == State.UNSATISFIED){
                    worldArr[emptyIndex[i]] = worldArr[z];
                    worldArr[z] = Actor.NONE;
                    i++;
                    if (i >= emptyIndex.length){
                        break;
                    }
                }
            }

            world = toMatrix(worldArr);

            plot(world);
            plot(state);

        }
        toggle = !toggle;
    }

    // Generate Actors for the world, nElements should be a square
    Actor[] initData(double[] distribution, int nElements) {
        // TODO
        return null;
    }

    // ------- Write your method below this ---------------
    <T> T[] toArray(T[][] matrix, T[] array) {

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                array[j + (i * matrix.length)] = matrix[i][j];
            }
        }

        return array;
    }

    boolean IsUn(int i, int j) {
        int same = 0;
        int nsame = 8;
        if (i != 0 && j != 0) {//not bot left
            if (world[i - 1][j - 1] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }

        if (i != 0 && j != world.length - 1) {//not top left
            if (world[i - 1][j + 1] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }
        if (i != world.length - 1 && j != world.length - 1) {//not top right
            if (world[i + 1][j + 1] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }
        if (i != world.length - 1 && j != 0) {//not bottom right
            if (world[i + 1][j - 1] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }
        if (i != 0) {//not left
            if (world[i - 1][j] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }
        if (j != world.length - 1) {//not top
            if (world[i][j + 1] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }
        if (i != world.length - 1) {//not right
            if (world[i + 1][j] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }
        if (j != 0) {//not bottom
            if (world[i][j - 1] == world[i][j]) {
                same++;
                nsame--;
            }
        } else {
            nsame--;
        }

        if (threshold < same / nsame) {
            return true;
        } else {
            return false;
        }
    }
    
    // --------- NOTHING to do below this --------------------------
    // --- Utility methods ----------------------------
    Actor[][] toMatrix(Actor[] arr) {
        int size = (int) round(sqrt(arr.length));
        Actor[][] matrix = new Actor[size][size];
        for (int i = 0; i < arr.length; i++) {
            matrix[i / size][i % size] = arr[i];
        }
        return matrix;
    }

    // Random shuffling of any reference type array
    <T> void shuffle(T[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rand.nextInt(i);
            T k = arr[i];
            arr[i] = arr[j];
            arr[j] = k;
        }
    }

    // ------ For Testing -----------------
    <T> void plot(T[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            out.println(Arrays.toString(matrix[row]));
        }
    }

    // ------- Graphics and Events -------------

    final int width = 400;
    final int height = 400;
    final int delay = 200;

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = 10 * col + 50;
                int y = 10 * row + 50;

                if (world[row][col] == Actor.RED) {
                    g2.setColor(Color.RED);
                } else if (world[row][col] == Actor.BLUE) {
                    g2.setColor(Color.BLUE);
                } else {
                    g2.setColor(Color.WHITE);
                }
                g2.fillOval(x, y, 10, 10);
                /* If not satisfied put a mark on
                if (...) {
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, y, 4, 4);
                }*/
            }
        }
        Toolkit.getDefaultToolkit().sync();
    }

    void initGraphics() {
        setPreferredSize(new Dimension(width, height));
        JFrame window = new JFrame();
        window.setTitle("Simulation");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(this);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    void initEvents() {
        Timer timer = new Timer(delay, e -> {
            updateWorld();
            repaint();
        });
        timer.start();
    }
}
