package com.gawkat.sandbox.client;

import com.google.gwt.junit.client.GWTTestCase;

public class CompileGwtTest extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "com.gawkat.sandbox.SandboxTheme";
  }

  public void testSandbox() {
    assertTrue(true);
  }
}
