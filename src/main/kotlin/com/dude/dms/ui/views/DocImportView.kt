package com.dude.dms.ui.views

import com.dude.dms.backend.containers.DocContainer
import com.dude.dms.brain.parsing.DocParser
import com.dude.dms.brain.polling.DocImportService
import com.dude.dms.brain.t
import com.dude.dms.utils.docImportCard
import com.dude.dms.utils.progressBar
import com.dude.dms.utils.tooltip
import com.dude.dms.ui.Const
import com.dude.dms.ui.components.cards.DocImportCard
import com.dude.dms.ui.components.dialogs.DocUploadDialog
import com.dude.dms.ui.components.misc.DocImportPreview
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.UIDetachedException
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.streams.toList


@Route(value = Const.PAGE_DOCIMPORT, layout = MainView::class)
@PageTitle("Doc Import")
class DocImportView(private val docImportService: DocImportService, private val docParser: DocParser) : VerticalLayout() {

    private lateinit var progressBar: ProgressBar

    private lateinit var progressText: Text

    private val docs = mutableSetOf<DocContainer>()

    private var loading = false

    private lateinit var itemContainer: HorizontalLayout

    private lateinit var importButton: Button

    private lateinit var itemPreview: DocImportPreview

    init {
        setSizeFull()
        isSpacing = false

        horizontalLayout {
            setWidthFull()
            alignItems = FlexComponent.Alignment.START

            button("Upload", VaadinIcon.UPLOAD.create()) {
                onLeftClick { DocUploadDialog().open() }
                width = "200px"
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
            }
            button(t("refresh"), VaadinIcon.REFRESH.create()) {
                onLeftClick { refresh() }
                width = "250px"
            }
            button(t("rules.rerun"), VaadinIcon.MAGIC.create()) {
                onLeftClick { rerunRules() }
                width = "250px"
                tooltip(t("rules.rerun.tooltip"))
            }
            verticalLayout(isPadding = false, isSpacing = false) {
                setWidthFull()

                progressBar = progressBar { setWidthFull() }
                progressText = text("")
            }
            importButton = button("Import", VaadinIcon.PLUS_CIRCLE.create()) {
                onLeftClick { createDocs() }
                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                width = "250px"
            }
        }
        splitLayout {
            setSizeFull()
            orientation = SplitLayout.Orientation.VERTICAL
            setSplitterPosition(15.0)
            setPrimaryStyle("minHeight", "100px")
            setPrimaryStyle("maxHeight", "300px")

            itemContainer = HorizontalLayout().apply {
                setSizeFull()
                style["overflowY"] = "hidden"
            }
            itemPreview = DocImportPreview().apply {
                onDone = { docContainer ->
                    docContainer.done = true
                    var index = -1
                    val cards = itemContainer.children.filter { it is DocImportCard }.map { it as DocImportCard }.toList()
                    cards.firstOrNull { it.docContainer == docContainer }?.let {
                        index = cards.indexOf(it) + 1
                        it.fill()
                    }
                    importButton.text = "Import ${docs.count { it.done }} / ${docs.count()}"
                    while (index > 0 && index < cards.size) {
                        if (!cards[index].docContainer.done) {
                            select(cards[index].docContainer)
                            break
                        }
                        index++
                    }
                }
            }
            addToPrimary(itemContainer)
            addToSecondary(itemPreview)
        }

        refresh()
    }

    private fun fill(ui: UI = UI.getCurrent(), update: Boolean = false) {
        if (!update) {
            itemContainer.removeAll()
            docs.clear()
        }
        val newDocs = docImportService.findAll().filter { if (update) it !in docs else true }
        docs.addAll(newDocs)
        ui.access {
            importButton.text = "Import ${docs.count { it.done }} / ${docs.count()}"
            newDocs.forEach { dc ->
                itemContainer.docImportCard(dc) {
                    onLeftClick { select(dc) }
                    ContextMenu().apply {
                        target = this@docImportCard
                        addItem(t("delete")) { delete(dc) }
                    }
                }
            }
        }
    }

    private fun rerunRules() {
        docs.filter { !it.done }.forEach { dc ->
            dc.tags = docParser.discoverTags(dc)
            dc.attributeValues = docParser.discoverAttributeValues(dc).map { it.key }.toMutableSet()
        }
        itemPreview.clear()
        fill()
    }

    private fun select(dc: DocContainer) {
        itemPreview.fill(dc)
        itemContainer.children.filter { it is DocImportCard }.forEach { (it as DocImportCard).select(it.docContainer == dc) }
    }

    private fun refresh() {
        if (loading) return
        loading = true
        fill()
        progressBar.value = 0.0
        progressText.text = ""
        if (docImportService.progress < 1.0) {
            docImportService.import()
        }
        val ui = UI.getCurrent()
        GlobalScope.launch {
            try {
                var process = docImportService.progress
                while (process < 1.0) {
                    ui.access {
                        progressBar.value = process
                        progressText.text = docImportService.progressText
                    }
                    process = docImportService.progress
                    delay(100)
                }
                delay(50)
                ui.access {
                    progressBar.value = 1.0
                    progressText.text = t("done")
                }
            } catch (e: UIDetachedException) {
            } catch (e: NullPointerException) {
            } finally {
                loading = false
            }
        }

        GlobalScope.launch {
            try {
                var process = docImportService.progress
                while (process < 1.0) {
                    fill(ui, true)
                    process = docImportService.progress
                    delay(1000)
                }
                delay(50)
                ui.access {
                    progressBar.value = 1.0
                    progressText.text = t("done")
                }
            } catch (e: UIDetachedException) {
            } catch (e: NullPointerException) {
            } finally {
                loading = false
            }
        }
    }

    private fun createDocs() {
        val done = docs.filter { it.done }
        done.forEach { docImportService.create(it) }
        docs.removeAll(done)
        itemPreview.clear()
        fill()
    }

    private fun delete(docContainer: DocContainer) {
        docs.remove(docContainer)
        docImportService.delete(docContainer)
        itemPreview.clear()
        fill()
    }
}