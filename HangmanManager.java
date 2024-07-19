/*  Student information for assignment:
 *
 *  On my honor, Dominic Paruolo, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Name: Dominic Paruolo
 *  email address: dominicparuolo78@gmail.com
 *  UTEID: dmp3588
 *  Section 5 digit ID: 52615
 *  Grader name: Nidhi
 *  Number of slip days used on this assignment: 2
 */

// add imports as necessary

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager {

    // instance variables / fields
	private String[] initialDictionary;
    private ArrayList<String> wordBank;
    private int wordLength;
    private int guessesLeft;
    private HangmanDifficulty difficulty;
    private ArrayList<Character> guesses;
    private String pattern;
    private boolean debug;
	
    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    
	public HangmanManager(Set<String> words, boolean debugOn) {
        if (words == null || words.size() == 0) {
        	throw new IllegalArgumentException("Violation of precondition: HangmanManager. "
        			+ "The parameter words must not be null and must have "
        			+ "a size greater than 0.");
        }
        //creates initial word list
        initialDictionary = words.toArray(new String[0]);
        debug = debugOn;
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        if (words == null || words.size() == 0) {
        	throw new IllegalArgumentException("Violation of precondition: HangmanManager. "
        			+ "The parameter words must not be null and must have "
        			+ "a size greater than 0.");
        }
        //creates initial word list
        initialDictionary = words.toArray(new String[0]);
        debug = false;
    }


    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     * with the given length
     */
    public int numWords(int length) {
    	int count = 0;
    	//finds number of words with given length
        for (int i = 0; i < initialDictionary.length; i++) {
        	if (initialDictionary[i].length() == length) {
        		count++;
        	}
        }
        return count;
    }


    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     * @param wordLen the length of the word to pick this time.
     * numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     * player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
    	if (wordLen <= 0 || numGuesses < 1) {
    		throw new IllegalArgumentException("Violation of precondtion: prepForRound. "
    				+ "The parameter wordLen must be greater than 0 and the parameter "
    				+ "numGuesses must be greater than or equal to 1.");
    	}
    	//initializes and resets instance variables
        wordLength = wordLen;
    	guessesLeft = numGuesses;
    	difficulty = diff;
    	guesses = new ArrayList<>();
        wordBank = new ArrayList<>(Arrays.asList(initialDictionary));
        //removes words with wrong size
        for (String s: initialDictionary) {
        	if (s.length() != wordLen) {
        		wordBank.remove(s);
        	}
        }
    	//sets initial pattern
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < wordLen; i++) {
    		sb.append("-");
    	}
    	pattern = sb.toString();
    }


    /**
     * The number of words still possible (live) based on the guesses so far.
     *  Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the
     * original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return wordBank.size();
    }


    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     * @return the number of wrong guesses the user has left
     * in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return guessesLeft;
    }


    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user
     * has guessed so far during this round.
     */
    public String getGuessesMade() {
    	//sorts list of guesses
    	Collections.sort(guesses);
        return guesses.toString();
    }


    /**
     * Check the status of a character.
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman,
     * false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
    	//finds if guess was already guessed
    	if (guesses.contains(guess)) {
    		return true;
    	}
        return false;
    }


    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character 
     * for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() {
        return pattern;
    }


    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
    	if (alreadyGuessed(guess)) {
    		throw new IllegalStateException("This guess has already been made.");
    	}
    	//adds new guess
    	guesses.add(guess);
    	//creates map of possible patterns and words with those patterns
    	Map<String, ArrayList<String>> patternMap = patternMapMaker(guess);
    	//keeps track of old pattern
    	String oldPattern = pattern;
    	//updates available words and current pattern with corresponding difficulty
    	final int EASY_BEHAVIOR = 2;
    	final int MED_BEHAVIOR = 4;
    	if ((difficulty.equals(HangmanDifficulty.EASY) && guesses.size() % EASY_BEHAVIOR == 0) ||
    		(difficulty.equals(HangmanDifficulty.MEDIUM) && guesses.size() % MED_BEHAVIOR == 0)) {
    	    wordBank = new ArrayList<>(secondHardestPattern(patternMap, guess));
    	} else {
    		wordBank = new ArrayList<>(hardestPattern(patternMap, guess));
    	}
    	//finds if guess was correct
    	if (oldPattern.equals(pattern)) {
    		guessesLeft--;
    	}
    	//initial debugging statement
    	if (debug) {
    		System.out.println("\nDebugging: Based on the guess, this is the resulting"
    				+ " patterns and\nword lists for each pattern:");
    	}
    	//creates new map of possible patterns and the number of words corresponding to each one
    	TreeMap<String, Integer> sizeMap = new TreeMap<>();
    	for (String pat: patternMap.keySet()) {
    		sizeMap.put(pat, patternMap.get(pat).size());
    		//prints debugging of patterns and word lists
    		if (debug) {
    			System.out.println("pattern: " + pat + ", word list: " + patternMap.get(pat));
    		}
    	}
        return sizeMap;
    }
    
    /**
     * Helper method for makeGuess that returns a map of all possible patterns for the available
     * words as the keys and the corresponding words to each pattern in array lists as the values.
     * @param guess, the current word from available words that a pattern is being created for.
     * @return the map of all possible patterns and the lists of words corresponding to
     * each pattern.
     */
     private HashMap<String, ArrayList<String>> patternMapMaker(char guess){
    	//adds all pattern keys and their word values to map
    	HashMap<String, ArrayList<String>> tempMap = new HashMap<>();
    	for (String currentWord: wordBank) {
    		//creates new pattern for each word
    		String newPattern = patternMaker(guess, currentWord);
    		//adds new pattern to map if not already present in key set
    		if (!tempMap.containsKey(newPattern)) {
    			ArrayList<String> newValue = new ArrayList<>();
    			newValue.add(currentWord);
    			tempMap.put(newPattern, newValue);
    		} else {
    			//if pattern already present, adds the word to its corresponding pattern's
        		//value arraylist
    			tempMap.get(newPattern).add(currentWord);
    		}
    	}
    	return tempMap;
    }
    
    /**
     * Helper method for patternMapMaker method that creates a new pattern from given word.
     * @param guess, the current guessed character.
     * @param currentWord, the current word from available words that a
     * pattern is being created for.
     * @return the pattern for the given word and guess.
     */
    private String patternMaker(char guess, String currentWord) {
    	StringBuilder sb = new StringBuilder();
    	//creates new pattern
		for (int i = 0; i < wordLength; i++) {
			//finds if current index in pattern already equals another letter
			if (pattern.charAt(i) == '-') {
				//finds if current index in word equals the guessed letter
			    if (currentWord.charAt(i) == guess) {
				    sb.append(guess);
			    } else {
			    	sb.append("-");
			    }
			} else {
				//adds already found letter to new pattern
				sb.append(pattern.charAt(i));
			}
		}
		return sb.toString();
    }
    
    /**
     * Helper method for makeGuess method that finds the second hardest list of
     * words if it exists.
     * @param patterns, the map of current possible patterns and their corresponding
     * arraylists of words.
     * @param guess, the current guessed character.
     * @return the second hardest list of words from the map.
     */
    private ArrayList<String> secondHardestPattern(Map<String, ArrayList<String>> patterns,
    		                                       char guess){
    	//finds the hardest word list and updates pattern to this new hardest pattern
    	ArrayList<String> newWordList = hardestPattern(patterns, guess);
    	//finds the second hardest word list and updates pattern to this second hardest pattern
    	//if the second hardest word list exists
    	if (patterns.size() > 1) {
    		//temporarily stores old pattern and hardest word list
    		String tempPattern = pattern;
    		ArrayList<String> tempWordList = new ArrayList<>(newWordList);
    		//removes hardest pattern from map, finds next hardest pattern,
    		//re adds hardest pattern
    	    patterns.remove(pattern);
    	    newWordList = hardestPattern(patterns, guess);
    	    patterns.put(tempPattern, tempWordList);
        }
    	return newWordList;
    }
    
    /**
     * Helper method for makeGuess method that finds the hardest list of words.
     * @param patterns, the map of current possible patterns and their corresponding
     * arraylists of words.
     * @param guess, the current guessed character.
     * @return the hardest list of words from the map.
     */
    private ArrayList<String> hardestPattern(Map<String, ArrayList<String>> patterns, char guess){
    	int maxSize = 0;
    	for (String pat: patterns.keySet()) {
    		//finds word list with the largest size
    		if (patterns.get(pat).size() > maxSize) {
    			pattern = pat;
    			maxSize = patterns.get(pat).size();
    		//if tied for largest size, finds which list reveals less letters or if tied again,
    		//finds which has smaller lexicographical order based on ASCII codes
    		} else if (patterns.get(pat).size() == maxSize) {
    			int prevCount = 0;
    			int newCount = 0;
    			//traverses both patterns to determine number of letters revealed by each
    			for (int i = 0; i < wordLength; i++) {
    				if (pattern.charAt(i) == guess) {
    					prevCount++;
    				}
    				if (pat.charAt(i) == guess) {
    					newCount++;
    				}
    			}
    			//finds which reveals less letters or which has smaller lexicographical
    			//order based on ASCII codes
    			if (prevCount > newCount || pattern.compareTo(pat) > 0) {
    				pattern = pat;
    				maxSize = patterns.get(pat).size();
    			}
    		}
    	}
    	return patterns.get(pattern);
    }


    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
    	if (numWordsCurrent() <= 0) {
    		throw new IllegalArgumentException("Violation of precondition: getSecretWord."
    				+ "numWordsCurrent() must be greater than 0");
    	}
    	Random r = new Random();
    	//picks a random word from remaining words with random index
        return wordBank.get(r.nextInt(wordBank.size()));
    }
}