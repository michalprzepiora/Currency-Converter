package pl.com.przepiora.currency.vaadin;

import com.google.gson.Gson;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.com.przepiora.currency.model.Currency;
import pl.com.przepiora.currency.model.CurrencyCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route
@StyleSheet("frontend://styles/style.css")
public class MainView extends VerticalLayout {

  private final OkHttpClient client = new OkHttpClient();
  private final Gson gson = new Gson();
  private TextField ammount;
  private ComboBox<Currency> from;
  private ComboBox<Currency> to;
  private List<Currency> currencyList = new ArrayList<>();


  public MainView() throws IOException {
    setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    for (CurrencyCode value : CurrencyCode.values()) {
      currencyList.add(currencyMid(value));
    }

    VerticalLayout mainLayout = getMainLayout();
    mainLayout.add(new Label("Currency Converter"));
    mainLayout.add(getCurrencyConverterLayout());

    System.out.println(currencyMid(CurrencyCode.CHF));
    System.out.println(currencyList);

    add(mainLayout);
  }

  private VerticalLayout getMainLayout() {
    VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    mainLayout.addClassName("layout");
    mainLayout.setWidth("60%");
    return mainLayout;
  }

  private HorizontalLayout getCurrencyConverterLayout() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setDefaultVerticalComponentAlignment(Alignment.END);
    ammount = new TextField("Ammount:");
    from = new ComboBox<>("From:");
    from.setItems(currencyList);
    from.setItemLabelGenerator(
        currency -> currency.getCode() + " (" + currency.getCurrency() + ")");
    to = new ComboBox<>("To:");
    to.setItems(currencyList);
    to.setItemLabelGenerator(currency -> currency.getCode() + " (" + currency.getCurrency() + ")");
    Button change = new Button(new Icon(VaadinIcon.EXCHANGE));
    change.setThemeName("primary");
    change.setWidth("5px");
    change.addClickListener(event -> replaceValues());
    Button count = new Button("Count");
    count.setThemeName("primary");
    layout.add(ammount, from, change, to, count);
    return layout;
  }

  private void replaceValues() {
    Currency temp = from.getValue();
    from.setValue(to.getValue());
    to.setValue(temp);
  }

  private Currency currencyMid(CurrencyCode currencyCode) throws IOException {
    String json;
    String url = String.format("http://api.nbp.pl/api/exchangerates/rates/A/%s/", currencyCode);
    Request request = new Request.Builder()
        .url(url)
        .build();
    Response response = client.newCall(request).execute();
    json = response.body().string();
    Currency currency = gson.fromJson(json, Currency.class);
    return currency;
  }
}
