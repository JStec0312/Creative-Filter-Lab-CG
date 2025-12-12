package actions;

import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {
    private final Deque<UndoableAction> undo = new ArrayDeque<>();
    private final Deque<UndoableAction> redo = new ArrayDeque<>();
    public void push(UndoableAction e){
        undo.push(e);
        redo.clear();
    }
    public void undo(){
        if(undo.isEmpty()) return;
        UndoableAction action = undo.pop();
        action.undo();
        redo.push(action);
    }
    public void redo(){
        if(redo.isEmpty()) return;
        UndoableAction action = redo.pop();
        action.redo();
        undo.push(action);
    }
    public void clear(){
        undo.clear();
        redo.clear();
    }
}
