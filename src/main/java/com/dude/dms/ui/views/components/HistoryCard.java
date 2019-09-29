package com.dude.dms.ui.views.components;

import com.dude.dms.backend.data.entity.History;
import com.github.appreciated.card.RippleClickableCard;
import com.github.appreciated.card.content.IconItem;
import com.github.appreciated.card.label.SecondaryLabel;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.time.format.DateTimeFormatter;

public class HistoryCard<T extends History> extends RippleClickableCard {

    private final SecondaryLabel userLabel;

    public HistoryCard(T entity) {
        setWidthFull();

        String date = entity.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        IconItem iconItem;
        if (entity.getCreated()) {
            iconItem = new IconItem(VaadinIcon.PLUS.create(), "", date);
        } else if (entity.getEdited()) {
            iconItem = new IconItem(VaadinIcon.EDIT.create(), "", date);
        } else if (entity.getDeleted()) {
            iconItem = new IconItem(VaadinIcon.TRASH.create(), date);
        } else {
            iconItem = new IconItem(VaadinIcon.QUESTION.create(), date + "  Unknown");
        }
        iconItem.setWidthFull();
        add(iconItem);
        String text = entity.getText();
        if (text != null && !text.isEmpty()) {
            SecondaryLabel label = new SecondaryLabel(entity.getText());
            label.setWidthFull();
            add(label);
        }

        userLabel = new SecondaryLabel(entity.getHistoryUser().getLogin());
        add(userLabel);
        userLabel.setVisible(false);
        addClickListener(event -> userLabel.setVisible(!userLabel.isVisible()));
    }
}
