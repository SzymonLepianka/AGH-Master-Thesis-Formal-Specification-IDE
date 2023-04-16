package org.example.parsers;


import org.example.gen.PythonBaseListener;
import org.example.gen.PythonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PythonLikeParser extends PythonBaseListener {
    List<String> functions =  new ArrayList<String>();
    List<String> Treads =  new ArrayList<String>();
    Stack<String> stack = new Stack<>();
    public void toFile() {
        String data = "";

        for (int i = stack.size()-1; i > -1; i--) {

            System.out.println(stack.get(i));
            data+=stack.get(i);
        }
        String txtFile = "codePython.txt";
        String javaFile = "codePython.py";

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
    public void exitSeq(PythonParser.SeqContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\n"
                +s2+"\n");

        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }

    }
    @Override
    public void exitFunction(PythonParser.FunctionContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.CharArray().getText()).append("(");
        for (PythonParser.Arg_pythonContext arg : ctx.arg_python()) {
            sb.append(stack.pop()).append(",");
        }
        if (ctx.arg_python().size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        stack.push(sb.toString());
        if(!functions.contains(sb.toString())){
            functions.add(sb.toString());
        }
    }
    @Override
    public void exitString(PythonParser.StringContext ctx) {

        stack.push(ctx.CharArray().getText());
    }
    @Override
    public void exitBranch(PythonParser.BranchContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if "+s1.replace(";","")+":\n   "
                +s2+"\nelse:\n   "
                +s3+"\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }

    }
    @Override
    public void exitSeqSeq(PythonParser.SeqSeqContext ctx) {
        StringBuilder sb = new StringBuilder();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\n"
                +s2+"\n"
                +s3);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }

    }
    @Override
    public void exitCond(PythonParser.CondContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if "+s1.replace(";","")+":\n   "
                +s2+"\nelse:\n   "
                +s3+"\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){


        }

    }
    @Override
    public void exitChoice(PythonParser.ChoiceContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\nif :\n   "
                +s2+"\nelse:\n   "
                +s3+"\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }
    }
    @Override
    public void exitLoop(PythonParser.LoopContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\n" +
                "while "+s2+":\n    "+
                s3+"\n" +
                s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }
    }
    @Override
    public void exitRepeat(PythonParser.RepeatContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1+"\n"+s3+"\nwhile "+s2+":\n    "+
                s3+"\n"+
                s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }
    }
    @Override
    public void exitConcur(PythonParser.ConcurContext ctx){
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        int num = Treads.size()*2;
        Treads.add("\ndef Thread"+num+"():\n    "+s2+"\n\ndef Thread"+(num+1)+"():\n    "+s3+"\n");
        sb.append(s1+"\nthread1 = threading.Thread(target=Thread"+num+")"
                +"\nthread2 = threading.Thread(target=Thread"+(num+1)+")"+
                "\nthread1.start()\nthread2.start()\n"+
                "thread1.join()\nthread2.join()\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }
    }
    @Override
    public void exitPara(PythonParser.ParaContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        int num = Treads.size()*2;
        Treads.add("\ndef Thread"+num+"():\n    "+s2+"\n\ndef Thread"+(num+1)+"():\n    "+s3+"\n");
        sb.append(s1+"\nthread1 = threading.Thread(target=Thread"+num+")"
                +"\nthread2 = threading.Thread(target=Thread"+(num+1)+")"+
                "\nthread1.start()\nthread2.start()\n"+
                "thread1.join()\nthread2.join()\n"+s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if(ctx.depth() ==1){
            while(Treads.size()>0){


                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if(Treads.size()==0){
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while(functions.size()>0){


                sb.append("def "+functions.get(0)+":\n     // Add code here\n");
                functions.remove(0);
            }
            if(functions.size()==0){
                stack.push(sb.toString());

            }


            toFile();
        }

    }


}
