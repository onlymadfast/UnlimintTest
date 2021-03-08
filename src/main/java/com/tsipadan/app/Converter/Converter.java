package com.tsipadan.app.Converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsipadan.app.model.Input;
import com.tsipadan.app.model.Output;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Converter input values to output line
 */
public class Converter {

  private final ObjectMapper objectMapper;

  public Converter() {
    this.objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  private static class SingletonHolder {
    private static final Converter INSTANCE = new Converter();
  }

  public static Converter getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Convert input to output line with validation elements
   *
   * @param input - input
   * @param filename - filename
   * @param line - line
   * @param errorMsg - error message
   * @return output line
   */
  public static Output convertToOut(Input input, String filename, Long line, String errorMsg) {
    Output target = new Output();
    target.setFilename(filename);
    target.setLine(line);
    if (input != null) {
      List<String> errors = new ArrayList<>();
      target.setId(convertId(input.getOrderId(), errors));
      target.setAmount(convertAmount(input.getAmount(), errors));
      target.setCurrency(validate("currency", input.getCurrency(), errors, null));
      target.setComment(validate("comment", input.getComment(), errors, null));
      if (!errors.isEmpty()) {
        target.setResult(String.join(", ", errors));
      } else {
        target.setResult("OK");
      }
    } else {
      target.setResult(errorMsg);
    }
    return target;
  }

  /**
   * Add white space between elements
   *
   * @param output - add whitespace
   * @return line
   */
  public String convertToString(Output output) {
    String string;
    try {
      string = objectMapper.writeValueAsString(output).replace(",\"", ", \"");
    } catch (JsonProcessingException e) {
      string = errorStringConvert(output.getFilename(), output.getLine(), e.getMessage());
    }
    return string;
  }

  /**
   * Valid toString method
   *
   * @param filename - filename
   * @param line - line
   * @param errorMsg - error
   * @return toString
   */
  public static String errorStringConvert(String filename, Long line, String errorMsg) {
    final StringBuilder stringBuilder = new StringBuilder("{");
    stringBuilder.append("\"" + "filename" + "\":\"").append(filename).append("\"");
    if (line != null) {
      stringBuilder.append(", \"" + "line" + "\":").append(line);
    }
    stringBuilder.append(", \"" + "result" + "\":\"").append(errorMsg);
    stringBuilder.append("\"}");
    return stringBuilder.toString();
  }

  /**
   * Convert id
   *
   * @param strId - input id
   * @param errors - error
   * @return id
   */
  private static Long convertId(String strId, List<String> errors) {
    Long id = null;
    String value;
    value = validate("id", strId, errors, val -> {
      if (!StringUtils.isNumeric(val)) {
        errors.add("id" + " is invalid " + val);
        return false;
      } else {
        return true;
      }
    });
    if (value != null) {
      id = Long.valueOf(value);
    }
    return id;
  }

  /**
   * Convert amount
   *
   * @param strAmount - input amount
   * @param errors - errors
   * @return amount
   */
  private static BigDecimal convertAmount(String strAmount, List<String> errors) {
    BigDecimal amount = null;
    String value;
    value = validate("amount", strAmount, errors, val -> {
      try {
        new BigDecimal(val);
        return true;
      } catch (NumberFormatException e) {
        errors.add("amount" + " is invalid " + val);
        return false;
      }
    });
    if (value != null) {
      amount = new BigDecimal(value);
    }
    return amount;
  }

  /**
   * Validate value (value must be not null)
   *
   * @param field - validated field
   * @param value - input value
   * @param errors - error message
   * @param stringPredicate - boolean
   * @return - result
   */
  private static String validate(String field, String value, List<String> errors, Predicate<String> stringPredicate) {
    String result = null;
    if (StringUtils.isNotBlank(value)) {
      result = value.trim();
      if (stringPredicate != null && !stringPredicate.test(result)) {
        result = null;
      }
    } else {
      errors.add(field + " not specific");
    }
    return result;
  }

}
