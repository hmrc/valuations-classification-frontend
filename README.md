
# valuations-classification-frontend

The frontend for the internal Advance Valuations Rulings Case Manager service for reviewing & answering AVaR applications.

### Running

##### To run this Service you will need:

1) [Service Manager](https://github.com/hmrc/service-manager) installed
2) [SBT](https://www.scala-sbt.org) Version `>=1.x` installed
3) [MongoDB](https://www.mongodb.com/) version `>=3.6` installed and running on port 27017
4) [Localstack](https://github.com/localstack/localstack) installed and running on port 4572
5) Create an S3 bucket in localstack by using `awslocal s3 mb s3://digital-valuationss-local` within the localstack container

The easiest way to run MongoDB and Localstack for local development is to use [Docker](https://docs.docker.com/get-docker/).

##### To run MongoDB

```
docker run --restart unless-stopped -d -p 27017-27019:27017-27019 --name mongodb mongo:3.6.13
```

##### To run Localstack and create the S3 bucket

```
> docker run -d --restart unless-stopped --name localstack -e SERVICES=s3 -p4572:4566 -p8080:8080 localstack/localstack
> docker exec -it localstack bash
> awslocal s3 mb s3://digital-valuationss-local
> exit
```

#### Starting the application:
Launch the frontend and dependencies using `sm --start VCCM_ALL -r`

This application runs on port 12590.

Open `http://localhost:12590/manage-valuations-classifications`.

### Authentication

The service uses the HMRC [auth-client](https://github.com/hmrc/auth-client) for authentication with STRIDE as the authentication provider. In non production environments you will be redirected to the stride-idp-stub. You can log in using the following enrolment information:

PID: `<any string>`

to log in as a classification team officer:

Roles: `classification`

and as a classification team manager:

Roles: `classification-manager`

and as a read-only user:

Roles: `classification-read-only`

### PDF Generator Service

This service requires the installation of some dependencies before it can be run using Service Manager. See [pdf-generator-service](https://github.com/hmrc/pdf-generator-service).

Running the pdf-generator-service locally on Mac OSX (currently) requires running an older version.  

Run `sm --start PDF_GENERATOR_SERVICE -r 1.20.0`

### Testing

Run `./run_all_tests.sh`. This also runs Scalastyle and does coverage testing.

or `sbt test it:test` to run the tests only.

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
