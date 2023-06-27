package sl.fside.services.code_generator2.compiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import sl.fside.services.code_generator2.PatternLexer;
import sl.fside.services.code_generator2.PatternParser;
import sl.fside.services.code_generator2.generator.Generator;
import sl.fside.services.code_generator2.generator.TargetConfig;
import sl.fside.services.code_generator2.model.Analyser;
import sl.fside.services.code_generator2.model.Model;
import sl.fside.services.code_generator2.validation.DefaultValidator;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Compiler for pattern language
 *
 * Given pattern source code and programming language, creates a skeleton of a program
 * with holes which can be filled with logic by human developer.
 */
public class Compiler {
    /**
     * Programming languages available as an output of compiler
     */
    public enum Language {
        JAVA (new TargetConfig("src/main/templates/java.stg")
                .counterConfig(Map.ofEntries(
                        entry(List.of("Concur", "Para"), 2)
                ))
                .validator(new DefaultValidator())
        ),
        PYTHON (new TargetConfig("src/main/templates/python.stg")
                .counterConfig(Map.ofEntries(
                        entry(List.of("Concur", "Para"), 2)
                ))
                .includes(Map.ofEntries(
                        entry("Concur", List.of("thread")),
                        entry("Para", List.of("thread"))
                ))
                .validator(new DefaultValidator())
        );

        Language(TargetConfig config) {
            this.config = config;
        }

        private final TargetConfig config;
    }


    private static String compile(String pattern, TargetConfig config) {
        CharStream stream = CharStreams.fromString(pattern);
        PatternLexer lexer = new PatternLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PatternParser parser = new PatternParser(tokens);
        Model model = Analyser.analyse(parser);

        if (config.getValidator() != null)
            config.getValidator().validate(model);
        return Generator.generate(model, config);
    }

    /**
     * Compile pattern source code
     * @param pattern pattern source code to compile
     * @param language output programming language
     * @return compiled program
     */
    public static String compile(String pattern, Language language) {
        return compile(pattern, language.config);
    }
}
