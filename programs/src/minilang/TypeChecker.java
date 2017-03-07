package minilang;

import minilang.parser.*;
import minilang.lexer.*;
import minilang.node.*;
import minilang.analysis.*;
import minilang.Util.*;
import java.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TypeChecker extends DepthFirstAdapter {

  private static int m_lineno = -1;
	
  public static void print(Node node) {
      Util.openFile(Util.SYMBOL_TABLE_FILE);
      node.apply(new TypeChecker());
      Util.closeFile();
  }

  private void puts(String s) {
      //System.out.print(s);
      //System.out.flush();
      Util.writeFile(s);
  }

  /* program */
  public void caseAProgramProg(AProgramProg node) {
	for(PDeclist e : node.getDeclist())
        {
		e.apply(this);
        }	
	for(PStmtlist e : node.getStmtlist())
        {
                e.apply(this);
        }
  }

  /* declist */
  public void caseADecDeclist(ADecDeclist node) {
	boolean redecl = false;
	//puts("var ");
	String id = node.getId().getText();
	int lineno = node.getId().getLine();	
	//puts(id);
        //puts(": ");
        node.getType().apply(this);
	//puts(";\n");

	PType p_type = node.getType();
	if (p_type instanceof AStringType) {
		if (Util.symbol_table.containsKey(id))
		{
			redecl = true;
		}
		else
		{
			Util.symbol_table.put(id, Type.STRING);	
			puts(id + ": " + Type.STRING.toString().toLowerCase() + "\n");
		}
	}
	else if (p_type instanceof AIntType) {
		if (Util.symbol_table.containsKey(id))
		{
			redecl = true;
		}
		else
		{	
			Util.symbol_table.put(id, Type.INT);	
			puts(id + ": " + Type.INT.toString().toLowerCase() + "\n");
		}
	}
	else if (p_type instanceof AFloatType) {
		if (Util.symbol_table.containsKey(id))
		{
			redecl = true;
		}
		else
		{
			Util.symbol_table.put(id, Type.FLOAT);
			puts(id + ": " + Type.FLOAT.toString().toLowerCase() + "\n");	
		}
	}	
	if (redecl) 
	{
		String msg = "INVALID: line " + lineno + ": identifier " + id + " already declared" + "\n";
		System.out.println(msg);
		puts(msg);
		System.exit(1);	
	}	
  } 

  /* int type */
  public void caseAIntType(AIntType node) {
  	//puts(node.getInt().getText());
  } 

  /* float type */
  public void caseAFloatType(AFloatType node) {
  	//puts(node.getFloat().getText());
  }

  /* string type */
  public void caseAStringType(AStringType node) {
	//puts(node.getString().getText());
  }
 
  /* assignment */
  public void caseAAssignmentStmtlist(AAssignmentStmtlist node) {
	Type typeId = Type.NONE;
	Type typeExp = Type.NONE;		
	int lineno = node.getId().getLine();
		
	String id = node.getId().getText();

	if (!Util.symbol_table.containsKey(id))
	{
		String msg = "INVALID: line " + lineno + ": undeclared identifier: " + id + "\n";
		System.out.println(msg);
		puts(msg);
		System.exit(1);	
	}
	else 
	{
		typeId = Util.symbol_table.get(id);
	}
	//puts(id);
	
	//puts(" = ");
        node.getExp().apply(this);
	typeExp = Util.exptype_table.get(node.getExp());
	if (typeId != typeExp)
	{
		if ((typeId == Type.STRING || typeExp == Type.STRING) || (typeId == Type.INT && typeExp == Type.FLOAT))
		{
			String msg = "INVALID: line " + lineno + ": Cannot assignt " + typeExp.toString().toLowerCase() + 
				" into a " + typeId.toString().toLowerCase() + "\n";
			System.out.println(msg);
			puts(msg);
			System.exit(1);
		}
	}

	//puts(";\n");
  }

  /* print exp */ 
  public void caseAPrintExpStmtlist(APrintExpStmtlist node) {
	//puts("print ");
	node.getExp().apply(this);
	//TODO 
	//puts(";\n");
  }

  /* read id */
  public void caseAReadIdStmtlist(AReadIdStmtlist node) {
	//TODO	
	//puts("read ");
	//puts(node.getId().getText());
	//puts(";\n");
  }	

  /* if */
  public void caseAIfStmtlist(AIfStmtlist node) {
	//puts("if ");
	node.getExp().apply(this);
	Type typeExp = Util.exptype_table.get(node.getExp());
	if (typeExp != Type.INT)
	{
		String msg = "INVALID: line " + m_lineno + ": expected int, found " + typeExp.toString().toLowerCase() + "\n";
		System.out.println(msg);
		puts(msg);
		System.exit(1);
	}
	//puts(" then\n");
	
	for (PStmtlist e: node.getStmtlist())
	{
		e.apply(this);
	}

        if (node.getOptionalElse() != null)
	{
		node.getOptionalElse().apply(this);
	}
	//puts("endif\n");
  }

  /* optional else */
  public void caseAElseOptionalElse(AElseOptionalElse node) {
	//puts("else \n");

	for (PStmtlist e: node.getStmtlist())
	{
		e.apply(this);
	}
  }

  /* while */
  public void caseAWhileStmtlist(AWhileStmtlist node) {
	//puts("while ");
	node.getExp().apply(this);
	Type typeExp = Util.exptype_table.get(node.getExp());
	if (typeExp != Type.INT)
	{
		String msg = "INVALID: line " + m_lineno + ": expected int, found " + typeExp.toString().toLowerCase() + "\n";
		System.out.println(msg);
		puts(msg);
		System.exit(1);
	}
	//puts(" do\n");

	for (PStmtlist e: node.getStmtlist())
	{
		e.apply(this);
	}
	//puts("done\n");
  }

  /* plus */ 
  public void caseAPlusExp(APlusExp node) {
      //puts("(");
      node.getL().apply(this);
      //puts(" + ");
      node.getR().apply(this);
      //puts(")");

      Type typeL = Util.exptype_table.get(node.getL());
      Type typeR = Util.exptype_table.get(node.getR());

      /* type checking */
      if (typeL == Type.STRING && typeR == Type.STRING)
      {
	Util.exptype_table.put(node, Type.STRING);
      }
      else if (typeL == Type.STRING || typeR == Type.STRING)
      {
	String msg = "INVALID: line " + m_lineno + ": Cannot use + on " + 
		typeL.toString().toLowerCase() + " and " + typeR.toString().toLowerCase() + "\n";
	System.out.println(msg);
	puts(msg);
	 System.exit(1);	
      } 
      else if (typeL == Type.FLOAT || typeR == Type.FLOAT)
      {
	 Util.exptype_table.put(node, Type.FLOAT);
      } 
      else if (typeL == Type.INT && typeR == Type.INT)
      {
	 Util.exptype_table.put(node, Type.INT);
      } 
  }

  /* minus */
  public void caseAMinusExp(AMinusExp node) { 
      //puts("(");
      node.getL().apply(this);
      //puts(" - ");
      node.getR().apply(this);
      //puts(")");

      Type typeL = Util.exptype_table.get(node.getL());
      Type typeR = Util.exptype_table.get(node.getR());

      /* type checking */
      if (typeL == Type.STRING && typeR == Type.STRING)
      {
	Util.exptype_table.put(node, Type.STRING);
      }
      else if (typeL == Type.STRING || typeR == Type.STRING)
      {
	String msg = "INVALID: line " + m_lineno + ": Cannot use - on " + 
		typeL.toString().toLowerCase() + " and " + typeR.toString().toLowerCase() + "\n";
	System.out.println(msg);
	puts(msg);
	 System.exit(1);	
      } 
      else if (typeL == Type.FLOAT || typeR == Type.FLOAT)
      {
	 Util.exptype_table.put(node, Type.FLOAT);
      } 
      else if (typeL == Type.INT && typeR == Type.INT)
      {
	 Util.exptype_table.put(node, Type.INT);
      } 
  }

  /* mult */
  public void caseAMultExp(AMultExp node) { 
      //puts("(");
      node.getL().apply(this);
      //puts(" * ");
      node.getR().apply(this);
      //puts(")");

      Type typeL = Util.exptype_table.get(node.getL());
      Type typeR = Util.exptype_table.get(node.getR());

      /* type checking */
      if (typeL == Type.STRING || typeR == Type.STRING)
      {
	String msg = "INVALID: line " + m_lineno + ": Cannot use * on " + 
		typeL.toString().toLowerCase() + " and " + typeR.toString().toLowerCase() + "\n";
	System.out.println(msg);
	 puts(msg);
	 System.exit(1);
      }
      else if (typeL == Type.FLOAT || typeR == Type.FLOAT)
      {
	 Util.exptype_table.put(node, Type.FLOAT);
      } 
      else if (typeL == Type.INT && typeR == Type.INT)
      {
	 Util.exptype_table.put(node, Type.INT);
      } 
  }

  /* div */
  public void caseADivdExp(ADivdExp node) {
      //puts("(");
      node.getL().apply(this);
      //puts(" / ");
      node.getR().apply(this);

      Type typeL = Util.exptype_table.get(node.getL());
      Type typeR = Util.exptype_table.get(node.getR());
      //puts(")");

      /* type checking */
      if (typeL == Type.STRING || typeR == Type.STRING)
      {
	String msg = "INVALID: line " + m_lineno + ": Cannot use / on " + 
		typeL.toString().toLowerCase() + " and " + typeR.toString().toLowerCase() + "\n";
	System.out.println(msg);
	 puts(msg);
	 System.exit(1);
      }
      else if (typeL == Type.FLOAT || typeR == Type.FLOAT)
      {
	 Util.exptype_table.put(node, Type.FLOAT);
      } 
      else if (typeL == Type.INT && typeR == Type.INT)
      {
	 Util.exptype_table.put(node, Type.INT);
      } 
  }

  /* unary minus */
  public void caseAUminusExp(AUminusExp node) {
        //puts("-");
        node.getExp().apply(this);
	Type type = Util.exptype_table.get(node.getExp());
	Util.exptype_table.put(node, type);
  }

  /* identifier */
  public void caseAIdExp(AIdExp node) {
      	//puts(node.getId().getText());
      	int lineno = node.getId().getLine();
	m_lineno = lineno;
	String id = node.getId().getText();
	Type typeId = Type.NONE;

	/* check symbol table */
	if (!Util.symbol_table.containsKey(id))
	{
		String msg = "INVALID: line " + lineno + ": undeclared identifier: " + id + "\n";
		System.out.println(msg);
		puts(msg);
		System.exit(1);	
	}
	else 
	{
		typeId = Util.symbol_table.get(id);
	}	
	Util.exptype_table.put(node, typeId);
  }
  
  /* int number */
  public void caseAIntNumberExp(AIntNumberExp node) {
	m_lineno = node.getIntNumber().getLine();
	Util.exptype_table.put(node, Type.INT);
      	//puts(node.getIntNumber().getText());
  }
 
  /* float number */
  public void caseAFloatNumberExp(AFloatNumberExp node) {
	m_lineno = node.getFloatNumber().getLine();
	Util.exptype_table.put(node, Type.FLOAT);
	//puts(node.getFloatNumber().getText());
  }

  /* string */
  public void caseAStrExp(AStrExp node) {
	m_lineno = node.getStr().getLine();
	Util.exptype_table.put(node, Type.STRING);
  	//puts(node.getStr().getText());
  } 
 
}
