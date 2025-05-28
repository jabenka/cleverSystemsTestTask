package com.zxcjabka;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        XslApplier applier = new XslApplier();
        List<File> xslFiles = new ArrayList<>(Arrays
                .asList(Objects.requireNonNull(
                        new File("./src/main/resources/xsl/")
                                .listFiles())
                )
        );
        for (File xslFile : xslFiles) {
            applier.apply("data.xml",xslFile.getAbsolutePath());
        }
    }
}