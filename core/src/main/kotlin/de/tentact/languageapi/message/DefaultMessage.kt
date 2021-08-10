/*
 * MIT License
 *
 * Copyright (c) 2021 0utplay (Aldin Sijamhodzic)
 * Copyright (c) 2021 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.tentact.languageapi.message

import de.tentact.languageapi.LanguageAPI
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.CompletableFuture

class DefaultMessage(private val identifier: Identifier) : Message {
  private var prefix: Message? = null

  constructor(identifier: Identifier, prefixIdentifier: Identifier) : this(identifier) {
    prefix = LanguageAPI.getInstance().messageHandler.getMessage(prefixIdentifier)
  }

  constructor(identifier: Identifier, prefix: Message?) : this(identifier) {
    this.prefix = prefix
  }

  override fun build(locale: Locale, vararg parameters: Any): String {
    val prefix = if (prefix == null) "" else prefix!!.build(locale)
    return prefix + MessageFormat.format(
      LanguageAPI.getInstance().messageHandler.getMessage(identifier, locale), parameters
    )
  }

  override fun buildAsync(locale: Locale, vararg parameters: Any): CompletableFuture<String> {
    return CompletableFuture.supplyAsync { this.build(locale, parameters) }
  }

  override fun getIdentifier(): Identifier {
    return identifier
  }
}
