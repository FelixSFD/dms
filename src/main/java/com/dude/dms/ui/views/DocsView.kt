package com.dude.dms.ui.views

import com.dude.dms.backend.data.docs.Doc
import com.dude.dms.backend.service.DocService
import com.dude.dms.backend.service.TagService
import com.dude.dms.brain.FileManager
import com.dude.dms.brain.parsing.PdfToDocParser
import com.dude.dms.ui.Const
import com.dude.dms.ui.MainView
import com.dude.dms.ui.builder.BuilderFactory
import com.dude.dms.ui.components.tags.TagContainer
import com.dude.dms.ui.extensions.convert
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem
import com.helger.commons.io.file.FileHelper
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.dnd.GridDropMode
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.*
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import dev.mett.vaadin.tooltip.Tooltips

@Route(value = Const.PAGE_DOCS, layout = MainView::class)
@RouteAlias(value = Const.PAGE_ROOT, layout = MainView::class)
@PageTitle("Docs")
class DocsView(
        private val builderFactory: BuilderFactory,
        private val docService: DocService,
        private val tagService: TagService,
        private val fileManager: FileManager,
        pdfToDocParser: PdfToDocParser
) : GridView<Doc>(), HasUrlParameter<String?> {

    private var param: String? = null

    init {
        val ui = UI.getCurrent()
        pdfToDocParser.addEventListener("docs") { success ->
            if (success) {
                ui.access { fillGrid() }
            }
        }
        grid.addColumn { it.documentDate?.convert() }.setHeader("Date")
        grid.addComponentColumn { TagContainer(it.tags) }.setHeader("Tags")
        grid.addComponentColumn { createGridActions(it) }
        grid.addColumn { it.guid }
        grid.columns.forEach { it.setResizable(true).setAutoWidth(true) }
        grid.isColumnReorderingAllowed = true

        grid.addItemDoubleClickListener { event -> builderFactory.docs().imageDialog(event.item!!).build().open() }
        grid.dropMode = GridDropMode.ON_TOP
        grid.addDropListener { event ->
            // Workaround
            val comp = event.source.ui.get().internals.activeDragSourceComponent
            if (comp is LeftClickableItem) {
                val doc = event.dropTargetItem.get()
                tagService.findByName(comp.name)?.let { tag ->
                    val tags = tagService.findByDoc(doc).toMutableSet()
                    if (tags.add(tag)) {
                        doc.tags = tags
                        docService.save(doc)
                        grid.dataProvider.refreshAll()
                    }
                }
            }
        }
    }

    private fun createGridActions(doc: Doc): HorizontalLayout {
        val file = fileManager.getDocPdf(doc)
        val download = Anchor().apply {
            add(Button(VaadinIcon.FILE_TEXT.create()))
            isEnabled = false
            if (file.exists()) {
                isEnabled = true
                setHref(StreamResource("pdf.pdf", InputStreamFactory { FileHelper.getInputStream(file) }))
                element.setAttribute("download", true)
            }
        }
        Tooltips.getCurrent().setTooltip(download, "Open")
        val text = Button(VaadinIcon.TEXT_LABEL.create()) { builderFactory.docs().textDialog(doc).build().open() }
        Tooltips.getCurrent().setTooltip(text, "Details")
        val edit = Button(VaadinIcon.EDIT.create()) { builderFactory.docs().editDialog(doc) { grid.dataProvider.refreshAll() }.build().open() }
        Tooltips.getCurrent().setTooltip(edit, "Edit")
        return HorizontalLayout(text, download, edit)
    }

    private fun fillGrid() {
        if (param != null && param!!.isNotEmpty()) {
            val parts = param!!.split(":").toTypedArray()
            if ("tag".equals(parts[0], ignoreCase = true)) {
                tagService.findByName(parts[1])?.let { grid.setItems(docService.findByTag(it)) }
            }
        } else {
            grid.setItems(docService.findAll())
        }
    }

    override fun setParameter(beforeEvent: BeforeEvent, @OptionalParameter t: String?) {
        param = t
        fillGrid()
    }
}