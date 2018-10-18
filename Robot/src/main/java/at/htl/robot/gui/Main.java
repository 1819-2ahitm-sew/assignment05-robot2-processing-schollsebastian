package at.htl.robot.gui;

import at.htl.robot.model.Robot;
import processing.core.PApplet;

/*
 * Sebastian Scholl
 */

public class Main extends PApplet {

    //region Member-Variables
    private boolean fullScreen = true;
    private int windowWidth = 800;
    private int windowHeight = 600;

    private int frameRate = 60;

    private int gridWidth = 10;
    private int gridHeight = 10;
    private int[][] grid = new int[gridHeight][gridWidth];
    private int gridColor = color(255);

    private int backgroundColor = color(209);
    private int selectedFieldColor = color(255, 0, 0);
    private int textColor = color(0);

    private int gameOverBackgoundColor = color(0);
    private int gameOverTextColor = color(255);

    private int startScreenBackgroundColor = color(0);
    private int startScreenTextcolor = color(255);

    private int marginTopBottom = 70;
    private int marginLeftRight = 50;

    private int fieldWith;
    private int fieldHeight;
    private boolean fieldsAreSqares = true;

    private char keyRotateLeftPlayer1 = 'l';
    private char keyStepForwardPlayer1 = 'f';
    private char keyChangeModePlayer1 = 'm';
    private char keyRotateLeftPlayer2 = '4';
    private char keyStepForwardPlayer2 = '6';
    private char keyChangeModePlayer2 = '2';

    private int scorePlayer1;
    private int scorePlayer2;
    private int highscore = 0;
    private int time;

    private int boxX;
    private int boxY;
    private int boxColor = color(229, 0, 255);

    private Robot player1;
    private Robot player2;
    private int robotColor = color(0, 0, 255);

    private boolean gameOver = false;
    private boolean wait = false;
    private boolean startScreen = true;
    //endregion

    public static void main(String[] args) {
        PApplet.main("at.htl.robot.gui.Main", args);
    }

    public void settings() {

        if (fullScreen) {
            fullScreen();
        } else {
            size(windowWidth, windowHeight);
        }

    }

    public void setup() {

        noCursor();
        background(backgroundColor);
        frameRate(frameRate);

        if (width > height && fieldsAreSqares) {

            fieldWith = (height - marginTopBottom * 2) / gridHeight;
            fieldHeight = fieldWith;
            marginLeftRight = (width - fieldWith * gridWidth) / 2;

        } else if (width < height && fieldsAreSqares) {

            fieldWith = (width - marginLeftRight * 2) / gridWidth;
            fieldHeight = fieldWith;
            marginTopBottom = (height - fieldHeight * gridHeight) / 2;

        } else {

            fieldWith = (width - marginLeftRight * 2) / gridWidth;
            fieldHeight = (height - marginTopBottom * 2) / gridHeight;
        }

        declareMemberVariables();

    }

    public void draw() {

        if (startScreen) {

            startScreen();

        } else if (gameOver) {

            gameOver();

            if (wait) {

                delay(1000);
                wait = false;

            }

        } else {

            interpretPressedKey();

            declareGrid();

            markSelectedField(player1);
            markSelectedField(player2);

            drawGrid();

            drawBox();

            checkForCollision(player1, player2);

            drawRobot(player1);
            drawRobot(player2);

            drawText();

            if (frameCount % frameRate == 1) {
                time--;
            }

            if (time == -1) {

                gameOver = true;
                wait = true;

            }

        }

    }

    private void checkForCollision(Robot robot1, Robot robot2) {

        boolean isBoxCollision = false;

        if (robot1.getX() == boxX && robot1.getY() == boxY) {

            scorePlayer1++;
            isBoxCollision = true;

        } else if (robot2.getX() == boxX && robot2.getY() == boxY) {

            scorePlayer2++;
            isBoxCollision = true;

        }

        if (Math.max(scorePlayer1, scorePlayer2) > highscore) {
            highscore = Math.max(scorePlayer1, scorePlayer2);
        }

        while (((robot1.getX() == boxX && robot1.getY() == boxY) || (robot2.getX() == boxX && robot2.getY() == boxY)) && isBoxCollision) {

            boxX = (int) (Math.random() * gridWidth - 1);
            boxY = (int) (Math.random() * gridHeight - 1);

        }

        if (player1.getX() == player2.getX() && player1.getY() == player2.getY()) {

            gameOver = true;
            wait = true;

        }

    }

