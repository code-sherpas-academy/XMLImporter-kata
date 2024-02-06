package io

import java.io.File

object FileManager {
  def listFilesWithExtension(fileExtension: String)(file: File): Array[File] = {
    val these: Array[File] = file.listFiles()
    these ++ these.filter(_.isFile).filter(_.toString.endsWith(fileExtension)).flatMap(listFilesWithExtension(fileExtension))
  }
}
