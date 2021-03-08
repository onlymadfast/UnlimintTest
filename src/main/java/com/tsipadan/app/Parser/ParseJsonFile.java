package com.tsipadan.app.Parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsipadan.app.Converter.Converter;
import com.tsipadan.app.exception.ParserException;
import com.tsipadan.app.model.Input;
import com.tsipadan.app.model.Output;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parser JSON to output line
 */
public class ParseJsonFile extends ParserAPI {

  private final ObjectMapper objectMapper;

  public ParseJsonFile(String fileName) {
    super(fileName);
    this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Parse input to output
   *
   * @param bufferedReader - input
   * @return output line
   */
  @Override
  protected List<Output> parse(BufferedReader bufferedReader) {

    String data = bufferedReader.lines().collect(Collectors.joining());
    if (StringUtils.isBlank(data)) throw new ParserException("File is empty");

    Object json;
    try {
      json = new JSONTokener(data).nextValue();
    } catch (JSONException jsonException) {
      throw new ParserException("Json is invalid");
    }

    //JSON with single of input
    if (json instanceof JSONObject) {
      Input input;
      try {
        input = objectMapper.readValue(data, Input.class);
      } catch (Exception e) {
        throw new ParserException(deserializationErrorHandler(e.getMessage()));
      }
      return Collections.singletonList((Converter.convertToOut(input, filename, null, null)));

      //JSON with array of input
    } else if (json instanceof JSONArray) {
      List<Input> sourceArray;
      try {
        sourceArray = objectMapper.readValue(data, new TypeReference<List<Input>>() {
        });
      } catch (Exception e) {
        throw new ParserException(deserializationErrorHandler(e.getMessage()));
      }
      return sourceArray.parallelStream().map(s -> Converter.convertToOut(s, filename, null, null))
          .collect(Collectors.toList());
      //invalid JSON
    } else {
      throw new ParserException("json is invalid");
    }

  }

  //Error handler
  private String deserializationErrorHandler(String errorMsg) {
    return errorMsg.substring(0, errorMsg.indexOf('\n')).replace('"', '`');
  }

}
