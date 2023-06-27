package pl.edu.agh.kis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import pl.edu.agh.kis.compiler.Compiler;

public class App {
    public static void main(String[] args) {
//        String pattern = "SeqSeq(eat(\"amount\": \"int\"),Cond(tried(),watchTheWorld(\"amount\": \"int\"),composeLetters(\"number\": \"int\"),beHappy(\"amount\": \"int\")),sleep(\"hours\"))";
        String pattern = "SeqSeq(eat,Cond(tried,watchTheWorld(),composeLetters(),beHappy()),sleep())";
//            String pattern = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
        String code = Compiler.compile(pattern, Compiler.Language.PYTHON);
        System.out.print(code);
    }
}
