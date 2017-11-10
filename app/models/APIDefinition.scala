package models

case class APIDefinition(
                          name: String,
                          description: String,
                          context: String,
                          versions: Seq[APIVersion]) {

  require(name.nonEmpty, s"name is required")
  require(context.nonEmpty, s"context is required")
  require(description.nonEmpty, s"description is required")
  require(versions.nonEmpty, s"at least one version is required")
  require(uniqueVersions, s"version numbers must be unique")
  versions.foreach(version => {
    require(version.version.nonEmpty, s"version is required")
    require(version.endpoints.nonEmpty, s"at least one endpoint is required")
    version.endpoints.foreach(endpoint => {
      require(endpoint.endpointName.nonEmpty, s"endpointName is required")
      endpoint.queryParameters.foreach(parameter => {
        require(parameter.name.nonEmpty, "parameter name is required")
      })
      endpoint.authType match {
        case AuthType.USER => require(endpoint.scope.nonEmpty, s"scope is required if authType is USER")
        case AuthType.APPLICATION => require(endpoint.scope.isEmpty, s"scope is not required if authType is APPLICATION")
        case _ => ()
      }
    })
  })

  private def uniqueVersions = {
    !versions.map(_.version).groupBy(identity).mapValues(_.size).exists(_._2 > 1)
  }

}

case class APIVersion(
                       version: String,
                       serviceBaseUrl: String,
                       status: APIStatus.Value,
                       endpoints: Seq[Endpoint]) {

  require(serviceBaseUrl.nonEmpty, s"serviceBaseUrl is required")
}

case class Endpoint(
                     uriPattern: String,
                     endpointName: String,
                     method: HttpMethod.Value,
                     authType: AuthType.Value,
                     scope: Option[String] = None,
                     queryParameters: Seq[Parameter] = Seq.empty)

case class Parameter(name: String, required: Boolean = false)

object APIStatus extends Enumeration {
  type APIStatus = Value
  val PROTOTYPED, PUBLISHED, DEPRECATED, RETIRED = Value
}

object AuthType extends Enumeration {
  type AuthType = Value
  val NONE, APPLICATION, USER = Value
}

object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val GET, POST, PUT, DELETE, OPTIONS = Value
}
