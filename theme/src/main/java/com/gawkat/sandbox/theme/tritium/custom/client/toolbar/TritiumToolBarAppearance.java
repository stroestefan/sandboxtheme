package com.gawkat.sandbox.theme.tritium.custom.client.toolbar;

import com.google.gwt.core.client.GWT;

import com.gawkat.sandbox.theme.tritium.client.base.toolbar.Css3ToolBarAppearance;

public class TritiumToolBarAppearance extends Css3ToolBarAppearance {

  public interface TritiumToolBarResources extends Css3ToolBarResources {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/container/BoxLayout.gss",
            "com/gawkat/sandbox/theme/tritium/client/base/container/Css3HBoxLayoutContainer.gss",
            "com/gawkat/sandbox/theme/tritium/client/base/toolbar/Css3ToolBar.gss", 
            "TritiumToolBar.gss"})
    TritiumToolBarStyle style();
  }

  public interface TritiumToolBarStyle extends Css3ToolBarStyle {
  }

  public TritiumToolBarAppearance() {
    super(GWT.<TritiumToolBarResources> create(TritiumToolBarResources.class));
  }
}
