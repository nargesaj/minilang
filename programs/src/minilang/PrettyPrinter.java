package minilang;

import minilang.parser.*;
import minilang.lexer.*;
import minilang.node.*;
import minilang.analysis.*;
import java.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import minilang.Util;

public class PrettyPrinter extends DepthFirstAdapter {

  Stack<String> st = new Stack<String>(); 
  
  public static void print(Node node) {
      Util.openFile(Util.PRETTY_FILE);
      node.apply(new PrettyPrinter());
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

	puts("var ");
	String id = node.getId().getText();
        puts(id);
        puts(": ");
        node.getType().apply(this);
	puts(";\n");
  } 

  /* int type */
  public void caseAIntType(AIntType node) {
  	puts(node.getInt().getText());
  } 

  /* float type */
  public void caseAFloatType(AFloatType node) {
  	puts(node.getFloat().getText());
  }

  /* string type */
  public void caseAStringType(AStringType node) {
	puts(node.getString().getText());
  }
 
  /* assignment */
  public void caseAAssignmentStmtlist(AAssignmentStmtlist node) {
	puts(node.getId().getText());
	puts(" = ");
        node.getExp().apply(this);
	puts(";\n");
  }

  /* print exp */ 
  public void caseAPrintExpStmtlist(APrintExpStmtlist node) {
	puts("print ");
	node.getExp().apply(this);
	puts(";\n");
  }

  /* read id */
  public void caseAReadIdStmtlist(AReadIdStmtlist node) {
	puts("read ");
	puts(node.getId().getText());
	puts(";\n");
  }	

  /* if */
  public void caseAIfStmtlist(AIfStmtlist node) {
	puts("if ");
	node.getExp().apply(this);
	puts(" then\n");

	// increase indent level after if
	String tab = "";
	if (!st.empty())
	{
		tab = st.peek();
	}
	String else_tab = tab;
	tab += '\t';
	st.push(tab);

	for (PStmtlist e: node.getStmtlist())
	{
		puts(tab);
		e.apply(this);
	}

        if (node.getOptionalElse() != null)
	{
		puts(else_tab);
		node.getOptionalElse().apply(this);
	}

	// decrease indent level before endif
	if (!st.empty()) 
	{
		st.pop();
		if (!st.empty())
		{
			tab = st.peek();
			puts(tab);
		}
	}
	puts("endif\n");
  }

  /* optional else */
  public void caseAElseOptionalElse(AElseOptionalElse node) {
	puts("else \n");

	// increase indent level after else
	String tab = "";
	if (!st.empty())
	{
		tab = st.peek();
	}
  	
	for (PStmtlist e: node.getStmtlist())
	{
		puts(tab);
		e.apply(this);
	}
  }

  /* while */
  public void caseAWhileStmtlist(AWhileStmtlist node) {
	puts("while ");
	node.getExp().apply(this);
	puts(" do\n");

	// increase indent level after while
	String tab = "";
	if (!st.empty())
	{
		tab = st.peek();
	}

	tab += '\t';
	st.push(tab);

	for (PStmtlist e: node.getStmtlist())
	{
		puts(tab);
		e.apply(this);
	}

	// decrease indent level before done
	if (!st.empty()) 
	{
		st.pop();
		if (!st.empty())
		{
			tab = st.peek();
			puts(tab);
		}
	}
	puts("done\n");
  }

  /* plus */ 
  public void caseAPlusExp(APlusExp node) {
      puts("(");
      node.getL().apply(this);
      puts(" + ");
      node.getR().apply(this);
      puts(")");
  }

  /* minus */
  public void caseAMinusExp(AMinusExp node) { 
      puts("(");
      node.getL().apply(this);
      puts(" - ");
      node.getR().apply(this);
      puts(")");
  }

  /* mult */
  public void caseAMultExp(AMultExp node) { 
      puts("(");
      node.getL().apply(this);
      puts(" * ");
      node.getR().apply(this);
      puts(")");
  }

  /* div */
  public void caseADivdExp(ADivdExp node) {
      puts("(");
      node.getL().apply(this);
      puts(" / ");
      node.getR().apply(this);
      puts(")");
  }	

  /* unary minus */
  public void caseAUminusExp(AUminusExp node) {
        puts("-");
        node.getExp().apply(this);
  }

  /* identifier */
  public void caseAIdExp(AIdExp node) {
      puts(node.getId().getText());
  }
  
  /* int number */
  public void caseAIntNumberExp(AIntNumberExp node) {
      puts(node.getIntNumber().getText());
  }
 
  /* float number */
  public void caseAFloatNumberExp(AFloatNumberExp node) {
	float f = Float.parseFloat(node.getFloatNumber().getText());
	String sFloat = String.format("%.6f", f);
	puts(sFloat);
  }

  /* string */
  public void caseAStrExp(AStrExp node) {
  	puts(node.getStr().getText());
  }  

}
