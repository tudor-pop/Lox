package org.example.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            var cwd = Paths.get(".").normalize().toAbsolutePath() + "/src/main/java/org/example/lox";
            System.out.println(cwd);
            args = new String[]{cwd};

//            System.exit(64);
//            return;
        }
        String outputDir = args[0];
        System.out.println(args);
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) {
        String path = outputDir + "/" + baseName + ".java";
        try (var writer = new PrintWriter(path, StandardCharsets.UTF_8)) {
            writer.println("package org.example.lox;");
            writer.println();
            writer.println("import java.util.List;");
            writer.println("""
                    /*
                    ***************************************************************************************************************
                    * This file was generated. Any modifications will be overriden next time the GenerateAst tool is being run. ***
                    ***************************************************************************************************************
                    */
                    """);
            writer.println("public abstract class " + baseName + " {");
            writer.println();
            writer.println("  abstract <R> R accept(Visitor<R> visitor);");
            defineVisitor(writer, baseName, types);

            // The AST classes.
            for (String type : types) {
                String className = type.split(":")[0].trim();
                String fields = type.split(":")[1].trim();
                defineType(writer, baseName, className, fields);
            }
            writer.println("}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.printf("  static class %s extends %s {%n", className, baseName);
        // Constructor.
        writer.printf("    %s(%s) {%n", className, fieldList);
        // Store parameters in fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.printf("      this.%s = %s;%n", name, name);
        }
        writer.println("    }");
// Fields.
        writer.println();
        for (String field : fields) {
            writer.printf("    final %s;%n", field);
        }
        // Visitor pattern.
        writer.println();
        writer.println("""
             \t@Override
             \t<R> R accept(Visitor<R> visitor) {
             \t     return visitor.visit(this);
             \t}
                """);
        // end visitor
        writer.println("  }");
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("  interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("    R visit(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("  }");
    }

}