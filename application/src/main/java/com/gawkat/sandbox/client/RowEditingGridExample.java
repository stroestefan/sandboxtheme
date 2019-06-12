package com.gawkat.sandbox.client;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class RowEditingGridExample implements EntryPoint, IsWidget {

    // Used to show the converter feature
    public enum Light {
        MOSTLYSHADY("Mostly Shady"), MOSTLYSUNNY("Mostly Sunny"), SHADE("Shade"), SUNNY("Sunny"), SUNORSHADE(
                "Sun or Shade");
        static Light parseString(String object) throws ParseException {
            if (Light.MOSTLYSUNNY.toString().equals(object)) {
                return Light.MOSTLYSUNNY;
            } else if (Light.SUNORSHADE.toString().equals(object)) {
                return Light.SUNORSHADE;
            } else if (Light.MOSTLYSHADY.toString().equals(object)) {
                return Light.MOSTLYSHADY;
            } else if (Light.SHADE.toString().equals(object)) {
                return Light.SHADE;
            } else if (Light.SUNNY.toString().equals(object)) {
                return Light.SUNNY;
            } else {
                throw new ParseException(object.toString() + " could not be parsed", 0);
            }
        }

        private String text;

        Light(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public interface PlaceProperties extends PropertyAccess<Plant> {
        ValueProvider<Plant, Date> available();

        @Path("id")
        ModelKeyProvider<Plant> key();

        ValueProvider<Plant, String> light();

        ValueProvider<Plant, String> name();

        ValueProvider<Plant, Boolean> indoor();

        ValueProvider<Plant, Double> price();
    }

    protected static final int MAX_HEIGHT = 600;
    protected static final int MAX_WIDTH = 800;
    protected static final int MIN_HEIGHT = 320;
    protected static final int MIN_WIDTH = 480;

    private static final PlaceProperties properties = GWT.create(PlaceProperties.class);

    protected ContentPanel panel;
    protected Grid<Plant> grid;

    public interface PriceTemplate extends XTemplates {
        @XTemplate("<div style='text-align:right;padding:3px'>{value:currency}</div>")
        SafeHtml render(double value);
    }

    @Override
    public void onModuleLoad() {
        RootPanel.get().add(asWidget());
    }

    @Override
    public Widget asWidget() {
        if (panel == null) {
            ColumnConfig<Plant, String> nameColumn = new ColumnConfig<Plant, String>(properties.name(), 220, "Name");
            ColumnConfig<Plant, String> lightColumn = new ColumnConfig<Plant, String>(properties.light(), 120, "Light");
            ColumnConfig<Plant, Date> dateColumn = new ColumnConfig<Plant, Date>(properties.available(), 95, "Date");
            ColumnConfig<Plant, Boolean> indoorColumn = new ColumnConfig<Plant, Boolean>(properties.indoor(), 65, "Indoor");
            ColumnConfig<Plant, Double> priceColumn = new ColumnConfig<Plant, Double>(properties.price(), 75, "Price");

            dateColumn.setCell(new DateCell(DateTimeFormat.getFormat("yyyy MMM dd")));
            indoorColumn.setCell(new SimpleSafeHtmlCell<Boolean>(new AbstractSafeHtmlRenderer<Boolean>() {
                @Override
                public SafeHtml render(Boolean object) {
                    return SafeHtmlUtils.fromTrustedString(object ? "True" : "False");
                }
            }));

            priceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
            priceColumn.setCell(new SimpleSafeHtmlCell<Double>(new AbstractSafeHtmlRenderer<Double>() {
                @Override
                public SafeHtml render(Double object) {
                    return SafeHtmlUtils.fromString(NumberFormat.getCurrencyFormat().format(object));
                }
            }));

            List<ColumnConfig<Plant, ?>> columns = new ArrayList<ColumnConfig<Plant, ?>>();
            columns.add(nameColumn);
            columns.add(lightColumn);
            columns.add(priceColumn);
            columns.add(dateColumn);
            columns.add(indoorColumn);

            ColumnModel<Plant> columnModel = new ColumnModel<Plant>(columns);

            final ListStore<Plant> store = new ListStore<Plant>(properties.key());
            store.addAll(getPlants());

            grid = new Grid<Plant>(store, columnModel);
            grid.getView().setAutoExpandColumn(nameColumn);
            grid.setSelectionModel(new CellSelectionModel<Plant>());
            grid.getColumnModel().getColumn(0).setHideable(false);

            // EDITING //
            SimpleComboBox<Light> lightCombo = new SimpleComboBox<Light>(new StringLabelProvider<Light>());
            lightCombo.setClearValueOnParseError(false);
            lightCombo.setPropertyEditor(new PropertyEditor<Light>() {
                @Override
                public Light parse(CharSequence text) throws ParseException {
                    return Light.parseString(text.toString());
                }

                @Override
                public String render(Light object) {
                    return object == null ? Light.SUNNY.toString() : object.toString();
                }
            });
            lightCombo.setTriggerAction(TriggerAction.ALL);
            lightCombo.add(Light.SUNNY);
            lightCombo.add(Light.MOSTLYSUNNY);
            lightCombo.add(Light.SUNORSHADE);
            lightCombo.add(Light.MOSTLYSHADY);
            lightCombo.add(Light.SHADE);

            Converter<String, Light> lightConverter = new Converter<String, Light>() {
                @Override
                public String convertFieldValue(Light object) {
                    return object == null ? "" : object.toString();
                }

                @Override
                public Light convertModelValue(String object) {
                    try {
                        return Light.parseString(object);
                    } catch (ParseException e) {
                        return null;
                    }
                }
            };

            DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
            DateField dateField = new DateField(new DateTimePropertyEditor(dateFormat));
            dateField.setClearValueOnParseError(false);

            ColumnConfig<Plant, Double> price = grid.getColumnModel().getColumn(2);

            GridInlineEditing<Plant> editing = new GridInlineEditing<Plant>(grid);
            editing.addEditor(nameColumn, new TextField());
            editing.addEditor(lightColumn, lightConverter, lightCombo);
            editing.addEditor(dateColumn, dateField);
            editing.addEditor(indoorColumn, new CheckBox());
            // column 5 is not editable

            // EDITING //

            TextButton addButton = new TextButton("Add Plant");
            addButton.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    Plant plant = new Plant();
                    plant.setName("New Plant 1");
                    plant.setLight("Mostly Shady");
                    plant.setPrice(0);
                    plant.setAvailable(new DateWrapper().clearTime().asDate());
                    plant.setIndoor(false);

                    editing.cancelEditing();
                    store.add(0, plant);

                    int row = store.indexOf(plant);
                    editing.startEditing(new GridCell(row, 0));
                }
            });

            ToolBar toolBar = new ToolBar();
            toolBar.add(addButton);

            VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
            verticalLayoutContainer.add(toolBar, new VerticalLayoutData(1, -1));
            verticalLayoutContainer.add(grid, new VerticalLayoutData(1, -1));

            panel = new ContentPanel();
            panel.setHeading("Row Editable Grid");
            panel.add(verticalLayoutContainer);
            panel.setButtonAlign(BoxLayoutPack.CENTER);
            panel.addButton(new TextButton("Reset", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    store.rejectChanges();
                }
            }));
            panel.addButton(new TextButton("Save", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    store.commitChanges();
                }
            }));
        }

        return panel;
    }

    public static List<Plant> getPlants() {
        List<Plant> plants = new ArrayList<Plant>();
        plants.add(new Plant("Bloodroot", "Mostly Shady", 2.44, "03/15/2012", true));
        plants.add(new Plant("Columbine", "Shade", 9.37, "03/15/2009", true));
        plants.add(new Plant("Marsh Marigold", "Mostly Sunny", 6.81, "05/17/2011", false));
        plants.add(new Plant("Cowslip", "Mostly Shady", 9.90, "03/06/2006", true));
        plants.add(new Plant("Dutchman's-Breeches", "Mostly Shady", 6.44, "01/20/2009", true));
        plants.add(new Plant("Ginger, Wild", "Mostly Shady", 9.03, "04/18/2011", true));
        plants.add(new Plant("Hepatica", "Sunny", 4.45, "01/26/2006", true));
        plants.add(new Plant("Liverleaf", "Mostly Sunny", 3.99, "01/02/2006", true));
        plants.add(new Plant("Jack-In-The-Pulpit", "Mostly Shady", 3.23, "02/01/2011", true));
        plants.add(new Plant("Mayapple", "Mostly Shady", 2.98, "06/05/2006", true));
        plants.add(new Plant("Phlox, Woodland", "Sun or Shade", 2.80, "01/22/2009", false));
        plants.add(new Plant("Phlox, Blue", "Sun or Shade", 5.59, "02/16/2008", false));
        plants.add(new Plant("Spring-Beauty", "Mostly Shady", 6.59, "02/01/2012", true));
        plants.add(new Plant("Trillium", "Sun or Shade", 3.90, "04/29/2006", false));
        plants.add(new Plant("Wake Robin", "Sun or Shade", 3.20, "02/21/20012", false));
        plants.add(new Plant("Violet, Dog-Tooth", "Shade", 9.04, "02/01/20011", true));
        plants.add(new Plant("Trout Lily", "Shade", 6.94, "03/24/2009", true));
        plants.add(new Plant("Adder's-Tongue", "Mostly Shady", 6.59, "02/01/2006", true));
        plants.add(new Plant("Anemone", "Mostly Shady", 8.86, "12/26/2011", true));
        plants.add(new Plant("Grecian Windflower", "Mostly Shady", 9.16, "07/10/2011", true));
        plants.add(new Plant("Bee Balm", "Shade", 4.59, "05/03/2009", true));
        plants.add(new Plant("Bergamot", "Shade", 7.16, "04/27/2010", true));
        plants.add(new Plant("Black-Eyed Susan", "Sunny", 9.80, "06/18/2010", false));
        plants.add(new Plant("Buttercup", "Shade", 2.57, "06/10/2009", true));
        plants.add(new Plant("Crowfoot", "Shade", 9.34, "04/03/2012", true));
        plants.add(new Plant("Butterfly Weed", "Sunny", 2.78, "06/30/2011", false));
        plants.add(new Plant("Cinquefoil", "Shade", 7.06, "05/25/2008", true));
        plants.add(new Plant("Primrose", "Sunny", 6.56, "01/30/2012", false));
        plants.add(new Plant("Gentian", "Sun or Shade", 7.81, "05/18/2008", false));
        plants.add(new Plant("Greek Valerian", "Shade", 3.41, "04/03/2009", true));
        plants.add(new Plant("California Poppy", "Mostly Shady", 2.78, "05/13/2011", false));
        plants.add(new Plant("Shooting Star", "Shade", 7.06, "07/11/2008", true));
        plants.add(new Plant("Snakeroot", "Sunny", 6.56, "02/22/2008", false));
        plants.add(new Plant("Cardinal Flower", "Shade", 7.81, "05/18/2006", false));
        return plants;
    }
}

class Plant {

    private DateTimeFormat df = DateTimeFormat.getFormat("MM/dd/y");
    private static int AUTO_ID = 0;

    private int id;
    private String name;
    private String light;
    private double price;
    private Date available;
    private boolean indoor;
    private String color;
    private int difficulty;
    private double progress;

    public Plant() {
        id = AUTO_ID++;

        difficulty = (int) (Math.random() * 100);
        progress = Math.random();

    }

    public Plant(String name, String light, double price, String available, boolean indoor) {
        this();
        setName(name);
        setLight(light);
        setPrice(price);
        setAvailable(df.parse(available));
        setIndoor(indoor);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getColor() {
        return color;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getAvailable() {
        return available;
    }

    public int getId() {
        return id;
    }

    public String getLight() {
        return light;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isIndoor() {
        return indoor;
    }

    public void setAvailable(Date available) {
        this.available = available;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIndoor(boolean indoor) {
        this.indoor = indoor;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name != null ? name : super.toString();
    }

}

