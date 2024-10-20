package com.nj.HoneyLock.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.json.JSONObject;

public class HoneywordGenerator {

  private final Map<String, Integer> commonWordsMap = new HashMap<>();
  private static final String COMMON_FILE_PATH = "src/main/java/com/nj/HoneyLock/Data/common.txt";
  private final int numOfHoney;

  public HoneywordGenerator(int numOfHoney) {
    this.numOfHoney = numOfHoney;
    loadCommonWords();
  }


  // Load common words from common.txt into a Map
  public void loadCommonWords() {
    StringBuilder jsonData = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(COMMON_FILE_PATH))) {
      String line;
      while ((line = reader.readLine()) != null) {
        jsonData.append(line);
      }

      // Parse JSON and store in map
      JSONObject jsonObject = new JSONObject(jsonData.toString());
      for (String key : jsonObject.keySet()) {
        commonWordsMap.put(key, jsonObject.getInt(key));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Method to generate random honeywords based on pattern
  public List<String> generateHoneywords(String pattern) {
    List<String> honeywords = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < numOfHoney; i++) {
      StringBuilder honeyword = new StringBuilder();

      // Loop through the pattern (e.g., "ADS" -> Alphabet, Digit, Special Character)
      for (char ch : pattern.toCharArray()) {
        if (ch == 'A') {
          honeyword.append(getRandomCommonWord(random)); // Get random alphabetic word
        } else if (ch == 'D') {
          honeyword.append(random.nextInt(9000) + 1000); // Random 4-digit number
        } else if (ch == 'S') {
          honeyword.append(getRandomSpecialChar()); // Random special character
        }
      }

      // Ensure password policy is met
      ensurePasswordPolicy(honeyword);
      honeywords.add(honeyword.toString());
    }

    return honeywords;
  }

  // Helper to get a random word from the common words
  private String getRandomCommonWord(Random random) {
    List<String> commonWords = new ArrayList<>(commonWordsMap.keySet());
    return commonWords.get(random.nextInt(commonWords.size()));
  }

  // Helper to get random special characters
  private char getRandomSpecialChar() {
    String specialChars = "!@#$%^&*()-_=+<>?";
    Random random = new Random();
    return specialChars.charAt(random.nextInt(specialChars.length()));
  }

  // Ensure at least one uppercase, one lowercase, one digit, and one special character
  private void ensurePasswordPolicy(StringBuilder honeyword) {
    boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
    for (char ch : honeyword.toString().toCharArray()) {
      if (Character.isUpperCase(ch)) hasUpper = true;
      if (Character.isLowerCase(ch)) hasLower = true;
      if (Character.isDigit(ch)) hasDigit = true;
      if ("!@#$%^&*()-_=+<>?".indexOf(ch) >= 0) hasSpecial = true;
    }

    // Ensure missing requirements are added if necessary
    Random random = new Random();
    if (!hasUpper) {
      int pos = random.nextInt(honeyword.length());
      honeyword.setCharAt(pos, Character.toUpperCase(honeyword.charAt(pos)));
    }
    if (!hasLower) {
      int pos = random.nextInt(honeyword.length());
      honeyword.setCharAt(pos, Character.toLowerCase(honeyword.charAt(pos)));
    }
    if (!hasDigit) {
      int pos = random.nextInt(honeyword.length());
      honeyword.insert(pos, random.nextInt(9) + 1);
    }
    if (!hasSpecial) {
      int pos = random.nextInt(honeyword.length());
      honeyword.insert(pos, getRandomSpecialChar());
    }
  }

  public static void main(String[] args) {
    HoneywordGenerator generator = new HoneywordGenerator(10);

    // Example of generating 5 honeywords based on pattern "ADS"
    List<String> honeywords = generator.generateHoneywords("ADS");

    // Print the honeywords
    for (String honeyword : honeywords) {
      System.out.println(honeyword);
    }
  }
}
