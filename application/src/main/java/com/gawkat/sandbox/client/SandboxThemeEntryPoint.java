package com.gawkat.sandbox.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SandboxThemeEntryPoint implements EntryPoint {

  @Override
  public void onModuleLoad() {    
    new RowEditingGridExample().onModuleLoad();
  }

}
