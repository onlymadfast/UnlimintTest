package com.tsipadan.app.model;

import lombok.*;

/**
 * Input values
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Input {

  private String orderId;
  private String amount;
  private String currency;
  private String comment;

}
