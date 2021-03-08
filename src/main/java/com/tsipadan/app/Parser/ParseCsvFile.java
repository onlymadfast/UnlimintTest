package com.tsipadan.app.Parser;

import com.tsipadan.app.Converter.Converter;
import com.tsipadan.app.exception.ParserException;
import com.tsipadan.app.model.Input;
import com.tsipadan.app.model.Output;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Parser CSV files
 */
public class ParseCsvFile extends ParserAPI {

  public ParseCsvFile(String filename) {
    super(filename);
  }

  /**
   * Parser input CSV to output line
   *
   * @param bufferedReader - input
   * @return output line
   */
  @Override
  protected List<Output> parse(BufferedReader bufferedReader) {
    Iterable<CSVRecord> csvRecords;
    try {
      csvRecords = CSVFormat.EXCEL.parse(bufferedReader);
    } catch (IOException e) {
      throw new ParserException(e.getMessage());
    }

    List<Output> outputs = StreamSupport.stream(csvRecords.spliterator(), true)
        .map(this::parseRecord).collect(Collectors.toList());

    if (outputs.isEmpty()) throw new ParserException("File is empty");
    else return outputs;
  }

  /**
   * Parser records
   *
   * @param record - rows
   * @return output
   */
  private Output parseRecord(CSVRecord record) {
    Output target;
    if (record.size() == 4) {
      Input source = new Input(record.get(0), record.get(1), record.get(2), record.get(3));
      target = Converter.convertToOut(source, filename, record.getRecordNumber(), null);
    } else {
      target = Converter.convertToOut(null, filename, record.getRecordNumber(), "invalid count of columns " + record.size());
    }
    return target;
  }

}
