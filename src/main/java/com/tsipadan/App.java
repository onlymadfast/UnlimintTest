package com.tsipadan;

import com.tsipadan.app.Converter.Converter;
import com.tsipadan.app.Parser.ParserAPI;
import com.tsipadan.app.Parser.ParserFactory;
import com.tsipadan.app.exception.ParserException;
import com.tsipadan.app.model.Output;

import java.util.List;
import java.util.stream.Stream;

public class App {

  private static ParserFactory parserFactory;
  private static Converter converter;

  public static void main(String[] args) {

    if (args.length != 0) {
      try {
        parserFactory = ParserFactory.getInstance();
        converter = Converter.getInstance();
        Stream.of(args).distinct().parallel().forEach(App::processFile);
      } catch (ParserException e) {
        System.out.println(e.getMessage());
      }
    } else {
      System.out.println("Incorrect command: source files not specified");
    }
  }

  private static void processFile(String filename) {
    try {
      ParserAPI parser = parserFactory.getParserByFileName(filename);
      List<Output> outputs = parser.execute();
      outputs.parallelStream().forEach(o -> System.out.println(converter.convertToString(o)));
    } catch (Exception e) {
      System.out.println(Converter.errorStringConvert(filename, null, e.getMessage()));
    }
  }
}
