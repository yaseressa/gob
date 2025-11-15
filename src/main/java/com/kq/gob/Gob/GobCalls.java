package com.kq.gob.Gob;
import java.util.List;
public interface GobCalls {
    public abstract int arity();
    public abstract Object call(Interpreter interpreter, List<Object> arguments);

}
