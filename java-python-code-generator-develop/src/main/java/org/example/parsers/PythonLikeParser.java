package org.example.parsers;


import org.example.gen.JavaParser;
import org.example.gen.PythonBaseListener;
import org.example.gen.PythonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PythonLikeParser extends PythonBaseListener {
    List<String> functions = new ArrayList<String>();
    List<String> Treads = new ArrayList<String>();
    Stack<String> stack = new Stack<>();
    static String FinalString = "";
    static String UUID = "";

    boolean isBranch= false;
    boolean isConcur= false;

    public static void SetUUID(String uuid) {
        UUID = uuid;
    }

    public void toFile() {
        String data = "";

        for (int i = stack.size() - 1; i > -1; i--) {

            System.out.println(stack.get(i));
            data += stack.get(i);
        }
        String txtFile = "codePython_" + UUID + ".txt";
        String javaFile = "codePython_" + UUID + ".py";
        FinalString = data;
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
            getResult();
        }
    }

    public static String getResult() {
        return FinalString;
    }

    @Override
    public void exitSeq(PythonParser.SeqContext ctx) {
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
            while (Treads.size() > 0) {

                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {


                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());

            }

            toFile();
        }

    }
    @Override
    public void exitAlt(PythonParser.AltContext ctx){
        StringBuilder sb = new StringBuilder();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if(").append(s1).append("): ").append(s2).append("\n").append(s3).append("\n");

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
        if (!functions.contains(sb.toString())) {
            functions.add(sb.toString());
        }
    }

    @Override
    public void exitString(PythonParser.StringContext ctx) {

        stack.push(ctx.CharArray().getText());
    }

    @Override
    public void exitBranchRe(PythonParser.BranchReContext ctx) {
        isBranch = true;
        StringBuilder sb = new StringBuilder();
        String s6 = stack.pop();
        String s5 = stack.pop();
        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append("if(").append(s1)
                .append(") :\n   ")
                .append(s2)
                .append("\n   ")
                .append(s4)
                .append("\nelse:\n   ")
                .append(s3)
                .append("\n   ")
                .append(s5)
                .append("\n}\n")
                .append(s6);stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
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
        sb.append(s1).append("\n").append(s2).append("\n").append(s3);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
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
        sb.append("if ").append(s1.replace(";", "")).append(":\n   ").append(s2).append("\nelse:\n   ").append(s3).append("\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitChoice(PythonParser.ChoiceContext ctx) {
        StringBuilder sb = new StringBuilder();

        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        sb.append(s1).append("\nif :\n   ").append(s2).append("\nelse:\n   ").append(s3).append("\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
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
        sb.append(s1).append("\n").append("while ").append(s2).append(":\n    ").append(s3).append("\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
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
        sb.append(s1).append("\n").append(s3).append("\nwhile ").append(s2).append(":\n    ").append(s3).append("\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }
    }

    @Override
    public void exitConcurRe(PythonParser.ConcurReContext ctx) {
        isConcur = true;
        StringBuilder sb = new StringBuilder();
        String s6 = stack.pop();
        String s5 = stack.pop();
        String s4 = stack.pop();
        String s3 = stack.pop();
        String s2 = stack.pop();
        String s1 = stack.pop();
        int num = Treads.size() * 2;
        Treads.add("\ndef Thread" + num + "():\n    " + s2 +"\n    " + s4 + "\n\ndef Thread" + (num + 1) + "():\n    " + s3 +"\n    " + s5 + "\n");
        sb.append(s1)
                .append("\nthread1 = threading.Thread(target=Thread")
                .append(num)
                .append(")")
                .append("\nthread2 = threading.Thread(target=Thread")
                .append(num + 1)
                .append(")")
                .append("\nthread1.start()\nthread2.start()\n")
                .append("thread1.join()\nthread2.join()\n")
                .append(s6);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
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
        int num = Treads.size() * 2;
        Treads.add("\ndef Thread" + num + "():\n    " + s2 + "\n\ndef Thread" + (num + 1) + "():\n    " + s3 + "\n");
        sb.append(s1).append("\nthread1 = threading.Thread(target=Thread").append(num).append(")").append("\nthread2 = threading.Thread(target=Thread").append(num + 1).append(")").append("\nthread1.start()\nthread2.start()\n").append("thread1.join()\nthread2.join()\n").append(s4);
        stack.push(sb.toString());
        sb.setLength(0);
        if (ctx.depth() == 1) {
            while (Treads.size() > 0) {
                sb.append(Treads.get(0));
                Treads.remove(0);
            }
            if (Treads.size() == 0) {
                stack.push(sb.toString());
                sb.setLength(0);
            }
            while (functions.size() > 0) {
                sb.append("def ").append(functions.get(0)).append(":\n     // Add code here\n");
                functions.remove(0);
            }
            if (functions.size() == 0) {
                stack.push(sb.toString());
            }
            toFile();
        }

    }

}
