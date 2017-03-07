package minilang;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.*;

import minilang.parser.*;
import minilang.lexer.*;
import minilang.node.*;
import minilang.analysis.*;

public class Util {

  public static String PRETTY_FILE = "";
  public static String SYMBOL_TABLE_FILE = "";
  public static String C_CODE_FILE = "";	
	
  private static FileWriter writer;

  public static HashMap<String, Type> symbol_table = new HashMap<String, Type>();
  public static HashMap<PExp, Type> exptype_table = new HashMap<PExp, Type>();

  public static enum Type {
	NONE, INT, FLOAT, STRING
  }	

  public static void openFile(String path) 
  {
     try
      {
	writer = new FileWriter(path);
      } 
      catch (IOException e)
      {
	e.printStackTrace();
      }
  } 

  public static void closeFile()
  {
    try
    {
	writer.close();
    } 
    catch (IOException e) 
    {
	e.printStackTrace();
    }
  }

  public static void writeFile(String s)
  {
    try
      {
	writer.append(s);
	writer.flush();
      }
      catch (IOException e)
      {
	e.printStackTrace();
      } 
  }

}
