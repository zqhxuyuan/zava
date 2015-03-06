package com.interview.books.ccinterview;

import com.interview.design.questions.DZ22_Tries;
import com.interview.utils.ctci.AssortedMethods;

import java.util.ArrayList;

/**
 * Created_By: stefanie
 * Date: 15-2-8
 * Time: 下午6:03
 */
public class CC38_WordPuzzleII {

    static class Puzzle {

        // Puzzle data.
        public int rows;
        public int cols;
        public char[][] puzzle;

        public Puzzle(int rows) {
            this.rows = rows;
        }

        public Puzzle(char[][] letters) {
            this.rows = letters.length;
            this.cols = letters[0].length;
            puzzle = letters;
        }

        //check if every column is a valid word
        public boolean isComplete(int row, int col, WordGroup groupList) {
            // Check if we have formed a complete rectangle.
            if (cols == col) {
                // Check if each column is a word in the dictionary.
                for (int i = 0; i < row; i++) {
                    String rowWord = String.valueOf(puzzle[i]);
                    if (!groupList.containsWord(rowWord)) {
                        return false; // Invalid rectangle.
                    }
                }
                return true; // Valid Puzzle!
            }
            return false;
        }

        //check if every column is a valid prefix
        public boolean isPartialOK(DZ22_Tries trie) {
            if (cols == 0) {
                return true;
            }
            for (int i = 0; i < cols; i++) {
                String rowWord = String.valueOf(puzzle[i]);
                if (!trie.isPrefix(rowWord)) {
                    return false; // Invalid rectangle.
                }
            }
            return true;
        }

        //add a new string in a new row and generate a new Puzzle
        public Puzzle append(String s) {
            if (s.length() == rows) {
                char temp[][] = new char[rows][cols + 1];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        temp[i][j] = puzzle[i][j];
                    }
                }
                for(int i = 0; i < rows; i++) temp[i][cols] = s.charAt(i);
                return new Puzzle(temp);
            }
            return null;
        }

        /* Print the rectangle out, row by row. */
        public void print() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    System.out.print(puzzle[i][j]);
                }
                System.out.println(" ");
            }
        }
    }

    static class WordGroup {
        private DZ22_Tries trie = new DZ22_Tries();
        private ArrayList<String> group = new ArrayList<String>();

        public boolean containsWord(String s) {
            return trie.isWord(s);
        }

        public void addWord (String s) {
            group.add(s);
            trie.add(s);
        }

        public int length() {
            return group.size();
        }

        public String getWord(int i) {
            return group.get(i);
        }

        public ArrayList<String> getWords(){
            return group;
        }

        public static WordGroup[] createWordGroups(String[] list) {
            WordGroup[] groupList;
            int maxWordLength = 0;
            // Find out the rows of the longest word
            for (int i = 0; i < list.length; i++) {
                if (list[i].length() > maxWordLength) {
                    maxWordLength = list[i].length();
                }
            }

            groupList = new WordGroup[maxWordLength];
            for (int i = 0; i < list.length; i++) {
			/* We do wordLength - 1 instead of just wordLength since this is used as
			 * an index and no words are of rows 0 */
                int wordLength = list[i].length() - 1;
                if (groupList[wordLength] == null) {
                    groupList[wordLength] = new WordGroup();
                }
                groupList[wordLength].addWord(list[i]);
            }
            return groupList;
        }
    }

    static class PuzzleGenerator{
        private int maxWordLength;
        private WordGroup[] wordGroups;

        public PuzzleGenerator(String[] dict) {
            wordGroups = WordGroup.createWordGroups(dict);
            maxWordLength = wordGroups.length;
        }

        public Puzzle findMaxPuzzle() {
            // The dimensions of the largest possible rectangle.
            for(int rows = maxWordLength; rows > 0; rows--){
                for(int cols = maxWordLength; cols >= rows; cols--){
                    Puzzle puzzle = makePuzzle(rows, cols);
                    if (puzzle != null) {
                        return puzzle;
                    }
                }
            }
            return null;
        }


        private Puzzle makePuzzle(int rows, int cols) {
            if (wordGroups[rows - 1] == null || wordGroups[rows - 1] == null) {
                return null;
            }
            return makePartialPuzzle(rows, cols, new Puzzle(rows), 0);
        }

        private Puzzle makePartialPuzzle(int rows, int cols, Puzzle puzzle, int wordIdx) {

            // Check if we have formed a complete puzzle by seeing if each column
            // is in the dictionary
            if (puzzle.cols == cols) {
                if (puzzle.isComplete(rows, cols, wordGroups[rows - 1])) {
                    return puzzle;
                } else {
                    return null;
                }
            }

            // If the puzzle is not empty, validate that each column is a
            // substring of a word of rows h in the dictionary using the
            // trie of words of rows h.
            if (!puzzle.isPartialOK(wordGroups[rows - 1].trie)) {
                return null;
            }

            //backtracing to add words in puzzle
            for (int i = wordIdx; i < wordGroups[rows - 1].length(); i++) {
                String word = wordGroups[rows - 1].getWord(i);
                Puzzle nextPuzzle = makePartialPuzzle(rows, cols, puzzle.append(word), i+1);
                if (nextPuzzle != null) {
                    return nextPuzzle;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        PuzzleGenerator generator = new PuzzleGenerator(AssortedMethods.getListOfWords());
        Puzzle puzzle = generator.findMaxPuzzle();
        if (puzzle != null) {
            puzzle.print();
        } else {
            System.out.println ("No rectangle exists");
        }
    }
}
