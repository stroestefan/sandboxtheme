package com.gawkat.sandbox.theme.tritium.custom.client.field;

import com.google.gwt.core.client.GWT;

import com.gawkat.sandbox.theme.tritium.client.base.field.Css3RadioAppearance;

public class TritiumRadioAppearance extends Css3RadioAppearance {

  public interface TritiumRadioResources extends Css3RadioResources {

    @Override
    @Source({"com/gawkat/sandbox/theme/tritium/client/base/field/Css3ValueBaseField.gss",
            "com/gawkat/sandbox/theme/tritium/client/base/field/Css3CheckBox.gss",
            "com/gawkat/sandbox/theme/tritium/client/base/field/Css3Radio.gss", "TritiumRadio.gss"})
    TritiumRadioStyle style();
  }

  public interface TritiumRadioStyle extends Css3RadioStyle {
  }

  public TritiumRadioAppearance() {
    super(GWT.<TritiumRadioResources> create(TritiumRadioResources.class));
  }
}
