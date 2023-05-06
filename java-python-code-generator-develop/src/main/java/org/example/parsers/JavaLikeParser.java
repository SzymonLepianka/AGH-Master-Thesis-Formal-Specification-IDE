package org.example.parsers;

import org.example.gen.JavaBaseListener;
import org.example.gen.JavaParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class JavaLikeParser extends JavaBaseListener {
    List<String> functions = new ArrayList<>();
    Stack<String> stack = new Stack<>();
    static String FinalString = "";
    static String UUID = "";
    boolean isBranch= false;
    boolean isConcur= false;

    public static void SetUUID(String uuid) {
        UUID = uuid;
    }

    public void toFile() {
        StringBuilder data = new StringBuilder();
        for (String s : stack) {
            System.out.println(s);
            data.append(s);
        }

        String txtFile = "codeJava_" + UUID + ".txt";
        String javaFile = "codeJava_" + UUID + ".java";
        FinalString = data.toString();
        try {
            FileWriter writerTxt = new FileWriter(txtFile);
            writerTxt.write(data.toString());
            writerTxt.close();
            FileWriter writerJava = new FileWriter(javaFile);
            writerJava.write(data.toString());
            writerJava.close();
            System.out.println("The data has been saved to files " + txtFile + " and " + javaFile);
        } catch (IOException e) {
            System.out.println("An error occurred while saving to files.");
            e.printStackTrace();
            getResult();
        }
    }

    public static String getResult() {
        return FinalString;
    }

    @Override
    public void exitSeq(JavaParser.SeqContext ctx) {
        StringBuilder sb = new StringBuilder();
        if(isBranch||isConcur){

            String s1 = stack.pop();

            sb.append(s1).append("\n");
            if(isBranch){
                isBranch= false;
            }else{
                isConcur= false;
            }
        }else {
            String s2 = stack.pop();
            String s1 = stack.pop();

            sb.append(s1).append("\n").append(s2).append("\n");
        }
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }

    }
    @Override
    public void exitAlt(JavaParser.AltContext ctx){
        StringBuilder sb = new StringBuilder();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if(").append(s1.replace(";","")).append(") {\n").append(s2).append("\n}").append(s3).append("\n");

        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitFunction(JavaParser.FunctionContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.CharArray().getText()).append("(");
        for (JavaParser.Arg_javaContext arg : ctx.arg_java()) {
            sb.append(stack.pop().replace(";", "")).append(",");
        }
        if (ctx.arg_java().size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        stack.push(sb.toString() + ";");
        if (!functions.contains(sb.toString())) {
            functions.add(sb.toString());
        }
    }

    @Override
    public void exitString(JavaParser.StringContext ctx) {

        stack.push(ctx.CharArray().getText() + ";");
    }

    @Override
    public void exitBranchRe(JavaParser.BranchReContext ctx) {
        isBranch = true;
        StringBuilder sb = new StringBuilder();
        String s6 = stack.pop();
        String s5 = stack.pop();
        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if(").append(s1.replace(";", ""))
                .append(") {\n   ")
                .append(s2)
                .append("\n   ")
                .append(s4)
                .append("\n} else {\n   ")
                .append(s3)
                .append("\n   ")
                .append(s5)
                .append("\n}\n")
                .append(s6);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }

    }

    @Override
    public void exitSeqSeq(JavaParser.SeqSeqContext ctx) {
        StringBuilder sb = new StringBuilder();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1).append("\n   ").append(s2).append("\n   ").append(s3);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }

    }

    @Override
    public void exitCond(JavaParser.CondContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if(").append(s1.replace(";", "")).append(") {\n   ").append(s2).append("\n} else {\n   ").append(s3).append("\n}\n").append(s4).append("\n");
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (functions.size() > 0) {
                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }

    }

    @Override
    public void exitChoice(JavaParser.ChoiceContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1).append("\nif() {\n   ").append(s2).append("\n} else {\n   ").append(s3).append("\n}\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitLoop(JavaParser.LoopContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1).append("\n").append("while(").append(s2.replace(";", "")).append(") {\n").append("    ").append(s3).append("\n").append("}\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void " + functions.get(0) + " {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitRepeat(JavaParser.RepeatContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1).append("\ndo {\n").append("    ").append(s2).append("\n").append("}while(").append(s3.replace(";", "")).append(")\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitConcurRe(JavaParser.ConcurReContext ctx) {
        isConcur = true;
        StringBuilder sb = new StringBuilder();
        String s6 = stack.pop();
        String s5 = stack.pop();
        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1)
                .append("\nThread thread1 = new Thread(new Runnable() {\n")
                .append("   @Override\n")
                .append("   public void run() {\n   ")
                .append(s2)
                .append("\n   ")
                .append(s4)
                .append("\n   }\n")
                .append("});\n")
                .append("Thread thread2 = new Thread(new Runnable() {\n")
                .append("   @Override\n")
                .append("   public void run() {\n   ")
                .append(s3)
                .append("\n   ")
                .append(s5)
                .append("\n   }\n")
                .append("});\n")
                .append("thread1.start();\nthread2.start();\n")
                .append("thread1.join()\nthread2.join();\n")
                .append(s6);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitPara(JavaParser.ParaContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1).append("\nThread thread").append(ctx.depth()).append("_1 = new Thread(new Runnable() {\n").append("   @Override\n").append("   public void run() {\n   ").append(s2).append("\n   }\n").append("});\n").append("Thread thread").append(ctx.depth()).append("_2 = new Thread(new Runnable() {\n").append("   @Override\n").append("   public void run() {\n   ").append(s3).append("\n   }\n").append("});\n").append("thread").append(ctx.depth()).append("_1.start();\nthread").append(ctx.depth()).append("_2.start();\n").append("thread").append(ctx.depth()).append("_1.join()\nthread").append(ctx.depth()).append("_2.join();\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {

            while (functions.size() > 0) {


                sb.append("\n\npublic void ").append(functions.get(0)).append(" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }

            toFile();
        }
    }

}
