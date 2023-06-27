package sl.fside.services.code_generator2;

import sl.fside.services.code_generator2.compiler.Compiler;

public class App {
    public static void main(String[] args) {
        String pattern = "Seq(a, b)";
//            String pattern = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
        String code = Compiler.compile(pattern, Compiler.Language.PYTHON);
        System.out.print(code);
    }
}
