package pl.owsica.andrzej.frameprogressbar.utils;


import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Direction {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    private static Random rand = new Random();

    public static int random() {
        return rand.nextInt(4) + 1;
    }

    public static boolean isDirection(int num) {
        return (num >= 1 && num <= 4);
    }

    public static String toString(int dir) {
        switch (dir) {
            case LEFT:
                return "LEFT";
            case RIGHT:
                return "RIGHT";
            case TOP:
                return "TOP";
            case BOTTOM:
                return "BOTTOM";
            default:
                return null;
        }
    }

    public static DrawStop getDrawEnd(float length, float width, float height, int startPlace, boolean clockwise) {
        DrawStop drawStop = new DrawStop();

        float halfOfCanvasWidth = width / 2;
        float halfOfCanvasHeight = height / 2;
        int currentPlace = startPlace;
        float lengthLeft = length;
        boolean placeFound = false;
        boolean firstPlace = true;

        while (!placeFound) {
            if (currentPlace == Direction.TOP || currentPlace == Direction.BOTTOM) {
                if (firstPlace) {
                    if (lengthLeft < halfOfCanvasWidth) {
                        drawStop.place = currentPlace;

                        if (clockwise) {
                            if (currentPlace == Direction.TOP)
                                drawStop.location = halfOfCanvasWidth + lengthLeft;
                            else
                                drawStop.location = halfOfCanvasWidth - lengthLeft;
                        } else {
                            if (currentPlace == Direction.TOP)
                                drawStop.location = halfOfCanvasWidth - lengthLeft;
                            else
                                drawStop.location = halfOfCanvasWidth + lengthLeft;
                        }

                        placeFound = true;
                    } else {
                        currentPlace = nextDirection(currentPlace, clockwise);
                        lengthLeft -= halfOfCanvasWidth;
                    }

                    firstPlace = false;
                } else {
                    if (lengthLeft < width) {
                        drawStop.place = currentPlace;

                        if (clockwise) {
                            if (currentPlace == Direction.TOP)
                                drawStop.location = lengthLeft;
                            else
                                drawStop.location = width - lengthLeft;
                        } else {
                            if (currentPlace == Direction.TOP)
                                drawStop.location = width - lengthLeft;
                            else
                                drawStop.location = lengthLeft;
                        }

                        placeFound = true;
                    } else {
                        currentPlace = nextDirection(currentPlace, clockwise);
                        lengthLeft -= width;
                    }
                }

            } else {//LEFT, RIGHT
                if (firstPlace) {
                    if (lengthLeft < halfOfCanvasHeight) {
                        drawStop.place = currentPlace;

                        if (clockwise) {
                            if (currentPlace == Direction.RIGHT)
                                drawStop.location = halfOfCanvasHeight + lengthLeft;
                            else
                                drawStop.location = halfOfCanvasHeight - lengthLeft;
                        } else {
                            if (currentPlace == Direction.RIGHT)
                                drawStop.location = halfOfCanvasHeight - lengthLeft;
                            else
                                drawStop.location = halfOfCanvasHeight + lengthLeft;

                        }

                        placeFound = true;
                    } else {
                        currentPlace = nextDirection(currentPlace, clockwise);
                        lengthLeft -= halfOfCanvasHeight;
                    }

                    firstPlace = false;
                } else {
                    if (lengthLeft < height) {
                        drawStop.place = currentPlace;

                        if (clockwise) {
                            if (currentPlace == Direction.RIGHT)
                                drawStop.location = lengthLeft;
                            else
                                drawStop.location = height - lengthLeft;
                        } else {
                            if (currentPlace == Direction.RIGHT)
                                drawStop.location = height - lengthLeft;
                            else
                                drawStop.location = lengthLeft;
                        }

                        placeFound = true;
                    } else {
                        currentPlace = nextDirection(currentPlace, clockwise);
                        lengthLeft -= height;
                    }
                }
            }
        }

        return drawStop;
    }

    public static int nextDirection(int dir, boolean clockwise) {
        switch (dir) {
            case Direction.TOP:
                if (clockwise)
                    return Direction.RIGHT;
                else
                    return Direction.LEFT;
            case Direction.RIGHT:
                if (clockwise)
                    return Direction.BOTTOM;
                else
                    return Direction.TOP;
            case Direction.BOTTOM:
                if (clockwise)
                    return Direction.LEFT;
                else
                    return Direction.RIGHT;
            case Direction.LEFT:
                if (clockwise)
                    return Direction.TOP;
                else
                    return Direction.BOTTOM;
            default:
                return 0;
        }
    }

    public static class DrawStop {

        public int place;
        public float location;

        public DrawStop() {
        }

        @Override
        public String toString() {
            return Direction.toString(place) + "|" + location;
        }
    }

    public static class Vector2 {

        public float x, y = 0;

        public Vector2() {
        }

        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }


    //Initial position is start position of progress bar so center of any ledge
    public static Vector2 initialPosition(int direction, float frameThickness, Rect container, Canvas canvas) {
        switch (direction) {
            case LEFT:
                return new Vector2(container.left + frameThickness * 1.5f, canvas.getHeight() / 2);
            case RIGHT:
                return new Vector2(container.right - frameThickness * 1.5f, canvas.getHeight() / 2);
            case TOP:
                return new Vector2(canvas.getWidth() / 2, container.top + frameThickness / 2);
            case BOTTOM:
                return new Vector2(canvas.getWidth() / 2, container.bottom - frameThickness / 2);
            default:
                return new Vector2();
        }
    }

    //Start postion is one ledge of top, bottom, righ, left.
    public static Vector2 startPosition(int direction, float frameThickness, boolean clockwise, Rect container, Canvas canvas) {
        switch (direction) {
            case RIGHT:
                if (clockwise)
                    return new Vector2(container.right - frameThickness * 1.5f, frameThickness);
                else
                    return new Vector2(container.right - frameThickness * 1.5f, canvas.getHeight() - frameThickness);
            case LEFT:
                if (clockwise)
                    return new Vector2(container.left + frameThickness * 1.5f, canvas.getHeight() - frameThickness);
                else
                    return new Vector2(container.left + frameThickness * 1.5f, frameThickness);
            case TOP:
                if (clockwise)
                    return new Vector2(frameThickness, container.top + frameThickness / 2);
                else
                    return new Vector2(canvas.getWidth() - frameThickness, container.top + frameThickness / 2);
            case BOTTOM:
                if (clockwise)
                    return new Vector2(canvas.getWidth() - frameThickness, container.bottom - frameThickness / 2);
                else
                    return new Vector2(frameThickness, container.bottom - frameThickness / 2);
            default:
                return new Vector2();
        }
    }

    public static Vector2 finishPosition(DrawStop drawStop, float frameThickness, Rect framedChild) {
        switch (drawStop.place) {
            case RIGHT:
                return new Vector2(framedChild.right + frameThickness / 2, drawStop.location);
            case LEFT:
                return new Vector2(framedChild.left - frameThickness / 2, drawStop.location);
            case TOP:
                return new Vector2(drawStop.location, framedChild.top - frameThickness / 2);
            case BOTTOM:
                return new Vector2(drawStop.location, framedChild.bottom + frameThickness / 2);
            default:
                return new Vector2();
        }
    }

    public static Vector2 calculateEnd(int currentPlace, float frameThickness, boolean clockwise, Rect container, Canvas canvas) {
        switch (currentPlace) {
            case RIGHT:
                if (clockwise)
                    return new Vector2(container.right - frameThickness * 1.5f, canvas.getHeight());
                else
                    return new Vector2(container.right - frameThickness * 1.5f, 0);
            case LEFT:
                if (clockwise)
                    return new Vector2(container.left + frameThickness * 1.5f, 0);
                else
                    return new Vector2(container.left + frameThickness * 1.5f, canvas.getHeight());
            case TOP:
                if (clockwise)
                    return new Vector2(canvas.getWidth(), container.top + frameThickness / 2);
                else
                    return new Vector2(0, container.top + frameThickness / 2);
            case BOTTOM:
                if (clockwise)
                    return new Vector2(0, container.bottom - frameThickness / 2);
                else
                    return new Vector2(canvas.getWidth(), container.bottom - frameThickness / 2);
            default:
                return new Vector2();
        }
    }

    public static boolean isEndOnFirstLedge(DrawStop drawEnd, Canvas canvas, boolean clockwise, float progress) {
        switch (drawEnd.place) {
            case RIGHT:
                return ((clockwise && drawEnd.location >= canvas.getHeight() / 2) || (!clockwise && drawEnd.location <= canvas.getHeight() / 2) || progress == 0);
            case LEFT:
                return ((clockwise && drawEnd.location <= canvas.getHeight() / 2) || (!clockwise && drawEnd.location >= canvas.getHeight() / 2) || progress == 0);
            case TOP:
                return ((clockwise && drawEnd.location >= canvas.getWidth() / 2) || (!clockwise && drawEnd.location <= canvas.getWidth() / 2) || progress == 0);
            case BOTTOM:
                return ((clockwise && drawEnd.location <= canvas.getWidth() / 2) || (!clockwise && drawEnd.location >= canvas.getWidth() / 2) || progress == 0);
            default:
                return false;
        }
    }

    public static boolean isVertical(int direction){
        switch (direction){
            case BOTTOM:
            case TOP:
                return true;
            default:
                return false;
        }
    }

    public static boolean isHorizontal(int direction){
        switch (direction){
            case RIGHT:
            case LEFT:
                return true;
            default:
                return false;
        }
    }
}
