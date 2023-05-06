package org.example.functions;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.example.gen.PythonLexer;
import org.example.gen.PythonParser;
import org.example.parsers.PythonLikeParser;

import java.io.IOException;

public class GenPython {
    public static String genPython(String input, String UUID) throws IOException {
        CharStream in;
        String[] split;
        if (input != null) {
            in = CharStreams.fromString(input);
        } else {
            in = CharStreams.fromFileName("src/INPUT.cc");
        }
        split = in.toString().split("[(]", 2);

        PythonLexer lexerPython = new PythonLexer(in);
        CommonTokenStream tokensPython = new CommonTokenStream(lexerPython);
        PythonParser parserPython = new PythonParser(tokensPython);
        PythonLikeParser.SetUUID(UUID);
        ParseTree treePython = switch (split[0]) {
            case "Seq" -> parserPython.seq();
            case "Alt" -> parserPython.alt();
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
        //Return string
        return PythonLikeParser.getResult();
    }
}
