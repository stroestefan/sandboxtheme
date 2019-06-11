package com.gawkat.sandbox.theme.tritium.custom.client.slider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;

import com.gawkat.sandbox.theme.tritium.client.base.slider.Css3VerticalSliderAppearance;

public class TritiumVerticalSliderAppearance extends Css3VerticalSliderAppearance {

  public interface TritiumVerticalSliderResources extends Css3VerticalSliderResources {

    @Source({"com/gawkat/sandbox/theme/tritium/client/base/slider/Css3HorizontalSlider.gss",
            "com/gawkat/sandbox/theme/tritium/client/base/slider/Css3VerticalSlider.gss",
            "TritiumSlider.gss", 
            "TritiumVerticalSlider.gss"})
    TritiumVerticalSliderStyle style();

    @ImageOptions(height = 20, width = 20)
    ImageResource sliderThumbVertical();
  }

  public interface TritiumVerticalSliderStyle extends Css3VerticalSliderStyle {
  }

  public TritiumVerticalSliderAppearance() {
    this(GWT.<TritiumVerticalSliderResources> create(TritiumVerticalSliderResources.class));
  }

  public TritiumVerticalSliderAppearance(TritiumVerticalSliderResources resources) {
    super(resources);
  }
}
