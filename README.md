
 Technical Highlights
Language: Java.
Data Structures: Uses 2D arrays for board representation and ArrayList for step tracking.
Clean Code: Organized with internal classes like TilingResult and TilingStep for structured data handling.
Configuration: Includes .gitignore for standard IDEs (IntelliJ, Eclipse, VS Code) to keep the repository clean.


 
 L-Tromino Tiling Problem (Phase 2)This repository contains a Java implementation of the L-Tromino Tiling Problem, a classic application of the Divide and Conquer algorithmic paradigm. 
 Project OverviewThe L-Tiling problem involves a board of size $2^n \times 2^n$ with exactly one "damaged" or "missing" cell. The goal is to cover every other square on the board using L-shaped trominoes (tiles consisting of 3 squares) without any overlaps or going out of bounds.
 This project focuses on a recursive solution that not only solves the tiling but also provides performance metrics, step-by-step logging, and complexity analysis.



FeaturesRecursive Solver: Uses an efficient Divide and Conquer approach to tile the board.Interactive CLI: Allows users to input board size ($n$), the coordinates of the damaged cell, and choose whether to log individual tiling steps.
Performance Benchmarking:
Includes a built-in test suite to measure execution time and approximate memory usage for various values of $n$.Visual Output:
Prints an ASCII representation of the board showing the placement of each numbered tile.



 File Structure
LTilingPhase2.java: The main logic, including the recursive algorithm, result logging, and performance testing.

Main.java: A simple entry point for basic environment testing.

.gitignore: Configured to exclude IDE-specific files (IntelliJ, Eclipse, VS Code).

project2.iml: IntelliJ IDEA module configuration.
