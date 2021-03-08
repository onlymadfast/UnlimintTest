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

public class ParserJson {

  private final ClassLoader classLoader = getClass().getClassLoader();
  private final ParserFactory factory = ParserFactory.getInstance();
  private final Converter converter = Converter.getInstance();

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void fileNotFoundTest() {
    expectedEx.expect(ParserException.class);
    expectedEx.expectMessage("File not found");
    ParserAPI parser = factory.getParserByFileName("ooorders.json");
    parser.execute();
  }

  @Test
  public void invalidJsonTest() {
    expectedEx.expect(ParserException.class);
    expectedEx.expectMessage("Json is invalid");
    File file = new File(Objects.requireNonNull(classLoader.getResource("invalid.json")).getFile());
    ParserAPI parser = factory.getParserByFileName(file.getAbsolutePath());
    parser.execute();
  }

  @Test
  public void emptyFileTest() {
    expectedEx.expect(ParserException.class);
    expectedEx.expectMessage("File is empty");
    File file = new File(Objects.requireNonNull(classLoader.getResource("empty.json")).getFile());
    ParserAPI parser = factory.getParserByFileName(file.getAbsolutePath());
    parser.execute();
  }

  @Test
  public void singleObjectTest() {
    File file = new File(Objects.requireNonNull(classLoader.getResource("object.json")).getFile());
    ParserAPI parser = factory.getParserByFileName(file.getAbsolutePath());
    List<Output> orders = parser.execute();
    assertEquals(1, orders.size());
    Output order = orders.get(0);
    assertNull(order.getLine());
    assertEquals(Long.valueOf(1), order.getId());
    assertEquals(new BigDecimal(100), order.getAmount());
    assertEquals("USD", order.getCurrency());
    assertEquals("оплата заказа", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("OK", order.getResult());
  }

  @Test
  public void executeSuccessTest() {
    File file = new File(Objects.requireNonNull(classLoader.getResource("array.json")).getFile());
    ParserAPI parser = factory.getParserByFileName(file.getAbsolutePath());
    List<Output> orders = parser.execute();
    orders.stream().map(converter::convertToString).forEach(System.out::println);
    assertEquals(6, orders.size());

    Output order = orders.stream().filter(o -> o.getAmount().equals(new BigDecimal(150))).findFirst().get();
    assertNull(order.getId());
    assertEquals("KZH", order.getCurrency());
    assertEquals("оплата заказа11", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("id" + " not specific", order.getResult());

    order = orders.stream().filter(o -> o.getAmount() == null).findFirst().get();
    assertEquals(Long.valueOf(12), order.getId());
    assertEquals("BLR", order.getCurrency());
    assertEquals("оплата заказа12", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("amount" + " not specific", order.getResult());

    order = orders.stream().filter(o -> o.getResult().equals("OK")).findFirst().get();
    assertEquals(Long.valueOf(13), order.getId());
    assertEquals(new BigDecimal(350), order.getAmount());
    assertEquals("UGG", order.getCurrency());
    assertEquals("оплата заказа13", order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());

    order = orders.stream().filter(o -> o.getCurrency().equals("GBP")).findFirst().get();
    assertEquals(Long.valueOf(14), order.getId());
    assertNull(order.getAmount());
    assertNull(order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("amount" + " is invalid " + "q450"
        + ", " + "comment" + " not specific", order.getResult());

    order = orders.stream().filter(o -> "800".equals(o.getComment())).findFirst().get();
    assertNull(order.getId());
    assertNull(order.getCurrency());
    assertEquals(new BigDecimal(450), order.getAmount());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("id" + " is invalid " + "15w"
        + ", " + "currency" + " not specific", order.getResult());

    order = orders.stream().filter(o -> (o.getAmount() == null && o.getId() == null)).findFirst().get();
    assertNull(order.getCurrency());
    assertNull(order.getComment());
    assertEquals(file.getAbsolutePath(), order.getFilename());
    assertEquals("id" + " not specific"
        + ", " + "amount" + " not specific"
        + ", " + "currency" + " not specific"
        + ", " + "comment" + " not specific", order.getResult());
  }

}
