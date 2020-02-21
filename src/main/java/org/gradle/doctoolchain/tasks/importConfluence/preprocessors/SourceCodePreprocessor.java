package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * PreProcessor, which converts macro source to something valid
 */
public class SourceCodePreprocessor implements Preprocessor {
  @Override public Document process(Document document) {
    Elements elements = document.getElementsByAttributeValue("ac:name", "code");
    for (int i = 0; i < elements.size(); i++) {
      Element nextElement = elements.get(i);
      String id = nextElement.attr("ac:macro-id");

      /**<ac:parameter ac:name="title">
          example build.gradle
          </ac:parameter>**/
      Elements titleElement = nextElement.getElementsByAttributeValue("ac:name", "title");
      String title = null;
      if (titleElement.size() > 0)
        title = titleElement.get(0).text();

      //TODO handle title

      Element plaintextElement = nextElement.getElementsByTag("ac:plain-text-body").first();

      Element newElement = new Element("pre");
      newElement.attr("class", "groovy"); //TODO make configurable
      //newElement.attr("startFrom", "100");
      //if (title != null)
      //  newElement.appendText(title);
      Element codeElement = newElement.appendElement("code");
      codeElement.appendText(plaintextElement.text());
      newElement.attr("id", id);
      nextElement.replaceWith(newElement);

    }
    return document;
  }
}
