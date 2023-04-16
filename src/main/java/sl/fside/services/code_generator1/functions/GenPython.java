package sl.fside.services.code_generator1.functions;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import sl.fside.services.code_generator1.gen.*;
import sl.fside.services.code_generator1.parsers.*;

import java.io.*;

public class GenPython {
    public static void GenPython(String input) throws IOException {
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
         * Generate PYTHON code
         *
         */

        PythonLexer lexerPython = new PythonLexer(in);
        CommonTokenStream tokensPython = new CommonTokenStream(lexerPython);
        PythonParser parserPython = new PythonParser(tokensPython);
        ParseTree treePython = switch (split[0]) {
            case "Seq" -> parserPython.seq();
            case "Branch" -> parserPython.branch();
            case "Concur" -> parserPython.concur();
            case "Cond" -> parserPython.cond();
            case "Para" -> parserPython.para();
            case "Loop" -> parserPython.loop();
            case "Choice" -> parserPython.choice();
            case "SeqSeq" -> parserPython.seqSeq();
            case "Repeat" -> parserPython.repeat();
            default -> throw new IllegalStateException("Unexpected value: " + split[0]);
        };
        ParseTreeWalker walkerPython = new ParseTreeWalker();
        PythonLikeParser listenerPython = new PythonLikeParser();
        walkerPython.walk(listenerPython, treePython);
    }
}
