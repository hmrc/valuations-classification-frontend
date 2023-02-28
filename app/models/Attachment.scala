/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import play.api.libs.json.{Json, OFormat}
import formats.JsonFormatInstances._
import java.time.Instant

case class Attachment(
  id: String,
  public: Boolean = false,
  operator: Option[Operator],
  timestamp: Instant              = Instant.now(),
  description: Option[String]     = None,
  shouldPublishToRulings: Boolean = false
)

case class Attachment2(
                       id: String,
                       public: Boolean = false,
                       operator: Option[Operator2],
                       timestamp: Instant              = Instant.now(),
                       description: Option[String]     = None,
                       shouldPublishToRulings: Boolean = false
                     )

object Attachment2{
  implicit val fmt: OFormat[Attachment2] = Json.format[Attachment2]
}

case class FileStoreAttachment(
  id: String,
  name: String,
  mimeType: String,
  size: Long
)

object FileStoreAttachment{
  implicit val fmt: OFormat[FileStoreAttachment] = Json.format[FileStoreAttachment]
}