    private void startScreen() {

        String text = "Move a robot on a grid by pressing [" + (keyStepForwardPlayer1 + "").toUpperCase() + "] or [" + (keyStepForwardPlayer2 + "").toUpperCase() + "] to make a step in the selected direction and [" + (keyRotateLeftPlayer1 + "").toUpperCase() + "] or [" + (keyRotateLeftPlayer2 + "").toUpperCase() + "] to rotate left. The goal is to collect more boxes than your opponent in 60 seconds.";

        background(startScreenBackgroundColor);

        fill(startScreenTextcolor);

        textSize(Math.min(width, height) / 10);
        textAlign(CENTER, BOTTOM);
        text("ROBOT", width / 2, height / 3);

        textSize(Math.min(width, height) / 70);
        textAlign(RIGHT, TOP);
        text("Sebastian Scholl", width - 10, 10);

        textSize(Math.min(width, height) / 50);
        textAlign(CENTER, TOP);
        text(text, width / 3, height / 2, width / 3, height);

        textSize(Math.min(width, height) / 40);
        textAlign(CENTER, BOTTOM);
        text("Press any key to start", width / 2, height - height / 10);

        if (keyPressed || mousePressed) {

            startScreen = false;
            background(backgroundColor);

        }

    }

    private void gameOver() {

        background(gameOverBackgoundColor);

        fill(gameOverTextColor);

        textSize(Math.min(marginLeftRight, marginTopBottom) / 2);

        textAlign(CENTER, BOTTOM);
        if (scorePlayer1 > scorePlayer2) {
            text("Player 1 won!", width / 2, height / 2);
        } else if (scorePlayer1 < scorePlayer2) {
            text("Player 2 won!", width / 2, height / 2);
        } else {
            text("Nobody won!", width / 2, height / 2);
        }

        textAlign(CENTER, TOP);
        text("Press any key to PLAY AGAIN", width / 2, height / 2);

        textSize(Math.min(marginLeftRight, marginTopBottom) / 4);
        textAlign(LEFT, BOTTOM);
        text("Score: P1 [" + scorePlayer1 + "], P2 [" + scorePlayer2 + "]", marginLeftRight / 10, marginTopBottom / 2);

        textAlign(RIGHT, BOTTOM);
        text("Highscore: " + highscore, width - marginLeftRight / 10, marginTopBottom / 2);

        if ((keyPressed || mousePressed) && !wait) {

            gameOver = false;
            background(backgroundColor);
            declareMemberVariables();

        }

    }

    private void declareMemberVariables() {

        time = 60;
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        player1 = new Robot(0, 0, gridWidth, gridHeight);
        player2 = new Robot(gridWidth - 1, 0, gridWidth, gridHeight);

        do {

            boxX = (int) (Math.random() * gridWidth - 1);
            boxY = (int) (Math.random() * gridHeight - 1);

        } while ((player1.getX() == boxX && player1.getY() == boxY) || (player2.getX() == boxX && player2.getY() == boxY));

    }

    private void drawBox() {

        int boxWidth = fieldWith - Math.min(fieldWith, fieldHeight) / 5;
        int boxHeigth = fieldHeight - Math.min(fieldWith, fieldHeight) / 5;
        int x = marginLeftRight + boxX * fieldWith + Math.min(fieldWith, fieldHeight) / 10;
        int y = marginTopBottom + boxY * fieldHeight + Math.min(fieldWith, fieldHeight) / 10;

        fill(boxColor);
        rect(x, y, boxWidth, boxHeigth);

    }

