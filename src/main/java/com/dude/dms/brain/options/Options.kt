package com.dude.dms.brain.options

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

data class Options(
        var view: ViewOptions,
        var doc: DocOptions,
        var mail: MailOptions,
        var storage: StorageOptions,
        var tag: TagOptions,
        var update: UpdateOptions) {

    fun save() {
        options = null
        jacksonObjectMapper().writeValue(FileOutputStream("options.json"), this)
    }

    companion object {
        private var options: Options? = null

        fun get(): Options {
            if (options == null) try {
                options = jacksonObjectMapper().readValue(File("options.json").readText(), Options::class.java)
            } catch (e: FileNotFoundException) {
                return jacksonObjectMapper().readValue(File("options.default.json").readText(), Options::class.java)
            } catch (e: MissingKotlinParameterException) {
                return jacksonObjectMapper().readValue(File("options.default.json").readText(), Options::class.java)
            }
            return options!!
        }
    }
}