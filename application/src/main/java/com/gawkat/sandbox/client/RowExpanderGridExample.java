package com.gawkat.sandbox.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowExpander;

public class RowExpanderGridExample implements IsWidget, EntryPoint {

  private static final StockProperties properties = GWT.create(StockProperties.class);

  private ContentPanel panel;

  public RowExpanderGridExample() {
    onModuleLoad();
  }

  @Override
  public Widget asWidget() {
    if (panel == null) {
      
      ListStore<String> comboStore = new ListStore<String>(new ModelKeyProvider<String>() {
        @Override
        public String getKey(String item) {
          return item;
        }
      });
      comboStore.add("Mostly Shady");
      comboStore.add("Mostly Sunny");
      comboStore.add("Shade");
      comboStore.add("Sunny");
      comboStore.add("Sun or Shade");
      
      ComboBoxCell<String> comboCellSymbol = new ComboBoxCell<String>(comboStore, new LabelProvider<String>() {
        @Override
        public String getLabel(String item) {
          return item;
        }
      });
      comboCellSymbol.addSelectionHandler(new SelectionHandler<String>() {
        @Override
        public void onSelection(SelectionEvent<String> event) {
          CellSelectionEvent<String> sel = (CellSelectionEvent<String>) event;
        }
      });
      comboCellSymbol.setTriggerAction(TriggerAction.ALL);
      comboCellSymbol.setForceSelection(true);

      RowExpander<Stock> rowExpander = new RowExpander<Stock>(new AbstractCell<Stock>() {
        @Override
        public void render(Context context, Stock value, SafeHtmlBuilder sb) {
          String text = "Row expander text";
          sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>Company:</b> " + value.getName() + "</p>");
          sb.appendHtmlConstant("<p style='margin: 5px 5px 10px'><b>Summary:</b> " + text);
        }
      });
      rowExpander.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      rowExpander.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      ColumnConfig<Stock, String> nameCol = new ColumnConfig<Stock, String>(properties.name(), 200, "Company");
      ColumnConfig<Stock, String> symbolCol = new ColumnConfig<Stock, String>(properties.symbol(), 250, "Symbol");
      ColumnConfig<Stock, Double> lastCol = new ColumnConfig<Stock, Double>(properties.last(), 75, "Last");
      ColumnConfig<Stock, Double> changeCol = new ColumnConfig<Stock, Double>(properties.change(), 100, "Change");
      ColumnConfig<Stock, Date> lastTransCol = new ColumnConfig<Stock, Date>(properties.lastTrans(), 100,
          "Last Updated");
      
      symbolCol.setCell(comboCellSymbol);
      symbolCol.setCellPadding(false);

      final NumberFormat number = NumberFormat.getFormat("0.00");
      changeCol.setCell(new AbstractCell<Double>() {
        @Override
        public void render(Context context, Double value, SafeHtmlBuilder sb) {
          String style = "style='color: " + (value < 0 ? "red" : "green") + "'";
          String v = number.format(value);
          sb.appendHtmlConstant("<span " + style + " qtitle='Change' qtip='" + v + "'>" + v + "</span>");
        }
      });

      lastTransCol.setCell(new DateCell(DateTimeFormat.getFormat("MM/dd/yyyy")));
      

      List<ColumnConfig<Stock, ?>> columns = new ArrayList<ColumnConfig<Stock, ?>>();
      columns.add(rowExpander);
      columns.add(nameCol);
      columns.add(symbolCol);
      columns.add(lastCol);
      columns.add(changeCol);
      columns.add(lastTransCol);

      ColumnModel<Stock> cm = new ColumnModel<Stock>(columns);

      ListStore<Stock> store = new ListStore<Stock>(properties.key());
      store.addAll(getStocks());

      final Grid<Stock> grid = new Grid<Stock>(store, cm);
      grid.getView().setAutoExpandColumn(nameCol);
      grid.setBorders(false);
      grid.getView().setStripeRows(true);
      grid.getView().setColumnLines(true);
      grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

      rowExpander.initPlugin(grid);

      panel = new ContentPanel();
      panel.setHeading("CustomRowExpander Grid");
      panel.setPixelSize(800, 620);
      panel.addStyleName("margin-10");
      panel.setWidget(grid);
    }

    return panel;
  }

  @Override
  public void onModuleLoad() {
    RootPanel.get().add(asWidget());
  }

  private int COUNTER;

  public class Stock implements Serializable, Comparable<Stock> {
    private Integer id;
    private Double change;
    private Date date = new Date();
    private String industry = getType();
    private Double last;
    private String name;
    private Double open;
    private String symbol;
    private boolean split = Boolean.valueOf(Math.random() > .5);

    public Stock() {
      this.id = Integer.valueOf(COUNTER++);
    }

