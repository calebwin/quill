package com.quill;

import java.util.HashMap;

public class Quill {

    // variable costs for operations
    private double additionCost;
    private double deletionCost;
    private double substitutionCost;
    private double transpositionCost;

    private double[][] substitutionCostRules;
    private HashMap<Character, Integer> characterIndices;

    public Quill() {
        // set default cost of 1 for all operations
        this.additionCost = 1;
        this.deletionCost = 1;
        this.substitutionCost = 1;
        this.transpositionCost = 1;

        this.characterIndices = new HashMap<Character, Integer>();

        this.substitutionCostRules  = new double[10][10];
    }

    // checks for bad transposition cost and throws exception
    // does not attempt to revert costs to a correct cost
    private void handleBadTranspositionCost() {
        if (2 * transpositionCost < additionCost + deletionCost) {
            throw new IllegalArgumentException("Transposition cost must be at least the average of the addition cost and deletion cost");
        }
    }

    private void handleBadCost(double newCost) {
        if (newCost <= 0) {
            throw new IllegalArgumentException("Cost must be positive");
        }
    }

    public void setAdditionCost(double newCost) {
        handleBadCost(newCost);
        this.additionCost = newCost;
    }

    public void setDeletionCost(double newCost) {
        handleBadCost(newCost);
        this.deletionCost = newCost;
    }

    public void setSubstitutionCost(double newCost) {
        handleBadCost(newCost);
        this.substitutionCost = newCost;
    }

    public void setTranspositionCost(double newCost) {
        handleBadCost(newCost);
        this.transpositionCost = newCost;
    }

    private double[][] resizeMatrix(double[][] matrix, int newLen) {
        double[][] newMatrix = new double[newLen][newLen];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }

