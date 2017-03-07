package minilang;

import minilang.parser.*; 
import minilang.lexer.*; 
import minilang.node.*; 
import minilang.Util.*;
import java.io.*;

public class Main{
    public static void main(String[] args){
	String path = args[0];
	InputStream inputStream = null;
	try 
	{
		inputStream = new FileInputStream(path);
	} 
	catch (Exception e)
	{
		System.out.println("Exception: " + e);
	}

	int index = path.indexOf(".min");
	String new_path = path.substring(0, index);
	Util.PRETTY_FILE = new_path + ".pretty.min";
	Util.SYMBOL_TABLE_FILE = new_path + ".symbol.txt";
	Util.C_CODE_FILE = new_path + ".c";

	try{
      		Parser p = new Parser( 
				new Lexer(
					new PushbackReader(
						new InputStreamReader(/*System.in*/inputStream), 1024)));
     
      Start tree = p.parse();
      //System.out.println("Valid");

      PrettyPrinter.print(tree);
      
      TypeChecker.print(tree);

      CodeGenerator.print(tree);
    }
    catch(Exception e){
      	System.out.println("Invalid: ");
	System.out.println(e);
        //e.printStackTrace();
    }
  }
}
