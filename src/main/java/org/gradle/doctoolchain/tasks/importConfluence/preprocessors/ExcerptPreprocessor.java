package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExcerptPreprocessor implements Preprocessor {
  @Override public Document process(Document document) {
    Elements elements = document.getElementsByAttributeValue("ac:name", "excerpt");
    for (int i = 0; i < elements.size(); i++) {
      Element nextElement = elements.get(i);
      nextElement.remove();
    }
    return document;
  }
}
