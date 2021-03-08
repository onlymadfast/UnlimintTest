package com.tsipadan;

import com.tsipadan.app.Parser.ParserAPI;
import com.tsipadan.app.Parser.ParseCsvFile;
import com.tsipadan.app.Parser.ParseJsonFile;
import com.tsipadan.app.Parser.ParserFactory;
import com.tsipadan.app.exception.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

public class FactoryTest {

  private final ParserFactory factory = ParserFactory.getInstance();

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void formatNotSpecifiedTest() {
    expectedException.expect(ParserException.class);
    expectedException.expectMessage("File format not specific");
    factory.getParserByFileName("ololo.");
  }

  @Test
  public void formatNotSupportedTest() {
    String extension = "xxx";
    expectedException.expect(ParserException.class);
    expectedException.expectMessage(extension.toUpperCase() + "File are not supported");
    factory.getParserByFileName("ololo." + extension);
  }

  @Test
  public void unableToLoadTest(){
    expectedException.expect(ParserException.class);
    expectedException.expectMessage("Unable to load");
    factory.getParserByFileName("ololo.xlsx");
  }

  @Test
  public void getParserSuccessTest() {
    ParserAPI parser = factory.getParserByFileName("ololo.csv");
    assertTrue(parser instanceof ParseCsvFile);
    parser = factory.getParserByFileName("ololo.json");
    assertTrue(parser instanceof ParseJsonFile);
  }

}
