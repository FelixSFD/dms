package com.dude.dms.ui.components;

import com.dude.dms.backend.data.entity.Tag;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TagContainer extends HorizontalLayout {

    public TagContainer(Iterable<Tag> tags) {
        getElement().getStyle().set("display", "inlineBlock");
        tags.forEach(tag -> add(new TagLabel(tag)));
    }

}
