package models

case class APIVersionRequest(
                       context: String,
                       apiName: String,
                       apiDescription: String,
                       version: String,
                       serviceBaseUrl: String,
                       status: APIStatus.Value,
                       endpoints: Seq[Endpoint])
