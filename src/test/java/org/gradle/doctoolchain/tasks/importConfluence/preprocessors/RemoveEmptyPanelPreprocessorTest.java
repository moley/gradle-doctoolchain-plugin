package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class RemoveEmptyPanelPreprocessorTest {

  @Test
  public void replace () throws IOException {

    String example = FileUtils.readFileToString(new File("src/test/resources/emptypanel.html"));
    Document document = Jsoup.parse(example);

    RemoveEmptyPanelPreprocessor removeEmptyPanelPreprocessor = new RemoveEmptyPanelPreprocessor();
    String processedDocument = CompareUtil.removeWhitespaces(removeEmptyPanelPreprocessor.process(document).toString());

    String examplePreProcessed = CompareUtil.removeWhitespaces(FileUtils.readFileToString(new File("src/test/resources/emptypanel_preprocessed.html")));
    Assert.assertEquals ("Preprocessed html invalid", examplePreProcessed, processedDocument);
  }
}
