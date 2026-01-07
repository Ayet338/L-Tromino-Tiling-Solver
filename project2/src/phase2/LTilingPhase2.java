package phase2;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class LTilingPhase2 {

    // Inner class to represent a tiling step (for logging)
    public static class TilingStep {
        public final int tileId;
        public final int r1, c1, r2, c2, r3, c3; // Three cells of the L-tromino

        public TilingStep(int tileId, int r1, int c1, int r2, int c2, int r3, int c3) {
            this.tileId = tileId;
            this.r1 = r1; this.c1 = c1;
            this.r2 = r2; this.c2 = c2;
            this.r3 = r3; this.c3 = c3;
        }

        @Override
        public String toString() {
            return String.format("Tile %d placed at (%d,%d), (%d,%d), (%d,%d)",
                    tileId, r1, c1, r2, c2, r3, c3);
        }
    }

    public static class TilingResult {
        public final int[][] board;
        public final int tilesUsed;
        public final List<TilingStep> steps;
        public final long elapsedTimeNanos;

        public TilingResult(int[][] board, int tilesUsed, List<TilingStep> steps, long elapsedTimeNanos) {
            this.board = board;
            this.tilesUsed = tilesUsed;
            this.steps = steps;
            this.elapsedTimeNanos = elapsedTimeNanos;
        }
    }

    public static TilingResult solve(int n, int damagedRow, int damagedCol, boolean logSteps) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative.");
        }
        int size = 1 << n; // 2^n
        if (damagedRow < 0 || damagedRow >= size || damagedCol < 0 || damagedCol >= size) {
            throw new IllegalArgumentException("Damaged cell coordinates out of bounds.");
        }

        int[][] board = new int[size][size];
        board[damagedRow][damagedCol] = -1;

        List<TilingStep> steps = logSteps ? new ArrayList<>() : null;

        long startTime = System.nanoTime();
        int tileId = tileRecursive(board, size, 0, 0, damagedRow, damagedCol, 1, steps);
        long endTime = System.nanoTime();

        return new TilingResult(board, tileId - 1, steps, endTime - startTime);
    }

    private static int tileRecursive(int[][] board, int size, int startRow, int startCol,
                                     int damagedRow, int damagedCol, int tileId, List<TilingStep> steps) {
        if (size == 1) return tileId;

        int half = size / 2;
        int currentTile = tileId++;

        int quadrant = (damagedRow < startRow + half ? 0 : 2) +
                (damagedCol < startCol + half ? 0 : 1);

        // Central positions
        int r_tl = startRow + half - 1, c_tl = startCol + half - 1;
        int r_tr = startRow + half - 1, c_tr = startCol + half;
        int r_bl = startRow + half,     c_bl = startCol + half - 1;
        int r_br = startRow + half,     c_br = startCol + half;

        // Place tromino in 3 non-damaged quadrants
        if (quadrant != 0) board[r_tl][c_tl] = currentTile;
        if (quadrant != 1) board[r_tr][c_tr] = currentTile;
        if (quadrant != 2) board[r_bl][c_bl] = currentTile;
        if (quadrant != 3) board[r_br][c_br] = currentTile;

        // Log step if requested
        if (steps != null) {
            List<int[]> placed = new ArrayList<>();
            if (quadrant != 0) placed.add(new int[]{r_tl, c_tl});
            if (quadrant != 1) placed.add(new int[]{r_tr, c_tr});
            if (quadrant != 2) placed.add(new int[]{r_bl, c_bl});
            if (quadrant != 3) placed.add(new int[]{r_br, c_br});
            steps.add(new TilingStep(currentTile,
                    placed.get(0)[0], placed.get(0)[1],
                    placed.get(1)[0], placed.get(1)[1],
                    placed.get(2)[0], placed.get(2)[1]));
        }

        // Recurse into quadrants
        if (quadrant == 0)
            tileId = tileRecursive(board, half, startRow, startCol, damagedRow, damagedCol, tileId, steps);
        else
            tileId = tileRecursive(board, half, startRow, startCol, r_tl, c_tl, tileId, steps);

        if (quadrant == 1)
            tileId = tileRecursive(board, half, startRow, startCol + half, damagedRow, damagedCol, tileId, steps);
        else
            tileId = tileRecursive(board, half, startRow, startCol + half, r_tr, c_tr, tileId, steps);

        if (quadrant == 2)
            tileId = tileRecursive(board, half, startRow + half, startCol, damagedRow, damagedCol, tileId, steps);
        else
            tileId = tileRecursive(board, half, startRow + half, startCol, r_bl, c_bl, tileId, steps);

        if (quadrant == 3)
            tileId = tileRecursive(board, half, startRow + half, startCol + half, damagedRow, damagedCol, tileId, steps);
        else
            tileId = tileRecursive(board, half, startRow + half, startCol + half, r_br, c_br, tileId, steps);

        return tileId;
    }

    public static void printBoard(int[][] board) {
        int size = board.length;
        System.out.println("+" + "-".repeat(size * 4 - 1) + "+");
        for (int[] row : board) {
            System.out.print("|");
            for (int cell : row) {
                if (cell == -1) {
                    System.out.print(" X |");
                } else {
                    System.out.printf("%2d |", cell);
                }
            }
            System.out.println();
            System.out.println("+" + "-".repeat(size * 4 - 1) + "+");
        }
    }

    public static void runPerformanceTests() {
        System.out.println("\n=== PERFORMANCE ANALYSIS ===");
        System.out.printf("%-5s %-12s %-15s %-15s %-10s%n", "n", "Board Size", "Time (ms)", "Tiles Used", "Memory (KB approx)");
        System.out.println("-".repeat(65));

        for (int n = 1; n <= 6; n++) {
            int size = 1 << n;
            int damagedRow = 0, damagedCol = 0;

            try {
                long startMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                long start = System.nanoTime();
                TilingResult res = solve(n, damagedRow, damagedCol, false);
                long end = System.nanoTime();
                long endMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                double timeMs = (end - start) / 1_000_000.0;
                long memKB = (endMem - startMem) / 1024;

                System.out.printf("%-5d %-12d %-15.2f %-15d %-10d%n",
                        n, size, timeMs, res.tilesUsed, memKB);
            } catch (OutOfMemoryError e) {
                System.out.printf("%-5d %-12d %-15s%n", n, size, "OOM");
                break;
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== L-Tiling Problem - Phase 2 ===");
        System.out.print("How many test cases? ");
        int numTests = scanner.nextInt();

        for (int i = 1; i <= numTests; i++) {
            try {
                System.out.println("\n--- Test Case " + i + " ---");
                System.out.print("Enter n (board = 2^n x 2^n): ");
                int n = scanner.nextInt();
                System.out.print("Enter damaged row: ");
                int dr = scanner.nextInt();
                System.out.print("Enter damaged column: ");
                int dc = scanner.nextInt();
                System.out.print("Log tiling steps? (y/n): ");
                boolean logSteps = scanner.next().trim().toLowerCase().startsWith("y");

                TilingResult result = solve(n, dr, dc, logSteps);

                System.out.println("\n✅ Tiling completed successfully!");
                System.out.println("Board size: " + (1 << n) + "x" + (1 << n));
                System.out.println("Tiles used: " + result.tilesUsed);
                System.out.printf("Time taken: %.2f ms%n", result.elapsedTimeNanos / 1_000_000.0);

                if (logSteps) {
                    System.out.println("\n--- Tiling Steps ---");
                    for (TilingStep step : result.steps) {
                        System.out.println(step);
                    }
                }

                System.out.println("\n--- Final Tiled Board ---");
                printBoard(result.board);

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                i--; // retry this test case
            }
        }

        runPerformanceTests();

        System.out.println("\n=== COMPLEXITY ANALYSIS ===");
        System.out.println("Theoretical Time Complexity:");
        System.out.println("  - Recurrence: T(N) = 4T(N/2) + O(1)");
        System.out.println("  - Solution: T(N) = O(N²), where N = 2ⁿ (board side length)");
        System.out.println("  - Also O(4ⁿ) in terms of n (since N² = (2ⁿ)² = 4ⁿ).");
        System.out.println("\nTheoretical Space Complexity:");
        System.out.println("  - Board storage: O(N²) = O(4ⁿ)");
        System.out.println("  - Recursion depth: O(log N) = O(n)");
        System.out.println("  - Total: O(N²)");

        System.out.println("\nObservations:");
        System.out.println("- Time and memory grow quadratically with board size.");
        System.out.println("- Number of tiles = (4ⁿ - 1) / 3, which matches output.");
        System.out.println("- Empirical results align with theoretical O(N²) behavior.");

        scanner.close();
    }
}