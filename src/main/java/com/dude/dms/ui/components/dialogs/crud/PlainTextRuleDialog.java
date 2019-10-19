package com.dude.dms.ui.components.dialogs.crud;

import com.dude.dms.backend.data.rules.PlainTextRule;
import com.dude.dms.backend.service.PlainTextRuleService;
import com.dude.dms.backend.service.TagService;
import com.dude.dms.ui.EntityEventListener;
import com.dude.dms.ui.components.dialogs.RuleDialog;
import com.dude.dms.ui.components.tags.Tagger;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class PlainTextRuleDialog extends RuleDialog {

    private final TextField plainText;
    private final Tagger ruleTagger;
    private final Checkbox caseSensitive;

    private final PlainTextRuleService plainTextRuleService;

    private PlainTextRule plainTextRule;

    /**
     * Constructor for creating an empty dialog. Create button will be added
     *
     * @param tagService           tag-service
     * @param plainTextRuleService plaintext-rule-service
     */
    public PlainTextRuleDialog(TagService tagService, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        plainText = new TextField("Text", "");
        plainText.setWidthFull();
        ruleTagger = new Tagger(tagService);
        ruleTagger.setHeight("80%");
        caseSensitive = new Checkbox("case sensitive");
        HorizontalLayout hLayout = new HorizontalLayout(plainText, caseSensitive);
        hLayout.setWidthFull();
        hLayout.setAlignItems(FlexComponent.Alignment.END);
        Button button = new Button("Create", e -> save());
        button.setWidthFull();
        add(hLayout, ruleTagger, button);
        setWidth("70vw");
        setHeight("70vh");
    }

    /**
     * Constructor for creating a dialog for an existing rule. Save button will be added
     *
     * @param plainTextRule        rule
     * @param tagService           tag-service
     * @param plainTextRuleService plaintext-rule-service
     */
    public PlainTextRuleDialog(PlainTextRule plainTextRule, TagService tagService, PlainTextRuleService plainTextRuleService) {
        this.plainTextRuleService = plainTextRuleService;
        this.plainTextRule = plainTextRule;
        plainText = new TextField("Text", plainTextRule.getText(), "");
        plainText.setWidthFull();
        ruleTagger = new Tagger(tagService);
        ruleTagger.setHeight("80%");
        ruleTagger.setSelectedTags(tagService.findByPlainTextRule(plainTextRule));
        caseSensitive = new Checkbox("case sensitive");
        HorizontalLayout hLayout = new HorizontalLayout(plainText, caseSensitive);
        hLayout.setWidthFull();
        hLayout.setAlignItems(FlexComponent.Alignment.END);
        Button saveButton = new Button("Save", e -> save());
        saveButton.setWidthFull();
        Button deleteButton = new Button("Delete", e -> delete());
        deleteButton.setWidthFull();
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton);
        buttonLayout.setWidthFull();
        add(hLayout, ruleTagger, buttonLayout);
        setWidth("70vw");
        setHeight("70vh");
    }

    @Override
    protected void save() {
        if (plainText.isEmpty()) {
            Notification.show("Text can not be empty!");
            return;
        }
        if (ruleTagger.getSelectedTags().isEmpty()) {
            Notification.show("At least on tag must be selected!");
            return;
        }
        if (plainTextRule == null) {
            plainTextRuleService.save(new PlainTextRule(plainText.getValue(), caseSensitive.getValue(), ruleTagger.getSelectedTags()));
            Notification.show("Created new rule!");
        } else {
            plainTextRule.setText(plainText.getValue());
            plainTextRule.setTags(ruleTagger.getSelectedTags());
            plainTextRuleService.save(plainTextRule);
            Notification.show("Edited rule!");
        }
        eventListener.ifPresent(EntityEventListener::onChange);
        close();
    }

    @Override
    protected void delete() {
        ConfirmDialog dialog = new ConfirmDialog("Confirm delete", "Are you sure you want to delete the item?", "Delete", event -> {
            plainTextRuleService.delete(plainTextRule);
            eventListener.ifPresent(EntityEventListener::onChange);
            close();
        }, "Cancel", event -> {});
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }
}