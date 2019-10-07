package com.dude.dms.ui.views.crud;

import com.dude.dms.backend.brain.BrainUtils;
import com.dude.dms.backend.data.DataEntity;
import com.dude.dms.backend.data.Diffable;
import com.dude.dms.backend.data.Historical;
import com.dude.dms.backend.data.history.History;
import com.dude.dms.backend.service.HistoricalCrudService;
import com.dude.dms.backend.service.HistoryCrudService;
import com.dude.dms.ui.components.history.HistoricalCrudForm;
import com.dude.dms.ui.views.HasNotifications;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import static com.dude.dms.backend.brain.OptionKey.CRUD_VIEW_SPLITTER_POS;

public abstract class HistoricalCrudView<T extends DataEntity & Historical<U> & Diffable<T>, U extends History> extends SplitLayout implements AfterNavigationObserver, HasNotifications {

    protected Grid<T> grid;

    private final HistoricalCrudForm<T, U> historicalCrudForm;

    protected final HistoricalCrudService<T, U> service;

    private boolean canUpdate = true;
    private boolean canCreate = true;
    private boolean showHistory = true;

    protected abstract void defineProperties();

    protected HistoricalCrudView(Class<T> clazz, HistoricalCrudService<T, U> service, HistoryCrudService<T, U> hisoryService) {
        this.service = service;

        setSizeFull();

        historicalCrudForm = new HistoricalCrudForm<>(clazz, service, hisoryService);
        historicalCrudForm.getElement().getStyle().set("padding", "10px");
        historicalCrudForm.setCreateListener(entity -> {
            fillGrid();
            showNotification("Created!");
        });
        historicalCrudForm.setSaveListener(entity -> {
            fillGrid();
            grid.select(entity);
            showNotification("Saved!");
        });
        historicalCrudForm.setErrorListener(errors -> errors.forEach(e -> showNotification(e.getErrorMessage(), true)));
        addToSecondary(historicalCrudForm);

        grid = new Grid<>();
        grid.setSizeFull();
        grid.asSingleSelect().addValueChangeListener(event -> historicalCrudForm.load(event.getValue()));
        addToPrimary(grid);

        addSplitterDragendListener(event -> historicalCrudForm.reload());
    }

    private void fillGrid() {
        grid.setItems(service.findAll());
    }

    /**
     * Adds a new column to the grid and a new component to the CRUD form
     *
     * @param name      Grid header and form label
     * @param component form component
     * @param getter    valueprovider for column and formfield
     * @param setter    value setter for column and formfield
     * @param <R>       the component type
     */
    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter) {
        addProperty(name, component, getter, getter, setter, false);
    }

    /**
     * Adds a new column to the grid and a new component to the CRUD form
     *
     * @param name         Grid header and form label
     * @param component    form component
     * @param getter       valueprovider for column and formfield
     * @param columnGetter special getter used in the grid, e.g. for date conversion
     * @param setter       value setter for column and formfield
     * @param <R>          the component type
     */
    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, ValueProvider<T, ?> columnGetter, Setter<T, R> setter) {
        addProperty(name, component, getter, columnGetter, setter, false);
    }

    /**
     * Adds a new column to the grid and a new component to the CRUD form
     *
     * @param name      Grid header and form label
     * @param component form component
     * @param getter    valueprovider for column and formfield
     * @param setter    value setter for column and formfield
     * @param readOnly  sets the component read-only
     * @param <R>       the component type
     */
    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter, boolean readOnly) {
        addProperty(name, component, getter, getter, setter, readOnly);
    }

    /**
     * Adds a new column to the grid and a new component to the CRUD form
     *
     * @param name         Grid header and form label
     * @param component    form component
     * @param getter       valueprovider for column and formfield
     * @param columnGetter special getter used in the grid, e.g. for date conversion
     * @param setter       value setter for column and formfield
     * @param readOnly     sets the component read-only
     * @param <R>          the component type
     */
    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, ValueProvider<T, ?> columnGetter, Setter<T, R> setter, boolean readOnly) {
        grid.addColumn(columnGetter).setHeader(name);
        component.setReadOnly(readOnly);
        historicalCrudForm.addFormField(name, component, getter, setter);
    }

    /**
     * Adds a new column to the grid and a new component to the CRUD form
     *
     * @param name         Grid header and form label
     * @param component    form component
     * @param getter       valueprovider for column and formfield
     * @param setter       value setter for column and formfield
     * @param validator    validation function for the value setter
     * @param errorMessage error message in case the validator fails
     * @param <R>          the component type
     */
    protected <R> void addProperty(String name, HasValue<? extends ValueChangeEvent<R>, R> component, ValueProvider<T, R> getter, Setter<T, R> setter, SerializablePredicate<? super R> validator, String errorMessage) {
        grid.addColumn(getter).setHeader(name);
        historicalCrudForm.addFormField(name, component, getter, setter, validator, errorMessage);
    }

    /**
     * Adds a new column to the grid
     *
     * @param header        grid header
     * @param valueProvider component for column
     */
    protected void addGridColumn(String header, ValueProvider<T, Component> valueProvider) {
        grid.addComponentColumn(valueProvider).setHeader(header);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        historicalCrudForm.clear();
        grid.removeAllColumns();
        fillGrid();
        defineProperties();
        historicalCrudForm.setPermissions(canUpdate, canCreate);
        if (showHistory) {
            historicalCrudForm.addHistory();
        }

        setSplitterPosition(Double.parseDouble(BrainUtils.getProperty(CRUD_VIEW_SPLITTER_POS)));
    }

    protected void canUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    protected void canCreate(boolean canCreate) {
        this.canCreate = canCreate;
    }

    protected void showHistory(boolean show) {
        showHistory = show;
    }
}