    public Stock(String name, double open, double change, double pctChange, Date date, String industry) {
      this();
      this.name = name;
      this.open = open;
      this.change = change;
      this.date = date;
      this.industry = industry;
    }

    public Stock(String name, String symbol, double open, double last, Date date) {
      this();
      this.name = name;
      this.symbol = symbol;
      this.change = last - open;
      this.open = open;
      this.last = last;
      this.date = date;
    }

    public Double getChange() {
      return change;
    }

    public Integer getId() {
      return id;
    }

    public String getIndustry() {
      return industry;
    }

    public Double getLast() {
      return last;
    }

    public Date getLastTrans() {
      return date;
    }

    public String getName() {
      return name;
    }

    public Double getOpen() {
      return open;
    }

    /**
     * Read-only property, based on other values
     * 
     * @return the percent change
     */
    public double getPercentChange() {
      return getChange() / getOpen();
    }

    public String getSymbol() {
      return symbol;
    }

    public boolean isSplit() {
      return split;
    }

    public void setChange(Double change) {
      this.change = change;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public void setIndustry(String industry) {
      this.industry = industry;
    }

    public void setLast(Double last) {
      this.last = last;
    }

    public void setLastTrans(Date date) {
      this.date = date;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setOpen(Double open) {
      this.open = open;
    }

    public void setSplit(boolean split) {
      this.split = split;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public String toString() {
      return getName();
    }

    private String getType() {
      double r = Math.random();
      if (r <= .25) {
        return "Auto";
      } else if (r > .25 && r <= .50) {
        return "Media";
      } else if (r > .5 && r <= .75) {
        return "Medical";
      } else {
        return "Tech";
      }
    }

    @Override
    public int compareTo(Stock o) {
      return (this.getLastTrans().compareTo(o.getLastTrans()));
    }

  }

  private Date randomDate() {
    DateWrapper w = new DateWrapper();
    int r = (int) (Math.random() * 10) * 10;
    w = w.addDays(-r);
    return w.asDate();
  }

  public interface StockProperties extends PropertyAccess<Stock> {
    @Path("symbol")
    ModelKeyProvider<Stock> key();

    ValueProvider<Stock, String> name();

    ValueProvider<Stock, String> symbol();

    ValueProvider<Stock, Double> last();

    ValueProvider<Stock, Double> change();

    ValueProvider<Stock, Date> lastTrans();

    ValueProvider<Stock, String> industry();
  }

  private void getRpcStocks(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<Stock>> callback) {
    final int offset = loadConfig.getOffset();
    final int limit = loadConfig.getLimit();

    System.out.println("getDatas: offset=" + offset + " limit=" + limit);

    PagingLoadResult<Stock> result = new PagingLoadResult<Stock>() {
      @Override
      public List<Stock> getData() {
        return getStocks(offset, limit);
      }

      @Override
      public void setTotalLength(int totalLength) {
      }

      @Override
      public void setOffset(int offset) {
      }

      @Override
      public int getTotalLength() {
        return getStocks().size();
      }

      @Override
      public int getOffset() {
        return offset;
      }
    };
    callback.onSuccess(result);
  }

  public List<Stock> getStocks(int offset, int limit) {
    if (limit > getStocks().size()) {
      limit = getStocks().size() - 1;
    }

    List<Stock> stocks = getStocks();
    List<Stock> subList = stocks.subList(offset, offset + limit);

    return subList;
  }

  private List<Stock> getStocks() {
    Date d1 = new Date();
    Date d2 = new Date();
    CalendarUtil.addDaysToDate(d2, 2);

    List<Stock> stocks = new ArrayList<Stock>();
    stocks.add(new Stock("Matillion", "ARR", 125.64, 123.43, d1));
    stocks.add(new Stock("Cisco Systems, Inc.", "CSCO", 25.84, 26.3, d2));
    stocks.add(new Stock("Google Inc.", "GOOG", 516.2, 512.6, d1));
    stocks.add(new Stock("Intel Corporation", "INTC", 21.36, 21.53, d2));
    stocks.add(new Stock("Level 3 Communications, Inc.", "LVLT", 5.55, 5.54, d2));
    stocks.add(new Stock("Microsoft Corporation", "MSFT", 29.56, 29.72, d1));
    stocks.add(new Stock("Nokia Corporation (ADR)", "NOK", 27.83, 27.93, d2));
    stocks.add(new Stock("Oracle Corporation", "ORCL", 18.73, 18.98, randomDate()));
    stocks.add(new Stock("Starbucks Corporation", "SBUX", 27.33, 27.36, randomDate()));
    stocks.add(new Stock("Yahoo! Inc.", "YHOO", 26.97, 27.29, randomDate()));
    stocks.add(new Stock("Applied Materials, Inc.", "AMAT", 18.4, 18.66, randomDate()));
    return stocks;
  }

}
