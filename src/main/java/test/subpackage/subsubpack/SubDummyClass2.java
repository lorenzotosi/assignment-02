package test.subpackage.subsubpack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubDummyClass2 {
    private final List<String> imports;
    private final Map<String, Integer> classData;
    private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([\\w\\.]+)\\s*;");

    public SubDummyClass2() {
        this.imports = new ArrayList<>();
        this.classData = new HashMap<>();
    }

    public List<String> analyzeFile(File javaFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(javaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = IMPORT_PATTERN.matcher(line);
                if (matcher.find()) {
                    String importStatement = matcher.group(1);
                    imports.add(importStatement);
                    classData.put(javaFile.getName(), imports.size());
                }
            }
        }
        return imports;
    }

    public int getDependencyCount() {
        return imports.size();
    }

    public Map<String, Integer> getClassStatistics() {
        return new HashMap<>(classData);
    }
}