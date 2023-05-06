package org.example.functions;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.example.gen.JavaLexer;
import org.example.gen.JavaParser;
import org.example.parsers.JavaLikeParser;

import java.io.IOException;

public class GenJava {
    public static String genJava(String input, String UUID) throws IOException {
        CharStream in;
        String[] split;
        if (input != null) {
            in = CharStreams.fromString(input);
        } else {
            in = CharStreams.fromFileName("src/INPUT.cc");
        }
        split = in.toString().split("[(]", 2);

        JavaLexer lexer = new JavaLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        JavaLikeParser.SetUUID(UUID);
        ParseTree tree = switch (split[0]) {
            case "Seq" -> parser.seq();
            case "Alt" -> parser.alt();
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

        return JavaLikeParser.getResult();
    }
}
