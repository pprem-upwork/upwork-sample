package com.sahaj.farecalcengine.exceptions;


import lombok.*;


@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FareCalcException extends RuntimeException {
  private String message;
  private String details;
  private String hint;
  private String nextActions;
  private String support;

  public FareCalcException(String message){
    this.message = message;
  }



  }