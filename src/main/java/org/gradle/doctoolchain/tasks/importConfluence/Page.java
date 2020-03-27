package org.gradle.doctoolchain.tasks.importConfluence;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.doctoolchain.tasks.importConfluence.preprocessors.ExcerptPreprocessor;
import org.gradle.doctoolchain.tasks.importConfluence.preprocessors.Preprocessor;
import org.gradle.doctoolchain.tasks.importConfluence.preprocessors.RemoveEmptyPanelPreprocessor;
import org.gradle.doctoolchain.tasks.importConfluence.preprocessors.SourceCodePreprocessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Wrapper around a confluence page
 */
public class Page {

    private Collection<Preprocessor> preprocessors = Arrays.asList( new SourceCodePreprocessor(),
                                                                    new ExcerptPreprocessor(),
                                                                    new RemoveEmptyPanelPreprocessor());

    private Long id;
    private String title;
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileName () {
        return getTitle().replace(" ", "_").replace("/", "_") + ".adoc";
    }

    public String getFileNameOutFileSuffix () {
        return getTitle().replace(" ", "_") + "{outfilesuffix}";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * saves the page
     * @param exportHtmlPath                path containing html page imported from confluence
     * @param exportHtmlPreProcessedPath    path containing html page after pre processing
     * @param importAdocPath                path containing adoc page after pandoc
     * @throws IOException
     */
    public void save (final File exportHtmlPath,
                      final File exportHtmlPreProcessedPath,
                      final File importAdocPath) throws IOException {
        exportHtmlPath.mkdirs();
        exportHtmlPreProcessedPath.mkdirs();
        importAdocPath.mkdirs();
        File exportFile = new File (exportHtmlPath, getTitle() + ".html");
        File exportFilePreProcessed = new File (exportHtmlPreProcessedPath, getTitle() + ".html");
        File adocFile = new File (importAdocPath, getFileName());
        String content = getContent();

        FileUtils.write(exportFile, content);

        Document document = Jsoup.parse(content);
        for (Preprocessor nextPreprocessor: preprocessors) {
            document = nextPreprocessor.process(document);
        }
        content = document.outerHtml().replaceAll("[^\\x00-\\x7F]", "").replaceAll("&nbsp;", "");

        //remove sonderzeichen

        FileUtils.write(exportFilePreProcessed, content);

        ProcessBuilder processBuilder = new ProcessBuilder("pandoc", "--wrap=none", "-f", "html", "-t", "asciidoc", exportFilePreProcessed.getAbsolutePath());
        System.out.println (String.join(" ", processBuilder.command()));

        Process process = processBuilder.start();

        InputStream is = process.getInputStream();
        String text = IOUtils.toString(is, StandardCharsets.UTF_8.name());

        Collection<String> lines = Arrays.asList(text.split("\n"));
        Collection<String> filteredLines = new ArrayList<>();
        filteredLines.add("= " + getTitle());
        for (String next: lines) {
            if (! next.trim().equals("+"))
                filteredLines.add(next);
        }



        FileUtils.writeLines(adocFile, filteredLines);

        try {
            int returnCode = process.waitFor();
            if (returnCode != 0)
                throw new IllegalStateException("Error calling pandoc for " + exportFile.getAbsolutePath() + "(" + returnCode + ")");
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

    }


}
