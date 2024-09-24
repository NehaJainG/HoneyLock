package com.nj.HoneyLock.utils;

import java.util.Random;

public class HoneywordGenerator {

  private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String DIGITS = "0123456789";
  private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";
  private final int numOfHoney;
  private final int lThreshold;

  public HoneywordGenerator(int numOfHoneywords, int levenshteinThreshold){
    numOfHoney = numOfHoneywords;
    lThreshold = levenshteinThreshold;
  }

  // Generate random honeywords using model-free randomized honeyword generation without edit distancce calculation
  public String[] generateNHoneywords(String password) {
    String[] honeywords = new String[this.numOfHoney];
    Random random = new Random();

    for (int i = 0; i < this.numOfHoney; i++) {
      honeywords[i] = generateHoneyword(password, random);
    }
    return honeywords;
  }

  // Modify the password randomly using substitutions, insertions, transpositions, etc.
  private String generateHoneyword(String password, Random random) {
    StringBuilder modified = new StringBuilder(password);

    // Random character substitution
    int substitution = random.nextInt(password.length() / 3) + 1;
    for (int i = 0; i < substitution; i++) {
      int index = random.nextInt(password.length());
      char newChar = getRandomChar(password.charAt(index), random);
      modified.setCharAt(index, newChar);
    }

    // Random insertion
    int numInsertions = random.nextInt(2) + 1;
    for (int i = 0; i < numInsertions; i++) {
      int index = random.nextInt(modified.length());
      char newChar = getRandomChar(' ', random);
      modified.insert(index, newChar);
    }

    // Transposition (swap two adjacent characters)
    if (password.length() > 1) {
      int index = random.nextInt(password.length() - 1);
      char temp = modified.charAt(index);
      modified.setCharAt(index, modified.charAt(index + 1));
      modified.setCharAt(index + 1, temp);
    }

    return modified.toString();
  }

  // Get a random character of the same type as the given char
  private char getRandomChar(char currentChar, Random random) {
    if (Character.isLowerCase(currentChar)) {
      return LOWERCASE.charAt(random.nextInt(LOWERCASE.length()));
    } else if (Character.isUpperCase(currentChar)) {
      return UPPERCASE.charAt(random.nextInt(UPPERCASE.length()));
    } else if (Character.isDigit(currentChar)) {
      return DIGITS.charAt(random.nextInt(DIGITS.length()));
    } else {
      return SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length()));
    }
  }

  // Calculate the Levenshtein distance between two strings
  public int editDistance(String s1, String s2) {
    int[][] dp = new int[s1.length() + 1][s2.length() + 1];

    for (int i = 0; i <= s1.length(); i++) {
      for (int j = 0; j <= s2.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
          dp[i][j] = Math.min(Math.min(
              dp[i - 1][j] + 1,      // deletion
              dp[i][j - 1] + 1),     // insertion
            dp[i - 1][j - 1] + cost // substitution
          );
        }
      }
    }

    return dp[s1.length()][s2.length()];
  }

  // Generate and validate honeywords
  public String[] generateValidHoneywords(String password) {
    String[] honeywords = new String[this.numOfHoney];
    int count = 0;
    Random random = new Random();

    while (count < this.numOfHoney) {
      String honeyword = generateHoneyword(password, random);
      if (editDistance(password,honeyword) >= this.lThreshold) {
        honeywords[count++] = honeyword;
      }
    }
    return honeywords;
  }
  public static void main(String[] args) {
    int numHoneywords = 5;
    int levenshteinThreshold = 3;
    HoneywordGenerator generator = new HoneywordGenerator(numHoneywords, levenshteinThreshold);

    String password = "P@ssw0rd";


    String[] honeywords = generator.generateValidHoneywords(password);

    System.out.println("Generated Honeywords:");
    for (String honeyword : honeywords) {
      System.out.println(honeyword);
    }
  }
}
