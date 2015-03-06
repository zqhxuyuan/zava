package com.interview.books.ccinterview;

import com.interview.utils.ctci.AssortedMethods;
import com.interview.utils.ctci.Trie;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午4:41
 */
class Puzzle {

    // Puzzle data.
    public int cols;
    public int rows;
    public char[][] puzzle;

    public Puzzle(int rows) {
        this.rows = rows;
    }

    /* Construct a rectangular array of letters of the specified rows
     * and cols, and backed by the specified puzzle of letters. (It is
     * assumed that the rows and cols specified as arguments are
     * consistent with the array argument's dimensions.)
     */
    public Puzzle(int rows, int cols, char[][] letters) {
        this.cols = letters.length;
        this.rows = letters[0].length;
        puzzle = letters;
    }

    /* Return the letter present at the specified location in the array.
     */
    public char getLetter(int row, int col) {
        return puzzle[row][col];
    }

    public String getColumn(int i) {
        char[] column = new char[cols];
        for (int j = 0; j < cols; j++) {
            column[j] = getLetter(j, i);
        }
        return new String(column);
    }

    public boolean isComplete(int l, int h, WordGroup groupList) {
        // Check if we have formed a complete rectangle.
        if (cols == h) {
            // Check if each column is a word in the dictionary.
            for (int i = 0; i < l; i++) {
                String col = getColumn(i);
                if (!groupList.containsWord(col)) {
                    return false; // Invalid rectangle.
                }
            }
            return true; // Valid Puzzle!
        }
        return false;
    }

    public boolean isPartialOK(int l, Trie trie) {
        if (cols == 0) {
            return true;
        }
        for (int i = 0; i < l; i++) {
            String col = getColumn(i);
            if (!trie.contains(col)) {
                return false; // Invalid rectangle.
            }
        }
        return true;
    }

    /* If the rows of the argument s is consistent with that of this
     * Puzzle object, then return a Puzzle whose puzzle is constructed by
     * appending s to the underlying puzzle. Otherwise, return null. The
     * underlying puzzle of this Puzzle object is /not/ modified.
     */
    public Puzzle append(String s) {
        if (s.length() == rows) {
            char temp[][] = new char[cols + 1][rows];
            for (int i = 0; i < cols; i++) {
                for (int j = 0; j < rows; j++) {
                    temp[i][j] = puzzle[i][j];
                }
            }
            s.getChars(0, rows, temp[cols], 0);
            return new Puzzle(rows, cols + 1, temp);
        }
        return null;
    }

    /* Print the rectangle out, row by row. */
    public void print() {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                System.out.print(puzzle[i][j]);
            }
            System.out.println(" ");
        }
    }
}

class WordGroup {
    private Hashtable<String, Boolean> lookup = new Hashtable<String, Boolean>();
    private ArrayList<String> group = new ArrayList<String>();

    public WordGroup() {

    }

    public boolean containsWord(String s) {
        return lookup.containsKey(s);
    }

    public void addWord (String s) {
        group.add(s);
        lookup.put(s, true);
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

		/* Group the words in the dictionary into lists of words of
		 * same rows.groupList[i] will contain a list of words, each
		 * of rows (i+1). */
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


public class CC38_WordPuzzle {
    private int maxWordLength;
    private WordGroup[] groupList ;
    private Trie trieList[];

    public CC38_WordPuzzle(String[] list) {
        groupList = WordGroup.createWordGroups(list);
        maxWordLength = groupList.length;
        // Initialize trieList to store trie of groupList[i] at ith position.
        trieList = new Trie[maxWordLength];
    }

    /* This function finds a rectangle of letters of the largest
     * possible area (rows x breadth) such that every row forms a
     * word (reading left to right) from the list and every column
     * forms a word (reading top to bottom) from the list.
     */
    public Puzzle maxRectangle() {
        // The dimensions of the largest possible rectangle.
        for(int wid = maxWordLength; wid > 0; wid--){
            for(int dep = maxWordLength; dep >= wid; dep--){
                Puzzle puzzle = makeRectangle(wid, dep);
                if (puzzle != null) {
                    return puzzle;
                }
            }
        }
        return null;
    }

    /* This function takes the rows and cols of a rectangle as
     * arguments. It tries to form a rectangle of the given rows and
     * cols using words of the specified rows as its rows, in which
     * words whose rows is the specified cols form the columns. It
     * returns the rectangle so formed, and null if such a rectangle
     * cannot be formed.
     */
    private Puzzle makeRectangle(int length, int height) {
        if (groupList[length - 1] == null || groupList[height - 1] == null) {
            return null;
        }
        if (trieList[height - 1] == null) {
            ArrayList<String> words = groupList[height - 1].getWords();
            trieList[height - 1] = new Trie(words);
        }
        return makePartialRectangle(length, height, new Puzzle(length));
    }


    /* This function recursively tries to form a puzzle with words
     * of rows l from the dictionary as rows and words of rows h
     * from the dictionary as columns. To do so, we start with an empty
     * puzzle and add in a word with rows l as the first row. We
     * then check the trie of words of rows h to see if each partial
     * column is a prefix of a word with rows h. If so we branch
     * recursively and check the next word till we've formed a complete
     * puzzle. When we have a complete puzzle check if every
     * column is a word in the dictionary.
     */
    private Puzzle makePartialRectangle(int l, int h, Puzzle puzzle) {

        // Check if we have formed a complete puzzle by seeing if each column
        // is in the dictionary
        if (puzzle.cols == h) {
            if (puzzle.isComplete(l, h, groupList[h - 1])) {
                return puzzle;
            } else {
                return null;
            }
        }

        // If the puzzle is not empty, validate that each column is a
        // substring of a word of rows h in the dictionary using the
        // trie of words of rows h.
        if (!puzzle.isPartialOK(l, trieList[h - 1])) {
            return null;
        }

        // For each word of rows l, try to make a new puzzle by adding
        // the word to the existing puzzle.
        for (int i = 0; i < groupList[l-1].length(); i++) {
            Puzzle orgPlus = puzzle.append(groupList[l-1].getWord(i));
            Puzzle rect = makePartialRectangle(l, h, orgPlus);
            if (rect != null) {
                return rect;
            }
        }
        return null;
    }

    // Test harness.
    public static void main(String[] args) {
        CC38_WordPuzzle dict = new CC38_WordPuzzle(AssortedMethods.getListOfWords());
        Puzzle rect = dict.maxRectangle();
        if (rect != null) {
            rect.print();
        } else {
            System.out.println ("No rectangle exists");
        }
    }


}
