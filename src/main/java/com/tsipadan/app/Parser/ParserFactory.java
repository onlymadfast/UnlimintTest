package com.tsipadan.app.Parser;

import com.tsipadan.app.exception.ParserException;

import java.io.InputStream;
import java.util.Properties;

public class ParserFactory {

  private static class SingletonHolder {
    private static final ParserFactory INSTANCE = new ParserFactory();
  }

  public static ParserFactory getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private final Properties parsers;

  private ParserFactory() {
    try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("parsers.properties")) {
      parsers = new Properties();
      parsers.load(in);
    } catch (Exception e) {
      throw new ParserException("Unable to load property file");
    }
  }

  public ParserAPI getParserByFileName(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    if ((dotIndex == -1) || (dotIndex == filename.length() - 1)) {
      throw new ParserException("File format not specific");
    }

    String extension = filename.substring(dotIndex + 1).toUpperCase();
    String className = parsers.getProperty(extension);
    if (className == null) {
      throw new ParserException(extension + "File are not supported");
    }

    ParserAPI parser;
    try {
      parser = (ParserAPI) Class.forName(className).getDeclaredConstructor(String.class).newInstance(filename);
    } catch (Exception e) {
      throw new ParserException("Unable to load" + extension + " - parser by name" + className);
    }
    return parser;
  }

}
