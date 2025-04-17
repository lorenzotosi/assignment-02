package aleTests;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.expr.*;

import java.io.File;
import java.util.*;

public class ParserTest {
    public static void main(String[] args) throws Exception {
        File sourceFile = new File("src/main/scala/aleTests/DummyClass.java");
        CompilationUnit cu = StaticJavaParser.parse(sourceFile);

        cu.getImports().forEach(importDecl -> {
            System.out.println("Import: " + importDecl.getNameAsString());
            importDecl.getChildNodes().forEach(ch -> {
                System.out.println(ch.toString());
            });
        });

        Set<String> usedTypes = new HashSet<>();

        cu.findAll(ClassOrInterfaceType.class).forEach(type -> {
            usedTypes.add(type.getNameAsString());
        });

        cu.findAll(ObjectCreationExpr.class).forEach(expr -> {
            usedTypes.add(expr.getType().getNameAsString());
        });

        System.out.println("Classi/Interfacce usate:");
        usedTypes.forEach(System.out::println);

    }
}