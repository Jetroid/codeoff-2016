import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class DamerauLevenshteinAlgorithm {

        /** Algorithm based on Wikipedia's Pseudocode for the Damerau-Levenshtein Algorithm. */
        public int distance(String stringA, String stringB){
            //Standardise the case
            char[] a = stringA.replaceAll("[^a-zA-Z ]", "").toLowerCase().toCharArray();
            char[] b = stringB.replaceAll("[^a-zA-Z ]", "").toLowerCase().toCharArray();

            if(a.length == 0)
                return b.length;
            if(b.length == 0)
                return a.length;

            //Generate a table to store the cost
            int[][] costs = new int[a.length+2][b.length+2];

            //We want to form this shape:
            //(a length 3, b length 2, maxdist = 3+2)
            // 5, 5, 5, 5, 5,
            // 5, 0, 1, 2, 3,
            // 5, 1, x, x, x
            // 5, 2, x, x, x
            //This is a similar table to Wagner-Fischer, with
            //the maxdist row and col added to stop erroneous
            //transpositions

            int maxDistance = a.length + b.length;
            costs[0][0] = maxDistance;
            for(int i = 0; i <= a.length; i++){
                //first row, maxdist
                costs[i+1][0] = maxDistance;
                //second row, integers going up
                costs[i+1][1] = i;
            }
            for(int j = 0; j <= b.length; j++){
                //first column, maxdist
                costs[0][j+1] = maxDistance;
                //second column, integers going up
                costs[1][j+1] = j;
            }

            //Allows lookup of row where character last appeared in stringA
            Map<Character,Integer> lastRow = new HashMap<>();
            char[] alphabet = "abcdefghijklmnopqrstuvwxyz ".toCharArray();
            //We initialise each letter to 0 to denote we haven't seen it yet.
            for(char c : alphabet){
                lastRow.put(c,0);
            }

            //Fill out the table of costs we made
            for(int row = 1; row <= a.length; row++){
                //For each character in a,
                char charA = a[row - 1];

                //Again, 0 denotes not matched yet.
                int lastMatchedColumn = 0;

                for(int col = 1; col <= b.length; col++){
                    //For each character in b,
                    char charB = b[col - 1];

                    //The last place in stringA where we found charB
                    int lastMatchedRow = lastRow.get(charB);

                    int cost = (charA == charB) ? 0 : 1;

                    //Distances of all the operations
                    int insertionCost = costs[row][col+1] + 1;
                    int deletionCost = costs[row+1][col] + 1;
                    int substitutionCost = costs[row][col] + cost;
                    //Calculate the cost of transpositions between charB and
                    //last character found in both strings. Any characters between are
                    //insertions or deletions.
                    int transpositionCost = costs[lastMatchedRow][lastMatchedColumn] +
                            (row-lastMatchedRow-1) + 1 + (col-lastMatchedColumn-1);

                    //Find the minimum operation
                    int min1 = Math.min(substitutionCost, insertionCost);
                    int min2 = Math.min(deletionCost,transpositionCost);
                    int min = Math.min(min1,min2);
                    //The overall minimum is the cost for this cell
                    costs[row+1][col+1] = min;

                    //If we had a match, update last matching column:
                    if (cost == 0)
                        lastMatchedColumn = col;
                }

                //Set the entry for this character's last appearance.
                lastRow.put(charA,row);
            }
            //The total distance
            return costs[a.length + 1][b.length + 1];
        }
    }