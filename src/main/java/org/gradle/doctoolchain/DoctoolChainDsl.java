package org.gradle.doctoolchain;

import java.util.ArrayList;
import java.util.List;

public class DoctoolChainDsl {


  private List<DoctoolchainImport> importList = new ArrayList<DoctoolchainImport>();

  public DoctoolChainDsl () {
    System.out.println ("Hello");
  }

  public void importItem (String pageId, String url, final String folder) {
    DoctoolchainImport newImportItem = new DoctoolchainImport();
    newImportItem.pageId = pageId;
    newImportItem.url = url;
    newImportItem.folder = folder;
    importList.add(newImportItem);
  }

  public List<DoctoolchainImport> getImportList() {
    return importList;
  }



}
