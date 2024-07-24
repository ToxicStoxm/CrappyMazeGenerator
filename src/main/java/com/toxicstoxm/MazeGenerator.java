package com.toxicstoxm;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class MazeGenerator {

    public static final int SectionsPerSecond = 60;
    public static final long TPF = 1000000000 / SectionsPerSecond;
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    public static boolean[][] current = new boolean[HEIGHT][WIDTH];
    public static long startOfFrame = System.nanoTime();

    public static void main(String[] args) {
        new MazeGenerator();
    }

    public MazeGenerator() {

        Window w = new Window(WIDTH, HEIGHT);

        //while (true) {
            clear();

            current[1][1] = true;

            for (int i = 0; i < current.length - 1; i++) {
                boolean[] row = current[i];
                for (int j = 0; j < row.length - 1; j++) {
                    if (i > 0 && j > 0) {
                        spawnPath(i, j, w);
                        w.pushPixels(current);
                    }
                }
            }
        //}
    }

    public void clear() {
        current = new boolean[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                current[i][j] = false;
            }
        }
    }

    public interface Printer {
        boolean print(int row, int column, callb c);
        Direction next();
    }


    public enum Direction implements Printer {
        UP(0) {
            @Override
            public boolean print(int row, int column, callb c) {

                int oneUp = row - 1;
                int twoUp = row - 2;

                if (oneUp <= 0 || twoUp <= 0) return false;
                if (current[oneUp][column] || current[twoUp][column]) return false;
                if (current[twoUp][column - 1] || current[twoUp][column + 1]) return false;
                if (current[oneUp][column - 1] || current[oneUp][column + 1]) return false;

                current[oneUp][column] = true;
                current[twoUp][column] = true;
                c.execute(twoUp, column);

                return true;
            }

            @Override
            public Direction next() {
                return DOWN;
            }
        },
        DOWN(1) {
            @Override
            public boolean print(int row, int column, callb c) {

                int oneDown = row + 1;
                int twoDown = row + 2;

                if (oneDown >= current.length - 1 || twoDown >= current.length - 1) return false;
                if (current[oneDown][column] || current[twoDown][column]) return false;
                if (current[twoDown][column - 1] || current[twoDown][column + 1]) return false;
                if (current[oneDown][column - 1] || current[oneDown][column + 1]) return false;

                current[oneDown][column] = true;
                current[twoDown][column] = true;
                c.execute(twoDown, column);

                return true;
            }

            @Override
            public Direction next() {
                return RIGHT;
            }
        },
        RIGHT(2) {
            @Override
            public boolean print(int row, int column, callb c) {

                int oneRight = column + 1;
                int twoRight = column + 2;

                if (oneRight >= current[0].length - 1|| twoRight >= current[0].length - 1) return false;
                if (current[row][oneRight] || current[row][twoRight]) return false;
                if (current[row - 1][twoRight] || current[row + 1][twoRight]) return false;
                if (current[row - 1][oneRight] || current[row + 1][oneRight]) return false;

                current[row][oneRight] = true;
                current[row][twoRight] = true;
                c.execute(row, twoRight);

                return true;
            }

            @Override
            public Direction next() {
                return LEFT;
            }

        },
        LEFT(3){
            @Override
            public boolean print(int row, int column, callb c) {

                int oneLeft = column - 1;
                int twoLeft = column - 2;

                if (oneLeft <= 0 || twoLeft <= 0) return false;
                if (current[row][oneLeft] || current[row][twoLeft]) return false;
                if (current[row - 1][twoLeft] || current[row + 1][twoLeft]) return false;
                if (current[row - 1][oneLeft] || current[row + 1][oneLeft]) return false;

                current[row][oneLeft] = true;
                current[row][twoLeft] = true;
                c.execute(row, twoLeft);
                return true;
            }

            @Override
            public Direction next() {
                return UP;
            }
        };

        int val;

        Direction(int i) {
        }

        public static Direction valueOf(int val) {
            for (Direction d : Direction.values()) {
                if (val == d.val) return d;
            }
            return null;
        }

        public static Direction random() {
            return switch (ThreadLocalRandom.current().nextInt(0, 3 + 1)) {
                case 0 -> UP;
                case 1 -> DOWN;
                case 2 -> RIGHT;
                case 3 -> LEFT;
                default -> null;
            };
        }
    }

    public void spawnPath(int row, int column, Window w) {
        startOfFrame = System.nanoTime();
        AtomicInteger _row = new AtomicInteger(row);
        AtomicInteger _column = new AtomicInteger(column);
        //System.out.println("spawnPath(" + row + ", " + column + ")");
        Direction d =  Direction.random();
        //System.out.println(d);
        if (d == null) return;
        if (!tryPaint(row, column, d, (__row, __column) -> {
            //System.out.println("callb()" + __row + ", " + __column + ")");
            _row.set(__row);
            _column.set(__column);
        })) {
            /*try {
                Thread.sleep(Duration.ofNanos(TPF - (System.nanoTime() - startOfFrame)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            return;
        }
        //w.pushPixels(current);

        spawnPath(_row.get(), _column.get(), w);
    }

    public boolean tryPaint(int row, int column, Direction dir, callb c) {
        //System.out.println("tryPaint(" + row + ", " + column + ")");
        if (!dir.print(row, column, c)) {
            Direction temp = dir;
            //System.out.println("tryPaint(fail)");
            for (int i = 0; i <= 2; i++) {
                //System.out.println("tryPaint(fail) LOOP:" + i);
                temp = temp.next();
                if (temp.print(row, column, c)) return true;
            }
            //System.out.println("tryPaint(fail) LOOP: FAIL");
        } else return true;
        return false;
    }

    interface callb {
        void execute(int row, int column);
    }
}
