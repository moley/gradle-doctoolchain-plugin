package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class ExcerptPreprocessorTest {

  @Test
  public void replace () throws IOException {

    String example = FileUtils.readFileToString(new File("src/test/resources/excerpt.html"));
    Document document = Jsoup.parse(example);

    ExcerptPreprocessor excerptPreprocessor = new ExcerptPreprocessor();
    String processedDocument = CompareUtil.removeWhitespaces(excerptPreprocessor.process(document).toString());

    String examplePreProcessed = CompareUtil.removeWhitespaces(FileUtils.readFileToString(new File("src/test/resources/excerpt_preprocessed.html")));
    Assert.assertEquals ("Preprocessed html invalid", examplePreProcessed, processedDocument);
  }
}
