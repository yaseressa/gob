package com.kq.gob.Gob;

import java.util.List;

public class GobFunction implements GobCalls {
    private final Stmt.Function definition;
    private final Environment closure;

    private final boolean isInitializer;
    GobFunction(Stmt.Function definition, Environment closure,
                boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.definition = definition;
        this.closure = closure;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < definition.params.size(); i++) {
            environment.declare(definition.params.get(i).lexeme,
                    arguments.get(i));
        }
        try {
            interpreter.executeBlock(definition.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "kan");
            return returnValue.value;
        }
        if (isInitializer) return closure.getAt(0, "kan");
        return null;
    }

    @Override
    public int arity() {
        return definition.params.size();
    }

    @Override
    public String toString() {
        return "<qabte " + definition.name.lexeme + ">";
    }

    public GobFunction bind(GobInstance gobInstance) {
        Environment environment = new Environment(closure);
        environment.declare("kan", gobInstance);
        return new GobFunction(definition, environment, isInitializer);
    }
}


