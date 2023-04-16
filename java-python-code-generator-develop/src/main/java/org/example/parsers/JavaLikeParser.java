package org.example.parsers;

import org.example.gen.JavaBaseListener;
import org.example.gen.JavaParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class JavaLikeParser extends JavaBaseListener {
    List<String> functions =  new ArrayList<>();
    Stack<String> stack = new Stack<>();
    public void toFile() {
        String data = "";
        for (String s: stack) {
            System.out.println(s);
            data+=s;
        }

        String txtFile = "codeJava.txt";
        String javaFile = "codeJava.java";

        try {
            FileWriter writerTxt = new FileWriter(txtFile);
            writerTxt.write(data);
            writerTxt.close();
            FileWriter writerJava = new FileWriter(javaFile);
            writerJava.write(data);
            writerJava.close();
            System.out.println("The data has been saved to files " + txtFile + " and " + javaFile);
        } catch (IOException e) {
            System.out.println("An error occurred while saving to files.");
            e.printStackTrace();
        }
    }

    @Override
    public void exitSeq(JavaParser.SeqContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\n"
                +s2+"\n");

        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
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
            sb.append(stack.pop().replace(";","")).append(",");
        }
        if (ctx.arg_java().size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        stack.push(sb.toString()+";");
        if(!functions.contains(sb.toString())){
            functions.add(sb.toString());
        }
    }
    @Override
    public void exitString(JavaParser.StringContext ctx) {

        stack.push(ctx.CharArray().getText()+";");
    }
    @Override
    public void exitBranch(JavaParser.BranchContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if("+s1.replace(";","")+") {\n   "
                +s2+"\n} else {\n   "
                +s3+"\n}\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
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
        sb.append(s1+"\n   "
                +s2+"\n   "
                +s3);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
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
        sb.append("if("+s1.replace(";","")+") {\n   "
                +s2+"\n} else {\n   "
                +s3+"\n}\n"+s4+"\n");
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){


        }

    }
    @Override
    public void exitChoice(JavaParser.ChoiceContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\nif() {\n   "
                +s2+"\n} else {\n   "
                +s3+"\n}\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
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
        sb.append(s1+"\n" +
                "while("+s2.replace(";","")+") {\n" +
                "    "+s3+"\n" +
                "}\n"+
                s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
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
        sb.append(s1+"\ndo {\n" +
                "    "+s2+"\n" +
                "}while("+s3.replace(";","")+")\n" +
                s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());
            }
            toFile();
        }
    }
    @Override
    public void exitConcur(JavaParser.ConcurContext ctx){
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\nThread thread1 = new Thread(new Runnable() {\n" +
                "   @Override\n" +
                "   public void run() {\n   "+s2+"\n   }\n" +
                "});\n" +
                "Thread thread2 = new Thread(new Runnable() {\n" +
                "   @Override\n" +
                "   public void run() {\n   "+s3+"\n   }\n" +
                "});\n" +
                "thread1.start();\nthread2.start();\n" +
                "thread1.join()\nthread2.join();\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
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
        sb.append(s1+"\nThread thread"+ctx.depth()+"_1 = new Thread(new Runnable() {\n" +
                "   @Override\n" +
                "   public void run() {\n   "+s2+"\n   }\n" +
                "});\n" +
                "Thread thread"+ctx.depth()+"_2 = new Thread(new Runnable() {\n" +
                "   @Override\n" +
                "   public void run() {\n   "+s3+"\n   }\n" +
                "});\n" +
                "thread"+ctx.depth()+"_1.start();\nthread"+ctx.depth()+"_2.start();\n" +
                "thread"+ctx.depth()+"_1.join()\nthread"+ctx.depth()+"_2.join();\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){

            while(functions.size()>0){


                sb.append("\n\npublic void "+functions.get(0)+" {\n     // Add code here\n   }\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());
            }

            toFile();
        }
    }

}
