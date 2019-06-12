package com.gawkat.sandbox.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.themebuilder.template.client.base.field.Css3StoreFilterFieldAppearance;
import com.sencha.gxt.themebuilder.template.client.base.field.Css3TriggerFieldAppearance;
import com.sencha.gxt.widget.core.client.form.StoreFilterField.StoreFilterFieldAppearance;

/**
 * A replacement {@link StoreFilterFieldAppearance} based off of {@link Css3StoreFilterFieldAppearance} that adds
 * one extra gss override to raise the trigger icon up by two pixels. See EMD-9447 for more.
 *
 * This replaces the {@link StoreFilterFieldAppearance} gwt binding in Emerald.gwt.xml (and dev-Emerald.gwt.xml)
 * as the appearance to use for all store filter fields.
 *
 * @author DC\john.adams
 */
public class CustomStoreFilterFieldAppearance extends Css3TriggerFieldAppearance
{

    public interface CustomStoreFilterFieldResources extends Css3TriggerFieldResources {
      @Override
      @Source({ "com/gawkat/sandbox/theme/tritium/client/base/field/Css3ValueBaseField.gss",
          "com/gawkat/sandbox/theme/tritium/client/base/field/Css3TextField.gss",
          "com/gawkat/sandbox/theme/tritium/base/field/Css3TriggerField.gss",
          "com/gawkat/sandbox/theme/tritium/base/field/Css3StoreFilterField.gss",
          "filterfield.gss"})
      Css3StoreFilterFieldAppearance.Css3StoreFilterFieldStyle style();

      @Override
      @Source("com/gawkat/sandbox/theme/tritium/base/field/clearTrigger.png")
      ImageResource triggerArrow();

      @Override
      @Source("com/gawkat/sandbox/theme/tritium/base/field/clearTriggerOver.png")
      ImageResource triggerArrowOver();

      @Override
      @Source("com/gawkat/sandbox/theme/tritium/base/field/clearTriggerClick.png")
      ImageResource triggerArrowClick();
    }

    public CustomStoreFilterFieldAppearance() {
      this(GWT.<CustomStoreFilterFieldResources>create(CustomStoreFilterFieldResources.class));
    }

    public CustomStoreFilterFieldAppearance(CustomStoreFilterFieldResources resources) {
      super(resources);
    }
  }
