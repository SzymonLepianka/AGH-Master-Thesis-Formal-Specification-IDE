/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tl_to_spass;

import java.util.ArrayList;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 *
 * @author kkapa
 */
public class ParserErrorListener extends BaseErrorListener{
    private final ArrayList<String> errorMessages;

    public ParserErrorListener() {
        this.errorMessages = new ArrayList<>();
    }
    
    public ArrayList<String> getErrors(){
        return this.errorMessages;
    }
    @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        
        errorMessages.add(String.format("line %d:%d %s", line, charPositionInLine, msg));
    }
    
}
