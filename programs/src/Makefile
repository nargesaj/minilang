
#Uncomment the next line and set it to the actual path of your SableCC Ver.3
#SABLECCDIR = $HOME/Sablecc3

# to compile the grammar using Sablecc3
all: 
	java -jar sablecc.jar minilang.sablecc
	javac minilang/lexer/*.java minilang/parser/*.java minilang/*.java
	
#compilecode: 
#	javac minilang/lexer/*.java minilang/parser/*.java minilang/*.java     

# to clean out all files
clean:
	find minilang -name '*.class' | xargs rm
	find minilang -name '*.dat' | xargs rm
	rm -f result

check:
	./check

realclean:	
	rm -fr minilang/*.class minilang/lexer/ minilang/parser/ minilang/node/ minilang/analysis/ result
