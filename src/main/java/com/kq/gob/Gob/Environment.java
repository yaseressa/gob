package com.kq.gob.Gob;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;

    Map<String, Object> variables = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object getVariable(Token name) {
        if(variables.containsKey(name.lexeme)){
            return variables.get(name.lexeme);
        }
        if (enclosing != null) return enclosing.getVariable(name);
        throw new RuntimeError(name, "doorsome aan la magaacabin '" + name.lexeme + "'.");
    }

    public void declare(String lexeme, Object value) {
        variables.put(lexeme, value);
    }

    public void assign(Token name, Object value) {
        if(variables.containsKey(name.lexeme)){
            variables.put(name.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "doorsome aan la magaacabin  '" + name.lexeme + "'.");
    }
    Object getAt(int distance, String name) {
        return ancestor(distance).variables.get(name);
    }
    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    public void assignAt(Integer distance, Token name, Object value) {
        ancestor(distance).variables.put(name.lexeme, value);
    }
}
