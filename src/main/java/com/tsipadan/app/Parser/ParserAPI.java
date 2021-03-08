package com.tsipadan.app.Parser;

import com.tsipadan.app.exception.ParserException;
import com.tsipadan.app.model.Output;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Global parser
 */
public abstract class ParserAPI {

  protected final String filename;

  public ParserAPI(String filename) {
    this.filename = filename;
  }

  /**
   * Execute parsing
   *
   * @return executed line
   */
  public List<Output> execute() {
    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filename))) {
      return parse(bufferedReader);
    } catch (IOException e) {
      throw new ParserException("File not found");
    }
  }

  /**
   * Parser input to output line
   *
   * @param bufferedReader - input
   * @return output line
   */
  protected abstract List<Output> parse(BufferedReader bufferedReader);

}
