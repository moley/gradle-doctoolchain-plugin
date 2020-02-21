package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class SourceCodePreprocessorTest {

  @Test
  public void replace () throws IOException {

    String example = FileUtils.readFileToString(new File("src/test/resources/sourcecode.html"));
    Document document = Jsoup.parse(example);

    SourceCodePreprocessor sourceCodePreProcessor = new SourceCodePreprocessor();
    String processedDocument = CompareUtil.removeWhitespaces(sourceCodePreProcessor.process(document).toString());

    String examplePreProcessed = CompareUtil.removeWhitespaces(FileUtils.readFileToString(new File("src/test/resources/sourcecode_preprocessed.html")));
    Assert.assertEquals ("Preprocessed html invalid", examplePreProcessed, processedDocument);
  }

  @Test
  public void check () {
    String string = "&nbsp;";
    String resultString = string.replaceAll("[^\\x00-\\x7F]", "");
    System.out.println (resultString);
  }
}
