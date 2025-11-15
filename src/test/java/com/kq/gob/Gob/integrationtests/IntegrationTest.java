package com.kq.gob.Gob.integrationtests;

import com.kq.gob.Gob.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrationTest {

    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        interpreter = new Interpreter();
    }

    private String interpretCode(String code) {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);

        List<Stmt> statements = parser.parse();
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        return (String) interpreter.interpret(statements);
    }

    @Test
    void testArithmeticOperations() {
        String code = """
                    daabac(5 + 3);
                    daabac(10 - 2);
                    daabac(4 * 2);
                    daabac(20 / 4);
                """;
        String expectedOutput = "8 8 8 5";
        String actualOutput = interpretCode(code);
        // assertTrue(expectedOutput.equals(actualOutput.trim()));
    }

    @Test
    void testVariableAssignments() {
        String code = """
                    door a = 10;
                    door b = 20;
                    daabac(a + b);
                """;
        String expectedOutput = "30";
        String actualOutput = interpretCode(code).trim();
        assertTrue(expectedOutput.equals(actualOutput));
    }

    @Test
    void testStringIndexing() {
        String code = """
                    door text = "hello";
                    daabac(text[1]);
                """;
        String expectedOutput = "e";
        String actualOutput = interpretCode(code).trim();
        assertTrue(expectedOutput.equals(actualOutput));
    }

    @Test
    void testFunctionCalls() {
        String code = """
                    qabte add(a, b) {
                        celi a + b;
                    }
                    daabac(add(5, 7));
                """;
        String expectedOutput = "12";
        String actualOutput = interpretCode(code).trim();
        assertTrue(expectedOutput.equals(actualOutput));
    }

    @Test
    void testClassHandling() {
        String code = """
                    cayn Person {
                        setName(n) {
                            kan.magac = n;
                        }
                        getName() {
                            celi kan.magac;
                        }
                        Person(){
                            kan.magac = "";
                        }
                    }
                    door p = Person();
                    p.setName("John");
                    daabac(p.getName());
                """;
        String expectedOutput = "John";
        String actualOutput = interpretCode(code).trim();
        assertTrue(expectedOutput.equals(actualOutput));
    }

    @Test
    void testControlFlow() {
        String code = """
                    door x = 5;
                    kol (x > 3) {
                        daabac("Greater");
                    } kolkale(x < 3) {
                        daabac("Smaller");
                    } kale {
                        daabac("Equal");
                    }
                """;
        String expectedOutput = "Greater";
        String actualOutput = interpretCode(code).trim();
        assertTrue(expectedOutput.equals(actualOutput));
    }

    @Test
    void testWhileLoop() {
        String code = """
                    door x = 0;
                    intay(x < 5) {
                        daabac(x);
                        x = x + 1;
                    }
                """;
        String expectedOutput = "0 1 2 3 4";
        String actualOutput = interpretCode(code).trim();
        // assertTrue(expectedOutput.equals(actualOutput));
    }
}
