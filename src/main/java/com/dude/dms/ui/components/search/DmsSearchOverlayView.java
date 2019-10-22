package com.dude.dms.ui.components.search;

import com.github.appreciated.app.layout.addons.search.overlay.QueryPair;
import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.github.appreciated.ironoverlay.IronOverlay;
import com.github.appreciated.ironoverlay.VerticalOrientation;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DmsSearchOverlayView<T> extends IronOverlay {

    private final TextField searchField;

    private final IconButton closeButton;

    private final VerticalLayout results;

    private final VerticalLayout wrapper;

    private Function<T, ClickNotifier> dataViewProvider;

    private DataProvider<T, String> dataProvider;

    private Consumer<T> queryResultListener;

    private boolean closeOnQueryResult = true;

    @SuppressWarnings("unchecked")
    public DmsSearchOverlayView() {
        getElement().getStyle().set("width", "100%");
        setVerticalAlign(VerticalOrientation.TOP);

        results = new VerticalLayout();
        results.setSizeFull();
        results.setMargin(false);
        results.getStyle().set("overflow", "auto");

        searchField = new TextField();
        searchField.getStyle().set("--lumo-contrast-10pct", "transparent");
        searchField.addValueChangeListener(event -> {
            results.removeAll();
            List<T> result = dataProvider.fetch(new Query<>(event.getValue())).collect(Collectors.toList());
            result.stream()
                    .map(t -> new QueryPair<>(t, dataViewProvider.apply(t)))
                    .forEach(clickNotifier -> {
                        results.add((Component) clickNotifier.getNotifier());
                        clickNotifier.getNotifier().addClickListener(clickEvent -> {
                            if (closeOnQueryResult) {
                                close();
                            }
                            if (queryResultListener != null) {
                                queryResultListener.accept(clickNotifier.getQuery());
                            }
                        });
                    });
        });
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setWidthFull();

        closeButton = new IconButton(VaadinIcon.ARROW_LEFT.create());
        closeButton.addClickListener(event -> {
            searchField.clear();
            close();
        });

        HorizontalLayout searchFieldWrapper = new HorizontalLayout(closeButton, searchField);
        searchFieldWrapper.getStyle()
                .set("background", "var(--app-layout-bar-background-base-color)")
                .set("height", "var(--app-bar-height)")
                .set("box-shadow", "var(--app-layout-bar-shadow)")
                .set("padding", "var(--app-layout-bar-padding)")
                .set("flex-shrink", "0")
                .set("z-index", "1");
        searchFieldWrapper.setWidthFull();
        searchFieldWrapper.setAlignItems(FlexComponent.Alignment.CENTER);

        Div div = new Div();
        div.setText("adfas fddsfsfgsd fsf sdf sfdsdf sfsd fsdfsdd fsdfsd fsdfsdf sdfsdf sdfsf sdfsdf");
        div.setWidthFull();

        wrapper = new VerticalLayout(searchFieldWrapper, div, results);
        wrapper.setSizeFull();
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        wrapper.setMargin(false);
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.getStyle()
                .set("max-width", "100vw")
                .set("height", "100vh");

        results.getStyle()
                .set("overflow-y", "auto")
                .set("max-width", "100%")
                .set("min-width", "40%")
                .set("--lumo-size-m", "var(--lumo-size-xl)")
                .set("--lumo-contrast-10pct", "transparent");
        results.setHeightFull();
        results.setWidth("unset");
        add(wrapper);
    }

    @Override
    public void open() {
        super.open();
        searchField.focus();
    }

    public Function<T, ClickNotifier> getDataViewProvider() {
        return dataViewProvider;
    }

    public void setDataViewProvider(Function<T, ClickNotifier> dataViewProvider) {
        this.dataViewProvider = dataViewProvider;
    }

    public DataProvider<T, String> getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(DataProvider<T, String> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public VerticalLayout getResults() {
        return results;
    }

    public VerticalLayout getWrapper() {
        return wrapper;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public void setQueryResultListener(Consumer<T> queryResultListener) {
        this.queryResultListener = queryResultListener;
    }

    public void setCloseOnQueryResult(boolean closeOnQueryResult) {
        this.closeOnQueryResult = closeOnQueryResult;
    }

    public Button getCloseButton() {
        return closeButton;
    }
}