    private void drawText() {

        noStroke();
        fill(backgroundColor);
        rect(0, 0, width, marginTopBottom);

        stroke(0);

        String textStepForward = "Step forward [" + (keyStepForwardPlayer1 + "").toUpperCase() + "] [" + (keyStepForwardPlayer2 + "").toUpperCase() + "]";
        String textRotateLeft = "Rotate Left [" + (keyRotateLeftPlayer1 + "").toUpperCase() + "] [" + (keyRotateLeftPlayer2 + "").toUpperCase() + "]";
        int y = marginTopBottom / 2;

        fill(textColor);
        textSize(Math.min(marginLeftRight, marginTopBottom) / 4);

        textAlign(LEFT, BOTTOM);
        text(textStepForward, marginLeftRight / 10, y);

        textAlign(LEFT, TOP);
        text(textRotateLeft, marginLeftRight / 10, y);

        textAlign(CENTER, BOTTOM);
        text("Exit [ESC]", width / 2, y);

        textAlign(CENTER, TOP);
        text(time + "s", width / 2, y);

        textAlign(RIGHT, BOTTOM);
        text("Score: P1 [" + scorePlayer1 + "], P2 [" + scorePlayer2 + "]", width - marginLeftRight / 10, y);

        textAlign(RIGHT, TOP);
        text("Highscore: " + highscore, width - marginLeftRight / 10, y);

    }

    private void markSelectedField(Robot robot) {

        int x = robot.getX();
        int y = robot.getY();

        switch (robot.getDirection()) {

            case NORTH:
                y -= 1;
                break;

            case EAST:
                x += 1;
                break;

            case SOUTH:
                y += 1;
                break;

            case WEST:
                x -= 1;
                break;

        }

        if (x < 0 && robot.getTeleportMode()) {
            x = gridWidth - 1;
        } else if (x >= gridWidth && robot.getTeleportMode()) {
            x = 0;
        } else if (x < 0 || x >= gridWidth) {
            x = -1;
        }

        if (y < 0 && robot.getTeleportMode()) {
            y = gridHeight - 1;
        } else if (y >= gridHeight && robot.getTeleportMode()) {
            y = 0;
        } else if (y < 0 || y >= gridHeight) {
            y = -1;
        }

        if (y != -1 && x != -1) {
            grid[x][y] = selectedFieldColor;
        }

    }

    private void declareGrid() {

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = gridColor;
            }
        }

    }

    private void drawGrid() {

        for (int i = 0; i < gridWidth; i++) {

            for (int j = 0; j < gridHeight; j++) {

                int x = marginLeftRight + fieldWith * i;
                int y = marginTopBottom + fieldHeight * j;

                fill(grid[i][j]);
                rect(x, y, fieldWith, fieldHeight);

            }

        }

    }

    private void drawRobot(Robot robot) {

        int radius = Math.min(fieldWith, fieldHeight) - Math.min(fieldWith, fieldHeight) / 10;
        int x = marginLeftRight + robot.getX() * fieldWith + fieldWith / 2;
        int y = marginTopBottom + robot.getY() * fieldHeight + fieldHeight / 2;

        fill(robotColor);
        ellipse(x, y, radius, radius);

        fill(textColor);
        textAlign(CENTER, CENTER);
        if (robot.getTeleportMode()) {
            text("T", x, y);
        } else {
            text("R", x, y);
        }

    }

    private void interpretPressedKey() {

        if (keyRotateLeftPlayer1 == (key + "").toLowerCase().charAt(0) && keyPressed) {
            player1.rotateLeft();
        } else if (keyStepForwardPlayer1 == (key + "").toLowerCase().charAt(0) && keyPressed) {
            player1.stepForward();
        } else if (keyChangeModePlayer1 == (key + "").toLowerCase().charAt(0) && keyPressed) {
            player1.setTeleportMode(!player1.getTeleportMode());
            player2.setTeleportMode(player1.getTeleportMode());
        }

        if (keyRotateLeftPlayer2 == (key + "").toLowerCase().charAt(0) && keyPressed) {
            player2.rotateLeft();
        } else if (keyStepForwardPlayer2 == (key + "").toLowerCase().charAt(0) && keyPressed) {
            player2.stepForward();
        } else if (keyChangeModePlayer2 == (key + "").toLowerCase().charAt(0) && keyPressed) {
            player2.setTeleportMode(!player2.getTeleportMode());
            player1.setTeleportMode(player2.getTeleportMode());
        }

        key = '§';

    }

}