        return newMatrix;
    }

    /*
     * Adds a rule for the substitution cost of a pair of characters
     *
     * @param the two strings characters, and their substitution cost
     */
    public void addSubstitutionCostRules(char c1, char c2, double substitutionCost) {
        handleBadCost(substitutionCost);

        // double dimensions of substitution cost rule matrix if necessary
        if (characterIndices.size() == substitutionCostRules.length) {
            substitutionCostRules = resizeMatrix(substitutionCostRules, substitutionCostRules.length * 2);
        }

        // add characters to index-character hash maps if necessary
        if (!characterIndices.containsKey(c1)) {
            characterIndices.put(c1, characterIndices.size() + 1);
        }
        if (!characterIndices.containsKey(c2)) {
            characterIndices.put(c2, characterIndices.size() + 1);
        }

        // add rule
        substitutionCostRules[characterIndices.get(c1)][characterIndices.get(c2)] = substitutionCost;
        substitutionCostRules[characterIndices.get(c2)][characterIndices.get(c1)] = substitutionCost;
    }

    /*
     * Computes a cost that relates to the edit distance between the two given strings
     *
     * @param the two strings to compute edit distance of
     * @param an OperationType that specifies what operations to count
     */
    public double computeCost(String s1, String s2, OperationType operationType) {
        handleBadTranspositionCost();
        if (s1 == null || s2 == null) {
            throw new IllegalArgumentException("Paarameters must not be null");
        }

        if (s1.equals(s2)) {
            return 0;
        }

        switch (operationType) {
            case SUBSTITUTION:
                return computeLevenshtein(s1, s2);
            case TRANSPOSITION:
                return computeDamerauLevenshtein(s1, s2);
            default:
                return computeLCS(s1, s2);
        }
    }

    /*
     * Computes LCS of the two given strings
     *
     * @param the two strings to compute edit distance of
     */
    private double computeLCS(String s1, String s2) {
        return computeLCS(s1.toCharArray(), s2.toCharArray(), s1.length(), s2.length());
    }

    // Computes LCS of s1[0..s1Limit-1] and s2[0..s2Limit-1]
    private double computeLCS(char[] s1, char[] s2, int s1Limit, int s2Limit) {
        double[][] LCS = new double[s1Limit + 1][s2Limit + 1];

        for (int i = 0; i < s1Limit + 1; i++) {
            for (int j = 0; j < s2Limit + 1; j++) {
                if (i == 0 || j == 0) { // base case
                    LCS[i][j] = 0;
                } else if (s1[i - 1] == s2[j - 1]) { // both characters are same
                    LCS[i][j] = LCS[i - 1][j - 1] + additionCost + deletionCost;
                } else {
                    LCS[i][j] = max(LCS[i - 1][j], LCS[i][j - 1]);
                }
            }
        }

        return LCS[s1Limit][s2Limit];
    }

    /*
     * Computes Levenshtein distance of the two given strings
     *
     * @param the two strings to compute edit distance of
     */
    private double computeLevenshtein(String s1, String s2) {
        double[][] LEVENSHTEIN = new double[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i < s1.length() + 1; i++) {
            for (int j = 0; j < s2.length() + 1; j++) {
                if (i == 0) {
                    LEVENSHTEIN[i][j] = j;
                } else if (j == 0) {
                    LEVENSHTEIN[i][j] = i;
                } else {
                    LEVENSHTEIN[i][j] = min(LEVENSHTEIN[i - 1][j - 1] + susbstitutionCost(s1.charAt(i - 1), s2.charAt(j - 1)),
                            LEVENSHTEIN[i - 1][j] + deletionCost,
                            LEVENSHTEIN[i][j - 1] + additionCost);
                }
            }
        }

        return LEVENSHTEIN[s1.length()][s2.length()];
    }

    /*
     * Computes optimal string alignment of the two given strings
     *
     * @param the two strings to compute edit distance of
     */
    private double computeOSA(String s1, String s2) {
        double[][] OSA = new double[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < s1.length() + 1; i++) {
            for (int j = 0; j < s2.length() + 1; j++) {
                if (j == 0) { // base case
                    OSA[i][j] = i;
                } else if (i == 0) { // base case
                    OSA[i][j] = j;
                } else {
                    double charPairSubstitutionCost = susbstitutionCost(s1.charAt(i - 1), s2.charAt(j - 1));
                    OSA[i][j] = min(OSA[i - 1][j] + deletionCost,
                                            OSA[i][j - 1] + additionCost,
                                            OSA[i - 1][j - 1] + charPairSubstitutionCost);
                    if (i > 1 && j > 1 && s1.charAt(i - 1) == s2.charAt(j - 2) && s1.charAt(i - 2) == s2.charAt(j - 1)) {
                        OSA[i][j] = min(OSA[i][j], OSA[i - 2][j - 2] + transpositionCost);
                    }
                }
            }
        }
        return OSA[s1.length()][s2.length()];
    }

    /*
     * Computes Damerau-Levenshtein distance of the two given strings including adjacent transpositions only
     *
     * @param the two strings to compute edit distance of
     */
    private double computeDamerauLevenshtein(String s1, String s2) {
        double[][] DAMERAU = new double[s1.length()][s2.length()];

        if (s1.charAt(0) != s2.charAt(0)) {
            DAMERAU[0][0] = min(substitutionCost, deletionCost + additionCost);
        }
        characterIndices.put(s1.charAt(0), 0);

        for (int i = 1; i < s1.length(); i++) {
            double deletionDistance = DAMERAU[i - 1][0] + deletionCost;
            double additionDistance = (i + 1) * deletionCost + additionCost;
            double substitutionDistance = i * deletionCost + (s1.charAt(i) == s2.charAt(0) ? 0 : substitutionCost);
            DAMERAU[i][0] = min(deletionDistance, additionDistance, substitutionDistance);
        }
        for (int j = 1; j < s2.length(); j++) {
            double deletionDistance = (j + 1) * additionCost + deletionCost;
            double additionDistance = DAMERAU[0][j - 1] + additionCost;
            double substitutionDistance = j * additionCost + (s1.charAt(0) == s2.charAt(j) ? 0 : substitutionCost);
            DAMERAU[0][j] = min(deletionDistance, additionDistance, substitutionDistance);
        }

        for (int i = 1; i < s1.length(); i++) {
            int maxSourceLetterMatchIndex = s1.charAt(i) == s2.charAt(0) ? 0 : -1;
            for (int j = 1; j < s2.length(); j++) {
                Integer candidateSwapIndex = characterIndices.get(s2.charAt(j));
                int jSwap = maxSourceLetterMatchIndex;
                double deletionDistance = DAMERAU[i - 1][j] + deletionCost;
                double additionDistance = DAMERAU[i][j - 1] + additionCost;
                double substitutionDistance = DAMERAU[i - 1][j - 1];
                if (s1.charAt(i) != s2.charAt(j)) {
                    substitutionDistance += substitutionCost;
                } else {
                    maxSourceLetterMatchIndex = j;
                }
                double transpositionDistance;
                if (candidateSwapIndex != null && jSwap != -1) {
                    int iSwap = candidateSwapIndex;
                    double preSwapCost;
                    if (iSwap == 0 && jSwap == 0) {
                        preSwapCost = 0;
                    } else {
                        preSwapCost = DAMERAU[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
                    }
                    transpositionDistance = preSwapCost + (i - iSwap - 1) * deletionCost + (j - jSwap - 1) * additionCost + transpositionCost;
                } else {
                    transpositionDistance = Integer.MAX_VALUE;
                }
                DAMERAU[i][j] = min(deletionDistance, additionDistance, substitutionDistance, transpositionDistance);
            }
            characterIndices.put(s1.charAt(i), characterIndices.size() + 1);
        }

        return DAMERAU[s1.length() - 1][s2.length() - 1];
    }

    private double max(double... n) {
        double max = n[0];
        for (double x : n) {
            max = Math.max(max, x);
        }

        return max;
    }

    private double min(double... n) {
        double min = n[0];
        for (double x : n) {
            min = Math.min(min, x);
        }

        return min;
    }

    private double susbstitutionCost(char c1, char c2) {
        if (c1 == c2) {
            return 0;
        }
        if (!characterIndices.containsKey(c1) || !characterIndices.containsKey(c2) || substitutionCostRules[characterIndices.get(c1)][characterIndices.get(c2)] == 0.0) {
            return substitutionCost;
        }
        return substitutionCostRules[characterIndices.get(c1)][characterIndices.get(c2)];
    }

}
