package com.zxcjabka;

import com.zxcjabka.exception.ValidationException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class XslApplier {

    public void apply(String inputXmlFilename,String XslFilename) {

        if(validate(inputXmlFilename,XslFilename)) {
            String outputXmlFilename = processOutputName(XslFilename);
            try {
                TransformerFactory factory = TransformerFactory.newInstance();
                Source xslSource = new StreamSource(new File(XslFilename));
                Transformer transformer = factory.newTransformer(xslSource);

                Source xmlSource = new StreamSource(new File(inputXmlFilename));
                StreamResult result = new StreamResult(new File(outputXmlFilename));

                transformer.transform(xmlSource, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            throw new ValidationException(String.format("Files %s and %s are not validated",inputXmlFilename,XslFilename));
        }
    }

    private String processOutputName(String xslFilename) {
        String outputDirectoryPath = "src/main/resources/output/";
        File outputDirectory = new File(outputDirectoryPath);
        if (!outputDirectory.exists()) {
            if(outputDirectory.mkdir()){
                System.out.printf("Directory %s created%n",outputDirectoryPath);
            }else{
                System.exit(100);
            }

        }
        String outputXmlFilename = xslFilename.replace(".xsl", ".xml");
        outputXmlFilename = outputXmlFilename.replace("xsl", "output");
        return outputXmlFilename;
    }

    private boolean validate(String inputXmlFilename,String XslFilename) {
        if(inputXmlFilename==null || inputXmlFilename.trim().equals("")) {
            return false;
        }
        if(XslFilename==null || XslFilename.trim().equals("")) {
            return false;
        }
        if (!inputXmlFilename.endsWith(".xml") && !inputXmlFilename.endsWith(".xsl")) {
            return false;
        }
        return true;
    }
}
