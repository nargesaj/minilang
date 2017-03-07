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

public class CodeGenerator extends DepthFirstAdapter {

  static FileWriter writer;

  Stack<String> st = new Stack<String>(); 
  
  public static void print(Node node) {
      Util.openFile(Util.C_CODE_FILE);
      node.apply(new CodeGenerator());
      Util.closeFile();
  }

  private void writePrelim()
  {
	puts("#include <stdio.h>\n");
	puts("#include <stdlib.h>\n");
	puts("#include <string.h>\n");
	puts("#define BUFSIZE 8192\n");
	puts("char *rev(char *s) {\n");
	puts("\tint len = strlen(s);\n");
	puts("\tchar *rev_s = malloc(sizeof(char) * (len + 1));\n");
	puts("\tfor (size_t i = 0; i < len; ++i) {\n");
	puts("\t\trev_s[len - i - 1] = s[i];\n");
	puts("\t}\n");
	puts("\treturn rev_s;\n");
	puts("}\n");
	puts("\n");
	puts("char *concat(char *s1, char *s2) {\n");
	puts("\tint len1 = strlen(s1);\n");
	puts("\tint len2 = strlen(s2);\n");
	puts("\tchar *new_s = malloc(sizeof(char) * (len1 + len2 + 1));\n");
	puts("\n");
	puts("\tsize_t k = 0;\n");
	puts("\tfor (size_t i = 0; i < len1; ++i) {\n");
	puts("\t\tnew_s[k++] = s1[i];\n");
	puts("\t}\n");
	puts("\tfor (size_t i = 0; i <= len2; ++i) {\n");
	puts("\t\tnew_s[k++] = s2[i];\n");
	puts("\t}\n");
	puts("\treturn new_s;\n");
	puts("}\n");
	puts("\n");
	puts("void read_line(char **dest, size_t maxlen) {\n");
	puts("\tfgets(*dest, maxlen, stdin);\n");
	puts("\tchar *newline = strrchr(*dest, '\\n');\n");
	puts("\tif (newline != NULL)\n");
	puts("\t\t*newline = '\\0';\n");
	puts("}\n");
	puts("\n");
  }

  private void puts(String s) {
      //System.out.print(s);
      //System.out.flush();
      Util.writeFile(s);	
  }

  /* program */
  public void caseAProgramProg(AProgramProg node) {
	writePrelim();	
	puts("int main(void)\n");
	puts("{\n");

	st.push("\t");

	for(PDeclist e : node.getDeclist())
        {
		String tab = st.peek();
		puts(tab);
		e.apply(this);
        }	
	
	for(PStmtlist e : node.getStmtlist())
        {
		String tab = st.peek();
		puts(tab);
		e.apply(this);
        }
	puts("}");
  }

  /* declist */
  public void caseADecDeclist(ADecDeclist node) {
	node.getType().apply(this);
	String id = node.getId().getText();
        puts(id);
	puts(" = ");
	PType p_type = node.getType();
	if (p_type instanceof AStringType) {
		puts("\"\";\n");
	}
	else if (p_type instanceof AIntType) {
		puts(" 0;\n");
	}
	else if (p_type instanceof AFloatType) {
		puts(" 0.0;\n");	
	}
 } 

  /* int type */
  public void caseAIntType(AIntType node) {
  	puts("int ");
  } 

  /* float type */
  public void caseAFloatType(AFloatType node) {
  	puts("float ");
  }

  /* string type */
  public void caseAStringType(AStringType node) {
	puts("char* ");
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
	puts("printf(");
	Type typeExp = Util.exptype_table.get(node.getExp());
	if (typeExp == Type.INT)
 	{
		puts("\"%d\\n\", ");
	}
	else if (typeExp == Type.FLOAT)
	{
		puts("\"%f\\n\", ");
	}
	else if (typeExp == Type.STRING)
	{
		puts("\"%s\\n\", ");
	} 
	node.getExp().apply(this);
	
	puts(");\n");
  }

  /* read id */
  public void caseAReadIdStmtlist(AReadIdStmtlist node) {
	String id = node.getId().getText();

	Type typeId = Util.symbol_table.get(id);
	
	if (typeId == Type.INT) {
		puts("scanf(\"%d\", &");
		puts(id);
		puts(");\n");
	}
	else if (typeId == Type.FLOAT) {
		puts("scanf(\"%f\", &");
		puts(id);
		puts(");\n");
	}
	if (typeId == Type.STRING) {
		puts(id);
		puts(" = malloc(BUFSIZE);\n");
		String tab = st.peek();
		puts(tab);
		puts("read_line(&");	
		puts(id);
		puts(", BUFSIZE);\n");
	}
  }	

  /* if */
  public void caseAIfStmtlist(AIfStmtlist node) {
	puts("if (");
	node.getExp().apply(this);
	puts(") {\n");

	// increase indent level after if
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

	// decrease indent level before }
	if (!st.empty()) 
	{
		st.pop();
		if (!st.empty())
		{
			tab = st.peek();
			puts(tab);
		}
	}
	puts("}\n");

        if (node.getOptionalElse() != null)
	{
		puts("\n");
		tab = st.peek();
		puts(tab);
		node.getOptionalElse().apply(this);
		// decrease indent level before }
		if (!st.empty()) 
		{
			st.pop();
			if (!st.empty())
			{
				tab = st.peek();
				puts(tab);
			}
		}
		puts("}\n");
	}
  }

  /* optional else */
  public void caseAElseOptionalElse(AElseOptionalElse node) {
	puts("else {\n");

	// increase indent level after else
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
  }

  /* while */
  public void caseAWhileStmtlist(AWhileStmtlist node) {
	puts("while (");
	node.getExp().apply(this);
	puts(") {\n");

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

	// decrease indent level before }
	if (!st.empty()) 
	{
		st.pop();
		if (!st.empty())
		{
			tab = st.peek();
			puts(tab);
		}
	}
	puts("}\n");
  }

  /* plus */ 
  public void caseAPlusExp(APlusExp node) {
	Type typeL = Util.exptype_table.get(node.getL());
     	Type typeR = Util.exptype_table.get(node.getR());

	if (typeL == Type.STRING && typeR == Type.STRING)
	{
		puts("concat(");
		node.getL().apply(this);
		puts(", ");
		node.getR().apply(this);
		puts(")");
	}
	else
	{
		puts("(");
		node.getL().apply(this);
		puts(" + ");
		node.getR().apply(this);
		puts(")");
	}
  }

  /* minus */
  public void caseAMinusExp(AMinusExp node) { 
      	Type typeL = Util.exptype_table.get(node.getL());
     	Type typeR = Util.exptype_table.get(node.getR());

	if (typeL == Type.STRING && typeR == Type.STRING)
	{
		puts("concat(");
		node.getL().apply(this);
		puts(", rev(");
		node.getR().apply(this);
		puts("))");
	}
	else
	{
		puts("(");
		node.getL().apply(this);
		puts(" - ");
		node.getR().apply(this);
		puts(")");
	}
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
	Type type = Util.exptype_table.get(node.getExp());
	if (type == Type.STRING)
	{
		puts("rev(");
		node.getExp().apply(this);
		puts(")");
	}
        else
	{
		puts("-");
		node.getExp().apply(this);
	}        
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
	String sFloat = String.format("%.4f", f);
	puts(sFloat);
  }

  /* string */
  public void caseAStrExp(AStrExp node) {
  	puts(node.getStr().getText());
  }  

}
