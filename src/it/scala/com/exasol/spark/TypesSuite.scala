package com.exasol.spark

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import org.apache.spark.sql.types._
import org.scalatest.funsuite.AnyFunSuite

class TypesSuite extends AnyFunSuite with BaseDockerSuite with DataFrameSuiteBase {

  test("converts Exasol types to Spark") {
    createAllTypesTable()

    val df = spark.read
      .format("com.exasol.spark")
      .option("host", container.host)
      .option("port", s"${container.port}")
      .option("query", s"SELECT * FROM $EXA_SCHEMA.$EXA_ALL_TYPES_TABLE")
      .load()

    val schemaTest = df.schema

    val schemaExpected = Map(
      "MYID" -> LongType,
      "MYTINYINT" -> ShortType,
      "MYSMALLINT" -> IntegerType,
      "MYBIGINT" -> DecimalType(36, 0),
      "MYDECIMALMAX" -> DecimalType(36, 36),
      "MYDECIMALSYSTEMDEFAULT" -> LongType,
      "MYNUMERIC" -> DecimalType(5, 2),
      "MYDOUBLE" -> DoubleType,
      "MYCHAR" -> StringType,
      "MYNCHAR" -> StringType,
      "MYLONGVARCHAR" -> StringType,
      "MYBOOLEAN" -> BooleanType,
      "MYDATE" -> DateType,
      "MYTIMESTAMP" -> TimestampType,
      "MYGEOMETRY" -> StringType,
      "MYINTERVAL" -> StringType
    )

    val fields = schemaTest.toList
    fields.foreach(field => {
      assert(field.dataType === schemaExpected.get(field.name).get)
    })
  }

}
