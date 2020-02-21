package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

import org.jsoup.nodes.Document;

public interface Preprocessor {

  Document process (Document document);
}
