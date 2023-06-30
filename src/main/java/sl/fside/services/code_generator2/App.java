package sl.fside.services.code_generator2;

import sl.fside.services.code_generator2.compiler.Compiler;

public class App {
    public static void main(String[] args) {
//        String pattern = "SeqSeq(eat(\"amount\": \"int\"),Cond(tried(),watchTheWorld(\"amount\": \"int\"),composeLetters(\"number\": \"int\"),beHappy(\"amount\": \"int\")),sleep(\"hours\"))";
//        String pattern = "SeqSeq(eat,Cond(tried,watchTheWorld(),composeLetters(),beHappy()),sleep())";
//        String pattern = "Seq(Seq(\"a\",\"b\"),\"c\")";
//        String pattern = "Seq(Branch(a,b,c), BranchRe(d,e,f))";
        String pattern = "Seq(Concur(a,b,c), ConcurRe(d,e,f))";
//            String pattern = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
        String code = Compiler.compile(pattern, Compiler.Language.JAVA);
        System.out.print(code);
    }
}
