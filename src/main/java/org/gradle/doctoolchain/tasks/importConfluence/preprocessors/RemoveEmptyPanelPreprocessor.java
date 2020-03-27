package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * PreProcessor, which removes empty panels
 */
public class RemoveEmptyPanelPreprocessor implements Preprocessor {
  @Override public Document process(Document document) {
    Elements elements = document.getElementsByAttributeValue("ac:name", "panel");
    for (int i = 0; i < elements.size(); i++) {
      Element nextElement = elements.get(i);
      Elements childElement = nextElement.getElementsByTag("ac:rich-text-body");
      if (childElement.size() == 0 || childElement.get(0).childrenSize() == 0)
        nextElement.remove();
    }
    return document;
  }
}
