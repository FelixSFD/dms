package com.dude.dms.ui.components.cards

import com.dude.dms.backend.data.mails.MailFilter
import com.github.appreciated.card.RippleClickableCard
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class MailFilterCard(mailFilter: MailFilter) : RippleClickableCard() {

    private val label = Label(mailFilter.folder)

    init {
        setWidthFull()
        val wrapper = HorizontalLayout(label).apply {
            setWidthFull()
            minHeight = "10vh"
            alignItems = FlexComponent.Alignment.CENTER
            isPadding = true
        }
        add(wrapper)
    }
}