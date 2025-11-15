package com.kq.gob.Gob;

import java.util.List;
import java.util.Map;

public class GobClass implements GobCalls{
    final String name;
    final GobClass superclass;
    private final Map<String, GobFunction> methods;
    GobClass(String name, GobClass superclass, Map<String, GobFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        GobFunction initializer = findMethod(name);
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        GobInstance instance = new GobInstance(this, Interpreter.objVars);
        GobFunction initializer = findMethod(name);
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }

    public GobFunction findMethod(String lexeme) {
        try {
            return methods.get(lexeme);
        }catch (Exception e) {

            return null;
        }

    }
}
