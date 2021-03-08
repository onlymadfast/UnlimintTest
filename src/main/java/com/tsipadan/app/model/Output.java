package com.tsipadan.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;

/**
 * Output values
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Output {

  private Long id;
  private BigDecimal amount;
  private String comment;
  @JsonIgnore //because in example output line this line is not
  private String currency;
  private String filename;
  private Long line;
  private String result;

}
