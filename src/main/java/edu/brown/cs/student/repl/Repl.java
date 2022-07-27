package edu.brown.cs.student.repl;


import edu.brown.cs.student.main.ErrorMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/** Class that executes a REPL to continually accept user commands.
 */
public class Repl {
  private final Map<String, BiFunction<String, String, String>> validCommands;

  /** Create an instance of a Repl with the passed valid commands and corresponding
   methods to handle those commands.
   @param validCommands A Hashmap of Strings and a Function that takes a String and
   returns a String back to the method, where the former is the command root (eg "stars")
   and the latter is the method that handles all commands with that root.
   */
  public Repl(Map<String, BiFunction<String, String, String>> validCommands) {
    this.validCommands = new HashMap<>(validCommands);
  }

  /** Activate the REPL and continually expect prompts unless hit with an EOF.
   @param reader A Reader that stimulates either a Stream Input for actual user REPL
   or manual input for testing.
   */
  public void activate(Reader reader) {
    BufferedReader in
        = new BufferedReader(reader);

    try {
      String command;
      while ((command = in.readLine()) != null) {
        String[] currentLine = command.split(" ");
        if (currentLine.length != 0 && validCommands.containsKey(currentLine[0])) {

          String message = validCommands.get(currentLine[0]).apply(command, "repl");
          if (!message.equals("")) {
            printMessage(message);
          }
        } else {
          printMessage(ErrorMessages.REPL_INVALID_COMMAND);
        }
      }
      in.close();
    } catch (IOException e) {
      printMessage(ErrorMessages.INVALID_STREAM_INPUT);
      this.activate(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
  }

  /** Return a String error or computation outcome of the process called.
   @param message A String containing the message to be consoled.
   */
  void printMessage(String message) {
    System.out.println(message);
  }
}
