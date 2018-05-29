 package com.bonc.uni.nlp.exception;
 
 public class BoncException extends Exception
 {
   private static final long serialVersionUID = 1L;
 
   public BoncException()
   {
   }
 
   public BoncException(String message)
   {
     super(message);
   }
 
   public BoncException(String message, Throwable cause)
   {
     super(message);
   }
 
   public BoncException(Throwable cause)
   {
     super(cause.getMessage());
   }
 }
