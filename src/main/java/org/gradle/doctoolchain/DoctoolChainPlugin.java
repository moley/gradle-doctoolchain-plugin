package org.gradle.doctoolchain;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;
import org.gradle.doctoolchain.tasks.importConfluence.ImportFromConfluenceTask;

public class DoctoolChainPlugin implements Plugin<Project> {
  @java.lang.Override public void apply(Project project) {

    final String user = System.getProperty("confluence.user");
    final String password = System.getProperty("confluence.plainPassword");

    if (user == null)
      throw new IllegalStateException("Confluence user is not set, use system property confluence.user");

    if (password == null)
      throw new IllegalStateException("Confluence password is not set, use system property confluence.plainPassword");


    createForwardTask(project, "generatePDF", "generate documentation to PDF", user, password);
    createForwardTask(project, "generateHTML", "generate documentation to HTML", user, password);
    createForwardTask(project, "publishToConfluence", "publishes docu to confluence", user, password);

    ImportFromConfluenceTask importFromConfluenceTask = project.getTasks().register("importFromConfluence", ImportFromConfluenceTask.class).get();
    importFromConfluenceTask.setUsername(user);
    importFromConfluenceTask.setPassword(password);
  }

  private void createForwardTask (final Project project, final String taskname, String description, final String user, final String password) {
    Exec execTask = project.getTasks().create(taskname, Exec.class);
    execTask.setDescription(description);
    execTask.setGroup("DocToolchain");
    execTask.commandLine("docToolchain/bin/doctoolchain", ".", "-s", taskname, "-Dconfluence.user=" + user, "-Dconfluence.plainPassword=" + password);
  }
}
