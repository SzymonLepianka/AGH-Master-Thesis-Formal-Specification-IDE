package sl.fside.services.code_generator1.functions;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import sl.fside.services.code_generator1.gen.*;
import sl.fside.services.code_generator1.parsers.*;

import java.io.*;

public class GenJava {
    public static void GenJava(String input) throws IOException {
        CharStream in;
        String split [];
        if(input != null){
            in = CharStreams.fromString(input);
            split = in.toString().split("[(]",2);
        }else{
            in = CharStreams.fromFileName("src/INPUT.cc");
            split = in.toString().split("[(]",2);
        }
        /**
         * Generate JAVA code
         *
         */
        JavaLexer lexer = new JavaLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = switch (split[0]) {
            case "Seq" -> parser.seq();
            case "Branch" -> parser.branch();
            case "Concur" -> parser.concur();
            case "Cond" -> parser.cond();
            case "Para" -> parser.para();
            case "Loop" -> parser.loop();
            case "Choice" -> parser.choice();
            case "SeqSeq" -> parser.seqSeq();
            case "Repeat" -> parser.repeat();
            default -> throw new IllegalStateException("Unexpected value: " + split[0]);
        };
        ParseTreeWalker javaWalker = new ParseTreeWalker();
        JavaLikeParser listener = new JavaLikeParser();
        javaWalker.walk(listener, tree);
    }
}
