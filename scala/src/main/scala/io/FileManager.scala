package io

import java.io.File

object FileManager {
  /**
   * List all files with a given extension
   * @param fileExtension the file extension to filter by
   * @param file the file to start the search from
   * @return
   */
  def listFilesWithExtension(fileExtension: String)(file: File): Array[File] = {
    val these: Array[File] = file.listFiles()
    these ++ these
      .filter(_.isFile)
      .filter(_.toString.endsWith(fileExtension))
      //.flatMap(listFilesWithExtension(fileExtension)) TODO: Prevent infinite loop
  }
}
