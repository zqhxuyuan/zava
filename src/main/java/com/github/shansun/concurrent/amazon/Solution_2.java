package com.github.shansun.concurrent.amazon;
/* Enter your code here. Read input from STDIN. Print output to STDOUT */
import java.util.Scanner;
import java.util.StringTokenizer;

public class Solution_2
{
    private String calculateOperationSequence(int[] originalArray, int[] resultArray)
    {
        // your code is here
        return "";
    }

    public static void main(String[] args)
    {
        Solution_2 solution = new Solution_2();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine())
        {
            String strLine1 = scanner.nextLine();
            StringTokenizer stringTokenizer1 = new StringTokenizer(strLine1);

            //Initialize the original array
            int arrayLength = stringTokenizer1.countTokens();
            int[] originalArray = new int[arrayLength];
            for(int i = 0; i < arrayLength; i++)
            {
                originalArray[i] = Integer.parseInt(stringTokenizer1.nextToken());
            }

            //Initialize the result array
            String strLine2 = scanner.nextLine();
            StringTokenizer stringTokenizer2 = new StringTokenizer(strLine2);
            arrayLength = stringTokenizer2.countTokens();
            int[] resultArray = new int[arrayLength];
            for(int j = 0; j < arrayLength; j++)
            {
                resultArray[j] = Integer.parseInt(stringTokenizer2.nextToken());
            }

            String operationSequence = solution.calculateOperationSequence(originalArray, resultArray);
            System.out.println(operationSequence);
        }
    }
}