/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.apache.spark.ml.h2o.models

import org.apache.hadoop.fs.Path
import org.apache.spark.ml.param.Params
import org.apache.spark.ml.util.{DefaultParamsWriter, MLWriter}

class H2OMOJOModelWriter(instance: Params, val mojoData: Array[Byte]) extends MLWriter {

  val serializedFileName = "mojo_model"

  override protected def saveImpl(path: String): Unit = {
    DefaultParamsWriter.saveMetadata(instance, path, sc)

    val outputPath = new Path(path, serializedFileName)
    val fs = outputPath.getFileSystem(sc.hadoopConfiguration)
    val qualifiedOutputPath = outputPath.makeQualified(fs.getUri, fs.getWorkingDirectory)
    val out = fs.create(qualifiedOutputPath)
    try {
      out.write(mojoData)
    } finally {
      out.close()
    }
    logInfo(s"Saved to: $qualifiedOutputPath")
  }

}