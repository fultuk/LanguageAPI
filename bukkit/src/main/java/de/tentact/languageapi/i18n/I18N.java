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

package de.tentact.languageapi.i18n;

import de.tentact.languageapi.LanguageAPI;
import de.tentact.languageapi.message.Identifier;
import de.tentact.languageapi.message.Message;

public enum I18N {

  LANGUAGEAPI_PREFIX(Identifier.of("languageapi.prefix"), "&bLanguageAPI x &7"),
  LANGUAGEAPI_NOPERMS(Identifier.of("languageapi.noperms"), "You dont have the permission to execute this command");

  private final Identifier identifier;

  I18N(Identifier identifier, String translation) {
    this.identifier = identifier;
    LanguageAPI.getInstance().getMessageHandler().translateMessage(identifier, translation);
  }

  public Message get() {
    return this.get(true);
  }

  public Message get(boolean prefix) {
    if(prefix) {
      return LanguageAPI.getInstance().getMessageHandler().getMessage(this.identifier, LANGUAGEAPI_PREFIX.get(false));
    }
    return LanguageAPI.getInstance().getMessageHandler().getMessage(this.identifier);
  }

}
