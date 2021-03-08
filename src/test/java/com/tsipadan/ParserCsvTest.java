package com.tsipadan;

import com.tsipadan.app.Converter.Converter;
import com.tsipadan.app.Parser.ParserAPI;
import com.tsipadan.app.Parser.ParserFactory;
import com.tsipadan.app.exception.ParserException;
import com.tsipadan.app.model.Output;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ParserCsvTest {

  private final ClassLoader classLoader = getClass().getClassLoader();
  private final ParserFactory factory = ParserFactory.getInstance();
  private final Converter converter = Converter.getInstance();

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void fileNotFoundTest() {
    expectedEx.expect(ParserException.class);
    expectedEx.expectMessage("File not found");
    ParserAPI parser = factory.getParserByFileName("ooorders.csv");
    parser.execute();
  }

  @Test
  public void emptyFileTest() {
    expectedEx.expect(ParserException.class);
    expectedEx.expectMessage("File is empty");
    File file = new File(Objects.requireNonNull(classLoader.getResource("empty.csv")).getFile());
    ParserAPI parser = factory.getParserByFileName(file.getAbsolutePath());
    parser.execute();
  }

  @Test
  public void executeSuccessTest() {
    File file = new File(Objects.requireNonNull(classLoader.getResource("orders.csv")).getFile());
    ParserAPI parser = factory.getParserByFileName(file.getAbsolutePath());
    List<Output> orders = parser.execute();
    orders.stream().map(converter::convertToString).forEach(System.out::println);
    assertEquals(9, orders.size());


    Output order = orders.stream().filter(o -> o.getLine() == 1L).findFirst().get();
    assertNull(order.getId());
    assertNull(order.getAmount());
    assertNull(order.getCurrency());
    assertEquals("оплата заказа1", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("id" + " not specific"
        + ", " + "amount" + " is invalid " + "a100"
        + ", " + "currency" + " not specific", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 2L).findFirst().get();
    assertEquals(Long.valueOf(2), order.getId());
    assertEquals(new BigDecimal(200), order.getAmount());
    assertEquals("RUB", order.getCurrency());
    assertEquals("оплата заказа2", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("OK", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 3L).findFirst().get();
    assertEquals(Long.valueOf(3), order.getId());
    assertEquals(new BigDecimal(300), order.getAmount());
    assertEquals("EUR", order.getCurrency());
    assertEquals("оплата заказа3", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("OK", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 4L).findFirst().get();
    assertNull(order.getId());
    assertEquals(new BigDecimal(400), order.getAmount());
    assertEquals("JPY", order.getCurrency());
    assertEquals("оплата заказа4", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("id" + " is invalid " + "4f", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 5L).findFirst().get();
    assertEquals(Long.valueOf(5), order.getId());
    assertNull(order.getAmount());
    assertEquals("BRP", order.getCurrency());
    assertEquals("оплата заказа5", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("amount" + " is invalid " + "dfg", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 6L).findFirst().get();
    assertEquals(Long.valueOf(6), order.getId());
    assertEquals(new BigDecimal(600), order.getAmount());
    assertEquals("USD", order.getCurrency());
    assertNull(order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("comment" + " not specific", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 7L).findFirst().get();
    assertEquals(Long.valueOf(7), order.getId());
    assertEquals(new BigDecimal(700), order.getAmount());
    assertNull(order.getCurrency());
    assertEquals("оплата заказа7", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("currency" + " not specific", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 8L).findFirst().get();
    assertNull(order.getId());
    assertNull(order.getAmount());
    assertNull(order.getCurrency());
    assertNull(order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("invalid count of columns " + "3", order.getResult());

    order = orders.stream().filter(o -> o.getLine() == 9L).findFirst().get();
    assertNull(order.getId());
    assertNull(order.getAmount());
    assertNull(order.getCurrency());
    assertNull(order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("invalid count of columns " + "5", order.getResult());
  }

}
