/*
 * Copyright 2011-2021 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.recorder.convert

package object template {

  val SimpleQuotes: String = "\""
  val TripleQuotes: String = SimpleQuotes * 3

  private def isUnsafeStringChar(char: Char) = char == '\\' || char == '"' || char == '\n'

  private def containsCharactersToBeEscaped(string: String) = string.exists(isUnsafeStringChar)

  def protectWithTripleQuotes(string: String): String = {
    val stringDelimiter = if (containsCharactersToBeEscaped(string)) TripleQuotes else SimpleQuotes
    s"$stringDelimiter$string$stringDelimiter"
  }
}