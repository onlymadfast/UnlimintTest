package com.tsipadan;

import com.tsipadan.app.Converter.Converter;
import com.tsipadan.app.model.Input;
import com.tsipadan.app.model.Output;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConverterTest {

  private final Converter converter = Converter.getInstance();

  private final String filename = "file.ext";
  private final Long line = 42L;
  private final Long id = 100L;
  private final BigDecimal amount = new BigDecimal(23);
  private final String currency = "USD";
  private final String comment = "payment";
  private final String error = "some error";

  @Test
  public void convertToOutTest() {
    Input source = new Input(id.toString(), amount.toString(), currency, comment);
    Output target = Converter.convertToOut(source, filename, line, null);
    assertEquals(id, target.getId());
    assertEquals(amount, target.getAmount());
    assertEquals(currency, target.getCurrency());
    assertEquals(comment, target.getComment());
    assertEquals(filename, target.getFilename());
    assertEquals(line, target.getLine());
    assertEquals("OK", target.getResult());

    target = Converter.convertToOut(null, filename, null, error);
    assertNull(target.getId());
    assertNull(target.getAmount());
    assertNull(target.getCurrency());
    assertNull(target.getComment());
    assertNull(target.getLine());
    assertEquals(filename, target.getFilename());
    assertEquals(error, target.getResult());

    source.setOrderId("123.4");
    source.setAmount("qwe");
    source.setCurrency(null);
    target = Converter.convertToOut(source, filename, line, null);
    assertNull(target.getId());
    assertNull(target.getAmount());
    assertNull(target.getCurrency());
    assertEquals(comment, target.getComment());
    assertEquals(filename, target.getFilename());
    assertEquals(line, target.getLine());
    assertEquals("id" + " is invalid " + "123.4, "
        + "amount" + " is invalid " + "qwe, "
        + "currency" + " not specific", target.getResult());

    source.setOrderId(null);
    source.setAmount(null);
    source.setCurrency(null);
    source.setComment(null);
    target = Converter.convertToOut(source, filename, null, null);
    assertNull(target.getComment());
    assertNull(target.getLine());
    assertEquals(filename, target.getFilename());
    assertEquals("id" + " not specific" + ", "
        + "amount" + " not specific" + ", "
        + "currency" + " not specific" + ", "
        + "comment" + " not specific", target.getResult());
  }

  @Test
  public void buildErrorStringTest() {
    String expected = "{\"" + "filename" + "\":\"" + filename + "\", \""
        + "line" + "\":42, \"" + "result" + "\":\"" + error + "\"}";
    String actual = Converter.errorStringConvert(filename, line, error);
    assertEquals(expected, actual);
    expected = "{\"" + "filename" + "\":\"" + filename + "\", \"" + "result" + "\":\"" + error + "\"}";
    actual = Converter.errorStringConvert(filename, null, error);
    assertEquals(expected, actual);
  }

  @Test
  public void convertOutToStringTest(){
    Output order = new Output();
    order.setId(id);
    order.setAmount(amount);
    order.setCurrency(currency);
    order.setComment(comment);
    order.setFilename(filename);
    order.setLine(line);
    order.setResult("OK");
    String expected = "{\"" + "id" + "\":" + order.getId()
        + ", \"" + "amount" + "\":" + order.getAmount()
        + ", \"" + "comment" + "\":\"" + order.getComment()
        + "\", \"" + "filename" + "\":\"" + order.getFilename()
        + "\", \"" + "line" + "\":" + order.getLine()
        + ", \"" + "result" + "\":\"" + order.getResult() + "\"}";
    String actual = converter.convertToString(order);
    assertEquals(expected, actual);
    order.setLine(null);
    expected = "{\"" + "id" + "\":" + order.getId()
        + ", \"" + "amount" + "\":" + order.getAmount()
        + ", \"" + "comment" + "\":\"" + order.getComment()
        + "\", \"" + "filename" + "\":\"" + order.getFilename()
        + "\", \"" + "result" + "\":\"" + order.getResult() + "\"}";
    actual = converter.convertToString(order);
    assertEquals(expected, actual);
  }

}
